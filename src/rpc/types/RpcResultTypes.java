package rpc.types;

import moshi.Json;

public class RpcResultTypes {

    public static class ValueLong extends RpcResultObject {
        @Json(name = "value")
        private long value;
    
        public long getValue() {
            return value;
        }
    }

}
