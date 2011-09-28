package jadex.android.bluetooth.device;

import android.os.Parcel;

public class MyBluetoothDevice implements IBluetoothDevice {

	private String address;
	private String name;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(address);
		dest.writeString(name);
	}

	public MyBluetoothDevice(String address) {
		this.address = address;
		this.name = deviceNames.get(address);
	}

	@Override
	public String getName() {
		return name;
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
	public String toString() {
		return getName() + " [" + getAddress() +"]";
	}
}
