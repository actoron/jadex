package jadex.android.bluetooth;

import jadex.android.bluetooth.device.AndroidBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothDevice;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ServerConnection extends AConnection {

	private BluetoothSocket socket;
	private Timer timer;

	public ServerConnection(BluetoothAdapter adapter, BluetoothSocket socket) {
		super(adapter, socket.getRemoteDevice());
		this.socket = socket;
	}

	@Override
	public void connect() {
		connectedThread = new ConnectedThread(socket);
		connectedThread.start();
		timer = new Timer();
		timer.scheduleAtFixedRate(pingTask, 1000, CONNECTION_TIMEOUT);
	}

	private TimerTask pingTask = new TimerTask() {

		private boolean lastPongReceived = true;

		@Override
		public void run() {
			if (isAlive()) {
				if (lastPongReceived) {
					DataPacket ping = new DataPacket(new AndroidBluetoothDevice(remoteDevice), null,
							DataPacket.TYPE_PING);
					try {
						addConnectionListener(new ConnectionListener() {
							@Override
							public void messageReceived(DataPacket pkt,
									IBluetoothDevice fromDevice, IConnection incomingConnection) {
								if (pkt.Type == DataPacket.TYPE_PONG) {
									lastPongReceived = true;
								}
							}

							@Override
							public void connectionStateChanged(
									IConnection connection) {
							}
						});
						write(ping.asByteArray());
						lastPongReceived = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					close();
				}
			}
		}
	};

}
