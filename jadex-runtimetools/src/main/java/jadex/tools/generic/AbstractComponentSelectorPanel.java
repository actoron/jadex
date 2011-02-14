package jadex.tools.generic;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.xml.annotation.XMLClassname;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;

/**
 *  Abstract base class for selector panels.
 */
public abstract class AbstractComponentSelectorPanel extends AbstractSelectorPanel
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess exta;
	
	/** The model name. */
	protected String modelname;
	
	//-------- constructors --------
	
	/**
	 *  Create a new selector panel.
	 */
	public AbstractComponentSelectorPanel(IExternalAccess exta, String modelname)
	{
		this.exta = exta;
		this.modelname = modelname;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the model name.
	 *  @return the model name.
	 */
	public String getModelName()
	{
		return modelname;
	}
	
	/**
	 *  Refresh the combo box.
	 */
	public void refreshCombo()
	{
		exta.scheduleStep(new IComponentStep()
		{
//			public static final String XML_CLASSNAME = "refresh-combo";
			@XMLClassname("refresh-combo")
			public Object execute(IInternalAccess ia)
			{
				ia.getRequiredService("cms")
					.addResultListener(new SwingDefaultResultListener(AbstractComponentSelectorPanel.this) 
				{
					public void customResultAvailable(Object result) 
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						IComponentDescription adesc = cms.createComponentDescription(null, null, null, null, null, getModelName());
						cms.searchComponents(adesc, null, isRemote()).addResultListener(new SwingDefaultResultListener(AbstractComponentSelectorPanel.this)
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
								JComboBox selcb = getSelectionComboBox();
								for(int i=0; i<selcb.getItemCount(); i++)
								{
									IComponentIdentifier oldcid = (IComponentIdentifier)selcb.getItemAt(i);
									if(!newcids.contains(oldcid))
									{
										// remove old cid
										removePanel(oldcid);
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
		
		exta.scheduleStep(
			new IComponentStep()
		{
//			public static final String XML_CLASSNAME = "create-panel";
			@XMLClassname("create-panel")
			public Object execute(IInternalAccess ia)
			{
				ia.getRequiredService("cms").addResultListener(new SwingDefaultResultListener(AbstractComponentSelectorPanel.this)
				{
					public void customResultAvailable(Object result)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						cms.getExternalAccess((IComponentIdentifier)cid)
							.addResultListener(new SwingDefaultResultListener(AbstractComponentSelectorPanel.this)
						{
							public void customResultAvailable(Object result)
							{
								IExternalAccess exta = (IExternalAccess)result;
								createComponentPanel(exta).addResultListener(new SwingDefaultResultListener(AbstractComponentSelectorPanel.this)
								{
									public void customResultAvailable(Object result)
									{
//										System.out.println("add: "+result+" "+sel);
										IComponentViewerPanel panel = (IComponentViewerPanel)result;
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
	
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(Object element)
	{
		return ((IComponentIdentifier)element).getName();
	}
	
	/**
	 *  Create the component panel.
	 */
	public abstract IFuture createComponentPanel(IExternalAccess component);

}
