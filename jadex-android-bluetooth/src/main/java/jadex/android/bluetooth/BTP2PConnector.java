package jadex.android.bluetooth;

import jadex.android.bluetooth.BTServer.ConnectionEstablishedListener;
import jadex.android.bluetooth.ConnectionManager.ConnectionsListener;
import jadex.android.bluetooth.device.AndroidBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.domain.BluetoothMessage;
import jadex.android.bluetooth.domain.MessageProtos;
import jadex.android.bluetooth.domain.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.exceptions.AlreadyConnectedToDeviceException;
import jadex.android.bluetooth.exceptions.DiscoveryAlreadyRunningException;
import jadex.android.bluetooth.routing.FloodingPacketRouter;
import jadex.android.bluetooth.routing.IMessageRouter;
import jadex.android.bluetooth.routing.IMessageSender;
import jadex.android.bluetooth.service.Future;
import jadex.android.bluetooth.service.IFuture;
import jadex.android.bluetooth.service.IResultListener;
import jadex.android.bluetooth.service.MessageListener;
import jadex.android.bluetooth.util.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.google.protobuf.InvalidProtocolBufferException;


public class BTP2PConnector {

	public static final Integer NOT_CONNECTABLE = -1;

	public static final Integer MESSAGE_SENT = 1;

	protected static final boolean PING_DEBUG = false;

	public static final byte MAXHOPS = 4;

	private Timer discoveryTimer;

	private HashMap<String, Long> packetWatcher;

	public ConnectionManager connections;

	private IMessageRouter packetRouter;

	private BTServer btServer;

	private IBluetoothAdapter _btAdapter;

	private String ownAdress;

	private ProximityDeviceChangedListener listener;

	/**
	 * List of Unbonded devices in range, could be unconnectable
	 */
	public Set<IBluetoothDevice> unbondedDevicesInRange;

	/**
	 * List of bonded devices in range, which are connectable
	 */
	public Set<IBluetoothDevice> bondedDevicesInRange;

	/**
	 * List of all bonded devices
	 */
	private Set<IBluetoothDevice> bondedDevices;

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

	private Context _context;

	private Map<IBluetoothDevice, List<MessageListener>> listeners;

	private boolean autoConnect;

	private TimerTask discoveryTimerTask;

	private int autoDiscoveryPeriod = 10000;

	public BTP2PConnector(Context ctx, Handler mHandler,
			IBluetoothAdapter btAdapter) {
		_btAdapter = btAdapter;
		_context = ctx;

		packetWatcher = new HashMap<String, Long>();
		ownAdress = btAdapter.getAddress();

		listeners = new HashMap<IBluetoothDevice, List<MessageListener>>();

		unbondedDevicesInRange = new HashSet<IBluetoothDevice>();
		bondedDevicesInRange = new HashSet<IBluetoothDevice>();
		bondedDevices = new HashSet<IBluetoothDevice>();
		Set<IBluetoothDevice> bondedDevices2 = btAdapter.getBondedDevices();
		for (IBluetoothDevice btd : bondedDevices2) {
			bondedDevices.add(btd);
		}

		btServer = new BTServer(mHandler, btAdapter);
		btServer.setConnectionEstablishedListener(new ConnectionEstablishedListener() {
			@Override
			public void connectionEstablished(IConnection con) {
				con.addConnectionListener(defaultConnectionListener);
				connections.put(con.getRemoteDevice().getAddress(), con);
			}
		});

		packetRouter = new FloodingPacketRouter(ownAdress);
		packetRouter.setPacketSender(new IMessageSender() {
			@Override
			public void sendMessageToConnectedDevice(DataPacket pkt,
					String address) {
				IConnection con = connections.get(address);
				if (con.isAlive()) {
					try {
						con.write(pkt.asByteArray());
					} catch (IOException e) {
						// handle broke connection
					}
				}
			}
		});
		packetRouter
				.addReachableDevicesChangeListener(new IMessageRouter.ReachableDevicesChangeListener() {
					@Override
					public void reachableDevicesChanged() {
						notifyProximityDevicesChanged();
					}
				});

		connections = new ConnectionManager();
		connections.addConnectionsListener(new ConnectionsListener() {

			@Override
			public void connectionRemoved(String address) {
				packetRouter.removeConnectedDevice(address);
			}

			@Override
			public void connectionAdded(String address, IConnection connection) {
				packetRouter.addConnectedDevice(address);
			}
		});

		discoveryTimer = new Timer();

		_context.registerReceiver(bcReceiver, intentFilter1);
		_context.registerReceiver(bcReceiver, intentFilter2);
		_context.registerReceiver(bcReceiver, intentFilter3);
		_context.registerReceiver(bcReceiver, intentFilter4);
		_context.registerReceiver(bcReceiver, intentFilter5);
	}

