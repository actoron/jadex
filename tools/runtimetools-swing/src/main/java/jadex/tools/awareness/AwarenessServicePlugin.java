package jadex.tools.awareness;

/**
 *  The security service plugin is used to wrap the security panel as JCC plugin.
 */
//TODO: duplicate: component vs service plugin? impl uses agent class, so drop service plugin
public class AwarenessServicePlugin //extends AbstractServicePlugin
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
//	 *  Get the service type.
//	 *  @return The service type.
//	 */
//	public Class<?> getServiceType()
//	{
//		return IAwarenessManagementService.class;
//	}
//	
//	/**
//	 *  Create the service panel.
//	 */
//	public IFuture<IAbstractViewerPanel> createServicePanel(IService service)
//	{
//		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
//		final AwarenessAgentPanel awap = new AwarenessAgentPanel();
//		awap.init(getJCC(), service).addResultListener(new ExceptionDelegationResultListener<Void, IAbstractViewerPanel>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				ret.setResult(awap);
//			}
//		});
//		return ret;
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
