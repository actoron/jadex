package jadex.platform.service.registry;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Search for a peer with a dedicated functionality.
 */
public abstract class PeerSearchFunctionality
{
	/** The peer. */
	protected IComponentIdentifier peer;
	
	/** The lastsearch. */
	protected long lastsearch;
	
	/** The search delay. */
	protected long searchdelay;
	
	/** The current search. */
	protected IFuture<IComponentIdentifier> currentfut;
	
	/**
	 *  Create a new search functionality.
	 */
	public PeerSearchFunctionality()
	{
		this(0);
	}
	
	/**
	 *  Create a new search functionality.
	 */
	public PeerSearchFunctionality(long searchdelay)
	{
		this.searchdelay = searchdelay>0? searchdelay: 10000;
	}
	
	/**
	 *  Find a peer from a given list of peers.
	 */
	protected IFuture<IComponentIdentifier> getPeer()
	{
		return getPeer(false);
	}
	
	/**
	 *  Find a peer from a given list of peers.
	 */
	protected IFuture<IComponentIdentifier> getPeer(boolean force)
	{
		Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		if(force)
			resetPeer();
		
		long ct = System.currentTimeMillis();
		
		if(peer!=null)
		{
			ret.setResult(peer);
		}
		else if(currentfut==null && (lastsearch==0 || lastsearch+searchdelay<ct))
		{
			lastsearch = ct;
			currentfut = findPeer(true);
			
			currentfut.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
			{
				@Override
				public void customResultAvailable(IComponentIdentifier result) 
				{
//					System.out.println("found peer: "+result);
					currentfut = null;
					peer = result;
					super.customResultAvailable(result);
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					currentfut = null;
					super.exceptionOccurred(exception);
				}
			});
		}
		else if(currentfut!=null)
		{
			currentfut.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
		}
		else
		{
			ret.setException(new RuntimeException("Waiting for next search"));
		}
		
		return ret;
	}
	
	/**
	 *  Reset the current peer.
	 */
	public void resetPeer()
	{
		peer = null;
		lastsearch = 0;
	}
	
//	/**
//	 *  Get the superpeer. Triggers search in background if none available.
//	 *  @return The superpeer.
//	 */
//	public IComponentIdentifier getPeerSync()
//	{
//		long ct = System.currentTimeMillis();
//		if(peer==null && (lastsearch==0 || lastsearch+searchdelay<ct))
//		{
//			// Ensure that a delay is waited between searches
//			lastsearch = ct;
//			
//			getPeer(true);
//			
////			searchSuperpeer().addResultListener(new IResultListener<IComponentIdentifier>()
////			{
////				public void resultAvailable(IComponentIdentifier result)
////				{
//////					System.out.println("Found superpeer: "+result);
//////					superpeer = result;
////					getRegistry().setSuperpeer(result);
//////					addQueriesToNewSuperpeer();
////					// initiating 
////				}
////				
////				public void exceptionOccurred(Exception exception)
////				{
//////					System.out.println("No superpeer found");
////				}
////			});
//		}
//		else
//		{
//			System.out.println("No superpeer search: "+searchtime+" "+ct);
//		}
//			
//		return peer;
//	}
	
	
//	/**
//	 *  Find a peer from a given list of peers.
//	 */
//	protected IFuture<IComponentIdentifier> findPeer(final Iterator<IComponentIdentifier> ssps)
//	{
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		
//		if(ssps!=null && ssps.hasNext())
//		{
//			final IComponentIdentifier peer = ssps.next();
//			isOk(peer).addResultListener(new ExceptionDelegationResultListener<Boolean, IComponentIdentifier>(ret)
//			{
//				public void customResultAvailable(Boolean result) throws Exception
//				{
//					if(result.booleanValue())
//						ret.setResult(peer);
//					else
//						findPeer(ssps).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//				}
//			});
//		}
//		else
//		{
//			ret.setException(new RuntimeException("No peers available"));
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Find a peer from a given list of peers.
	 */
	protected IFuture<IComponentIdentifier> findPeer(final boolean reset)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		getNextPotentialPeer(reset).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
		{
			@Override
			public void customResultAvailable(final IComponentIdentifier peer)
			{
//				System.out.println("Next potential peer is: "+peer);
				
				isOk(peer).addResultListener(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						if(result.booleanValue())
						{
//							System.out.println("Valid next peer is: "+peer);
							ret.setResult(peer);
						}
						else
						{
							findPeer(false).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						findPeer(false).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
					}
				});
			}
		});
		
		return ret;
	}
	
//	/**
//	 *  Get the collection of potential peers.
//	 *  @return The collection of potential peers.
//	 */
//	public abstract Collection<IComponentIdentifier> getPotentialPeers();
	
	/**
	 *  Get the next potential peer.
	 *  @return The next potential peer.
	 */
	public abstract IFuture<IComponentIdentifier> getNextPotentialPeer(boolean reset);
	
	/**
	 *  Check if a peer is ok.
	 *  @return True if peer is ok. 
	 */
	public IFuture<Boolean> isOk(IComponentIdentifier peer)
	{
		return Future.TRUE;
	}

	/**
	 *  Get the last search time.
	 *  @return The lastsearch time.
	 */
	public long getLastSearch()
	{
		return lastsearch;
	}

	/**
	 *  Set the last search time.
	 *  @param lastsearch The lastsearch to set
	 */
	public void setLastSearch(long lastsearch)
	{
		this.lastsearch = lastsearch;
	}

	/**
	 *  Get the search delay.
	 *  @return the searchdelay
	 */
	public long getSearchDelay()
	{
		return searchdelay;
	}

	/**
	 *  Set the search delay.
	 *  @param searchdelay The searchdelay to set.
	 */
	public void setSearchDelay(long searchdelay)
	{
		this.searchdelay = searchdelay;
	}
}