	public void startBTServer() {
		btServer.listen();
	}

	public void shutdown() {
		connections.stopAll();
		_context.unregisterReceiver(bcReceiver);
	}

	// public IFuture sendInitialMessage(BluetoothMessage msg,
	// boolean useControlChannel) {
	// DataPacket dataPacket = new DataPacket(msg,
	// useControlChannel ? DataPacket.TYPE_PING : DataPacket.TYPE_DATA);
	// return sendMessage(dataPacket, true);
	// }

	public IFuture sendMessage(BluetoothMessage msg) {
		DataPacket dataPacket = new DataPacket(msg, DataPacket.TYPE_DATA);
		return sendMessage(dataPacket);
	}

	public IFuture sendMessage(final DataPacket msg) {
		return packetRouter.routePacket(msg, null);
	}

	public IFuture connect(IBluetoothDevice dev) {
		return connect(dev.getAddress());
	}

	public IFuture connect(String address) {
		DataPacket dataPacket = new DataPacket(address, null,
				DataPacket.TYPE_CONNECT_SYN);
		return sendInitialMessage(dataPacket);
	}

	public IFuture sendInitialMessage(BluetoothMessage msg) {
		return sendInitialMessage(new DataPacket(msg, msg.getType()));
	}

	public IFuture sendInitialMessage(final DataPacket msg) {
		final Future future = new Future();
		// establish new connection
		IBluetoothDevice destinationDevice = msg.getDestinationDevice();
		if (connections.containsKey(destinationDevice.getAddress())) {
			future.setException(new AlreadyConnectedToDeviceException(
					destinationDevice));
			return future;
		}
//		if (destinationDevice instanceof AndroidBluetoothDevice) {
//			nativeDevice = ((AndroidBluetoothDevice) destinationDevice)
//					.getDevice();
//		} else {
//			nativeDevice = _btAdapter.getRemoteDevice(destinationDevice
//					.getAddress());
//		}

		final ClientConnection newConnection = new ClientConnection(_btAdapter,
				destinationDevice);
		newConnection.addConnectionListener(defaultConnectionListener);
		newConnection.addConnectionListener(new ConnectionListener() {

			@Override
			public void messageReceived(DataPacket btMsg,
					IBluetoothDevice fromDevice, IConnection incomingConnection) {
			}

			@Override
			public void connectionStateChanged(IConnection connection) {
				synchronized (future) {
					if (connection.isAlive()) {
						connections.put(msg.getDestinationDevice().getAddress(),
								newConnection);
						try {
							connection.write(msg.asByteArray());
							if (!future.resultAvailable) {
								future.setResult(MESSAGE_SENT);
							}
							connection.removeConnectionListener(this);
						} catch (IOException e) {
							// try again?
							connection.close();
							connection.removeConnectionListener(this);
							if (!future.resultAvailable) {
								future.setResult(NOT_CONNECTABLE);
							}
						}
					} else {
						connection.removeConnectionListener(this);
						if (!future.resultAvailable) {
							future.setResult(NOT_CONNECTABLE);
						}
						boolean remove = bondedDevicesInRange.remove(connection
								.getRemoteDevice());
						if (remove) {
							// proximityDevicesChanged();
						}
						connection = null;
					}
				}
			}
		});
		newConnection.connect();
		return future;
	}

