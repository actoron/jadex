package jadex.service;

import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.CounterResultListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 *  The nested service contained supports searching up and downwards
 *  parent and child containers.
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
	public IFuture getServicesTypes(IVisitDecider decider)
	{
		System.out.println("search services: "+this+" "+decider);
		
		final Future ret = new Future();
		final boolean[] results = new boolean[3];
		final Collection coll = Collections.synchronizedList(new LinkedList());
		
		super.getServicesTypes(decider).addResultListener(new IResultListener()
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
		
		// todo: parent and children
		
		return ret;
	}
	
	/**
	 *  Get a services for a type.
	 *  @param type The type.
	 */
	public IFuture getServices(final Class type, final IVisitDecider decider)
	{
		System.out.println("search services: "+this+" "+type+" "+decider);
		
		final Future ret = new Future();
		final boolean[] results = new boolean[3];
		final Collection coll = Collections.synchronizedList(new LinkedList());
		
		super.getServices(type, decider).addResultListener(new IResultListener()
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
				if(parent!=null && decider.searchNode(NestedServiceContainer.this, parent, true))
				{
					parent.getServices(type, decider).addResultListener(new DefaultResultListener()
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
		
//		System.out.println("search children (b): "+getName()+", "+type+", "+visited);
		getChildren().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final List children = (List)result;
//				System.out.println("found children (b): "+type+", "+getName()+" "+children);
				if(children!=null && !children.isEmpty())
				{
					final int[] cnt = new int[]{0};
					for(int i=0; i<children.size(); i++)
					{
						final IServiceProvider child = (IServiceProvider)children.get(i);
//						if(child.getName().indexOf("bdi")!=-1)
//							System.out.println("searching child: "+child);
						if(decider.searchNode(NestedServiceContainer.this, child, false))
						{
							child.getServices(type, decider).addResultListener(new IResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
//									System.out.println("child search completed: "+child+", "+result);
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
//			System.out.println("search result is: "+result);
			ret.setResult(result);
		}
	}
		
	/**
	 *  Get a service for a type.
	 *  @param type The type.
	 */
	public IFuture getService(final Class type, final IVisitDecider decider)
	{
//		System.out.println("search 1: "+this+" "+type+" "+visited);
		
		final Future ret = new Future();
		final boolean[] results = new boolean[3];
		
		super.getService(type, decider).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				results[0] = true;
				if(!checkAndSetResult(results, ret, result, null))
				{
					checkParent(type, decider, ret, results);
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				results[0] = true;
				if(!checkAndSetResult(results, ret, null, exception))
				{
					checkParent(type, decider, ret, results);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Check parent for locating a service.
	 */
	protected void checkParent(final Class type, final IVisitDecider decider, final Future ret, final boolean[] results)
	{
//		System.out.println("Check parent: "+type+", "+visited+", "+SUtil.arrayToString(results));
		getParent().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
//				System.out.println("Check parent1: "+result);
				IServiceProvider parent = (IServiceProvider)result;
				if(parent!=null && decider.searchNode(NestedServiceContainer.this, parent, true))
				{
					parent.getService(type, decider).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							results[1] = true;
							if(!checkAndSetResult(results, ret, result, null))
							{
								checkChildren(type, decider, ret, results);
							}
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							results[1] = true;
							if(!checkAndSetResult(results, ret, null, exception))
							{
								checkChildren(type, decider, ret, results);
							}
						}
					});
				}
				else
				{
					results[1] = true;
					if(!checkAndSetResult(results, ret, null, null))
					{
						checkChildren(type, decider, ret, results);
					}
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
//				System.out.println("Check parent2: "+exception);
				results[1] = true;
				if(!checkAndSetResult(results, ret, null, exception))
				{
					checkChildren(type, decider, ret, results);
				}
			}
		});
	}
	
	/**
	 *  Check the children for locating a service.
	 */
	protected void checkChildren(final Class type, final IVisitDecider decider, final Future ret, final boolean[] results)
	{
		getChildren().addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final List children = (List)result;
//				System.out.println("found children (a): "+children);
				if(children!=null)
				{
					CounterResultListener cl = new CounterResultListener(children.size())
					{
						public void finalResultAvailable(Object source, Object result)
						{
							results[2] = true;
							checkAndSetResult(results, ret, result, null);
						}
						
						public void intermediateResultAvailable(Object source, Object result)
						{
							checkAndSetResult(results, ret, result, null);
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							checkAndSetResult(results, ret, null, exception);
						}
					};
					
					for(int i=0; i<children.size(); i++)
					{
						IServiceProvider child = (IServiceProvider)children.get(i);
						if(decider.searchNode(NestedServiceContainer.this, child, false))
						{
							child.getService(type, decider).addResultListener(cl);
						}
						else
						{
							cl.resultAvailable(null, null);
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
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "NestedServiceContainer(name="+getName()+")";
	}
}
