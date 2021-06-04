package banki.main;

import banki.java.util.Queue;
import banki.util.Server;

public class Connection{	
	
	public Connection(Server server, Queue queue) throws Exception {
		TcpConnection connection = new TcpConnection(server);
		queue.insert(new ServerSocketTuple(server,connection));
	}
	
}
