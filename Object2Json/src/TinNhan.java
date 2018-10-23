import java.util.Date;

public class TinNhan {
	private String id;
	private String from;
	private String to;
	private String msg;
	private Date createdAt;
	private Date readAt;
	private boolean isTransfer;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getReadAt() {
		return readAt;
	}
	public void setReadAt(Date receivedAt) {
		this.readAt = receivedAt;
	}
	public boolean getIsTransfer() {
		return isTransfer;
	}
	public void setIsTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	
}
