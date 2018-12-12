package jadex.commons.gui.autocombo;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.gui.SDirScan;
import jadex.commons.gui.future.SwingIntermediateResultListener;

/**
 * 
 */
public class ClassComboModel extends AbstractAutoComboModel<Class<?>>
{
	protected boolean inter;
	protected boolean absclasses;
	protected boolean inclasses;
	protected boolean classes;
	
	protected IFilter<Class<?>> classfilter;
	protected IFilter<String> filefilter;
	
	
	/**
	 *  Create a new ClassComboModel. 
	 */
	public ClassComboModel(AutoCompleteCombo combo, int max)
	{
		this(combo, max, true, false, false, true, null, null);
	}
	
	/**
	 *  Create a new ClassComboModel. 
	 */
	public ClassComboModel(AutoCompleteCombo combo, int max, 
		boolean inter, boolean absclasses, boolean inclasses, boolean classes, 
		IFilter<String> filefilter, IFilter<Class<?>> classfilter)
	{
		super(combo, max);
		this.inter = inter;
		this.absclasses = absclasses;
		this.inclasses = inclasses;
		this.classes = classes;
		this.filefilter = filefilter;
		this.classfilter = classfilter;
	}
	
	/**
	 * 
	 */
	public Class<?> convertFromString(String val)
	{
		return SReflect.findClass0(val, null, getCombo().getClassLoader());
	}
	
	/**
	 * 
	 */
	public String convertToString(Class<?> val)
	{
		return val==null? "": SReflect.getInnerClassName(val);
	}
	
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<Class<?>> doSetPattern(final String pattern)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		ISubscriptionIntermediateFuture<Class<?>> ret;
		
		if(pattern==null || pattern.isEmpty())
		{
			if(entries.size()>0)
				getCombo().setSelectedItem(getElementAt(0));
			
			ret = new SubscriptionIntermediateFuture<Class<?>>();
			((SubscriptionIntermediateFuture<Class<?>>)ret).setResult(null);
		}
		else
		{				
			entries.clear();

			ret = performSearch(pattern, inter, absclasses, classes, inclasses, max);
			final ISubscriptionIntermediateFuture<Class<?>> fret = ret;
			
			ret.addResultListener(new IIntermediateResultListener<Class<?>>()
			{
				public void intermediateResultAvailable(Class<?> result)
				{
					if(getCombo().getCurrentSearch()==fret)
					{
						entries.add(result);
					}
				}
				
				public void finished()
				{
//					getCombo().setSelectedItem(pattern);
					getCombo().updatePopup();
				}
				
				public void resultAvailable(Collection<Class<?>> result)
				{
					if(getCombo().getCurrentSearch()==fret)
					{
						for(Class<?> clazz: result)
						{
							intermediateResultAvailable(clazz);
						}
						finished();
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Perform a search using a search expression.
	 */
	public ISubscriptionIntermediateFuture<Class<?>> performSearch(final String exp, 
		final boolean inter, final boolean abscla, final boolean cla, final boolean incla, final int max)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		final ISubscriptionIntermediateFuture<Class<?>>[] fut = new ISubscriptionIntermediateFuture[1];
		
		final SubscriptionIntermediateFuture<Class<?>> ret = new SubscriptionIntermediateFuture<Class<?>>(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
//				System.out.println("terminating: "+exp);
				if(fut[0]!=null)
					fut[0].terminate();
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		
		if(exp==null || exp.length()==0)
		{
//			status.setText("idle");
			ret.setResultIfUndone(null);
			return ret;
		}
		
		final Pattern pat = SUtil.createRegexFromGlob(exp+"*");
		
//		setCurrentQuery(exp);
//		System.out.println("perform search: "+exp);
		
//		ctm.clear();
		
//		if(exp!=null && exp.length()>0)
//		status.setText("searching '"+exp+"'");
						
		getCombo().getThreadPool().execute(new Runnable()
		{
			public void run()
			{
				IFilter<Object> filefilter = new IFilter<Object>()
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
						
						if(!incla && fn.indexOf("$")!=-1)
						{
							return false;
						}
						
						if(ClassComboModel.this.filefilter!=null)
						{
							if(!ClassComboModel.this.filefilter.filter(fn))
								return false;
						}
						
						StringTokenizer stok = new StringTokenizer(exp, "*?");
						boolean ret = true;
						while(ret && stok.hasMoreElements())
						{
							String tst = stok.nextToken();
							ret = fn.indexOf(tst)!=-1;
						}
						return ret;
					}
				};
				IFilter<Class<?>> classfilter = new IFilter<Class<?>>()
				{
					public boolean filter(Class<?> clazz)
					{
//						System.out.println("found: "+clazz);
						boolean in = clazz.isInterface();
						boolean abs = Modifier.isAbstract(clazz.getModifiers()); 
						boolean ret = (in && inter)
							|| (!in && abs && abscla)
							|| (!in && !abs && cla);
						
						if(ret)
						{
							if(ClassComboModel.this.classfilter!=null)
							{
								ret = ClassComboModel.this.classfilter.filter(clazz);
							}
							
							if(ret)
							{
								String clname = SReflect.getInnerClassName(clazz);
								
								if(exp.indexOf("*")==-1 && exp.indexOf("?")==-1)
								{
									ret = clname.startsWith(exp);
								}
								else
								{
									Matcher m = pat.matcher(clname);
									ret = m.matches(); 
								}
							}
						}
						
						return ret;
					}
				};
				
				fut[0] = SDirScan.asyncScanForClasses(getCombo().getClassLoader(), filefilter, classfilter, max, false);
				
				fut[0].addResultListener(new SwingIntermediateResultListener<Class<?>>(new IIntermediateResultListener<Class<?>>()
				{
//					List<Class<?>> res = new ArrayList<Class<?>>();
					
					public void intermediateResultAvailable(Class<?> result)
					{
//						if(!ret.isDone())
//							res.add(result);
//						ctm.addEntry(result);
//						System.out.println("found: "+result);
						ret.addIntermediateResultIfUndone(result);
					}
					
					public void finished()
					{
//						if(!ret.isDone())
//						{
//							ctm.clear();
//							for(Class<?> cl: res)
//								ctm.addEntry(cl);
//							if(ctm.size()>0)
//							{
//				                results.changeSelection(0, 0, false, false);
//								results.requestFocus();
//							}
//							
//							status.setText("searching '"+exp+"' ("+ctm.size()+") finished");
//							
							ret.setFinishedIfUndone();
//						}
					}
					
					public void resultAvailable(Collection<Class<?>> result)
					{
						for(Class<?>clazz: result)
						{
							intermediateResultAvailable(clazz);
						}
						finished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						status.setText("idle '"+exp+"'");
						
//						lastsearch.setExceptionIfUndone(exception);
						ret.setExceptionIfUndone(exception);
					}
				}));
			}
		});
		
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	public boolean contains(Class<?> val)
//	{
//		if(val == null)// || val.trim().isEmpty())
//			return true;
//		
////		val = val.toLowerCase();
//		for(Class<?> item : entries)
//		{
//			if(item.equals(val))
//			{
//				return true;
//			}
//		}
//		return false;
//	}
}
