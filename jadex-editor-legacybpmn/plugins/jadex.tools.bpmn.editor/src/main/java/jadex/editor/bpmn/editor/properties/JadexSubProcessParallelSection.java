package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractCheckboxPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

/**
 * 
 */
public class JadexSubProcessParallelSection extends
		AbstractCheckboxPropertySection
{
	public static final String[] items = new String[]{"true", "false"};

	public JadexSubProcessParallelSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, "parallel", "parallel");
	}

	

}
