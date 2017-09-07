package jadex.android.bluetooth.service;

import jadex.android.bluetooth.connection.BTP2PConnector;
import jadex.android.bluetooth.connection.ConnectionManager;
import jadex.android.bluetooth.connection.IBluetoothStateInformer;
import jadex.android.bluetooth.connection.IBluetoothStateListener;
import jadex.android.bluetooth.connection.IConnection;
import jadex.android.bluetooth.device.AndroidBluetoothAdapterWrapper;
import jadex.android.bluetooth.device.AndroidBluetoothDeviceWrapper;
import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothAdapter.BluetoothState;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothDevice.BluetoothBondState;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.util.Helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Android Service for Applications that want to use the Bluetooth Connection
 * Framework
 * 
 * @author Julian Kalinowski
 * 
 */
public class ConnectionService extends Service implements
		IBluetoothStateInformer {

	private IBluetoothAdapter btAdapter;

	private IBTP2PMessageCallback msgCallback;

	private IBTP2PAwarenessInfoCallback awarenessCallback;

//	public static Context CONTEXT = null;

	private Thread uiThread;

	private BTP2PConnector btp2pConnector;

	private List<IBluetoothStateListener> stateListeners;

	// intent filters
	private IntentFilter intentFilter1 = new IntentFilter(
			BluetoothAdapter.ACTION_STATE_CHANGED);
	private IntentFilter intentFilter2 = new IntentFilter(
			BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	private IntentFilter intentFilter3 = new IntentFilter(
			BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	private IntentFilter intentFilter4 = new IntentFilter(
			BluetoothDevice.ACTION_FOUND);
	private IntentFilter intentFilter5 = new IntentFilter(
			BluetoothDevice.ACTION_BOND_STATE_CHANGED);

	private boolean receiverRegistered;

	@Override
	public void onCreate() {
		super.onCreate();

//		CONTEXT = this;
		btAdapter = Helper.getBluetoothAdapterFactory()
				.getDefaultBluetoothAdapter();
		if (btAdapter == null) {
			Log.e(Helper.LOG_TAG,
					"No BT Adapter found! This will cause exceptions.");
		}
		btp2pConnector = new BTP2PConnector(this, btAdapter);

		stateListeners = new CopyOnWriteArrayList<IBluetoothStateListener>();
		addBluetoothStateListener(btp2pConnector);

		uiThread = Thread.currentThread();

		registerListeners();
	}

	private void registerListeners() {
		btp2pConnector
				.addConnectionsListener(new ConnectionManager.ConnectionsListener() {

					@Override
					public void connectionAdded(String address,
							IConnection connection) {
						// knownDevicesChanged();
					}

					@Override
					public void connectionRemoved(String address) {
						// knownDevicesChanged();
					}
				});

		btp2pConnector.addMessageListener(null,
				new BTP2PConnector.MessageListener() {

					@Override
					public synchronized void messageReceived(
							BluetoothMessage msg) {
						try {
							if (msg.getType() == DataPacket.TYPE_AWARENESS_INFO) {
								if (awarenessCallback != null) {
									awarenessCallback.awarenessInfoReceived(msg
											.getData());
								} else {
									Log.w(Helper.LOG_TAG, "Received awareness info but no callback registered!");
								}
							} else if (msgCallback != null) {
								// showToast(msg.getDataAsString());
								msgCallback.messageReceived(msg.getRemoteAddress(), msg.getData());
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				});

		btp2pConnector
				.setKnownDevicesChangedListener(new BTP2PConnector.KnownDevicesChangedListener() {

					@Override
					public void knownDevicesChanged() {
						ConnectionService.this.knownDevicesChanged();
					}
				});

		this.registerReceiver(bcReceiver, intentFilter1);
		this.registerReceiver(bcReceiver, intentFilter2);
		this.registerReceiver(bcReceiver, intentFilter3);
		this.registerReceiver(bcReceiver, intentFilter4);
		this.registerReceiver(bcReceiver, intentFilter5);
		this.receiverRegistered = true;
	}

	// private Handler mHandler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// if (msg.what == SHOW_TOAST) {
	// Toast.makeText(ConnectionService.this, msg.obj.toString(),
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// };

	// private void showToast(String s) {
	// if (Thread.currentThread() != uiThread) {
	// mHandler.obtainMessage(ConnectionService.SHOW_TOAST, s)
	// .sendToTarget();
	// } else {
	// Toast.makeText(ConnectionService.this, s, Toast.LENGTH_SHORT)
	// .show();
	// }
	// }

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onDestroy() {
		btp2pConnector.shutdown();
		synchronized (this) {
			if (this.receiverRegistered) {
				this.unregisterReceiver(bcReceiver);
				receiverRegistered = false;
			} else {
				Log.e(Helper.LOG_TAG,
						"(ConnectionService) Didn't unregister BroadcastIntentReceiver: was not registered");
			}
		}
		super.onDestroy();
	}

	private final IBinder binder = new IConnectionServiceConnection.Stub() {

		@Override
		public void startBTServer() throws RemoteException {
			btp2pConnector.startBTServer();
		}

		@Override
		public void stopBTServer() throws RemoteException {
			onDestroy();
		};

		public void scanEnvironment() throws RemoteException {
			btp2pConnector.scanEnvironment(true);
		}

		@Override
		public void registerAwarenessInfoCallback(
				IBTP2PAwarenessInfoCallback callback) throws RemoteException {
			ConnectionService.this.awarenessCallback = callback;
		}

		@Override
		public void registerMessageCallback(IBTP2PMessageCallback callback)
				throws RemoteException {
			ConnectionService.this.msgCallback = callback;
		}

		@Override
		public IBluetoothDevice[] getUnbondedDevicesInRange()
				throws RemoteException {
			return btp2pConnector.getUnbondedDevicesInRange().toArray(
					new IBluetoothDevice[btp2pConnector
							.getUnbondedDevicesInRange().size()]);
		}

		@Override
		public IBluetoothDevice[] getBondedDevicesInRange()
				throws RemoteException {
			return btp2pConnector.getBondedDevicesInRange().toArray(
					new IBluetoothDevice[btp2pConnector
							.getBondedDevicesInRange().size()]);
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

			Set<Entry<String, IConnection>> entrySet = btp2pConnector
					.getConnections().entrySet();
			Iterator<Entry<String, IConnection>> it = entrySet.iterator();
			int i = 0;
			while (it.hasNext()) {
				Entry<String, IConnection> next = it.next();
				if (next.getValue().isAlive()) {
					result.add(Helper.getBluetoothDeviceFactory()
							.createBluetoothDevice(next.getKey()));
					i++;
				}
			}
			return result.toArray(new IBluetoothDevice[result.size()]);
		}

		@Override
		public IBluetoothDevice[] getReachableDevices() throws RemoteException {
			// Set<String> reachableDevices =
			// btp2pConnector.getReachableDevices();
			// ArrayList<IBluetoothDevice> result = new
			// ArrayList<IBluetoothDevice>(
			// reachableDevices.size());
			//
			// for (String string : reachableDevices) {
			// result.add(Helper.getBluetoothDeviceFactory().createBluetoothDevice(string));
			// }
			// return result.toArray(new IBluetoothDevice[result.size()]);
			return btp2pConnector.getKnownDevices();
		}

		@Override
		public void startAutoConnect() throws RemoteException {
			btp2pConnector.setAutoConnect(true);

		}

		@Override
		public void stopAutoConnect() throws RemoteException {
			btp2pConnector.setAutoConnect(false);
		}

		@Override
		public String getBTAddress() throws RemoteException {
			return btAdapter.getAddress();
		}
	};

	private BroadcastReceiver bcReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				notifyBluetoothStateChanged(BluetoothState.discovery_started,
						null);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				notifyBluetoothStateChanged(BluetoothState.discovery_finished,
						null);
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// found new devices
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				notifyBluetoothDeviceFound(new AndroidBluetoothDeviceWrapper(
						device));
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int bondState = intent.getIntExtra(
						BluetoothDevice.EXTRA_BOND_STATE,
						BluetoothDevice.BOND_NONE);
				int prevBondState = intent.getIntExtra(
						BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
						BluetoothDevice.BOND_NONE);
				BluetoothBondState newState = AndroidBluetoothDeviceWrapper
						.convertFromAndroidBondState(bondState);
				BluetoothBondState oldState = AndroidBluetoothDeviceWrapper
						.convertFromAndroidBondState(prevBondState);
				notifyBluetoothDeviceBondStateChanged(
						new AndroidBluetoothDeviceWrapper(device), newState,
						oldState);
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.STATE_OFF);
				int prevState = intent.getIntExtra(
						BluetoothAdapter.EXTRA_PREVIOUS_STATE,
						BluetoothAdapter.STATE_OFF);
				BluetoothState newState = AndroidBluetoothAdapterWrapper
						.convertFromAndroidAdapterState(state);
				BluetoothState oldState = AndroidBluetoothAdapterWrapper
						.convertFromAndroidAdapterState(prevState);
				notifyBluetoothStateChanged(newState, oldState);
			}
		}
	};

	public void addBluetoothStateListener(IBluetoothStateListener l) {
		stateListeners.add(l);
	}

	public boolean removeBluetoothStateListener(IBluetoothStateListener l) {
		return stateListeners.remove(l);
	}

	protected void notifyBluetoothDeviceBondStateChanged(
			AndroidBluetoothDeviceWrapper device, BluetoothBondState newState,
			BluetoothBondState oldState) {
		for (IBluetoothStateListener l : stateListeners) {
			l.bluetoothDeviceBondStateChanged(device, newState, oldState);
		}
	}

	protected void notifyBluetoothDeviceFound(
			AndroidBluetoothDeviceWrapper device) {
		for (IBluetoothStateListener l : stateListeners) {
			l.bluetoothDeviceFound(device);
		}
	}

	protected void notifyBluetoothStateChanged(BluetoothState newState,
			BluetoothState oldState) {
		for (IBluetoothStateListener l : stateListeners) {
			l.bluetoothStateChanged(newState, oldState);
		}
	}

	protected void knownDevicesChanged() {
		if (awarenessCallback != null) {
			try {
				// msgCallback.deviceListChanged();
				awarenessCallback.knownDevicesChanged(btp2pConnector
						.getKnownDevices());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
