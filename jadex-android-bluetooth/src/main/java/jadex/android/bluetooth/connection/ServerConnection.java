package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothSocket;
import jadex.android.bluetooth.message.DataPacket;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ServerConnection extends AConnection {

	private IBluetoothSocket socket;
	private Timer timer;

	public ServerConnection(IBluetoothAdapter adapter, IBluetoothSocket socket) {
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
					DataPacket ping = new DataPacket(remoteDevice, null,
							DataPacket.TYPE_PING);
					try {
						addConnectionListener(new IConnectionListener() {
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
