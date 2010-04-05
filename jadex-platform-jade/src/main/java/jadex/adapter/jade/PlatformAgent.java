package jadex.adapter.jade;

import jade.content.lang.sl.SLCodec;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.wrapper.gateway.GatewayAgent;

/**
 *  Agent that is used by platform services to execute
 *  agent-related functionalities like message sending.
 *  Is used via the JADE gateway method.
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
   			Platform.getPlatform().setPlatformController(getContainerController().getPlatformController());
//       	Platform.getPlatform().setPlatformAgentController(getContainerController().getAgent(getLocalName()));
  			Platform.getPlatform().setPlatformAgent(getAID());
  	   	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
        super.setup();
    }
}
