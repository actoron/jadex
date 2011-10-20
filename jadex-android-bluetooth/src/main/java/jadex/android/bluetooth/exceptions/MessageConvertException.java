package jadex.android.bluetooth.exceptions;

public class MessageConvertException extends Error {
	private String msg;

	public MessageConvertException(String msg) {
		this.msg = msg;
	}

	@Override
	public String getMessage() {
		return msg;
	}
	
	
}
