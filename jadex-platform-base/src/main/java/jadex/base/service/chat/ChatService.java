package jadex.base.service.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.commons.SUtil;
import jadex.commons.Tuple3;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.annotation.Binding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *  Chat service implementation.
 */
@Service
public class ChatService implements IChatService, IChatGuiService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The futures of active subscribers. */
	protected Set<SubscriptionIntermediateFuture<ChatEvent>>	subscribers;
	
	/** The local nick name. */
	protected String nick;
	
	/** The currently managed file transfers. */
	protected Map<String, Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>>	transfers;
	protected Map<String, Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>>	transfers2;
	
	/** Flag to avoid duplicate initialization/shutdown due to duplicate use of implementation. */
	protected boolean	running;
	
	//-------- initialization methods --------
	
	/**
	 *  Called on startup.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		if(running)
		{
			return IFuture.DONE;
		}
		else
		{
			running	= true;
			final Future<Void>	ret	= new Future<Void>();
	
			// Todo: load settings
			this.nick	= SUtil.createUniqueId("user", 3);
			this.transfers	= new LinkedHashMap<String, Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>>();
			this.transfers2	= new LinkedHashMap<String, Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>>();
			
			IIntermediateFuture<IChatService>	chatfut	= agent.getServiceContainer().getRequiredServices("chatservices");
			chatfut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
			{
				public void intermediateResultAvailable(IChatService chat)
				{
					chat.status(nick, STATE_IDLE);
				}
				public void finished()
				{
					ret.setResult(null);
				}
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(null);
				}
			});
			
			return ret;
		}
	}
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		if(!running)
		{
			return IFuture.DONE;
		}
		else
		{
			running	= false;
			final Future<Void>	ret	= new Future<Void>();
			
			if(subscribers!=null)
			{
				for(SubscriptionIntermediateFuture<ChatEvent> fut: subscribers)
				{
					fut.terminate();
				}
			}
			
			// Todo: required services don't work in service shutdown!?
			IIntermediateFuture<IChatService>	chatfut	= SServiceProvider.getServices(agent.getServiceContainer(), IChatService.class, Binding.SCOPE_GLOBAL);
	//		IIntermediateFuture<IChatService>	chatfut	= agent.getServiceContainer().getRequiredServices("chatservices");
			chatfut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
			{
				public void intermediateResultAvailable(IChatService chat)
				{
					// Hack!!! change local id from rms to chat agent.
					IComponentIdentifier id	= IComponentIdentifier.LOCAL.get();
					IComponentIdentifier.LOCAL.set(agent.getComponentIdentifier());
					chat.status(nick, STATE_DEAD);
					IComponentIdentifier.LOCAL.set(id);
				}
				public void finished()
				{
					ret.setResult(null);
				}
				public void exceptionOccurred(Exception exception)
				{
					ret.setResult(null);
				}
			});
			return ret;
		}
	}
	
	//-------- IChatService interface --------
	
	/**
	 *  Post a message
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String nick, String text)
	{
		boolean	published	= publishEvent(ChatEvent.TYPE_MESSAGE, nick, IComponentIdentifier.CALLER.get(), text);
		return published ? IFuture.DONE : new Future<Void>(new RuntimeException("No GUI, message was discarded."));
	}

	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IFuture<Void>	status(String nick, String status)
	{
		publishEvent(ChatEvent.TYPE_STATECHANGE, nick, IComponentIdentifier.CALLER.get(), status);
		return IFuture.DONE;
	}
	
	/**
	 *  Send a file.
	 *  
	 *  @param nick The sender's nick name.
	 *  @param filename The filename.
	 *  @param size The size of the file.
	 *  @param id An optional id to identify the transfer (e.g. for resume after error).
	 *  @param con The connection.
	 *  
	 *  @return The returned future publishes updates about the total number of bytes received.
	 *    Exception messages of the returned future correspond to file transfer states (aborted vs. error vs. rejected).
	 */
	public ITerminableIntermediateFuture<Long> sendFile(String nick, String filename, long size, String id, IInputConnection con)
	{
		TerminableIntermediateFuture<Long> ret = new TerminableIntermediateFuture<Long>();
		IComponentIdentifier sender = IComponentIdentifier.CALLER.get();
		
		TransferInfo	ti	= new TransferInfo(true, id, filename, sender, size);
		ti.setState(TransferInfo.STATE_WAITING);
		
		transfers.put(ti.getId(), new Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>(ti, ret, con));
		
		publishEvent(ChatEvent.TYPE_FILE, nick, sender, ti);
		
		return ret;
	}
	
	
	/**
	 *  Send a file. Alternative method signature.
	 *  
	 *  @param nick The sender's nick name.
	 *  @param filename The filename.
	 *  @param size The size of the file.
	 *  @param id An optional id to identify the transfer (e.g. for resume after error).
	 *  
	 *  @return When the upload is accepted, the output connection for sending the file is returned.
	 */
	public ITerminableFuture<IOutputConnection> startUpload(final String nick, String filename, long size, String id)
	{
		final IComponentIdentifier sender = IComponentIdentifier.CALLER.get();
		
		final TransferInfo	ti	= new TransferInfo(true, id, filename, sender, size);
		ti.setState(TransferInfo.STATE_WAITING);
		
		// Todo: automatically decouple termination commands
		final IExternalAccess	exta	= agent.getExternalAccess();
		TerminableFuture<IOutputConnection> ret = new TerminableFuture<IOutputConnection>(new Runnable()
		{
			public void run()
			{
				// Called when request is externally terminated.
				exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ti.setState(TransferInfo.STATE_REJECTED);
						publishEvent(ChatEvent.TYPE_FILE, nick, sender, ti);
						transfers2.remove(ti.getId());
						return IFuture.DONE;
					}
				});
			}
		});
		
		transfers2.put(ti.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(ti, ret, null));
		
		publishEvent(ChatEvent.TYPE_FILE, nick, sender, ti);

		return ret;
	}


	//-------- IChatGuiService interface --------
	
	/**
	 *  Set the user name.
	 */
	public IFuture<Void>	setNickName(String nick)
	{
		this.nick	= nick;
		return IFuture.DONE;
	}
	
	/**
	 *  Get the user name.
	 */
	public IFuture<String>	getNickName()
	{
		return new Future<String>(nick);
	}

	/**
	 *  Subscribe to events from the chat service.
	 *  @return A future publishing chat events as intermediate results.
	 */
	public ISubscriptionIntermediateFuture<ChatEvent>	subscribeToEvents()
	{
		if(subscribers==null)
		{
			subscribers	= new LinkedHashSet<SubscriptionIntermediateFuture<ChatEvent>>();
		}
		
		SubscriptionIntermediateFuture<ChatEvent>	ret	= new SubscriptionIntermediateFuture<ChatEvent>();
		subscribers.add(ret);
		
		return ret;		
	}
	
	/**
	 *  Search for available chat services.
	 *  @return The currently available remote services.
	 */
	public IIntermediateFuture<IChatService> findUsers()
	{
		IIntermediateFuture<IChatService> ret	= agent.getServiceContainer().getRequiredServices("chatservices");
		return ret;
	}
	
	/**
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(final String text, final IComponentIdentifier[] receivers)
	{
		final IntermediateFuture<IChatService>	ret	= new IntermediateFuture<IChatService>();
		
		if(receivers.length>0)
		{
			final int[] cnt = new int[]{receivers.length};
			for(int i=0; i<receivers.length; i++)
			{
				agent.getServiceContainer().getService(IChatService.class, receivers[i])
					.addResultListener(new IResultListener<IChatService>()
				{
					public void resultAvailable(final IChatService chat)
					{
						chat.message(nick, text).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								ret.addIntermediateResultIfUndone(chat);	// Might return after later exception in service search!?
								
								if(--cnt[0]==0)
									ret.setFinished();
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(--cnt[0]==0)
									ret.setFinished();
							}
						});
						
						ret.addIntermediateResult(chat);
					}
					public void exceptionOccurred(Exception exception)
					{
						if(--cnt[0]==0)
							ret.setFinished();
					}
				});
			}
		}
		else //if(receivers.length==0)
		{
			final IIntermediateFuture<IChatService> ifut = agent.getServiceContainer().getRequiredServices("chatservices");
			ifut.addResultListener(new IntermediateDelegationResultListener<IChatService>(ret)
			{
				boolean	finished;
				int cnt;
				public void intermediateResultAvailable(final IChatService chat)
				{
					cnt++;
					chat.message(nick, text).addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							ret.addIntermediateResultIfUndone(chat);	// Might return after later exception in service search!?
							
							if(--cnt==0 && finished)
							{
								ret.setFinished();
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(--cnt==0 && finished)
							{
								ret.setFinished();
							}
						}
					});
				}
				public void finished()
				{
					finished	= true;
					if(finished && cnt==0)
					{
						ret.setFinished();
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IIntermediateFuture<IChatService> status(final String status)
	{
		final IntermediateFuture<IChatService>	ret	= new IntermediateFuture<IChatService>();
		final IIntermediateFuture<IChatService> ifut = agent.getServiceContainer().getRequiredServices("chatservices");
		ifut.addResultListener(new IntermediateDelegationResultListener<IChatService>(ret)
		{
			boolean	finished;
			int cnt	= 0;
			public void intermediateResultAvailable(final IChatService chat)
			{
				cnt++;
				chat.status(nick, status).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.addIntermediateResultIfUndone(chat);	// Might return after later exception in service search!?
						
						if(--cnt==0 && finished)
						{
							ret.setFinished();
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(--cnt==0 && finished)
						{
							ret.setFinished();
						}
					}
				});
			}
			public void finished()
			{
				finished	= true;
				if(finished && cnt==0)
				{
					ret.setFinished();
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get a snapshot of the currently managed file transfers.
	 */
	public IIntermediateFuture<TransferInfo>	getFileTransfers()
	{
		List<TransferInfo>	ret	= new ArrayList<TransferInfo>();
		for(Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection> tup: transfers.values())
		{
			ret.add(tup.getFirstEntity());
		}
		for(Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection> tup: transfers2.values())
		{
			ret.add(tup.getFirstEntity());
		}
		return new IntermediateFuture<TransferInfo>(ret);
	}

	
	/**
	 *  Accept a waiting file transfer.
	 *  @param id	The transfer id. 
	 *  @param file	The location of the file (possibly changed by user). 
	 */
	public IFuture<Void>	acceptFile(String id, String file)
	{
		IFuture<Void>	ret;
		Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>	tup	= transfers.get(id);
		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(id);
		if(tup!=null)
		{
			TransferInfo	ti	= tup.getFirstEntity();
			if(TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				ti.setFile(file);
				doDownload(ti, tup.getSecondEntity(), tup.getThirdEntity());
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_REJECTED.equals(ti.getState()))
			{
				ret	= new Future<Void>(new RuntimeException("Transfer already rejected."));				
			}
			else
			{
				// Already accepted -> ignore.
				ret	= IFuture.DONE;
			}
		}
		else if(tup2!=null)
		{
			TransferInfo	ti	= tup2.getFirstEntity();
			if(TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				ServiceInputConnection	sic	= new ServiceInputConnection();
				((Future<IOutputConnection>) tup2.getSecondEntity()).setResultIfUndone(sic.getOutputConnection());
				ti.setFile(file);
				doDownload(ti, null, sic);
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_REJECTED.equals(ti.getState()))
			{
				ret	= new Future<Void>(new RuntimeException("Transfer already rejected."));				
			}
			else
			{
				// Already accepted -> ignore.
				ret	= IFuture.DONE;
			}
		}
		else
		{
			ret	= new Future<Void>(new RuntimeException("No such file transfer."));
		}
		
		return ret;
	}
	
	/**
	 *  Reject a waiting file transfer.
	 *  @param id	The transfer id. 
	 */
	public IFuture<Void>	rejectFile(String id)
	{
		IFuture<Void>	ret;
		Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>	tup	= transfers.get(id);
		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(id);
		if(tup!=null)
		{
			TransferInfo	ti	= tup.getFirstEntity();
			if(TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				ti.setState(TransferInfo.STATE_REJECTED);
				publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
				tup.getSecondEntity().setException(new RuntimeException(TransferInfo.STATE_REJECTED));
				transfers.remove(id);
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_REJECTED.equals(ti.getState()))
			{
				// Already rejected -> ignore.
				ret	= IFuture.DONE;
			}
			else
			{
				ret	= new Future<Void>(new RuntimeException("Transfer already accepted."));				
			}
		}
		else if(tup2!=null)
		{
			TransferInfo	ti	= tup2.getFirstEntity();
			if(TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				ti.setState(TransferInfo.STATE_REJECTED);
				publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
				((Future<IOutputConnection>) tup2.getSecondEntity()).setException(new RuntimeException(TransferInfo.STATE_REJECTED));
				transfers2.remove(id);
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_REJECTED.equals(ti.getState()))
			{
				// Already rejected -> ignore.
				ret	= IFuture.DONE;
			}
			else
			{
				ret	= new Future<Void>(new RuntimeException("Transfer already accepted."));				
			}
		}
		else
		{
			ret	= new Future<Void>(new RuntimeException("No such file transfer."));
		}
		
		return ret;	
	}
	
	/**
	 *  Cancel an ongoing file transfer.
	 *  @param id	The transfer id. 
	 */
	public IFuture<Void>	cancelTransfer(String id)
	{
		IFuture<Void>	ret;
		Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>	tup	= transfers.get(id);
		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(id);
		if(tup!=null)
		{
			TransferInfo	ti	= tup.getFirstEntity();
			if(TransferInfo.STATE_TRANSFERRING.equals(ti.getState()) || TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				ti.setState(TransferInfo.STATE_CANCELLING);
				publishEvent(ChatEvent.TYPE_FILE, null, null, ti);
				tup.getSecondEntity().terminate();
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_COMPLETED.equals(ti.getState()))
			{
				ret	= new Future<Void>(new RuntimeException("Transfer already completed."));				
			}
			else
			{
				// Already aborted -> ignore.
				ret	= IFuture.DONE;
			}
		}
		else if(tup2!=null)
		{
			TransferInfo	ti	= tup2.getFirstEntity();
			if(TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				ti.setState(TransferInfo.STATE_CANCELLING);
				publishEvent(ChatEvent.TYPE_FILE, null, null, ti);
				tup2.getSecondEntity().terminate();
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_TRANSFERRING.equals(ti.getState()))
			{
				ti.setState(TransferInfo.STATE_CANCELLING);
				publishEvent(ChatEvent.TYPE_FILE, null, null, ti);
				tup2.getThirdEntity().close();
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_COMPLETED.equals(ti.getState()))
			{
				ret	= new Future<Void>(new RuntimeException("Transfer already completed."));				
			}
			else
			{
				// Already aborted -> ignore.
				ret	= IFuture.DONE;
			}
		}
		else
		{
			ret	= new Future<Void>(new RuntimeException("No such file transfer."));
		}
		
		return ret;
	}

	/**
	 *  Send a local file to the target component.
	 *  @param filename	The file.
	 *  @param cid	The id of a rmote chat component.
	 */
	public IFuture<Void>	sendFile(final String filename, final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();

		IFuture<IChatService> fut = agent.getServiceContainer().getService(IChatService.class, cid);
		fut.addResultListener(new ExceptionDelegationResultListener<IChatService, Void>(ret)
		{
			public void customResultAvailable(IChatService cs)
			{
				final File file = new File(filename);
				final long size = file.length();
				
//				// Call chat service of receiver 
//				final ServiceOutputConnection ocon = new ServiceOutputConnection();
//				final IInputConnection icon = ocon.getInputConnection();
//				ITerminableIntermediateFuture<Long> fut = cs.sendFile(nick, file.getName(), size, fi.getId(), icon);
//
//				// Receives notifications how many bytes were received. 
//				fut.addResultListener(new IntermediateExceptionDelegationResultListener<Long, Void>(ret)
//				{
//					boolean	started;
//					
//					public void intermediateResultAvailable(Long result)
//					{
////						System.out.println("rec: "+result);
//						// Start sending after first intermediate result was received
//						if(!started)
//						{
//							started = true;
//							ret.setResult(null);
//							doUpload(fi, ocon, cid);
//						}
//					}
//					
//					public void finished()
//					{
//						fi.setState(TransferInfo.STATE_COMPLETED);
//						publishEvent(ChatEvent.TYPE_FILE, null, cid, fi);
//					}
//					
//					public void customResultAvailable(Collection<Long> result)
//					{
//						fi.setState(TransferInfo.STATE_COMPLETED);
//						publishEvent(ChatEvent.TYPE_FILE, null, cid, fi);
//					}
//
//					public void exceptionOccurred(Exception exception)
//					{
//						if(exception instanceof FutureTerminatedException || TransferInfo.STATE_ABORTED.equals(exception.getMessage()))
//						{
//							fi.setState(TransferInfo.STATE_ABORTED);
//						}
//						else if(TransferInfo.STATE_REJECTED.equals(exception.getMessage()))
//						{	
//							fi.setState(TransferInfo.STATE_REJECTED);
//						}
//						else
//						{
//							fi.setState(TransferInfo.STATE_ERROR);
//						}
//						publishEvent(ChatEvent.TYPE_FILE, null, cid, fi);
//					}
//				});
				
				// Call chat service of receiver (alternative interface)
				final TransferInfo fi = new TransferInfo(false, null, filename, cid, new File(filename).length());
				fi.setState(TransferInfo.STATE_WAITING);
				ITerminableFuture<IOutputConnection> fut = cs.startUpload(nick, file.getName(), size, fi.getId());
				transfers2.put(fi.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(fi, fut, null));
				publishEvent(ChatEvent.TYPE_FILE, null, cid, fi);
				fut.addResultListener(new ExceptionDelegationResultListener<IOutputConnection, Void>(ret)
				{
					public void customResultAvailable(IOutputConnection ocon)
					{
						doUpload(fi, ocon, cid);
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(exception instanceof FutureTerminatedException || TransferInfo.STATE_ABORTED.equals(exception.getMessage())
							|| TransferInfo.STATE_CANCELLING.equals(fi.getState()))
						{
							fi.setState(TransferInfo.STATE_ABORTED);
						}
						else if(TransferInfo.STATE_REJECTED.equals(exception.getMessage()))
						{	
							fi.setState(TransferInfo.STATE_REJECTED);
						}
						else
						{
							fi.setState(TransferInfo.STATE_ERROR);
						}
						transfers2.remove(fi.getId());
						publishEvent(ChatEvent.TYPE_FILE, null, cid, fi);
					}
				});

			}					
		});
					
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Post an event to registered subscribers.
	 *  @param type	The event type.
	 *  @param nick	The nick name.
	 *  @param cid	The component ID.
	 *  @param value The event value.
	 */
	protected boolean	publishEvent(String type, String nick, IComponentIdentifier cid,	Object value)
	{
		boolean	ret	= false;
		if(subscribers!=null)
		{
			ChatEvent	ce	= new ChatEvent(type, nick, cid, value);
			for(Iterator<SubscriptionIntermediateFuture<ChatEvent>> it=subscribers.iterator(); it.hasNext(); )
			{
				if(it.next().addIntermediateResultIfUndone(ce))
				{
					ret	= true;
				}
				else
				{
					it.remove();
				}
			}
			
			if(subscribers.isEmpty())
			{
				subscribers	= null;
			}
		}
		return ret;
	}

	/**
	 *  Perform a download.
	 */
	protected void	doDownload(final TransferInfo ti, final TerminableIntermediateFuture<Long> ret, final IInputConnection con)
	{
		assert TransferInfo.STATE_WAITING.equals(ti.getState());
		ti.setState(TransferInfo.STATE_TRANSFERRING);
		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(ti.getId());
		if(tup2!=null)
			transfers2.put(ti.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(ti, tup2.getSecondEntity(), con));
		publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
		
		try
		{
			// Enable sending
			if(ret!=null)
			{
				ret.addIntermediateResult(new Long(0));
			}
			
			final FileOutputStream fos = new FileOutputStream(ti.getFile());
			final ITerminableIntermediateFuture<Long> fut = con.writeToOutputStream(fos, agent.getExternalAccess());
			
			fut.addResultListener(new IIntermediateResultListener<Long>()
			{
				public void resultAvailable(Collection<Long> result)
				{
					finished();
				}
				public void intermediateResultAvailable(Long filesize)
				{
					if(TransferInfo.STATE_ABORTED.equals(ti.getState()))
					{
						fut.terminate();
					}
					if(ret!=null)
					{
						ret.addIntermediateResult(new Long(filesize));
					}
					if(ti.update(filesize))
					{
						publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
					}
				}
				public void finished()
				{
					try
					{
						fos.close();
					}
					catch(Exception e)
					{
					}
					con.close();
					ti.setState(ti.getSize()==ti.getDone() ? TransferInfo.STATE_COMPLETED : TransferInfo.STATE_ABORTED);
					transfers.remove(ti.getId());
					transfers2.remove(ti.getId());
					publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
				}
				public void exceptionOccurred(Exception exception)
				{
					try
					{
						fos.close();
					}
					catch(Exception e)
					{
					}
					con.close();
					ti.setState(TransferInfo.STATE_CANCELLING.equals(ti.getState()) ? TransferInfo.STATE_ABORTED : TransferInfo.STATE_ERROR);
					transfers.remove(ti.getId());
					transfers2.remove(ti.getId());
					publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
				}
			});
		}
		catch(Exception e)
		{
			ti.setState(TransferInfo.STATE_ERROR);
			transfers.remove(ti.getId());
			transfers2.remove(ti.getId());
			publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
			if(ret!=null)
			{
				ret.setExceptionIfUndone(new RuntimeException(TransferInfo.STATE_ERROR, e));
			}
		}
	}
	
	/**
	 *  Perform an upload.
	 *  Called from file sender.
	 *  Writes bytes from file input stream to output connection.
	 */
	protected void	doUpload(final TransferInfo ti, final IOutputConnection ocon, final IComponentIdentifier receiver)
	{
		assert TransferInfo.STATE_WAITING.equals(ti.getState());
		ti.setState(TransferInfo.STATE_TRANSFERRING);
		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(ti.getId());
		if(tup2!=null)
			transfers2.put(ti.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(ti, tup2.getSecondEntity(), ocon));
		publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
		
		try
		{
			final FileInputStream fis = new FileInputStream(new File(ti.getFile()));
			final ITerminableIntermediateFuture<Long> fut = ocon.writeFromInputStream(fis, agent.getExternalAccess());

			fut.addResultListener(new IIntermediateResultListener<Long>()
			{
				public void resultAvailable(Collection<Long> result)
				{
					finished();
				}
				public void intermediateResultAvailable(Long filesize)
				{
					if(TransferInfo.STATE_ABORTED.equals(ti.getState()))
					{
						ocon.close();
						fut.terminate();
					}
					if(ti.update(filesize))
					{
						publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
					}
				}
				public void finished()
				{
					try
					{
						fis.close();
					}
					catch(Exception e)
					{
					}
					ocon.close();
					ti.setState(TransferInfo.STATE_COMPLETED);
					transfers.remove(ti.getId());
					transfers2.remove(ti.getId());
					publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);			
				}
				public void exceptionOccurred(Exception exception)
				{
					try
					{
						fis.close();
					}
					catch(Exception e)
					{
					}
					ocon.close();
					ti.setState(TransferInfo.STATE_CANCELLING.equals(ti.getState()) ? TransferInfo.STATE_ABORTED : TransferInfo.STATE_ERROR);
					transfers.remove(ti.getId());
					transfers2.remove(ti.getId());
					publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
				}
			});
		}
		catch(Exception e)
		{
			ti.setState(TransferInfo.STATE_ERROR);
			transfers.remove(ti.getId());
			transfers2.remove(ti.getId());
			publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
		}
	}
}
