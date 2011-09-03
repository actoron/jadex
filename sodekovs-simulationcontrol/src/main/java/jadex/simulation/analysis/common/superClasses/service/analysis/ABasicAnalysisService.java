package jadex.simulation.analysis.common.superClasses.service.analysis;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAListener;

import java.awt.GridBagLayout;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ABasicAnalysisService extends BasicService implements IAnalysisService
{
	protected Object mutex = new Object();
	protected JComponent view;
	protected Set<IAListener> listeners = new HashSet<IAListener>();
	protected IExternalAccess access;
	protected ISuspendable susThread = new ThreadSuspendable(this);
	
	public ABasicAnalysisService(IExternalAccess access, Class serviceInterface)
	{
		super(access.getServiceProvider().getId(), serviceInterface, null);
		synchronized (mutex)
		{
			this.access = access;
			view = new JPanel(new GridBagLayout());
			Map prop = getPropertyMap();
			if (prop.equals(Collections.EMPTY_MAP)) prop = new HashMap();
			prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.common.superclasses.service.view.DefaultServiceViewerPanel");
			setPropertyMap(prop);
		}
	}
	
	// ------ IAnalysisService ------- (IAServiceObserver)

	@Override
	public IFuture getWorkload()
	{
		return new Future(0.0);
	}
	
	@Override
	public Object getMutex()
	{
		return mutex;
	}
	
	// ------ IAObserver ------

	@Override
	public void addListener(IAListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}	
	}

	@Override
	public void removeListener(IAListener listener)
	{
		synchronized (mutex)
		{
			listeners.remove(listener);
		}	
	}

	@Override
	public void notify(IAEvent event)
	{
		synchronized (mutex)
		{
			for (IAListener listener : listeners)
			{
				listener.update(event);
			}
		}	
	}

	@Override
	public IFuture getView()
	{
		return new Future(view);
	}

}
