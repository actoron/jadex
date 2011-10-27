package jadex.android.bluetooth.exceptions;

import jadex.android.bluetooth.util.Helper;

public class JadexBluetoothException extends Exception {
	private String msg;

	public JadexBluetoothException(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		return msg;
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n" + Helper.stackTraceToString(getStackTrace());
	}
}
