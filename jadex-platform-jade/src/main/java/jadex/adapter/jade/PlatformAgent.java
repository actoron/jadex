package jadex.adapter.jade;

import jade.content.lang.sl.SLCodec;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.wrapper.gateway.GatewayAgent;

/**
 * 
 */
public class PlatformAgent extends GatewayAgent
{
	
    public void setup() 
    { 
    	getContentManager().registerOntology(JADEManagementOntology.getInstance());
    	getContentManager().registerOntology(FIPAManagementOntology.getInstance());
    	SLCodec codec = new SLCodec();
    	getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
    	getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL1);
    	getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL2);
    	getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL); 
    	
    	try
    	{
   			Platform.getPlatform().setPlatformAgent(getAID());
   			Platform.getPlatform().setPlatformController(getContainerController().getPlatformController());
//       	Platform.getPlatform().setPlatformAgentController(getContainerController().getAgent(getLocalName()));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
        super.setup();
    }
}
