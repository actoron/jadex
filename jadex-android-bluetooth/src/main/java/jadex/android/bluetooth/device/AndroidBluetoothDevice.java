package jadex.android.bluetooth.device;

import android.bluetooth.BluetoothDevice;

public class AndroidBluetoothDevice extends MyBluetoothDevice {
	
	private BluetoothDevice dev;

//	public static final Parcelable.Creator<AndroidBluetoothDevice> CREATOR = new Parcelable.Creator<AndroidBluetoothDevice>() {
//
//		public AndroidBluetoothDevice createFromParcel(Parcel in) {
//			BluetoothDevice dev = in.readParcelable(BluetoothDevice.class
//					.getClassLoader());
//			return new AndroidBluetoothDevice(dev);
//		}
//
//		@Override
//		public AndroidBluetoothDevice[] newArray(int size) {
//			return new AndroidBluetoothDevice[size];
//		}
//	};

	public AndroidBluetoothDevice(BluetoothDevice dev) {
		super(dev.getAddress());
		deviceNames.put(dev.getAddress(), dev.getName());
		this.dev = dev;
	}

	public String toString() {
		return dev.getName() + " [" + dev.getAddress() + "]";
	}

	public BluetoothDevice getDevice() {
		return dev;
	}
	
	@Override
	public String getName() {
		return dev.getName();
	}

//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeParcelable(dev, flags);
//	}
}
