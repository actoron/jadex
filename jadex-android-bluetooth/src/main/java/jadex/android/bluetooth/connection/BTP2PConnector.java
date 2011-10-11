package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.connection.BTServer.ConnectionEstablishedListener;
import jadex.android.bluetooth.connection.ConnectionManager.ConnectionsListener;
import jadex.android.bluetooth.device.AndroidBluetoothDeviceWrapper;
import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothAdapter.BluetoothState;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothDevice.BluetoothBondState;
import jadex.android.bluetooth.exceptions.AlreadyConnectedToDeviceException;
import jadex.android.bluetooth.exceptions.DiscoveryAlreadyRunningException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.routing.FloodingPacketRouter;
import jadex.android.bluetooth.routing.IMessageRouter;
import jadex.android.bluetooth.routing.IMessageSender;
import jadex.android.bluetooth.service.Future;
import jadex.android.bluetooth.service.IFuture;
import jadex.android.bluetooth.service.IResultListener;
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

import android.os.Handler;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

public class BTP2PConnector implements IBluetoothStateListener {

	public static final Integer NOT_CONNECTABLE = -1;

	public static final Integer MESSAGE_SENT = 1;

	private static final boolean PING_DEBUG = false;

	public static final byte MAXHOPS = 4;

	private Timer discoveryTimer;

	private HashMap<String, Long> packetWatcher;

	private ConnectionManager connections;

	private IMessageRouter packetRouter;

	private BTServer btServer;

	private IBluetoothAdapter btAdapter;

	private String ownAdress;

	private ProximityDeviceChangedListener listener;

	/**
	 * List of Unbonded devices in range, could be unconnectable
	 */
	private Set<IBluetoothDevice> unbondedDevicesInRange;

	/**
	 * List of bonded devices in range, which are connectable
	 */
	private Set<IBluetoothDevice> bondedDevicesInRange;

	/**
	 * List of all bonded devices
	 */
	private Set<IBluetoothDevice> bondedDevices;

	private Map<IBluetoothDevice, List<MessageListener>> listeners;

	private boolean autoConnect;

	private TimerTask discoveryTimerTask;

	private int autoDiscoveryPeriod = 10000;

	private IBluetoothStateInformer stateInformer;
	
	private boolean scanning;

