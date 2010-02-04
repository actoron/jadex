package jadex.tools.bpmn.editor.properties;

/**
 * 
 *
 */
public class JadexErrorEventPropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"exception"};
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexErrorEventPropertySection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_EVENT_ERROR_DETAIL, textFieldNames);
	}
}
