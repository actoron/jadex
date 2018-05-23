package jadex.android.bluetooth.routing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import jadex.android.bluetooth.CustomTestRunner;
import jadex.android.bluetooth.TestConstants;
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.message.MessageProtos.RoutingType;
import jadex.android.bluetooth.routing.IPacketRouter.RoutingEntriesChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Ignore;

import android.bluetooth.BluetoothAdapter;

import com.google.protobuf.InvalidProtocolBufferException;

@RunWith(CustomTestRunner.class)
public abstract class PacketRouterTest {

	protected static final String ownAddress = TestConstants.defaultAdapterAddress;
	private static final String ownAddress2 = TestConstants.defaultAdapterAddress2;
	protected static final String device1 = TestConstants.adapterAddress1;
	protected static final String device2 = TestConstants.adapterAddress2;
	protected static final String device3 = TestConstants.adapterAddress3;

	protected IPacketRouter packetRouter1;

	protected HashMap<String, Map<String, Queue<DataPacket>>> sentMessages;

	@Before
	public void setUp() {
		packetRouter1 = getPacketRouter(ownAddress);
		sentMessages = new HashMap<String, Map<String, Queue<DataPacket>>>();
		connectPacketRouters(packetRouter1, null);
		BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
		String address = defaultAdapter.getAddress();
		assertEquals(TestConstants.defaultAdapterAddress, address);
	}

	@Test
	public void simpleTests() {
		Set<String> reachableDeviceAddresses = packetRouter1
				.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());
		packetRouter1.setPacketSender(null);
		try {
			packetRouter1.addConnectedDevice(device1);
			packetRouter1.routePacket(new DataPacket(device1, "uhh".getBytes(),
					DataPacket.TYPE_DATA), packetRouter1.getOwnAddress());
			fail("Should throw exception when no sender is set");
		} catch (Exception e) {
		}

		packetRouter1.setOwnAddress("testAddress");
		assertEquals("testAddress", packetRouter1.getOwnAddress());
	}

	@Test
	public void testAddConnectedDevice() {
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
	public void testRemoveConnectedDevice() throws InterruptedException {
		packetRouter1.addConnectedDevice(device1);
		packetRouter1.removeConnectedDevice(device1);
		Set<String> reachableDeviceAddresses = packetRouter1
				.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());

		Set<String> connectedDeviceAddresses = packetRouter1
				.getConnectedDeviceAddresses();
		assertTrue(connectedDeviceAddresses.isEmpty());
	}
	
	@Test
	public void testRemoveReachableDevice() throws InterruptedException {
		IPacketRouter packetRouter2 = getPacketRouter(ownAddress2);
		connectPacketRouters(packetRouter1, packetRouter2);
		connectPacketRouters(packetRouter2, packetRouter1);
		
		packetRouter1.addConnectedDevice(device1);
		System.out.println("Waiting " + getBroadcastWaitTime() + "ms for Message Propagation...");
		Thread.sleep(getBroadcastWaitTime());
		assertTrue(packetRouter2.getReachableDeviceAddresses().contains(device1));

		// now remove device1 from router 1
		packetRouter1.removeConnectedDevice(device1);
		
		System.out.println("Waiting " + getBroadcastWaitTime() + "ms for Message Propagation...");
		Thread.sleep(getBroadcastWaitTime());
		
		// should be removed from router 2 now, too
		Set<String> reachableDeviceAddresses = packetRouter2
				.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());
		
		//re-add
		packetRouter1.addConnectedDevice(device1);
		System.out.println("Waiting " + getBroadcastWaitTime() + "ms for Message Propagation...");
		Thread.sleep(getBroadcastWaitTime());
		assertTrue(packetRouter2.getReachableDeviceAddresses().contains(device1));
		//disconnect routers:
