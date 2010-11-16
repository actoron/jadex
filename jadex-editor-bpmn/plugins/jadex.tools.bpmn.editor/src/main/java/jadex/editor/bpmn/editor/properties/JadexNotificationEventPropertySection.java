package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractMultiTextfieldPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

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
