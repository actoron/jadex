package jadex.base.gui.modeltree;

import jadex.base.SComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.commons.IRemoteFilter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.util.List;

/**
 * 
 */
public class ModelFileFilter implements IRemoteFilter
{
	protected boolean all;
	protected List selectedcomponents;
	protected ModelFileFilterMenuItemConstructor filtercon;
	protected IExternalAccess exta;
	
	public ModelFileFilter()
	{
	}

	public ModelFileFilter(ModelFileFilterMenuItemConstructor filtercon, IExternalAccess exta)
	{
		this.filtercon = filtercon;
		this.exta = exta;
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
	
	public IExternalAccess getExternalAccess()
	{
		return exta;
	}

	public void setExternalAccess(IExternalAccess exta)
	{
		this.exta = exta;
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
				SComponentFactory.isModelType(exta, file.getAbsolutePath(), getSelectedComponents())
					.addResultListener(new DelegationResultListener(ret));
			}
		}
		else
		{
			ret.setResult(Boolean.FALSE);
		}
		return ret;
	}
}