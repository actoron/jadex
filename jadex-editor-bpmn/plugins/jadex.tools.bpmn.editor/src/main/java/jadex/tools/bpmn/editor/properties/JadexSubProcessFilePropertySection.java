package jadex.tools.bpmn.editor.properties;


/**
 * 
 */
public class JadexSubProcessFilePropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"file"};
	
	// ---- attributes ----


	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexSubProcessFilePropertySection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_SUBPROCESS_ANNOTATION,
				textFieldNames);
	}

	// ---- methods ----
}
