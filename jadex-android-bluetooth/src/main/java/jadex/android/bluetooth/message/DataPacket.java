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


	public final static String[] TYPE_DESCRIPTIONS = { "PING", "PONG", "SYN",
			"ACK", "ROUTING_INFORMATION", "BROADCAST", "DATA", "AWARENESS_INFO" };

	public byte Type;
	public final static int INDEX_Type = 0;
	
	public String Src;
	public final static int INDEX_Src_START = 1;
	public final static int INDEX_Src_END = 17;
	public final static int INDEX_Src_SIZE = INDEX_Src_END - INDEX_Src_START + 1;
	
	public String Dest;
	public final static int INDEX_Dest_START = 18;
	public final static int INDEX_Dest_END = 34;
	public final static int INDEX_Dest_SIZE = INDEX_Dest_END - INDEX_Dest_START + 1;
	
	private String pktId;
	public final static int INDEX_pktId_START = 35;
	public final static int INDEX_pktId_END = 55;
	public final static int INDEX_pktId_SIZE = INDEX_pktId_END - INDEX_pktId_START + 1;
	
	public byte HopCount;
	public final static int INDEX_HopCount = 56;
	
	public final static int INDEX_dataSize_START = 57;
	public final static int INDEX_dataSize_END = 58;
	private short dataSize;

	public final static int INDEX_SeqNo = 59;
	public byte SeqNo;



	private byte[] data;

	// this is copied from BTClick. Setting higher values should work just fine.
//	public static final int PACKET_SIZE = 1008;
	public static final short PACKET_SIZE = 2048;
	public static final short HEADER_SIZE = 1 + 17 + 17 + 1 + 21 + 2 + 1;
	public static final short DATA_MAX_SIZE = PACKET_SIZE - HEADER_SIZE;

	public DataPacket(BluetoothMessage msg, byte type) throws MessageConvertException {
		this(msg.getRemoteAdress(), msg.getData(), type);
	}

	public DataPacket(IBluetoothDevice dev, byte[] data, byte type) throws MessageConvertException {
		this(dev.getAddress(), data, type);
	}

	public DataPacket(String dest, byte[] data, byte type) throws MessageConvertException {
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

	public DataPacket(byte[] buffer) throws MessageConvertException {
		this.Type = buffer[0];
		checkType();
		this.Src = new String(buffer, INDEX_Src_START, INDEX_Src_SIZE);
		this.Dest = new String(buffer, INDEX_Dest_START, INDEX_Dest_SIZE);
		this.pktId = new String(buffer, INDEX_pktId_START, INDEX_pktId_SIZE);
		this.HopCount = buffer[INDEX_HopCount];

		this.dataSize = getDataSizeFromPacketByteArray(buffer, 0);
		
		if (this.dataSize < 0) {
			throw new MessageConvertException("Could not read packet from byte Array. Buffer length is " +
					buffer.length + " and dataSize is " +
					this.dataSize + " (plus header)!\n"
					+ this.toString());
		}
		
		this.SeqNo = buffer[INDEX_SeqNo];

		if (buffer.length < this.dataSize+HEADER_SIZE) {
			throw new MessageConvertException("Could not read packet from byte Array. Buffer length is " +
					buffer.length + " and dataSize is " +
					this.dataSize + " (plus header)!\n"
					+ this.toString());
		}
		
		this.data = new byte[this.dataSize];
		if (this.dataSize > 0) {
			for (int i = 0; i < this.dataSize; i++) {
				this.data[i] = buffer[i + HEADER_SIZE];
			}
		}

		buffer = null;
	}

	public static short getDataSizeFromPacketByteArray(byte[] buffer, int offset) {
		return readShortFromBytes(buffer[INDEX_dataSize_START + offset], buffer[INDEX_dataSize_END + offset]);
	}



	public byte[] asByteArray() throws MessageConvertException {
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

	private void checkAddresses() throws MessageConvertException {
		if (Src.length() != 17) {
			if (Src.matches("bt-mtp://.*")) {
				Src = Src.substring(9);
				checkAddresses();
			} else {
				throw new MessageConvertException("Could not encode/decode Message: SRC must be a valid Bluetooth Address (17 byte), but was: " + Src);
			}
		}
		
		if (Dest.length() != 17) {
			if (Dest.matches("bt-mtp://.*")) {
				Dest = Dest.substring(9);
				checkAddresses();
			} else {
				throw new MessageConvertException("Could not encode/decode Message: DEST must be a valid Bluetooth Address (17 byte), but was: " + Dest);
			}
		}
	}
	
	private void checkType() throws MessageConvertException {
		if (this.Type < 0 || this.Type >= TYPE_DESCRIPTIONS.length) {
			throw new MessageConvertException("Could not encode/decode Message: Type must be valid! (was " + this.Type);
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

	private static short readShortFromBytes(byte byte1, byte byte2) {
		return (short) (((byte1 << 8)) | ((byte2 & 0xff)));
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
