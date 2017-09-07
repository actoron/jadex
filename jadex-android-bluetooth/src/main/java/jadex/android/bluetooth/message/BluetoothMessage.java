package jadex.android.bluetooth.message;

import jadex.android.bluetooth.device.IBluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is a BluetoothMessage. This Class is used in all Levels above Packet
 * Routing. Contains raw data for the application.
 * 
 * @author Julian Kalinowski
 */
public class BluetoothMessage implements Parcelable {
	private String remoteAddress;
	private byte[] data;
	private byte type;

	/**
	 * Sending unsuccessful
	 */
	public static final Integer NOT_CONNECTABLE = -1;

	/**
	 * Sending successful
	 */
	public static final Integer MESSAGE_SENT = 1;

	public static final Parcelable.Creator<BluetoothMessage> CREATOR = new Parcelable.Creator<BluetoothMessage>() {

		public BluetoothMessage createFromParcel(Parcel in) {
			// BluetoothDevice device = in.readParcelable(BluetoothMessage.class
			// .getClassLoader());
			String deviceAdress = in.readString();
			int length = in.readInt();
			byte[] data = new byte[length];
			in.readByteArray(data);
			byte type = in.readByte();
			return new BluetoothMessage(deviceAdress, data, type);
		}

		@Override
		public BluetoothMessage[] newArray(int size) {
			return new BluetoothMessage[size];
		}
	};

	/**
	 * Constructor
	 * 
	 * @param remoteDevice
	 * @param data
	 * @param type
	 *            The type of this Message, one of the Type Constants in
	 *            {@link DataPacket}
	 */
	public BluetoothMessage(IBluetoothDevice remoteDevice, byte[] data,
			byte type) {
		this.remoteAddress = remoteDevice.getAddress();
		this.data = data;
		this.type = type;
	}

	/**
	 * @param remoteDeviceAdress
	 * @param data
	 * @param type
	 *            The type of this Message, one of the Type Constants in
	 *            {@link DataPacket}
	 */
	public BluetoothMessage(String remoteDeviceAdress, byte[] data, byte type) {
		this.remoteAddress = remoteDeviceAdress;
		this.data = data;
		this.type = type;
	}

	/**
	 * @return The raw data carried by this Message
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Interpret the raw data as String and return it
	 * 
	 * @return String interpretation of the data
	 */
	public String getDataAsString() {
		return (data == null) ? "" : new String(data).trim();
	}

	/**
	 * Returns the remote address
	 * @return String
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Sets the Remote Address
	 * @param adr String
	 */
	public void setRemoteAddress(String adr) {
		this.remoteAddress = adr;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// dest.writeParcelable(remoteDevice, 0);
		dest.writeString(remoteAddress);
		dest.writeInt(data.length);
		dest.writeByteArray(data);
		dest.writeByte(getType());
	}

	/**
	 * Returns the type of this Message.
	 * @return one of the Constants in DataPacket
	 */
	public byte getType() {
		return type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BluetoothMessage\n");
		sb.append("Receiver: ");
		sb.append(remoteAddress);
		sb.append(", Type: ");
		sb.append(type);
		sb.append("\nData (first 20 bytes):\n");
		for (int i = 0; i < 20 && i < data.length; i++) {
			sb.append(data[i]);
		}
		return sb.toString();
	}
}
