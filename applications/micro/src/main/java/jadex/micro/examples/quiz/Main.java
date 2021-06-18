package jadex.micro.examples.quiz;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.cms.CreationInfo;

/**
 *  Main for starting the application.
 */
public class Main
{
	/**
	 *  Main method.
	 */
	public static void main(String[] args)
	{
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefault()).get();
		platform.createComponent(new CreationInfo()
			.setFilenameClass(QuizMasterAgent.class)
			.addArgument("scope", ServiceScope.GLOBAL)).get();
		platform.createComponent(new CreationInfo()
			.setFilenameClass(QuizClientAgent.class)
			.addArgument("scope", ServiceScope.GLOBAL)).get();
	}
}
