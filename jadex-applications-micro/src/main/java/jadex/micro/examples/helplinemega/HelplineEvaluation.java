package jadex.micro.examples.helplinemega;

import java.io.FileWriter;
import java.util.Collection;
import java.util.Collections;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;

/**
 *  Main class to launch a given number of superpeers.
 */
public class HelplineEvaluation
{
	public static void main(String[] args)	throws Exception
	{
		// Parse args into config and extract settings.
		IPlatformConfiguration config	= PlatformConfigurationHandler.getDefaultNoGui();
//		config.setSimulation(true);	// Todo: fix sim delay in registry!?
		config.setValue("spcnt", 3);
		config.setValue("platformcnt", 10);
		config.setValue("personinc", 100);
		config.enhanceWith(Starter.processArgs(args));
		int	spcnt	= (Integer) config.getArgs().get("spcnt");
		int	platformcnt	= (Integer) config.getArgs().get("platformcnt");
		int	personinc	= (Integer) config.getArgs().get("personinc");

		// Start #spcnt superpeers.
		System.out.println("Starting "+spcnt+" superpeer platforms.");
		config.setSuperpeer(true);
		long	start	= System.nanoTime();
		for(int i=0; i<spcnt; i++)
		{
			Starter.createPlatform(config).get();
			System.out.println("Started Superpeer #"+i);
		}
		long	end	= System.nanoTime();
		config.setSuperpeer(false);
		System.out.println("Started "+spcnt+" superpeers in "+((end-start)/100000000/10.0)+" seconds.");
		
		// Start #platformcnt helpline platforms.
		System.out.println("Starting "+platformcnt+" helpline platforms.");
		start	= System.nanoTime();
		IExternalAccess[]	platforms	= new IExternalAccess[platformcnt];
		for(int i=0; i<platformcnt; i++)
		{
			platforms[i]	= Starter.createPlatform(config).get();
			System.out.println("Started Platform #"+i);
		}
		end	= System.nanoTime();
		System.out.println("Started "+platformcnt+" helpline platforms in "+((end-start)/100000000/10.0)+" seconds.");
		
		String	filename	= "eval"+System.currentTimeMillis()+".csv";
		FileWriter	out	= new FileWriter(filename, true);
		out.write("# of Services;Creation Time;Search Time;Found Services;Settings: spcnt="+spcnt+" platformcnt="+platformcnt+" personinc="+personinc+"\n");
		out.close();
		
		// Loop to start (additional) #personinc helpline components per platform until program is interrupted.
		for(int offset=0; ; offset+=personinc)
		{
			System.gc();
			start	= System.nanoTime();
			for(int i=0; i<platforms.length; i++)
			{
				IComponentManagementService	cms	= SServiceProvider.getService(platforms[i], IComponentManagementService.class).get();
				for(int j=0; j<personinc; j++)
				{
					cms.createComponent(HelplineAgent.class.getName()+".class",
						new CreationInfo(Collections.singletonMap("person", (Object)("person0")))).getFirstResult();
//						new CreationInfo(Collections.singletonMap("person", (Object)("person"+(offset+j))))).getFirstResult();
				}
			}
			end	= System.nanoTime();
			String	creation	= (""+((end-start)/1000000)).replace('.', ',');
			System.out.println("Started "+personinc*platformcnt+" helpline apps in "+creation+" milliseconds. Total: "+(offset+personinc)*platformcnt+", per platform: "+(offset+personinc));
			
//			Thread.sleep(5000);	// Wait for registration?
			
			// Search for first person to check if searches get slower.
			System.gc();
			start	= System.nanoTime();
			Collection<IHelpline>	found	= SServiceProvider.getTaggedServices(platforms[0], IHelpline.class, RequiredServiceInfo.SCOPE_NETWORK, "person0").get();
			end	= System.nanoTime();
			String	search	= (""+((end-start)/1000000)).replace('.', ',');
//			System.out.println("Found "+found.size()+" of "+platformcnt+" helpline apps in "+search+" milliseconds.");
			System.out.println("Found "+found.size()+" of "+(offset+personinc)*platformcnt+" helpline apps in "+search+" milliseconds.");
			
			out	= new FileWriter(filename, true);
			out.write((offset+personinc)*platformcnt+";"+creation+";"+search+";"+found.size()+"\n");
			out.close();

			
//			break;
		}
	}
}
