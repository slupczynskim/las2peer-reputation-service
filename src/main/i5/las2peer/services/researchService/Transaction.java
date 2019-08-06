package i5.las2peer.services.researchService;

import java.io.Serializable;

public class Transaction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String msg;

	public Transaction(String msg) {
		this.setMsg(msg);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String toString() {
		return this.getMsg();
	}

}
