package jadex.base;

import java.io.File;
import java.util.List;

import jadex.commons.IAsyncFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  The default file filter allows using different file extensions.
 *  This class needs to handle remote transfer.
 */
public class DefaultFileFilter implements IAsyncFilter
{
	//-------- attributes --------
	
	/** Boolean if all is selected. */
	protected boolean all;
	
	/** The selected component list. */
	protected List<String> selected;
	
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
	public DefaultFileFilter(boolean all, List<String> selected)
	{
		this.all	= all;
		this.selected	= selected;
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
		return all;
	}
	
	/**
	 *  Get the selected filter elements.
	 *  @return The list of elements.
	 */
	public List<String> getSelectedComponents()
	{
		return selected;
	}

	/**
	 *  Set the selected elements.
	 *  @param selectedcomponents The selected components.
	 */
	public void setSelectedComponents(List<String> selected)
	{
		this.selected = selected;
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