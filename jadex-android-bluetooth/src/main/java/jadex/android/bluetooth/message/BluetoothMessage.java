package jadex.android.bluetooth.message;

import jadex.android.bluetooth.device.IBluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class BluetoothMessage implements Parcelable {
	private String remoteAdress;
	private byte[] data;
	private byte type;
	
	public static final Integer NOT_CONNECTABLE = -1;

	public static final Integer MESSAGE_SENT = 1;

	public static final Parcelable.Creator<BluetoothMessage> CREATOR = new Parcelable.Creator<BluetoothMessage>() {

		public BluetoothMessage createFromParcel(Parcel in) {
//			BluetoothDevice device = in.readParcelable(BluetoothMessage.class
//					.getClassLoader());
			String deviceAdress = in.readString();
			int length = in.readInt();
			byte[] data = new byte[length];
			in.readByteArray(data);
			byte type = in.readByte();
			return new BluetoothMessage(deviceAdress, data,type);
		}

		@Override
		public BluetoothMessage[] newArray(int size) {
			return new BluetoothMessage[size];
		}
	};
	
	public BluetoothMessage(IBluetoothDevice remoteDevice, byte[] data, byte type) {
		this.remoteAdress = remoteDevice.getAddress();
		this.data = data;
		this.type = type;
	}

	public BluetoothMessage(String remoteDeviceAdress, byte[] data, byte type) {
		this.remoteAdress = remoteDeviceAdress;
		this.data = data;
		this.type = type;
	}
	

	public byte[] getData() {
		return data;
	}

	public String getRemoteAdress() {
		return remoteAdress;
	}

	public String getDataAsString() {
		return (data == null) ? "" : new String(data).trim();
	}
	
	public void setRemoteAddress(String adr) {
		this.remoteAdress = adr;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//dest.writeParcelable(remoteDevice, 0);
		dest.writeString(remoteAdress);
		dest.writeInt(data.length);
		dest.writeByteArray(data);
		dest.writeByte(getType());
	}

	public byte getType() {
		return type;
	}
}
