package jadex.bpmn.editor;

import java.io.File;
import java.util.Collection;
import java.util.jar.JarEntry;

import jadex.commons.IFilter;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.gui.SDirScan;

/**
 *  Test to find perm gen space error.
 */
public class ScanTest
{
	public static void	main(String[] args) throws InterruptedException
	{
		final int[]	cnt	= new int[2];
		IFilter<Object>	filefilter	= new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				System.out.println("Filter"+(++cnt[0])+": "+obj);
				
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
				
				return fn.endsWith(".class");
			}
		};
		IFilter<Class<?>>	classfilter	= new IFilter<Class<?>>()
		{
			public boolean filter(Class<?> obj)
			{
				System.out.println("Class filter"+(++cnt[1])+": "+obj);
				return false;
			}
		};

//		ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
		
		SDirScan.asyncScanForClasses(ScanTest.class.getClassLoader(), filefilter, classfilter, -1, true)
			.addResultListener(new IIntermediateResultListener<Class<?>>()
		{
			public void exceptionOccurred(Exception exception)
			{
			}
			public void finished()
			{
			}
			public void intermediateResultAvailable(Class< ? > result)
			{
			}
			public void resultAvailable(Collection<Class< ? >> result)
			{
			}
		});
		
		Thread.sleep(1000000);
	}
}
