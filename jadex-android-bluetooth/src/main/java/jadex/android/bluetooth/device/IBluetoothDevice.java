package jadex.android.bluetooth.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.os.Parcel;
import android.os.Parcelable;

public interface IBluetoothDevice extends Parcelable {
	
	public static Map<String, String> deviceNames = new HashMap<String, String>();
	
	public static final Parcelable.Creator<IBluetoothDevice> CREATOR = new Parcelable.Creator<IBluetoothDevice>() {

		public IBluetoothDevice createFromParcel(Parcel in) {
			String address = in.readString();
			String name = in.readString();
			return BluetoothDeviceFactory.createBluetoothDevice(address);
		}

		@Override
		public IBluetoothDevice[] newArray(int size) {
			return new IBluetoothDevice[size];
		}
	};
	String getName();
	String getAddress();
	
	void setName(String name);
	void setAddress(String address);
	
	IBluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) throws IOException;
}
