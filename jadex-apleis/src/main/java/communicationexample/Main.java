package communicationexample;

import eis.AgentListener;
import eis.EnvironmentListener;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.EnvironmentEvent;
import eis.iilang.Identifier;
import eis.iilang.Percept;

/**
 * The main class of the example. It registers three agents, associates them
 * with on entity each and performs a special messaging-action.
 * 
 * @author tristanbehrens
 *
 */
public class Main implements EnvironmentListener,AgentListener {

	public static void main(String[] args) {
		
		Main m = new Main();

		try {
	
			EnvironmentInterface ei = new EnvironmentInterface();
			
			ei.attachEnvironmentListener(m);

			ei.registerAgent("ag1");
			ei.registerAgent("ag2");
			ei.registerAgent("ag3");
	
			ei.associateEntity("ag1", "en1");
			ei.associateEntity("ag2", "en2");
			ei.associateEntity("ag3", "en3");
			
			ei.performAction("ag1", new Action("tellall", new Identifier("Hi")));
			ei.performAction("ag2", new Action("tellall", new Identifier("Hello")));
			ei.performAction("ag3", new Action("tellall", new Identifier("Bonjour")));
			ei.performAction("ag4", new Action("tellall", new Identifier("Greetings"))); 

		} 
		catch(AgentException e) {
			
			System.out.println("Caught exception: " + e.getMessage());
			
		} catch (RelationException e) {

			e.printStackTrace();
		
		} catch (ActException e) {
			e.printStackTrace();
		} catch (NoEnvironmentException e) {
			e.printStackTrace();
		}
	}

	public void handlePercept(String agent, Percept percept) {

		System.out.println("Agent \"" + agent + "\" gets the event \"" + percept + "\"");
		
	}

	public void handleDeletedEntity(String entity) {
	}

	public void handleEnvironmentEvent(EnvironmentEvent event) {
	}

	public void handleFreeEntity(String entity) {
	}

	public void handleNewEntity(String entity) {
	}
	
}
