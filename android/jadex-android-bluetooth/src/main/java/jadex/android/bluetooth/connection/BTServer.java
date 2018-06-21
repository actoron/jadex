package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothAdapter.BluetoothState;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothServerSocket;
import jadex.android.bluetooth.device.IBluetoothSocket;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.service.ConnectionService;
import jadex.android.bluetooth.util.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.os.Handler;
import android.util.Log;

/**
 * This is the Bluetooth Server class. It can start several Threads to listen
 * for incoming Bluetooth Connections.
 * 
 * @author Julian Kalinowski
 */
public class BTServer {
	private IBluetoothAdapter adapter;

	private boolean listening;

	private List<AcceptThread> listenThreads;

	private ConnectionEstablishedListener estListener;
	
	/**
	 * Listener Class
	 */
	public interface ConnectionEstablishedListener {
		/**
		 * Called, when a new Connection is established. 
		 * @param con The {@link IConnection}
		 */
		void connectionEstablished(IConnection con);
	}

	private List<ServerConnection> connectedThreads;

	private IBluetoothStateInformer stateInformer;

	private static final String SERVICE_NAME = "";

	static Set<Integer> usedUUIDnums;

	static final UUID[] UUIDS = new UUID[] {
			UUID.fromString("8050a070-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050a071-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050a072-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050a073-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c780-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c781-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c782-bcf2-11e0-962b-0800200c9a66"),
	// UUID.fromString("8050c783-bcf2-11e0-962b-0800200c9a66"),
	// UUID.fromString("8050c784-bcf2-11e0-962b-0800200c9a66"),
	// UUID.fromString("8050c785-bcf2-11e0-962b-0800200c9a66")
	};

	private static final int MESSAGE_READ = 0;



	/**
	 * Constructor
	 * @param stateInformer The instance which is able to inform the BTServer about BT State changes.
	 * @param adapter The BluetoothAdapter to use to listen for connections
	 */
	public BTServer(IBluetoothStateInformer stateInformer,
			IBluetoothAdapter adapter) {
		this.adapter = adapter;
		this.stateInformer = stateInformer;
		listenThreads = new ArrayList<AcceptThread>();
		connectedThreads = new ArrayList<ServerConnection>();
		usedUUIDnums = new HashSet<Integer>();
	}

	/**
	 * Starts UUIDS.length() Server Threads.
	 */
	public void listen() {
		if (!adapter.isEnabled()) {
			stateInformer
					.addBluetoothStateListener(new BluetoothStateListenerAdapter() {
						@Override
						public void bluetoothStateChanged(
								BluetoothState newState, BluetoothState oldState) {
							if (newState == BluetoothState.on) {
								_listen();
								stateInformer
										.removeBluetoothStateListener(this);
							}
						}
					});
			adapter.enable();
		} else {
			_listen();
		}

		stateInformer
				.addBluetoothStateListener(new BluetoothStateListenerAdapter() {
					@Override
					public void bluetoothStateChanged(BluetoothState newState,
							BluetoothState oldState) {
						if (newState == BluetoothState.switching_off) {
							for (AcceptThread listenThread : listenThreads) {
								listenThread.cancel();
							}
							for (IConnection con : connectedThreads) {
								if (con instanceof ServerConnection) {
									con.close();
								}
							}
							listening = false;
							stateInformer.removeBluetoothStateListener(this);
						}
					}
				});
	}

	private void _listen() {
		if (!listening) {
			for (int i = 0; i < UUIDS.length; i++) {
				AcceptThread acceptThread = new AcceptThread(UUIDS[i], i);
				listenThreads.add(acceptThread);
				acceptThread.start();
				Log.d(Helper.LOG_TAG, "AcceptThread #" + i + " started");
			}
			listening = true;
			Log.d(Helper.LOG_TAG, "Server Threads started");
		} else {
			Log.d(Helper.LOG_TAG, "BTServer already started.");
		}
	}


	class AcceptThread extends Thread {
		private IBluetoothServerSocket mmServerSocket;
		private UUID uuid;
		private int uuidNum;

		public AcceptThread(UUID uuid, int num) {
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			this.uuid = uuid;
			this.uuidNum = num;
		}

		public void run() {
			IBluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned
			// while (true) {
			try {
				IBluetoothServerSocket tmp = null;
				tmp = adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME,
						uuid);
				mmServerSocket = tmp;
				Log.d(Helper.LOG_TAG, "Socket listening with uuid: " + uuidNum);
				socket = mmServerSocket.accept();
			} catch (IOException e) {
				// e.printStackTrace();
				Log.e(Helper.LOG_TAG,
						"Could not start Bluetooth Server: " + e.toString());
				// break;
				return;
			}
			// If a connection was accepted
			if (socket != null) {
				// Do work to manage the connection (in a separate thread)
				if (uuidNum == 0) {
					try {
						Log.d(Helper.LOG_TAG,
								"Incoming Connection on first UUID. Dropping...");
						Thread.sleep(500);
						socket.close();
					} catch (IOException e) {
					} catch (InterruptedException e) {
						try {
							socket.close();
						} catch (IOException e1) {
						}
					} finally {
						run();
					}
				} else {
					Log.d(Helper.LOG_TAG,
							"Incoming Connection accepted on UUID " + uuidNum);
					usedUUIDnums.add(uuidNum);
					ServerConnection connection = new ServerConnection(adapter,
							socket);
					connectedThreads.add(connection);
					connection.addConnectionListener(new IConnectionListener() {

						@Override
						public void messageReceived(DataPacket pkt,
								IBluetoothDevice fromDevice,
								IConnection incomingConnection) {
						}

						@Override
						public void connectionStateChanged(
								IConnection connection) {
							if (!connection.isAlive()) {
								connection.removeConnectionListener(this);
								usedUUIDnums.remove(uuidNum);
								run();
							}
						}

						@Override
						public void messageNotSent(DataPacket pkt) {
						}
					});
					connection.connect();
					estListener.connectionEstablished(connection);
				}

				try {
					mmServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// break;
			}
			// }
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * Sets the Listener to be informed when new Connections are made.
	 * @param estListener
	 */
	public void setConnectionEstablishedListener(
			ConnectionEstablishedListener estListener) {
		this.estListener = estListener;
	}


	/**
	 * @return true, if the Server is already running.
	 */
	public boolean isListening() {
		return listening;
	}
}
