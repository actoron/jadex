package jadex.android.bluetooth.routing;

import static org.junit.Assert.*;
import jadex.android.bluetooth.TestConstants;
import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder;
import jadex.android.bluetooth.message.MessageProtos.RoutingTable;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.message.MessageProtos.RoutingType;
import jadex.android.bluetooth.routing.dsdv.DsdvRouter;
import jadex.android.bluetooth.routing.dsdv.info.ConfigInfo;
import jadex.android.bluetooth.routing.dsdv.info.CurrentInfo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DsdvPacketRouterTest extends PacketRouterTest {

	@Test
	public void testDSDVStaleRoutingEntryDeletion() throws InterruptedException {
		IPacketRouter packetRouter2 = getPacketRouter(TestConstants.adapterAddress2);
		
		connectPacketRouters(packetRouter1, packetRouter2);
		connectPacketRouters(packetRouter2, packetRouter1);
		
		Thread.sleep(ConfigInfo.periodicRouteBroadcastIncremental*2);
		
		packetRouter1.setPacketSender(null);
		packetRouter2.setPacketSender(null);
		
		Thread.sleep(ConfigInfo.deleteRouteStaleRoute + 1000);
		assertTrue(packetRouter2.getReachableDeviceAddresses().isEmpty());
	}
	
	
	@Override
	protected IPacketRouter getPacketRouter(String ownAddress) {
		DsdvRouter dsdvRouter = new DsdvRouter();
		dsdvRouter.setOwnAddress(ownAddress);
		dsdvRouter.start();
		return dsdvRouter;
	}

	@Override
	protected RoutingType getRouterRoutingType() {
		return RoutingType.DSDV;
	}

	@Override
	protected RoutingInformation getSampleRoutingInformation() {
		Builder riBuilder = MessageProtos.RoutingInformation.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTable.Builder rtBuilder = MessageProtos.RoutingTable.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry.Builder entryBuilder = MessageProtos.RoutingTableEntry.newBuilder();
		
		
		entryBuilder.setNextHop(device2);
		entryBuilder.setNumHops(0);
		entryBuilder.setSeqNum(CurrentInfo.incrementOwnSeqNum());
		entryBuilder.setDestination(device2);
		RoutingTableEntry dev2 = entryBuilder.build();
		 
		entryBuilder.setNextHop(device2);
		entryBuilder.setNumHops(1);
		entryBuilder.setDestination(device3);
		RoutingTableEntry dev3 = entryBuilder.build();
		
		RoutingTable table = rtBuilder.addEntry(dev2).addEntry(dev3).build();
		
		RoutingInformation ri = riBuilder.setType(getRouterRoutingType()).setRoutingTable(table).setFromAddress(device2).build();
		
		return ri;
	}
	
	@Override
	protected RoutingInformation getSampleRoutingInformation_containingOwnDevice() {
		Builder riBuilder = MessageProtos.RoutingInformation.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTable.Builder rtBuilder = MessageProtos.RoutingTable.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry.Builder entryBuilder = MessageProtos.RoutingTableEntry.newBuilder();
		
		entryBuilder.setNextHop(device2);
		entryBuilder.setNumHops(0);
		entryBuilder.setSeqNum(CurrentInfo.incrementOwnSeqNum());
		entryBuilder.setDestination(device2);
		RoutingTableEntry dev2 = entryBuilder.build();
		 
		entryBuilder.setNextHop(ownAddress);
		entryBuilder.setNumHops(1);
		entryBuilder.setDestination(ownAddress);
		RoutingTableEntry dev3 = entryBuilder.build();
		
		RoutingTable table = rtBuilder.addEntry(dev2).addEntry(dev3).build();
		
		RoutingInformation ri = riBuilder.setType(getRouterRoutingType()).setRoutingTable(table).setFromAddress(device2).build();
		
		return ri;
	}
	
	@Override
	protected List<String> getSampleExpectedReachableDevices() {
		List<String> list = new ArrayList<String>();
		list.add(device3);
		return list;
	}
	
	@Override
	protected List<String> getSampleExpectedConnectedDevices() {
		List<String> list = new ArrayList<String>();
		list.add(device2);
		return list;
	}

	@Override
	protected int getBroadcastWaitTime() {
		return ConfigInfo.changedRouteDampening * 2;
	}
}
