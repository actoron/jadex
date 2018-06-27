package jadex.platform.service.awareness.discovery.bluetoothp2p;

import jadex.android.AndroidContextManager;
import jadex.android.AndroidContextManager.AndroidContextChangeListener;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.service.ConnectionService;
import jadex.android.bluetooth.service.IBTP2PAwarenessInfoCallback;
import jadex.android.bluetooth.service.IConnectionServiceConnection;
import jadex.android.bluetooth.util.Helper;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.ReceiveHandler;
import jadex.platform.service.awareness.discovery.SendHandler;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 *  Agent that uses the Bluetooth Connection Service to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the bluetooth neighbourhood.")
@Arguments(
{
//	@Argument(name="address", clazz=String.class, defaultvalue="\"224.0.0.0\"", description="The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255)."),
//	@Argument(name="port", clazz=int.class, defaultvalue="55667", description="The port used for finding other agents."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior).")
})
@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000")),
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000")),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"))
})
@Properties(@NameValue(name="system", value="true"))
public class BluetoothP2PDiscoveryAgent extends DiscoveryAgent implements AndroidContextChangeListener
{

	private Intent intent;
	private IConnectionServiceConnection binder;
	protected IBluetoothDevice[] _knownDevices;
	private Context context;
	
	/**
	 * Constructor
	 */
	public BluetoothP2PDiscoveryAgent() {
		intent = new Intent();
		_knownDevices = new IBluetoothDevice[0];
		AndroidContextManager.getInstance().addContextChangeListener(this);
		//intent.setClassName("jadex.android.bluetooth.service", "jadex.android.bluetooth.service.ConnectionService");
	}

	@Override
	public SendHandler createSendHandler() {
		sender = new BluetoothP2PSendHandler(this);
		return sender;
	}

	@Override
	public ReceiveHandler createReceiveHandler() {
		receiver = new BluetoothP2PReceiveHandler(this);
		return receiver;
	}

	@Override
	public void onContextCreate(Context ctx) {
		Log.d(Helper.LOG_TAG, "(BTP2PDiscoveryAgent) Context created");
		context = ctx;
		if (isStarted()) {
			intent = new Intent(context, ConnectionService.class);
			Log.d(Helper.LOG_TAG, "(BTP2PDiscoveryAgent) Trying to bind BT Service...");
			boolean bindService = context.bindService(intent, sc, Activity.BIND_AUTO_CREATE);
			if (!bindService) {
				Log.e(Helper.LOG_TAG, "(BTP2PDiscoveryAgent) Could not bind Service, maybe it's not declared correctly?");
			}
		}
	}
	
	@Override
	public void setStarted(boolean started) {
		Log.d(Helper.LOG_TAG, "(BTP2PDiscoveryAgent) started");
		super.setStarted(started);
		if (context != null) {
			onContextCreate(context);
		}
	}
	
	@Override
	public void onContextDestroy(Context ctx) {
		if (isStarted() && context == ctx && binder != null) {
			context.unbindService(sc);
			context = null;
		}
	}
	
	@Override
	protected void initNetworkRessource() {
		if (context != null && binder == null) {
			//context.startService(intent);
			intent = new Intent(context, ConnectionService.class);
			Log.d(Helper.LOG_TAG, "(BTP2PDiscoveryAgent) Trying to bind BT Service...");
			boolean bindService = context.bindService(intent, sc, Activity.BIND_AUTO_CREATE);
			if (!bindService) {
				Log.e(Helper.LOG_TAG, "(BTP2PDiscovery) Could not bind Service, maybe it's not declared correctly?");
			}
		}
	}

	@Override
	protected void terminateNetworkRessource() {
		if (binder != null && context != null) {
			try {
				Log.d(Helper.LOG_TAG, "Stopping autoconnect...");
				binder.stopAutoConnect();
				binder.stopBTServer();
			} catch (RemoteException e) {
				e.printStackTrace();
			} finally {
				binder = null;
				Log.d(Helper.LOG_TAG, "Unbinding Service...");
				context.unbindService(sc);
			}
		}
	}
	
	private IBTP2PAwarenessInfoCallback.Stub awarenessCallback = new IBTP2PAwarenessInfoCallback.Stub() {
		
		@Override
		public void knownDevicesChanged(IBluetoothDevice[] knownDevices)
		throws RemoteException {
			_knownDevices = knownDevices;
		}
		
		@Override
		public void awarenessInfoReceived(byte[] data) throws RemoteException {
			BluetoothP2PReceiveHandler btrec = (BluetoothP2PReceiveHandler) receiver;
			btrec.addReceivedAwarenessInfo(data);
		}
	};
	
	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			binder = null;
		}
	
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			binder = IConnectionServiceConnection.Stub.asInterface(arg1);
			Log.d(Helper.LOG_TAG, "(BTP2PDiscovery) Service bound! starting autoconnect...");
			try {
				binder.registerAwarenessInfoCallback(awarenessCallback);
				binder.startAutoConnect();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * Sends Awareness Info to all (by the Connection service) known devices.
	 * @param data
	 */
	public void sendAwarenessInfo(byte[] data) {
		BluetoothMessage btMsg = new BluetoothMessage("", data, DataPacket.TYPE_AWARENESS_INFO);
		for (IBluetoothDevice d : _knownDevices) {
			btMsg.setRemoteAddress(d.getAddress());
			try {
				binder.sendMessage(btMsg);
			} catch (RemoteException e) {
			}
		} 
	}
}
