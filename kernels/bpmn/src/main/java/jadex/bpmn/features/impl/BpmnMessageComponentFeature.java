package jadex.bpmn.features.impl;

import java.util.Iterator;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.MessageComponentFeature;
import jadex.bridge.service.types.security.ISecurityInfo;
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
	
	
	//-------- micro agent specific message handling --------
	
	/**
	 *  Called for all messages without matching message handlers.
	 *  Can be overwritten by specific message feature implementations (e.g. micro or BDI).
	 */
	protected void processUnhandledMessage(final ISecurityInfo secinf, final IMsgHeader header, final Object body)
	{
//		System.out.println("rec msg: "+message);
		
		IInternalBpmnComponentFeature bcf = (IInternalBpmnComponentFeature)getComponent().getFeature(IBpmnComponentFeature.class);

		// Iterate through process threads and dispatch message to first
		// waiting and fitting one (filter check).
		boolean processed = false;
		for(Iterator<ProcessThread> it=bcf.getTopLevelThread().getAllThreads().iterator(); it.hasNext() && !processed; )
		{
			ProcessThread pt = it.next();
			if(pt.isWaiting())
			{
				// TODO: allow filtering also header and/or secinf???
				IFilter<Object> filter = pt.getWaitFilter();
				if(filter!=null && filter.filter(body))
				{
//					System.out.println("Dispatched to thread: "+getComponent().getComponentIdentifier().getLocalName()+" "+System.identityHashCode(body)+", "+pt);
					bcf.notify(pt.getActivity(), pt, body);
					processed = true;
				}
			}
		}
		
		if(!processed)
		{
			bcf.getMessages().add(body);
//			System.out.println("Dispatched to waitqueue: "+getComponent().getComponentIdentifier().getLocalName()+" "+body);
		}
	}
	
}
