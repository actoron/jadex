package jadex.platform.service.chat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IConnection;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.TimeoutResultListener;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.Base64;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.Tuple3;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
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
import jadex.commons.future.TerminationCommand;


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
	
	/** The service identifier. */
//	@ServiceIdentifier
//	protected IServiceIdentifier sid;
	
	/** The futures of active subscribers. */
	protected Set<SubscriptionIntermediateFuture<ChatEvent>> subscribers;
	
	/** The local nick name. */
	protected String nick;
	
	/** The current status (idle, typing, away). */
	protected String status;
	
	/** The currently managed file transfers. */
	protected Map<String, Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>>	transfers;
	protected Map<String, Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>>	transfers2;
	
	/** Flag to avoid duplicate initialization/shutdown due to duplicate use of implementation. */
	protected boolean	running;
	
	/** The image. */
	protected byte[] image;
	
	//-------- initialization methods --------
	
	/**
	 *  Called on startup.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(!running)
		{
			running	= true;
			status	= STATE_AWAY;	// Changes to idle only when a gui is connected.
			
			final PropProvider pp = new PropProvider();
			agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new IResultListener<ISettingsService>()
			{
				public void resultAvailable(ISettingsService settings)
				{
					if(!(agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("nosave") instanceof Boolean)
						|| !((Boolean)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("nosave")).booleanValue())
					{
						settings.registerPropertiesProvider(getSubname(), pp)
							.addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
		//							pp.isCalled().addResultListener(new DelegationResultListener<Void>(ret)
		//							{
		//								public void customResultAvailable(Void result)
		//								{
										proceed();
		//								}
		//							});
							}
						});
					}
					else
					{
						proceed();
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// No settings service: ignore.
					proceed();
				}
				
				public void proceed()
				{
					if(nick==null)
						nick	= SUtil.createPlainRandomId("user", 3);
					transfers	= new LinkedHashMap<String, Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>>();
					transfers2	= new LinkedHashMap<String, Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>>();
					
					// Search and post status in background for not delaying platform startup.
					IIntermediateFuture<IChatService>	chatfut	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("chatservices");
					chatfut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
					{
						public void intermediateResultAvailable(IChatService chat)
						{
							chat.status(nick, STATE_IDLE, null);
						}
						public void finished()
						{
							// ignore...
						}
						public void exceptionOccurred(Exception exception)
						{
							// ignore...
						}
					});
					
					ret.setResult(null);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
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
			
			final Future<Void> done	= new Future<Void>();
			IIntermediateFuture<IChatService>	chatfut	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("chatservices");
			chatfut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
			{
				public void intermediateResultAvailable(IChatService chat)
				{
					chat.status(nick, STATE_DEAD, null);
				}
				public void finished()
				{
					done.setResult(null);
				}
				public void exceptionOccurred(Exception exception)
				{
					done.setResult(null);
				}
			});
			
			agent.getComponentFeature(IRequiredServicesFeature.class).searchService(ISettingsService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new IResultListener<ISettingsService>()
			{
				public void resultAvailable(ISettingsService settings)
				{
					if(!(agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("nosave") instanceof Boolean)
						|| !((Boolean)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("nosave")).booleanValue())
					{
						settings.deregisterPropertiesProvider(getSubname())
							.addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								proceed();
							}
						});
					}
					else
					{
						proceed();
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					// No settings service: ignore.
					proceed();
				}
				
				public void proceed()
				{
					
					// Only wait 2 secs for sending status before terminating the agent.
					done.addResultListener(new TimeoutResultListener<Void>(2000, agent.getExternalAccess(),
						new DelegationResultListener<Void>(ret)
					{
						public void exceptionOccurred(Exception exception)
						{
							super.resultAvailable(null);
						}
					}));
				}
			});
			
			return ret;
		}
	}

	/**
	 *  Get the "semi-qualified" sub name for settings.
	 */
	protected String	getSubname()
	{
		String	subname	= null;
		IComponentIdentifier	cid	= agent.getComponentIdentifier();
		while(cid.getParent()!=null)
		{
			subname	= subname==null ? cid.getLocalName() : subname+"."+cid.getLocalName();
			cid	= cid.getParent();
		}
		return subname;
	}
	
	//-------- IChatService interface --------
	
	/**
	 *  Post a message
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String nick, String text, boolean privatemessage)
	{
//		System.out.println("Timeout: "+ServiceCall.getInstance().getTimeout()+", "+ServiceCall.getInstance().isRealtime());
		boolean	published	= publishEvent(ChatEvent.TYPE_MESSAGE, nick, ServiceCall.getCurrentInvocation().getCaller(), text, privatemessage, null);
		return published ? IFuture.DONE : new Future<Void>(new RuntimeException("No GUI, message was discarded."));
	}

	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IFuture<Void>	status(String nick, String status, byte[] image)
	{
		publishEvent(ChatEvent.TYPE_STATECHANGE, nick, ServiceCall.getCurrentInvocation().getCaller(), status, false, image);
		return IFuture.DONE;
	}
	
	/**
	 *  Get the current status.
	 */
	public IFuture<String>	getStatus()
	{
		return new Future<String>(status);
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
	public ITerminableIntermediateFuture<Long> sendFile(final String nick, String filename, long size, String id, final IInputConnection con)
	{
		final ServiceCall call = ServiceCall.getCurrentInvocation();
		
		// Hack!!! always assume real time for chat interaction.
		final TransferInfo	ti	= new TransferInfo(true, id, filename, null, call.getCaller(), size, System.currentTimeMillis() + call.getTimeout());
		ti.setState(TransferInfo.STATE_WAITING);
		
		final TerminableIntermediateFuture<Long> ret = new TerminableIntermediateFuture<Long>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				ti.setState(TransferInfo.STATE_ABORTED);
				publishEvent(ChatEvent.TYPE_FILE, nick, call.getCaller(), ti);
				transfers2.remove(ti.getId());
			}
		});
		
		transfers.put(ti.getId(), new Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>(ti, ret, con));
		publishEvent(ChatEvent.TYPE_FILE, nick, call.getCaller(), ti);
		
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
		final ServiceCall call = ServiceCall.getCurrentInvocation();
		
		final TransferInfo	ti	= new TransferInfo(true, id, filename, null, call.getCaller(), size, System.currentTimeMillis() + call.getTimeout());
		ti.setState(TransferInfo.STATE_WAITING);
		
		TerminableFuture<IOutputConnection> ret = new TerminableFuture<IOutputConnection>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				ti.setState(TransferInfo.STATE_ABORTED);
				publishEvent(ChatEvent.TYPE_FILE, nick, call.getCaller(), ti);
				transfers2.remove(ti.getId());
			}
		});
		
		transfers2.put(ti.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(ti, ret, null));
		
		publishEvent(ChatEvent.TYPE_FILE, nick, call.getCaller(), ti);

		return ret;
	}


	//-------- IChatGuiService interface --------
	
	/**
	 *  Set the user name.
	 */
	public IFuture<Void>	setNickName(String nick)
	{
		this.nick	= nick;
		// Publish new nickname
		status(null, null, new IComponentIdentifier[0]);
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
	 *  Set the image.
	 */
	public IFuture<Void>	setImage(byte[] image)
	{
		this.image = image;
		// Publish new image
		status(null, image, new IComponentIdentifier[0]);
		return IFuture.DONE;
	}
	
	/**
	 *  Get the image.
	 */
	public IFuture<byte[]>	getImage()
	{
		return new Future<byte[]>(image);
	}

	/**
	 *  Subscribe to events from the chat service.
	 *  @return A future publishing chat events as intermediate results.
	 */
	public ISubscriptionIntermediateFuture<ChatEvent>	subscribeToEvents()
	{
//		final SubscriptionIntermediateFuture<ChatEvent>	ret	= new SubscriptionIntermediateFuture<ChatEvent>();
		final SubscriptionIntermediateFuture<ChatEvent>	ret	= (SubscriptionIntermediateFuture<ChatEvent>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);

		if(subscribers==null)
		{
			subscribers	= new LinkedHashSet<SubscriptionIntermediateFuture<ChatEvent>>();
		}
		subscribers.add(ret);
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				subscribers.remove(ret);
			}
		});
		
		return ret;		
	}
	
	/**
	 *  Search for available chat services.
	 *  @return The currently available remote services.
	 */
	public IIntermediateFuture<IChatService> findUsers()
	{
		IIntermediateFuture<IChatService> ret	= agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("chatservices");
//		ret.addResultListener(new DefaultResultListener<Collection<IChatService>>()
//		{
//			public void resultAvailable(Collection<IChatService> result)
//			{
//				System.out.println("Found chat users: "+result);
//			}
//		});
		return ret;
	}
	
	/**
	 *  Post a message.
	 *  Searches for available chat services and posts the message to all.
	 *  @param text The text message.
	 *  @return The remote services, to which the message was successfully posted.
	 */
	public IIntermediateFuture<IChatService> message(final String text, final IComponentIdentifier[] receivers, boolean self)
	{
		final IntermediateFuture<IChatService>	ret = (IntermediateFuture<IChatService>)SFuture.getNoTimeoutFuture(IntermediateFuture.class, agent);
//		final IntermediateFuture<IChatService>	ret	= new IntermediateFuture<IChatService>();
		
		if(receivers!=null && receivers.length>0)
		{
			boolean foundself = false;
			
			if(self)
			{
				for(int i=0; i<receivers.length; i++)
				{
					if(agent.getComponentIdentifier().equals(receivers[i]))
					{
						foundself = true;
					}
				}
			}
			
			final int cnt = (self && !foundself)? receivers.length+1: receivers.length;
			
			final CollectionResultListener<IChatService> lis = new CollectionResultListener<IChatService>(
				cnt, true, new IResultListener<Collection<IChatService>>()
			{
				public void resultAvailable(Collection<IChatService> result) 
				{
					ret.setFinished();
				}

				public void exceptionOccurred(Exception exception)
				{
					ret.setFinished();
				}
			});
			
			for(int i=0; i<receivers.length; i++)
			{
				sendTo(text, receivers[i], true).addResultListener(new IResultListener<IChatService>()
				{
					public void resultAvailable(IChatService result)
					{
						ret.addIntermediateResultIfUndone(result); // Might return after later exception in service search!?
						lis.resultAvailable(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						lis.exceptionOccurred(exception);
					}
				});
			}
			
			if(self && !foundself)
			{
				sendTo(text, agent.getComponentIdentifier(), true).addResultListener(new IResultListener<IChatService>()
				{
					public void resultAvailable(IChatService result)
					{
						ret.addIntermediateResultIfUndone(result); // Might return after later exception in service search!?
						lis.resultAvailable(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						lis.exceptionOccurred(exception);
					}
				});
			}
		}
		else //if(receivers.length==0)
		{
			final IIntermediateFuture<IChatService> ifut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("chatservices");
			
			ifut.addResultListener(new IntermediateDelegationResultListener<IChatService>(ret)
			{
				boolean	finished;
				int cnt;
				public void customIntermediateResultAvailable(final IChatService chat)
				{
					cnt++;
					chat.message(nick, text, false).addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							ret.addIntermediateResultIfUndone(chat);	// Might be called after concurrent exception in service search!
							
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
	 *  Helper method for sending message to cid.
	 */
	protected IFuture<IChatService> sendTo(final String text, IComponentIdentifier rec, final boolean privatemessage)
	{
		final Future<IChatService> ret = new Future<IChatService>();
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IChatService.class, rec)
			.addResultListener(new DelegationResultListener<IChatService>(ret)
		{
			public void customResultAvailable(final IChatService chat)
			{
//				ret.setResult(chat);
				chat.message(nick, text, privatemessage).addResultListener(new ExceptionDelegationResultListener<Void, IChatService>(ret)
				{
					public void customResultAvailable(Void result)
					{
						ret.setResult(chat);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Post a status change.
	 *  @param status The new status or null for no change.
	 *  @param image The new avatar image or null for no change.
	 */
	public IIntermediateFuture<IChatService> status(final String status, final byte[] image, IComponentIdentifier[] receivers)
	{
		final IntermediateFuture<IChatService>	ret	= new IntermediateFuture<IChatService>();
		if(status!=null)
		{
			this.status	= status;
		}
		
		if(receivers!=null && receivers.length>0)
		{
			boolean foundself = false;
			
			for(int i=0; i<receivers.length; i++)
			{
				if(agent.getComponentIdentifier().equals(receivers[i]))
				{
					foundself = true;
				}
			}
			
			final int cnt = (!foundself)? receivers.length+1: receivers.length;
			
			final CollectionResultListener<IChatService> lis = new CollectionResultListener<IChatService>(
				cnt, true, new IResultListener<Collection<IChatService>>()
			{
				public void resultAvailable(Collection<IChatService> result) 
				{
					ret.setFinished();
				}

				public void exceptionOccurred(Exception exception)
				{
					ret.setFinished();
				}
			});
			
			for(int i=0; i<receivers.length; i++)
			{
				statusTo(nick, status, image, receivers[i]).addResultListener(new IResultListener<IChatService>()
				{
					public void resultAvailable(IChatService result)
					{
						ret.addIntermediateResultIfUndone(result); // Might return after later exception in service search!?
						lis.resultAvailable(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						lis.exceptionOccurred(exception);
					}
				});
			}
			
			if(!foundself)
			{
				statusTo(nick, status, image, agent.getComponentIdentifier()).addResultListener(new IResultListener<IChatService>()
				{
					public void resultAvailable(IChatService result)
					{
						ret.addIntermediateResultIfUndone(result); // Might return after later exception in service search!?
						lis.resultAvailable(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						lis.exceptionOccurred(exception);
					}
				});
			}
		}
		else //if(receivers.length==0)
		{
			final IIntermediateFuture<IChatService> ifut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("chatservices");
			ifut.addResultListener(new IntermediateDelegationResultListener<IChatService>(ret)
			{
				boolean	finished;
				int cnt	= 0;
				public void customIntermediateResultAvailable(final IChatService chat)
				{
					cnt++;
					chat.status(nick, status, image).addResultListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							ret.addIntermediateResultIfUndone(chat);	// Might be called after concurrent exception in service search!
							
							if(--cnt==0 && finished)
							{
								ret.setFinishedIfUndone();
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							if(--cnt==0 && finished)
							{
								ret.setFinishedIfUndone();
							}
						}
					});
				}
				public void finished()
				{
					finished	= true;
					if(finished && cnt==0)
					{
						ret.setFinishedIfUndone();
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Helper method for posting status to cid.
	 */
	protected IFuture<IChatService> statusTo(final String nick, final String status, final byte[] image, IComponentIdentifier rec)
	{
		final Future<IChatService> ret = new Future<IChatService>();
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IChatService.class, rec)
			.addResultListener(new DelegationResultListener<IChatService>(ret)
		{
			public void customResultAvailable(final IChatService chat)
			{
//				ret.setResult(chat);
				chat.status(nick, status, image).addResultListener(new ExceptionDelegationResultListener<Void, IChatService>(ret)
				{
					public void customResultAvailable(Void result)
					{
						ret.setResult(chat);
					}
				});
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
	 *  @param filepath	The location of the file (possibly changed by user). 
	 */
	public IFuture<Void>	acceptFile(String id, String filepath)
	{
		IFuture<Void>	ret;
		Tuple3<TransferInfo, TerminableIntermediateFuture<Long>, IInputConnection>	tup	= transfers.get(id);
		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(id);
		if(tup!=null)
		{
			TransferInfo	ti	= tup.getFirstEntity();
			if(TransferInfo.STATE_WAITING.equals(ti.getState()))
			{
				ti.setFileName(new File(filepath).getName());
				ti.setFilePath(filepath);
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
				((Future<IOutputConnection>)tup2.getSecondEntity()).setResultIfUndone(sic.getOutputConnection());
				ti.setFileName(new File(filepath).getName());
				ti.setFilePath(filepath);
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
				((Future<?>)tup2.getSecondEntity()).setException(new RuntimeException(TransferInfo.STATE_REJECTED));
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
	 *  @param filepath	The file path, local to the chat component.
	 *  @param cid	The id of a remote chat component.
	 */
	public IFuture<Void>	sendFile(final String filepath, final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();

		IFuture<IChatService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IChatService.class, cid);
		fut.addResultListener(new ExceptionDelegationResultListener<IChatService, Void>(ret)
		{
			public void customResultAvailable(IChatService cs)
			{
				final File file = new File(filepath);
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
				final TransferInfo fi = new TransferInfo(false, null, file.getName(), filepath, cid, file.length(), System.currentTimeMillis() + // Hack!!! assume real time timeout.
					(cid.getRoot().equals(agent.getComponentIdentifier().getRoot()) ? Starter.getLocalDefaultTimeout(agent.getComponentIdentifier()) : Starter.getRemoteDefaultTimeout(agent.getComponentIdentifier())));	// Todo: actual timeout of method!?
				fi.setState(TransferInfo.STATE_WAITING);
				ITerminableFuture<IOutputConnection> fut = cs.startUpload(nick, file.getName(), size, fi.getId());
				transfers2.put(fi.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(fi, fut, null));
				publishEvent(ChatEvent.TYPE_FILE, null, cid, fi);
				fut.addResultListener(new ExceptionDelegationResultListener<IOutputConnection, Void>(ret)
				{
					public void customResultAvailable(IOutputConnection ocon)
					{
						try
						{
							FileInputStream fis = new FileInputStream(new File(fi.getFilePath()));
							doUpload(fi, fis, ocon, cid);
							ret.setResult(null);
						}
						catch(Exception e)
						{
							ret.setException(e);
						}
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
	
	/**
	 *  Send a file to the target component via bytes.
	 *  @param filepath	The file path, local to the chat component.
	 *  @param cid	The id of a remote chat component.
	 */
	public IFuture<Void> sendFile(final String fname, final byte[] data, final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();

		IFuture<IChatService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IChatService.class, cid);
		fut.addResultListener(new ExceptionDelegationResultListener<IChatService, Void>(ret)
		{
			public void customResultAvailable(IChatService cs)
			{
				final long size = data.length;
				
				// Call chat service of receiver (alternative interface)
				String filepath = fname.indexOf(".")!=-1? fname.substring(fname.lastIndexOf(".")): null;
				String name = fname.indexOf(".")!=-1? fname.substring(0, fname.lastIndexOf(".")-1): fname;
				final TransferInfo fi = new TransferInfo(false, null, name, filepath, cid, size, System.currentTimeMillis() + // Hack!!! assume real time timeout.
					(cid.getRoot().equals(agent.getComponentIdentifier().getRoot()) ? Starter.getLocalDefaultTimeout(agent.getComponentIdentifier()) : Starter.getRemoteDefaultTimeout(agent.getComponentIdentifier())));	// Todo: actual timeout of method!?
				fi.setState(TransferInfo.STATE_WAITING);
				ITerminableFuture<IOutputConnection> fut = cs.startUpload(nick, name, size, fi.getId());
				transfers2.put(fi.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(fi, fut, null));
				publishEvent(ChatEvent.TYPE_FILE, null, cid, fi);
				
				fut.addResultListener(new ExceptionDelegationResultListener<IOutputConnection, Void>(ret)
				{
					public void customResultAvailable(IOutputConnection ocon)
					{
						ByteArrayInputStream bis = new ByteArrayInputStream(data);
						doUpload(fi, bis, ocon, cid);
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
	protected boolean	publishEvent(String type, String nick, IComponentIdentifier cid, Object value)
	{
		return publishEvent(type, nick, cid, value, false, null);
	}
	
	/**
	 *  Post an event to registered subscribers.
	 *  @param type	The event type.
	 *  @param nick	The nick name.
	 *  @param cid	The component ID.
	 *  @param value The event value.
	 */
	protected boolean	publishEvent(String type, String nick, IComponentIdentifier cid, Object value, boolean privatemessage, byte[] image)
	{
//		if(cid==null)
//		{
//			Thread.dumpStack();
//		}
//		
		boolean	ret	= false;
		if(subscribers!=null && cid!=null)	// Hack!!! why is cid null?
		{
			ChatEvent	ce	= new ChatEvent(type, nick, cid, value, privatemessage, image);
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
				ret.addIntermediateResult(Long.valueOf(0));
			}
			
			final FileOutputStream fos = new FileOutputStream(ti.getFilePath());
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
						ret.addIntermediateResult(filesize);
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
			} else {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *  Perform an upload.
	 *  Called from file sender.
	 *  Writes bytes from file input stream to output connection.
	 */
	protected void	doUpload(final TransferInfo ti, final InputStream is, final IOutputConnection ocon, final IComponentIdentifier receiver)
	{
		assert TransferInfo.STATE_WAITING.equals(ti.getState()) : ti.getState();
		ti.setState(TransferInfo.STATE_TRANSFERRING);
		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(ti.getId());
		if(tup2!=null)
			transfers2.put(ti.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(ti, tup2.getSecondEntity(), ocon));
		publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
		
		try
		{
//			final FileInputStream fis = new FileInputStream(new File(ti.getFilePath()));
			
			final ISubscriptionIntermediateFuture<Long> fut = ocon.writeFromInputStream(is, agent.getExternalAccess());

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
						is.close();
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
						is.close();
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
	
//	/**
//	 *  Perform an upload.
//	 *  Called from file sender.
//	 *  Writes bytes from file input stream to output connection.
//	 */
//	protected void	doUpload(final TransferInfo ti, final IOutputConnection ocon, final IComponentIdentifier receiver)
//	{
//		assert TransferInfo.STATE_WAITING.equals(ti.getState()) : ti.getState();
//		ti.setState(TransferInfo.STATE_TRANSFERRING);
//		Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>	tup2	= transfers2.get(ti.getId());
//		if(tup2!=null)
//			transfers2.put(ti.getId(), new Tuple3<TransferInfo, ITerminableFuture<IOutputConnection>, IConnection>(ti, tup2.getSecondEntity(), ocon));
//		publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
//		
//		try
//		{
//			final FileInputStream fis = new FileInputStream(new File(ti.getFilePath()));
//			
//			final ISubscriptionIntermediateFuture<Long> fut = ocon.writeFromInputStream(fis, agent.getExternalAccess());
//
//			fut.addResultListener(new IIntermediateResultListener<Long>()
//			{
//				public void resultAvailable(Collection<Long> result)
//				{
//					finished();
//				}
//				public void intermediateResultAvailable(Long filesize)
//				{
//					if(TransferInfo.STATE_ABORTED.equals(ti.getState()))
//					{
//						ocon.close();
//						fut.terminate();
//					}
//					if(ti.update(filesize))
//					{
//						publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
//					}
//				}
//				public void finished()
//				{
//					try
//					{
//						fis.close();
//					}
//					catch(Exception e)
//					{
//					}
//					ocon.close();
//					ti.setState(TransferInfo.STATE_COMPLETED);
//					transfers.remove(ti.getId());
//					transfers2.remove(ti.getId());
//					publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);			
//				}
//				public void exceptionOccurred(Exception exception)
//				{
//					try
//					{
//						fis.close();
//					}
//					catch(Exception e)
//					{
//					}
//					ocon.close();
//					ti.setState(TransferInfo.STATE_CANCELLING.equals(ti.getState()) ? TransferInfo.STATE_ABORTED : TransferInfo.STATE_ERROR);
//					transfers.remove(ti.getId());
//					transfers2.remove(ti.getId());
//					publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
//				}
//			});
//		}
//		catch(Exception e)
//		{
//			ti.setState(TransferInfo.STATE_ERROR);
//			transfers.remove(ti.getId());
//			transfers2.remove(ti.getId());
//			publishEvent(ChatEvent.TYPE_FILE, null, ti.getOther(), ti);
//		}
//	}
	
	//-------- IPropertiesProvider interface --------
	
	/**
	 * 
	 */
	@Reference
	public class PropProvider implements IPropertiesProvider
	{
		protected Future<Void> called = new Future<Void>();
		
		/**
		 * 
		 */
		public IFuture<Void> isCalled()
		{
			return called;
		}
		
		/**
		 *  Update from given properties.
		 */
		public IFuture<Void> setProperties(Properties props)
		{
			String tmp = props.getStringProperty("nickname");
			if(tmp!=null)
				setNickName(tmp);
			tmp = props.getStringProperty("image");
			if(tmp!=null)
			{
				try
				{
					setImage(Base64.decode(tmp.getBytes("UTF-8")));
				}
				catch(UnsupportedEncodingException e)
				{
					throw new RuntimeException(e);
				}
			}
			
			called.setResultIfUndone(null);
			
			return IFuture.DONE;
		}
		
		/**
		 *  Write current state into properties.
		 */
		public IFuture<Properties> getProperties()
		{
			Properties	props	= new Properties();
			// Only save as executing when in normal mode.
			props.addProperty(new Property("nickname", nick));
			if(image!=null)
			{
				try
				{
					props.addProperty(new Property("image", new String(Base64.encode(image), "UTF-8")));
				}
				catch (UnsupportedEncodingException e)
				{
					throw new RuntimeException(e);
				}
			}
			return new Future<Properties>(props);
		}
	}
	
}
