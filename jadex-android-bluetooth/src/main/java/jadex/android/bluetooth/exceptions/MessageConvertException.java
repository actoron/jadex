package jadex.android.bluetooth.exceptions;

import jadex.android.bluetooth.util.Helper;
import android.util.Log;

public class MessageConvertException extends JadexBluetoothException {
	private static final long serialVersionUID = 2222432462261576788L;

	public MessageConvertException(String msg) {
		super(msg);
	}
	
	public void logThisException() {
		Log.e(Helper.LOG_TAG, this.toString());
	}
}
