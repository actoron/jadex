package jadex.commons;

import java.util.ArrayList;
import java.util.List;

/**
 *  An exception that can store multiple causes.
 */
public class MultiException extends RuntimeException
{
	/** The exceptions. */
	protected List<Throwable> causes;
	
	/** The message. */
	protected String message;
	
	/**
	 *  Create a new multi exception.
	 */
	public MultiException()
	{
	}
	
	/**
	 *  Create a new multi exception.
	 */
	public MultiException(String message)
	{
		this(message, null);
	}
	
	/**
	 *  Create a new multi exception.
	 */
	public MultiException(List<Throwable> causes)
	{
		this(null, causes);
	}
	
	/**
	 *  Create a new multi exception.
	 */
	public MultiException(String message, List<Throwable> causes)
	{
		this.message = message;
		this.causes = causes;
	}
	
	/**
	 *  Add an exception.
	 *  @param cause The cause.
	 */
	public synchronized void addCause(Throwable cause)
	{
		if(causes==null)
			causes = new ArrayList<Throwable>();
		causes.add(cause);
	}
	
	/**
	 *  Get the causes.
	 *  @return The causes.
	 */
	public synchronized Throwable[] getCauses()
	{
		return causes==null? new Throwable[0]: causes.toArray(new Throwable[causes.size()]);
	}
	
	 /**
     * Returns the detail message string of this throwable.
     * @return  The detail message string of this {@code Throwable} instance
     *          (which may be {@code null}).
     */
    public synchronized String getMessage() 
    {
    	StringBuffer ret = new StringBuffer();
    	if(message!=null)
    		ret.append(message+": ");
    	if(causes!=null)
    	{
    		ret.append("caused by: ");
    		for(int i=0; i<causes.size(); i++)
    		{
    			Throwable cause = causes.get(i);
    			ret.append(cause.getMessage());
    			if(i+1<causes.size())
    				ret.append(", ");
    		}
    	}
    	return ret.toString();
    }
    
    /**
     *  Set the message.
     *  @param message The message.
     */
    public synchronized void setText(String message)
    {
    	this.message = message;
    }
}
