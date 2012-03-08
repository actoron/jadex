package jadex.base.service.message;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import jadex.bridge.IInputConnection;
import jadex.commons.future.IFuture;

/**
 * 
 */
public class InputConnection implements IInputConnection
{
	/** Boolean flag if connection is closed. */
	protected boolean closed;
	
	/** The connection id. */
	protected int id;
	
	/** The data. */
	protected List<byte[]> data;
	
	/** The position. */
	protected int position;
	
	/**
	 * 
	 */
	public InputConnection(int id)
	{
		this.id = id;
		this.data = new ArrayList<byte[]>();
	}
	
	/**
	 * 
	 */
	public IFuture<Void> read(byte[] buffer)
	{
		int startpos = 0;
		int rowcnt = 0;
		for(; rowcnt<data.size(); rowcnt++)
		{
			byte[] row = data.get(rowcnt);
			if(position<startpos+row.length)
				break;
			startpos += row.length;
		}
		
		if(rowcnt<data.size())
		{
			byte[] row = data.get(rowcnt);
			int inrowstart = position-startpos;
			int buffercnt = 0;
			for(; buffercnt<buffer.length;)
			{
				buffer[buffercnt++] = row[inrowstart];
				inrowstart++;
				if(inrowstart>row.length)
				{
					inrowstart = 0;
					if(++rowcnt<data.size())
					{
						row = data.get(rowcnt);
					}
				}
				else
				{
					break;
				}
			}
			position += buffercnt;
		}
		else if(closed)
		{
			throw new RuntimeException("End of stream reached.");
		}
		// todo: support blocking version?
		
//		if(buffercnt==0 && closed)
//			return new Future(new RuntimeException());
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> close()
	{
		// todo:
		return IFuture.DONE;
	}
	
	//-------- methods called from message service --------
	
	/**
	 * 
	 */
	public void addData(byte[] data)
	{
		this.data.add(data);
	}
	
	/**
	 * 
	 */
	public void setClosed()
	{
		this.closed = true;
	}
}
