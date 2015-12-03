package jadex.bpmn.features.impl;

import java.util.Iterator;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.commons.IFilter;

/**
 *  Extension to allow message injection in agent methods.
 */
public class BpmnMessageComponentFeature extends MessageComponentFeature
{
	//-------- constants --------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IMessageFeature.class, BpmnMessageComponentFeature.class);
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public BpmnMessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
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
		return new HandleBpmnMessageStep(message);
	}
	
	/**
	 *  Step to handle a message.
	 */
	public class HandleBpmnMessageStep	extends HandleMessageStep
	{
		public HandleBpmnMessageStep(IMessageAdapter message)
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
			
//			System.out.println("rec msg: "+message);
			
			if(!ret)
			{
				IInternalBpmnComponentFeature bcf = (IInternalBpmnComponentFeature)getComponent().getComponentFeature(IBpmnComponentFeature.class);

				// Iterate through process threads and dispatch message to first
				// waiting and fitting one (filter check).
				boolean processed = false;
				for(Iterator<ProcessThread> it=bcf.getTopLevelThread().getAllThreads().iterator(); it.hasNext() && !processed; )
				{
					ProcessThread pt = it.next();
					if(pt.isWaiting())
					{
						IFilter<Object> filter = pt.getWaitFilter();
						if(filter!=null && filter.filter(message))
						{
//							System.out.println("Dispatched to thread: "+getComponent().getComponentIdentifier().getLocalName()+" "+System.identityHashCode(message)+", "+pt);
							bcf.notify(pt.getActivity(), pt, message);
//							((DefaultActivityHandler)getActivityHandler(pt.getActivity())).notify(pt.getActivity(), BpmnInterpreter.this, pt, message);
							processed = true;
						}
					}
				}
				
				if(!processed)
				{
					bcf.getMessages().add(message);
//					messages.add(message);
//					System.out.println("Dispatched to waitqueue: "+getComponent().getComponentIdentifier().getLocalName()+" "+message);
				}
			}
			
			return ret;
		}
	}

	/**
	 *  Helper method to override stream handling.
	 *  May be called from external threads.
	 */
	protected IComponentStep<Void> createHandleStreamStep(IConnection con)
	{
		return new HandleBpmnStreamStep(con);
	}
	
	/**
	 *  Step to handle a message.
	 */
	public class HandleBpmnStreamStep	extends HandleStreamStep
	{
		public HandleBpmnStreamStep(IConnection con)
		{
			super(con);
		}

		/**
		 *  Extracted to allow overriding behaviour.
		 *  @return true, when at least one matching handler was found.
		 */
		protected boolean invokeHandlers(IConnection con)
		{
			boolean	ret	= super.invokeHandlers(con);
			
			if(!ret)
			{
				IInternalBpmnComponentFeature bcf = (IInternalBpmnComponentFeature)getComponent().getComponentFeature(IBpmnComponentFeature.class);

				// Iterate through process threads and dispatch message to first
				// waiting and fitting one (filter check).
				boolean processed = false;
				for(Iterator<ProcessThread> it=bcf.getTopLevelThread().getAllThreads().iterator(); it.hasNext() && !processed; )
				{
					ProcessThread pt = (ProcessThread)it.next();
					if(pt.isWaiting())
					{
						IFilter<Object> filter = pt.getWaitFilter();
						if(filter!=null && filter.filter(con))
						{
							bcf.notify(pt.getActivity(), pt, con);
//							((DefaultActivityHandler)getActivityHandler(pt.getActivity())).notify(pt.getActivity(), BpmnInterpreter.this, pt, message);
							processed = true;
						}
					}
				}
				
				if(!processed)
				{
					bcf.getStreams().add(con);
//					System.out.println("Dispatched to waitqueue: "+message);
				}
			}
			
			return ret;
		}
	}
}
