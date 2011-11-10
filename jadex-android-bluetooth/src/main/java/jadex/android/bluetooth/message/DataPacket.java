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

	private byte _type;
	public final static int INDEX_Type = 0;
	
	private String _src;
	public final static int INDEX_Src_START = 1;
	public final static int INDEX_Src_END = 17;
	public final static int INDEX_Src_SIZE = INDEX_Src_END - INDEX_Src_START + 1;
	
	private String _dest;
	public final static int INDEX_Dest_START = 18;
	public final static int INDEX_Dest_END = 34;
	public final static int INDEX_Dest_SIZE = INDEX_Dest_END - INDEX_Dest_START + 1;
	
	private String _pktId;
	public final static int INDEX_pktId_START = 35;
	public final static int INDEX_pktId_END = 55;
	public final static int INDEX_pktId_SIZE = INDEX_pktId_END - INDEX_pktId_START + 1;
	
	private byte _hopCount;
	public final static int INDEX_HopCount = 56;
	
	public final static int INDEX_dataSize_START = 57;
	public final static int INDEX_dataSize_END = 58;
	private short dataSize;

	private final static int INDEX_SeqNo = 59;
	private byte SeqNo;

	private byte[] _data;

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
		this._type = type;
		checkType();
		this._data = data == null ? new byte[0] : data;
		checkDataSize();
		this._src = Helper.getBluetoothAdapterFactory()
		.getDefaultBluetoothAdapter().getAddress();
		if (dest != null) {
			this._dest = dest;
			checkAddresses();
		}
		newPaketID();
	}

	public DataPacket(byte[] buffer) throws MessageConvertException {
		this._type = buffer[0];
		checkType();
		this._src = new String(buffer, INDEX_Src_START, INDEX_Src_SIZE);
		this._dest = new String(buffer, INDEX_Dest_START, INDEX_Dest_SIZE);
		checkAddresses();
		this._pktId = new String(buffer, INDEX_pktId_START, INDEX_pktId_SIZE);
		this._hopCount = buffer[INDEX_HopCount];

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
		
		this._data = new byte[this.dataSize];
		if (this.dataSize > 0) {
			for (int i = 0; i < this.dataSize; i++) {
				this._data[i] = buffer[i + HEADER_SIZE];
			}
		}
		checkDataSize();

		buffer = null;
	}
	
	public static short getDataSizeFromPacketByteArray(byte[] buffer, int offset) {
		return readShortFromBytes(buffer[INDEX_dataSize_START + offset], buffer[INDEX_dataSize_END + offset]);
	}

	public byte[] asByteArray() throws MessageConvertException {
		if (_src == null || _dest == null || _pktId == null) {
			throw new MessageConvertException(
					"Message was underspecified. Dest, pktId are required.");
		}
		
		checkAddresses();
		checkDataSize();
		checkType();
		
		byte[] packet = new byte[HEADER_SIZE + dataSize];
		packet[0] = this._type;
		int pos = insertByteArrayFromPosition(1, packet, this._src.getBytes());
		pos = insertByteArrayFromPosition(pos, packet, this._dest.getBytes());
		pos = insertByteArrayFromPosition(pos, packet, this._pktId.getBytes());
		packet[pos] = this._hopCount;
		
		
		pos = insertByteArrayFromPosition(pos + 1, packet,
				shortToByteArray(this.dataSize));
		packet[pos] = this.SeqNo;
		
		if (pos != 59) {
			throw new MessageConvertException("Something went wrong while encoding this message.\n" + this.toString());
		}
		if (dataSize != 0) {
			pos = insertByteArrayFromPosition(pos + 1, packet, this._data);
		}
		return packet;
	}

	private void checkAddresses() throws MessageConvertException {
		if (_src == null || _dest == null) {
			throw new MessageConvertException("Src and Dest must be non-null!");
		}
		if (_src.length() != 17) {
			if (_src.matches("bt-mtp://.*")) {
				_src = _src.substring(9);
				checkAddresses();
			} else {
				throw new MessageConvertException("Could not encode/decode Message: SRC must be a valid Bluetooth Address (17 byte), but was: " + _src);
			}
		}
		
		if (_dest.length() != 17) {
			if (_dest.matches("bt-mtp://.*")) {
				_dest = _dest.substring(9);
				checkAddresses();
			} else {
				throw new MessageConvertException("Could not encode/decode Message: DEST must be a valid Bluetooth Address (17 byte), but was: " + _dest);
			}
		}
	}
	
	private void checkType() throws MessageConvertException {
		if (this._type < 0 || this._type >= TYPE_DESCRIPTIONS.length) {
			throw new MessageConvertException("Could not encode/decode Message: Type must be valid! (was " + this._type);
		}
	}
	
	private void checkDataSize() {
		int length = _data.length;
		if (length > Short.MAX_VALUE || length > DATA_MAX_SIZE) {
			throw new MessageToLongException(_data, DATA_MAX_SIZE);
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
		this._pktId = UUID.randomUUID().toString();
		this._pktId = this._pktId.substring(_pktId.length() - 21, _pktId.length());
	}

	private static short readShortFromBytes(byte byte1, byte byte2) {
		return (short) (((byte1 << 8)) | ((byte2 & 0xff)));
	}

	public byte[] shortToByteArray(short s) {
		return new byte[] { (byte) ((s & 0xFF00) >> 8), (byte) (s & 0x00FF) };
	}

	public String getDataAsString() {
		return new String(_data);
	}

	public IBluetoothDevice getDestinationDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(_dest);
	}

	public IBluetoothDevice getSourceDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(_src);
	}
	
	public String getDestination() {
		return _dest;
	}
	
	public void setDestination(String newDest) {
		_dest = newDest;
	}
	
	public String getSource() {
		return _src;
	}
	
	public void setSource(String newSource) {
		_src = newSource;
	}
	
	public String getPktId() {
		return _pktId;
	}

	public byte[] getData() {
		return _data;
	}
	
	public byte getType() {
		return _type;
	}
	
	public void setType(byte newType) {
		_type = newType;
	}
	
	public void incHopCount() {
		_hopCount++;
	}
	public byte getHopCount() {
		return _hopCount;
	}

	@Override
	public String toString() {
		String type = TYPE_DESCRIPTIONS[_type];
		StringBuilder s = new StringBuilder("From: ");
		s.append(_src);
		s.append("\nTo: ");
		s.append(_dest);
		s.append("\nType: ");
		s.append(type);
		if (dataSize < 100) {
			s.append("\nContent (size" + dataSize + "/" + _data.length + "): ");
			s.append(getDataAsString());
		}
		return s.toString();
	}



	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DataPacket)) {
			return false;
		}
		DataPacket other = (DataPacket) obj;
		boolean edata = Arrays.equals(other._data, this._data);
		boolean esrc = other._src.equals(this._src);
		boolean edest = other._dest.equals(this._dest);
		boolean esize = other.dataSize == this.dataSize;
		boolean etype = other._type == this._type;
		boolean ehop = other._hopCount == this._hopCount;
		boolean eid = other._pktId.equals(this._pktId);
		return (edata && esrc && edest && esize && etype && ehop && eid);
	}

	public String getTypeDescription() {
		return TYPE_DESCRIPTIONS[_type];
	}
}
