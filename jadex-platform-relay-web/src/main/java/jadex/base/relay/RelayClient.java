package jadex.base.relay;

import jadex.xml.bean.JavaWriter;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *  Test for the relay servlet.
 *  Just prints out the objects it receives.
 */
public class RelayClient
{
	/**
	 *  Main method.
	 */
	public static void	main(String[] args) throws Exception
	{
		int	cnt	= 0;
		while(true)
		{
			String	id	= args.length>0 ? args[0] : InetAddress.getLocalHost().getHostName();
			System.out.println("Connecting as: "+id);
			String	xmlid	= JavaWriter.objectToXML(id, RelayClient.class.getClassLoader());
			URL	url	= new URL("http://localhost:8080/jadex-applications-web/relay/?id="+URLEncoder.encode(xmlid, "UTF-8"));
			System.out.println("Connecting to: "+url);
			URLConnection	con	= url.openConnection();
			con.setUseCaches(false);
			InputStream	in	= con.getInputStream();
			try
			{
				while(true)
				{
					Object	obj	= SRelay.readObject(in);
					System.out.println(id+" received ("+(++cnt)+"): "+obj);
				}
			}
			catch(Exception e)
			{
				System.out.println(id+" got exception: "+e);
			}
		}
	}
}
