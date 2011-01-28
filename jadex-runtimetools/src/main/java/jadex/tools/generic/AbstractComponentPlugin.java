package jadex.tools.generic;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.xml.annotation.XMLClassname;

import java.util.HashSet;
import java.util.Set;

/**
 *  Plugin that allows to look at viewable components.
 */
public abstract class AbstractComponentPlugin extends AbstractGenericPlugin
{	
	//-------- methods --------
	
	/**
	 *  Get the model name.
	 *  @return the model name.
	 */
	public abstract String getModelName();
	
	/**
	 *  Create the component panel.
	 */
	public abstract IFuture createComponentPanel(IExternalAccess component);
	
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(Object element)
	{
		return ((IComponentIdentifier)element).getName();
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return getModelName();
	}

	/**
	 *  Refresh the combo box.
	 */
	public void refreshCombo()
	{
		getJCC().getExternalAccess().scheduleStep(new IComponentStep()
		{
//			public static final String XML_CLASSNAME = "refresh-combo";
			@XMLClassname("refresh-combo")
			public Object execute(IInternalAccess ia)
			{
				ia.getRequiredService("cms")
					.addResultListener(new SwingDefaultResultListener(centerp) 
				{
					public void customResultAvailable(Object result) 
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						IComponentDescription adesc = cms.createComponentDescription(null, null, null, null, null, getModelName());
						cms.searchComponents(adesc, null, remotecb.isSelected()).addResultListener(new SwingDefaultResultListener(centerp)
						{
							public void customResultAvailable(Object result)
							{
								IComponentDescription[] descs = (IComponentDescription[])result;
		//						System.out.println("descs: "+SUtil.arrayToString(descs)+" "+remotecb.isSelected());
								Set newcids = new HashSet();
								for(int i=0; i<descs.length; i++)
								{
									newcids.add(descs[i].getName());
								}
								
								// Find items to remove
								for(int i=0; i<selcb.getItemCount(); i++)
								{
									IComponentIdentifier oldcid = (IComponentIdentifier)selcb.getItemAt(i);
									if(!newcids.contains(oldcid))
									{
										// remove old cid
										IComponentViewerPanel panel = (IComponentViewerPanel)panels.remove(oldcid);
										if(panel!=null)
											removePanel(panel);
									}
								}
								
								selcb.removeAllItems();
								for(int i=0; i<descs.length; i++)
								{
									selcb.addItem(descs[i].getName());
								}
							}
						});
					}
				});
				return null;
			}
		});
	}
	
	/**
	 *  Create a panel for a component identifier.
	 */
	public IFuture createPanel(final Object element)
	{
		final Future ret = new Future();
		final IComponentIdentifier cid = (IComponentIdentifier)element;
		
		getJCC().getExternalAccess().scheduleStep(
			new IComponentStep()
		{
//			public static final String XML_CLASSNAME = "create-panel";
			@XMLClassname("create-panel")
			public Object execute(IInternalAccess ia)
			{
				ia.getRequiredService("cms").addResultListener(new SwingDefaultResultListener(centerp)
				{
					public void customResultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						cms.getExternalAccess((IComponentIdentifier)cid)
							.addResultListener(new SwingDefaultResultListener(centerp)
						{
							public void customResultAvailable(Object result)
							{
								IExternalAccess exta = (IExternalAccess)result;
								createComponentPanel(exta).addResultListener(new SwingDefaultResultListener(centerp)
								{
									public void customResultAvailable(Object result)
									{
//										System.out.println("add: "+result+" "+sel);
										IComponentViewerPanel panel = (IComponentViewerPanel)result;
										panels.put(cid, panel);
										centerp.add(panel.getComponent(), cid);
										if(getPanelProperties()!=null)
											panel.setProperties(getPanelProperties());
										ret.setResult(panel);
									}
									
									public void customExceptionOccurred(Exception exception)
									{
										ret.setException(exception);
									}
								});
							}
							public void customExceptionOccurred(Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
				return null;
			}
		});
		
		return ret;
	}
	
}
