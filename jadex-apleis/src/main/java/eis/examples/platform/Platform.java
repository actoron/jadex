package eis.examples.platform;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import eis.EnvironmentListener;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.AgentException;
import eis.exceptions.RelationException;
import eis.iilang.Percept;
import eis.iilang.EnvironmentEvent;

public class Platform implements EnvironmentListener {

	private EnvironmentInterfaceStandard ei = null;
	
	public Platform() {}
	
	private void loadMAS(String jarFileName, LinkedList<String> params) {

		System.out.println("Loading MAS");
		System.out.println("  Jar-file: " + jarFileName);
		
		// 1. loading environment interface
		try {

			ei = EnvironmentInterfaceStandard.fromJarFile(new File(jarFileName));
		
			System.out.println("Environment interface loaded.");
			
		} catch (IOException e) {
		
			System.out.println("Jar-file could not be loaded.");
			
			System.exit(0);

		}
		
		// 2. registering listener
		ei.attachEnvironmentListener(this);
		System.out.println("Attached to environment interface.");
		
		// 3. registering agents and associating
		while( params.isEmpty() == false ) {
			
			String agent = params.removeFirst();
			String entity = params.removeFirst();
			
			try {

				ei.registerAgent(agent);
				System.out.println("Added agent " + agent);

			} catch (AgentException e) {

				System.out.println("Agent " + agent + " could not be added.");
			
			}

			try {
				
				ei.associateEntity(agent, entity);

				System.out.println("Associated " + agent + " with " + entity);

			} catch (RelationException e) {

				System.out.println("Failed to associate " + agent + " with " + entity);

			}
			
		}
		
		// 5. showing entities
		System.out.println("Free entities: " + ei.getFreeEntities());
		
	}	

	
	public static void main(String[] args) {
		
		if(args.length == 0) {
			
			System.out.println("Parameters: jarfile (agentname entityname)");
			System.out.println("Example: java eis.examples.platform.Platform carriageexample.jar agent1 carriage1 agent2 carriage2");
		
			System.exit(0);
			
		}

		Platform platform = new Platform();

		String jarFileName = args[0];
		LinkedList<String> params = new LinkedList<String>();

		for( int a = 1 ; a < args.length ; a++ ) {
			
			params.add(args[a]);
			
		}
		
		if( params.size() % 2 != 0 ) {
			
			System.out.println("Wrong number of parameters.");
			System.exit(0);
			
		}
			
		platform.loadMAS(jarFileName, params);
		
	}

	public void handlePercept(String agent, Percept percept) {

		System.out.println(agent + " received this percept " + "\n" + percept);
		
	}

	public void handleDeletedEntity(String entity) {
		
	}

	public void handleEnvironmentEvent(EnvironmentEvent event) {
		
	}

	public void handleFreeEntity(String entity) {
		
		System.out.println(entity + " is free");
		
	}

	public void handleNewEntity(String entity) {
		
	}

	
}
