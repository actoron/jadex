package jadex.tools.bpmn.editor.properties;


/**
 * 
 */
public class JadexTimerEventPropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"duration"};
	
	// ---- attributes ----


	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexTimerEventPropertySection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_EVENT_TIMER_DETAIL,
				textFieldNames);
	}

	// ---- methods ----
}
