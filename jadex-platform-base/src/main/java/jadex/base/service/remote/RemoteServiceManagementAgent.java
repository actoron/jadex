package jadex.base.service.remote;

import jadex.base.fipa.SFipa;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.MessageType;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;
import jadex.micro.MicroAgent;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;
import jadex.xml.reader.Reader;
import jadex.xml.writer.Writer;

import java.util.Map;

/**
 *  Remote service management service that hosts the corresponding
 *  service. It basically has the task to forward messages from
 *  remote service management components on other platforms to its service.
 */
public class RemoteServiceManagementAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The remote management service. */
	protected RemoteServiceManagementService rms;
	
	//-------- constructors --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		rms = new RemoteServiceManagementService(getExternalAccess());
		addService(rms);
	}
	
	/**
	 *  Called, whenever a message is received.
	 *  @param msg The message.
	 *  @param mt The message type.
	 */
	public void messageArrived(final Map msg, final MessageType mt)
	{
//		if(msg.toString().indexOf("Shop")!=-1)
//			System.out.println("received: "+msg);
		if(SFipa.MESSAGE_TYPE_NAME_FIPA.equals(mt.getName()))
		{
			SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
				.addResultListener(createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					// Hack!!! Manual decoding for using custom class loader.
					final ILibraryService ls = (ILibraryService)result;
					Object	content	= msg.get(SFipa.CONTENT);
					if(content instanceof String)
					{
						// Catch decode problems.
						// Should be ignored or be a warning.
						try
						{
//							content = JavaReader.objectFromXML((String)content, ls.getClassLoader());
							content = Reader.objectFromXML(rms.getCallContext().getReader(), (String)content, ls.getClassLoader(), rms.getCallContext());
						}
						catch(Exception e)
						{
							content	= null;
//							getLogger().warning("Remote service management service could not decode message."+content);
						}
					}
					
					if(content instanceof IRemoteCommand)
					{
						final IRemoteCommand com = (IRemoteCommand)content;
						com.execute(getExternalAccess(), rms.getCallContext()).addResultListener(createResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
		//						System.out.println("result of command: "+com+" "+result);
								if(result!=null)
								{
									final Object repcontent = result;
									createReply(msg, mt).addResultListener(createResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											Map reply = (Map)result;
//											reply.put(SFipa.CONTENT, JavaWriter.objectToXML(repcontent, ls.getClassLoader()));
											String content = Writer.objectToXML(rms.getCallContext().getWriter(), repcontent, ls.getClassLoader(), rms.getCallContext());
											reply.put(SFipa.CONTENT, content);
//											System.out.println("content: "+content);
											sendMessage(reply, mt);
										}
										public void exceptionOccurred(Object source, Exception exception)
										{
											// Terminated, when rms killed in mean time
											if(!(exception instanceof ComponentTerminatedException))
											{
												super.exceptionOccurred(source, exception);
											}
										}
									}));
								}
							}
							public void exceptionOccurred(Object source, Exception exception)
							{
								// Terminated, when rms killed in mean time
								if(!(exception instanceof ComponentTerminatedException))
								{
									super.exceptionOccurred(source, exception);
								}
							}
						}));
					}
					else if(content!=null)
					{
						getLogger().warning("RMS unexpected message content: "+content);
					}
				}
			}));
		}
	}
}
