package carriageexample;

import java.util.LinkedList;

import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.*;

abstract class Agent extends Thread {
	
	protected EnvironmentInterface ei = null;
	protected String id = null;
	
	public Agent(EnvironmentInterface ei, String id) {
		
		this.ei = ei;
		this.id = id;
		
		this.setPriority(MIN_PRIORITY);
		
	}
	
	protected void say(String msg) {
		
		System.out.println(id + " says: " + msg);
		
	}
	
}

class PushingAgent extends Agent {
	
	public PushingAgent(EnvironmentInterface ei, String id) {
	
		super(ei,id);

	}

	public void run() {
		
		try {

			//ei.performAction(id, new Item("enter"));
			ei.associateEntity(id, ei.getFreeEntities().getFirst());
			
			while(true) {
			
				// perceive
				LinkedList<Percept> percepts = null;
				percepts = ei.getAllPercepts(id);
				say("I believe the carriage is at " + percepts);

				// act
				ei.performAction(id, new Action("push"));
		
				try {
					Thread.sleep(950);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		} catch (PerceiveException e) {
			e.printStackTrace();
		} catch (RelationException e) {
			e.printStackTrace();
		} catch (ActException e) {
			e.printStackTrace();
		} catch (NoEnvironmentException e) {
			e.printStackTrace();
		}
		
		
	}
	
}

class AlternatingAgent extends Agent {
	
	public AlternatingAgent(EnvironmentInterface ei, String id) {
		super(ei,id);
	}

	public void run() {
		
		try {

			ei.associateEntity(id, ei.getFreeEntities().getFirst());
			
			while(true) {
				
				// perceive
				LinkedList<Percept> percepts = null;
				percepts = ei.getAllPercepts(id);
				say("I believe the carriage is at " + percepts);

				// act
				ei.performAction(id, new Action("push"));

				// perceive
				percepts = ei.getAllPercepts(id);
				say("I believe the carriage is at " + percepts);

				// act
				ei.performAction(id, new Action("wait"));
		
				try {
					Thread.sleep(950);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		
		} catch (PerceiveException e) {
			e.printStackTrace();
		} catch (RelationException e) {
			e.printStackTrace();
		} catch (ActException e) {
			e.printStackTrace();
		} catch (NoEnvironmentException e) {
			e.printStackTrace();
		}
		
		
	}
	
}

public class Main {

	public static void main(String[] args) {
		
		try {

			// loading the environment
			EnvironmentInterface ei = new EnvironmentInterface();
			Thread.sleep(1000);

			// creating two agents
			Agent ag1 = new PushingAgent(ei, "ag1");
			Agent ag2 = new AlternatingAgent(ei, "ag2");
			
			// registering agents
			ei.registerAgent("ag1");
			ei.registerAgent("ag2");
		
			// starting the agents
			ag1.start();
			ag2.start();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
