package jadex.android.bluetooth.message;

import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.exceptions.MessageToLongException;
import jadex.android.bluetooth.util.Helper;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

/**
 * The Message Format which is sent via Bluetooth RFComm Channels.
 * 
 * @author Julian Kalinowski
 * 
 */
public class DataPacket {

	// Types:
	public final static byte TYPE_PING = 0;
	public final static byte TYPE_PONG = 1;

	public final static byte TYPE_CONNECT_SYN = 2;
	public final static byte TYPE_CONNECT_ACK = 3;
	public final static byte TYPE_ROUTING_INFORMATION = 4;

	public final static byte TYPE_BROADCAST = 5;

	public final static byte TYPE_DATA = 6;
	public final static byte TYPE_AWARENESS_INFO = 7;

	/**
	 * The textual representations of the available types. Index in this array
	 * equals byte-value of the corresponding constant.
	 */
	public final static String[] TYPE_DESCRIPTIONS = { "PING", "PONG", "SYN",
			"ACK", "ROUTING_INFORMATION", "BROADCAST", "DATA", "AWARENESS_INFO" };

	// Data Fields; Start/End Indexes of the Fields in the encoded byte array
	private byte _type;
	private final static int INDEX_Type = 0;
	private final static int SIZE_Type = 1;

	private String _src;
	private final static int INDEX_Src_START = 1;
	private final static int INDEX_Src_END = 17;
	private final static int SIZE_Src = INDEX_Src_END - INDEX_Src_START + 1;

	private String _dest;
	private final static int INDEX_Dest_START = 18;
	private final static int INDEX_Dest_END = 34;
	private final static int SIZE_Dest = INDEX_Dest_END - INDEX_Dest_START + 1;

	private String _pktId;
	private final static int INDEX_pktId_START = 35;
	private final static int INDEX_pktId_END = 55;
	private final static int SIZE_pktId = INDEX_pktId_END - INDEX_pktId_START
			+ 1;

	private byte _hopCount;
	private final static int INDEX_HopCount = 56;
	private final static int SIZE_HopCount = 1;

	private final static int INDEX_dataSize_START = 57;
	public final static int INDEX_dataSize_END = 58;
	private final static int SIZE_dataSize = INDEX_dataSize_END
			- INDEX_dataSize_START + 1;
	private short dataSize;

	private final static int INDEX_SeqNo = 59;
	private final static int SIZE_SeqNo = 1;

	private byte SeqNo;

	private byte[] _data;

	/**
	 * Max packet size in bytes.
	 */
	public static final short PACKET_SIZE = 20000;
	/**
	 * Header size
	 */
	public static final short HEADER_SIZE = SIZE_Type + SIZE_Src + SIZE_Dest
			+ SIZE_pktId + SIZE_HopCount + SIZE_dataSize + SIZE_SeqNo;
	/**
	 * Max Data Size
	 */
	public static final short DATA_MAX_SIZE = PACKET_SIZE - HEADER_SIZE;

	public static Random random = new Random();

	/**
	 * Create a DataPacket from a {@link BluetoothMessage}
	 * 
	 * @param msg
	 *            {@link BluetoothMessage}
	 * @param type
	 *            Type
	 * @throws MessageConvertException
	 *             if Message contains errors
	 */
	public DataPacket(BluetoothMessage msg, byte type)
			throws MessageConvertException {
		this(msg.getRemoteAddress(), msg.getData(), type);
	}

	/**
	 * Create a DataPacket
	 * 
	 * @param dev
	 *            Destination Device
	 * @param data
	 *            Raw Data
	 * @param type
	 *            Type
	 * @throws MessageConvertException
	 *             if Message contains errors
	 */
	public DataPacket(IBluetoothDevice dev, byte[] data, byte type)
			throws MessageConvertException {
		this(dev.getAddress(), data, type);
	}

