package deco4mas.mechanism.v2.tspaces;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;

import java.io.Serializable;
import java.util.Map.Entry;

import com.ibm.tspaces.Field;
import com.ibm.tspaces.Tuple;
import com.ibm.tspaces.TupleSpace;
import com.ibm.tspaces.TupleSpaceException;

import deco.lang.dynamics.mechanism.MechanismConfiguration;
import deco4mas.annotation.agent.AgentCoordinationConfiguration;
import deco4mas.mechanism.CoordinationInformation;

/**
 * The generic client to a TSpaces server.
 * 
 * This client encapsulates the interactions of agents (coordination end-points)
 * with the actual tuple space implementation.
 * 
 * Supported operations: read/write to be called by coordination end-points.
 * 
 * @author Ante Vilenica & Jan Sudeikat
 * 
 */
public class TSpacesClient {

	// -------- constants -----------

	// /** The identifier of the field where (xml) content objects are stored.
	// */
	// public static final String XML_CONTENT_FIELD_IDENTIFIER = "XML-content";
	//	
	/** The identifier of the field where (xml) content objects are stored. */
	public static final String CI_CONTENT_FIELD_IDENTIFIER = "Coordination_Information";

	/** The local host identifier. */
	private static final String LOCALHOST = "localhost";

	/** The default tuple space name. */
	private static final String DEFAULT_TSPACE_NAME = "cooordination_tspace";

	// -------- attributes ----------

	/** The host address. */
	private String host;

	/** The name of the (coordination type) */
	private String space_id;

	/** The handle to a connected tuple space. */
	private TupleSpace ts;

	/** The interdependency configuration. */
	private AgentCoordinationConfiguration coordination_configuration;

	/** The interaction technique/mechanism configuration. */
	private MechanismConfiguration mechanism_configuration;

	/** The query that identifies the *relevant* coordination tuples. */
	private String query = "/coordination_information";

	/** The environment space the tspace is related to. */
	private IEnvironmentSpace envSpace;

	// -------- constructors --------

	// public TSpacesClient() {
	// super();
	// this.host = LOCALHOST;
	// this.space_id = DEFAULT_TSPACE_NAME;
	// }

	public TSpacesClient(IEnvironmentSpace envSpace) {
		super();
		this.host = LOCALHOST;
		this.space_id = DEFAULT_TSPACE_NAME;
		this.envSpace = envSpace;
	}

	// public TSpacesClient(String host, String space_id) {
	// super();
	// this.host = host;
	// this.space_id = space_id;
	// }
	//	
	// public TSpacesClient(MechanismConfiguration mc,
	// AgentCoordinationConfiguration ac) {
	// super();
	// this.host = LOCALHOST;
	// this.space_id = DEFAULT_TSPACE_NAME;
	// this.mechanism_configuration = mc;
	// this.coordination_configuration = ac;
	// }
	//	
	// public TSpacesClient(String host, String space_id, MechanismConfiguration
	// mc, AgentCoordinationConfiguration ac) {
	// super();
	// this.host = host;
	// this.space_id = space_id;
	// this.mechanism_configuration = mc;
	// this.coordination_configuration = ac;
	// }

	// -------- methods -------------

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSpace_id() {
		return space_id;
	}

	public void setSpace_id(String space_id) {
		this.space_id = space_id;
	}

	/**
	 * Fetch a handle to a TupleSpace instance. If the tuple space is not
	 * existing, it will be created.
	 * 
	 * @return the reference to the connected space.
	 */
	public boolean connect() {

		try {
			ts = new TupleSpace(space_id, host);
//			test();
		} catch (TupleSpaceException e) {
			System.err.println("Problem connecting to tuble space: " + space_id + "(name)" + host + "(host)");
			e.printStackTrace();	
		}
		if (ts != null) {
			return true;
		} else
			return false;

	}

