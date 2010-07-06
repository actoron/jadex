package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * 
 */
public abstract class NestedServiceContainer extends BasicServiceContainer
{
	//-------- attributes --------
	
	/** The upwards search flag. */
	public boolean search_up;
	
	/** The downwards search flag. */
	public boolean search_down;
	
	//-------- constructors --------
	
	/**
	 *  Create a new container.
	 */
	public NestedServiceContainer(String name)
	{
		this(name, true, true);
	}
	
	/**
	 *  Create a new container.
	 */
	public NestedServiceContainer(String name, boolean search_up, boolean search_down)
	{
		super(name);
		this.search_up = search_up;
		this.search_down = search_down;
	}
	
	//-------- abstract methods --------
	
	/**
	 *  Get the parent service container.
	 *  @return The parent container.
	 */
	public abstract IFuture getParent();
	
	/**
	 *  Get the children container.
	 *  @return The children container.
	 */
	public abstract IFuture getChildren();
	
	//-------- methods --------
	
	/**
	 *  Get the available service types.
	 *  @return The service types.
	 */
	public IFuture getServicesTypes()
	{
		// todo:
		throw new UnsupportedOperationException();
		
//		Future ret = new Future();
//		ret.setResult(services==null? new Class[0]: (Class[])services.keySet().toArray(new Class[services.size()]));
//		return ret;
	}
	
