package jadex.tools.awareness;

/**
 *  The awareness component plugin is used to wrap the awareness agent panel as JCC plugin.
 */
public class AwarenessComponentPlugin //extends AbstractComponentPlugin
{
//	//-------- constants --------
//
//	static
//	{
//		icons.put("awareness", SGUI.makeIcon(AwarenessComponentPlugin.class, "/jadex/tools/common/images/awareness.png"));
//		icons.put("awareness_sel", SGUI.makeIcon(AwarenessComponentPlugin.class, "/jadex/tools/common/images/awareness_sel.png"));
//	}
//
//	//-------- methods --------
//	
//	/**
//	 *  Get the model name.
//	 *  @return the model name.
//	 */
//	public String getModelName()
//	{
//		String ret = AwarenessManagementAgent.class.getName();
//		ret = ret.substring(0, ret.length()-5); // strip Agent
//		return ret;
////		return "jadex.platform.service.awareness.management.AwarenessManagement";
//	}
//	
//	/**
//	 *  Get the name.
//	 *  @return The name.
//	 */
//	public String getName()
//	{
//		return "Awareness Settings";
//	}
//	
//	/**
//	 *  Create the component panel.
//	 */
//	public IFuture<IAbstractViewerPanel> createComponentPanel(IExternalAccess component)
//	{
//		AwarenessAgentPanel awap = new AwarenessAgentPanel();
//		awap.init(getJCC(), component);
//		return new Future<IAbstractViewerPanel>(awap);
//	}
//	
//	/**
//	 *  Get the icon.
//	 */
//	public Icon getToolIcon(boolean selected)
//	{
//		return selected? icons.getIcon("awareness_sel"): icons.getIcon("awareness");
//	}
}