	private void handleDataPacketReceived(DataPacket packet) {
		String dataString = packet.getDataAsString();
		if ("foo".equals(dataString)) {
			DataPacket reply = new DataPacket(packet.Src, "bar".getBytes(),
					DataPacket.TYPE_DATA);
			reply.Src = ownAdress;
			sendMessage(reply);
		}

		BluetoothMessage bluetoothMessage = new BluetoothMessage(packet.Src,
				packet.data, packet.Type);
		List<MessageListener> list = listeners.get(bluetoothMessage
				.getRemoteAdress());
		if (list != null) {
			synchronized (list) {
				for (MessageListener btClientListener : list) {
					btClientListener.messageReceived(bluetoothMessage);
				}
			}
		}
		List<MessageListener> list2 = listeners.get(null);
		if (list2 != null) {
			synchronized (list2) {
				for (MessageListener btClientListener : list2) {
					btClientListener.messageReceived(bluetoothMessage);
				}
			}
		}
	}

	/**
	 * Handles all connection-relevant messages, such as SYN,ACK,PING,ROUTING
	 */
	private ConnectionListener defaultConnectionListener = new ConnectionListener() {
		@Override
		public void messageReceived(DataPacket pkt,
				IBluetoothDevice fromDevice, IConnection incomingConnection) {
			if (isPacketRepeated(pkt)) {
				return;
			}

			if (ownAdress.equals(pkt.Dest)) {
				// handle messages directed to us
				if (!(pkt.Type == DataPacket.TYPE_PING || pkt.Type == DataPacket.TYPE_PONG)
						|| PING_DEBUG) {
					String string = "[Received] " + pkt.toString();
					Helper.jLog(string);
					// showToast(string);
				}
				IConnection connection = connections.get(pkt.Src);
				if (connection == null) {
					connection = incomingConnection;
				}
				if (connection != null) {
					if (pkt.Type == DataPacket.TYPE_PING) {
						/**
						 * handle PING
						 */
						pkt.Dest = pkt.Src;
						pkt.Type = DataPacket.TYPE_PONG;
						pkt.Src = ownAdress;
						pkt.newPaketID();
						try {
							connection.write(pkt.asByteArray());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (pkt.Type == DataPacket.TYPE_PONG) {
						/**
						 * handle PONG
						 */
						Helper.jLog("Bonded device in Range: "
								+ pkt.getSourceDevice());
						bondedDevicesInRange.add(pkt.getSourceDevice());
						notifyProximityDevicesChanged();
					} else if (pkt.Type == DataPacket.TYPE_CONNECT_SYN) {
						/**
						 * handle SYN
						 */
						// proximityDevicesChanged();
						DataPacket ack = new DataPacket(pkt.getSourceDevice(),
								null, DataPacket.TYPE_CONNECT_ACK);
						sendMessage(ack);
					} else if (pkt.Type == DataPacket.TYPE_CONNECT_ACK) {
						/**
						 * handle ACK
						 */

					} else if (pkt.Type == DataPacket.TYPE_ROUTING_INFORMATION) {
						/**
						 * handle ROUTING INFORMATION
						 */
						try {
							RoutingInformation ri = MessageProtos.RoutingInformation.parseFrom(pkt.data);
							packetRouter.updateRoutingInformation(ri);
						} catch (InvalidProtocolBufferException e) {
							e.printStackTrace();
						}
					}
				}

				if (pkt.Type == DataPacket.TYPE_DATA) {
					handleDataPacketReceived(pkt);
				}
				notifyProximityDevicesChanged();

			} else if (pkt.Type == DataPacket.TYPE_BROADCAST) {
				handleDataPacketReceived(pkt);
				packetRouter.routePacket(pkt, fromDevice.getAddress());
			} else {
				// we are not target
				packetRouter.routePacket(pkt, fromDevice.getAddress());
			}
		}

		@Override
		public void connectionStateChanged(IConnection connection) {
			if (!connection.isAlive()) {
				connection.close();
				Helper.jLog("Connection lost: "
						+ connection.getRemoteDevice().getName());
				connections.remove(connection.getRemoteDevice().getAddress());
				packetRouter.removeConnectedDevice(connection.getRemoteDevice()
						.getAddress());
				connection.removeConnectionListener(this);
				connection = null;
			}
		}
	};

	protected synchronized boolean isPacketRepeated(DataPacket pkt) {
		for (Iterator<Long> iter = packetWatcher.values().iterator(); iter
				.hasNext();) {
			Long time = iter.next();
			if ((Calendar.getInstance().getTimeInMillis() - time) > 1000) {
				iter.remove();// avoids ConcurrentModificationException
			}
		}

		// Clean up memory
		// r.gc();

		if (packetWatcher.containsKey(pkt.pktId)) {
			return true;
		} else {
			packetWatcher.put(pkt.pktId, Calendar.getInstance()
					.getTimeInMillis());
			return false;
		}
	}

	protected void notifyProximityDevicesChanged() {
		if (listener != null) {
			listener.proximityDevicesChanged();
		}
	}

	private boolean scanning;

	public IFuture scanEnvironment(boolean discoverNewDevices) {
		final Future result = new Future();
		if (scanning) {
			result.setException(new DiscoveryAlreadyRunningException());
			return result;
		}
		scanning = true;

		final Thread scanKnownDevicesThread = new Thread(new Runnable() {
			public void run() {
				Helper.jLog("trying known devices...");
				final int last = bondedDevices.size();
				int i = 0;
				for (final IBluetoothDevice device : bondedDevices) {
					i++;
					// dont try already reachable or connected devices
					if (packetRouter.getReachableDeviceAddresses().contains(
							device.getAddress())
							|| connections.containsKey(device.getAddress())) {
						if (i == last) {
							scanning = false;
							Helper.jLog("finished pinging known devices");
							result.setResult(bondedDevicesInRange);
						}
						continue;
					}

					IFuture future = connect(device);

					future.addResultListener(new IResultListener() {
						@Override
						public void resultAvailable(Object messageResult) {
							if (messageResult != MESSAGE_SENT) {
								Helper.jLog("Bonded device not available: "
										+ device.getName());
								if (bondedDevicesInRange.contains(device)) {
									bondedDevicesInRange.remove(device);
									notifyProximityDevicesChanged();
								}
							}
						}

						@Override
						public void exceptionOccurred(Exception exception) {
							if (bondedDevicesInRange.contains(device)
									&& !(exception instanceof AlreadyConnectedToDeviceException)) {
								bondedDevicesInRange.remove(device);
								notifyProximityDevicesChanged();
							}
						}
					});

					// care about termination event
					if (i >= last) {
						future
								.addResultListener(new IResultListener() {
									@Override
									public void resultAvailable(Object result) {
										finish();
									}

									@Override
									public void exceptionOccurred(
											Exception exception) {
										finish();
									}

									private void finish() {
										scanning = false;
										Helper.jLog("finished pinging known devices");
										result.setResult(bondedDevicesInRange);
									}
								});
					}
				}
			}
		});

		if (discoverNewDevices) {
			new BroadcastIntentListenerAdapter(_context, intentFilter3) {
				@Override
				protected void onReceive(Context context, Intent intent) {
					// check bonded devices when Normal discovery is finished
					scanKnownDevicesThread.start();
				}
			};

			// scan new devices
			unbondedDevicesInRange.clear();
			if (!_btAdapter.isEnabled()) {
				new BroadcastIntentListenerAdapter(_context, intentFilter1,
						false) {
					@Override
					protected void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						if (BluetoothAdapter.ACTION_STATE_CHANGED
								.equals(action)) {
							if (_btAdapter.isEnabled()) {
								// showToast("starting Discovery");
								Helper.jLog("starting Discovery");
								_btAdapter.startDiscovery();
								unregister();
							}
						}
					}
				};
				Helper.jLog("enabling BT");
				_btAdapter.enable();
			} else {
				if (!_btAdapter.isDiscovering()) {
					Helper.jLog("starting Discovery");
					_btAdapter.startDiscovery();
				}
			}
		} else {
			// just scan known devices:
			scanKnownDevicesThread.start();
		}
		return result;
	}

	private BroadcastReceiver bcReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Helper.jLog("Discovery started");
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				Helper.jLog("Discovery finished");
				notifyProximityDevicesChanged();
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// found new devices
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Helper.jLog("Device found: " + device.getName());
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					unbondedDevicesInRange.add(new AndroidBluetoothDevice(
							device));
				}
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				Helper.jLog("bond state changed");
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				int bondState = intent.getIntExtra(
						BluetoothDevice.EXTRA_BOND_STATE,
						BluetoothDevice.BOND_NONE);
				if (bondState == BluetoothDevice.BOND_BONDED) {
					bondedDevices.add(new AndroidBluetoothDevice(device));
					unbondedDevicesInRange.remove(device);
					notifyProximityDevicesChanged();
				}
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				ownAdress = _btAdapter.getAddress();
			}
		}
	};

	public void addMessageListener(IBluetoothDevice remoteDevice,
			MessageListener l) {
		List<MessageListener> list = listeners.get(remoteDevice);
		if (list == null) {
			list = new ArrayList<MessageListener>();
			listeners.put(remoteDevice, list);
		}
		synchronized (list) {
			list.add(l);
		}
	}

	public void removeMessageListener(final IBluetoothDevice remoteDevice,
			final MessageListener l) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<MessageListener> list = listeners.get(remoteDevice);
				if (list != null) {
					synchronized (list) {
						list.remove(l);
					}
				}
			}
		}).start();
	}

	public void addConnectionsListener(ConnectionsListener connectionsListener) {
		connections.addConnectionsListener(connectionsListener);
	}

	public void removeConnectionsListener(ConnectionsListener l) {
		connections.removeConnectionsListener(l);
	}

	public void setProximityDeviceChangedListener(
			ProximityDeviceChangedListener l) {
		listener = l;
	}

	public Set<String> getReachableDevices() {
		return packetRouter.getReachableDeviceAddresses();
	}
	
	public void setAutoDiscoveryPeriod(int autoDiscoveryPeriod) {
		this.autoDiscoveryPeriod = autoDiscoveryPeriod;
	}

	public int getAutoDiscoveryPeriod() {
		return autoDiscoveryPeriod;
	}

	public synchronized void setAutoConnect(boolean b) {
		if (b != autoConnect) {
			autoConnect = b;
			if (autoConnect) {
				startBTServer();
				discoveryTimerTask = new TimerTask() {

					@Override
					public void run() {
						Helper.jLog("Autoconnect Task Active...");
						IFuture result = scanEnvironment(false);
						result.addResultListener(new IResultListener() {
							@Override
							public void resultAvailable(Object result) {
								Set<IBluetoothDevice> inRange = (Set<IBluetoothDevice>) result;
							}

							@Override
							public void exceptionOccurred(Exception exception) {
								setAutoDiscoveryPeriod((int) (getAutoDiscoveryPeriod() * 1.5));
								Helper.jLog("Previous scan not yet finished, increased period to "
										+ getAutoDiscoveryPeriod());
								setAutoConnect(false);
								setAutoConnect(true);
							}
						});
					}
				};

				discoveryTimer.schedule(discoveryTimerTask,
						getAutoDiscoveryPeriod() / 2, getAutoDiscoveryPeriod());
			} else {
				discoveryTimerTask.cancel();
				Helper.jLog("Autoconnect Task Cancelled.");
			}
		}
	}

	public interface ProximityDeviceChangedListener {
		public void proximityDevicesChanged();
	}

}