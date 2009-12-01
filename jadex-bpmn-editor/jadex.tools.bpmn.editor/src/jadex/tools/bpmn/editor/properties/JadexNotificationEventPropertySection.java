package jadex.tools.bpmn.editor.properties;

/**
 * 
 *
 */
public class JadexNotificationEventPropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"notifier"};
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexNotificationEventPropertySection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_EVENT_RULE_DETAIL,
				textFieldNames);
	}
}
