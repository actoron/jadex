package jadex.android.bluetooth.message;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import jadex.android.bluetooth.CustomTestRunner;
import jadex.android.bluetooth.TestConstants;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothSocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.os.Parcel;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(CustomTestRunner.class)
public class DataPacketTest {
	private IBluetoothDevice dev;
	private BluetoothMessage btmsg;
	
	private int REPEAT_TIMES = 100;

	@Before
	public void setup() {
		dev = getBTDummyDevice();

		String testStringPart = "This is TestData stuff";

		StringBuilder testString = new StringBuilder();

		for (int i = 0; i < 40; i++) {
			testString.append(testStringPart);
		}
		btmsg = new BluetoothMessage(dev, testString.toString().getBytes(),
				DataPacket.TYPE_DATA);

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
	public void testCustomSerialisation() {
		long start = System.nanoTime(); // requires java 1.5
		DataPacket dataPacket = new DataPacket(btmsg, btmsg.getType());
		DataPacket dataPacket2 = null;
		
		for (int i = 0; i < REPEAT_TIMES; i++) {
			dataPacket.Src = TestConstants.sampleAddress;
			byte[] byteArr = dataPacket.asByteArray();
			dataPacket2 = new DataPacket(byteArr);
		}
		
		double elapsedTimeInSec = (System.nanoTime() - start) * 1.0e-9;
		System.out.println("Custom Byte Array Building took: "
				+ elapsedTimeInSec);
		assertEquals(dataPacket, dataPacket2);
	}
	
	@Test
	public void testDataPacketCreation() {
		DataPacket packet = new DataPacket(TestConstants.sampleAddress, "".getBytes(), DataPacket.TYPE_BROADCAST);
		byte[] asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);
		
		BluetoothMessage btMsg = new BluetoothMessage(TestConstants.sampleAddress, "test".getBytes(), DataPacket.TYPE_CONNECT_SYN);
		packet = new DataPacket(btMsg, btMsg.getType());
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);
		
		packet = new DataPacket(asByteArray);
		assertEquals("test", packet.getDataAsString());
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);
	}
	
	@Test
	public void testDataPacketCreationWithEmptyData() {
		DataPacket packet = new DataPacket(TestConstants.sampleAddress, null, DataPacket.TYPE_BROADCAST);
		byte[] asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);
		
		packet = new DataPacket(asByteArray);
		assertNull(packet.getData());
		assertEquals("", packet.getDataAsString());
		asByteArray = packet.asByteArray();
		assertNotNull(asByteArray);
	}

	
	private IBluetoothDevice getBTDummyDevice() {
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
