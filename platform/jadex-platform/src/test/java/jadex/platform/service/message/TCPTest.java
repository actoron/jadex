package jadex.platform.service.message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPTest
{
	public static void main(String[] args) throws Exception
	{
		if(args.length==2)
		{
			String	address	= args[0];
			File	f	= new File(args[1]);
			if(!f.exists())
			{
				System.out.println("Error: File does not exist.");				
			}
			else
			{
				Socket	sock	= new Socket(address, 12345);
				OutputStream	os	= new BufferedOutputStream(sock.getOutputStream());
				InputStream	is	= new BufferedInputStream(new FileInputStream(f));
				byte[]	buf	= new byte[8192*4];
				int	len;
				while((len=is.read(buf)) != -1)
				{
					os.write(buf, 0, len);
				}
				os.flush();
				os.close();
				is.close();
				sock.close();
			}
		}
		else if(args.length==1)
		{
			File	f	= new File(args[0]);
//			if(f.exists())
//			{
//				System.out.println("Error: File already exists.");				
//			}
//			else
//			{
				ServerSocket	server	= new ServerSocket(12345);
				Socket	sock	= server.accept();
				InputStream	is	= new BufferedInputStream(sock.getInputStream());
				OutputStream	os	= new BufferedOutputStream(new FileOutputStream(f));
				byte[]	buf	= new byte[8192*4];  
				int	len;
				while((len=is.read(buf)) != -1)
				{
					os.write(buf, 0, len);
				}
				os.flush();
				os.close();
				is.close();
				sock.close();
				server.close();
//			}
		}
		else
		{
			System.out.println("Usage (Send): TCPTest <address> <filename>");
			System.out.println("Usage (Receive): TCPTest <filename>");
		}
	}
}
