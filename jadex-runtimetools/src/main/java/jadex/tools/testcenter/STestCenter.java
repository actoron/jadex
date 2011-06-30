package jadex.tools.testcenter;

import jadex.base.SComponentFactory;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Helper class to identify test cases.
 */
public class STestCenter
{
	/**
	 *  Check if a component model can be started as test case.
	 */
	public static IFuture	isTestcase(final String model, final IExternalAccess access)
	{
		final Future	ret	= new Future();
		
		SComponentFactory.isLoadable(access, model).addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(((Boolean)result).booleanValue())
				{
					SComponentFactory.loadModel(access, model).addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							boolean	istest	= false;
							IModelInfo model = (IModelInfo)result;
							if(model!=null && model.getReport()==null)
							{
								IArgument[]	results	= model.getResults();
								for(int i=0; !istest && i<results.length; i++)
								{
									if(results[i].getName().equals("testresults") && results[i].getClassname().equals("Testcase"))
										istest	= true;
								}
							}
							
							ret.setResult(Boolean.valueOf(istest));
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
}

