package jadex.bdiv3;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.IFuture;

public class BDIAgentFactoryAndroid extends BDIAgentFactory
{

	public BDIAgentFactoryAndroid(IServiceProvider provider)
	{
		super(provider);
	}

	@Override
	public IFuture<Void> startService()
	{
		IFuture<Void> startService = super.startService();
		startService.addResultListener(new DefaultResultListener<Void>()
		{

			@Override
			public void resultAvailable(Void result)
			{
				prepareBDIModels();
			}
		});
		return startService;
	}

	protected void prepareBDIModels()
	{
		libservice.getClassLoader(null).addResultListener(new DefaultResultListener<ClassLoader>()
		{

			@Override
			public void resultAvailable(ClassLoader cl)
			{
				try
				{
//					IModelInfo mi = loader.loadComponentModel(model, imports, cl, new Object[]
//					{rid, getProviderId().getRoot()}).getModelInfo();
					
					IBDIClassGenerator gen = BDIClassGeneratorFactory.getInstance().createBDIClassGenerator();
					
					ClassLoader userCl = SUtil.androidUtils().findJadexDexClassLoader(cl);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected Class getMicroAgentClass(String clname, String[] imports, ClassLoader classloader)
	{
		return super.getMicroAgentClass(clname, imports, classloader);
	}
	
	

}
