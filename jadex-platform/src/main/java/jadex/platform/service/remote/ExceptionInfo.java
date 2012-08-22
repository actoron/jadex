package jadex.platform.service.remote;

import jadex.bridge.service.types.remote.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception info for transferring an exception. 
 */
public class ExceptionInfo
{
	//-------- attributes --------
	
	/** The exception type. */
	// todo: make class info
	protected Class type;
	
	/** The message. */
	protected String message;
	
	/** The strack trace elements. */
	protected List stacktrace;
	
	//-------- constructors --------
	
	/**
	 *  Create a new exception info.
	 */
	public ExceptionInfo()
	{
	}
	
	/**
	 *  Create a new exception info.
	 */
	public ExceptionInfo(Exception ex)
	{
//		ex.printStackTrace();
		
		// todo: possibly add support for saving all nested causes.
		
		// Strip remote exception to conserve real exception
		this.type = ex instanceof RemoteException? ((RemoteException)ex).getType(): ex.getClass();
		this.message = ex.getMessage();
		StackTraceElement[] stes =  ex.getStackTrace();
		if(stes!=null)
		{
			for(int i=0; i<stes.length; i++)
			{
				addStackTrace(new Object[]{stes[i].getClassName(), stes[i].getMethodName(),
					stes[i].getFileName(), new Integer(stes[i].getLineNumber())});
			}
		}
	}

	//-------- methods --------
	
	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public Class getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class type)
	{
		this.type = type;
	}

	/**
	 *  Get the message.
	 *  @return the message.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 *  Set the message.
	 *  @param message The message to set.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 *  Get the stacktrace.
	 *  @return the stacktrace.
	 */
	public List getStackTrace()
	{
		return stacktrace;
	}

	/**
	 *  Set the stacktrace.
	 *  @param stacktrace The stacktrace to set.
	 */
	public void setStackTrace(List stacktrace)
	{
		this.stacktrace = stacktrace;
	}

	/**
	 *  Add a stack trace element.
	 */
	public void addStackTrace(Object[] ste)
	{
		if(stacktrace==null)
			stacktrace = new ArrayList();
		this.stacktrace.add(ste);
	}
	
	/**
	 *  Recreate the remote exception.
	 */
	public RemoteException recreateException()
	{
		RemoteException ret = new RemoteException(type, message);
		if(stacktrace!=null)
		{
			StackTraceElement[] stes = new StackTraceElement[stacktrace.size()];
			for(int i=0; i<stes.length; i++)
			{
				Object[] tmp = (Object[])stacktrace.get(i); 
				stes[i] = new StackTraceElement((String)tmp[0], (String)tmp[1], 
					(String)tmp[2], ((Number)tmp[3]).intValue()); 
			}
			ret.setStackTrace(stes);
		}
		return ret;
	}
	
	/**
	 *  Get as string.
	 */
	public String toString()
	{
		return recreateException().toString();
	}
}
