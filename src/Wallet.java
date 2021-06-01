import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class Wallet extends MIDlet {
	
	public Wallet() {
		TransactionTest test = new TransactionTest();
		test.signAndSerialize();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

}
