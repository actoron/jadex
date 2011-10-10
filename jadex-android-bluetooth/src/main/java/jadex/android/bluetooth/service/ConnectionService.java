package jadex.android.bluetooth.service;

import jadex.android.bluetooth.BTP2PConnector;
import jadex.android.bluetooth.ConnectionManager;
import jadex.android.bluetooth.IConnection;
import jadex.android.bluetooth.device.BluetoothAdapterFactory;
import jadex.android.bluetooth.device.BluetoothDeviceFactory;
import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.domain.BluetoothMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

public class ConnectionService extends Service {

	public static final int SHOW_TOAST = 0;

	private IBluetoothAdapter btAdapter;

	private IConnectionCallback callback;

	public static Context CONTEXT = null;

	private Thread uiThread;

	private BTP2PConnector btp2pConnector;

	@Override
	public void onCreate() {
		super.onCreate();

		CONTEXT = this;
		btAdapter = BluetoothAdapterFactory.getBluetoothAdapter();
		if (btAdapter == null) {
			showToast("No BT Adapter found! This will cause exceptions.");
		}
		btp2pConnector = new BTP2PConnector(this, mHandler, btAdapter);

		btp2pConnector
				.addConnectionsListener(new ConnectionManager.ConnectionsListener() {

					@Override
					public void connectionAdded(String address,
							IConnection connection) {
						proximityDevicesChanged();
					}

					@Override
					public void connectionRemoved(String address) {
						proximityDevicesChanged();

					}
				});

		btp2pConnector.addMessageListener(null, new MessageListener() {

			@Override
			public void messageReceived(BluetoothMessage msg) {
				showToast(msg.getDataAsString());
			}
		});

		btp2pConnector
				.setProximityDeviceChangedListener(new BTP2PConnector.ProximityDeviceChangedListener() {

					@Override
					public void proximityDevicesChanged() {
						ConnectionService.this.proximityDevicesChanged();
					}
				});

		uiThread = Thread.currentThread();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_TOAST) {
				Toast.makeText(ConnectionService.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void showToast(String s) {
		if (Thread.currentThread() != uiThread) {
			mHandler.obtainMessage(ConnectionService.SHOW_TOAST, s)
					.sendToTarget();
		} else {
			Toast.makeText(ConnectionService.this, s, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onDestroy() {
		btp2pConnector.shutdown();
		super.onDestroy();
	}

	private final IBinder binder = new IConnectionServiceConnection.Stub() {

		@Override
		public void startBTServer() throws RemoteException {
			btp2pConnector.startBTServer();
		}

		@Override
		public void stopBTServer() throws RemoteException {

		};

		public void scanEnvironment() throws RemoteException {
			btp2pConnector.scanEnvironment(true);
		}

		@Override
		public void registerCallback(IConnectionCallback callback)
				throws RemoteException {
			ConnectionService.this.callback = callback;
		}

		@Override
		public IBluetoothDevice[] getUnbondedDevicesInRange()
				throws RemoteException {
			return btp2pConnector.unbondedDevicesInRange
					.toArray(new IBluetoothDevice[btp2pConnector.unbondedDevicesInRange
							.size()]);
		}

		@Override
		public IBluetoothDevice[] getBondedDevicesInRange()
				throws RemoteException {
			return btp2pConnector.bondedDevicesInRange
					.toArray(new IBluetoothDevice[btp2pConnector.bondedDevicesInRange
							.size()]);
		}

		@Override
		public void connectToDevice(final IBluetoothDevice dev)
				throws RemoteException {
			btp2pConnector.connect(dev);
		}

		@Override
		public void sendMessage(BluetoothMessage msg) throws RemoteException {
			btp2pConnector.sendMessage(msg);
		}

		@Override
		public void disconnectDevice(IBluetoothDevice dev)
				throws RemoteException {
			// IConnection connection = connections.get(dev);
			// if (connection != null) {
			// connection.close();
			// }
		}

		@Override
		public IBluetoothDevice[] getConnectedDevices() throws RemoteException {
			ArrayList<IBluetoothDevice> result = new ArrayList<IBluetoothDevice>();

			Set<Entry<String, IConnection>> entrySet = btp2pConnector.connections
					.entrySet();
			Iterator<Entry<String, IConnection>> it = entrySet.iterator();
			int i = 0;
			while (it.hasNext()) {
				Entry<String, IConnection> next = it.next();
				if (next.getValue().isAlive()) {
					result.add(BluetoothDeviceFactory
							.createBluetoothDevice(next.getKey()));
					i++;
				}
			}
			return result.toArray(new IBluetoothDevice[result.size()]);
		}

		@Override
		public IBluetoothDevice[] getReachableDevices() throws RemoteException {
			Set<String> reachableDevices = btp2pConnector.getReachableDevices();
			ArrayList<IBluetoothDevice> result = new ArrayList<IBluetoothDevice>(
					reachableDevices.size());

			for (String string : reachableDevices) {
				result.add(BluetoothDeviceFactory.createBluetoothDevice(string));
			}
			return result.toArray(new IBluetoothDevice[result.size()]);
		}

		@Override
		public void startAutoConnect() throws RemoteException {
			btp2pConnector.setAutoConnect(true);

		}

		@Override
		public void stopAutoConnect() throws RemoteException {
			btp2pConnector.setAutoConnect(false);
		}

	};

	protected void proximityDevicesChanged() {
		if (callback != null) {
			try {
				callback.deviceListChanged();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