	public BTP2PConnector(IBluetoothStateInformer stateInformer,
			Handler mHandler, IBluetoothAdapter btAdapter) {
		this.btAdapter = btAdapter;
		this.stateInformer = stateInformer;

		packetWatcher = new HashMap<String, Long>();
		ownAdress = btAdapter.getAddress();

		listeners = new HashMap<IBluetoothDevice, List<MessageListener>>();
		connections = new ConnectionManager();
		
		unbondedDevicesInRange = new HashSet<IBluetoothDevice>();
		bondedDevicesInRange = new HashSet<IBluetoothDevice>();
		bondedDevices = new HashSet<IBluetoothDevice>();
		Set<IBluetoothDevice> bondedDevices2 = btAdapter.getBondedDevices();
		for (IBluetoothDevice btd : bondedDevices2) {
			bondedDevices.add(btd);
		}

		btServer = new BTServer(stateInformer, mHandler, btAdapter);
		btServer.setConnectionEstablishedListener(new ConnectionEstablishedListener() {
			@Override
			public void connectionEstablished(IConnection con) {
				con.addConnectionListener(defaultConnectionListener);
				getConnections().put(con.getRemoteDevice().getAddress(), con);
			}
		});

		packetRouter = new FloodingPacketRouter(ownAdress);
		packetRouter.setPacketSender(new IMessageSender() {
			@Override
			public void sendMessageToConnectedDevice(DataPacket pkt,
					String address) {
				IConnection con = getConnections().get(address);
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

		
		getConnections().addConnectionsListener(new ConnectionsListener() {

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

		// context.registerReceiver(bcReceiver, intentFilter1);
		// context.registerReceiver(bcReceiver, intentFilter2);
		// context.registerReceiver(bcReceiver, intentFilter3);
		// context.registerReceiver(bcReceiver, intentFilter4);
		// context.registerReceiver(bcReceiver, intentFilter5);
	}

	public void startBTServer() {
		btServer.listen();
	}

	public void shutdown() {
		getConnections().stopAll();
	}

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
		if (getConnections().containsKey(destinationDevice.getAddress())) {
			future.setException(new AlreadyConnectedToDeviceException(
					destinationDevice));
			return future;
		}
		// if (destinationDevice instanceof AndroidBluetoothDeviceWrapper) {
		// nativeDevice = ((AndroidBluetoothDeviceWrapper) destinationDevice)
		// .getDevice();
		// } else {
		// nativeDevice = btAdapter.getRemoteDevice(destinationDevice
		// .getAddress());
		// }

		final ClientConnection newConnection = new ClientConnection(btAdapter,
				destinationDevice);
		newConnection.addConnectionListener(defaultConnectionListener);
		newConnection.addConnectionListener(new IConnectionListener() {

			@Override
			public void messageReceived(DataPacket btMsg,
					IBluetoothDevice fromDevice, IConnection incomingConnection) {
			}

			@Override
			public void connectionStateChanged(IConnection connection) {
				synchronized (future) {
					if (connection.isAlive()) {
						getConnections().put(
								msg.getDestinationDevice().getAddress(),
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
						boolean remove = getBondedDevicesInRange().remove(connection
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
				packet.getData(), packet.Type);
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
	private IConnectionListener defaultConnectionListener = new IConnectionListener() {
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
					Log.i(Helper.LOG_TAG, string);
					// showToast(string);
				}
				IConnection connection = getConnections().get(pkt.Src);
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
						Log.d(Helper.LOG_TAG,"Bonded device in Range: "
								+ pkt.getSourceDevice());
						getBondedDevicesInRange().add(pkt.getSourceDevice());
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
							RoutingInformation ri = MessageProtos.RoutingInformation
									.parseFrom(pkt.getData());
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
				Log.d(Helper.LOG_TAG, "Connection lost: "
						+ connection.getRemoteDevice().getName());
				getConnections().remove(connection.getRemoteDevice().getAddress());
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

		if (packetWatcher.containsKey(pkt.getPktId())) {
			return true;
		} else {
			packetWatcher.put(pkt.getPktId(), Calendar.getInstance()
					.getTimeInMillis());
			return false;
		}
	}

	protected void notifyProximityDevicesChanged() {
		if (listener != null) {
			listener.proximityDevicesChanged();
		}
	}

	public IFuture scanEnvironment(boolean discoverNewDevices) {
		final Future result = new Future();
		if (scanning) {
			result.setException(new DiscoveryAlreadyRunningException());
			return result;
		}
		scanning = true;

		final Thread scanKnownDevicesThread = new Thread(new Runnable() {
			public void run() {
				Log.d(Helper.LOG_TAG, "trying known devices...");
				final int last = bondedDevices.size();
				int i = 0;
				for (final IBluetoothDevice device : bondedDevices) {
					i++;
					// dont try already reachable or connected devices
					if (packetRouter.getReachableDeviceAddresses().contains(
							device.getAddress())
							|| getConnections().containsKey(device.getAddress())) {
						if (i == last) {
							scanning = false;
							Log.d(Helper.LOG_TAG, "finished pinging known devices");
							result.setResult(getBondedDevicesInRange());
						}
						continue;
					}

					IFuture future = connect(device);

					future.addResultListener(new IResultListener() {
						@Override
						public void resultAvailable(Object messageResult) {
							if (messageResult != MESSAGE_SENT) {
								Log.d(Helper.LOG_TAG, "Bonded device not available: "
										+ device.getName());
								if (getBondedDevicesInRange().contains(device)) {
									getBondedDevicesInRange().remove(device);
									notifyProximityDevicesChanged();
								}
							}
						}

						@Override
						public void exceptionOccurred(Exception exception) {
							if (getBondedDevicesInRange().contains(device)
									&& !(exception instanceof AlreadyConnectedToDeviceException)) {
								getBondedDevicesInRange().remove(device);
								notifyProximityDevicesChanged();
							}
						}
					});

					// care about termination event
					if (i >= last) {
						future.addResultListener(new IResultListener() {
							@Override
							public void resultAvailable(Object result) {
								finish();
							}

							@Override
							public void exceptionOccurred(Exception exception) {
								finish();
							}

							private void finish() {
								scanning = false;
								Log.d(Helper.LOG_TAG,"finished pinging known devices");
								result.setResult(getBondedDevicesInRange());
							}
						});
					}
				}
			}
		});

		if (discoverNewDevices) {
			// scan known devices after discovery is finished
			stateInformer
					.addBluetoothStateListener(new BluetoothStateListenerAdapter() {
						@Override
						public void bluetoothStateChanged(
								BluetoothState newState, BluetoothState oldState) {
							if (newState == BluetoothState.discovery_finished) {
								scanKnownDevicesThread.start();
								stateInformer.removeBluetoothStateListener(this);
							}
						}
					});
			// scan new devices
			getUnbondedDevicesInRange().clear();
			if (!btAdapter.isEnabled()) {
				stateInformer
						.addBluetoothStateListener(new BluetoothStateListenerAdapter() {
							@Override
							public void bluetoothStateChanged(
									BluetoothState newState,
									BluetoothState oldState) {
								if (btAdapter.isEnabled()) {
									// showToast("starting Discovery");
									Log.d(Helper.LOG_TAG, "starting Discovery");
									btAdapter.startDiscovery();
									stateInformer.removeBluetoothStateListener(this);
								}
							}
						});
				Log.d(Helper.LOG_TAG, "enabling BT");
				btAdapter.enable();
			} else {
				if (!btAdapter.isDiscovering()) {
					Log.d(Helper.LOG_TAG, "starting Discovery");
					btAdapter.startDiscovery();
				}
			}

		} else {
			// just scan known devices:
			scanKnownDevicesThread.start();
		}
		return result;
	}

	@Override
	public void bluetoothStateChanged(BluetoothState newState,
			BluetoothState oldState) {
		switch (newState) {
		case on:
			ownAdress = btAdapter.getAddress();
			break;
		case discovery_started:
			Log.d(Helper.LOG_TAG, "Discovery started");
			break;
		case discovery_finished:
			Log.d(Helper.LOG_TAG, "Discovery finished");
			notifyProximityDevicesChanged();
			break;
		default:
			break;
		}
	}

	@Override
	public void bluetoothDeviceFound(IBluetoothDevice device) {
		Log.d(Helper.LOG_TAG, "Device found: " + device.getName());
		if (!btAdapter.isDeviceBonded(device)) {
			getUnbondedDevicesInRange()
					.add(new AndroidBluetoothDeviceWrapper(device));
		}
	};

	@Override
	public void bluetoothDeviceBondStateChanged(IBluetoothDevice device,
			BluetoothBondState newState, BluetoothBondState oldState) {
		Log.d(Helper.LOG_TAG, "bond state changed");
		if (newState == BluetoothBondState.bonded) {
			bondedDevices.add(new AndroidBluetoothDeviceWrapper(device));
			getUnbondedDevicesInRange().remove(device);
			notifyProximityDevicesChanged();
		}
	}

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
		getConnections().addConnectionsListener(connectionsListener);
	}

	public void removeConnectionsListener(ConnectionsListener l) {
		getConnections().removeConnectionsListener(l);
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
				if (!btServer.isListening()) {
					startBTServer();
				}
				discoveryTimerTask = new TimerTask() {

					@Override
					public void run() {
						Log.i(Helper.LOG_TAG, "Autoconnect Task Active...");
						IFuture result = scanEnvironment(false);
						result.addResultListener(new IResultListener() {
							@Override
							public void resultAvailable(Object result) {
								Set<IBluetoothDevice> inRange = (Set<IBluetoothDevice>) result;
							}

							@Override
							public void exceptionOccurred(Exception exception) {
								setAutoDiscoveryPeriod((int) (getAutoDiscoveryPeriod() * 1.5));
								Log.d(Helper.LOG_TAG, "Previous scan not yet finished, increased period to "
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
				Log.d(Helper.LOG_TAG,"Autoconnect Task Cancelled.");
			}
		}
	}

	public ConnectionManager getConnections() {
		return connections;
	}

	public Set<IBluetoothDevice> getBondedDevicesInRange() {
		return bondedDevicesInRange;
	}

	public Set<IBluetoothDevice> getUnbondedDevicesInRange() {
		return unbondedDevicesInRange;
	}

	public interface ProximityDeviceChangedListener {
		public void proximityDevicesChanged();
	}
	

}