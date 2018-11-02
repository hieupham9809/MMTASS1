import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ThreadService extends Thread {
	Socket socket;
	BufferedReader is;
	BufferedWriter os;
	HashMap<String, SocketInfo> socketInfo;
	String owner=null;

	public ThreadService(Socket accept, HashMap<String, SocketInfo> socketInfo) {
		// TODO Auto-generated constructor stub
		this.socket = accept;
		this.socketInfo = socketInfo;
	}

	@Override
	public void run() {
		try {
		is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		String input;
			try {
				while ((input=is.readLine())!=null) {
					String[] val = input.split("/");
					switch (val[0]) {
					case "init":
						/*
						 * init/userId/IP/Port
						 */
						if (val.length != 4)
							continue;
						SocketInfo info = new SocketInfo();
						owner = val[1];
						info.setIP(socket.getInetAddress().getHostAddress()+"/"+socket.getPort()+"/"+val[2]+"/"+val[3]);
						info.setSocket(socket);
						synchronized (socketInfo) {
							socketInfo.put(owner, info);
						}
						System.out.println("IP Pub of :"+owner+":"+socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
						System.out.println("IP pri of: "+owner+":"+val[2]+":"+val[3]);
						break;
					case "getUser":
						// getUser/userId
						/*
						 * Format of msg: [Header]Content
						 */
						if (val.length != 2)
							continue;
						String user = val[1];
						SocketInfo usrInfo = socketInfo.get(user);
						if (usrInfo == null) {
							ComingMessage cm = new ComingMessage();
							cm.setHeader("getUser");
							cm.setOwner(val[1]);
							cm.setMsg("404");
							os.write(cm.toJSON());
							os.newLine();
							os.flush();
						} else {
							ComingMessage cm = new ComingMessage();
							cm.setHeader("getUser");
							cm.setOwner(val[1]);
							//IP?
							cm.setMsg(usrInfo.getIP());
							os.write(cm.toJSON());
							os.newLine();
							os.flush();
						}
						break;
					case "getOnline":
						if (val.length != 1)
							continue;
						ArrayList<String> usr = new ArrayList<>();
						for (String i : socketInfo.keySet()) {
							if (!socketInfo.get(i).getSocket().isClosed()) {
								usr.add(i);
							}
						}
						ComingMessage cm = new ComingMessage();
						cm.setHeader("getOnline");
						cm.setMsg(new ObjectMapper().writeValueAsString(usr));
						os.write(cm.toJSON());
						os.newLine();
						os.flush();
						break;
					case "forward":
						/*
						 * format:
						 * forward/userFrom/userTo/Msg
						 */
						if(val.length!=4) continue;
						String from=val[1];
						String to=val[2];
						String msg=val[3];
						if(socketInfo.get(to).getSocket().isClosed()) {
							cm = new ComingMessage();
							cm.setHeader("forward");
							cm.setMsg("error");
							os.write(cm.toJSON());
							os.newLine();
							os.flush();
						} else {
						BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socketInfo.get(to).getSocket().getOutputStream()));
						bw.write(msg);
						bw.newLine();
						bw.flush();
						}
						break;
					case "close":
						socket.close();
						if(owner!=null) {
							socketInfo.remove(owner);
						}
						System.out.println("Disconnected from Client!");
						return;
					}
					

				}
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