	/**
	 *  Get a service map for a type.
	 *  @param type The type.
	 */
	public IFuture getServiceOfType(final Class type, final Set visited)
	{
//		System.out.println("search 1: "+this+" "+type+" "+visited);
		
		final Future ret = new Future();
		final boolean[] results = new boolean[3];
		
		super.getServiceOfType(type, visited).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				results[0] = true;
				if(!checkAndSetResult(results, ret, result, null))
				{
					checkParent(type, visited, ret, results);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				results[0] = true;
				if(!checkAndSetResult(results, ret, null, exception))
				{
					checkParent(type, visited, ret, results);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void checkParent(final Class type, final Set visited, final Future ret, final boolean[] results)
	{
//		System.out.println("Check parent: "+type+", "+visited+", "+SUtil.arrayToString(results));
		getParent().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
//				System.out.println("Check parent1: "+result);
				IServiceProvider parent = (IServiceProvider)result;
				if(parent!=null && searchNode(NestedServiceContainer.this, parent, true, visited))
				{
					parent.getServiceOfType(type, visited).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							results[1] = true;
							if(!checkAndSetResult(results, ret, result, null))
							{
								checkChildren(type, visited, ret, results);
							}
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							results[1] = true;
							if(!checkAndSetResult(results, ret, null, exception))
							{
								checkChildren(type, visited, ret, results);
							}
						}
					});
				}
				else
				{
					results[1] = true;
					if(!checkAndSetResult(results, ret, null, null))
					{
						checkChildren(type, visited, ret, results);
					}
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
//				System.out.println("Check parent2: "+exception);
				results[1] = true;
				if(!checkAndSetResult(results, ret, null, exception))
				{
					checkChildren(type, visited, ret, results);
				}
			}
		});
	}
	
	/**
	 * 
	 */
	protected void checkChildren(final Class type, final Set visited, final Future ret, final boolean[] results)
	{
		getChildren().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final List children = (List)result;
				System.out.println("found children (a): "+children);
				if(children!=null)
				{
					final int[] cnt = new int[]{0};
					for(int i=0; i<children.size(); i++)
					{
						IServiceProvider child = (IServiceProvider)children.get(i);
						if(searchNode(NestedServiceContainer.this, child, false, visited))
						{
							child.getServiceOfType(type, visited).addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									cnt[0]=cnt[0]+1;
									if(cnt[0]==children.size())
									{
										results[2] = true;
									}
									checkAndSetResult(results, ret, result, null);
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									checkAndSetResult(results, ret, null, exception);
								}
							});
						}
						else
						{
							cnt[0]=cnt[0]+1;
							if(cnt[0]==children.size())
							{
								results[2] = true;
								checkAndSetResult(results, ret, null, null);
							}
						}
					}
				}
				else
				{
					results[2] = true;
					checkAndSetResult(results, ret, null, null);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				results[2] = true;
				checkAndSetResult(results, ret, null, exception);
			}
		});	
	}
	
	/**
	 *  Check if the last result is available and then set it on future.
	 */
	protected boolean checkAndSetResult(boolean[] results, Future ret, Object result, Exception exception)
	{
		boolean finished = false;
		
//		System.out.println("res: "+results[0]+results[1]+results[2]);
		if(result!=null)
		{
			ret.setResult(result);
			finished = true;
		}
		else if(exception!=null)
		{
			ret.setException(exception);
			finished = true;
		}
		else if(results[0] && results[1] && results[2])
		{
			ret.setResult(null);
			finished = true;
		}
		
		return finished;
	}
	
	/**
	 *  Get a service map for a type.
	 *  @param type The type.
	 */
	public IFuture getServicesOfType(final Class type, final Set visited)
	{
		System.out.println("search services: "+this+" "+type+" "+visited);
		
		final Future ret = new Future();
		final boolean[] results = new boolean[3];
		final Collection coll = Collections.synchronizedList(new LinkedList());
		
		super.getServicesOfType(type, visited).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(result!=null)
				{
					coll.addAll((Collection)result);
				}
				results[0] = true;
				checkAndSetResults(results, ret, coll, null);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				results[0] = true;
				checkAndSetResults(results, ret, null, exception);
			}
		});
		
		getParent().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IServiceProvider parent = (IServiceProvider)result;
				if(parent!=null && searchNode(NestedServiceContainer.this, parent, true, visited))
				{
					parent.getServicesOfType(type, visited).addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							Collection tmp = (Collection)result;
							if(tmp!=null)
							{
								coll.addAll(tmp);
							}
							
							results[1] = true;
							checkAndSetResults(results, ret, coll, null);
						}
					});
				}
				else
				{
					results[1] = true;
					checkAndSetResults(results, ret, coll, null);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				results[1] = true;
				checkAndSetResults(results, ret, null, exception);
			}
		});
		
		System.out.println("search children (b): "+getName()+", "+type+", "+visited);
		getChildren().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final List children = (List)result;
				System.out.println("found children (b): "+type+", "+getName()+" "+children);
				if(children!=null && !children.isEmpty())
				{
					final int[] cnt = new int[]{0};
					for(int i=0; i<children.size(); i++)
					{
						final IServiceProvider child = (IServiceProvider)children.get(i);
						if(child.getName().indexOf("bdi")!=-1)
							System.out.println("searching child: "+child);
						if(searchNode(NestedServiceContainer.this, child, false, visited))
						{
							child.getServicesOfType(type, visited).addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									System.out.println("child search completed: "+child+", "+result);
									if(result!=null)
										coll.addAll((Collection)result);
									
									cnt[0]=cnt[0]+1;
									if(cnt[0]==children.size())
									{
										results[2] = true;
										checkAndSetResults(results, ret, coll, null);
									}
								}
								
								public void exceptionOccurred(Object source, Exception exception)
								{
									results[2] = true;
									checkAndSetResults(results, ret, null, exception);
								}
							});
						}
						else
						{
							cnt[0]=cnt[0]+1;
							if(cnt[0]==children.size())
							{
								results[2] = true;
								checkAndSetResults(results, ret, coll, null);
							}
						}
					}
				}
				else
				{
					results[2] = true;
					checkAndSetResults(results, ret, coll, null);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				results[2] = true;
				checkAndSetResults(results, ret, null, exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Check if the last result is available and then set it on future.
	 */
	protected void checkAndSetResults(boolean[] results, Future ret, Object result, Exception exception)
	{
//		System.out.println("res: "+results[0]+results[1]+results[2]);
		if(exception!=null)
		{
			ret.setException(exception);
		}
		else if(results[0] && results[1] && results[2])
		{
			System.out.println("search result is: "+result);
			ret.setResult(result);
		}
	}

	/**
	 *  Test if a specific node should be searched.
	 *  Simple default logic that allows search in unknown nodes in allows directios.
	 */
	public synchronized boolean searchNode(IServiceProvider source, IServiceProvider target, boolean up, Set visited)
	{
		boolean ret = false;
		if(!visited.contains(target.getName()))
		{
			if(up && search_up || !up && search_down)
			{
				ret = true;
			}
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "NestedServiceContainer(name="+getName()+")";
	}
}
