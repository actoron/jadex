package jadex.base.gui.filetree;

import jadex.bridge.IExternalAccess;
import jadex.commons.IRemoteFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.util.List;

/**
 * 
 */
public class DefaultFileFilter implements IRemoteFilter
{
	protected boolean all;
	protected List selectedcomponents;
	protected DefaultFileFilterMenuItemConstructor filtercon;
	protected IExternalAccess exta;
	
	public DefaultFileFilter()
	{
	}

	public DefaultFileFilter(DefaultFileFilterMenuItemConstructor filtercon)
	{
		this.filtercon = filtercon;
	}

	public void setAll(boolean all)
	{
		this.all = all;
	}
	
	public boolean isAll()
	{
		boolean ret;
		if(filtercon!=null)
			ret = filtercon.isAll();
		else
			ret = all;
		return ret;
	}
	
	public List getSelectedComponents()
	{
		List ret;
		if(filtercon!=null)
			ret = filtercon.getSelectedComponentTypes();
		else
			ret = selectedcomponents;
		return ret;
	}

	public void setSelectedComponents(List selectedcomponents)
	{
		this.selectedcomponents = selectedcomponents;
	}
	
	public IFuture filter(Object obj)
	{
		Future ret =  new Future();
		
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