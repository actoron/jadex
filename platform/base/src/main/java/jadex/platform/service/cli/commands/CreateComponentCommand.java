package jadex.platform.service.cli.commands;

import java.util.List;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Command for creating components.
 */
public class CreateComponentCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"cc", "createcomponent"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Create a component on the platform.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "cc -model jadex/micro/examples/helloworld/HelloWorldAgent.class -rid applications-micro " +
			": create component from model jadex/micro/examples/helloworld/HelloWorldAgent.class with resource id similar to rid applications-micro";
	}
	
//	/**
//	 *  Invoke the command.
//	 *  @param context The context.
//	 *  @param args The arguments.
//	 */
//	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
//	{
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		
//		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
//		
//		comp.scheduleStep(new IComponentStep<IComponentIdentifier>()
//		{
//			public IFuture<IComponentIdentifier> execute(IInternalAccess ia)
//			{
//				final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//				
//				ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
//				{
//					public void customResultAvailable(final IComponentManagementService cms)
//					{
//						final String name = (String)args.get("-name");
//						final String model = (String)args.get("-model");
//						final String config = (String)args.get("-config");
//						final IComponentIdentifier parent = (IComponentIdentifier)args.get("-parent");
//						final String ridtext = (String)args.get("-rid");
//						final Integer count = (Integer)args.get("-cnt");
//						
//						IExternalAccess comp = (IExternalAccess)((CliContext)context).getUserContext();
//				
//						comp.searchService( new ServiceQuery<>( ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//							.addResultListener(new ExceptionDelegationResultListener<ILibraryService, IComponentIdentifier>(ret)
//						{
//							public void customResultAvailable(ILibraryService  libs)
//							{
//								libs.getAllResourceIdentifiers().addResultListener(new ExceptionDelegationResultListener<List<IResourceIdentifier>, IComponentIdentifier>(ret)
//								{
//									public void customResultAvailable(List<IResourceIdentifier> rids) 
//									{
//										IResourceIdentifier found = null;
//										if(ridtext!=null)
//										{
//											for(IResourceIdentifier rid: rids)
//											{
//												if(rid.toString().indexOf(ridtext)!=-1)
//												{
//													if(found==null)
//													{
//														found = rid;
//													}
//													else
//													{
//														ret.setException(new RuntimeException("More than one rid possible: "+found+" "+rid));
//														return;
//													}
//												}
//											}
//										}
//										
//										CreationInfo info = new CreationInfo();
//										if(parent!=null)
//											info.setParent(parent);
//										if(config!=null)
//											info.setConfiguration(config);
//										if(found!=null)
//											info.setResourceIdentifier(found);
//										
//										for(int i=0; i<(count==null? 1: count.intValue()); i++)
//											cms.createComponent(name, model, info, null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//									}
//								});
//							}
//						});
//					}
//				});
//				
//				return ret;
//			}
//		}).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
//		
//		return ret;
//	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		comp.scheduleStep(new IComponentStep<IComponentIdentifier>()
		{
			public IFuture<IComponentIdentifier> execute(IInternalAccess ia)
			{
				final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
				
				final String name = (String)args.get("-name");
				final String model = (String)args.get("-model");
				final String config = (String)args.get("-config");
				final IComponentIdentifier parent = (IComponentIdentifier)args.get("-parent");
				final String ridtext = (String)args.get("-rid");
				final Integer count = (Integer)args.get("-cnt");
				
				IExternalAccess comp = (IExternalAccess)((CliContext)context).getUserContext();
		
				comp.searchService( new ServiceQuery<>(ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM))
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
								if(name!=null)
									info.setName(name);
								if(model!=null)
									info.setFilename(model);
								
								for(int i=0; i<(count==null? 1: count.intValue()); i++)
								{
									comp.createComponent(info, null).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IComponentIdentifier>(ret)
									{
										public void customResultAvailable(IExternalAccess result) throws Exception
										{
											ret.setResult(result.getId());
										}
									});
								}
							}
						});
					}
				});
				
				return ret;
			}
		}).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
		
		return ret;
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo name = new ArgumentInfo("-name", String.class, null, "The component name.", null);
		ArgumentInfo model = new ArgumentInfo("-model", String.class, null, "The model name.", null);
		ArgumentInfo config = new ArgumentInfo("-config", String.class, null, "The model configuration.", null);
		ArgumentInfo parent = new ArgumentInfo("-parent", IComponentIdentifier.class, null, "The parent.", DestroyComponentCommand.CID_CONVERTER);
		ArgumentInfo rid = new ArgumentInfo("-rid", String.class, null, "The resource identifier.", null);
		ArgumentInfo cnt = new ArgumentInfo("-cnt", Integer.class, null, "The number of components to start.", null);
		
		return new ArgumentInfo[]{name, model, config, parent, rid, cnt};
	}
	
	/**
	 *  Get the result info.
	 *  @param context The context.
	 *  @return The result info.
	 */
	public ResultInfo getResultInfo(CliContext context, Map<String, Object> args)
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
