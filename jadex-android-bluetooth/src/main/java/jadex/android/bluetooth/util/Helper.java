package jadex.android.bluetooth.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import jadex.android.bluetooth.device.factory.AndroidBluetoothAdapterFactory;
import jadex.android.bluetooth.device.factory.AndroidBluetoothDeviceFactory;
import jadex.android.bluetooth.device.factory.IBluetoothAdapterFactory;
import jadex.android.bluetooth.device.factory.IBluetoothDeviceFactory;

/**
 * Static Helper Class
 * @author Julian Kalinowski
 */
public class Helper {
	/**
	 * Log Tag used to log messages and errors
	 */
	public static final String LOG_TAG = "android-bluetooth";

	/**
	 * Returns an Implementation of {@link IBluetoothAdapterFactory}
	 * @return
	 */
	public static IBluetoothAdapterFactory getBluetoothAdapterFactory() {
		return AndroidBluetoothAdapterFactory.getInstance();
	}
	/**
	 * Returns an Implementation of {@link IBluetoothDeviceFactory}
	 * @return
	 */
	public static IBluetoothDeviceFactory getBluetoothDeviceFactory() {
		return AndroidBluetoothDeviceFactory.getInstance();
	}
	
	/**
	 * Converts a stack Trace to an easy-readable String
	 * @param stackTrace
	 * @return String
	 */
	public static String stackTraceToString(StackTraceElement[] stackTrace) {
		StringWriter sw = new StringWriter();
		printStackTrace(stackTrace, new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * Prints a StrackTrace to a specified {@link PrintWriter}
	 * @param stackTrace
	 * @param pw
	 */
	public static void printStackTrace(StackTraceElement[] stackTrace,
			PrintWriter pw) {
		for (StackTraceElement stackTraceEl : stackTrace) {
			pw.println(stackTraceEl);
		}
	}

}
