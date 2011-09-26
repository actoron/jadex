package jadex.base.gui.filetree;

import jadex.bridge.IExternalAccess;
import jadex.commons.IRemoteFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.util.List;

/**
 *  The default file filter allows using different file extensions.
 *  This class needs to handle local gui access as well as 
 *  remote transfer. Hence all attributes can be copied when used remotely.
 */
public class DefaultFileFilter implements IRemoteFilter
{
	//-------- attributes --------
	
	/** Boolean if all is selected. */
	protected boolean all;
	
	/** The selected component list. */
	protected List selectedcomponents;
	
	/** The menu item constructor. */
	protected DefaultFileFilterMenuItemConstructor filtercon;
	
	/** The external access. */
	protected IExternalAccess exta;
	
	//-------- constructors --------

	/**
	 *  Create a new file filter.
	 */
	public DefaultFileFilter()
	{
	}

	/**
	 *  Create a new file filter.
	 */
	public DefaultFileFilter(DefaultFileFilterMenuItemConstructor filtercon)
	{
		this.filtercon = filtercon;
	}

	//-------- methods --------

	/**
	 *  Set the all flag.
	 *  @param all The all flag.
	 */
	public void setAll(boolean all)
	{
		this.all = all;
	}
	
	/**
	 *  Test the all flag .
	 *  @return True, if all is set.
	 */
	public boolean isAll()
	{
		boolean ret;
		if(filtercon!=null)
			ret = filtercon.isAll();
		else
			ret = all;
		return ret;
	}
	
	/**
	 *  Get the selected filter elements.
	 *  @return The list of elements.
	 */
	public List getSelectedComponents()
	{
		List ret;
		if(filtercon!=null)
			ret = filtercon.getSelectedComponentTypes();
		else
			ret = selectedcomponents;
		return ret;
	}

	/**
	 *  Set the selected elements.
	 *  @param selectedcomponents The selected components.
	 */
	public void setSelectedComponents(List selectedcomponents)
	{
		this.selectedcomponents = selectedcomponents;
	}
	
	/**
	 *  Filter an object.
	 *  @param obj The object to filter.
	 *  @return True, if passes filter.
	 */
	public IFuture<Boolean> filter(Object obj)
	{
		Future<Boolean> ret =  new Future<Boolean>();
		
		if(obj instanceof File)
		{
			File file = (File)obj;
			if(isAll() || file.isDirectory())
			{
				ret.setResult(Boolean.TRUE);
			}
			else
			{
				String ext = null;
				int pos = file.getName().lastIndexOf(".");
				if(pos!=-1)
				{
					ext = file.getName().substring(pos);
				}
//				System.out.println("filter: "+file+", "+getSelectedComponents().contains(ext));
				ret.setResult(getSelectedComponents().contains(ext));
			}
		}
		else
		{
			ret.setResult(Boolean.FALSE);
		}
		return ret;
	}
}