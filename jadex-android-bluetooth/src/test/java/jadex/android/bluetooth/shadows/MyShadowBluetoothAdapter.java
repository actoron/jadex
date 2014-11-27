package jadex.android.bluetooth.shadows;

import jadex.android.bluetooth.TestConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.robolectric.Robolectric;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowApplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;


@Implements(BluetoothAdapter.class)
public class MyShadowBluetoothAdapter {

	@Implementation
	public String getAddress() {
		return TestConstants.defaultAdapterAddress;
	}
	
    private Set<BluetoothDevice> bondedDevices = new HashSet<BluetoothDevice>();
    private boolean isDiscovering;

    @Implementation
    public static BluetoothAdapter getDefaultAdapter() {
        return (BluetoothAdapter) ((ShadowApplication) Robolectric.shadowOf_(Robolectric.application)).getBluetoothAdapter();
    }

    @Implementation
    public Set<BluetoothDevice> getBondedDevices() {
        return Collections.unmodifiableSet(bondedDevices);
    }

    public void setBondedDevices(Set<BluetoothDevice> bluetoothDevices) {
        bondedDevices = bluetoothDevices;
    }

    @Implementation
    public boolean startDiscovery() {
        isDiscovering = true;
        return true;
    }

    @Implementation
    public boolean cancelDiscovery() {
        isDiscovering = false;
        return true;
    }

    @Implementation
    public boolean isDiscovering() {
        return isDiscovering;
    }
}
