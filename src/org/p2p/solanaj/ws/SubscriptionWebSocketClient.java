package org.p2p.solanaj.ws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.p2p.solanaj.rpc.types.RpcNotificationResult;
import org.p2p.solanaj.rpc.types.RpcRequest;
import org.p2p.solanaj.rpc.types.RpcResponse;
import org.p2p.solanaj.ws.listeners.NotificationEventListener;

public class SubscriptionWebSocketClient extends WebSocketClient {

    private class SubscriptionParams {
        RpcRequest request;
        NotificationEventListener listener;

        SubscriptionParams(RpcRequest request, NotificationEventListener listener) {
            this.request = request;
            this.listener = listener;
        }
    }

    private static SubscriptionWebSocketClient instance;

    private Map<String, SubscriptionParams> subscriptions = new ConcurrentHashMap<>();
    private Map<String, Long> subscriptionIds = new ConcurrentHashMap<>();
    private Map<Long, NotificationEventListener> subscriptionLinsteners = new ConcurrentHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(SubscriptionWebSocketClient.class.getName());

    public static SubscriptionWebSocketClient getInstance(String endpoint) {
        URI endpointURI;
        URI serverURI;

        try {
            endpointURI = new URI(endpoint);
            serverURI = new URI(endpointURI.getScheme() == "https" ? "wss" : "ws" + "://" + endpointURI.getHost());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        if (instance == null) {
            instance = new SubscriptionWebSocketClient(serverURI);
        }

        if (!instance.isOpen()) {
            instance.connect();
        }

        return instance;

    }

    public SubscriptionWebSocketClient(URI serverURI) {
        super(serverURI);

    }

    public void accountSubscribe(String key, NotificationEventListener listener) {
        List<Object> params = new ArrayList<Object>();
        params.add(key);
        params.add(Map.of("encoding", "jsonParsed"));

        RpcRequest rpcRequest = new RpcRequest("accountSubscribe", params);

        subscriptions.put(rpcRequest.getId(), new SubscriptionParams(rpcRequest, listener));
        subscriptionIds.put(rpcRequest.getId(), null);

        updateSubscriptions();
    }

    public void signatureSubscribe(String signature, NotificationEventListener listener) {
        List<Object> params = new ArrayList<Object>();
        params.add(signature);

        RpcRequest rpcRequest = new RpcRequest("signatureSubscribe", params);

        subscriptions.put(rpcRequest.getId(), new SubscriptionParams(rpcRequest, listener));
        subscriptionIds.put(rpcRequest.getId(), null);

        updateSubscriptions();
    }

    public void logsSubscribe(String mention, NotificationEventListener listener) {
        List<Object> params = new ArrayList<Object>();
        params.add(Map.of("mentions", List.of(mention)));
        params.add(Map.of("commitment", "finalized"));

        RpcRequest rpcRequest = new RpcRequest("logsSubscribe", params);

        subscriptions.put(rpcRequest.getId(), new SubscriptionParams(rpcRequest, listener));
        subscriptionIds.put(rpcRequest.getId(), 0L);

        updateSubscriptions();
    }

    public void logsSubscribe(List<String> mentions, NotificationEventListener listener) {
        List<Object> params = new ArrayList<Object>();
        params.add(Map.of("mentions", mentions));
        params.add(Map.of("commitment", "finalized"));

        RpcRequest rpcRequest = new RpcRequest("logsSubscribe", params);

        subscriptions.put(rpcRequest.getId(), new SubscriptionParams(rpcRequest, listener));
        subscriptionIds.put(rpcRequest.getId(), null);

        updateSubscriptions();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LOGGER.info("Websocket connection opened");
        updateSubscriptions();
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    public void onMessage(String message) {
        JsonAdapter<RpcResponse<Long>> resultAdapter = new Moshi.Builder().build()
                .adapter(Types.newParameterizedType(RpcResponse.class, Long.class));

        try {
            RpcResponse<Long> rpcResult = resultAdapter.fromJson(message);
            String rpcResultId = rpcResult.getId();
            if (rpcResultId != null) {
                if (subscriptionIds.containsKey(rpcResultId)) {
                    try {
                        subscriptionIds.put(rpcResultId, rpcResult.getResult());
                        subscriptionLinsteners.put(rpcResult.getResult(), subscriptions.get(rpcResultId).listener);
                        subscriptions.remove(rpcResultId);
                    } catch (NullPointerException ignored) {

                    }
                }
            } else {
                JsonAdapter<RpcNotificationResult> notificationResultAdapter = new Moshi.Builder().build()
                        .adapter(RpcNotificationResult.class);
                RpcNotificationResult result = notificationResultAdapter.fromJson(message);
                NotificationEventListener listener = subscriptionLinsteners.get(result.getParams().getSubscription());

                Map value = (Map) result.getParams().getResult().getValue();

                switch (result.getMethod()) {
                    case "signatureNotification":
                        listener.onNotificationEvent(new SignatureNotification(value.get("err")));
                        break;
                    case "accountNotification":
                    case "logsNotification":
                        if (listener != null) {
                            listener.onNotificationEvent(value);
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: " + reason);

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void updateSubscriptions() {
        if (isOpen() && subscriptions.size() > 0) {
            JsonAdapter<RpcRequest> rpcRequestJsonAdapter = new Moshi.Builder().build().adapter(RpcRequest.class);

            for (SubscriptionParams sub : subscriptions.values()) {
                send(rpcRequestJsonAdapter.toJson(sub.request));
            }
        }
    }

}
