package jadex.android.bluetooth.routing;

import java.util.ArrayList;
import java.util.List;

import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation.Builder;
import jadex.android.bluetooth.message.MessageProtos.RoutingTable;
import jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.message.MessageProtos.RoutingType;

public class FloodingPacketRouterTest extends PacketRouterTest {

	@Override
	protected IPacketRouter getPacketRouter(String ownAddress) {
		FloodingPacketRouter floodingPacketRouter = new FloodingPacketRouter();
		floodingPacketRouter.setOwnAddress(ownAddress);
		floodingPacketRouter.start();
		return floodingPacketRouter;
	}

	@Override
	protected RoutingType getRouterRoutingType() {
		return RoutingType.Flooding;
	}

	@Override
	protected RoutingInformation getSampleRoutingInformation() {
		Builder riBuilder = MessageProtos.RoutingInformation.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTable.Builder rtBuilder = MessageProtos.RoutingTable.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry.Builder entryBuilder = MessageProtos.RoutingTableEntry.newBuilder();
		
		RoutingTableEntry dev2 = entryBuilder.setDestination(device2).build();
		RoutingTableEntry dev3 = entryBuilder.setDestination(device3).build();
		
		RoutingTable table = rtBuilder.addEntry(dev2).addEntry(dev3).build();
		
		RoutingInformation ri = riBuilder.setType(getRouterRoutingType()).setRoutingTable(table).build();
		
		return ri;
	}
	
	@Override
	protected RoutingInformation getSampleRoutingInformation_containingOwnDevice() {
		Builder riBuilder = MessageProtos.RoutingInformation.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTable.Builder rtBuilder = MessageProtos.RoutingTable.newBuilder();
		jadex.android.bluetooth.message.MessageProtos.RoutingTableEntry.Builder entryBuilder = MessageProtos.RoutingTableEntry.newBuilder();
		
		RoutingTableEntry dev2 = entryBuilder.setDestination(ownAddress).build();
		RoutingTableEntry dev3 = entryBuilder.setDestination(device3).build();
		
		RoutingTable table = rtBuilder.addEntry(dev2).addEntry(dev3).build();
		
		RoutingInformation ri = riBuilder.setType(getRouterRoutingType()).setRoutingTable(table).build();
		
		return ri;
	}

	@Override
	protected List<String> getSampleExpectedReachableDevices() {
		List<String> list = new ArrayList<String>();
		list.add(device3);
		list.add(device2);
		return list;
	}
	
	@Override
	protected List<String> getSampleExpectedConnectedDevices() {
		List<String> list = new ArrayList<String>();
		return list;
	}

	@Override
	protected int getBroadcastWaitTime() {
		return 0;
	}


	
	
}
