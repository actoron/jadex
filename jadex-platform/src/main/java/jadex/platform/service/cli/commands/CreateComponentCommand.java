/**
 * 
 */
package jadex.platform.service.cli.commands;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class CreateComponentCommand extends ACliCommand
{
	/**
	 *  Get the command names.
	 */
	public String[] getNames()
	{
		return new String[]{"cc", "createcomponent"};
	}
	
	/**
	 *  Get the command description.
	 */
	public String getDescription()
	{
		return "Create a component on the platform.";
	}
	
	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				final String name = (String)args.get("-name");
				final String model = (String)args.get("-model");
				final String config = (String)args.get("-config");
				final IComponentIdentifier parent = (IComponentIdentifier)args.get("-parent");
				final String ridtext = (String)args.get("-rid");
				
				IExternalAccess comp = (IExternalAccess)((CliContext)context).getUserContext();
		
				SServiceProvider.getService(comp.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<ILibraryService, IComponentIdentifier>(ret)
				{
					public void customResultAvailable(ILibraryService  libs)
					{
						libs.getAllResourceIdentifiers().addResultListener(new ExceptionDelegationResultListener<List<IResourceIdentifier>, IComponentIdentifier>(ret)
						{
							public void customResultAvailable(List<IResourceIdentifier> rids) 
							{
								IResourceIdentifier found = null;
								if(ridtext!=null)
								{
									for(IResourceIdentifier rid: rids)
									{
										if(rid.toString().indexOf(ridtext)!=-1)
										{
											if(found==null)
											{
												found = rid;
											}
											else
											{
												ret.setException(new RuntimeException("More than one rid possible: "+found+" "+rid));
												return;
											}
										}
									}
								}
								
								CreationInfo info = new CreationInfo();
								if(parent!=null)
									info.setParent(parent);
								if(config!=null)
									info.setConfiguration(config);
								if(found!=null)
									info.setResourceIdentifier(found);
								
								cms.createComponent(name, model, info, null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo name = new ArgumentInfo("-name", String.class, null, "The component name.", null);
		ArgumentInfo model = new ArgumentInfo("-model", String.class, null, "The model name.", null);
		ArgumentInfo config = new ArgumentInfo("-config", String.class, null, "The model configuration.", null);
		ArgumentInfo parent = new ArgumentInfo("-parent", IComponentIdentifier.class, null, "The parent.", DestroyComponentCommand.CID_CONVERTER);
		ArgumentInfo rid = new ArgumentInfo("-rid", String.class, null, "The resource identifier.", null);
		
		return new ArgumentInfo[]{name, model, config, parent, rid};
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public ResultInfo getResultInfo(CliContext context)
	{
		return new ResultInfo(IComponentIdentifier.class, "The creation result.", new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				StringBuffer buf = new StringBuffer();
				
				buf.append("component successfully created: ").append(val).append(SUtil.LF);
				
				return buf.toString();
			}
		});
	}
}
