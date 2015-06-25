package jadex.bpmn.features.impl;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.ProcessServiceInvocationHandler;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.component.ProvidedServicesComponentFeature;
import jadex.commons.IValueFetcher;
import jadex.commons.collection.MultiCollection;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Overriden to allow for service implementations as BPMN processes using signal events.
 */
public class BpmnProvidedServicesFeature extends ProvidedServicesComponentFeature
{
	//-------- constructors --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BpmnProvidedServicesFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Init a service.
	 *  Overriden to allow for service implementations as BPMN processes using signal events.
	 */
	protected Object createServiceImplementation(ProvidedServiceInfo info, IValueFetcher fetcher) throws Exception
	{
		Object ret = null;
		ProvidedServiceImplementation	impl	= info.getImplementation();
		MBpmnModel model = (MBpmnModel)getComponent().getModel().getRawModel();
		
		// Service implementation inside BPMN: find start events for service methods.
		// Find service without implementation
		if(impl!=null && impl.getValue()==null && impl.getClazz()==null && info.getName()!=null)
		{
			// Build map of potentially matching events: method name -> {list of matching signal event activities}
			MultiCollection<String, MActivity>	events	= new MultiCollection<String, MActivity>();
//			List<MActivity>	starts	= this.bpmnmodel.getStartActivities(pool, lane);
			List<MActivity>	starts	= model.getStartActivities();
			for(int i=0; starts!=null && i<starts.size(); i++)
			{
				MActivity	act	= (MActivity)starts.get(i);
				if(MBpmnModel.EVENT_START_MULTIPLE.equals(act.getActivityType())
					&& info.getName().equals(act.getName()))
				{
					List<MSequenceEdge>	edges	= act.getOutgoingSequenceEdges();
					for(int k=0; k<edges.size(); k++)
					{
						MActivity	target	= ((MSequenceEdge)edges.get(k)).getTarget();
						if(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL.equals(target.getActivityType()))
						{
							events.add(target.getName(), target);
						}
					}
				}
				else if(MBpmnModel.EVENT_START_SIGNAL.equals(act.getActivityType())
					&& act.getName()!=null && act.getName().startsWith(info.getName()+"."))
				{
					events.add(act.getName().substring(info.getName().length()+1), act);
				}
			}

			// Find matching events for each method.
			Class<?> type = info.getType().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports());
			Method[] meths = type.getMethods();
			Map<String, MActivity>	methods	= new HashMap<String, MActivity>(); // method -> event.
			for(int i=0; i<meths.length; i++)
			{
				Collection<MActivity>	es	= events.getCollection(meths[i].getName());
				for(Iterator<MActivity> it=es.iterator(); it.hasNext(); )
				{
					MActivity	event	= it.next();
					if(event.getPropertyNames().length==meths[i].getParameterTypes().length)
					{
						if(methods.containsKey(meths[i]))
						{
							throw new RuntimeException("Ambiguous start events found for service method: "+meths[i]);
						}
						else
						{
							methods.put(meths[i].toString(), event);
						}
					}
				}
				
				if(!methods.containsKey(meths[i].toString()))
				{
					throw new RuntimeException("No start event found for service method: "+meths[i]);
				}
			}

//			System.out.println("Found mapping: "+methods);
			// Todo: interceptors
			ret = Proxy.newProxyInstance(getComponent().getClassLoader(), new Class[]{info.getType().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports())}, 
				new ProcessServiceInvocationHandler(getComponent(), methods));
		}
		
		// External service implementation
		else
		{
			ret	= super.createServiceImplementation(info, fetcher);
		}
		
		return ret;
	}
}