//		packetRouter1.setPacketSender(null);
//		packetRouter2.setPacketSender(null);
//		Thread.sleep(getBroadcastWaitTime() * 2);
//		assertFalse(packetRouter2.getReachableDeviceAddresses().contains(device1));
	}

	@Test
	public void testUpdateRoutingInformation() {
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
	
	@Test
	public void testUpdateRoutingInformation_shouldNotContainSelf() {
		assertTrue(packetRouter1.getReachableDeviceAddresses().isEmpty());
		System.out.println(packetRouter1);
		packetRouter1.updateRoutingInformation(getSampleRoutingInformation_containingOwnDevice());
		
		Set<String> reachableDeviceAddresses = packetRouter1.getReachableDeviceAddresses();
		System.out.println(packetRouter1);
		assertFalse(reachableDeviceAddresses.contains(ownAddress));
	}

	private List<String> getDeviceList(RoutingInformation routingInformation) {
		List<RoutingTableEntry> entryList = routingInformation
				.getRoutingTable().getEntryList();
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
	public void testDoNotSendMessageToUnknownDevice() throws MessageConvertException {
		DataPacket dataPacket = new DataPacket(device2, "data1".getBytes(),
				DataPacket.TYPE_DATA);
		packetRouter1.routePacket(dataPacket, ownAddress);
		assertTrue(sentMessages.isEmpty());
	}

	@Test
	public void testSendMessageToConnectedDevice() throws MessageConvertException {
		DataPacket dataPacket = new DataPacket(device2, "data1".getBytes(),
				DataPacket.TYPE_DATA);
		dataPacket.setSource(packetRouter1.getOwnAddress());
		packetRouter1.addConnectedDevice(device2);
		packetRouter1.routePacket(dataPacket, packetRouter1.getOwnAddress());
		assertEquals(dataPacket, getSentPackages(ownAddress, device2).peek());
	}

	@Test
	public void testSendMessageToReachableDevice() throws MessageConvertException {
		RoutingInformation sampleRI = getSampleRoutingInformation();
		String sampleReachableDevice = getDeviceList(sampleRI).get(0);
		DataPacket dataPacket = new DataPacket(sampleReachableDevice,
				"data1".getBytes(), DataPacket.TYPE_DATA);
		packetRouter1.routePacket(dataPacket, packetRouter1.getOwnAddress());
		assertTrue(sentMessages.isEmpty());

		packetRouter1.updateRoutingInformation(sampleRI);
		packetRouter1.addConnectedDevice(sampleReachableDevice);
		packetRouter1.routePacket(dataPacket, packetRouter1.getOwnAddress());
		DataPacket sentPacket = getSentPackages(packetRouter1.getOwnAddress(),
				sampleReachableDevice).peek();
		assertTrue(sentPacket.getDataAsString().equals("data1"));
	}
	
	@Test
	public void testDevicesChangedListeners() throws MessageConvertException {
		RoutingInformation sampleRI = getSampleRoutingInformation();
		List<String> deviceList = getDeviceList(sampleRI);
		
		String sampleReachableDevice = null;
		
		for (String string : deviceList) {
			if (!TestConstants.sampleAddress.equals(string)) {
				sampleReachableDevice = string;
			}
		}
		
		
//		String sampleReachableDevice = getDeviceList(sampleRI).get(0);
		DataPacket dataPacket = new DataPacket(sampleReachableDevice,
				"data1".getBytes(), DataPacket.TYPE_DATA);
		
		
		final Set<String>[] reachableDeviceAddresses = new Set[1];
		final Set<String>[] connectedDeviceAddresses = new Set[1];
		
		packetRouter1.addRoutingEntriesChangedListener(new RoutingEntriesChangeListener() {

			@Override
			public void reachableDevicesChanged() {
				reachableDeviceAddresses[0] = packetRouter1.getReachableDeviceAddresses();
			}

			@Override
			public void connectedDevicesChanged() {
				connectedDeviceAddresses[0] = packetRouter1.getConnectedDeviceAddresses();
				
			}
		});
		
		packetRouter1.addConnectedDevice(TestConstants.sampleAddress);
		assertNull(reachableDeviceAddresses[0]);
		assertTrue(connectedDeviceAddresses[0].contains(TestConstants.sampleAddress));
		
//		packetRouter1.routePacket(dataPacket, packetRouter1.getOwnAddress());
		packetRouter1.updateRoutingInformation(sampleRI);
		assertTrue(reachableDeviceAddresses[0].size() > 0);
		assertTrue(reachableDeviceAddresses[0].contains(sampleReachableDevice));
//		packetRouter1.routePacket(dataPacket, packetRouter1.getOwnAddress());
//		DataPacket sentPacket = getSentPackages(packetRouter1.getOwnAddress(),
//				sampleReachableDevice).peek();
	}

	@Test
	public void test2CommunicatingPacketRouters() throws InterruptedException, MessageConvertException {
		final IPacketRouter packetRouter2 = getPacketRouter(ownAddress2);
		connectPacketRouters(packetRouter1, packetRouter2);
		connectPacketRouters(packetRouter2, packetRouter1);

		// packetRouter1.addConnectedDevice(ownAddress2);
		// packetRouter2.addConnectedDevice(ownAddress);

		// packetrouters should know each others device as connected now
		assertTrue(packetRouter1.getConnectedDeviceAddresses().contains(
				ownAddress2));
		assertEquals(1, packetRouter1.getConnectedDeviceAddresses().size());
		assertTrue(packetRouter2.getConnectedDeviceAddresses().contains(
				ownAddress));
		assertEquals(1, packetRouter2.getConnectedDeviceAddresses().size());
		// but musnt contain it as reachable
		assertEquals(0, packetRouter1.getReachableDeviceAddresses().size());
		assertEquals(0, packetRouter2.getReachableDeviceAddresses().size());

		// now we add another device to router2
		packetRouter2.addConnectedDevice(device1);
		assertTrue(packetRouter2.getConnectedDeviceAddresses()
				.contains(device1));
		System.out.println("Waiting " + getBroadcastWaitTime() + "ms for Message Propagation...");
		Thread.sleep(getBroadcastWaitTime());

		assertEquals(2, packetRouter2.getConnectedDeviceAddresses().size());
		assertEquals(0, packetRouter2.getReachableDeviceAddresses().size());
		// this changes should propagate to packet router 1

		assertEquals(1, packetRouter1.getConnectedDeviceAddresses().size());

		assertEquals(1, packetRouter1.getConnectedDeviceAddresses().size());
		assertEquals(1, packetRouter1.getReachableDeviceAddresses().size());
		assertTrue(packetRouter1.getReachableDeviceAddresses()
				.contains(device1));

		// now we try to address a packet to device1 and send it via router 1,
		// which is not directly connected.
		DataPacket dataPacket = new DataPacket(device1,
				"testData 1234".getBytes(), DataPacket.TYPE_DATA);
		dataPacket.setSource(TestConstants.sampleAddress);

		packetRouter1.routePacket(dataPacket, packetRouter1.getOwnAddress());

		DataPacket dataPacket2 = getSentPackages(packetRouter1.getOwnAddress(),
				packetRouter2.getOwnAddress()).peek();
		// packet should have been sent by router 1
		assertEquals("testData 1234", dataPacket2.getDataAsString());
		assertEquals(TestConstants.sampleAddress, dataPacket2.getSource());
		assertEquals(device1, dataPacket2.getDestination());

		// now route the package to target by router2:
		packetRouter2.routePacket(dataPacket2, ownAddress);
		Queue<DataPacket> sentPackages = getSentPackages(ownAddress2, device1);
		// dataPacket2 = sentMessages2.get(device1);
		dataPacket2 = sentPackages.peek();
		assertEquals("testData 1234", dataPacket2.getDataAsString());
		assertEquals(TestConstants.sampleAddress, dataPacket2.getSource());
		assertEquals(device1, dataPacket2.getDestination());
	}

	@Test
	public void test3CommunicatingPacketRouters() {
	}

	protected void connectPacketRouters(final IPacketRouter packetRouter1,
			final IPacketRouter packetRouter2) {
		packetRouter1.setPacketSender(new IPacketSender() {
			@Override
			public void sendMessageToConnectedDevice(DataPacket packet,
					String address) {
				if ((packetRouter2 != null)
						&& (address.equals(packetRouter2.getOwnAddress()))
						&& (packet.getType() == DataPacket.TYPE_ROUTING_INFORMATION)) {
					try {
						RoutingInformation ri = MessageProtos.RoutingInformation
								.parseFrom(packet.getData());
						// final List<String> deviceList = getDeviceList(ri);

						if (packetRouter2 != null) {
							packetRouter2.updateRoutingInformation(ri);
						}
					} catch (InvalidProtocolBufferException e) {
						throw new RuntimeException();
					}
				} else if (packet.getType() == DataPacket.TYPE_DATA) {
					putSentPacket(packetRouter1.getOwnAddress(), address,
							packet);
					// sentMessages2.put(address, packet);
				}
			}
		});
		if (packetRouter2 != null) {
			packetRouter1.addConnectedDevice(packetRouter2.getOwnAddress());
		}
	}

	private void putSentPacket(String source, String target, DataPacket packet) {
		Map<String, Queue<DataPacket>> sourceMap = sentMessages.get(source);
		if (sourceMap == null) {
			sourceMap = new HashMap<String, Queue<DataPacket>>();
			sentMessages.put(source, sourceMap);
		}

		Queue<DataPacket> targetPacketList = sourceMap.get(target);

		if (targetPacketList == null) {
			targetPacketList = new LinkedBlockingQueue<DataPacket>();
			sourceMap.put(target, targetPacketList);
		}

		targetPacketList.add(packet);
	}

	private Queue<DataPacket> getSentPackages(String source, String target) {
		Map<String, Queue<DataPacket>> sourceMap = sentMessages.get(source);
		if (sourceMap == null) {
			LinkedBlockingQueue<DataPacket> newQueue = new LinkedBlockingQueue<DataPacket>();
			HashMap<String, Queue<DataPacket>> newMap = new HashMap<String, Queue<DataPacket>>();
			newMap.put(target, newQueue);
			sentMessages.put(source, newMap);
			return newQueue;
		} else {
			return sourceMap.get(target);
		}
	}

	protected abstract int getBroadcastWaitTime();

	protected abstract IPacketRouter getPacketRouter(String ownAddress);

	protected abstract RoutingType getRouterRoutingType();

	protected abstract RoutingInformation getSampleRoutingInformation();

	protected abstract List<String> getSampleExpectedReachableDevices();

	protected abstract List<String> getSampleExpectedConnectedDevices();

	protected abstract RoutingInformation getSampleRoutingInformation_containingOwnDevice();

}
