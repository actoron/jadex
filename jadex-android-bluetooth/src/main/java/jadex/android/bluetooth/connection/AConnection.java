package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothSocket;
import jadex.android.bluetooth.message.DataPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AConnection implements IConnection {

	protected static final int CONNECTION_TIMEOUT = 2000;
	
	public static final int MESSAGE_READ = 0;
	protected IBluetoothAdapter adapter;
	protected IBluetoothDevice remoteDevice;

	protected ConnectedThread connectedThread;

	protected List<IConnectionListener> listeners;

	public AConnection(IBluetoothAdapter adapter, IBluetoothDevice remoteDevice) {
		this.adapter = adapter;
		this.remoteDevice = remoteDevice;
		listeners = new CopyOnWriteArrayList<IConnectionListener>();
	}

	protected class ConnectedThread extends Thread {
		private final IBluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(IBluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			new Thread(new Runnable() {

				public void run() {
					setConnectionAlive(true);
					byte[] buffer = new byte[1024]; // buffer store for the
													// stream
					int bytes; // bytes returned from read()
					// Keep listening to the InputStream until an exception
					// occurs
					while (true) {
						try {
							// Read from the InputStream
							bytes = mmInStream.read(buffer);
							// Send the obtained bytes to the UI Activity
							DataPacket dataPacket = new DataPacket(buffer);
							// BluetoothMessage bluetoothMessage = new
							// BluetoothMessage(
							// mmSocket.getRemoteDevice(), buffer);
							// mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
							// bluetoothMessage).sendToTarget();
							synchronized (ConnectedThread.this) {
								notifyMessageReceived(dataPacket);
							}
						} catch (IOException e) {
							// maybe remove connection from list here?
							setConnectionAlive(false);
							break;
						}
					}
				}
			}).start();
		}

		/* Call this from the main Activity to send data to the remote device */
		public synchronized void write(byte[] bytes) throws IOException {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
				setConnectionAlive(false);
				throw e;
			}
		}

		public void cancel() {
			try {
				mmSocket.close();

			} catch (IOException e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.android.bluetooth.IConnection#write(byte[])
	 */
	@Override
	public void write(byte[] bytes) throws IOException {
		if (isAlive()) {
			connectedThread.write(bytes);
		} else {
			throw new IOException("Not Connected.");
		}
	}

	private boolean connectionAlive;

	protected void setConnectionAlive(boolean alive) {
		if (connectionAlive != alive) {
			connectionAlive = alive;
			notifyConnectionStateChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.android.bluetooth.IConnection#isAlive()
	 */
	@Override
	public boolean isAlive() {
		return connectionAlive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jadex.android.bluetooth.IConnection#setConnectionListener(de
	 * .unihamburg.vsis.test.bluetooth.BTConnectionListener)
	 */
	@Override
	public void addConnectionListener(final IConnectionListener l) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (listeners) {
					listeners.add(l);
				}
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jadex.android.bluetooth.IConnection#setConnectionListener(de
	 * .unihamburg.vsis.test.bluetooth.BTConnectionListener)
	 */
	@Override
	public void removeConnectionListener(final IConnectionListener l) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (listeners) {
					listeners.remove(l);
				}
			}
		}).start();
	}

	protected void notifyMessageReceived(DataPacket dataPacket) {
		synchronized (listeners) {
			for (IConnectionListener l : listeners) {
				l.messageReceived(dataPacket, this.remoteDevice, this);
			}
		}
	}

	protected void notifyConnectionStateChanged() {
		synchronized (listeners) {
			for (IConnectionListener l : listeners) {
				l.connectionStateChanged(this);
			}
		}
	}

	/* Call this from the main Activity to shutdown the connection */
	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.android.bluetooth.IConnection#close()
	 */
	@Override
	public void close() {
		if (connectedThread != null) {
			connectedThread.cancel();
			setConnectionAlive(false);
			connectedThread = null;
		}
	}

	@Override
	public IBluetoothDevice getRemoteDevice() {
		return remoteDevice;
	}
}