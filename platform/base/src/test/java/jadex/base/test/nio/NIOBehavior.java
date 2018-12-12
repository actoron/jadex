package jadex.base.test.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Trying to understand NIO behavior.
 */
public class NIOBehavior
{
	protected static final Random	rnd	= new Random();
	
	public static void main(String[] args) throws Exception
	{
		Selector	selector	= Selector.open();
		SocketChannel	sc	= SocketChannel.open();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_CONNECT);
//		sc.connect(new InetSocketAddress("google.de", 80));
		sc.connect(new InetSocketAddress("192.168.1.144", 8080));
		byte[]	bytes	= new byte[12345];
		rnd.nextBytes(bytes);
		ByteBuffer	buf	= ByteBuffer.wrap(bytes);
		while(selector.isOpen())
		{
			selector.select();
			Set<SelectionKey>	sk	= selector.selectedKeys();
			for(Iterator<SelectionKey> it=sk.iterator(); it.hasNext(); )
			{
				SelectionKey	key	= it.next();
				if(key.isConnectable())
				{
					System.out.println("connecting...");
					sc.finishConnect();
					System.out.println("connected");
					key.interestOps(SelectionKey.OP_WRITE);
				}
				else if(key.isWritable())
				{
					System.out.println("writing...");
					while(buf.remaining()>0 && sc.write(buf)>0);
					
					if(buf.remaining()>0)
					{
						System.out.println("waiting for write...");
					}
					else
					{
						System.out.println("written "+bytes.length+" bytes");
						key.interestOps(SelectionKey.OP_READ);
					}
				}
				else if(key.isReadable())
				{
					System.out.println("reading...");
					buf.clear();
					int	read	= sc.read(buf);
					if(read==-1)
					{
						System.out.println("end of channel");
						selector.close();
					}
					else
					{
						System.out.println("read "+read+" bytes");
					}
				}
			}
		}
	}
}
