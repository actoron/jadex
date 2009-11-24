/**
 * 
 */
package jadex.tools.bpmn.editor.properties;



/**
 * @author Claas Altschaffel
 * 
 */
public class JadexSequencePropertiesSection extends
		AbstractMultiTextfieldPropertySection
{

	// ---- constants ----
	
	private static final String[] textFieldNames = new String[] {"condition"};
	
	// ---- attributes ----


	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexSequencePropertiesSection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_IMPORT_LIST_DETAIL,
				textFieldNames);
	}

	// ---- methods ----

}
