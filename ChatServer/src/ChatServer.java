import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class ChatServer {
	public static DatagramPacket getPacket(DatagramSocket ds) throws Exception{
		byte[] buf = new byte[4096];
		DatagramPacket incoming = new DatagramPacket(buf, buf.length);
		ds.receive(incoming);
		return incoming;
	}
	public static String getString(DatagramPacket dp) throws Exception{
		return new String(dp.getData(),0,dp.getLength());
	}
	public static void sendString(DatagramSocket ds, String s, InetAddress server,int port) throws Exception{
		byte[] sending = s.getBytes();
		DatagramPacket outsending = new DatagramPacket(sending, sending.length, server,
				port);
		ds.send(outsending);
	}
	public static void main(String[] args) throws SocketException {
	ArrayList<User> users=new ArrayList<>();
	User tuankiet=new User("1","tuankiet","Luong Tuan Kiet","https://www.w3schools.com/howto/img_avatar.png","friend","1/1/1998","Nam");
	User huy=new User("2","huy","Nguyen Khac Quang Huy","https://i.imgur.com/I80W1Q0.png","friend","1/1/1998","Nam");
	User hieu=new User("3","hieu","Pham Minh Hieu","http://www.zoomyourtraffic.com/wp-content/uploads/avatar-4.png","friend","1/1/1998","Nam");
	users.add(tuankiet);
	users.add(huy);
	users.add(hieu);
	HashMap<String,IPINFO> ipInfo=new HashMap<>();
	DatagramSocket ds=new DatagramSocket(30000);
	Runnable nhan=new Runnable() {
		@Override
		public void run() {
			byte[] BUFFER=new byte[60000];
			while(true) {
				
				DatagramPacket dp=new DatagramPacket(BUFFER, BUFFER.length);
				try {
					ds.receive(dp);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				String s;
				try {
					s = getString(dp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				//init/user
				//connect/user
				String[] params=s.replace("\n", "").split("/");
				if(params[0].equals("get")) {
					//format: get/userid
					IPINFO ip=ipInfo.get(params[1]);
					if(ip==null)
						System.out.println("User not found");
					System.out.println(ip.toString());
					
				}
				else if(params[0].equals("init")) {
					System.out.println("Get init request");
					//format init request: "init/userId/IP/Port" (private ip)
					if(params.length!=4) continue;
					String username=params[1];
					String ip=params[2];
					String port=params[3];
					IP pub=new IP(dp.getAddress().toString().replace("/",""),dp.getPort());
					IP pri=new IP(ip,Integer.parseInt(port));
					ipInfo.put(username, new IPINFO(pub,pri));
					System.out.println(ipInfo.get(username).toString());
					try {
						Msg msg=new Msg("","","","200");
						Packet p=new Packet("initSession",msg);
						sendString(ds,new ObjectMapper().writeValueAsString(p),dp.getAddress(),dp.getPort());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(params[0].equals("syn"))
				{
					//format: "syn/userFrom/userTo"
					
					if(params.length!=3) continue;
					System.out.println("Get syn request "+params.length);
					String userStart=params[1];
					String userEnd=params[2];
					IPINFO ip=ipInfo.get(userStart);
					IPINFO ipto=ipInfo.get(userEnd);
					System.out.println("Response Syn");
					if(ip==null) {
						try {
							Msg msg=new Msg("","","","404");
							Packet p=new Packet("responseSyn",msg);
							sendString(ds,new ObjectMapper().writeValueAsString(p),dp.getAddress(),dp.getPort());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							continue;
						}
					}
					else
					{
						//syn/tuankiet/hieu
//						Msg msg=new Msg("",userStart,"",ipInfo.get(userStart).toString());
//						Packet p=new Packet("responseSyn",msg);
//						try {
//							sendString(ds,new ObjectMapper().writeValueAsString(p),InetAddress.getByName(ipInfo.get(userEnd).getIpPublic().getiP()),ipInfo.get(userEnd).getIpPublic().getPort());
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						Msg msg=new Msg("",userEnd,"",ipInfo.get(userEnd).toString());
						Packet p=new Packet("responseSyn",msg);
						try {
							sendString(ds,new ObjectMapper().writeValueAsString(p),InetAddress.getByName(ipInfo.get(userStart).getIpPublic().getiP()),ipInfo.get(userStart).getIpPublic().getPort());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				//command: getOnline/All
				else if(params[0].equals("getOnline")) {
					System.out.println("Get getOnline request");
					Packet p=new Packet("online",new Msg("","","",""));
					ObjectMapper om=new ObjectMapper();
					try {
						String output=om.writeValueAsString(users);
						p.getData().setText(output);
						String last=om.writeValueAsString(p);
						sendString(ds,last,dp.getAddress(),dp.getPort());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				//command: disconnect/userid
				else if(params[0].equals("disconnect")) {
					System.out.println("Get disconnect request");
					ipInfo.remove(params[1]);
				}
				else if(params[0].equals("responseConnect")) {
					//format: responseConnect/fromUser
//					synchronized (requestOK) {
//						requestOK.get(params[1]).setFinish(true);
//					}
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							int i=0;
//							while(i<10) {
//								Msg msg=new Msg("","","","200");
//								Packet p=new Packet("responseConnect",msg);
//								sendString(ds,new ObjectMapper().writeValueAsString(p),dp.getAddress(),dp.getPort());
//							}
//							
//						}
//					}).start();
				}
			}		
		}
	};
	new Thread(nhan).start();
	}
}
class Packet{
    String type;
    Msg data;

    public Packet(String type, Msg data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Msg getData() {
        return data;
    }

    public void setData(Msg data) {
        this.data = data;
    }
}
class Msg{
    String id;
    String owner;
    String createdAt;
    String text;

    public Msg(String id, String owner, String createdAt, String text) {
        this.id = id;
        this.owner = owner;
        this.createdAt = createdAt;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setText(String text) {
        this.text = text;
    }

}
class IP{
	String iP;
	int port;
	public IP(String iP, int port) {
		this.iP = iP;
		this.port = port;
	}
	public String getiP() {
		return iP;
	}
	public void setiP(String iP) {
		this.iP = iP;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	public String toString() {
		return iP+":"+port;
	}
}
class User{
    String id;
    String username;
    String fullName;
    String avatar;
    String role;
    String ngaySinh;
    String gioiTinh;

    public User(String id, String name, String fullName, String avatar, String role, String ngaySinh, String gioiTinh) {
        this.id = id;
        this.username = name;
        this.fullName = fullName;
        this.avatar = avatar;
        this.role = role;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return username;
    }
    public String getAvatar() {
        return avatar;
    }

    public String getFullName(){
        return fullName;
    }
    public String getRole() {
        return role;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }
}
class IPINFO{
	public IP ipPublic;
	public IP ipPrivate;
	public IPINFO(IP ipPublic, IP ipPrivate) {
		super();
		this.ipPublic = ipPublic;
		this.ipPrivate = ipPrivate;
	}
	
	public IP getIpPublic() {
		return ipPublic;
	}

	public void setIpPublic(IP ipPublic) {
		this.ipPublic = ipPublic;
	}

	public IP getIpPrivate() {
		return ipPrivate;
	}

	public void setIpPrivate(IP ipPrivate) {
		this.ipPrivate = ipPrivate;
	}

	@Override
	public String toString() {
		return ipPublic.toString()+":"+ipPrivate.toString();
	}
}
class Request{
	String userRequest;
	boolean isFinish;
	public String getUserRequest() {
		return userRequest;
	}
	public void setUserRequest(String userRequest) {
		this.userRequest = userRequest;
	}
	public boolean isFinish() {
		return isFinish;
	}
	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	public Request(String userRequest, boolean isFinish) {
		super();
		this.userRequest = userRequest;
		this.isFinish = isFinish;
	}
	
}
