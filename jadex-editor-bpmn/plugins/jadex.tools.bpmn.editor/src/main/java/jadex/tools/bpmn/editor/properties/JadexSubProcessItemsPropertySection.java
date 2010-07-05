package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.editor.properties.template.AbstractMultiTextfieldPropertySection;
import jadex.tools.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;


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
