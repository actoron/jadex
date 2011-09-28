package jadex.android.bluetooth.routing;

import java.util.List;

import android.bluetooth.BluetoothDevice;

public interface IRoutingInformation {
	
	public enum RoutingType {
		Flooding, DSDV, DSR
	}

	RoutingType getRoutingType();
	
	List<String> getReachableDeviceList();
}
