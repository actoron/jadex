package jadex.android.bluetooth.routing;

import java.util.ArrayList;
import java.util.List;

import jadex.android.bluetooth.routing.IRoutingInformation.RoutingType;

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
	protected IRoutingInformation getSampleRoutingInformation() {
		return new IRoutingInformation() {

			@Override
			public RoutingType getRoutingType() {
				return getRouterRoutingType();
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
