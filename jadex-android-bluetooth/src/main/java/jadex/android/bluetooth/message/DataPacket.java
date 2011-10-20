package jadex.android.bluetooth.message;

import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.exceptions.MessageToLongException;
import jadex.android.bluetooth.util.Helper;

import java.util.Arrays;
import java.util.UUID;

import android.util.Log;

public class DataPacket {

	public final static byte TYPE_PING = 0;
	public final static byte TYPE_PONG = 1;

	public final static byte TYPE_DATA = 2;
	public final static byte TYPE_BROADCAST = 3;

	public final static byte TYPE_CONNECT_SYN = 4;
	public final static byte TYPE_CONNECT_ACK = 5;

	public final static byte TYPE_ROUTING_INFORMATION = 6;
	
	public final static byte TYPE_AWARENESS_INFO = 7;

	public byte Type;
	
	public final static String[] TYPE_DESCRIPTIONS = {"PING", "PONG", "DATA", "BROADCAST", "SYN",
		"ACK", "ROUTING_INFORMATION", "AWARENESS_INFO"};
	
	public String Src;
	public String Dest;
	public byte SeqNo;

	private String pktId;
	public byte HopCount;

	private short dataSize;

	private byte[] data;

	// this is copied from BTClick. Setting higher values should work just fine.
	public static final int PACKET_SIZE = 1024;
	public static final int HEADER_SIZE = 1 + 17 + 17 + 1 + 21 + 2 + 1;
	public static final int DATA_MAX_SIZE = PACKET_SIZE - HEADER_SIZE;

	public DataPacket(BluetoothMessage msg, byte type) {
		this(msg.getRemoteAdress(), msg.getData(), type);
	}

	public DataPacket(IBluetoothDevice dev, byte[] data, byte type) {
		this(dev.getAddress(), data, type);
	}

	public DataPacket(String dest, byte[] data, byte type) {
		this.Type = type;
		this.data = data;
		if (data != null) {
			int length = data.length;
			if (length > Short.MAX_VALUE || length > DATA_MAX_SIZE) {
				throw new MessageToLongException(data, data.length);
			}
			this.dataSize = (short) length;
		} else {
			this.dataSize = 0;
		}
		if (dest != null) {
			this.Dest = dest;
		}
		this.Src = Helper.getBluetoothAdapterFactory()
				.getDefaultBluetoothAdapter().getAddress();
		newPaketID();
	}

	public DataPacket(byte[] buffer) {
		this.Type = buffer[0];
		this.Src = new String(buffer, 1, 17);
		this.Dest = new String(buffer, 18, 17);
		this.pktId = new String(buffer, 35, 21);
		this.HopCount = buffer[56];
		byte[] dataSize = new byte[] { buffer[57], buffer[58] };

		this.dataSize = readShort(dataSize, 0);
		this.SeqNo = buffer[59];
		if (this.dataSize > 0) {
			this.data = new byte[this.dataSize];

			for (int i = 0; i < this.dataSize; i++) {
				this.data[i] = buffer[i + 60];
			}
		}

		dataSize = null;
		buffer = null;
	}

	// public byte[] asByteArray() {
	// byte header[] = new byte[1];
	// header[0] = this.Type;
	// byte packetAsBytes[] = joinByteArray(header, this.Src.getBytes());
	// packetAsBytes = joinByteArray(packetAsBytes, this.Dest.getBytes());
	//
	// packetAsBytes = joinByteArray(packetAsBytes, this.pktId.getBytes());
	// byte hop[] = new byte[1];
	// hop[0] = this.HopCount;
	// packetAsBytes = joinByteArray(packetAsBytes, hop);
	//
	// byte[] shortToByteArray = shortToByteArray(this.dataSize);
	// packetAsBytes = joinByteArray(packetAsBytes, shortToByteArray);
	//
	// byte seq[] = new byte[1];
	// seq[0] = this.SeqNo;
	// packetAsBytes = joinByteArray(packetAsBytes, seq);
	//
	// packetAsBytes = joinByteArray(packetAsBytes, this.data);
	// // //add a stop
	// // packetAsBytes = joinByteArray(packetAsBytes," ".getBytes());
	// // packetAsBytes[packetAsBytes.length-1] = 0;
	//
	// header = null;
	// hop = null;
	// seq = null;
	//
	// return packetAsBytes;
	// }

	public byte[] asByteArray() {
		if (Src == null || Dest == null || pktId == null) {
			throw new MessageConvertException(
					"Message was underspecified. Dest, pktId are required.");
		}

		byte[] packet = new byte[HEADER_SIZE + dataSize];
		packet[0] = this.Type;
		int pos = insertByteArrayFromPosition(1, packet, this.Src.getBytes());
		pos = insertByteArrayFromPosition(pos, packet, this.Dest.getBytes());
		pos = insertByteArrayFromPosition(pos, packet, this.pktId.getBytes());
		packet[pos] = this.HopCount;
		pos = insertByteArrayFromPosition(pos + 1, packet,
				shortToByteArray(this.dataSize));
		packet[pos] = this.SeqNo;
		if (dataSize != 0) {
			pos = insertByteArrayFromPosition(pos + 1, packet, this.data);
		}
		return packet;
	}

	private int insertByteArrayFromPosition(int pos, byte[] baseArr,
			byte[] toInsert) {
		int i;
		for (i = 0; i < toInsert.length; i++) {
			baseArr[pos + i] = toInsert[i];
		}
		return pos + i;
	}

//	private byte[] joinByteArray(byte[] a, byte[] b) {
//		if (a == null) {
//			return b;
//		} else if (b == null) {
//			return a;
//		}
//		int count = a.length + b.length;
//		byte[] result = new byte[count];
//		for (int i = 0; i < a.length; i++) {
//			result[i] = a[i];
//		}
//		for (int i = 0; i < b.length; i++) {
//			result[a.length + i] = b[i];
//		}
//
//		return result;
//	}

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
		return (data != null) ? new String(data) : new String();
	}

	public IBluetoothDevice getDestinationDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(Dest);
	}

	public IBluetoothDevice getSourceDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(Src);
	}

	@Override
	public String toString() {
		String type = TYPE_DESCRIPTIONS[Type];
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
		if (obj == null || !(obj instanceof DataPacket)) {
			return false;
		}
		DataPacket other = (DataPacket) obj;
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
