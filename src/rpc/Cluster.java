package rpc;

public enum Cluster {
    DEVNET("https://devnet.solana.com"),
    TESTNET("https://testnet.solana.com"),
    MAINNET("https://api.mainnet-beta.solana.com");

    private String endpoint;

    Cluster(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