	/**
	 * Create a DataPacket
	 * 
	 * @param dest
	 *            Destination Device
	 * @param data
	 *            Raw Data
	 * @param type
	 *            Type
	 * @throws MessageConvertException
	 *             if Message contains errors
	 */
	public DataPacket(String dest, byte[] data, byte type)
			throws MessageConvertException {
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

	/**
	 * Create a DataPacket from Byte Array.
	 * 
	 * @param buffer
	 *            which contains the Message
	 * @throws MessageConvertException
	 *             if Message contains errors
	 */
	public DataPacket(byte[] buffer) throws MessageConvertException {
		this._type = buffer[INDEX_Type];
		checkType();
		this._src = new String(buffer, INDEX_Src_START, SIZE_Src);
		this._dest = new String(buffer, INDEX_Dest_START, SIZE_Dest);
		checkAddresses();
		this._pktId = new String(buffer, INDEX_pktId_START, SIZE_pktId);
		this._hopCount = buffer[INDEX_HopCount];

		this.dataSize = getDataSizeFromPacketByteArray(buffer, 0);

		if (this.dataSize < 0) {
			throw new MessageConvertException(
					"Could not read packet from byte Array. Buffer length is "
							+ buffer.length + " and dataSize is "
							+ this.dataSize + " (plus header)!\n"
							+ this.toString());
		}

		this.SeqNo = buffer[INDEX_SeqNo];

		if (buffer.length < this.dataSize + HEADER_SIZE) {
			throw new MessageConvertException(
					"Could not read packet from byte Array. Buffer length is "
							+ buffer.length + " and dataSize is "
							+ this.dataSize + " (plus header)!\n"
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

	/**
	 * Reads only the DataSize from a DataPacket, which has been converted to a
	 * Byte Array
	 * 
	 * @param buffer
	 *            the byte Array
	 * @param offset
	 *            the offset to the index where the DataPacket starts
	 * @return {@link Short}
	 */
	public static short getDataSizeFromPacketByteArray(byte[] buffer, int offset) {
		return readShortFromBytes(buffer[INDEX_dataSize_START + offset],
				buffer[INDEX_dataSize_END + offset]);
	}

	/**
	 * Converts this DataPacket to a Byte Array.
	 * 
	 * @return byte Array containing all fields of this {@link DataPacket},
	 *         including the raw data.
	 * @throws MessageConvertException
	 *             if the Message could not be written in a byte Array
	 */
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
			throw new MessageConvertException(
					"Something went wrong while encoding this message.\n"
							+ this.toString());
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
				throw new MessageConvertException(
						"Could not encode/decode Message: SRC must be a valid Bluetooth Address (17 byte), but was: "
								+ _src);
			}
		}

		if (_dest.length() != 17) {
			if (_dest.matches("bt-mtp://.*")) {
				_dest = _dest.substring(9);
				checkAddresses();
			} else {
				throw new MessageConvertException(
						"Could not encode/decode Message: DEST must be a valid Bluetooth Address (17 byte), but was: "
								+ _dest);
			}
		}
	}

	private void checkType() throws MessageConvertException {
		if (this._type < 0 || this._type >= TYPE_DESCRIPTIONS.length) {
			throw new MessageConvertException(
					"Could not encode/decode Message: Type must be valid! (was "
							+ this._type);
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

	/**
	 * Generates a new random Packet ID for this DataPacket
	 */
	public void newPaketID() {
		StringBuilder sb = new StringBuilder();
		while (sb.length() < 21) {
			sb.append(Long.toHexString(random.nextLong()));
		}
		this._pktId = sb.toString();
		this._pktId = this._pktId.substring(_pktId.length() - 21,
				_pktId.length());
	}

	private static short readShortFromBytes(byte byte1, byte byte2) {
		return (short) (((byte1 << 8)) | ((byte2 & 0xff)));
	}

	/**
	 * Converts a Short Value to a byte Array
	 * 
	 * @param s
	 * @return byte Array which contains the two bytes of the {@link Short}
	 */
	public byte[] shortToByteArray(short s) {
		return new byte[] { (byte) ((s & 0xFF00) >> 8), (byte) (s & 0x00FF) };
	}

	/**
	 * Returns the raw data as String.
	 * 
	 * @return String
	 */
	public String getDataAsString() {
		return new String(_data);
	}

	/**
	 * Returns the Destination of this DataPacket.
	 * 
	 * @return {@link IBluetoothDevice}
	 */
	public IBluetoothDevice getDestinationDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(_dest);
	}

	/**
	 * Returns the Destination Address of this DataPacket
	 * 
	 * @return String
	 */
	public String getDestination() {
		return _dest;
	}

	/**
	 * Sets the Destination Address of this DataPacket
	 * 
	 * @param String
	 */
	public void setDestination(String newDest) {
		_dest = newDest;
	}

	/**
	 * Returns the Source of this DataPacket.
	 * 
	 * @return {@link IBluetoothDevice}
	 */
	public IBluetoothDevice getSourceDevice() {
		return Helper.getBluetoothDeviceFactory().createBluetoothDevice(_src);
	}

	/**
	 * Returns the Source Address of this DataPacket.
	 * 
	 * @return {@link IBluetoothDevice}
	 */
	public String getSource() {
		return _src;
	}

	/**
	 * Sets the Source Address of this DataPacket.
	 * 
	 * @param {@link String}
	 */
	public void setSource(String newSource) {
		_src = newSource;
	}

	/**
	 * Returns the Unique ID of this Packet
	 * @return String
	 */
	public String getPktId() {
		return _pktId;
	}

	/**
	 * Returns the payload as byte Array
	 * @return byte[]
	 */
	public byte[] getData() {
		return _data;
	}

	/**
	 * Returns the Type of this DataPacket
	 * @return byte
	 */
	public byte getType() {
		return _type;
	}

	/**
	 * Sets the Type
	 * @param newType
	 */
	public void setType(byte newType) {
		_type = newType;
	}

	/**
	 * Increase the Hop Count of this DataPacket
	 */
	public void incHopCount() {
		_hopCount++;
	}

	/**
	 * Returns the Hop Count of this DataPacket
	 */
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

	/**
	 * Return the textual Representation of this Packet's type.
	 * @return
	 */
	public String getTypeDescription() {
		return TYPE_DESCRIPTIONS[_type];
	}
}
