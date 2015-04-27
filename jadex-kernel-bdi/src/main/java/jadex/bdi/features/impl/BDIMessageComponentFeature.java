package jadex.bdi.features.impl;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.MessageComponentFeature;

/**
 *  Extension to allow message injection in agent methods.
 */
public class BDIMessageComponentFeature extends MessageComponentFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IMessageFeature.class, BDIMessageComponentFeature.class);
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public BDIMessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- IInternalMessageFeature interface --------
	
	/**
	 *  Helper method to override message handling.
	 *  May be called from external threads.
	 */
	protected IComponentStep<Void> createHandleMessageStep(IMessageAdapter message)
	{
		return new HandleBDIMessageStep(message);
	}
	
	/**
	 *  Step to handle a message.
	 */
	public class HandleBDIMessageStep	extends HandleMessageStep
	{
		public HandleBDIMessageStep(IMessageAdapter message)
		{
			super(message);
		}

		/**
		 *  Extracted to allow overriding behaviour.
		 *  @return true, when at least one matching handler was found.
		 */
		protected boolean invokeHandlers(IMessageAdapter message)
		{
			IInternalBDIAgentFeature bdif = (IInternalBDIAgentFeature)getComponent().getComponentFeature(IBDIAgentFeature.class);
			bdif.getState().addAttributeValue(bdif.getAgent(), OAVBDIRuntimeModel.agent_has_inbox, message);
//			System.out.println("message moved to inbox: "+getAgentAdapter().getComponentIdentifier().getLocalName()
//				+"("+state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state)+")"+", "+message);
			return true;
		}
	}

	/**
	 *  Helper method to override stream handling.
	 *  May be called from external threads.
	 */
	protected IComponentStep<Void> createHandleStreamStep(IConnection con)
	{
		return new HandleBDIStreamStep(con);
	}
	
	/**
	 *  Step to handle a message.
	 */
	public class HandleBDIStreamStep	extends HandleStreamStep
	{
		public HandleBDIStreamStep(IConnection con)
		{
			super(con);
		}

		/**
		 *  Extracted to allow overriding behaviour.
		 *  @return true, when at least one matching handler was found.
		 */
		protected boolean invokeHandlers(IConnection con)
		{
			throw new UnsupportedOperationException("Streams not supported");
		}
	}
}
