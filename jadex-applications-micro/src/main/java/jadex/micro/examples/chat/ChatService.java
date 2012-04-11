package jadex.micro.examples.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.annotation.Binding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;


/**
 *  Chat service implementation.
 */
@Service
public class ChatService implements IChatService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
	
	/** The chat gui. */
	protected ChatPanel chatpanel;
	
	//-------- methods --------
	
	/**
	 *  Called on startup.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		final IExternalAccess	exta	= agent.getExternalAccess();
		agent.getServiceContainer().searchService(IClockService.class, Binding.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(final IClockService clock)
			{
				ChatPanel.createGui(exta, clock)
					.addResultListener(new ExceptionDelegationResultListener<ChatPanel, Void>(ret)
				{
					public void customResultAvailable(ChatPanel result)
					{
						chatpanel = result;
						ret.setResult(null);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@ServiceShutdown
	public IFuture<Void> shutdown()
	{
		chatpanel.dispose();
		
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
				chat.status(STATE_DEAD);
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
	
	/**
	 *  Post a message
	 *  @param text The text message.
	 */
	public IFuture<Void>	message(String text)
	{
		chatpanel.addMessage(IComponentIdentifier.CALLER.get(), text);
		return IFuture.DONE;
	}
	
	/**
	 *  Post a status change.
	 *  @param status The new status.
	 */
	public IFuture<Void>	status(String status)
	{
		chatpanel.setUserState(IComponentIdentifier.CALLER.get(), status);
		return IFuture.DONE;		
	}
	
	/**
	 *  Send a file.
	 *  @param filename The filename.
	 *  @param con The connection.
	 */
	public ITerminableIntermediateFuture<Long> sendFile(String filename, final long size, final IInputConnection con)
	{
		final TerminableIntermediateFuture<Long> ret = new TerminableIntermediateFuture<Long>();
		final IComponentIdentifier sender = IComponentIdentifier.CALLER.get();

		final FileInfo fi = new FileInfo(new File(filename), sender, size, 0, FileInfo.WAITING);
		fi.setCancelCommand(new Runnable()
		{
			public void run()
			{
				ret.terminate();
			}
		});
		chatpanel.updateDownload(fi);

		// Check if user wants file
		IFuture<File> fut = chatpanel.acceptFile(filename, size, sender);
		fut.addResultListener(new IResultListener<File>()
		{
			public void resultAvailable(final File f)
			{
				try
				{
					fi.setFile(f);
					chatpanel.updateDownload(fi);
					final long[] cnt = new long[1];
//					final File f = new File("./"+filename);
					final FileOutputStream fos = new FileOutputStream(f);

					// Enable sending
					ret.addIntermediateResult(new Long(0));
					
					final ISubscriptionIntermediateFuture<byte[]> fut = ((IInputConnection)con).aread();
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
							if(fi.isFinished())
							{
								ret.setFinishedIfUndone();
								fut.terminate();
								return;
							}
							
							cnt[0] += result.length;
			//				if(cnt[0]%1000==0)
			//					System.out.println("bytes: "+cnt[0]);
							try
							{
								fos.write(result);
								fi.setState(FileInfo.TRANSFERRING);
								fi.setDone(cnt[0]);
								chatpanel.updateDownload(fi);
								boolean set = ret.addIntermediateResultIfUndone(new Long(cnt[0]));
								// todo: close con?
							}
							catch(Exception e)
							{
								fi.setState(FileInfo.ERROR);
								chatpanel.updateDownload(fi);
								ret.setExceptionIfUndone(e);
								e.printStackTrace();
							}
						}
						
						public void finished()
						{
							try
							{
								fos.close();
								if(cnt[0]==size)
								{
									System.out.println("Received file: "+f.getAbsolutePath()+", size: "+cnt[0]);
									fi.setState(FileInfo.COMPLETED);
									boolean set = ret.setFinishedIfUndone();
								}
								else
								{
									fi.setState(FileInfo.ABORTED);
									ret.setExceptionIfUndone(new RuntimeException(FileInfo.ABORTED));
								}
								chatpanel.updateDownload(fi);
								
								// todo: close con?
							}
							catch(Exception e)
							{
								fi.setState(FileInfo.ERROR);
								chatpanel.updateDownload(fi);
								ret.setExceptionIfUndone(e);
								e.printStackTrace();
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setExceptionIfUndone(exception);
							System.out.println("ex:"+exception);
						}
					}));
				}
				catch(Exception e)
				{
					ret.setExceptionIfUndone(e);
					e.printStackTrace();
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				fi.setState(FileInfo.REJECTED);
				chatpanel.updateDownload(fi);
				ret.setException(exception);
			}
		});
			
		return ret;
	}
}
