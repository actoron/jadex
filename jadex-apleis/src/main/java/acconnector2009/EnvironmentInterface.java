package acconnector2009;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import eis.EnvironmentInterfaceStandard;
import eis.exceptions.ActException;
import eis.exceptions.EntityException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.iilang.ActionResult;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.DataContainer;
import eis.iilang.EnvironmentCommand;
import eis.iilang.Percept;


/**
 * Establishes connections to the MASSim-Server.
 * 
 * Entities are single connection to the server.
 * 
 * @author tristanbehrens
 *
 */
public class EnvironmentInterface extends EnvironmentInterfaceStandard implements ConnectionListener, Runnable {

	/** Used to facilitate the EIS-to-environment connection. */
	private HashMap<String,Connection> entitiesToConnections = new HashMap<String,Connection>(); 
	
	/** Store the last percept. */
	private HashMap<String,Percept> entitiesToPercepts = new HashMap<String,Percept>();
	
	/** For checking connections. */
	private boolean running = true;
	
	/**
	 * Contructs an environment.
	 */
	public EnvironmentInterface() {
		
		// add all entities
		// we have 10 cowboys in 2009
		try {
		
			addEntity("connector1");
			addEntity("connector2");
			addEntity("connector3");
			addEntity("connector4");
			addEntity("connector5");
			addEntity("connector6");
			addEntity("connector7");
			addEntity("connector8");
			addEntity("connector9");
			addEntity("connector10");

		} catch (EntityException e) {
			e.printStackTrace();
		}
	
		// start connections checker
		new Thread(this).start();
	
	}
	
	/**
	 * Returns the percepts of an entity. That is the last percept that has been
	 * receved from the massim server.
	 */
	@Override
	public LinkedList<Percept> getAllPerceptsFromEntity(String entity) {

		Percept p = entitiesToPercepts.get(entity);
		
		LinkedList<Percept> ret = new LinkedList<Percept>();
		
		if ( p == null)
			ret.add( new Percept("empty") );
		else 
			ret.add(p);
		
		return ret;
		
	}

	@Override
	public boolean isConnected() {
		
		// no connection, no environment
		if( entitiesToConnections.values().size() == 0 ) 
			return false;

		// if there is an non-connected socket return false
		for(Connection c : entitiesToConnections.values()) {
			
			if( c.isConnected() == false) 
				return false;
			
		}
		
		return true;

	}

	@Override
	public void manageEnvironment(EnvironmentCommand command, String... args)
			throws ManagementException {

		assert false : "Implement!";

	}

	@Override
	public void release() {

		// stop execution
		running = false;

		// TODO take down all connections
		
		assert false : "Implement!";

	}

	/**
	 * Connect to a MASSim-server.
	 * 
	 * @param entity the entity that is going to be associated to the connection.
	 * @param server the server.
	 * @param port the port
	 * @param user the username
	 * @param password the password
	 * @return the result of the action, either connected of failed.
	 * @throws ActException
	 */
	public ActionResult actionconnect(String entity, Identifier server, Numeral port, Identifier user, Identifier password) throws ActException {

		Connection c = this.entitiesToConnections.get(entity);
		
		// there is a connection; close ot
		if( c != null ) {
			
			try {
				c.close();
			} catch (IOException e) {
				throw new ActException("Existing connection could not be closed", e);
			}

			c = null; 
		
		}
		
		assert c == null;
		
		// open socket
		try {

			c = new Connection(this, server.getValue(), ((Long)port.getValue()).intValue());

		} catch (UnknownHostException e) {
	
			throw new ActException("Unknown host " + server.getValue());
		
		} catch (IOException e) {

			e.printStackTrace();
			throw new ActException("IO Error");
		
		}
		
		assert c != null;
				
		// authent
		boolean result = c.authenticate(user.getValue(), password.getValue());
		
		if( result == false ) {
			
			return new ActionResult("failed");
		
		}
		
		// store
		entitiesToConnections.put(entity,c);

		// start thread
		new Thread(c).start();
		
		return new ActionResult("connected");
	
	}

	/**
	 * Movement action.
	 * 
	 * @param entity
	 * @param direction
	 * @return the result of the action
	 * @throws ActException
	 * @throws NoEnvironmentException
	 */
	public ActionResult actionmove(String entity, Identifier direction) throws ActException, NoEnvironmentException {

		Connection c = this.entitiesToConnections.get(entity);
		
		if( c == null )
			throw new NoEnvironmentException("Not connected");
		
		c.act(direction.getValue());
		
		return new ActionResult("moved");

		
	}

	/**
	 * Skip action.
	 * 
	 * @param entity
	 * @return the result of the action
	 * @throws ActException
	 * @throws NoEnvironmentException
	 */
	public ActionResult actionskip(String entity) throws ActException, NoEnvironmentException {

		Connection c = this.entitiesToConnections.get(entity);
		
		if( c == null )
			throw new NoEnvironmentException("Not connected");
		
		c.act("skip");
		
		return new ActionResult("skipped");

		
	}
	
	
	/** 
	 * Handles an incoming message.
	 */
	public void handleMessage(Connection connection, DataContainer container) {

		for( Entry<String,Connection> e : this.entitiesToConnections.entrySet() ) {
			
			// look up entity
			if( connection.equals(e.getValue())) {
				
				String entity = e.getKey();
				
				// store last percept
				entitiesToPercepts.put(entity, DataContainer.toPercept(container));
				
				// notify agents
				if( container instanceof Percept )
					try {
						this.notifyAgentsViaEntity((Percept)container, entity);
					} catch (EnvironmentInterfaceException e1) {
						e1.printStackTrace();
					}
				
			}
			
		}
		
		
		//System.out.println(container);
		
	}

	/**
	 * Check all connections every 4 connections.
	 */
	public void run() {

		while( running ) {

			// check the connections
			for( Entry<String, Connection> entry : this.entitiesToConnections.entrySet() ) {
				
				String e = entry.getKey();
				Connection c = entry.getValue();
				
				if( c.isBound() == false || c.isClosed() == true || c.isConnected() == false || c.isExecuting() == false ) {
					entitiesToConnections.remove(e);
				}
				
			}
			
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			
		}
		
	}
	
}
