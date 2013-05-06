package jadex.bpmn.editor.gui;

import jadex.bpmn.model.task.ITask;
import jadex.bridge.ClassInfo;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * 
 * A global cache.
 *
 */
public class GlobalCache
{
	/** Global task classes */
	protected List<ClassInfo> globaltaskclasses = new ArrayList<ClassInfo>();
	
	/**
	 *  Returns the global task classes.
	 *  @return The global task classes.
	 */
	public List<ClassInfo> getGlobalTaskClasses()
	{
		return globaltaskclasses;
	}
	
	/**
	 *  Scan for task classes.
	 */
	public static final List<ClassInfo> scanForTaskClasses(ClassLoader cl)
	{
		final List<ClassInfo> taskclasses = new ArrayList<ClassInfo>();
		
		ISubscriptionIntermediateFuture<Class<?>> fut = SReflect.asyncScanForClasses(cl, new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				String	fn	= "";
				if(obj instanceof File)
				{
					File	f	= (File)obj;
					fn	= f.getName();
				}
				else if(obj instanceof JarEntry)
				{
					JarEntry	je	= (JarEntry)obj;
					fn	= je.getName();
				}
				
				return fn.indexOf("Task")!=-1;
			}
		}, new IFilter<Class<?>>()
		{
			public boolean filter(Class<?> obj)
			{
				boolean ret = false;
				try
				{
					if(!obj.isInterface() && !Modifier.isAbstract(obj.getModifiers()))
					{
						ClassLoader cl = obj.getClassLoader();
						Class<?> taskcl = Class.forName(ITask.class.getName(), true, cl);
						ret = SReflect.isSupertype(taskcl, obj);
					}
				}
				catch(Exception e)
				{
				}
				return ret;
			}
		}, -1);
		fut.addResultListener(new SwingIntermediateResultListener<Class<?>>(new IIntermediateResultListener<Class<?>>()
		{
			public void intermediateResultAvailable(Class<?> result)
			{
				taskclasses.add(new ClassInfo(result));
			}
			public void finished()
			{
			}
			public void resultAvailable(Collection<Class<?>> result)
			{
			}
			public void exceptionOccurred(Exception exception)
			{
			}
		}));
		fut.get(new ThreadSuspendable());
		
		return taskclasses;
	}
}
