package jadex.android.bluetooth.exceptions;

public class MessageToLongException extends Error {
	private byte[] data;
	private long maxLength;

	public MessageToLongException(byte[] data, long maxLength) {
		this.data = data;
		this.maxLength = maxLength;
	}
	
	@Override
	public String getMessage() {
		return "Message too long: " + data.length + ". Max Length is: " + maxLength + "\n" +
				"Data was: " + data;
	}
}
