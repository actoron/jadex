package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.editor.properties.template.AbstractMultiTextfieldPropertySection;
import jadex.tools.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

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
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
				textFieldNames);
	}
}
