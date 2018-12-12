package jadex.bdi.examples.blackjack;

/**
 *  Generated Java class for ontology blackjack_beans.
 */
public class BlackjackOntology
{
	//-------- constants --------

	/** The name of the ontology. */
	public static final String	ONTOLOGY_NAME	= "blackjack_beans";

	/** The allowed java classes. */
	public static java.util.HashSet java_classes = new java.util.HashSet();

	//-------- static part --------

	static
	{
		String[] sp = jadex.commons.beans.Introspector.getBeanInfoSearchPath();
		String[] nsp = new String[sp.length+1];
		System.arraycopy(sp, 0, nsp, 0, sp.length);
		nsp[nsp.length-1] = "jadex.examples.blackjack";
		// Use try/catch for applets / webstart, etc.
		try
		{
			jadex.commons.beans.Introspector.setBeanInfoSearchPath(nsp);
		}
		catch(SecurityException e)
		{
			System.out.println("Warning: Cannot set BeanInfo search path 'jadex.examples.blackjack'.");
		}
	}


}

