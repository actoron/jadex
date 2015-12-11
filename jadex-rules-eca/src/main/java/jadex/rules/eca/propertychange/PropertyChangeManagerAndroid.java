package jadex.rules.eca.propertychange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.IFuture;


/**
 * Only supports Usage of jadex.commons.beans types in watched objects, because
 * java.beans is not available for Android  
 */
public class PropertyChangeManagerAndroid extends PropertyChangeManager
{
	/**  
	 *  Add a property change listener.
	 */
	public void	addPropertyChangeListener(Object object, final IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
	{
		if(object!=null)
		{
			// Invoke addPropertyChangeListener on value
			try
			{
				if(pcls==null)
					pcls = new IdentityHashMap<Object, Map<Object, Object>>(); // values may change, therefore identity hash map
				Map<Object, Object> mypcls = pcls.get(object);
				Object pcl = mypcls==null? null: mypcls.get(eventadder);
				
				if(pcl==null)
				{
					pcl = createPCL(eventadder);
				}
				
				if(mypcls==null)
				{
					mypcls = new IdentityHashMap<Object, Object>();
					pcls.put(object, mypcls);
				}
				
				mypcls.put(eventadder, pcl);
				
				// Do not use Class.getMethod (slow).
				Method	meth = SReflect.getMethod(object.getClass(), "addPropertyChangeListener", PCL);
				if(meth!=null)
					meth.invoke(object, new Object[]{pcl});				
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
	}
	
	/**
	 *  Deregister a value for observation.
	 *  if its a bean then remove the property listener.
	 */
	public void	removePropertyChangeListener(Object object, IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
	{
		if(object!=null)
		{
//			System.out.println("deregister ("+cnt[0]+"): "+value);
			// Stop listening for bean events.
			if(pcls!=null)
			{
				Map<Object, Object> mypcls = pcls.get(object);
				if(mypcls!=null)
				{
					if(eventadder!=null)
					{
						Object pcl = mypcls.remove(eventadder);
						removePCL(object, pcl);
					}
					else
					{
						for(Object pcl: mypcls.values())
						{
							removePCL(object, pcl);
						}
						mypcls.clear();
					}
					if(mypcls.size()==0)
						pcls.remove(object);
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void removePCL(Object object, Object pcl)
	{
		if(pcl!=null)
		{
			try
			{
//				System.out.println(getTypeModel().getName()+": Deregister: "+value+", "+type);						
				// Do not use Class.getMethod (slow).
				Method	meth = SReflect.getMethod(object.getClass(), "removePropertyChangeListener", PCL);
				if(meth!=null)
					meth.invoke(object, new Object[]{pcl});
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
	}
}
