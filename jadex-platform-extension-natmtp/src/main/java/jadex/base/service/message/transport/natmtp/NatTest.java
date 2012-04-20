package jadex.base.service.message.transport.natmtp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.zip.GZIPInputStream;

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
		
		URL	url	= new URL("http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz");
		HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
		System.out.println("Date: "+new Date(con.getLastModified()));
		InputStream	is	= new GZIPInputStream(con.getInputStream());
		OutputStream	os	= new FileOutputStream(new File("./GeoLiteCity.dat"));
		byte[]	buf	= new byte[8192];
		int read;
		while((read=is.read(buf))!=-1)
		{
			os.write(buf, 0, read);
		}
		os.close();
	}
}
