package jadex.android.bluetooth.device;

import jadex.android.bluetooth.util.Helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Interface for Bluetooth Devices
 * 
 * @author Julian Kalinowski
 */
public interface IBluetoothDevice extends Parcelable {

	/**
	 * Bond states for Bluetooth Devices
	 * 
	 * @author Julian Kalinowski
	 */
	public enum BluetoothBondState {
		/**
		 * Bonded state
		 */
		bonded,
		/**
		 * Not Bonded state
		 */
		none,
		/**
		 * Binding in progress state
		 */
		bonding
	}

	/**
	 * Cached device names
	 */
	static Map<String, String> deviceNames = new HashMap<String, String>();

	public static final Parcelable.Creator<IBluetoothDevice> CREATOR = new Parcelable.Creator<IBluetoothDevice>() {

		public IBluetoothDevice createFromParcel(Parcel in) {
			String address = in.readString();
			String name = in.readString();
			return Helper.getBluetoothDeviceFactory().createBluetoothDevice(
					address);
		}

		@Override
		public IBluetoothDevice[] newArray(int size) {
			return new IBluetoothDevice[size];
		}
	};

	/**
	 * @return Name of this Bluetooth Device
	 */
	String getName();

	/**
	 * @return Address of this Bluetooth Device
	 */
	String getAddress();

	/**
	 * Sets the Name of this device
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * Sets the Address of this device
	 * 
	 * @param address
	 */
	void setAddress(String address);

	/**
	 * Try to Connect to this device using RFcomm and the specified UUID
	 * @param uuid 
	 * @return {@link IBluetoothSocket}
	 * @throws IOException if connection failed
	 */
	IBluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)
			throws IOException;
}
