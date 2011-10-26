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

	public final static byte TYPE_CONNECT_SYN = 2;
	public final static byte TYPE_CONNECT_ACK = 3;
	public final static byte TYPE_ROUTING_INFORMATION = 4;

	public final static byte TYPE_BROADCAST = 5;

	public final static byte TYPE_DATA = 6;
	public final static byte TYPE_AWARENESS_INFO = 7;

	public byte Type;

	public final static String[] TYPE_DESCRIPTIONS = { "PING", "PONG", "SYN",
			"ACK", "ROUTING_INFORMATION", "BROADCAST", "DATA", "AWARENESS_INFO" };

	public String Src;
	public String Dest;
	public byte SeqNo;

	private String pktId;
	public byte HopCount;

	private short dataSize;

	private byte[] data;

	// this is copied from BTClick. Setting higher values should work just fine.
	public static final int PACKET_SIZE = 2048;
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
		checkType();
		this.data = data == null ? new byte[0] : data;
		checkDataSize();
		if (dest != null) {
			this.Dest = dest;
		}
		this.Src = Helper.getBluetoothAdapterFactory()
				.getDefaultBluetoothAdapter().getAddress();
		newPaketID();
	}

	public DataPacket(byte[] buffer) {
		this.Type = buffer[0];
		checkType();
		this.Src = new String(buffer, 1, 17);
		this.Dest = new String(buffer, 18, 17);
		this.pktId = new String(buffer, 35, 21);
		this.HopCount = buffer[56];
		byte[] dataSize = new byte[] { buffer[57], buffer[58] };

		this.dataSize = readShort(dataSize, 0);
		this.SeqNo = buffer[59];

		if (buffer.length < this.dataSize+60) {
			throw new MessageConvertException("Could not read packet from byte Array. Buffer length is " +
					buffer.length + " and dataSize is " +
					this.dataSize + " (plus header)!\n"
					+ this.toString());
		}
		
		this.data = new byte[this.dataSize];
		if (this.dataSize > 0) {
			for (int i = 0; i < this.dataSize; i++) {
				this.data[i] = buffer[i + 60];
			}
		}

		dataSize = null;
		buffer = null;
	}



	public byte[] asByteArray() {
		if (Src == null || Dest == null || pktId == null) {
			throw new MessageConvertException(
					"Message was underspecified. Dest, pktId are required.");
		}
		checkDataSize();
		byte[] packet = new byte[HEADER_SIZE + dataSize];
		packet[0] = this.Type;
		checkAddresses();
		int pos = insertByteArrayFromPosition(1, packet, this.Src.getBytes());
		pos = insertByteArrayFromPosition(pos, packet, this.Dest.getBytes());
		pos = insertByteArrayFromPosition(pos, packet, this.pktId.getBytes());
		packet[pos] = this.HopCount;
		
		
		pos = insertByteArrayFromPosition(pos + 1, packet,
				shortToByteArray(this.dataSize));
		packet[pos] = this.SeqNo;
		
		if (pos != 59) {
			throw new MessageConvertException("Something went wrong while encoding this message.\n" + this.toString());
		}
		if (dataSize != 0) {
			pos = insertByteArrayFromPosition(pos + 1, packet, this.data);
		}
		return packet;
	}

	private void checkAddresses() {
		if (Src.length() != 17) {
			if (Src.matches("bt-mtp://.*")) {
				Src = Src.substring(9);
				checkAddresses();
			} else {
				throw new MessageConvertException("Could not encode Message: SRC must be a valid Bluetooth Address (17 byte)");
			}
		}
		
		if (Dest.length() != 17) {
			if (Dest.matches("bt-mtp://.*")) {
				Dest = Dest.substring(9);
				checkAddresses();
			} else {
				throw new MessageConvertException("Could not encode Message: DEST must be a valid Bluetooth Address (17 byte)");
			}
		}
	}
	
	private void checkType() {
		if (this.Type > TYPE_DESCRIPTIONS.length) {
			throw new MessageConvertException("Could not encode Message: Type must be valid!");
		}
	}
	
	private void checkDataSize() {
		int length = data.length;
		if (length > Short.MAX_VALUE || length > DATA_MAX_SIZE) {
			throw new MessageToLongException(data, DATA_MAX_SIZE);
		}
		this.dataSize = (short) length;
}

	private int insertByteArrayFromPosition(int pos, byte[] baseArr,
			byte[] toInsert) {
		int i;
		try {
			for (i = 0; i < toInsert.length; i++) {
				baseArr[pos + i] = toInsert[i];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return pos;
		}
		return pos + i;
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
		String type = TYPE_DESCRIPTIONS[Type];
		StringBuilder s = new StringBuilder("From: ");
		s.append(Src);
		s.append("\nTo: ");
		s.append(Dest);
		s.append("\nType: ");
		s.append(type);
		if (dataSize < 100) {
			s.append("\nContent (size" + dataSize + "/" + data.length + "): ");
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
