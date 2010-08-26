package jadex.jade;

import jadex.base.AbstractComponentAdapter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.concurrent.IExecutable;

import java.io.Serializable;

/**
 *  Adapter for running Jadex components on top of JADE.
 */
public class JadeComponentAdapter	extends AbstractComponentAdapter	implements IComponentAdapter, IExecutable, Serializable
{
	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public JadeComponentAdapter(IComponentDescription desc, IModelInfo model, IComponentInstance component, IExternalAccess parent)
	{
		super(desc, model, component, parent);
	}
	
	//-------- AbstractComponentAdapter methods --------

	/**
	 *  Wake up this component.
	 */
	protected void	doWakeup()
	{
	}
}
