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
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.message.MessageProtos;
import jadex.android.bluetooth.message.MessageProtos.RoutingInformation;
import jadex.android.bluetooth.routing.IPacketRouter;
import jadex.android.bluetooth.routing.dsdv.DsdvRouter;
import jadex.android.bluetooth.service.Future;
import jadex.android.bluetooth.service.IFuture;
import jadex.android.bluetooth.service.IFuture.IResultListener;
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

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * This Class initializes the Connection to the Bluetooth Ad-Hoc Network.
 * 
 * @author Julian Kalinowski
 */
public class BTP2PConnector implements IBluetoothStateListener {

	/**
	 * If true, this will display ping messages in ADB Debug Level
	 */
	private static final boolean PING_DEBUG = false;

	/**
	 * Constant to define how many hops are tolerated before a
	 * {@link DataPacket} is dropped.
	 */
	public static final byte MAXHOPS = 4;

	/**
	 * This Map contains already received PacketIds to avoid receiving them more
	 * than once. It gets cleaned after some time.
	 */
	private HashMap<String, Long> packetWatcher;

	/**
	 * Manages all BluetoothConnections.
	 */
	private ConnectionManager connections;

	/**
	 * The used packetRouter.
	 */
	private IPacketRouter packetRouter;

	/**
	 * The BTServer is used to open RFCOMM channels for incoming connections
	 */
	private BTServer btServer;

	/**
	 * The local {@link IBluetoothAdapter}
	 */
	private IBluetoothAdapter btAdapter;

	/**
	 * The local BluetoothAddress
	 */
	private String ownAdress;

	/**
	 * This listener gets informed when there are new reachable devices.
	 */
	private KnownDevicesChangedListener listener;

	/**
	 * Listener Interface
	 */
	public interface KnownDevicesChangedListener {
		/**
		 * Called, if there are new reachable devices.
		 */
		public void knownDevicesChanged();
	}

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

	/**
	 * Listeners for incoming Messages for specific devices
	 */
	private Map<IBluetoothDevice, List<MessageListener>> listeners;
	
	/**
	 * Listener Interface
	 */
	public interface MessageListener {
		/**
		 * Called when a Message was received.
		 * @param msg
		 */
		void messageReceived(BluetoothMessage msg);
	}

	/**
	 * Indicates wether automatic connection to new devices is activated.
	 */
	private boolean autoConnect;

	/**
	 * This timer is used to periodically scan for new devices.
	 */
	private Timer discoveryTimer;

	/**
	 * The Task that is executed by discoveryTimer
	 */
	private TimerTask discoveryTimerTask;

	/**
	 * The period (in ms), which is used by discoveryTimer (can be increased
	 * during in runtime)
	 */
	private int autoDiscoveryPeriod = 30000;

	/**
	 * The {@link IBluetoothStateInformer} which can inform this
	 * {@link BTP2PConnector} of changes in BluetoothAdapter states (on, off,
	 * discovering ...).
	 */
	private IBluetoothStateInformer stateInformer;

	/**
	 * Indicates wether we are currently scanning for available devices.
	 */
	private boolean scanning;

