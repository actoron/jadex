package jadex.android.bluetooth.routing;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import jadex.android.bluetooth.TestConstants;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.message.MessageProtos.RoutingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.protobuf.InvalidProtocolBufferException;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public abstract class PacketRouterTest {

	private static final String ownAddress = "OwnBluetoothAddress";
	private static final String ownAddress2 = "OwnBluetoothAddress2";
	protected static final String device1 = "device1Address";
	protected static final String device2 = "device2Address";
	protected static final String device3 = "device3Address";
	
	protected IPacketRouter packetRouter1;
	protected Map<String, DataPacket> sentMessages1;
	protected Map<String, DataPacket> sentMessages2;

	@Before
	public void setUp() {
		packetRouter1 = getPacketRouter(ownAddress);
		sentMessages1 = new HashMap<String, DataPacket>();
		sentMessages2 = new HashMap<String, DataPacket>();
		packetRouter1.setPacketSender(new IPacketSender() {
			@Override
			public void sendMessageToConnectedDevice(DataPacket packet,
					String address) {
				sentMessages1.put(address, packet);
			}
		});
	}

	@Test
	public void initTest() {
		Set<String> reachableDeviceAddresses = packetRouter1
				.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());
		packetRouter1.setPacketSender(null);
		try {
			packetRouter1.addConnectedDevice(device1);
			fail("Should throw exception when no sender is set");
		} catch (Exception e) {
		}
	}

	@Test
	public void testConnectedDevices() {
		packetRouter1.addConnectedDevice(device1);
		Set<String> reachableDeviceAddresses = packetRouter1
				.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());

		Set<String> connectedDeviceAddresses = packetRouter1
				.getConnectedDeviceAddresses();
		assertTrue(connectedDeviceAddresses.contains(device1));
		assertTrue(connectedDeviceAddresses.size() == 1);
		// Test if router double-adds devices:
		packetRouter1.addConnectedDevice(device1);
		assertTrue(connectedDeviceAddresses.contains(device1));
		assertTrue(connectedDeviceAddresses.size() == 1);
	}

	@Test
	public void testReachableDevices() {
		assertTrue(packetRouter1.getReachableDeviceAddresses().isEmpty());
		packetRouter1.updateRoutingInformation(getSampleRoutingInformation());
		Set<String> reachableDeviceAddresses = packetRouter1
				.getReachableDeviceAddresses();

		List<String> expectedReachableDeviceList = getSampleExpectedReachableDevices();

		for (String dev : expectedReachableDeviceList) {
			assertTrue(reachableDeviceAddresses.contains(dev));
		}

		assertTrue(reachableDeviceAddresses.size() == expectedReachableDeviceList
				.size());
		packetRouter1.addConnectedDevice(device1);
		reachableDeviceAddresses = packetRouter1.getReachableDeviceAddresses();
		assertFalse(reachableDeviceAddresses.contains(device1));
	}

	private List<String> getDeviceList(RoutingInformation routingInformation) {
		List<RoutingTableEntry> entryList = routingInformation.getRoutingTable().getEntryList();
		ArrayList<String> result = new ArrayList<String>(entryList.size());
		for (RoutingTableEntry routingTableEntry : entryList) {
			result.add(routingTableEntry.getDestination());
		}
		return result;
	}

	@Test
	public void negativeTestReachableDevices() {
		assertTrue(packetRouter1.getReachableDeviceAddresses().isEmpty());
		packetRouter1
				.updateRoutingInformation(getUnsupportedRoutingInformation());
		Set<String> reachableDeviceAddresses = packetRouter1
				.getReachableDeviceAddresses();
		assertFalse(reachableDeviceAddresses.contains(device2));
		assertFalse(reachableDeviceAddresses.contains(device3));
		assertTrue(reachableDeviceAddresses.size() == 0);
		packetRouter1.addConnectedDevice(device1);
		reachableDeviceAddresses = packetRouter1.getReachableDeviceAddresses();
		assertFalse(reachableDeviceAddresses.contains(device1));
	}

	private RoutingInformation getUnsupportedRoutingInformation() {
		Builder builder = RoutingInformation.newBuilder();
		builder.setRoutingTable(getSampleRoutingInformation().getRoutingTable());
		for (RoutingType type : RoutingType.values()) {
			if (type != getRouterRoutingType()) {
				builder.setType(type);
			}
		}
		return builder.build();
	}

	@Test
	public void testDoNotSendMessageToUnknownDevice() {
		DataPacket dataPacket = new DataPacket(device2, "data1".getBytes(),
				DataPacket.TYPE_DATA);
		packetRouter1.routePacket(dataPacket, ownAddress);
		assertTrue(sentMessages1.isEmpty());
	}

	@Test
	public void testSendMessageToConnectedDevice() {
		DataPacket dataPacket = new DataPacket(device2, "data1".getBytes(),
				DataPacket.TYPE_DATA);
		dataPacket.Src = TestConstants.sampleAddress;
		packetRouter1.addConnectedDevice(device2);
		packetRouter1.routePacket(dataPacket, ownAddress);
		assertTrue(sentMessages1.get(device2).equals(dataPacket));
	}

	@Test
	public void testSendMessageToReachableDevice() {
		RoutingInformation sampleRI = getSampleRoutingInformation();
		String sampleReachableDevice = getDeviceList(sampleRI)
				.get(0);
		DataPacket dataPacket = new DataPacket(sampleReachableDevice,
				"data1".getBytes(), DataPacket.TYPE_DATA);
		packetRouter1.updateRoutingInformation(sampleRI);
		packetRouter1.routePacket(dataPacket, ownAddress);
		assertTrue(sentMessages1.isEmpty());
		packetRouter1.addConnectedDevice(device1);
		packetRouter1.routePacket(dataPacket, ownAddress);
		DataPacket sentPacket = sentMessages1.get(device1);
		assertTrue(sentPacket.getDataAsString().equals("data1"));
	}

	@Test
	public void testCommunicatingPacketRouters() {
		final IPacketRouter packetRouter2 = getPacketRouter(ownAddress2);
		packetRouter2.setPacketSender(new IPacketSender() {

			@Override
			public void sendMessageToConnectedDevice(DataPacket packet,
					String address) {
				if ((address.equals(ownAddress))
						&& (packet.Type == DataPacket.TYPE_ROUTING_INFORMATION)) {
					try {
						RoutingInformation ri = MessageProtos.RoutingInformation.parseFrom(packet.getData());
						// final List<String> deviceList =  getDeviceList(ri);
						
						packetRouter1.updateRoutingInformation(ri);
					} catch (InvalidProtocolBufferException e) {
						throw new RuntimeException();
					}
				} else {
					sentMessages2.put(address, packet);
				}
			}
		});

		packetRouter1.setPacketSender(new IPacketSender() {
			@Override
			public void sendMessageToConnectedDevice(DataPacket packet,
					String address) {
				if ((address.equals(ownAddress2))
						&& (packet.Type == DataPacket.TYPE_ROUTING_INFORMATION)) {
						RoutingInformation ri;
						try {
							ri = MessageProtos.RoutingInformation.parseFrom(packet.getData());
							packetRouter2.updateRoutingInformation(ri);
						} catch (InvalidProtocolBufferException e) {
							throw new RuntimeException();
						}
				} else {
					sentMessages1.put(address, packet);
				}
			}
		});

		packetRouter1.addConnectedDevice(ownAddress2);
		packetRouter2.addConnectedDevice(ownAddress);

		// packetrouters should know each others device as connected now
		assertTrue(packetRouter1.getConnectedDeviceAddresses().contains(
				ownAddress2));
		assertTrue(packetRouter1.getConnectedDeviceAddresses().size() == 1);
		assertTrue(packetRouter2.getConnectedDeviceAddresses().contains(
				ownAddress));
		assertTrue(packetRouter2.getConnectedDeviceAddresses().size() == 1);
		// but musnt contain it as reachable
		assertTrue(packetRouter1.getReachableDeviceAddresses().size() == 0);
		assertTrue(packetRouter2.getReachableDeviceAddresses().size() == 0);

		// now we add another device to router2
		packetRouter2.addConnectedDevice(device1);
		assertTrue(packetRouter2.getConnectedDeviceAddresses()
				.contains(device1));
		assertTrue(packetRouter2.getConnectedDeviceAddresses().size() == 2);
		assertTrue(packetRouter2.getReachableDeviceAddresses().size() == 0);
		// this changes should propagate to packet router 1
		assertTrue(packetRouter1.getConnectedDeviceAddresses().size() == 1);
		assertTrue(packetRouter1.getReachableDeviceAddresses().contains(device1));
		assertTrue(packetRouter1.getReachableDeviceAddresses().size() == 1);
		
		
		// now we try to address a packet to device1 and send it via router 1, which is not directly connected.
		DataPacket dataPacket = new DataPacket(device1, "testData 1234".getBytes(), DataPacket.TYPE_DATA);
		dataPacket.Src = TestConstants.sampleAddress;
		packetRouter1.routePacket(dataPacket, ownAddress);
		DataPacket dataPacket2 = sentMessages1.get(ownAddress2);
		// packet should have been sent by router 1
		assertEquals("testData 1234", dataPacket2.getDataAsString());
		assertEquals(TestConstants.sampleAddress, dataPacket2.Src);
		assertEquals(device1, dataPacket2.Dest);
		
		// now route the package to target by router2:
		packetRouter2.routePacket(dataPacket2, ownAddress);
		dataPacket2 = sentMessages2.get(device1);
		assertEquals("testData 1234", dataPacket2.getDataAsString());
		assertEquals(TestConstants.sampleAddress, dataPacket2.Src);
		assertEquals(device1, dataPacket2.Dest);
	}

	protected abstract IPacketRouter getPacketRouter(String ownAddress);

	protected abstract RoutingType getRouterRoutingType();

	protected abstract RoutingInformation getSampleRoutingInformation();
	
	protected abstract List<String> getSampleExpectedReachableDevices();
	
	protected abstract List<String> getSampleExpectedConnectedDevices();

}
