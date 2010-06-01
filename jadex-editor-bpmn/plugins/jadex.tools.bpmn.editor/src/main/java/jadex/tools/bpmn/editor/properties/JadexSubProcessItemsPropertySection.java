package jadex.tools.bpmn.editor.properties;


/**
 * 
 */
public class JadexSubProcessItemsPropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"items"};
	
	// ---- attributes ----


	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexSubProcessItemsPropertySection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_SUBPROCESS_ANNOTATION,
				textFieldNames);
	}

	// ---- methods ----
}
