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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;

/**
 * A global cache.
 */
public class GlobalCache
{
//	public static IFilter<Object> TASKFILEFILTER = new FileFilter("Task");
//	
//	public static IFilter<Class<?>> TASKCLASSFILTER = new IFilter<Class<?>>()
//	{
//		public boolean filter(Class<?> obj)
//		{
//			boolean ret = false;
//			try
//			{
//				if(!obj.isInterface() && !Modifier.isAbstract(obj.getModifiers()))
//				{
//					ClassLoader cl = obj.getClassLoader();
//					Class<?> taskcl = Class.forName(ITask.class.getName(), true, cl);
//					ret = SReflect.isSupertype(taskcl, obj);
//				}
//			}
//			catch(Exception e)
//			{
//			}
//			return ret;
//		}
//	};
	
	/** Global task classes */
	protected List<ClassInfo> globaltaskclasses = new ArrayList<ClassInfo>();

	/** Global interfaces */
	protected List<ClassInfo> globalinterfaces = new ArrayList<ClassInfo>();

	/** All classes (without inner and abstract ones) */
	protected List<ClassInfo> allclasses = new ArrayList<ClassInfo>();
	
	
	/**
	 *  Get the globalinterfaces.
	 *  @return The globalinterfaces.
	 */
	public List<ClassInfo> getGlobalInterfaces()
	{
		return globalinterfaces;
	}

	/**
	 *  Returns the global task classes.
	 *  @return The global task classes.
	 */
	public List<ClassInfo> getGlobalTaskClasses()
	{
		return globaltaskclasses;
	}
	
	/**
	 *  Returns all classes.
	 *  @return The set of all classes.
	 */
	public List<ClassInfo> getGlobalAllClasses()
	{
		return allclasses;
	}
	
//	/**
//	 *  Scan for task classes.
//	 */
//	public static final List<ClassInfo> scanForInterfaces(ClassLoader cl)
//	{
//		return scanForTaskClasses(cl, new FileFilter(".class"), new IFilter<Class<?>>()
//		{
//			public boolean filter(Class<?> obj)
//			{
//				return obj.isInterface();
//			}
//		});
//	}
	
	/**
	 *  Scan for task classes.
	 */
	public static final Set<ClassInfo>[] scanForClasses(ClassLoader cl)
	{
		final Set<ClassInfo> res1 = new HashSet<ClassInfo>();
		final Set<ClassInfo> res2 = new HashSet<ClassInfo>();
		final Set<ClassInfo> res3 = new HashSet<ClassInfo>();
		
		scanForClasses(cl, new FileFilter("$", false), new IFilter<Class<?>>()
		{
			public boolean filter(final Class<?> obj)
			{
				boolean ret = false;
				try
				{
					if(!obj.isInterface())
					{
						if(!Modifier.isAbstract(obj.getModifiers()) && Modifier.isPublic(obj.getModifiers()))
						{
							ClassInfo ci = new ClassInfo(obj.getName());
							res3.add(ci);
							if(!res1.contains(ci))
							{
								ClassLoader cl = obj.getClassLoader();
								Class<?> taskcl = Class.forName(ITask.class.getName(), true, cl);
								ret = SReflect.isSupertype(taskcl, obj);
								if(ret)
								{
									res1.add(ci);
								}
							}
						}
					}
					else
					{
						// collect interfaces
						ClassInfo ci = new ClassInfo(obj.getName());
						res2.add(ci);
						res3.add(ci);
					}
				}
				catch(Exception e)
				{
				}
				return ret;
			}
		});
		
		return new Set[]{res1, res2, res3};
	}
	
	/**
	 *  Scan for task classes.
	 */
	public static final List<ClassInfo> scanForClasses(ClassLoader cl, IFilter<Object> filefilter, IFilter<Class<?>> classfilter)
	{
		final List<ClassInfo> taskclasses = new ArrayList<ClassInfo>();
		
		ISubscriptionIntermediateFuture<Class<?>> fut = SReflect.asyncScanForClasses(cl, filefilter, classfilter, -1, false);
		fut.addResultListener(new SwingIntermediateResultListener<Class<?>>(new IIntermediateResultListener<Class<?>>()
		{
			public void intermediateResultAvailable(Class<?> result)
			{
//				System.out.println("Found: "+result.getName());
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
	

	/**
	 *  Scan for task classes.
	 */
	public static final List<ClassInfo> asyncScanForClasses(ClassLoader cl, IFilter<Object> filefilter, IFilter<Class<?>> classfilter)
	{
		final List<ClassInfo> taskclasses = new ArrayList<ClassInfo>();
		
		ISubscriptionIntermediateFuture<Class<?>> fut = SReflect.asyncScanForClasses(cl, filefilter, classfilter, -1, false);
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
	
	/**
	 * 
	 */
	public static class FileFilter implements IFilter<Object>
	{
		/** The filename. */
		protected String filename;
		
		/** The contains flag. */
		protected boolean contains;
		
		/**
		 * 
		 */
		public FileFilter(String filename, boolean contains)
		{
			this.filename = filename;
			this.contains = contains;
		}
		
		/**
		 * 
		 */
		public boolean filter(Object obj)
		{
			if(filename==null)
				return true;
			
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
			
			return fn.endsWith(".class") && (contains? fn.indexOf(filename)!=-1: fn.indexOf(filename)==-1);
		}
	}
}
