package jadex.android.bluetooth.exceptions;

import jadex.android.bluetooth.util.Helper;

public class JadexBluetoothRuntimeError extends Error {
	private String msg;

	public JadexBluetoothRuntimeError(String msg) {
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
