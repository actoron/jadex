package jadex.micro.features.impl;

import jadex.bridge.IConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.component.streams.StreamPacket;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.commons.MethodInfo;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroModel;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.micro.annotation.AgentStreamArrived;
import jadex.micro.annotation.OnMessage;
import jadex.micro.annotation.OnStream;

/**
 *  Extension to allow message injection in agent methods.
 */
public class MicroMessageComponentFeature extends MessageComponentFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IMessageFeature.class, MicroMessageComponentFeature.class);
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public MicroMessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- micro agent specific message handling --------
	
	/**
	 *  Called for all messages without matching message handlers.
	 *  Can be overwritten by specific message feature implementations (e.g. micro or BDI).
	 */
	protected void processUnhandledMessage(final ISecurityInfo secinf, final IMsgHeader header, final Object body)
	{
		///WTF?
//		if(body instanceof StreamPacket)
//		{
			MicroModel model = (MicroModel)component.getModel().getRawModel();
			MethodInfo mi = model.getAgentMethod(AgentMessageArrived.class);
		
			if(mi!=null)
			{
				MicroLifecycleComponentFeature.invokeMethod(getInternalAccess(), AgentMessageArrived.class, new Object[]{secinf, header, body, body != null ? body.getClass() : null})
					.addResultListener(new IResultListener<Void>()
				{
					@Override
					public void resultAvailable(Void result)
					{
						// OK -> ignore
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						getComponent().getLogger().warning("Exception during message handling: "+exception);
					}
				});
			}
			else
			{
				MicroLifecycleComponentFeature.invokeMethod(getInternalAccess(), OnMessage.class, new Object[]{secinf, header, body, body != null ? body.getClass() : null})
					.addResultListener(new IResultListener<Void>()
				{
					@Override
					public void resultAvailable(Void result)
					{
						// OK -> ignore
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						getComponent().getLogger().warning("Exception during message handling: "+exception);
					}
				});
			}
//		}
	}	
	
	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con)
	{
		MicroModel model = (MicroModel)component.getModel().getRawModel();
		MethodInfo mi = model.getAgentMethod(AgentStreamArrived.class);
	
		if(mi!=null)
		{
			MicroLifecycleComponentFeature.invokeMethod(getInternalAccess(), AgentStreamArrived.class, new Object[]{con})
				.addResultListener(new IResultListener<Void>()
			{
				@Override
				public void resultAvailable(Void result)
				{
					// OK -> ignore
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					getComponent().getLogger().warning("Exception during message handling: "+exception);
				}
			});
		}
		else
		{
			MicroLifecycleComponentFeature.invokeMethod(getInternalAccess(), OnStream.class, new Object[]{con})
				.addResultListener(new IResultListener<Void>()
			{
				@Override
				public void resultAvailable(Void result)
				{
					// OK -> ignore
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					getComponent().getLogger().warning("Exception during message handling: "+exception);
				}
			});
		}
	}
}