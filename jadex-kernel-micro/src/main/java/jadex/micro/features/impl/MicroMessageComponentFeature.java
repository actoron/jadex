package jadex.micro.features.impl;

import jadex.bridge.IConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.micro.annotation.AgentMessageArrived;

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
	
	//-------- IInternalMessageFeature interface --------
	
	/**
	 *  Inform the component that a message has arrived.
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		getComponent().getComponentFeature(IExecutionFeature.class)
			.scheduleStep(new HandleMicroMessageStep(message));
	}
	
	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con)
	{
		throw new UnsupportedOperationException();
//		getComponent().getComponentFeature(IExecutionFeature.class)
//			.scheduleStep(new HandleStreamStep(con));		
	}
	
	/**
	 *  Step to handle a message.
	 */
	public class HandleMicroMessageStep	extends HandleMessageStep
	{
		public HandleMicroMessageStep(IMessageAdapter message)
		{
			super(message);
		}

		/**
		 *  Extracted to allow overriding behaviour.
		 *  @return true, when at least one matching handler was found.
		 */
		protected boolean invokeHandlers(IMessageAdapter message)
		{
			boolean	ret	= super.invokeHandlers(message);
			
			if(!ret)
			{
				MicroLifecycleComponentFeature.invokeMethod(getComponent(), AgentMessageArrived.class, new Object[]{message, message.getParameterMap(), message.getMessageType()});
			}
			
			return ret;
		}
	}
}
