import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	static HashMap<String,SocketInfo> socketInfo=new HashMap<>();
	public static void main(String[] args) {
		int PORT=5000;
		ServerSocket listener=null;
		try {
		listener=new ServerSocket(PORT);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		while(true) {
			try {
				Socket socket=listener.accept();
				System.out.println("Client is connected");
				new ThreadService(socket,socketInfo).start();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}
}
