package jadex.base;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.IAsyncFilter;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Filter for Jadex component models. 
 *  
 *  Is so complicated because it has to work locally and must be transferable.
 */
public class ModelFileFilter implements IAsyncFilter
{
	//-------- attributes --------
	
	/** The all selected flag. */
	protected boolean all;
	
//	/** The list of selected component names. */
//	protected List<String> selectedcomponents;
	
	/** The resource identifiers of the tree's root entries. */
	protected Map<URL, IResourceIdentifier>	rids;
	
	/** The external access. */
	protected IExternalAccess exta;
	
	//-------- constructors --------
	
	/**
	 *  Create a new filter.
	 */
	public ModelFileFilter()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new filter.
	 */
//	public ModelFileFilter(boolean all, List<String> selectedcomponents, Map<URL, IResourceIdentifier> rids, IExternalAccess exta)
	public ModelFileFilter(boolean all, Map<URL, IResourceIdentifier> rids, IExternalAccess exta)
	{
		this.all	= all;
//		this.selectedcomponents	=  selectedcomponents;
		this.rids	= rids;
		this.exta = exta;
	}

	//-------- methods --------

	/**
	 *  Set the all.
	 *  @param all The all to set.
	 */
	public void setAll(boolean all)
	{
		this.all = all;
	}
	
	/**
	 *  Get the all.
	 *  @return the all.
	 */
	public boolean isAll()
	{
		return all;
	}

//	/**
//	 *  Get the list of selected components.
//	 *  @return The list of components.
//	 */
//	public List<String> getSelectedComponents()
//	{
//		return selectedcomponents;
//	}
//
//	/**
//	 *  Set the list of selected components.
//	 *  @param selectedcomponents The list of selcted components.
//	 */
//	public void setSelectedComponents(List<String> selectedcomponents)
//	{
//		this.selectedcomponents = selectedcomponents;
//	}
	
	/**
	 *  Get the resource identifiers for the root path entries.
	 */
	public Map<URL, IResourceIdentifier>	getResourceIdentifiers()
	{
		return rids;
	}
	
	/**
	 *  Set the resource identifiers for the root path entries.
	 */
	public void	setResourceIdentifiers(Map<URL, IResourceIdentifier> rids)
	{
		this.rids	= rids;
	}
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		return exta;
	}

	/**
	 *  Set the external access.
	 *  @param exta The external acccess.
	 */
	public void setExternalAccess(IExternalAccess exta)
	{
		this.exta = exta;
	}

	/**
	 *  Test if object is accepted by filter.
	 *  @param obj The object to filter.
	 *  @return True, if ok.
	 */
	public IFuture<Boolean> filter(Object obj)
	{
		final Future<Boolean> ret =  new Future<Boolean>();
		
		if(obj instanceof File)
		{
			final File file = (File)obj;
			if(isAll() || file.isDirectory())
			{
				ret.setResult(Boolean.TRUE);
			}
			else
			{
				String	furl	= SUtil.toURL(file.getAbsolutePath()).toString();
				if(furl.startsWith("jar:"))
					furl	= furl.substring(4);
				IResourceIdentifier	rid	= null;
				for(Iterator<URL> it=rids.keySet().iterator(); rid==null && it.hasNext(); )
				{
					URL	url	= it.next();
					if(furl.startsWith(url.toString()))
					{
						rid	= rids.get(url);
					}
				}
				
				if(rid==null)
				{
					// Shouldn't happen!?
					System.out.println("no rid for url: "+furl+", "+rids);
				}
				
//				ret.setResult(Boolean.TRUE);
				
				final long start = System.currentTimeMillis();
//				SComponentFactory.isModelType(exta, file.getAbsolutePath(), getSelectedComponents(), rid)
				SComponentFactory.isModelType(exta, file.getAbsolutePath(), rid)
					.addResultListener(new DelegationResultListener<Boolean>(ret)
				{
					public void customResultAvailable(Boolean val)
					{
						long dur = System.currentTimeMillis()-start;
//						if(dur>1000)
//							System.out.println("Needed isModelType: "+dur);
						super.customResultAvailable(val);
					}
				});
			}
		}
		else
		{
			ret.setResult(Boolean.FALSE);
		}
		return ret;
	}
}