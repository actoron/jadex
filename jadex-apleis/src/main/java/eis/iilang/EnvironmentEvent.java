package eis.iilang;

/**
 * Environments are sent by the environment-interface to notify about certain changes.
 * 
 * @author tristanbehrens
 *
 */
public class EnvironmentEvent extends DataContainer {

	/** the environment has been started */
	public static int STARTED = 1;
	
	/** the environment has been killed */
	public static int KILLED = 2;
	
	/** the environment has been paused */
	public static int PAUSED = 3;
	
	/** the environment has been reset */
	public static int RESET = 4;
	
	/** the environment has been initialized */
	public static int INITED = 5; 
	
	/** an event that is not defined yet uses name and parameters. */
	public static int MISC = 6;

	/** the type of the event */
	private int type = 0;
	
	/**
	 * Constructs an environment-event that is not of type MISC.
	 * 
	 * @param name the name of the event.
	 * @param parameters the event's parameters.
	 */
	public EnvironmentEvent(int type, Parameter...parameters) {

		super(); // no name, no params
		
		this.type = type;
		
		// do not get fooled by the caller
		if( this.type < 1 || this.type > 6)
			this.type = 0;
	
	}

	/**
	 * Constructs an environment-event that is of type MISC.
	 * 
	 * @param name
	 * @param params
	 */
	public EnvironmentEvent(String name, Parameter...params) {
		
		super(name,params);
		
		this.type = MISC;
		
	}

	@Override
	protected String toXML(int depth) {

		String xml = "";
		
		if( type != MISC) {
			
			// the name 
			xml += indent(depth) + "<environmentEvent ";

			// the type
			xml += "type=\"";
			if( type == STARTED)
				xml += "started";
			else if( type == KILLED)
				xml += "killed";
			else if( type == PAUSED)
				xml += "paused";
			else if( type == RESET)
				xml += "reset";
			else if( type == INITED)
				xml += "inited";
			xml += "\">" + "\n";

			xml += indent(depth) + "</environmentEvent>" + "\n";
	
		}
		else {
		
			xml += indent(depth) + "<environmentEvent name=\"" + name + "\" type=\"misc\">" + "\n";
			
			for( Parameter p : params ) {
				
				xml += indent(depth+1) + "<environmentEventParameter>" + "\n";
				xml += p.toXML(depth+2);
				xml += indent(depth+1) + "</environmentEventParameter>" + "\n";
				
			}

			xml += indent(depth) + "</environmentEvent>" + "\n";
			
		}
		
		return xml;

	}

	@Override
	public String toProlog() {
		
		String ret = "environmentevent";
		
		ret+="(";
		
		if( type == STARTED)
			ret += "started";
		else if( type == KILLED)
			ret += "killed";
		else if( type == PAUSED)
			ret += "paused";
		else if( type == RESET)
			ret += "reset";
		else if( type == INITED)
			ret += "inited";
		else if( type == MISC) {
			
			ret += "misc";
			ret+= "," + name;
		
		}
		
		for( Parameter p : params ) 
			ret += "," + p.toProlog();
		
		ret+=")";
		
		return ret;

		// TODO TEST
		
	}
	
	public int getType() {
		
		return type;
	
	}
	
}
