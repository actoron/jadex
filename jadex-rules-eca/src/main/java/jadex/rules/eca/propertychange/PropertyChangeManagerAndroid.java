package jadex.rules.eca.propertychange;

import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;


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
					pcls = new IdentityHashMap<Object, Object>(); // values may change, therefore identity hash map
				Object pcl = pcls.get(object);
				
				if(pcl==null)
				{
					pcl = createPCL(eventadder);
				}
				
				// Do not use Class.getMethod (slow).
				Method	meth = SReflect.getMethod(object.getClass(), "addPropertyChangeListener", PCL);
				if(meth!=null)
					meth.invoke(object, new Object[]{pcl});				
	
				pcls.put(object, pcl);
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
	}
	
	/**
	 *  Deregister a value for observation.
	 *  if its a bean then remove the property listener.
	 */
	public void	removePropertyChangeListener(Object object)
	{
		if(object!=null)
		{
//			System.out.println("deregister ("+cnt[0]+"): "+value);
			// Stop listening for bean events.
			if(pcls!=null)
			{
				Object pcl = pcls.remove(object);
				if(pcl!=null)
				{
					try
					{
//						System.out.println(getTypeModel().getName()+": Deregister: "+value+", "+type);						
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
	}

}
