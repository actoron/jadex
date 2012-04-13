package jadex.micro.examples.chat;

import java.io.File;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 * 
 */
public class FileInfo
{
	public static final String WAITING = "Waiting";
	public static final String REJECTED = "Rejected";
	public static final String TRANSFERRING = "Transferring";
	public static final String COMPLETED = "Completed";
	public static final String ABORTED = "Aborted";
	public static final String ERROR = "Error";
	
	/** The idcnt. */
	protected static int idcnt;
	
	/** The id. */
	protected int id;
	
	/** The name. */
	protected File file;
	
	/** The sender. */
	protected IComponentIdentifier sender;
	
	/** The size. */
	protected long size;
	
	/** The done size. */
	protected long done;
	
	/** The state. */
	protected String state;
	
	/** The cancel command. */
	protected Runnable cancelcommand;

	/**
	 * @param file
	 * @param sender
	 * @param size
	 * @param done
	 */
	public FileInfo(File file, IComponentIdentifier sender, long size, long done, String state)
	{
		synchronized(FileInfo.class)
		{
			this.id = idcnt++;
		}
		setState(state);
		this.file = file;
		this.sender = sender;
		this.size = size;
		this.done = done;
	}

	/**
	 *  Get the file.
	 *  @return the file.
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 *  Set the file.
	 *  @param file The file to set.
	 */
	public void setFile(File file)
	{
		this.file = file;
	}

	/**
	 *  Get the sender.
	 *  @return the sender.
	 */
	public IComponentIdentifier getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender to set.
	 */
	public void setSender(IComponentIdentifier sender)
	{
		this.sender = sender;
	}

	/**
	 *  Get the size.
	 *  @return the size.
	 */
	public long getSize()
	{
		return size;
	}

	/**
	 *  Set the size.
	 *  @param size The size to set.
	 */
	public void setSize(long size)
	{
		this.size = size;
	}

	/**
	 *  Get the done.
	 *  @return the done.
	 */
	public long getDone()
	{
		return done;
	}

	/**
	 *  Set the done.
	 *  @param done The done to set.
	 */
	public void setDone(long done)
	{
		this.done = done;
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 *  Get the state.
	 *  @return the state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		if(!WAITING.equals(state) && !TRANSFERRING.equals(state) && !COMPLETED.equals(state) 
			&& !ABORTED.equals(state) && !ERROR.equals(state) && !REJECTED.equals(state))
		{
			throw new RuntimeException("Unknown state: "+state);
		}
		if(ERROR.equals(state))
			System.out.println("herehhh");
		this.state = state;
	}

	/**
	 * 
	 */
	public void cancel()
	{
		if(!isFinished())
		{
			if(cancelcommand!=null)
			{
				cancelcommand.run();
				cancelcommand = null;
			}
			setState(FileInfo.ABORTED);
		}
	}
	
//	/**
//	 *  Get the cancel command.
//	 *  @return the cancel command.
//	 */
//	public Runnable getCancelCommand()
//	{
//		return cancelcommand;
//	}
	
	/**
	 *  Set the cancel command.
	 *  @param cancel command The cancel command to set.
	 */
	public void setCancelCommand(Runnable cancelcommand)
	{
		this.cancelcommand = cancelcommand;
	}

	/**
	 * 
	 */
	public boolean isFinished()
	{
		return COMPLETED.equals(state) || ABORTED.equals(state) || ERROR.equals(state) || REJECTED.equals(state);
	}
	
	/**
	 * 
	 */
	public int hashCode()
	{
		return id;
	}

	/**
	 * 
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof FileInfo)
			ret = ((FileInfo)obj).getId()==getId();
		return ret;
	}
	
}