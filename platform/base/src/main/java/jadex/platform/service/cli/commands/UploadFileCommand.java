package jadex.platform.service.cli.commands;

import java.io.File;
import java.io.FileInputStream;
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
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.filetransfer.IFileTransferService;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
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
 *  Command to upload a file.
 */
public class UploadFileCommand extends ACliCommand
{
	/**
	 *  Get the command names (name including alias').
	 *  @return A string array of the command name and optional further alias names.
	 */
	public String[] getNames()
	{
		return new String[]{"uf", "uploadfile", "upload"};
	}
	
	/**
	 *  Get the command description.
	 *  @return The command description.
	 */
	public String getDescription()
	{
		return "Upload a file.";
	}
	
	/**
	 *  Get example usage(s).
	 *  @return Example usages.
	 */
	public String getExampleUsage()
	{
		return "uf -s c:\\zips\\basic.zip -d c:\\Files -p Delle_a10 : upload local file basic.zip to target dir c:\\Files on platform Delle_a10";
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
							final File source = new File(s);
							final FileInputStream fis = new FileInputStream(source);
							ServiceOutputConnection soc = new ServiceOutputConnection();
							soc.writeFromInputStream(fis, comp).addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<Long>()
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
							ITerminableIntermediateFuture<Long> fut = ds.uploadFile(soc.getInputConnection(), d, source.getName());
							fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<Long>()
							{
								long last = 0;
								public void intermediateResultAvailable(final Long result)
								{
									if(last==0 || System.currentTimeMillis()-2000>last)
									{
										last = System.currentTimeMillis();
	//									System.out.println("rec: "+result);
										final double done = ((int)((result/(double)source.length())*10000))/100.0;
										DecimalFormat fm = new DecimalFormat("#0.00");
										final String txt = "Copy "+fm.format(done)+"% done ("+SUtil.bytesToString(result)+" / "+SUtil.bytesToString(source.length())+")";
										System.out.println(txt);
									}
								}
								
								public void finished()
								{
//									System.out.println("Copied: "+s+" to "+d);
									ret.setResult(s+" to "+d+" on "+p);
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
			ia.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IFileTransferService.class, RequiredServiceInfo.SCOPE_GLOBAL))
				.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<IFileTransferService>()
			{
				public void intermediateResultAvailable(IFileTransferService result)
				{
//					System.out.println("found: "+((IService)result).getServiceIdentifier().getProviderId().getRoot()+" - "+cid);
					if(((IService)result).getServiceIdentifier().getProviderId().getRoot().equals(cid))
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
//			SServiceProvider.searchService(ia.getServiceContainer(), new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
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
//									return ia.getServiceContainer().searchService(IDeploymentService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//								}
//							}).addResultListener(new DelegationResultListener<IDeploymentService>(ret));
//						}
//					}));
//				}
//			}));
		}
		else
		{
			ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IFileTransferService.class, RequiredServiceInfo.SCOPE_PLATFORM))
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
		ArgumentInfo srcfile = new ArgumentInfo("-s", String.class, null, "The source file.", null);
		ArgumentInfo destdir = new ArgumentInfo("-d", String.class, null, "The destination dir.", null);
		ArgumentInfo targetplat = new ArgumentInfo("-p", String.class, null, "The target platform", null);
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
	
//	public static void main(String[] args)
//	{
//		String[] phases = {"|", "/", "-", "\\"};
//
//		System.out.printf("Spinning... |");
//
//		while(true)
//		{
//			for(String phase : phases)
//			{
//				System.out.printf("\b" + phase);
//				try
//				{
//					Thread.sleep(100);
//				}
//				catch(Exception e)
//				{
//				}
//			}
//		}
//	}
}
