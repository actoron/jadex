package jadex.bridge.service.types.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ServiceOutputConnection implements IOutputConnection
{
	/** The remote output connection. */
	protected IOutputConnection ocon;

	/** The closed flag. */
	protected boolean closed;
	
	/** The buffer. */
	protected List<byte[]> buffer;
	
	/**
	 * 
	 */
	public ServiceOutputConnection()
	{
		this.buffer = new ArrayList<byte[]>();
	}
	
	/**
	 *  Write the content to the stream.
	 *  @param data The data.
	 */
	public IFuture<Void> write(byte[] data)
	{
		Future<Void> ret = new Future<Void>();
		if(ocon!=null)
		{
			ocon.write(data).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			buffer.add(data);
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Close the connection.
	 */
	public void close()
	{
		if(!closed)
		{
			closed = true;
			if(ocon!=null)
				ocon.close();
		}
	}
	
	/**
	 * 
	 */
	public IInputConnection getInputConnection()
	{
		return new ServiceInputConnection();
	}
	
	/**
	 * 
	 */
	protected void setOutputConnection(IOutputConnection ocon)
	{
		if(this.ocon!=null)
			throw new RuntimeException("Connection already set.");
		
		this.ocon = ocon;
		
		while(buffer.size()>0)
		{
			byte[] data = buffer.remove(0);
			ocon.write(data);
		}
		
		if(closed)
		{
			ocon.close();
		}
	}
	
	/**
	 * 
	 */
	public class ServiceInputConnection implements IInputConnection
	{
		public void setOutputConnection(IExternalAccess component, IComponentIdentifier receiver, IOutputConnection ocon)
		{
			ServiceOutputConnection.this.setOutputConnection(ocon);
//			createOutputConnection(component, receiver)
//				.addResultListener(new IResultListener<IOutputConnection>()
//			{
//				public void resultAvailable(IOutputConnection ocon)
//				{
//					System.out.println("has output");
//					ServiceOutputConnection.this.setOutputConnection(ocon);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					System.out.println("excepetion: "+exception);
//				}
//			});
		}
		
//		public ServiceOutputConnection getOutputConnection()
//		{
//			return ServiceOutputConnection.this;
//		}
		
		public int read(byte[] buffer)
		{
			throw new UnsupportedOperationException();
		}
		
		public int read()
		{
			throw new UnsupportedOperationException();
		}
		
		public void close()
		{
			throw new UnsupportedOperationException();
		}
		
		public IIntermediateFuture<Byte> aread()
		{
			throw new UnsupportedOperationException();
		}
	}
	
//	/**
//	 * 
//	 */
//	public IFuture<IOutputConnection> createOutputConnection(final IExternalAccess component, final IComponentIdentifier receiver)
//	{
//		final Future<IOutputConnection> ret = new Future<IOutputConnection>();
//		
//		component.scheduleStep(new IComponentStep<IOutputConnection>()
//		{
//			public IFuture<IOutputConnection> execute(final IInternalAccess ia)
//			{
//				final Future<IOutputConnection> fut = new Future<IOutputConnection>();
//				
//				SServiceProvider.getService(ia.getServiceContainer(), 
//					IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IMessageService, IOutputConnection>(ret)
//				{
//					public void customResultAvailable(IMessageService ms)
//					{
//						ms.createOutputConnection(component.getComponentIdentifier(), receiver)
//							.addResultListener(ia.createResultListener(new DelegationResultListener<IOutputConnection>(fut)));
//					}
//				}));
//				
//				return fut; 
//			}
//		}).addResultListener(new DelegationResultListener<IOutputConnection>(ret));
//		
//		return ret;
//	}
}
