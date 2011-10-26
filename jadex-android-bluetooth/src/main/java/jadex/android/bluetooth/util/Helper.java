package jadex.android.bluetooth.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import jadex.android.bluetooth.device.factory.AndroidBluetoothAdapterFactory;
import jadex.android.bluetooth.device.factory.AndroidBluetoothDeviceFactory;
import jadex.android.bluetooth.device.factory.IBluetoothAdapterFactory;
import jadex.android.bluetooth.device.factory.IBluetoothDeviceFactory;

public class Helper {
	public static final String LOG_TAG = "android-bluetooth";

	public static IBluetoothAdapterFactory getBluetoothAdapterFactory() {
		return AndroidBluetoothAdapterFactory.getInstance();
	}

	public static IBluetoothDeviceFactory getBluetoothDeviceFactory() {
		return AndroidBluetoothDeviceFactory.getInstance();
	}

	public static String stackTraceToString(StackTraceElement[] stackTrace) {
		StringWriter sw = new StringWriter();
		printStackTrace(stackTrace, new PrintWriter(sw));
		return sw.toString();
	}

	public static void printStackTrace(StackTraceElement[] stackTrace,
			PrintWriter pw) {
		for (StackTraceElement stackTraceEl : stackTrace) {
			pw.println(stackTraceEl);
		}
	}

}
