package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.domain.MessageProtos;
import jadex.android.bluetooth.domain.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.domain.MessageProtos.RoutingInformation.Builder;
import jadex.android.bluetooth.domain.MessageProtos.RoutingTable;
import jadex.android.bluetooth.domain.MessageProtos.RoutingTableEntry;
import jadex.android.bluetooth.domain.MessageProtos.RoutingType;

public class FloodingPacketRouterTest extends PacketRouterTest {

	@Override
	protected IMessageRouter getPacketRouter(String ownAddress) {
		return new FloodingPacketRouter(ownAddress);
	}

	@Override
	protected RoutingType getRouterRoutingType() {
		return RoutingType.Flooding;
	}

	@Override
	protected RoutingInformation getSampleRoutingInformation() {
		Builder riBuilder = MessageProtos.RoutingInformation.newBuilder();
		jadex.android.bluetooth.domain.MessageProtos.RoutingTable.Builder rtBuilder = MessageProtos.RoutingTable.newBuilder();
		jadex.android.bluetooth.domain.MessageProtos.RoutingTableEntry.Builder entryBuilder = MessageProtos.RoutingTableEntry.newBuilder();
		
		RoutingTableEntry dev2 = entryBuilder.setDevice(device2).build();
		RoutingTableEntry dev3 = entryBuilder.setDevice(device3).build();
		
		RoutingTable table = rtBuilder.addEntry(dev2).addEntry(dev3).build();
		
		RoutingInformation ri = riBuilder.setType(getRouterRoutingType()).setRoutingTable(table).build();
		
		return ri;
	}

}
