public class Message {
	private String type;
	private TinNhan params;
	private String hash;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getParams() {
		return params;
	}
	public void setParams(TinNhan params) {
		this.params = params;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
}
