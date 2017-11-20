package jadex.android.bluetooth.message;

import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.exceptions.MessageToLongException;
import jadex.android.bluetooth.util.Helper;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public class DataPacketSerializable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3104965344653702936L;
	public static byte TYPE_PING = 0;
	public static byte TYPE_PONG = 1;

	public static byte TYPE_DATA = 2;
	public static byte TYPE_BROADCAST = 3;

	public static byte TYPE_CONNECT_SYN = 4;
	public static byte TYPE_CONNECT_ACK = 5;

	public static byte TYPE_ROUTING_INFORMATION = 6;

	public byte Type;
	public String Src;
	public String Dest;
	public byte SeqNo;

	private String pktId;
	public byte HopCount;

	private short dataSize;

	private byte[] data;

	// this is copied from BTClick. Setting higher values should work just fine.
	public static final int PACKET_SIZE = DataPacket.PACKET_SIZE;
	public static final int HEADER_SIZE = 1 + 17 + 17 + 1 + 2 + 1;
	public static final int DATA_MAX_SIZE = PACKET_SIZE - HEADER_SIZE;

	public DataPacketSerializable(BluetoothMessage msg, byte type) {
		this(msg.getRemoteAddress(), msg.getData(), type);
	}

	public DataPacketSerializable(IBluetoothDevice dev, byte[] data, byte type) {
		this(dev.getAddress(), data, type);
	}

	public DataPacketSerializable(String dest, byte[] data, byte type) {
		this.Type = type;
		this.data = data;
		if (data != null) {
			int length = data.length;
			if (length > Short.MAX_VALUE || length > DATA_MAX_SIZE) {
				throw new MessageToLongException(data, DATA_MAX_SIZE);
			}
			this.dataSize = (short) length;
		} else {
			this.dataSize = 0;
		}
		if (dest != null) {
			this.Dest = dest;
		}
		this.Src = Helper.getBluetoothAdapterFactory().getDefaultBluetoothAdapter().getAddress();
		newPaketID();
	}

	public DataPacketSerializable(byte[] buffer) {
		this.Type = buffer[0];
		this.Src = new String(buffer, 1, 17);
		this.Dest = new String(buffer, 18, 17);
		this.pktId = new String(buffer, 35, 21);
		this.HopCount = buffer[56];
		byte[] dataSize = new byte[] { buffer[57], buffer[58] };

		this.dataSize = readShort(dataSize, 0);
		this.SeqNo = buffer[59];
		this.data = new byte[this.dataSize];

		for (int i = 0; i < this.dataSize; i++) {
			this.data[i] = buffer[i + 60];
		}

		dataSize = null;
		buffer = null;
	}

	public byte[] asByteArray() {
		byte header[] = new byte[1];
		header[0] = this.Type;
		byte packetAsBytes[] = joinByteArray(header, this.Src.getBytes());
		packetAsBytes = joinByteArray(packetAsBytes, this.Dest.getBytes());

		packetAsBytes = joinByteArray(packetAsBytes, this.pktId.getBytes());
		byte hop[] = new byte[1];
		hop[0] = this.HopCount;
		packetAsBytes = joinByteArray(packetAsBytes, hop);

		byte[] shortToByteArray = shortToByteArray(this.dataSize);
		packetAsBytes = joinByteArray(packetAsBytes, shortToByteArray);

		byte seq[] = new byte[1];
		seq[0] = this.SeqNo;
		packetAsBytes = joinByteArray(packetAsBytes, seq);

		packetAsBytes = joinByteArray(packetAsBytes, this.data);
		// //add a stop
		// packetAsBytes = joinByteArray(packetAsBytes," ".getBytes());
		// packetAsBytes[packetAsBytes.length-1] = 0;

		header = null;
		hop = null;
		seq = null;

		return packetAsBytes;
	}

	private byte[] joinByteArray(byte[] a, byte[] b) {
		if (a == null) {
			return b;
		} else if (b == null) {
			return a;
		}
		int count = a.length + b.length;
		byte[] result = new byte[count];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i];
		}
		for (int i = 0; i < b.length; i++) {
			result[a.length + i] = b[i];
		}

		return result;
	}

	public void newPaketID() {
		this.pktId = UUID.randomUUID().toString();
		this.pktId = this.pktId.substring(pktId.length() - 21, pktId.length());
	}

	public short readShort(byte[] data, int offset) {
		return (short) (((data[offset] << 8)) | ((data[offset + 1] & 0xff)));
	}

	public byte[] shortToByteArray(short s) {
		return new byte[] { (byte) ((s & 0xFF00) >> 8), (byte) (s & 0x00FF) };
	}

	public String getDataAsString() {
		return new String(data);
	}

	public IBluetoothDevice getDestinationDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(Dest);
	}

	public IBluetoothDevice getSourceDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(Src);
	}

	@Override
	public String toString() {
		String type = "DATA";
		if (Type == TYPE_PONG) {
			type = "PONG";
		} else if (Type == TYPE_PING) {
			type = "PING";
		} else if (Type == TYPE_CONNECT_SYN) {
			type = "SYN";
		} else if (Type == TYPE_CONNECT_ACK) {
			type = "ACK";
		} else if (Type == TYPE_BROADCAST) {
			type = "BROADCAST";
		} else if (Type == TYPE_ROUTING_INFORMATION) {
			type = "ROUTING_INFO";
		}
		StringBuilder s = new StringBuilder("From: ");
		s.append(Src);
		s.append("\nTo: ");
		s.append(Dest);
		s.append("\nType: ");
		s.append(type);
		if (data != null || dataSize < 100) {
			s.append("\nContent: ");
			s.append(getDataAsString());
		}
		return s.toString();
	}

	public String getPktId() {
		return pktId;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DataPacketSerializable)) {
			return false;
		}
		DataPacketSerializable other = (DataPacketSerializable) obj;
		boolean edata = Arrays.equals(other.data, this.data);
		boolean esrc = other.Src.equals(this.Src);
		boolean edest = other.Dest.equals(this.Dest);
		boolean esize = other.dataSize == this.dataSize;
		boolean etype = other.Type == this.Type;
		boolean ehop = other.HopCount == this.HopCount;
		boolean eid = other.pktId.equals(this.pktId);
		return (edata && esrc && edest && esize && etype && ehop && eid);
	}
}
