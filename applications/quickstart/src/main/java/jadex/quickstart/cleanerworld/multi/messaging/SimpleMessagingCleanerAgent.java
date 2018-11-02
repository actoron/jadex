package jadex.quickstart.cleanerworld.multi.messaging;

import java.util.LinkedHashSet;
import java.util.Set;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.FipaMessage;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentMessageArrived;
import jadex.quickstart.cleanerworld.environment.ICleaner;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.environment.impl.Cleaner;
import jadex.quickstart.cleanerworld.gui.SensorGui;

/**
 *  Simple example of using the environment sensor.
 *  @author Alexander Pokahr
 *  @version 1.1 (2017/11/07)
 *
 */
@Agent(type="bdi")
public class SimpleMessagingCleanerAgent
{
	//-------- beliefs that can be used in plan and goal conditions --------
	
	/** Set of currently seen cleaners. Managed by SensorActuator object. */
	@Belief
	private Set<ICleaner>	others	= new LinkedHashSet<>();
	
	/** The sensor gives access to the environment. */
	private SensorActuator	actsense	= new SensorActuator();
	
	//-------- simple example behavior --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@AgentBody
	private void	exampleBehavior(IBDIAgentFeature bdifeature)
	{
		// Manage the belief of other cleaners.
		actsense.manageCleanersIn(others);
		
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);

		while(true)
		{
			// move to random location in the area (0.0, 0.0) - (1.0, 1.0). 
			actsense.moveTo(Math.random(), Math.random());
		}
	}
	
	//-------- example for sending/receiving messages --------
	
	/**
	 *  An example plan to be executed whenever a new cleaner is seen.
	 *  Sends a message with all knows wastes to the other cleaner.
	 *  @param other The other cleaner (info object provided by sensor).
	 *  @param agent Internal API object of this agent used to send a message.
	 */
	@Plan(trigger=@Trigger(factadded="others"))
	public void cleanerAdded(Cleaner other, IInternalAccess agent)
	{
		// Messages can be sent to any agent.
		// Each agent decides itself how to react to a message.
		System.out.println("Agent "+agent+" sending message to: "+other);
		
		// You can use performatives (i.e. speech-acts) to differentiate messages.
		// There are some predefined constants according to the FIPA standard: http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729689
		// You can also define your own, as they are just arbitrary strings, e.g.: "AskPickupWaste"
		String	performative	= FipaMessage.Performative.INFORM;
		
		// The content can be an arbitrary Java object like a waste or a set of wastes.
		Object	content	= actsense.getWastes();
		
		FipaMessage	message 	= new FipaMessage(performative, content);

		// The receiver can be a single agent or also multiple agents
		message.addReceiver(other.getAgentIdentifier());

		agent.getFeature(IMessageFeature.class).sendMessage(message).get();
	}
	
	/**
	 *  This method gets called whenever the agent receives a message.
	 */
	@AgentMessageArrived
	void messageArrived(FipaMessage message)
	{
		// The sender is an identifier that can be used to reply to the agent.
		IComponentIdentifier	sender	= message.getSender();
		String	performative	= message.getPerformative();
		Object	content	= message.getContent();
		System.out.println("Agent received message: "+performative+"("+content+") from "+sender);
	}
}
