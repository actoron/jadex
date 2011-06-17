package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractMultiCheckboxPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

/**
 * 
 */
public class JadexBpmnDiagramFlagsSection extends AbstractMultiCheckboxPropertySection
{
	// ---- constants ----
	
	protected static final String[] CHECKBOX_NAMES = new String[]{"Suspend", "Master", "Daemon", "Autoshutdown", "Keep alive"};
	protected static final boolean[] CHECKBOX_STATES = new boolean[]{false, false, false, false, false};
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramFlagsSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, CHECKBOX_NAMES, CHECKBOX_STATES);
	}

	/**
	 * @see jadex.editor.bpmn.editor.properties.template.IConfigurationChangedListener#fireConfigurationChanged(java.lang.String, java.lang.String)
	 */
	public void fireConfigurationChanged(String oldConfiguration, String newConfiguration)
	{
		updateSectionValues();
	}
}