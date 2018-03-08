package jadex.commons.gui;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

public class SDirScan
{
	/**
	 *  Scan for classes that fulfill certain criteria as specified by the file and classfilters.
	 */
	public static ISubscriptionIntermediateFuture<Class<?>> asyncScanForClasses(ClassLoader classloader, 
		IFilter<Object> filefilter, IFilter<Class<?>> classfilter, int max, boolean includebootpath)
	{
		return asyncScanForClasses(SUtil.getClasspathURLs(classloader, includebootpath).toArray(new URL[0]), classloader, filefilter, classfilter, max);
	}
	
	/**
	 *  Scan for classes that fulfill certain criteria as specified by the file and classfilters.
	 */
	public static ISubscriptionIntermediateFuture<Class<?>> asyncScanForClasses(final URL[] urls, 
		final ClassLoader classloader, final IFilter<Object> filefilter, final IFilter<Class<?>> classfilter, 
		final int max)
	{
		final SubscriptionIntermediateFuture<Class<?>>	ret	= new SubscriptionIntermediateFuture<Class<?>>();
		
		final ClassLoader[] newcl = new ClassLoader[1];
		final int[] cnt = new int[1];
		
		final ISubscriptionIntermediateFuture<String> fut = asyncScanForFiles(urls, filefilter);
		
		fut.addResultListener(new IIntermediateResultListener<String>()
		{
			int resultcnt = 0;
			
			public void intermediateResultAvailable(String file)
			{
				try
				{
					String	clname	= file.substring(0, file.length()-6).replace('/', '.');
//					System.out.println("Found candidate: "+clname+" "+cnt[0]);
					
					if(newcl[0]==null || cnt[0]++%500==0)
						newcl[0] = new URLClassLoader(urls, null);
//					
					Class<?> tmpcl = Class.forName(clname, false, newcl[0]);
					
					if(tmpcl!=null && classfilter.filter(tmpcl))
					{
						if((max>0 && ++resultcnt>max) || !ret.addIntermediateResultIfUndone(tmpcl))
						{
//							System.out.println("term: "+resultcnt);
							fut.terminate();
						}
						
//						ret.addIntermediateResult(SReflect.findClass0(clname, null, classloader));
					}
				}
				catch(Throwable t)
				{
//					t.printStackTrace();
//					System.out.println(file);
				}
			}
			
			public void resultAvailable(Collection<String> result)
			{
				for(String res: result)
				{
					intermediateResultAvailable(res);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
			
			public void finished()
			{
//				System.out.println("finiiii");
				ret.setFinishedIfUndone();
			}
		});
		
		return ret;
	}

	/**
	 *  Scan for files in a given list of urls.
	 */
	public static ISubscriptionIntermediateFuture<String> asyncScanForFiles(URL[] urls, IFilter<Object> filter)
	{
		final SubscriptionIntermediateFuture<String>	ret	= new SubscriptionIntermediateFuture<String>();
		
		if(urls.length==0)
		{
			ret.setFinished();
			return ret;
		}
		
		final CounterResultListener<Void> lis = new CounterResultListener<Void>(urls.length, true, new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				ret.setFinishedIfUndone();
			}
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}
		});
		
