package jadex.tools.bpmn.editor.properties;

/**
 * 
 *
 */
public class JadexMessageEventPropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"mode", "msgtype", "message", "filter"};
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexMessageEventPropertySection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_EVENT_MESSAGE_DETAIL,
				textFieldNames);
	}
}

