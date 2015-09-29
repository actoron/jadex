package jadex.android.bluetooth.connection;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jadex.android.bluetooth.CustomTestRunner;
import jadex.android.bluetooth.connection.AConnection.ConnectedThread;
import jadex.android.bluetooth.device.AndroidBluetoothAdapterWrapper;
import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothServerSocket;
import jadex.android.bluetooth.device.IBluetoothSocket;
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.DataPacketTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.Parcel;

@RunWith(CustomTestRunner.class)
public class AConnectionTest {

	private IBluetoothDevice iBluetoothDevice;
	private IBluetoothAdapter iBluetoothAdapter;
	private AConnection conn;
	private IBluetoothSocket socket;
	private ConnectedThread thread;

	@Before
	public void setUp() {
		createDummyClasses();
		conn = new AConnection(iBluetoothAdapter, iBluetoothDevice) {

			@Override
			public void connect() {
			}
		};
		thread = conn.new ConnectedThread(socket);
		conn.setConnectionAlive(true);
	}

	@Test
	public void testSmallPacket() throws MessageConvertException, IOException {
		byte[] buffer = new byte[DataPacket.PACKET_SIZE / 2];
		DataPacket packet = getMessage(DataPacket.DATA_MAX_SIZE / 3);
		final byte[] smallMessage = packet.asByteArray();
		InputStream inputStream = new EmulatedInputStream(smallMessage);
		DataPacket read = conn.readPacketFromStream(inputStream, buffer).get(0);
		assertEquals(packet, read);
	}

	@Test
	public void testBigPacket() throws MessageConvertException, IOException {
		byte[] buffer = new byte[DataPacket.PACKET_SIZE / 2];
		DataPacket packet = getMessage(DataPacket.DATA_MAX_SIZE);
		final byte[] smallMessage = packet.asByteArray();
		InputStream inputStream = new EmulatedInputStream(smallMessage);
		DataPacket read = conn.readPacketFromStream(inputStream, buffer).get(0);
		assertEquals(packet, read);
	}

	@Test
	public void testMultiPacket() throws MessageConvertException, IOException {
		byte[] buffer = new byte[DataPacket.PACKET_SIZE / 2];
		DataPacket packet1 = getMessage(DataPacket.DATA_MAX_SIZE / 3);
		DataPacket packet2 = getMessage(DataPacket.DATA_MAX_SIZE / 4);

		writeReadAndCompare(buffer, packet1, packet2);
	}

	@Test
	public void testMultiPacket2() throws MessageConvertException, IOException {
		byte[] buffer = new byte[DataPacket.PACKET_SIZE / 2];
		DataPacket packet1 = getMessage(110 - DataPacket.HEADER_SIZE);
		DataPacket packet2 = getMessage(60 - DataPacket.HEADER_SIZE);
		
		writeReadAndCompare(buffer, packet1, packet2);
	}

	@Test
	public void testMultiPacket3() throws MessageConvertException, IOException {
		byte[] buffer = new byte[1024];
		DataPacket packet1 = getMessage(1023 - DataPacket.HEADER_SIZE);
		DataPacket packet2 = getMessage(80);

		writeReadAndCompare(buffer, packet1, packet2);
	}


	@Test
	public void testMultiPacket4() throws MessageConvertException, IOException {
		byte[] buffer = new byte[10];
		DataPacket packet1 = getMessage(1023 - DataPacket.HEADER_SIZE);
		DataPacket packet2 = getMessage(80);
		
		writeReadAndCompare(buffer, packet1, packet2);
	}
	
	private void writeReadAndCompare(byte[] buffer, DataPacket packet1,
			DataPacket packet2) throws MessageConvertException, IOException {
		final byte[] message1 = packet1.asByteArray();
		final byte[] message2 = packet2.asByteArray();
		
		byte[] together = new byte[message1.length + message2.length];
		
		for (int i = 0; i < message1.length; i++) {
			together[i] = message1[i];
		}
		for (int i = message1.length; i < together.length; i++) {
			together[i] = message2[i - message1.length];
		}
		
		List<DataPacket> allPackets = new ArrayList<DataPacket>();
		List<DataPacket> read = new ArrayList<DataPacket>();
		InputStream inputStream = new EmulatedInputStream(together);
		
		while (allPackets.size() != 2) {
			read = conn.readPacketFromStream(inputStream, buffer);
			allPackets.addAll(read);
		}
		
		assertEquals(packet1, allPackets.get(0));
		assertTrue(Arrays.equals(message1, allPackets.get(0).asByteArray()));
		
		assertEquals(packet2, allPackets.get(1));
		assertTrue(Arrays.equals(message2, allPackets.get(1).asByteArray()));
		
	}

	private DataPacket getMessage(int datasize) throws MessageConvertException {
		IBluetoothDevice dev = DataPacketTest.getBTDummyDevice();

		String testStringPart = "This is TestData stuff";

		StringBuilder testString = new StringBuilder();

		while (testString.length() < datasize - testStringPart.length()) {
			testString.append(testStringPart);
		}
		BluetoothMessage btmsg = new BluetoothMessage(dev, testString
				.toString().getBytes(), DataPacket.TYPE_DATA);

		DataPacket dataPacket = new DataPacket(btmsg, btmsg.getType());
		return dataPacket;
	}

	private void createDummyClasses() {
		iBluetoothAdapter = new IBluetoothAdapter() {

			@Override
			public void startDiscovery() {
				// TODO Auto-generated method stub

			}

			@Override
			public IBluetoothServerSocket listenUsingRfcommWithServiceRecord(
					String serviceName, UUID uuid) throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isEnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDiscovering() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDeviceBonded(IBluetoothDevice device) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public IBluetoothDevice getRemoteDevice(String address) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<IBluetoothDevice> getBondedDevices() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getAddress() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void enable() {
				// TODO Auto-generated method stub

			}

			@Override
			public void cancelDiscovery() {
				// TODO Auto-generated method stub

			}
		};

		iBluetoothDevice = new IBluetoothDevice() {

			@Override
			public int describeContents() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void writeToParcel(Parcel dest, int flags) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getAddress() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setName(String name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setAddress(String address) {
				// TODO Auto-generated method stub

			}

			@Override
			public IBluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)
					throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

		};

		socket = new IBluetoothSocket() {

			@Override
			public void close() throws IOException {
			}

			@Override
			public IBluetoothDevice getRemoteDevice() {
				return null;
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
				return null;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return null;
			}

			@Override
			public void connect() throws IOException {
			}
		};
	}

	public class EmulatedInputStream extends InputStream {

		private int bytesRead;
		private byte[] source;

		public EmulatedInputStream(byte[] source) {
			this.source = source;
			bytesRead = -1;
		}

		@Override
		public int read() throws IOException {
			bytesRead++;
			if (source.length > bytesRead) {
				return source[bytesRead];
			} else
				return -1;
		}

		@Override
		public int available() throws IOException {
			return source.length - (bytesRead == -1 ? 0 : bytesRead);
		}
	}

}