	/**
	 * Create a new {@link BTP2PConnector}.
	 * 
	 * @param stateInformer
	 *            The stateInformer where we register as listener
	 * @param btAdapter
	 *            the local BluetoothAdapter
	 */
	public BTP2PConnector(IBluetoothStateInformer stateInformer, IBluetoothAdapter btAdapter) {
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

		btServer = new BTServer(stateInformer, btAdapter);
		btServer.setConnectionEstablishedListener(new ConnectionEstablishedListener() {
			@Override
			public void connectionEstablished(IConnection con) {
				con.addConnectionListener(defaultConnectionListener);
				getConnections().put(con.getRemoteDevice().getAddress(), con);
			}
		});

		// packetRouter = new FloodingPacketRouter();
		packetRouter = new DsdvRouter();
		if (ownAdress != null) {
			packetRouter.setOwnAddress(ownAdress);
			packetRouter.start();
		}
		packetRouter.setPacketSender(connections);
		packetRouter
				.addRoutingEntriesChangedListener(new IPacketRouter.RoutingEntriesChangeListener() {
					@Override
					public void reachableDevicesChanged() {
						notifyKnownDevicesChanged();
					}

					@Override
					public void connectedDevicesChanged() {
						notifyKnownDevicesChanged();
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
	}

	/**
	 * Starts the {@link BTServer}.
	 */
	public void startBTServer() {
		btServer.listen();
	}

	/**
	 * Cancels all connections.
	 */
	public void shutdown() {
		getConnections().stopAll();
	}

	/**
	 * Sends a {@link BluetoothMessage} to its specified target
	 * 
	 * @param msg
	 *            the {@link BluetoothMessage} to send.
	 * @return a Future which will deliver the result or a possible exception
	 *         thrown during sending the message.
	 */
	public IFuture sendMessage(BluetoothMessage msg) {
		DataPacket dataPacket;
		try {
			dataPacket = new DataPacket(msg, msg.getType());
			return sendMessage(dataPacket);
		} catch (MessageConvertException e) {
			Future ret = new Future();
			ret.setException(e);
			return ret;
		}
	}

	/**
	 * Sends a {@link DataPacket} to its specified target.
	 * 
	 * @param msg
	 *            the {@link DataPacket}
	 * @return a Future which will deliver the result or a possible exception
	 *         thrown during sending the message. Result will be
	 *         BluetoothMessage.MESSAGE_SENT or BluetoothMessage.NOT_CONNECTABLE
	 */
	public IFuture sendMessage(final DataPacket msg) {
		return packetRouter.routePacket(msg, null);
	}

	/**
	 * Initiates a Connection the the given remote device.
	 * 
	 * @param dev
	 *            The {@link IBluetoothDevice} to connect to.
	 * @return a future containing the result or a possible exception thrown
	 *         during sending the message. Result will be
	 *         BluetoothMessage.MESSAGE_SENT or BluetoothMessage.NOT_CONNECTABLE
	 */
	public IFuture connect(IBluetoothDevice dev) {
		return connect(dev.getAddress());
	}

	/**
	 * Initiates a Connection the the given remote device.
	 * 
	 * @param address
	 *            the Bluetooth Address of the remove device
	 * @return a future containing the result or a possible exception thrown
	 *         during sending the message. Result will be
	 *         BluetoothMessage.MESSAGE_SENT or BluetoothMessage.NOT_CONNECTABLE
	 */
	public IFuture connect(String address) {
		DataPacket dataPacket;
		try {
			dataPacket = new DataPacket(address, null,
					DataPacket.TYPE_CONNECT_SYN);
			return sendInitialMessage(dataPacket);
		} catch (MessageConvertException e) {
			e.logThisException();
			Future ret = new Future();
			ret.setException(e);
			return ret;
		}
	}

	/**
	 * Initiates a connection and sends the given {@link BluetoothMessage}.
	 * 
	 * @param msg
	 *            {@link BluetoothMessage}
	 * @return a Future which will deliver the result or a possible exception
	 *         thrown during sending the message. Result will be
	 *         BluetoothMessage.MESSAGE_SENT or BluetoothMessage.NOT_CONNECTABLE
	 */
	public IFuture sendInitialMessage(BluetoothMessage msg) {
		try {
			return sendInitialMessage(new DataPacket(msg, msg.getType()));
		} catch (MessageConvertException e) {
			Future ret = new Future();
			ret.setException(e);
			return ret;
		}
	}

	/**
	 * Initiates a connection and sends the given {@link DataPacket}.
	 * 
	 * @param msg
	 * @return a Future which will deliver the result or a possible exception
	 *         thrown during sending the message. Result will be
	 *         BluetoothMessage.MESSAGE_SENT or BluetoothMessage.NOT_CONNECTABLE
	 */
	public IFuture sendInitialMessage(final DataPacket msg) {
		final Future future = new Future();
		// establish new connection
		IBluetoothDevice destinationDevice = msg.getDestinationDevice();
		if (getConnections().containsConnection(destinationDevice.getAddress())) {
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
						newConnection
								.addConnectionListener(defaultConnectionListener);
						try {
							connection.write(msg);
							if (!future.resultAvailable) {
								future.setResult(BluetoothMessage.MESSAGE_SENT);
							}
						} catch (IOException e) {
							// try again?
							connection.close();
							if (!future.resultAvailable) {
								future.setResult(BluetoothMessage.NOT_CONNECTABLE);
							}
						} finally {
							connection.removeConnectionListener(this);
						}
					} else {
						connection.removeConnectionListener(this);
						if (!future.resultAvailable) {
							future.setResult(BluetoothMessage.NOT_CONNECTABLE);
						}
						boolean remove = getBondedDevicesInRange().remove(
								connection.getRemoteDevice());
						if (remove) {
							// proximityDevicesChanged();
						}
						connection = null;
					}
				}
			}

			@Override
			public void messageNotSent(DataPacket pkt) {
			}
		});
		newConnection.connect();
		return future;
	}

	private void handleDataPacketReceived(DataPacket packet) {
		// String dataString = packet.getDataAsString();
		// if ("foo".equals(dataString)) {
		// DataPacket reply = new DataPacket(packet.Src, "bar".getBytes(),
		// DataPacket.TYPE_DATA);
		// reply.Src = ownAdress;
		// sendMessage(reply);
		// }

		if (packet.getData() == null) {
			Log.e(Helper.LOG_TAG, "received packet with data=null, type was: "
					+ packet.getTypeDescription());
		}

		BluetoothMessage bluetoothMessage = new BluetoothMessage(
				packet.getSource(), packet.getData(), packet.getType());
		List<MessageListener> list = listeners.get(bluetoothMessage
				.getRemoteAddress());
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

			if (ownAdress.equals(pkt.getDestination())) {
				// handle messages directed to us
				if (!(pkt.getType() == DataPacket.TYPE_PING || pkt.getType() == DataPacket.TYPE_PONG)
						|| PING_DEBUG) {
					// String string = "[Received] " + pkt.toString();
					// Log.d(Helper.LOG_TAG, string);
				}
				IConnection connection = getConnections().get(pkt.getSource());
				if (connection == null) {
					connection = incomingConnection;
				}
				if (connection != null) {
					if (pkt.getType() == DataPacket.TYPE_PING) {
						/**
						 * handle PING
						 */
						pkt.setDestination(pkt.getSource());
						pkt.setType(DataPacket.TYPE_PONG);
						pkt.setSource(ownAdress);
						pkt.newPaketID();
						try {
							connection.write(pkt);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (pkt.getType() == DataPacket.TYPE_PONG) {
						/**
						 * handle PONG
						 */
						Log.d(Helper.LOG_TAG,
								"Bonded device in Range: "
										+ pkt.getSourceDevice());
						getBondedDevicesInRange().add(pkt.getSourceDevice());
						notifyKnownDevicesChanged();
					} else if (pkt.getType() == DataPacket.TYPE_CONNECT_SYN) {
						/**
						 * handle SYN
						 */
						// proximityDevicesChanged();
						Log.d(Helper.LOG_TAG,
								"SYN received from: " + pkt.getSource());
						DataPacket ack;
						try {
							ack = new DataPacket(pkt.getSourceDevice(), null,
									DataPacket.TYPE_CONNECT_ACK);
							sendMessage(ack);
						} catch (MessageConvertException e) {
							e.logThisException();
						}
					} else if (pkt.getType() == DataPacket.TYPE_CONNECT_ACK) {
						/**
						 * handle ACK
						 */
						Log.d(Helper.LOG_TAG,
								"ACK received from: " + pkt.getSource());
					} else if (pkt.getType() == DataPacket.TYPE_ROUTING_INFORMATION) {
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

				if (pkt.getType() == DataPacket.TYPE_DATA
						|| pkt.getType() == DataPacket.TYPE_AWARENESS_INFO) {
					handleDataPacketReceived(pkt);
				}
				notifyKnownDevicesChanged();

			} else if (pkt.getType() == DataPacket.TYPE_BROADCAST) {
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
				getConnections().remove(
						connection.getRemoteDevice().getAddress());
				// packetRouter.removeConnectedDevice(connection.getRemoteDevice()
				// .getAddress());
				connection.removeConnectionListener(this);
				connection = null;
			}
		}

		@Override
		public void messageNotSent(DataPacket pkt) {
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

	protected void notifyKnownDevicesChanged() {
		if (listener != null) {
			listener.knownDevicesChanged();
		}
	}

	/**
	 * Scans the Bluetooth Environment for connectable Devices. Ignores all
	 * already reachable devices.
	 * 
	 * @param discoverNewDevices
	 *            true if unpaired devices should be tried, too
	 * @return Set of connectable Devices or
	 *         {@link DiscoveryAlreadyRunningException}
	 */
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
							|| getConnections()
									.containsConnection(device.getAddress())) {
						if (i == last) {
							scanning = false;
							Log.d(Helper.LOG_TAG,
									"finished pinging known devices");
							result.setResult(getBondedDevicesInRange());
						}
						Log.d(Helper.LOG_TAG, "Not trying " + device.getName() + " (" + device.getAddress() +") "
								+ ", because it is already reachable.");
						continue;
					}

					IFuture future = connect(device);

					future.addResultListener(new IResultListener() {
						@Override
						public void resultAvailable(Object messageResult) {
							if (messageResult != BluetoothMessage.MESSAGE_SENT) {
								Log.d(Helper.LOG_TAG,
										"Bonded device not available: "
												+ device.getName() + " (" + device.getAddress() +") ");
								if (getBondedDevicesInRange().contains(device)) {
									getBondedDevicesInRange().remove(device);
									notifyKnownDevicesChanged();
								}
							}
						}

						@Override
						public void exceptionOccurred(Exception exception) {
							if (getBondedDevicesInRange().contains(device)
									&& !(exception instanceof AlreadyConnectedToDeviceException)) {
								getBondedDevicesInRange().remove(device);
								notifyKnownDevicesChanged();
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
								Log.d(Helper.LOG_TAG,
										"finished pinging known devices");
								// TODO: remove:
								// if (connections.size() > 0 ) {
								// setAutoConnect(false);
								// }
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
								stateInformer
										.removeBluetoothStateListener(this);
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
									stateInformer
											.removeBluetoothStateListener(this);
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
			Log.d(Helper.LOG_TAG, "Local Device Address: " + ownAdress);
			packetRouter.setOwnAddress(ownAdress);
			packetRouter.start();
			break;
		case discovery_started:
			Log.d(Helper.LOG_TAG, "Discovery started");
			break;
		case discovery_finished:
			Log.d(Helper.LOG_TAG, "Discovery finished");
			notifyKnownDevicesChanged();
			break;
		default:
			break;
		}
	}

	@Override
	public void bluetoothDeviceFound(IBluetoothDevice device) {
		Log.d(Helper.LOG_TAG, "Device found: " + device.getName() + " (" + device.getAddress() +") ");
		if (!btAdapter.isDeviceBonded(device)) {
			getUnbondedDevicesInRange().add(
					new AndroidBluetoothDeviceWrapper(device));
		}
	};

	@Override
	public void bluetoothDeviceBondStateChanged(IBluetoothDevice device,
			BluetoothBondState newState, BluetoothBondState oldState) {
		Log.d(Helper.LOG_TAG, "bond state changed");
		if (newState == BluetoothBondState.bonded) {
			bondedDevices.add(new AndroidBluetoothDeviceWrapper(device));
			getUnbondedDevicesInRange().remove(device);
			notifyKnownDevicesChanged();
		}
	}

	/**
	 * Adds a Message Listener which will be informed when a Message from device
	 * remoteDevice is received.
	 * 
	 * @param remoteDevice
	 *            The remote device from which to receive Messages
	 * @param l
	 *            The Listener to be informed
	 */
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

	/**
	 * Removes a Message Listener
	 * 
	 * @param remoteDevice
	 * @param l
	 */
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

	/**
	 * Adds a ConnectionListener which will be informed about new and broken
	 * Connections.
	 * 
	 * @param connectionsListener
	 */
	public void addConnectionsListener(ConnectionsListener connectionsListener) {
		getConnections().addConnectionsListener(connectionsListener);
	}

	/**
	 * Removes the specified ConnectionsListener.
	 * 
	 * @param l
	 */
	public void removeConnectionsListener(ConnectionsListener l) {
		getConnections().removeConnectionsListener(l);
	}

	/**
	 * Sets the Listener, which will be informed when the List of Known Devices
	 * in the P2P Network changes.
	 * 
	 * @param l
	 */
	public void setKnownDevicesChangedListener(KnownDevicesChangedListener l) {
		listener = l;
	}

	/**
	 * @return An Array of Connected and Reachable Devices in the Network.
	 */
	public IBluetoothDevice[] getKnownDevices() {
		HashSet<String> hashSet = new HashSet<String>(
				packetRouter.getReachableDeviceAddresses());
		hashSet.addAll(packetRouter.getConnectedDeviceAddresses());
		IBluetoothDevice[] arr = new IBluetoothDevice[hashSet.size()];
		Iterator<String> it = hashSet.iterator();
		for (int i = 0; i < hashSet.size(); i++) {
			arr[i] = Helper.getBluetoothDeviceFactory().createBluetoothDevice(
					it.next());
		}
		return arr;
	}

	/**
	 * Sets the period after which the auto discovery mechanism is launched
	 * again.
	 * 
	 * @param autoDiscoveryPeriod
	 *            time in ms
	 */
	public void setAutoDiscoveryPeriod(int autoDiscoveryPeriod) {
		this.autoDiscoveryPeriod = autoDiscoveryPeriod;
	}

	/**
	 * @return Auto Discovery Period in ms.
	 */
	public int getAutoDiscoveryPeriod() {
		return autoDiscoveryPeriod;
	}

	/**
	 * Enables/Disables the Auto Connect Mechanism.
	 * 
	 * @param b
	 *            true to enable, false to disable
	 */
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
						if (connections.size() >= 1) {
							Log.i(Helper.LOG_TAG,
									"Not activating Autoconnect. Already Connected to "
											+ connections.size() + " devices.");
							
							Log.d(Helper.LOG_TAG, packetRouter.toString());
							return;
						}
						Log.i(Helper.LOG_TAG,
								"Autoconnect Task Active... currently connected to "
										+ connections.size() + " devices.");
						Log.d(Helper.LOG_TAG, packetRouter.toString());
						IFuture result = scanEnvironment(false);
						result.addResultListener(new IResultListener() {
							@Override
							public void resultAvailable(Object result) {
								Set<IBluetoothDevice> inRange = (Set<IBluetoothDevice>) result;
							}

							@Override
							public void exceptionOccurred(Exception exception) {
								setAutoDiscoveryPeriod((int) (getAutoDiscoveryPeriod() * 1.5));
								Log.d(Helper.LOG_TAG,
										"Previous scan not yet finished, increased period to "
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
				Log.d(Helper.LOG_TAG, "Autoconnect Task Cancelled.");
			}
		}
	}

	/**
	 * @return the {@link ConnectionManager} which handles all Incoming/Outgoing
	 *         Connections.
	 */
	public ConnectionManager getConnections() {
		return connections;
	}

	/**
	 * @return All bonded Devices that are in Range
	 */
	public Set<IBluetoothDevice> getBondedDevicesInRange() {
		return bondedDevicesInRange;
	}

	/**
	 * @return All unbonded Devices that are in Range
	 */
	public Set<IBluetoothDevice> getUnbondedDevicesInRange() {
		return unbondedDevicesInRange;
	}

}