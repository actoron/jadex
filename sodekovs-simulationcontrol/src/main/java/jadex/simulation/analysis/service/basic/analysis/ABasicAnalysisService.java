package jadex.simulation.analysis.service.basic.analysis;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.events.service.IAServiceListener;
import jadex.simulation.analysis.common.util.AConstants;

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
	protected Set<IAServiceListener> listeners = new HashSet<IAServiceListener>();
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
			prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.service.basic.view.DefaultServiceViewerPanel");
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
	
	// ------ IAServiceObserver ------

	@Override
	public void addServiceListener(IAServiceListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}	
	}

	@Override
	public void removeServiceListener(IAServiceListener listener)
	{
		synchronized (mutex)
		{
			listeners.remove(listener);
		}	
	}

	@Override
	public void serviceChanged(AServiceEvent e)
	{
		synchronized (mutex)
		{
			for (IAServiceListener listener : listeners)
			{
				listener.serviceEventOccur(e);
			}
		}	
	}

	@Override
	public IFuture getView()
	{
		return new Future(view);
	}

}
