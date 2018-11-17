import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class ComingMessage {
	    String header;
	    String msg;
	    String group;
	    String owner;
	    String createdAt;

	    public String getHeader() {
	        return header;
	    }

	    public void setHeader(String header) {
	        this.header = header;
	    }

	    public String getMsg() {
	        return msg;
	    }

	    public void setMsg(String msg) {
	        this.msg = msg;
	    }

	    public String getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(String createdAt) {
	        this.createdAt = createdAt;
	    }

	    public static ComingMessage fromString(String str) throws IOException {
	        ObjectMapper om=new ObjectMapper();
	        ComingMessage cm=om.readValue(str, ComingMessage.class);
	        return cm;
	    }
	    public String toJSON() throws IOException {
	        ObjectMapper om=new ObjectMapper();
	        String s=om.writeValueAsString(this);
	        return s;
	    }

	    public String getGroup() {
	        return group;
	    }

	    public void setGroup(String group) {
	        this.group = group;
	    }

	    public String getOwner() {
	        return owner;
	    }

	    public void setOwner(String owner) {
	        this.owner = owner;
	    }
}