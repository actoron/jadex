package jadex.android.bluetooth;

import jadex.android.bluetooth.device.IBluetoothAdapter;
import jadex.android.bluetooth.device.IBluetoothServerSocket;
import jadex.android.bluetooth.device.IBluetoothSocket;
import jadex.android.bluetooth.service.ConnectionService;
import jadex.android.bluetooth.util.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class BTServer {
	private IBluetoothAdapter adapter;

	private boolean listening;

	private Handler guiHandler;

	private List<AcceptThread> listenThreads;
	
	private ConnectionEstablishedListener estListener;

	public List<ServerConnection> connectedThreads;

	public static final String SERVICE_NAME = "";

	public static final UUID[] UUIDS = new UUID[] { UUID.fromString("8050a070-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050a071-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050a072-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050a073-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c780-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c781-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c782-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c783-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c784-bcf2-11e0-962b-0800200c9a66"),
			UUID.fromString("8050c785-bcf2-11e0-962b-0800200c9a66") };

	public static final int MESSAGE_READ = 0;

	public interface ConnectionEstablishedListener {
		void connectionEstablished(IConnection con);
	}
	
	public BTServer(Handler handler, IBluetoothAdapter adapter) {
		this.adapter = adapter;
		this.guiHandler = handler;
		listenThreads = new ArrayList<AcceptThread>();
		connectedThreads = new ArrayList<ServerConnection>();
	}
	
	public void listen() {
		if (!adapter.isEnabled()) {
			new BroadcastIntentListenerAdapter(ConnectionService.CONTEXT, new IntentFilter(
					BluetoothAdapter.ACTION_STATE_CHANGED), false) {
				@Override
				protected void onReceive(Context context, Intent intent) {
					int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
					if (state == BluetoothAdapter.STATE_ON) {
						_listen();
						unregister();
					}
				}
			};
			adapter.enable();
		} else {
			_listen();
		}

		new BroadcastIntentListenerAdapter(ConnectionService.CONTEXT, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED), false) {
			@Override
			protected void onReceive(Context context, Intent intent) {
				int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
				if (state == BluetoothAdapter.STATE_TURNING_OFF) {
					for (AcceptThread listenThread : listenThreads) {
						listenThread.cancel();
					}
					for (IConnection con: connectedThreads) {
						if (con instanceof ServerConnection) {
							con.close();
						}
					}
					listening = false;
					unregister();
				}
			}
		};
	}

	private void _listen() {
		if (!listening) {
			for (int i = 0; i < UUIDS.length; i++) {
				AcceptThread acceptThread = new AcceptThread(UUIDS[i]);
				listenThreads.add(acceptThread);
				acceptThread.start();
			}
			listening = true;
			guiHandler.obtainMessage(ConnectionService.SHOW_TOAST, "Server Threads started").sendToTarget();
		} else {
			guiHandler.obtainMessage(ConnectionService.SHOW_TOAST, "BTServer already started.").sendToTarget();
		}
	}
	
	public void setConnectionEstablishedListener(ConnectionEstablishedListener estListener) {
		this.estListener = estListener;
	}

	class AcceptThread extends Thread {
		private IBluetoothServerSocket mmServerSocket;
		private UUID uuid;

		public AcceptThread(UUID uuid) {
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			this.uuid = uuid;
		}

		public void run() {
			IBluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				try {
					IBluetoothServerSocket tmp = null;
					tmp = adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, uuid);
					mmServerSocket = tmp;
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					//e.printStackTrace();
					Helper.jError("Could not start Bluetooth Server: " + e.getMessage());
					break;
				}
				// If a connection was accepted
				if (socket != null) {
					// Do work to manage the connection (in a separate thread)
					ServerConnection connection = new ServerConnection(adapter, socket);
					connectedThreads.add(connection);
					estListener.connectionEstablished(connection);
					connection.connect();
					
					try {
						mmServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// break;
				}
			}
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
