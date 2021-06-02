package rpc.types;

import java.util.List;

import moshi.Json;

public class ConfirmedTransaction {

    public static class Header {

        @Json(name = "numReadonlySignedAccounts")
        private long numReadonlySignedAccounts;
        @Json(name = "numReadonlyUnsignedAccounts")
        private long numReadonlyUnsignedAccounts;
        @Json(name = "numRequiredSignatures")
        private long numRequiredSignatures;

        public long getNumReadonlySignedAccounts() {
            return numReadonlySignedAccounts;
        }

        public long getNumReadonlyUnsignedAccounts() {
            return numReadonlyUnsignedAccounts;
        }

        public long getNumRequiredSignatures() {
            return numRequiredSignatures;
        }

    }

    public static class Instruction {

        @Json(name = "accounts")
        private List<Long> accounts = null;
        @Json(name = "data")
        private String data;
        @Json(name = "programIdIndex")
        private long programIdIndex;

        public List<Long> getAccounts() {
            return accounts;
        }

        public String getData() {
            return data;
        }

        public long getProgramIdIndex() {
            return programIdIndex;
        }

    }

    public static class Message {

        @Json(name = "accountKeys")
        private List<String> accountKeys = null;
        @Json(name = "header")
        private Header header;
        @Json(name = "instructions")
        private List<Instruction> instructions = null;
        @Json(name = "recentBlockhash")
        private String recentBlockhash;

        public List<String> getAccountKeys() {
            return accountKeys;
        }

        public Header getHeader() {
            return header;
        }

        public List<Instruction> getInstructions() {
            return instructions;
        }

        public String getRecentBlockhash() {
            return recentBlockhash;
        }

    }

    public static class Status {

        @Json(name = "Ok")
        private Object ok;

        public Object getOk() {
            return ok;
        }

    }

    public static class TokenBalance {

        public static class UiTokenAmount {
            @Json(name = "amount")
            private Long amount;

            @Json(name = "decimals")
            private Double decimals;

            @Json(name = "uiAmount")
            private Double uiAmount;

            @Json(name = "uiAmountString")
            private String uiAmountString;

            public Long getAmount() {
                return amount;
            }

            public Double getDecimals() {
                return decimals;
            }

            public Double getUiAmount() {
                return uiAmount;
            }

            public String getUiAmountString() {
                return uiAmountString;
            }
        }

        @Json(name = "accountIndex")
        private Double accountIndex;

        @Json(name = "mint")
        private String mint;

        @Json(name = "uiTokenAmount")
        private UiTokenAmount uiTokenAmount;

        public Double getAccountIndex() {
            return accountIndex;
        }

        public String getMint() {
            return mint;
        }

        public UiTokenAmount getUiTokenAmount() {
            return uiTokenAmount;
        }
    }

    public static class Meta {

        @Json(name = "err")
        private Object err;
        @Json(name = "fee")
        private long fee;
        @Json(name = "innerInstructions")
        private List<Object> innerInstructions = null;
        @Json(name = "preTokenBalances")
        private List<TokenBalance> preTokenBalances = null;
        @Json(name = "postTokenBalances")
        private List<TokenBalance> postTokenBalances = null;
        @Json(name = "postBalances")
        private List<Long> postBalances = null;
        @Json(name = "preBalances")
        private List<Long> preBalances = null;
        @Json(name = "status")
        private Status status;

        public Object getErr() {
            return err;
        }

        public long getFee() {
            return fee;
        }

        public List<Object> getInnerInstructions() {
            return innerInstructions;
        }

        public List<Long> getPostBalances() {
            return postBalances;
        }

        public List<Long> getPreBalances() {
            return preBalances;
        }

        public Status getStatus() {
            return status;
        }

        public List<TokenBalance> getPreTokenBalances() {
            return preTokenBalances;
        }

        public List<TokenBalance> getPostTokenBalances() {
            return postTokenBalances;
        }
    }

    public static class Transaction {

        @Json(name = "message")
        private Message message;
        @Json(name = "signatures")
        private List<String> signatures = null;

        public Message getMessage() {
            return message;
        }

        public List<String> getSignatures() {
            return signatures;
        }

    }

    @Json(name = "meta")
    private Meta meta;
    @Json(name = "slot")
    private long slot;
    @Json(name = "transaction")
    private Transaction transaction;

    public Meta getMeta() {
        return meta;
    }

    public long getSlot() {
        return slot;
    }

    public Transaction getTransaction() {
        return transaction;
    }

}
