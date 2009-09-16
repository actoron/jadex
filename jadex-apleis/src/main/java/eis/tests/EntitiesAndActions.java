package eis.tests;

import java.util.LinkedList;

import eis.*;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.EntityException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.ActionResult;
import eis.iilang.EnvironmentCommand;
import eis.iilang.Identifier;
import eis.iilang.Percept;

public class EntitiesAndActions extends EnvironmentInterfaceStandard {

	@Override
	public LinkedList<Percept> getAllPerceptsFromEntity(String entity) {
		return null;
	}

	@Override
	public void manageEnvironment(EnvironmentCommand command, String... args) {
	}

	public ActionResult actionmove(String entity, Identifier dir1, Identifier dir2) throws ActException {
				
		if( entity.equals("thermostat") || entity.equals("clock") )
			throw new ActException(entity + " cannot move");

		System.out.println(entity + " moves to " + dir1.getValue() + dir2.getValue() );

		return new ActionResult("success");
		
	}

	public ActionResult actionturn(String entity, Identifier state) throws ActException {
		
		System.out.println(entity + " is turned " + state.getValue() );
		
		return new ActionResult("success");
		
	}

	public static void main(String[] args) throws AgentException, EntityException, RelationException {
		
		// instantiate environment interface
		EntitiesAndActions eis = new EntitiesAndActions();
		
		// register agent -- APL platform side
		eis.registerAgent("agent");
		
		// add the entities -- environment side
		eis.addEntity("robot");
		eis.addEntity("thermostat");
		eis.addEntity("clock");
		
		// associate -- APL platform side
		eis.associateEntity("agent", "robot"); // can be turned on/off and can move
		eis.associateEntity("agent", "thermostat"); // can be turned on/off, movement not supported
		
		LinkedList<Action> actions = new LinkedList<Action>();
		LinkedList<LinkedList<String>> entities = new LinkedList<LinkedList<String>>();
		LinkedList<Boolean> expectedOutcomes = new LinkedList<Boolean>();

		// prepare the tests
		
		Action action = null;
		LinkedList<String> list = null;
		
		// 1. turns on all entities, should succeed
		action = new Action("turn", new Identifier("on") );
		list = new LinkedList<String>( );
		actions.add(action);
		entities.add(list);
		expectedOutcomes.add(true);
		
		// 2. turns off thermostat, should succeed
		action = new Action("turn", new Identifier("off") );
		list = new LinkedList<String>();
		list.add("thermostat");
		actions.add(action);
		entities.add(list);
		expectedOutcomes.add(true);

		// 3. moving all entities, should fail
		action = new Action("move", new Identifier("north"), new Identifier("east") );
		list = new LinkedList<String>( );
		actions.add(action);
		entities.add(list);
		expectedOutcomes.add(false);

		// 4. turn on clock, should fail (not associated)
		action = new Action("turn", new Identifier("on") );
		list = new LinkedList<String>();
		list.add("clock");
		actions.add(action);
		entities.add(list);
		expectedOutcomes.add(false);

		// do tests
		int testNo = 1;
		while( actions.isEmpty() == false ) {
			
			System.out.println("---------------------------------------");
			System.out.println("Test No " + testNo++);
			
			Action a = actions.removeFirst();
			LinkedList<String> e = entities.removeFirst();
			boolean expected = expectedOutcomes.removeFirst();
			
			System.out.println("Action " + a);
			System.out.println("Entities: " + e);
			
			String[] arr = null;
			if(e.size() == 0)
				arr = new String[0];
			else
				arr = e.toArray(new String[0]);
			
			boolean result = false;
			
			try {

				eis.performAction("agent", a, arr);
				System.out.println("Success");
				result = true;
		
			} catch (ActException e1) {
			
				System.out.println("Failed");
				
			} catch (NoEnvironmentException e1) {

				System.out.println("Failed");
			
			}
			
			if( result != expected )
				System.out.println("ERROR! Result not expected.");
			
			System.out.println("---------------------------------------");
			System.out.println("");
			
		}
		
		
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

}
