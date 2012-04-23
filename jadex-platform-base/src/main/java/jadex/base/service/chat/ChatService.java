package jadex.base.service.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
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
import jadex.bridge.service.types.remote.ServiceOutputConnection;
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
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.annotation.Binding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
	
	//-------- initialization methods --------
	
	/**
	 *  Called on startup.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		this.nick	= SUtil.createUniqueId("user", 3);
		this.transfers	= new LinkedHashMap<String, Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>>();
		
		// Todo: load settings
//		final Future<Void>	ret	= new Future<Void>();
//		agent.getServiceContainer().searchService(IClockService.class, Binding.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
//		{
//			public void customResultAvailable(final IClockService clock)
//			{
//				ChatPanel.createGui(exta, clock)
//					.addResultListener(new ExceptionDelegationResultListener<ChatPanel, Void>(ret)
//				{
//					public void customResultAvailable(ChatPanel result)
//					{
//						chatpanel = result;
//						ret.setResult(null);
//					}
//				});
//			}
//		});
		return IFuture.DONE;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
//		chatpanel.dispose();
		
		final Future<Void>	ret	= new Future<Void>();
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
	
	//-------- IChatService interface --------
	
	/**
	 *  Post a message
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String nick, String text)
	{
		publishEvent(ChatEvent.TYPE_MESSAGE, nick, IComponentIdentifier.CALLER.get(), text);
		return IFuture.DONE;
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
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(final String text)
	{
		final IntermediateFuture<IChatService>	ret	= new IntermediateFuture<IChatService>();
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
	 *  Accept a waiting file transfer.
	 *  @param id	The transfer id. 
	 *  @param file	The location of the file (possibly changed by user). 
	 */
	public IFuture<Void>	acceptFile(String id, String file)
	{
		IFuture<Void>	ret;
		Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>	tup	= transfers.get(id);
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
		if(tup!=null)
		{
			TransferInfo	ti	= tup.getFirstEntity();
			if(TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				tup.getSecondEntity().setException(new RuntimeException(TransferInfo.STATE_REJECTED));
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
		if(tup!=null)
		{
			TransferInfo	ti	= tup.getFirstEntity();
			if(TransferInfo.STATE_TRANSFERRING.equals(ti.getState()) || TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				tup.getSecondEntity().terminate();
				ret	= IFuture.DONE;
			}
			else if(TransferInfo.STATE_COMPLETED.equals(ti.getState()))
			{
				ret	= new Future<Void>(new RuntimeException("Transfer already comnpleted."));				
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
	 *  @param file	The file.
	 *  @param cid	The id of a rmote chat component.
	 */
	public IFuture<Void>	sendFile(String file, final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();

		final TransferInfo fi = new TransferInfo(false, null, file, cid, new File(file).length());
		fi.setState(TransferInfo.STATE_WAITING);
		
		IFuture<IChatService> fut = agent.getServiceContainer().getService(IChatService.class, cid);
		fut.addResultListener(new ExceptionDelegationResultListener<IChatService, Void>(ret)
		{
			public void customResultAvailable(IChatService cs)
			{
				final File file = new File(fi.getFile());
				final ServiceOutputConnection ocon = new ServiceOutputConnection();
				final IInputConnection icon = ocon.getInputConnection();
				final long size = file.length();
				
				// Call chat service of receiver 
				ITerminableIntermediateFuture<Long> fut = cs.sendFile(nick, file.getName(), size, fi.getId(), icon);
				
				// Receives notifications how many bytes were received. 
				fut.addResultListener(new IntermediateExceptionDelegationResultListener<Long, Void>(ret)
				{
					boolean	started;
					
					public void intermediateResultAvailable(Long result)
					{
						// Start sending after first intermediate result was received
						if(!started)
						{
							started = true;
							doUpload(fi, ocon, cid);
						}
					}
					
					public void finished()
					{
						fi.setState(TransferInfo.STATE_COMPLETED);
						publishEvent(ChatEvent.TYPE_FILE, nick, cid, fi);
					}
					
					public void customResultAvailable(Collection<Long> result)
					{
						fi.setState(TransferInfo.STATE_COMPLETED);
						publishEvent(ChatEvent.TYPE_FILE, nick, cid, fi);
					}

					public void exceptionOccurred(Exception exception)
					{
						if(exception instanceof FutureTerminatedException || TransferInfo.STATE_ABORTED.equals(exception.getMessage()))
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
						publishEvent(ChatEvent.TYPE_FILE, nick, cid, fi);
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
	protected void publishEvent(String type, String nick, IComponentIdentifier cid,	Object value)
	{
		if(subscribers!=null)
		{
			ChatEvent	ce	= new ChatEvent(type, nick, cid, value);
			for(Iterator<SubscriptionIntermediateFuture<ChatEvent>> it=subscribers.iterator(); it.hasNext(); )
			{
				if(!it.next().addIntermediateResultIfUndone(ce))
				{
					it.remove();
				}
			}
			
			if(subscribers.isEmpty())
			{
				subscribers	= null;
			}
		}
	}

	/**
	 *  Perform a download.
	 */
	protected void	doDownload(final TransferInfo ti, final TerminableIntermediateFuture<Long> ret, IInputConnection con)
	{
		assert TransferInfo.STATE_WAITING.equals(ti.getState());
		ti.setState(TransferInfo.STATE_TRANSFERRING);
		publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
		
		try
		{
			final FileOutputStream fos = new FileOutputStream(ti.getFile());
	
			// Enable sending
			ret.addIntermediateResult(new Long(0));
			
			final ISubscriptionIntermediateFuture<byte[]> fut = con.aread();
			fut.addResultListener(agent.createResultListener(new IIntermediateResultListener<byte[]>()
			{
				public void resultAvailable(Collection<byte[]> result)
				{
					for(Iterator<byte[]> it=result.iterator(); it.hasNext(); )
					{
						intermediateResultAvailable(it.next());
					}
					finished();
				}
				
				public void intermediateResultAvailable(byte[] result)
				{
					// Check if was aborted on receiver side
					if(ti.isFinished())
					{
						ret.setFinishedIfUndone();
						fut.terminate();
						return;
					}
					
					try
					{
						fos.write(result);
						if(ti.update(ti.getDone()+result.length))
						{
							publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
						}
						ret.addIntermediateResultIfUndone(new Long(ti.getDone()));
						// todo: close con?
					}
					catch(Exception e)
					{
						ti.setState(TransferInfo.STATE_ERROR);
						publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
						ret.setExceptionIfUndone(new RuntimeException(TransferInfo.STATE_ERROR, e));
					}
				}
				
				public void finished()
				{
					try
					{
						fos.close();
						if(ti.getDone()==ti.getSize())
						{
	//						System.out.println("Received file: "+f.getAbsolutePath()+", size: "+cnt[0]);
							ti.setState(TransferInfo.STATE_COMPLETED);
							ret.setFinishedIfUndone();
						}
						else
						{
							ti.setState(TransferInfo.STATE_ABORTED);
							ret.setExceptionIfUndone(new RuntimeException(TransferInfo.STATE_ABORTED));
						}
						publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
						
						// todo: close con?
					}
					catch(Exception e)
					{
						ti.setState(TransferInfo.STATE_ERROR);
						publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
						ret.setExceptionIfUndone(new RuntimeException(TransferInfo.STATE_ERROR, e));
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ti.setState(TransferInfo.STATE_ERROR);
					publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
					ret.setExceptionIfUndone(new RuntimeException(TransferInfo.STATE_ERROR, exception));
				}
			}));
		}
		catch(Exception e)
		{
			ti.setState(TransferInfo.STATE_ERROR);
			publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
			ret.setExceptionIfUndone(new RuntimeException(TransferInfo.STATE_ERROR, e));
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
		publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
		
		try
		{
			final FileInputStream fis = new FileInputStream(new File(ti.getFile()));
			
			IComponentStep<Void> step = new IComponentStep<Void>()
			{
				long filesize	= 0;
				
				public IFuture<Void> execute(final IInternalAccess ia)
				{
					// Stop transfer on error etc.
					if(ti.isFinished())
					{
						ocon.close();
						return IFuture.DONE;
					}
					
					try
					{
						final IComponentStep<Void> self = this;
						int size = Math.min(200000, fis.available());
						filesize += size;
						byte[] buf = new byte[size];
						int read = 0;
						while(read!=buf.length)
						{
							read += fis.read(buf, read, buf.length-read);
						}
						ocon.write(buf);
//						System.out.println("wrote: "+size);
						
						if(ti.update(filesize))
						{
							publishEvent(ChatEvent.TYPE_FILE, nick, ti.getOther(), ti);
						}
						
						if(fis.available()>0)
						{
//							ia.waitForDelay(100, self);
	//						agent.scheduleStep(self);
							ocon.waitForReady().addResultListener(ia.createResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
//									ia.waitForDelay(1000, self);
									agent.getExternalAccess().scheduleStep(self);
	//								agent.waitFor(10, self);
								}
								public void exceptionOccurred(Exception exception)
								{
									ocon.close();
									ti.setState(TransferInfo.STATE_ERROR);
									publishEvent(ChatEvent.TYPE_FILE, nick, ti.getOther(), ti);
								}
							}));
						}
						else
						{
							fis.close();
							ocon.close();
							ti.setState(TransferInfo.STATE_COMPLETED);
							publishEvent(ChatEvent.TYPE_FILE, nick, ti.getOther(), ti);						}
					}
					catch(Exception e)
					{
						ti.setState(TransferInfo.STATE_ERROR);
						publishEvent(ChatEvent.TYPE_FILE, nick, ti.getOther(), ti);					}
					
					return IFuture.DONE;
				}
			};
			agent.getExternalAccess().scheduleStep(step);
		}
		catch(Exception e)
		{
		}
	}
}
