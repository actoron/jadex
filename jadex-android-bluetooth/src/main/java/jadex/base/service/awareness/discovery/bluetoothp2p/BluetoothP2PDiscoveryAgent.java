package jadex.base.service.awareness.discovery.bluetoothp2p;

import jadex.android.bluetooth.JadexBluetoothActivity;
import jadex.android.bluetooth.service.ConnectionService;
import jadex.android.bluetooth.service.IConnectionServiceConnection;
import jadex.android.bluetooth.util.Helper;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.DiscoveryService;
import jadex.base.service.awareness.discovery.IDiscoveryService;
import jadex.base.service.awareness.discovery.ReceiveHandler;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.base.service.awareness.management.IManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.net.DatagramPacket;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(
{
	@Argument(name="address", clazz=String.class, defaultvalue="\"224.0.0.0\"", description="The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255)."),
	@Argument(name="port", clazz=int.class, defaultvalue="55667", description="The port used for finding other agents."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior).")
})
@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000")),
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000")),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"))
})
@ProvidedServices(
	@ProvidedService(type=IDiscoveryService.class, implementation=@Implementation(DiscoveryService.class))
)
@RequiredServices(
{
	@RequiredService(name="threadpool", type=IThreadPoolService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="management", type=IManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class BluetoothP2PDiscoveryAgent extends DiscoveryAgent
{

	private Intent intent;
	private IConnectionServiceConnection binder;
	
	public BluetoothP2PDiscoveryAgent() {
		intent = new Intent();
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
	protected void initNetworkRessource() {
		Context context = JadexBluetoothActivity.application_context;
		if (context != null) {
			//context.startService(intent);
			intent = new Intent(context, ConnectionService.class);
			Log.d(Helper.LOG_TAG, "Trying to bind BT Service...");
			context.bindService(intent, sc, Activity.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void terminateNetworkRessource() {
		Context context = JadexBluetoothActivity.application_context;
		if (binder != null && context != null) {
			try {
				Log.d(Helper.LOG_TAG, "Stopping autoconnect...");
				binder.stopAutoConnect();
			} catch (RemoteException e) {
				e.printStackTrace();
			} finally {
				binder = null;
				Log.d(Helper.LOG_TAG, "Unbinding Service...");
				context.unbindService(sc);
			}
		}
	}
	
	private ServiceConnection sc = new ServiceConnection() {


		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			binder = null;
		}
	
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			binder = IConnectionServiceConnection.Stub.asInterface(arg1);
			Log.d(Helper.LOG_TAG, "Service bound! starting autoconnect...");
			try {
				binder.registerCallback(((BluetoothP2PReceiveHandler)receiver).callback);
				binder.startAutoConnect();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	
	public void send(DatagramPacket p) {
		
	}
}
