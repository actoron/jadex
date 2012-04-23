package jadex.base.service.message.transport.natmtp;

import org.hyperic.sigar.NetConnection;
import org.hyperic.sigar.Sigar;

public class NatTest
{
	public static void main(String[] args) throws Exception
	{
//		Socket	sock	= new Socket();
//		sock.connect(new InetSocketAddress("vsisstaff0.informatik.uni-hamburg.de", 54321), 30000);
		Sigar	sigar	= new Sigar();
		// Flags ???
		NetConnection[]	cons	= sigar.getNetConnectionList(Integer.MAX_VALUE);
		for(int i=0; i<cons.length; i++)
		{
			System.out.println(cons[i]);
		}
	}
}
