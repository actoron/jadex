package jadex.bdi.interpreter.bpmn.model;


public interface IBpmnTask extends IBpmnState
{

	// ---- attributes -----
	
	public static final String SIMPLE_TEST_TASK 		= "simple test";
	
	public static final String GET_INPUT 				= "get input from user via parameterized GUI";
	public static final String WRITE_CONTEXT 			= "write Context";
	public static final String SHOW_FIX_GUI 			= "show fix GUI";
	public static final String LIST_CONTEXTVARIABLES 	= "list contextvariables";
	public static final String ACTIVATE_GOALS 			= "activate goals";
	public static final String SHOW_TEXT_AT_GUI 		= "show text at GUI";
	
	
	
}
