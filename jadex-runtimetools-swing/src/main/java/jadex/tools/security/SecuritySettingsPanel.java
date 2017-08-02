package jadex.tools.security;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

public class SecuritySettingsPanel implements IServiceViewerPanel
{
	protected static final String DEFAULT_CERT_STORE = "certstore.zip";
	
	/** Access to jcc component. */
	protected IExternalAccess jccaccess;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	protected JTabbedPane main;
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture<Void> init(IControlCenter jcc, IService service)
	{
		Future<Void> ret = new Future<Void>();
		this.secservice	= (ISecurityService)service;
		jccaccess = jcc.getJCCAccess();
		
		main = new JTabbedPane();
		
		main.add("Certificates Store", new CertTree(DEFAULT_CERT_STORE));
		
		ret.setResult(null);
		
		return ret;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "SecuritySettingsPanel";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return main;
	}

	/**
	 * Override
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		return IFuture.DONE;
	}

	/**
	 * Override
	 */
	public IFuture<Properties> getProperties()
	{
		return new Future<Properties>(new Properties());
	}
}
