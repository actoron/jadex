package jadex.base.service.awareness.discovery.bluetoothp2p;

import jadex.android.bluetooth.service.IConnectionCallback;
import jadex.android.bluetooth.util.Helper;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.ReceiveHandler;

import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;
import android.util.Log;

/**
 * 
 */
public class BluetoothP2PReceiveHandler extends ReceiveHandler
{
	/** The receive buffer. */
	protected byte[] buffer;
	
	protected IConnectionCallback callback;
	
	private List<String> deviceList;
	
	/**
	 *  Create a new receive handler.
	 */
	public BluetoothP2PReceiveHandler(DiscoveryAgent agent)
	{
		super(agent);
		deviceList = new ArrayList<String>();
		callback = new IConnectionCallback.Stub() {
			
			@Override
			public void deviceListChanged() throws RemoteException {
				Log.d(Helper.LOG_TAG, "BluetoothP2PReceiver: devicelistcahnged!");
			}
		};
	}
	
	/**
	 *  Receive a packet.
	 */
	public Object[] receive()
	{
		
		while (deviceList.isEmpty()) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
			}
		}
		
		
		Object[] ret = null;
//		try
//		{
//
//			if(buffer==null)
//			{
//				// todo: max ip datagram length (is there a better way to determine length?)
//				buffer = new byte[8192];
//			}
//
//			final DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
//			getAgent().getSocket().receive(pack);
//			byte[] data = new byte[pack.getLength()];
//			System.arraycopy(buffer, 0, data, 0, pack.getLength());
//			ret = new Object[]{pack.getAddress(), new Integer(pack.getPort()), data};
//		}
//		catch(Exception e)
//		{
////			getAgent().getMicroAgent().getLogger().warning("Message receival error: "+e);
//		}
//		
		return ret;
	}
	
	/**
	 *  Get the agent.
	 */
	public BluetoothP2PDiscoveryAgent getAgent()
	{
		return (BluetoothP2PDiscoveryAgent)agent;
	}
}

