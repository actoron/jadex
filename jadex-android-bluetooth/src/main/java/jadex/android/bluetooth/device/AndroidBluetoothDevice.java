package jadex.android.bluetooth.device;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;

public class AndroidBluetoothDevice implements IBluetoothDevice {

	private BluetoothDevice dev;
	private String address;
	private String name;

	// public static final Parcelable.Creator<AndroidBluetoothDevice> CREATOR =
	// new Parcelable.Creator<AndroidBluetoothDevice>() {
	//
	// public AndroidBluetoothDevice createFromParcel(Parcel in) {
	// BluetoothDevice dev = in.readParcelable(BluetoothDevice.class
	// .getClassLoader());
	// return new AndroidBluetoothDevice(dev);
	// }
	//
	// @Override
	// public AndroidBluetoothDevice[] newArray(int size) {
	// return new AndroidBluetoothDevice[size];
	// }
	// };
	
	public AndroidBluetoothDevice(IBluetoothDevice dev) {
		IBluetoothAdapter bluetoothAdapter = BluetoothAdapterFactory.getBluetoothAdapter();
		if (bluetoothAdapter instanceof AndroidBluetoothAdapterWrapper) {
			AndroidBluetoothAdapterWrapper androidAdapter = (AndroidBluetoothAdapterWrapper) bluetoothAdapter;
			BluetoothDevice remoteDevice = androidAdapter.getBluetoothAdapter().getRemoteDevice(dev.getAddress());
			this.address = remoteDevice.getAddress();
			this.name = remoteDevice.getName();
			deviceNames.put(remoteDevice.getAddress(), remoteDevice.getName());
			this.dev = remoteDevice;
		} else {
			throw new RuntimeException("Don't use the AndroidBluetoothDevice class on J2SE");
		}
	}

	public AndroidBluetoothDevice(BluetoothDevice dev) {
		this.address = dev.getAddress();
		this.name = dev.getName();
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

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public IBluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)
			throws IOException {
		BluetoothSocket socket = dev.createRfcommSocketToServiceRecord(uuid);
		return new AndroidBluetoothSocketWrapper(socket);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(address);
		dest.writeString(name);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IBluetoothDevice) {
			IBluetoothDevice other = (IBluetoothDevice) o;
			if (other.getAddress().equals(this.getAddress())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getAddress().hashCode();
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
