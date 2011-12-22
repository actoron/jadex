package jadex.base.service.awareness.discovery.bluetoothp2p;

import jadex.android.bluetooth.util.Helper;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import android.util.Log;

/**
 *  Handle sending.
 */
public class BluetoothP2PSendHandler extends SendHandler
{
	/**
	 *  Create a new lease time handling object.
	 */
	public BluetoothP2PSendHandler(DiscoveryAgent agent)
	{
		super(agent);
		Log.d(Helper.LOG_TAG, "BluetoothP2PSendHandler created.");
	}
	
	/**
	 *  Method to send messages.
	 */
	@Override
	public void send(AwarenessInfo info)
	{
//		Log.d(Helper.LOG_TAG, "BluetoothP2PSendHandler: sending Awareness Info");
		byte[] data = DiscoveryAgent.encodeObject(createAwarenessInfo(), getAgent().getMicroAgent().getClassLoader());
		getAgent().sendAwarenessInfo(data);
	}
	
	/**
	 *  Send a packet.
	 */
//	public boolean send(byte[] data, InetAddress address, int port)
//	{
////		System.out.println("sent packet: "+address+" "+port);
//		boolean ret = true;
//		try
//		{
//			DatagramPacket p = new DatagramPacket(data, data.length, new InetSocketAddress(address, port));
//			getAgent().send(data, address
//		}
//		catch(Exception e)
//		{
//			ret = false;
//		}
//		return ret;
//	}
	
	/**
	 *  Get the agent.
	 */
	protected BluetoothP2PDiscoveryAgent getAgent()
	{
		return (BluetoothP2PDiscoveryAgent)agent;
	}
}