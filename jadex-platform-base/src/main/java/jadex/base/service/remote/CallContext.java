package jadex.base.service.remote;

import jadex.commons.Future;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class CallContext
{
	//-------- attributes --------
	
	/** The map of waiting calls (callid -> future). */
	protected Map waitingcalls;
	
	/** The map of target objects (id -> target object). */
	protected Map targetobjects;

//	/** The map of target ids (target object -> id). */
//	protected Map targetids;
	
	/** The rmi object to xml writer. */
	protected Writer writer;
	
	/** The rmi xml to object reader. */
	protected Reader reader;

	
	/** The id counter. */
	protected long idcnt;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public CallContext(Reader reader, Writer writer)
	{
		this.reader = reader;
		this.writer = writer;
		this.waitingcalls = Collections.synchronizedMap(new HashMap());
		this.targetobjects = Collections.synchronizedMap(new HashMap());
//		this.targetids = Collections.synchronizedMap(new HashMap());
	}

	/**
	 * 
	 */
	public void putWaitingCall(String callid, Future future)
	{
		waitingcalls.put(callid, future);
	}
	
	/**
	 * 
	 */
	public Future getWaitingCall(String callid)
	{
		return (Future)waitingcalls.get(callid);
	}
	
	/**
	 * 
	 */
	public Future removeWaitingCall(String callid)
	{
		return (Future)waitingcalls.remove(callid);
	}
	
	/**
	 * 
	 */
	public String putTargetObject(Object target)
	{
		// todo: add each object only once!
		// i.e. check for duplicates before adding and only return existing id else.
		String ret = generateId();
		targetobjects.put(ret, target);
		return ret;
	}
	
	/**
	 * 
	 */
	public Object getTargetObject(String tid)
	{
		return targetobjects.get(tid);
	}
	
	/**
	 * 
	 */
	public Object removeTargetObject(String tid)
	{
		return targetobjects.remove(tid);
	}

	/**
	 *  Get the reader.
	 *  @return The reader.
	 */
	public Reader getReader()
	{
		return reader;
	}

	/**
	 *  Get the writer.
	 *  @return the writer.
	 */
	public Writer getWriter()
	{
		return writer;
	}
	
	/**
	 * 
	 */
	public synchronized String generateId()
	{
		return ""+idcnt++;
	}
}