	// /**
	// * Fetch a handle to a TupleSpace instance.
	// * If the tuple space is not existing, it will be created.
	// *
	// * @return the reference to the connected space.
	// */
	// public boolean connect(String space_id, String host) {
	//		
	// this.setSpace_id(space_id);
	// this.setHost(host);
	// return this.connect();
	//		
	// }

	/**
	 * Write a tuple to the tuple space (POJO-Based content).
	 * 
	 * @param ci
	 *            The published element
	 */
	public void publish(CoordinationInformation ci) {

		// check publication dependent properties and configure the publishing
		// operations accordingly.:
		Long expire = 0l;

		// if (mechanism_configuration.hasProperty("expiration")) {
		// expire = new Long(mechanism_configuration.getProperty("expiration"));
		// }

		// add communication dependent information to the tuple.
		TupleContent content = new TupleContent(ci);

		// Fill tuple:
		try {
			Field ci_field = new Field(content);
			Tuple t = new Tuple(CI_CONTENT_FIELD_IDENTIFIER, ci_field);

			if (expire > 0) {
				t.setExpire(expire);
			}
			ts.write(t);

		} catch (TupleSpaceException e) {
			System.err.println("The Tuple could not be created / written");
			System.err.println("Publishing coordination information FAILED...");
			e.printStackTrace();
		}

	}

	/**
	 * Write a tuple to the tuple space (POJO-Based content).
	 * 
	 * @param ci
	 *            The published element
	 */
	public void publish(ISpaceObject obj) {

		// check publication dependent properties and configure the publishing
		// operations accordingly.:
		// ....

		// add communication dependent information to the tuple.
		// TupleContent content = new TupleContent(ci);

		// Fill tuple:
		try {
			Field ci_field = new Field(obj.toString());
			Tuple t = new Tuple(CI_CONTENT_FIELD_IDENTIFIER, ci_field);

			// if (expire > 0) {
			// t.setExpire(expire);
			// }
			ts.write(t);

		} catch (TupleSpaceException e) {
			System.err.println("The Tuple could not be created / written");
			System.err.println("Publishing coordination information FAILED...");
			e.printStackTrace();
		}

	}

	// /**
	// * perceive a tuple on the tuple space.
	// * @param my_type
	// *
	// * @return CoordinationInformation
	// */
	// public CoordinationInformation perceive(String type) {
	//
	// // check perception dependent properties and configure the publishing
	// operations accordingly.:
	// String consuming = mechanism_configuration.getProperty("consuming");
	// if (consuming != null) {
	// if (consuming.equalsIgnoreCase("false")) {
	// return preceptionOfCoordinationInformation(type,false);
	// }
	// }
	//		
	// return preceptionOfCoordinationInformation(type,true); // default
	// behavior:
	//
	// }

