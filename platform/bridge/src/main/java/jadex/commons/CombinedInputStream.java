package jadex.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jadex.commons.concurrent.IThreadPool;

/**
 *  A combined input stream allows for combining two input streams in one.
 *  
 *  As input streams are blocking two threads are necessary to wait for data 
 *  and pump it into an underlying output stream that is piped to the resulting
 *  input stream.
 *  
 *  In order to avoid messed up input in the resulting stream readLine() is used
 *  as read/write unit. 
 */
class CombinedInputStream extends PipedInputStream
{
	//-------- attributes --------
	
	/** The first input stream. */
	protected InputStream in1;

	/** The second input stream. */
	protected InputStream in2;
	
	/** The first input stream. */
	protected PipedOutputStream outin;

	/** The output stream that is piped to the result input stream. */
	protected PipedOutputStream out;
	
	/** The thread pool. */
	protected IThreadPool	tp;
	
	/** The first thread. */
	protected List<Thread> threads;
	
	/** Closed flag. */
	protected boolean closed;
	
	//-------- constructors --------
	
	/**
	 *  Create a new combined input stream.
	 *  @param in1 The first input stream.
	 *  @param outin The second stream that can be used to write 
	 *    data that is pumped as input to the input stream.
	 *  @throws IOException if streams cannot be opened.
	 */
	public CombinedInputStream(final InputStream in1, final PipedOutputStream outin, 
		IThreadPool tp) throws IOException
	{
		this.tp	= tp;
		this.threads = Collections.synchronizedList(new ArrayList<Thread>());
		this.in1 = in1;
		this.outin = outin;
		this.in2 = new PipedInputStream(outin);
		this.out = new PipedOutputStream();
		connect(out);
		
		if(tp!=null)
		{
			tp.execute(new Reader(in1, out));
			tp.execute(new Reader(in2, out));
		}
		else
		{
			Thread t1 = new Thread(new Reader(in1, out));
			Thread t2 = new Thread(new Reader(in2, out));
			t1.start();
			t2.start();
		}
	}
	
	/**
	 *  Create a new combined input stream.
	 *  @param in1 The first input stream.
	 *  @param outin The second stream that can be used to write 
	 *    data that is pumped as input to the input stream.
	 *  @throws IOException if streams cannot be opened.
	 */
	public CombinedInputStream(final InputStream in1, final InputStream in2, 
		IThreadPool tp) throws IOException
	{
		this.in1 = in1;
		this.in2 = in2;
		this.out = new PipedOutputStream();
		connect(out);
		
		if(tp!=null)
		{
			tp.execute(new Reader(in1, out));
			tp.execute(new Reader(in2, out));
		}
		else
		{
			Thread t1 = new Thread(new Reader(in1, out));
			Thread t2 = new Thread(new Reader(in2, out));
			t1.start();
			t2.start();
		}
	}

	//-------- methods --------
	
	/**
	 *  Get the original in.
	 *  @return The in.
	 */
	public InputStream getIn()
	{
		return in1;
	}
	
	/**
	 *  Get the outin.
	 *  @return The outin.
	 */
	public PipedOutputStream getOutin()
	{
		return outin;
	}
	
	/**
	 *  Close the stream 
	 */
	public void close()
	{
		closed = true;
		Thread[] ts = threads.toArray(new Thread[0]);
		for(Thread t: ts)
		{
			t.interrupt();
		}
		try
		{
			if(in1!=null)
				in1.close();
		}
		catch(Exception e)
		{
		}
		try
		{
			if(in2!=null)
				in2.close();
		}
		catch(Exception e)
		{
		}
		try
		{
			if(outin!=null)
				outin.close();
		}
		catch(Exception e)
		{
		}
		try
		{
			if(out!=null)
				out.close();
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 *  Reader to pump data from the input to the output stream.
	 */
	class Reader implements Runnable
	{
		/** The input stream. */
		protected InputStream in;
		
		/** The output stream. */
		protected OutputStream out;

		/**
		 *  Create a new reader.
		 */
		public Reader(InputStream in, OutputStream out)
		{
			this.in = in;
			this.out = out;
		}
		
		/**
		 *  The run method that reads in and writes out.
		 */
		public void run()
		{
			threads.add(Thread.currentThread());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			try
			{
				while(tp.isRunning() && !closed)
				{
					if(br.ready())
					{
						String	line	= br.readLine();
						if(line==null)	// null means end of stream
						{
							break;
						}
						byte[] data = (line+SUtil.LF).getBytes();
						synchronized(out)
						{
	//						System.out.println("wrote to comb is: "+new String(data));
							out.write(data);	
						}
					}
					else
					{
						Thread.sleep(500);
					}
				}
			}
			catch(Exception e)
			{
//				e.printStackTrace();
			}
//			System.out.println("exit: "+in);
		}
	}
}



