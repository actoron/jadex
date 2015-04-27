package jadex.bdi.runtime.impl;

import jadex.bdi.model.IMParameter;
import jadex.bdi.model.IMParameterSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *  Find and call a service.
 */
public class ServiceCallPlan extends Plan
{
	/** The service name. */
	protected String service;
	
	/** The method. */
	protected String method;
	
	/**
	 * 
	 */
	public ServiceCallPlan(String service, String method)
	{
		this.service = service;
		this.method = method;
	}
	
	public void body()
	{
//		try
//		{
		boolean	success	= false;
//		String	service	= (String)getParameter("service").getValue();
//		String	method	= (String)getParameter("method").getValue();
//		Object[]	args	= (Object[])getParameter("args").getValue();
//		Object[] args = new Object[0];
		
		IIntermediateFuture<?>	services	= getInterpreter().getComponentFeature(IRequiredServicesFeature.class).getRequiredServices(service);
		// Todo: implement suspendable intermediate futures.
//		while(!success && services.hasNextIntermediateResult(this))
		Collection<?>	results	= services.get();//this);
//		System.out.println("received: "+results);
		for(Object proxy: results)
		{
			try
			{
//				Object	proxy	= services.getNextIntermediateResult(this);
				Method[]	meths	= SReflect.getMethods(proxy.getClass(), method);	
				Object[] myargs = createArguments(meths[0]);
//				System.out.println("invoking service, args: "+SUtil.arrayToString(myargs));
				Object	res	= meths[0].invoke(proxy, myargs);
				if(res instanceof IFuture<?>)
				{
					Object resu = ((IFuture<?>)res).get();//this);
//					System.out.println("invoked, result: "+resu);
					// todo: set return value on parameter
				}
				success	= true;
			}
			catch(Exception e)
			{
//				e.printStackTrace();
			}
		}
		
		if(!success)
			fail();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}
	
	/**
	 * 
	 */
	protected Object[] createArguments(Method method)
	{
		Class<?>[] mptypes = method.getParameterTypes();
		Object[] ret = new Object[]{mptypes.length};
		
		List<IParameter> params = SUtil.arrayToList(getParameters());
		List<IParameterSet> paramsets = SUtil.arrayToList(getParameterSets());
		
		for(int i=0; i<mptypes.length; i++)
		{
			boolean set = false;
			for(Iterator<IParameter> it = params.iterator(); it.hasNext(); )
			{
				IParameter p = it.next();
				
				IMParameter mp = (IMParameter)p.getModelElement();
				if(OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(mp.getDirection())
					|| OAVBDIMetaModel.PARAMETER_DIRECTION_INOUT.equals(mp.getDirection()))
				{
					if(SReflect.isSupertype(mptypes[i], mp.getClazz()))
					{
						ret[i] = p.getValue();
						set = true;
						it.remove();
						break;
					}
				}
				else
				{
					it.remove();
				}
			}
				
			if(!set)
			{
				for(Iterator<IParameterSet> it = paramsets.iterator(); it.hasNext(); )
				{
					IParameterSet ps = it.next();
					
					IMParameterSet mps = (IMParameterSet)ps.getModelElement();
					if(OAVBDIMetaModel.PARAMETER_DIRECTION_IN.equals(mps.getDirection()) && mptypes[i].isArray())
					{
						if(SReflect.isSupertype(mptypes[i].getComponentType(), mps.getClazz()))
						{
							ret[i] = ps.getValues();
							set = true;
							it.remove();
							break;
						}
					}
					else
					{
						it.remove();
					}
				}
			}
			
			if(!set)
				throw new RuntimeException("Could not map: "+mptypes[i]);
		}
		
		return ret;
	}
}