	// /**
	// * Querying Tuples.
	// * The perceived info is *null* or has been retrieved from the tuple
	// space.
	// *
	// * This method can be used to poll a tuple space.
	// *
	// * @return
	// */
	// public CoordinationInformation preceptionOfCoordinationInformation(String
	// type, boolean consuming) {
	//		
	// CoordinationInformation result = null;
	//
	// try {
	//			
	// Tuple msg;
	//			
	// // create template:
	// Tuple template = new Tuple(CI_CONTENT_FIELD_IDENTIFIER,new
	// Field(TupleContent.class));
	//			
	// // fetch tuples from space:
	// if (consuming) {
	//				
	// msg = ts.take(template);
	// }
	// else {
	// msg = ts.read(template);
	// }
	//			
	// if ( msg != null) {
	//				
	// result = (CoordinationInformation) msg.getField(1).getValue();
	// }
	//		
	// } catch (TupleSpaceException e) {
	// System.err.println("Tuples could not be scanned");
	// System.err.println("Reading coordination information FAILED...");
	// e.printStackTrace();
	// }
	//
	// return result;
	// }
	//	
	// /**
	// * Querying Tuples by XQl queries.
	// *
	// * @param query the XQL query
	// * @return
	// */
	// @SuppressWarnings("unchecked")
	// public CoordinationInformation[]
	// preceiveCoordinationInformationByXQL(String query) {
	//		
	// Tuple resultSet;
	// ArrayList<CoordinationInformation> cis = new
	// ArrayList<CoordinationInformation>();
	//		
	// try {
	//			
	// // fetch tuples from space:
	// resultSet = ts.scan(new XMLQuery(query));
	// cis = new ArrayList<CoordinationInformation>();
	//			
	// if ( resultSet != null) {
	// for( Enumeration e = resultSet.fields(); e.hasMoreElements(); ) { //
	// convert to CoordinationInformation[]
	// Field f = (Field)e.nextElement();
	// Tuple tuple = (Tuple)f.getValue(); // we know this super-tuple has tuples
	// as fields
	// String xml_content = (String) tuple.getField(1).getValue();
	// CoordinationInformation ci = (CoordinationInformation)
	// XmlUtil.retrieveFromXMLContent(CoordinationInfo.class, xml_content);
	// cis.add(ci);
	// }
	// }
	//		
	// } catch (TupleSpaceException e) {
	// System.err.println("Tuples could not be scanned");
	// System.err.println("Reading coordination information FAILED...");
	// e.printStackTrace();
	// } catch (JAXBException e) {
	// System.err.println("Tuple content could not be transformed");
	// System.err.println("Reading coordination information FAILED...");
	// e.printStackTrace();
	// }
	//		
	// // convert result:
	// CoordinationInformation[] result = (CoordinationInformation[])
	// cis.toArray(new CoordinationInformation[cis.size()]);
	// return result;
	// }

	/**
	 * Create Tuple from a CoordinationInformation element. CI information are
	 * stored as key value pairs: key->index n value-> index n+1.
	 * 
	 * @param ci
	 *            CoordinationInformation element
	 * @return tuple element
	 */
	@SuppressWarnings("unused")
	private Tuple createCoordinationTuple(CoordinationInformation ci) {

		Tuple t = new Tuple();

		for (Entry<String, Object> e : ci.getValues().entrySet()) {
			try {

				Field key = new Field(e.getKey());
				t.add(key);
				Field value = new Field((Serializable) e.getValue());
				t.add(value);

			} catch (TupleSpaceException e1) {
				System.err.println("Problem: creating a tuple.");
				e1.printStackTrace();
			}
		}

		return t;

	}

	public AgentCoordinationConfiguration getCoordination_configuration() {
		return coordination_configuration;
	}

	public void setCoordination_configuration(AgentCoordinationConfiguration coordination_configuration) {
		this.coordination_configuration = coordination_configuration;
	}

	public MechanismConfiguration getMechanism_configuration() {
		return mechanism_configuration;
	}

	public void setMechanism_configuration(MechanismConfiguration mechanism_configuration) {
		this.mechanism_configuration = mechanism_configuration;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Register an event listener to the tuple space (for a specific agent).
	 * 
	 * 
	 * @param externalAccess
	 */
	public void registerCallback() {

		CICallback callback = new CICallback(envSpace);

		try {

			// create template:
			Tuple template = new Tuple(CI_CONTENT_FIELD_IDENTIFIER, new Field(TupleContent.class));
			boolean newThread = true; // default is false
			@SuppressWarnings("unused")
			int seqNum = ts.eventRegister(TupleSpace.WRITE, template, callback, newThread);
//			System.out.println("#TSpaceClient# RegisteredCallback");

		} catch (TupleSpaceException e) {
			System.err.println("Problem: registering a tuple-sapce event listener");
			e.printStackTrace();
		}

	}

//	private void test() {
//		Properties prop = System.getProperties();
//		prop.setProperty("java.class.path", getClassPathPriv(prop));
//		System.out.println("java.class.path now = " + getClassPathPriv(prop));
//	}
//
//	private static String getClassPathPriv(Properties prop) {
//		return prop.getProperty("java.class.path", null);
//	}
}