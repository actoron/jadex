package jadex.android.bluetooth.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import jadex.android.bluetooth.CustomTestRunner;
import jadex.android.bluetooth.TestConstants;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothSocket;
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.exceptions.MessageToLongException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.Parcel;

@RunWith(CustomTestRunner.class)
public class DataPacketTest {
	private IBluetoothDevice dev;
	private BluetoothMessage btmsg;

	// for performance measures:
	// private int REPEAT_TIMES = 1000;
	private int REPEAT_TIMES = 1;
	private String maxDataString;

	@Before
	public void setup() {
		dev = getBTDummyDevice();

		String testStringPart = "This is TestData stuff";

		StringBuilder testString = new StringBuilder();

		while (testString.length() < DataPacket.DATA_MAX_SIZE
				- testStringPart.length()) {
			testString.append(testStringPart);
		}
		btmsg = new BluetoothMessage(dev, testString.toString().getBytes(),
				DataPacket.TYPE_DATA);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < DataPacket.DATA_MAX_SIZE; i++) {
			sb.append('A');
		}

		maxDataString = sb.toString();
	}

	@Test
	public void testJavaSerialisation() throws IOException,
			ClassNotFoundException {
		DataPacketSerializable dataPacketSerializable = new DataPacketSerializable(
				btmsg, btmsg.getType());
		dataPacketSerializable.Src = TestConstants.sampleAddress;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		Object readObject = null;

		long start = System.nanoTime(); // requires java 1.5
		for (int i = 0; i < REPEAT_TIMES; i++) {
			objectOutputStream.writeObject(dataPacketSerializable);
			objectOutputStream.flush();
			objectOutputStream.close();
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			ObjectInputStream objectInputStream = new ObjectInputStream(
					new ByteArrayInputStream(byteArray));
			readObject = objectInputStream.readObject();
		}

		double elapsedTimeInSec = (System.nanoTime() - start) * 1.0e-9;
		System.out.println("Default Java Serialisation took: "
				+ elapsedTimeInSec);
		assertEquals(readObject, dataPacketSerializable);
	}

	@Test
	public void testCustomSerialisation() throws MessageConvertException {
		long start = System.nanoTime(); // requires java 1.5
		DataPacket dataPacket = new DataPacket(btmsg, btmsg.getType());
		DataPacket dataPacket2 = null;

		for (int i = 0; i < REPEAT_TIMES; i++) {
			dataPacket.setSource(TestConstants.sampleAddress);
			byte[] byteArr = dataPacket.asByteArray();
			dataPacket2 = new DataPacket(byteArr);
		}

		double elapsedTimeInSec = (System.nanoTime() - start) * 1.0e-9;
		System.out.println("Custom Byte Array Building took: "
				+ elapsedTimeInSec);
		assertEquals(dataPacket, dataPacket2);
	}

	@Test
	public void testDataPacketCreation() throws MessageConvertException {
		DataPacket packet = new DataPacket(TestConstants.sampleAddress,
				"".getBytes(), DataPacket.TYPE_BROADCAST);
		byte[] asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);

		BluetoothMessage btMsg = new BluetoothMessage(
				TestConstants.sampleAddress, "test".getBytes(),
				DataPacket.TYPE_CONNECT_SYN);
		packet = new DataPacket(btMsg, btMsg.getType());
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);

		packet = new DataPacket(asByteArray);
		assertEquals("test", packet.getDataAsString());
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);
	}

	@Test
	public void testBigDataPacket() throws MessageConvertException {

		DataPacket packet = new DataPacket(TestConstants.sampleAddress,
				maxDataString.getBytes(), DataPacket.TYPE_DATA);
		byte[] asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);

		packet = new DataPacket(asByteArray);
		assertEquals(maxDataString, packet.getDataAsString());

		// modify data:
		packet.setDestination(null);
		try {
			packet.asByteArray();
			fail("Should have thrown an Exception");
		} catch (MessageConvertException e) {
		}

		packet.setDestination("ABC");
		try {
			asByteArray = packet.asByteArray();
			packet = new DataPacket(asByteArray);
			fail("Should have thrown an Exception");
		} catch (MessageConvertException e) {
		}

		String toLong = maxDataString + 'A';
		// now the data is too big..
		try {
			packet = new DataPacket(TestConstants.sampleAddress,
					toLong.getBytes(), DataPacket.TYPE_DATA);
			fail("Should have thrown an Exception");
		} catch (MessageToLongException e) {
		}
	}

	@Test
	public void testDataPacketFromTooBigBuffer() throws MessageConvertException {

		DataPacket packet = new DataPacket(TestConstants.sampleAddress,
				maxDataString.getBytes(), DataPacket.TYPE_DATA);
		byte[] asByteArray = packet.asByteArray();

//		byte[] copyOf = Arrays.copyOf(asByteArray, asByteArray.length + 1);
//		DataPacket packet2 = new DataPacket(copyOf);

//		assertEquals(packet, packet2);
	}

	@Test
	public void testDataPacketWithWrongAddressFormat() throws MessageConvertException {
		BluetoothMessage btmsg = new BluetoothMessage("bt-mtp://"
				+ getBTDummyDevice().getAddress(), "".getBytes(),
				DataPacket.TYPE_DATA);
		DataPacket dataPacket = new DataPacket(btmsg, btmsg.getType());
		byte[] bytes = dataPacket.asByteArray();

		DataPacket packet2 = new DataPacket(bytes);
		assertEquals(dataPacket, packet2);
	}

	@Test
	public void testDataPacketWithWrongType() {
		BluetoothMessage btmsg = new BluetoothMessage(getBTDummyDevice()
				.getAddress(), "".getBytes(), (byte) DataPacket.TYPE_DESCRIPTIONS.length);

		try {
			DataPacket dataPacket = new DataPacket(btmsg, btmsg.getType());
			byte[] bytes = dataPacket.asByteArray();
			fail("exception expected");
		} catch (MessageConvertException e) {
		}
	}
	
	@Test
	public void testDataPacketWithRightType() {
		BluetoothMessage btmsg = new BluetoothMessage(getBTDummyDevice()
				.getAddress(), "".getBytes(), (byte) (DataPacket.TYPE_DESCRIPTIONS.length-1));

		try {
			DataPacket dataPacket = new DataPacket(btmsg, btmsg.getType());
			byte[] bytes = dataPacket.asByteArray();
		} catch (MessageConvertException e) {
			fail("no exception expected");
		}
	}

	@Test
	public void testDataPacketCreationWithEmptyData() throws MessageConvertException {
		DataPacket packet = new DataPacket(TestConstants.sampleAddress, null,
				DataPacket.TYPE_BROADCAST);
		byte[] asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);

		packet = new DataPacket(asByteArray);
		assertEquals(0, packet.getData().length);
		assertEquals("", packet.getDataAsString());
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);

		packet = new DataPacket(TestConstants.sampleAddress, " ".getBytes(),
				DataPacket.TYPE_BROADCAST);
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);

		packet = new DataPacket(asByteArray);
		assertNotNull(packet.getData());
		assertEquals(" ", packet.getDataAsString());
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);
	}

	public static IBluetoothDevice getBTDummyDevice() {
		return new IBluetoothDevice() {
			@Override
			public void writeToParcel(Parcel dest, int flags) {
			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void setName(String name) {
			}

			@Override
			public void setAddress(String address) {
			}

			@Override
			public String getName() {
				return "testName";
			}

			@Override
			public String getAddress() {

				return TestConstants.sampleAddress;
			}

			@Override
			public IBluetoothSocket createRfcommSocketToServiceRecord(UUID uuid)
					throws IOException {
				return null;
			}
		};
	}
}
