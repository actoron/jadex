package jadex.android.bluetooth.routing;


import static org.junit.Assert.*;

import jadex.android.bluetooth.DataPacket;
import jadex.android.bluetooth.routing.IRoutingInformation.RoutingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FloodingPacketRouterTest {

	private static final String ownAddress = "OwnBluetoothAddress";
	private static final String device1 = "device1Address";
	private static final String device2 = "device2Address";
	private static final String device3 = "device3Address";
	
	private FloodingPacketRouter floodingPacketRouter;
	protected Map<String, DataPacket> sentMessages;

	@Before
	public void setUp() {
		floodingPacketRouter = new FloodingPacketRouter(ownAddress);
		sentMessages = new HashMap<String, DataPacket>();
		floodingPacketRouter.setPacketSender(new IMessageSender() {
			@Override
			public void sendMessageToConnectedDevice(DataPacket packet, String address) {
				sentMessages.put(address, packet);
			}
		});
	}
	
	@Test
	public void initTest() {
		Set<String> reachableDeviceAddresses = floodingPacketRouter.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());
		floodingPacketRouter.setPacketSender(null);
		try {
			floodingPacketRouter.addConnectedDevice(device1);
			fail("Should throw exception when no sender is set");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testConnectedDevices() {
		floodingPacketRouter.addConnectedDevice(device1);
		Set<String> reachableDeviceAddresses = floodingPacketRouter.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.isEmpty());
		
		Set<String> connectedDeviceAddresses = floodingPacketRouter.getConnectedDeviceAddresses();
		assertTrue(connectedDeviceAddresses.contains(device1));
		assertTrue(connectedDeviceAddresses.size() == 1);
		// Test if router double-adds devices:
		floodingPacketRouter.addConnectedDevice(device1);
		assertTrue(connectedDeviceAddresses.contains(device1));
		assertTrue(connectedDeviceAddresses.size() == 1);
	}
	
	@Test
	public void testReachableDevices() {
		assertTrue(floodingPacketRouter.getReachableDeviceAddresses().isEmpty());
		floodingPacketRouter.updateRoutingInformation(getCorrectRoutingInformation());
		Set<String> reachableDeviceAddresses = floodingPacketRouter.getReachableDeviceAddresses();
		assertTrue(reachableDeviceAddresses.contains(device2));
		assertTrue(reachableDeviceAddresses.contains(device3));
		assertTrue(reachableDeviceAddresses.size() == 2);
		floodingPacketRouter.addConnectedDevice(device1);
		reachableDeviceAddresses = floodingPacketRouter.getReachableDeviceAddresses();
		assertFalse(reachableDeviceAddresses.contains(device1));
	}
	
	public IRoutingInformation getCorrectRoutingInformation() {
		return new IRoutingInformation() {
			
			@Override
			public RoutingType getRoutingType() {
				return RoutingType.Flooding;
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

}
