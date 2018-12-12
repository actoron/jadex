package jadex.platform.service.cli.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.bridge.service.types.remote.ServiceInputConnection;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.platform.service.cli.ACliCommand;
import jadex.platform.service.cli.ArgumentInfo;
import jadex.platform.service.cli.CliContext;
import jadex.platform.service.cli.ResultInfo;

/**
 *  Command to download a file.
 */
public class DownloadFileCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"df", "downloadfile", "download"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Download a file.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "df -s c:\\zips\\basic.zip -d c:\\temp -p Lars-PC_b14 : download file basic.zip from platform Lars-PC_b14 to local dir c:\\temp";
	}
	
	/**
	 *  Invoke the command.
	 *  @param context The context.
	 *  @param args The arguments.
	 */
	public Object invokeCommand(final CliContext context, final Map<String, Object> args)
	{
//		final SubscriptionIntermediateFuture<Long> ret = new SubscriptionIntermediateFuture<Long>();
		final Future<String> ret = new Future<String>();
		
		final String s = (String)args.get("-s");
		final String d = (String)args.get("-d");
		final String pname = (String)args.get("-p");
		final IComponentIdentifier p = pname==null? null: new BasicComponentIdentifier(pname);
		
		final IExternalAccess comp = (IExternalAccess)context.getUserContext();
		
		comp.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				getDeploymentService(ia, p)
					.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IFileTransferService, String>(ret)
//					.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IDeploymentService, Collection<Long>>(ret)
				{
					public void customResultAvailable(final IFileTransferService ds)
					{
						try
						{
							// extract src file name
							int idx = Math.max(s.lastIndexOf("/"), s.lastIndexOf("\\"));
							String fn = idx!=-1? s.substring(idx+1): s;
							File dest = new File(d+File.separator+fn);
							FileOutputStream fos = new FileOutputStream(dest);
							ServiceInputConnection sic = new ServiceInputConnection();
							
							ITerminableIntermediateFuture<Long> fut = ds.downloadFile(sic.getOutputConnection(), s);
							fut.addResultListener(new IIntermediateResultListener<Long>()
							{
								long last = 0;
								long size = -1;
								public void intermediateResultAvailable(final Long result)
								{
									if(size==-1)
									{
//										System.out.println("size: "+result);
										size = result.longValue();
									}
									else
									{
//										System.out.println("res: "+result);
										if(last==0 || System.currentTimeMillis()-2000>last)
										{
											last = System.currentTimeMillis();
		//									System.out.println("rec: "+result);
											final double done = ((int)((result/(double)size)*10000))/100.0;
											DecimalFormat fm = new DecimalFormat("#0.00");
											final String txt = "Copy "+fm.format(done)+"% done ("+SUtil.bytesToString(result)+" / "+SUtil.bytesToString(size)+")";
											System.out.println(txt);
										}
									}
								}
								
								public void finished()
								{
//									System.out.println("Copied: "+s+" to "+d);
									ret.setResult(s+" on "+p+" to "+d);
			//						((JCCAgent)ia).getControlCenter().getPCC().setStatusText("Copied: "+sel1+" to "+sel2);
								}
								
								public void resultAvailable(Collection<Long> result)
								{
									finished();
								}
								
								public void exceptionOccurred(final Exception exception)
								{
									ret.setExceptionIfUndone(exception);
								}
							});
							
							sic.writeToOutputStream(fos, comp).addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<Long>()
							{
								public void intermediateResultAvailable(Long result) 
								{
			//						System.out.println("wro ira: "+result);
								}
								public void finished()
								{
			//						System.out.println("wro fin");
								}
								public void resultAvailable(Collection<Long> result)
								{
			//						System.out.println("wro ra: "+result);
								}
								public void exceptionOccurred(Exception exception)
								{
			//						System.out.println("wro ex: "+exception);
								}
							}));
						}
						catch(Exception e)
						{
							ret.setException(e);
						}
					}
				}));
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the deployment service. 
	 */
	protected IFuture<IFileTransferService> getDeploymentService(final IInternalAccess ia, final IComponentIdentifier cid)
	{
		final Future<IFileTransferService> ret = new Future<IFileTransferService>();
		
		if(cid!=null)
		{
			// global search not a good idea due to long timeout but what to do else?
			ia.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IFileTransferService.class, ServiceScope.GLOBAL))
				.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<IFileTransferService>()
			{
				public void intermediateResultAvailable(IFileTransferService result)
				{
//					System.out.println("found: "+((IService)result).getId().getProviderId().getRoot()+" - "+cid);
					if(((IService)result).getServiceId().getProviderId().getRoot().equals(cid))
					{
						ret.setResult(result);
					}
				}
				
				public void finished()
				{
					ret.setExceptionIfUndone(new RuntimeException("Deployment service not found: "+cid));
				}
				
				public void resultAvailable(Collection<IFileTransferService> result)
				{
					for(IFileTransferService ds: result)
					{
						intermediateResultAvailable(ds);
					}
					finished();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setExceptionIfUndone(new RuntimeException("Deployment service not found: "+cid));
				}
			}));
			
			// does not work due to cid has no address
//			ia.getServiceContainer().searchService( new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
//				.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IDeploymentService>(ret)
//			{
//				public void customResultAvailable(final IComponentManagementService cms)
//				{
//					cms.getExternalAccess(cid).addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IExternalAccess, IDeploymentService>(ret)
//					{
//						public void customResultAvailable(IExternalAccess plat)
//						{
//							plat.scheduleStep(new IComponentStep<IDeploymentService>()
//							{
//								public IFuture<IDeploymentService> execute(IInternalAccess ia)
//								{
//									return ia.getServiceContainer().searchService(IDeploymentService.class, ServiceScope.PLATFORM);
//								}
//							}).addResultListener(new DelegationResultListener<IDeploymentService>(ret));
//						}
//					}));
//				}
//			}));
		}
		else
		{
			ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IFileTransferService.class, ServiceScope.PLATFORM))
				.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<IFileTransferService>(ret)));
		}
		
		return ret;
	}
	
	/**
	 *  Get the argument infos.
	 *  @param context The context.
	 *  @return The argument infos.
	 */
	public ArgumentInfo[] getArgumentInfos(CliContext context)
	{
		ArgumentInfo srcfile = new ArgumentInfo("-s", String.class, null, "The remote source file.", null);
		ArgumentInfo destdir = new ArgumentInfo("-d", String.class, null, "The local destination dir.", null);
		ArgumentInfo targetplat = new ArgumentInfo("-p", String.class, null, "The source platform", null);
		return new ArgumentInfo[]{srcfile, destdir, targetplat};
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
				
				buf.append("upload finished: ").append(val).append(SUtil.LF);
				
				return buf.toString();
			}
		});
	}
}
