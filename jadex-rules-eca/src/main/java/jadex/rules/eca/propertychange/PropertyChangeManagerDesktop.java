package jadex.rules.eca.propertychange;

import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.future.IFuture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;

/**
 * Supports Usage of java.beans and jadex.commons.beans types in watched objects.  
 */
public class PropertyChangeManagerDesktop extends PropertyChangeManager
{
	/** The argument types for alternative property change listener adding/removal (cached for speed). */
	protected static Class<?>[]	JAVABEANS_PCL	= new Class[]{java.beans.PropertyChangeListener.class};
	
	protected PropertyChangeManagerDesktop()
	{
		super();
	}
	
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
				
				// check if java.beans or jadex.commons are used:
				boolean javaBeans = false;
				// Do not use Class.getMethod (slow).
				Method	meth = SReflect.getMethod(object.getClass(), "addPropertyChangeListener", PCL);
				if (meth == null) {
					meth = SReflect.getMethod(object.getClass(), "addPropertyChangeListener", JAVABEANS_PCL);
					if (meth != null) {
						javaBeans = true;
					}
				}
				
				if(pcl==null)
				{
					PropertyChangeListener jadexPcl = createPCL(eventadder);
					if (javaBeans) {
						pcl = wrapJadexPcl(jadexPcl);
					} else {
						pcl = jadexPcl;
					}
				}
				
				if(meth!=null)
					meth.invoke(object, new Object[]{pcl});	
				pcls.put(object, pcl);
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
	}
	

	private java.beans.PropertyChangeListener wrapJadexPcl(final jadex.commons.beans.PropertyChangeListener pcl)
	{
		return new java.beans.PropertyChangeListener()
		{
			
			@Override
			public void propertyChange(java.beans.PropertyChangeEvent evt)
			{
				PropertyChangeEvent jadexPCE = new PropertyChangeEvent(evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
				jadexPCE.setPropagationId(evt.getPropagationId());
				pcl.propertyChange(jadexPCE);
			}
		};
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
						Method	meth = null;
						if (pcl instanceof jadex.commons.beans.PropertyChangeListener) {
							meth = SReflect.getMethod(object.getClass(), "removePropertyChangeListener", PCL);
						} else {
							meth = SReflect.getMethod(object.getClass(), "removePropertyChangeListener", JAVABEANS_PCL);
						}
						
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
