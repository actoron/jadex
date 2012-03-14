package jadex.tools.testcenter;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.xml.annotation.XMLClassname;

/**
 *  Helper class to identify test cases.
 */
public class STestCenter
{
	/**
	 *  Check if a component model can be started as test case.
	 */
	public static IFuture<Boolean>	isTestcase(final String model, IExternalAccess access, final IResourceIdentifier rid)
	{
		return access.scheduleImmediate(new IComponentStep<Boolean>()
		{
			@XMLClassname("isTestcase")
			public IFuture<Boolean> execute(IInternalAccess ia)
			{
				final Future<Boolean>	ret	= new Future<Boolean>();
				final IExternalAccess access	= ia.getExternalAccess();
				SComponentFactory.isLoadable(access, model, rid)
					.addResultListener(new DelegationResultListener<Boolean>(ret)
				{
					public void customResultAvailable(Boolean result)
					{
						if(result.booleanValue())
						{
							SComponentFactory.loadModel(access, model, rid)
								.addResultListener(new ExceptionDelegationResultListener<IModelInfo, Boolean>(ret)
							{
								public void customResultAvailable(final IModelInfo model)
								{
									if(model!=null && model.getReport()==null)
									{
										IArgument[]	results	= model.getResults();
										boolean	istest	= false;
										for(int i=0; !istest && i<results.length; i++)
										{
											if(results[i].getName().equals("testresults") && results[i].getClazz()!=null
												&& "jadex.base.test.Testcase".equals(results[i].getClazz().getTypeName()))
//												&& Testcase.class.equals(results[i].getClazz(ls.getClassLoader(model.getResourceIdentifier()), model.getAllImports())))
											{	
												istest	= true;
											}
										}
										ret.setResult(istest? Boolean.TRUE: Boolean.FALSE);
									}
									else
									{
										ret.setResult(Boolean.FALSE);
									}
								}
							});
						}
						else
						{
							ret.setResult(Boolean.FALSE);
						}
					}
				});
				return ret;
			}
		});
	}
}

