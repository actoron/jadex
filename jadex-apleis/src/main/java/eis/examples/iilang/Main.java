package eis.examples.iilang;

import eis.iilang.*;

/**
 * Shows some examples for the <i>Interface Immediate Language</i>
 * 
 * @author tristanbehrens
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DataContainer dc = null;
		
		// moving to (2,3)
		dc = new Action(
						"moveTo", 
						new Numeral(2), 
						new Numeral(3)
						);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");
		
		// following a path at a given speed
		dc = new Action(
						"followPath", 
						new ParameterList( 
								new Function("pos", new Numeral(1), new Numeral(1)), 
								new Function("pos", new Numeral(2), new Numeral(1)), 
								new Function("pos", new Numeral(2), new Numeral(2)), 
								new Function("pos", new Numeral(3), new Numeral(2)), 
								new Function("pos", new Numeral(4), new Numeral(2)), 
								new Function("pos", new Numeral(4), new Numeral(3)) 
						), 
						new Function("speed", new Numeral(10.0))
				);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");
		
		// perceiving a red rubber-ball
		dc = new Percept(
						"sensors", 
						new ParameterList(
								new Function("red", new Identifier("ball")),
								new Function("rubber", new Identifier("ball"))
						)
					);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");
		
		// perceiving visible entities
		dc = new Percept(
						"entities", 
						new ParameterList(
								new Identifier("entity1"),
								new Identifier("entity2"),
								new Identifier("entity3")
						)
				);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");

		
		// kills the environment 
		dc = new EnvironmentCommand(
				EnvironmentCommand.PAUSE
			);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");

		// init 
		dc = new EnvironmentCommand(
				EnvironmentCommand.INIT,
				new Identifier("/home/groucho/eisexamples/config.txt")
			);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");

		// request time 
		dc = new EnvironmentCommand(
				"request",
				new Identifier("time")
			);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");

		// execution has stopped
		dc = new EnvironmentEvent(
						EnvironmentEvent.PAUSED 
				);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");

		// execution has stopped
		dc = new EnvironmentEvent(
						"environmentTime",
						new Numeral(System.currentTimeMillis())
				);
		
		System.out.println(dc.toProlog() + "\n");
		System.out.println(dc.toXML());
		System.out.println("");

	}

}
