package com.actoron.webservice.messages;

/**
 * 
 */
public class ResultMessage extends BaseMessage
{
	/** The result. */
	protected Object result;
	
	/** The exception. */
	protected Exception exception;

	/** The finished flag. */
	protected boolean finished;
	
	/**
	 *  Create a new result message.
	 */
	public ResultMessage()
	{
	}
	
	/**
	 *  Create a new result message.
	 */
	public ResultMessage(Object result, String callid, boolean finished)
	{
		super(callid);
		this.result = result;
		this.finished = finished;
	}
	
	/**
	 *  Create a new result message.
	 */
	public ResultMessage(Exception exception, String callid)
	{
		super(callid);
		this.exception = exception;
		this.finished = true;
	}

	/** 
	 *  Get the result.
	 *  @return Tthe result
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}

	/** 
	 *  Get the exception.
	 *  @return Tthe exception
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	/** 
	 *  Get the finished.
	 *  @return Tthe finished
	 */
	public boolean isFinished()
	{
		return finished;
	}

	/**
	 *  Set the finished.
	 *  @param finished The finished to set
	 */
	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}
}
