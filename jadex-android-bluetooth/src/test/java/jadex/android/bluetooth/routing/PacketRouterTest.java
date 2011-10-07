package jadex.android.bluetooth.routing;

import static org.junit.Assert.*;

import jadex.android.bluetooth.DataPacket;
import jadex.android.bluetooth.domain.MessageProtos;
import jadex.android.bluetooth.domain.MessageProtos.DeviceList;
import jadex.android.bluetooth.routing.IRoutingInformation.RoutingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.bytecode.ByteArray;

import junit.framework.Assert;

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

	protected IMessageRouter packetRouter;
	protected Map<String, DataPacket> sentMessages;

	@Before
	public void setUp() {
		packetRouter = getPacketRouter(ownAddress);
		sentMessages = new HashMap<String, DataPacket>();
		packetRouter.setPacketSender(new IMessageSender() {
			@Override
			public void sendMessageToConnectedDevice(DataPacket packet,
					String address) {
				sentMessages.put(address, packet);
			}
		});
	}

	@Test
	public void initTest() {
		Set<String> reachableDeviceAddresses = packetRouter
				.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());
		packetRouter.setPacketSender(null);
		try {
			packetRouter.addConnectedDevice(device1);
			fail("Should throw exception when no sender is set");
		} catch (Exception e) {
		}
	}

	@Test
	public void testConnectedDevices() {
		packetRouter.addConnectedDevice(device1);
		Set<String> reachableDeviceAddresses = packetRouter
				.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());

		Set<String> connectedDeviceAddresses = packetRouter
				.getConnectedDeviceAddresses();
		assertTrue(connectedDeviceAddresses.contains(device1));
		assertTrue(connectedDeviceAddresses.size() == 1);
		// Test if router double-adds devices:
		packetRouter.addConnectedDevice(device1);
		assertTrue(connectedDeviceAddresses.contains(device1));
		assertTrue(connectedDeviceAddresses.size() == 1);
	}

	@Test
	public void testReachableDevices() {
		assertTrue(packetRouter.getReachableDeviceAddresses().isEmpty());
		packetRouter.updateRoutingInformation(getSampleRoutingInformation());
		Set<String> reachableDeviceAddresses = packetRouter
				.getReachableDeviceAddresses();

		List<String> expectedReachableDeviceList = getSampleRoutingInformation()
				.getReachableDeviceList();

		for (String dev : expectedReachableDeviceList) {
			assertTrue(reachableDeviceAddresses.contains(dev));
		}

		assertTrue(reachableDeviceAddresses.size() == expectedReachableDeviceList
				.size());
		packetRouter.addConnectedDevice(device1);
		reachableDeviceAddresses = packetRouter.getReachableDeviceAddresses();
		assertFalse(reachableDeviceAddresses.contains(device1));
	}

	@Test
	public void negativeTestReachableDevices() {
		assertTrue(packetRouter.getReachableDeviceAddresses().isEmpty());
		packetRouter
				.updateRoutingInformation(getUnsupportedRoutingInformation());
		Set<String> reachableDeviceAddresses = packetRouter
				.getReachableDeviceAddresses();
		assertFalse(reachableDeviceAddresses.contains(device2));
		assertFalse(reachableDeviceAddresses.contains(device3));
		assertTrue(reachableDeviceAddresses.size() == 0);
		packetRouter.addConnectedDevice(device1);
		reachableDeviceAddresses = packetRouter.getReachableDeviceAddresses();
		assertFalse(reachableDeviceAddresses.contains(device1));
	}

	private IRoutingInformation getUnsupportedRoutingInformation() {
		return new IRoutingInformation() {

			@Override
			public RoutingType getRoutingType() {
				for (RoutingType type : RoutingType.values()) {
					if (type != getRouterRoutingType()) {
						return type;
					}
				}
				throw new RuntimeException();
			}

			@Override
			public List<String> getReachableDeviceList() {
				ArrayList<String> list = new ArrayList<String>();
				list.add(device2);
				list.add(device3);
				return list;
			}
		};
	}

	@Test
	public void testDoNotSendMessageToUnknownDevice() {
		DataPacket dataPacket = new DataPacket(device2, "data1".getBytes(),
				DataPacket.TYPE_DATA);
		packetRouter.routePacket(dataPacket, ownAddress);
		assertTrue(sentMessages.isEmpty());
	}

	@Test
	public void testSendMessageToConnectedDevice() {
		DataPacket dataPacket = new DataPacket(device2, "data1".getBytes(),
				DataPacket.TYPE_DATA);
		packetRouter.addConnectedDevice(device2);
		packetRouter.routePacket(dataPacket, ownAddress);
		assertTrue(sentMessages.get(device2).equals(dataPacket));
	}

	@Test
	public void testSendMessageToReachableDevice() {
		IRoutingInformation sampleRI = getSampleRoutingInformation();
		String sampleReachableDevice = sampleRI.getReachableDeviceList().get(0);
		DataPacket dataPacket = new DataPacket(sampleReachableDevice,
				"data1".getBytes(), DataPacket.TYPE_DATA);
		packetRouter.updateRoutingInformation(sampleRI);
		packetRouter.routePacket(dataPacket, ownAddress);
		assertTrue(sentMessages.isEmpty());
		packetRouter.addConnectedDevice(device1);
		packetRouter.routePacket(dataPacket, ownAddress);
		DataPacket sentPacket = sentMessages.get(device1);
		assertTrue(sentPacket.getDataAsString().equals("data1"));
	}

	@Test
	public void testCommunicatingPacketRouters() {
		final IMessageRouter packetRouter2 = getPacketRouter(ownAddress2);
		packetRouter2.setPacketSender(new IMessageSender() {

			@Override
			public void sendMessageToConnectedDevice(DataPacket packet,
					String address) {
				if ((address.equals(ownAddress))
						&& (packet.Type == DataPacket.TYPE_ROUTING_INFORMATION)) {
					try {
						final DeviceList deviceList = MessageProtos.DeviceList
								.parseFrom(packet.data);
						packetRouter
								.updateRoutingInformation(new IRoutingInformation() {

									@Override
									public RoutingType getRoutingType() {
										return getRouterRoutingType();
									}

									@Override
									public List<String> getReachableDeviceList() {
										return deviceList.getDeviceList();
									}
								});
					} catch (InvalidProtocolBufferException e) {
						throw new RuntimeException();
					}
				}
			}
		});

		packetRouter.setPacketSender(new IMessageSender() {

			@Override
			public void sendMessageToConnectedDevice(DataPacket packet,
					String address) {
				if ((address.equals(ownAddress2))
						&& (packet.Type == DataPacket.TYPE_ROUTING_INFORMATION)) {
					try {
						final DeviceList deviceList = MessageProtos.DeviceList
								.parseFrom(packet.data);
						packetRouter2
								.updateRoutingInformation(new IRoutingInformation() {

									@Override
									public RoutingType getRoutingType() {
										return getRouterRoutingType();
									}

									@Override
									public List<String> getReachableDeviceList() {
										return deviceList.getDeviceList();
									}
								});
					} catch (InvalidProtocolBufferException e) {
						throw new RuntimeException();
					}
				}
			}
		});

		packetRouter.addConnectedDevice(ownAddress2);
		packetRouter2.addConnectedDevice(ownAddress);

		// packetrouters should know each others device as connected now
		assertTrue(packetRouter.getConnectedDeviceAddresses().contains(ownAddress2));
		assertTrue(packetRouter.getConnectedDeviceAddresses().size() == 1);
		assertTrue(packetRouter2.getConnectedDeviceAddresses().contains(ownAddress));
		assertTrue(packetRouter2.getConnectedDeviceAddresses().size() == 1);
		// but musnt contain it as reachable
		assertTrue(packetRouter.getReachableDeviceAddresses().size() == 0);
		assertTrue(packetRouter2.getReachableDeviceAddresses().size() == 0);

		// now we add another device to router2
		packetRouter2.addConnectedDevice(device1);
		assertTrue(packetRouter2.getConnectedDeviceAddresses().contains(device1));
		assertTrue(packetRouter2.getConnectedDeviceAddresses().size() == 2);
		assertTrue(packetRouter2.getReachableDeviceAddresses().size() == 0);
		// this changes should propagate to packet router 1
		assertTrue(packetRouter.getConnectedDeviceAddresses().size() == 1);
		assertTrue(packetRouter.getReachableDeviceAddresses().contains(device1));
		assertTrue(packetRouter.getReachableDeviceAddresses().size() == 1);
		
	}

	protected abstract IMessageRouter getPacketRouter(String ownAddress);

	protected abstract RoutingType getRouterRoutingType();

	protected abstract IRoutingInformation getSampleRoutingInformation();

}
