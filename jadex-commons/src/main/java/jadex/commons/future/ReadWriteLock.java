package jadex.commons.future;

import jadex.commons.IResultCommand;

import java.util.LinkedList;

/**
 *  Locking class implementing RX-style operation:
 *  Multiple reads are allowed to be performed at the same time
 *  but write operations are exclusive.
 *  Scheduled write operations delay later read operations to
 *  prevent starvation.
 *
 */
public class ReadWriteLock
{
	/** Number of read operations in progress */
	protected int reads = 0;
	
	/** Flag of the write operation in progress */
	protected boolean write = false;
	
	/** Backlog of read requests */
	protected LinkedList<IResultCommand<IFuture<Void>, Void>> readbacklog = new LinkedList<IResultCommand<IFuture<Void>, Void>>();
	
	/** Backlog of write requests */
	protected LinkedList<IResultCommand<IFuture<Void>, Void>> writebacklog = new LinkedList<IResultCommand<IFuture<Void>, Void>>();
	
	public void scheduleRead(IResultCommand<IFuture<Void>, Void> readcommand)
	{
		if (write || !writebacklog.isEmpty())
		{
			readbacklog.add(readcommand);
		}
		else
		{
			++reads;
			readcommand.execute(null).addResultListener(new ActionPerformedListener(true));
		}
	}
	
	/**
	 *  Schedules a write operation. 
	 *  
	 *  @param writecommand The write operation.
	 */
	public void scheduleWrite(IResultCommand<IFuture<Void>, Void> writecommand)
	{
		if (reads > 0 || write)
		{
			writebacklog.add(writecommand);
		}
		else
		{
			write = true;
			writecommand.execute(null).addResultListener(new ActionPerformedListener(false));
		}
	}
	
	protected class ActionPerformedListener implements IResultListener<Void>
	{
		/** Flag indicating a read operation. */
		protected boolean readop;
		
		/**
		 *  Creates the listener.
		 *  
		 *  @param readop Flag indicating a read operation.
		 */
		public ActionPerformedListener(boolean readop)
		{
			this.readop = readop;
		}
		
		/**
		 * 
		 */
		public void resultAvailable(Void result)
		{
			if (readop)
			{
				--reads;
			}
			else
			{
				write = false;
			}
			
			if (reads == 0 && !writebacklog.isEmpty())
			{
				readop = false;
				write = true;
				writebacklog.remove().execute(null).addResultListener(this);
			}
			else if (writebacklog.isEmpty() && !readbacklog.isEmpty())
			{
				readop = true;
				while (!readbacklog.isEmpty())
				{
					++reads;
					readbacklog.remove().execute(null).addResultListener(this);
				}
			}
		}
		
		/**
		 * 
		 */
		public void exceptionOccurred(Exception exception)
		{
			resultAvailable(null);
		}
	}
}
