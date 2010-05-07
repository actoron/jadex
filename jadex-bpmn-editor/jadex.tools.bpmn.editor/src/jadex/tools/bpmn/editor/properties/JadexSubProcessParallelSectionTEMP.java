package jadex.tools.bpmn.editor.properties;

public class JadexSubProcessParallelSectionTEMP extends
		AbstractComboPropertySection
{
	public static final String[] items = new String[]{"true", "false"};

	public JadexSubProcessParallelSectionTEMP()
	{
		super(JadexBpmnPropertiesUtil.JADEX_SUBPROCESS_ANNOTATION, "parallel", "Parallel");
	}

	@Override
	protected String[] getComboItems()
	{
		return items;
	}

}
