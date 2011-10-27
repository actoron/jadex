package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothSocket;
import jadex.android.bluetooth.exceptions.MessageConvertException;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.util.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

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
		private InputStream mmInStream;
		private OutputStream mmOutStream;
		private writerThread writer;
		private readerThread reader;
		boolean running;

		private BlockingQueue<byte[]> packetQueue;

		public ConnectedThread(IBluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			running = false;

			packetQueue = new LinkedBlockingQueue<byte[]>();

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;

			reader = new readerThread();
			writer = new writerThread();
		}

		public void run() {
			running = true;
			reader.start();
			writer.start();
		}
		
		private synchronized void _write(byte[] bytes) throws IOException {
			try {
				mmOutStream.write(bytes);
				mmOutStream.flush();
			} catch (IOException e) {
				Log.e(Helper.LOG_TAG,
						"catched Exception while writing to Stream: "
								+ e.toString() + "\n " + Helper.stackTraceToString(e.getStackTrace()));
				setConnectionAlive(false);
				throw e;
			}
		}
		
		/* Call this from the main Activity to send data to the remote device */
		public void write(byte[] bytes) {
			if (running) {
				packetQueue.add(bytes);
			}
		}

		public synchronized void cancel() {
			if (running) {
				running = false;
				writer.running = false;
				reader.running = false;
				try {
					// this wakes up the writer thread
					// which will then terminate
					packetQueue.add(new byte[0]);
					mmOutStream.close();
					mmInStream.close();
					mmSocket.close();
				} catch (IOException e) {

				} finally {
					mmOutStream = null;
					mmInStream = null;
					writer = null;
					reader = null;
					packetQueue = null;
				}
			}
		}

		class writerThread extends Thread {
			public boolean running = true;

			@Override
			public void run() {
				while (running) {
					try {
						byte[] take = packetQueue.take();
						_write(take);
					} catch (Exception e) {
						if (running) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		class readerThread extends Thread {

			public boolean running = true;

			@Override
			public void run() {
				setConnectionAlive(true);
				byte[] buffer;  // buffer store for the
												// stream
				int bytes; // bytes returned from read()
				// Keep listening to the InputStream until an exception
				// occurs
				while (running) {
					try {
						// Read from the InputStream
						buffer = new byte[DataPacket.PACKET_SIZE + 1];
						bytes = mmInStream.read(buffer);
						Log.i(Helper.LOG_TAG, "(Connection) received a packet of size: " + bytes);
						if (bytes > DataPacket.PACKET_SIZE) {
							mmInStream.skip(mmInStream.available());
							Log.e(Helper.LOG_TAG, "Received a DataPacket which is too big for the receivebuffer! Dropping.");
							continue;
						}
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
						if (running) {
						Log.e(Helper.LOG_TAG,
								"catched IOException while reading from Stream:"
										+ e.toString() + "\n "
										+ Helper.stackTraceToString(e.getStackTrace()));
						}
						setConnectionAlive(false);
						break;
					} catch (MessageConvertException e) {
						e.logThisException();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.android.bluetooth.IConnection#write(byte[])
	 */
	@Override
	public void write(DataPacket pkt) throws IOException {
		if (isAlive()) {
			try {
				connectedThread.write(pkt.asByteArray());
			} catch (MessageConvertException e) {
				e.logThisException();
			}
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
	 * @see jadex.android.bluetooth.IConnection#setConnectionListener(de
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
	 * @see jadex.android.bluetooth.IConnection#setConnectionListener(de
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