		for(int i=0; i<urls.length && !ret.isDone(); i++)
		{
//			System.out.println("Scanning: "+urls[i]+" "+ret.isDone());
			
//			if(urls[i].toString().indexOf("sftp")!=-1)
//				System.out.println("ggggg");
			
			try
			{
//				System.out.println("url: "+urls[i].toURI());
				final URL url = urls[i];
				final int fi = i;
				File f = new File(urls[i].toURI());
				if(f.getName().endsWith(".jar"))
				{
					JarFile	jar = null;
					try
					{
						jar	= new JarFile(f);
						for(Enumeration<JarEntry> e=jar.entries(); e.hasMoreElements() && !ret.isDone(); )
						{
							JarEntry	je	= e.nextElement();
							if(filter.filter(je))	
							{
//								System.out.println("add: "+urls[i]+" "+je.getName());
								ret.addIntermediateResultIfUndone(je.getName());
							}
						}
					}
					catch(Exception e)
					{
						lis.exceptionOccurred(e);
//						System.out.println("Error opening jar: "+urls[i]+" "+e.getMessage());
					}
					finally
					{
						if(jar!=null)
						{
							jar.close();
						}
					}
					lis.resultAvailable(null);
				}
				else if(f.isDirectory())
				{
					final ISubscriptionIntermediateFuture<String> fut = asyncScanDir(urls, f, filter, new ArrayList<String>());
					fut.addResultListener(new IIntermediateResultListener<String>()
					{
						public void intermediateResultAvailable(String result)
						{
							if(!ret.addIntermediateResultIfUndone(result))
							{
								fut.terminate();
							}
						}
						
						public void finished()
						{
							lis.resultAvailable(null);
						}
						
						public void resultAvailable(Collection<String> result)
						{
							for(String res: result)
							{
								intermediateResultAvailable(res);
							}
							finished();
						}
						
						public void exceptionOccurred(Exception exception)
						{
							lis.exceptionOccurred(exception);
						}
					});
//					throw new UnsupportedOperationException("Currently only jar files supported: "+f);
				}
				else
				{
					lis.resultAvailable(null);
				}
			}
			catch(Exception e)
			{
				lis.exceptionOccurred(e);
				System.out.println("scan problem with: "+urls[i]);
//				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Scan directories.
	 */
	public static ISubscriptionIntermediateFuture<String> asyncScanDir(URL[] urls, File file, IFilter filter, List<String> donedirs)
	{
		final SubscriptionIntermediateFuture<String>	ret	= new SubscriptionIntermediateFuture<String>();
		
		File[] files = file.listFiles(new FileFilter()
		{
			public boolean accept(File f)
			{
				return !f.isDirectory();
			}
		});
		for(File fi: files)
		{
			if(fi.getName().endsWith(".class") && filter.filter(fi))
			{
				String fn = SUtil.convertPathToPackage(fi.getAbsolutePath(), urls);
//				System.out.println("fn: "+fi.getName());
				if(!ret.addIntermediateResultIfUndone(fn+"."+fi.getName()))
				{
					break;
				}
			}
		}
		
		if(file.isDirectory() && !ret.isDone())
		{
			donedirs.add(file.getAbsolutePath());
			File[] sudirs = file.listFiles(new FileFilter()
			{
				public boolean accept(File f)
				{
					return f.isDirectory();
				}
			});
			
			if(sudirs.length>0)
			{
				final CounterResultListener<Void> lis = new CounterResultListener<Void>(sudirs.length, new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setFinishedIfUndone();
					}
					public void exceptionOccurred(Exception exception)
					{
						ret.setExceptionIfUndone(exception);
					}
				});
				
				for(File dir: sudirs)
				{
					if(!donedirs.contains(dir.getAbsolutePath()))
					{
						final ISubscriptionIntermediateFuture<String> fut = asyncScanDir(urls, dir, filter, donedirs);
						fut.addResultListener(new IIntermediateResultListener<String>()
						{
							public void intermediateResultAvailable(String result)
							{
								if(!ret.addIntermediateResultIfUndone(result))
								{
									fut.terminate();
								}
							}
							
							public void finished()
							{
								lis.resultAvailable(null);
							}
							
							public void resultAvailable(Collection<String> result)
							{
								for(String res: result)
								{
									intermediateResultAvailable(res);
								}
								finished();
							}
							
							public void exceptionOccurred(Exception exception)
							{
								lis.exceptionOccurred(exception);
							}
						});
					}
				}
			}
			else
			{
				ret.setFinishedIfUndone();
			}
		}
		else
		{
			ret.setFinishedIfUndone();
		}
		
		return ret;
	}
}
