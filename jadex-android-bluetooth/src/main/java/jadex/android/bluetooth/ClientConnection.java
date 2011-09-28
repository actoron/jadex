package jadex.android.bluetooth;

import jadex.android.bluetooth.device.AndroidBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.util.Helper;
import jadex.android.bluetooth.util.ResettableTimer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ClientConnection extends AConnection {

	public ClientConnection(BluetoothAdapter adapter, IBluetoothDevice dev) {
		this(
				adapter,
				dev instanceof AndroidBluetoothDevice ? ((AndroidBluetoothDevice) dev)
						.getDevice() : adapter
						.getRemoteDevice(dev.getAddress()));
	}

	public ClientConnection(BluetoothAdapter adapter, BluetoothDevice dev) {
		super(adapter, dev);
	}

	public void connect() {
		if (connectThread == null && connectThread == null) {
			connectThread = new ConnectThread(remoteDevice);
			connectThread.run();
		}
	}

	private ConnectThread connectThread;

	private Runnable receivePingTask = new Runnable() {

		@Override
		public void run() {
			Helper.jLog("Connection to " + remoteDevice.getAddress()
					+ " timed out.");
			close();
		}
	};

	private ResettableTimer timer;

	public void manageConnectedSocket(BluetoothSocket mmSocket) {
		connectedThread = new ConnectedThread(mmSocket);
		connectedThread.start();
		timer = new ResettableTimer(
				Executors.newSingleThreadScheduledExecutor(),
				CONNECTION_TIMEOUT * 2, TimeUnit.MILLISECONDS, receivePingTask);
	}

	@Override
	protected void notifyMessageReceived(DataPacket dataPacket) {
		// We need to overwrite this method. If we add ourselves as just another
		// listener only, we get called after writing the response - which could
		// take forever on dead connections.
		if (dataPacket.Type == DataPacket.TYPE_PING && timer != null) {
			timer.reset(false);
		}
		super.notifyMessageReceived(dataPacket);
	}

	private class ConnectThread extends Thread {
		private BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private int uuidNum;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				uuidNum = 0;
				tmp = mmDevice
						.createRfcommSocketToServiceRecord(BTServer.UUIDS[uuidNum]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			if (adapter.isDiscovering()) {
				adapter.cancelDiscovery();
			}

			while (true) {
				try {
					// check if device is responding:
					mmSocket.connect();
					if (uuidNum == 0) {
						// if this is the first UUID, reserved for connection
						// checking,
						// close the socket and connect using another UUID.
						mmSocket.close();
						uuidNum++;
						mmSocket = mmDevice
								.createRfcommSocketToServiceRecord(BTServer.UUIDS[uuidNum]);
						mmSocket.connect();
					}
					manageConnectedSocket(mmSocket);
					break;
				} catch (IOException e) {
					uuidNum++;
					// cancel connection if tried UUID was the first
					// (because then no server is running on remote device)
					if (uuidNum != 1 && (uuidNum < BTServer.UUIDS.length)) {
						try {
							mmSocket = mmDevice
									.createRfcommSocketToServiceRecord(BTServer.UUIDS[uuidNum]);

						} catch (IOException e2) {
							try {
								mmSocket.close();
							} catch (IOException closeException) {
							}
						}
					} else {
						// connection could not be established
						try {
							mmSocket.close();
						} catch (IOException closeException) {
						}
						notifyConnectionStateChanged();
						// setConnectionAlive(false);
						break;
					}
				}
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public synchronized void close() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
		super.close();
	}

}
