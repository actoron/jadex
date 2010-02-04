package jadex.tools.bpmn.editor.properties;


/**
 * 
 */
public class JadexBpmnDiagramResultsSection extends
	Abstract4ColumnTablePropertySection
{

	public static final String label = "Results";
	public static final String[] fields = new String[] {"Name", "Description", "Typename", "Value"};
	public static final int[] columnWeights = new int[] {1,1,1,8};
	public static final String[] defaultListElementAttributeValues = new String[]{"name", "description", "Object", ""};
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramResultsSection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_RESULTS_LIST_DETAIL,
			label, fields, columnWeights, defaultListElementAttributeValues);
	}
}
