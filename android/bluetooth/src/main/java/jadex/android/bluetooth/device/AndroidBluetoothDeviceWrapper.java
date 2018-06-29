package jadex.android.bluetooth.device;

import jadex.android.bluetooth.util.Helper;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;

/**
 * A Wrapper to abstract from the Android BluetoothDevice Implementation. 
 * @author Julian Kalinowski
 */
public class AndroidBluetoothDeviceWrapper implements IBluetoothDevice {

	private BluetoothDevice dev;
	private String address;
	private String name;

	// public static final Parcelable.Creator<AndroidBluetoothDeviceWrapper> CREATOR =
	// new Parcelable.Creator<AndroidBluetoothDeviceWrapper>() {
	//
	// public AndroidBluetoothDeviceWrapper createFromParcel(Parcel in) {
	// BluetoothDevice dev = in.readParcelable(BluetoothDevice.class
	// .getClassLoader());
	// return new AndroidBluetoothDeviceWrapper(dev);
	// }
	//
	// @Override
	// public AndroidBluetoothDeviceWrapper[] newArray(int size) {
	// return new AndroidBluetoothDeviceWrapper[size];
	// }
	// };
	
	/**
	 * Constructor
	 * @param dev {@link IBluetoothDevice} to be wrapped.
	 */
	public AndroidBluetoothDeviceWrapper(IBluetoothDevice dev) {
		IBluetoothAdapter bluetoothAdapter = Helper.getBluetoothAdapterFactory().getDefaultBluetoothAdapter();
		if (bluetoothAdapter instanceof AndroidBluetoothAdapterWrapper) {
			AndroidBluetoothAdapterWrapper androidAdapter = (AndroidBluetoothAdapterWrapper) bluetoothAdapter;
			BluetoothDevice remoteDevice = androidAdapter.getBluetoothAdapter().getRemoteDevice(dev.getAddress());
			this.address = remoteDevice.getAddress();
			this.name = remoteDevice.getName();
			deviceNames.put(remoteDevice.getAddress(), remoteDevice.getName());
			this.dev = remoteDevice;
		} else {
			throw new RuntimeException("Don't use the AndroidBluetoothDeviceWrapper class on J2SE");
		}
	}

	/**
	 * Constructor
	 * @param dev {@link BluetoothDevice} to be wrapped
	 */
	public AndroidBluetoothDeviceWrapper(BluetoothDevice dev) {
		this.address = dev.getAddress();
		this.name = dev.getName();
		deviceNames.put(dev.getAddress(), dev.getName());
		this.dev = dev;
	}

	public String toString() {
		return dev.getName() + " [" + dev.getAddress() + "]";
	}

	/**
	 * @return the wrapped {@link BluetoothDevice}
	 */
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
	
	/**
	 * Converts from Android Bond States to the device-independent states in {@link BluetoothBondState}
	 * @param androidBondState
	 * @return {@link BluetoothBondState}
	 */
	public static BluetoothBondState convertFromAndroidBondState(int androidBondState) {
		switch (androidBondState) {
		case BluetoothDevice.BOND_BONDED:
			return BluetoothBondState.bonded;
		case BluetoothDevice.BOND_BONDING:
			return BluetoothBondState.bonding;
		case BluetoothDevice.BOND_NONE:
		default:
			return BluetoothBondState.none;
		}
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
