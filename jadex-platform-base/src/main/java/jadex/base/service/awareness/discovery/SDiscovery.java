package jadex.base.service.awareness.discovery;

import jadex.base.service.message.transport.codecs.GZIPCodec;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
/* $if !android $ */
import java.net.InterfaceAddress;
/* $endif $ */
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

/**
 * 
 */
public class SDiscovery
{
	/**
	 *  Encode an object.
	 *  @param object The object.
	 *  @return The byte array.
	 */
	public static byte[] encodeObject(Object object, ClassLoader classloader)
	{
		return GZIPCodec.encodeBytes(JavaWriter.objectToByteArray(object, 
			classloader), classloader);
	}
	
	/**
	 *  Decode an object.
	 *  @param data The byte array.
	 *  @return The object.
	 */
	public static Object decodeObject(byte[] data, ClassLoader classloader)
	{
		return JavaReader.objectFromByteArray(GZIPCodec.decodeBytes(data, 
			classloader), classloader);
//		return Reader.objectFromByteArray(reader, GZIPCodec.decodeBytes(data, 
//			classloader), classloader);
	}
	
	/**
	 *  Decode a datagram packet.
	 *  @param data The byte array.
	 *  @return The object.
	 */
	public static Object decodePacket(DatagramPacket pack, ClassLoader classloader)
	{
		byte[] data = new byte[pack.getLength()];
		System.arraycopy(pack.getData(), 0, data, 0, pack.getLength());
		return decodeObject(data, classloader);
	}
	
	/**
	 *  Get a IPV4 address of the local host.
	 *  Ignores loopback address and V6 addresses.
	 *  @return First found IPV4 address.
	 */
	public static InetAddress getInet4Address()
	{
		InetAddress ret = null;
		
		try
		{
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements() && ret==null)
			{
				NetworkInterface ni = (NetworkInterface)e.nextElement();
				Enumeration e2 = ni.getInetAddresses();
				while(e2.hasMoreElements() && ret==null)
				{
					InetAddress tmp = (InetAddress)e2.nextElement();
					if(tmp instanceof Inet4Address && !tmp.isLoopbackAddress())
						ret = (InetAddress)tmp;
				}
			}
			
			if(ret==null)
			{
				InetAddress tmp = InetAddress.getLocalHost();
				if(tmp instanceof Inet4Address && !tmp.isLoopbackAddress())
					ret = (InetAddress)tmp;
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Get the network prefix length for IPV4 address
	 *  24=C, 16=B, 8=A classes. 
	 *  Returns -1 in case of V6 address.
	 *  @param iadr The address.
	 *  @return The length of the prefix.
	 */
	public static short getNetworkPrefixLength(InetAddress iadr)
	{
		short ret = -1;
		/* $if !android $ */
		try
		{
			NetworkInterface ni = NetworkInterface.getByInetAddress(iadr);
			List iads = ni.getInterfaceAddresses();
			if(iads!=null)
			{
				for(int i=0; i<iads.size() && ret==-1; i++)
				{
					InterfaceAddress ia = (InterfaceAddress)iads.get(i);
					if(ia.getAddress() instanceof Inet4Address)
						ret = ia.getNetworkPrefixLength();
				}
			}
			
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		/* $endif $ */
		
		return ret;
	}
}
