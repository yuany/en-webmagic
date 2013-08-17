package us.codecraft.webmagic;

import java.io.Serializable;

/**
 * 
 * include reponse info from http response
 * 
 * @Author Ligang Yao
 * 
 */
public class Response implements Serializable {

	private static final long serialVersionUID = 5534567793637955513L;

	private int statueCode;

	public int getStatueCode() {
		return statueCode;
	}

	public void setStatueCode(int statueCode) {
		this.statueCode = statueCode;
	}

}
