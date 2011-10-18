package jadex.android.bluetooth.exceptions;

public class RoutingTableException extends Exception {
	private String message;

	public RoutingTableException(String msg) {
		this.message = msg;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
