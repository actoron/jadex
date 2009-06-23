package jadex.bdi.interpreter.bpmn.model.state.task;


import jadex.bdi.interpreter.bpmn.model.IBpmnTask;
import jadex.bdi.interpreter.bpmn.model.ITaskProcessor;
import jadex.bdi.interpreter.bpmn.model.ITaskProperty;
import jadex.bdi.interpreter.bpmn.model.state.AbstractState;
import jadex.bdi.runtime.IBpmnPlanContext;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

public class BasicTask extends AbstractState implements IBpmnTask
{
	
	// ---- constants ------
	
	public static Map classMapping;
	
	static {
		classMapping = new HashMap();
		classMapping.put(SIMPLE_TEST_TASK.toLowerCase(), 		"SimpleTestTaskProcessor.class");
		classMapping.put(WRITE_CONTEXT.toLowerCase(), 			"WriteContextTaskProcessor.class");
		classMapping.put(GET_INPUT.toLowerCase(), 				"GetInputTaskProcessor.class");
		classMapping.put(SHOW_FIX_GUI.toLowerCase(), 			"ShowFixGuiTaskProcessor.class");
		classMapping.put(LIST_CONTEXTVARIABLES.toLowerCase(), 	"ListContextvariablesTaskProcessor.class");
		classMapping.put(ACTIVATE_GOALS.toLowerCase(), 			"ActivateGoalsTaskProcessor.class");
		classMapping.put(SHOW_TEXT_AT_GUI.toLowerCase(), 		"ShowTextAtGuiTaskProcessor.class");
	};

	// ---- attributes -----
	
	/** The class name of the task processor */
	protected String taskProcessorClassName;
	
	/** The loaded task processor */
	protected ITaskProcessor taskProcessor;

	/** The role for this task */
	protected String taskRole;

	/** An text to explain this task */
	protected String taskExplanantionText;
	
	// these properties are NOT runtime values, they where provided during modeling
	// and should not be changed during runtime.
	/** Properties list for this task */
	protected Map taskProperties;
	
	// ---- constructors ----
	
	public BasicTask()
	{
		taskExplanantionText = "";
		taskProperties = new HashMap();
	}

	// ---- overrides ----
	
	public IBpmnPlanContext execute(IBpmnPlanContext body)
	{
		// TODO: implement ability to start sub threads in a task processor?
		
//		setFinished(false);
		
		this.taskProcessor = (ITaskProcessor) loadTaskProcessor(taskProcessorClassName);
		if (taskProcessor != null)
		{
			taskProcessor.execute(body, this);
//			setFinished(true);
		}
		else
		{
			System.err.println("No task implementation for "
					+ taskProcessorClassName + " in Task" + getId());

		}
		
		return body;

	}
	
	// ---- wrapper for miss configured XML tags ----
	
	public void setMsgImplementation(String type)
	{
		System.err.println("WARNING! -- BasicTask#setMsgImplementation(String) used");
		this.setTaskType(type);
	}
	
	// ---- getter / setter -----

	public void setTaskType(String taskType)
	{
		this.taskProcessorClassName = taskType;
	}

	public String getTaskType()
	{
		return this.taskProcessorClassName;
	}
	
	public void setRole(String role)
	{
		taskRole = role;
	}
	
	public String getRole()
	{
		return taskRole;
	}

	public void setExplanantionText(String text)
	{
		this.taskExplanantionText = text;;
	}
	
	public String getExplanantionText()
	{
		return taskExplanantionText.trim();
	}
	
	public void addTaskProperty(Object key, ITaskProperty prop)
	{
		this.taskProperties.put(key, prop);
	}
	
	public void setTaskProperties(Map props)
	{
		this.taskProperties = props;
	}
	
	public Map getTaskProperties()
	{
		return taskProperties;
	}
	
	// ---- helper methods ----
	
	private Object loadTaskProcessor(String taskType)
	{
		String className = null;
		
		assert taskType != null : "No task implementation given";
		
		// HACK to provide backwards compatibility
		if (taskType.endsWith(".class"))
		{
			className = taskType;
		}
		else
		{
			className = (String) classMapping.get(taskType.toLowerCase());
		}

		if (className != null)
		{
			try
			{
				// TO DO: use ClassLoader from LibService?
				Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
				return clazz.newInstance();
			}
			catch (Exception e)
			{
				// TODO: handle exception(s)
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	// ---- self parsing element overrides - remove later ----
	
	//
	// The following methods contain some more than stupid HACKS partial copied
	// from daimler classes to provide backward compatibility with old net files
	// using the daimler parser.
	//
	// PLEASE DON'T HIT ME FOR THIS :-)
	//
	
	private ITaskProperty currentTaskProperty;
	
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	{
		if (qName.equals("attribute"))
		{
			String sAttributeName = attributes.getValue("name");
			if (sAttributeName.equals("MsgImplementation"))
			{
				setTaskType(attributes.getValue("value"));
			}
			else if (sAttributeName.equals("Role"))
			{
				taskRole = attributes.getValue("value");
			}
			else if (sAttributeName.equals("ExplanationText"))
			{
				taskExplanantionText = attributes.getValue("value");
			}
		}
		else if (qName.equals("table"))
		{
			String aName = attributes.getValue("name");
			if (aName.equalsIgnoreCase("TaskProperties"))
			{
				setTableRow(-1);
				// System.out.println("Parsing task properties");
			}

		}

		else if (qName.equals("tr"))
		{
			setTableRow(getTableRow() + 1);
			setTableColumn(-1);
		}

		else if (qName.equals("td"))
		{
			getCharacterBuffer().clear();
			setTableColumn(getTableColumn() + 1);
		}
	}

	public void endElement(String uri, String localName, String qName)
	{
		if (qName.equals("table"))
		{
		}

		else if (qName.equals("td") && getTableColumn() == 0)
		{
//			 if (taskProcessor instanceof GetInputTaskProcessor
//			 || taskProcessor instanceof WriteContextTaskProcessor) {
//			 currentTaskProperty = taskProcessor
//			 .createNewTaskProperty(StringUtils
//			 .removeUmlauts(getTheCharacterBuffer()
//			 .toString()));
//			 }
//			 else
//			{
//				currentTaskProperty = taskProcessor
//						.createNewTaskProperty(getTheCharacterBuffer()
//								.toString());
//			}
			
			// we load the processors at runtime, so create the
			// property here, the first column contains the property name
			currentTaskProperty = 
				new BasicTaskProperty(getCharacterBuffer().toString());

		}

		else if (qName.equals("td") && getTableColumn() > 0)
		{
//			 //put currently read value to the current taskproperty
//			 if (getTheTableColumn() == 1
//			 && (taskProcessor instanceof GetInputTaskProcessor ||
//			 taskProcessor instanceof WriteContextTaskProcessor)) {
//			 currentTaskProperty.set(getTheTableColumn(), StringUtils
//			 .removeUmlauts(getTheCharacterBuffer().toString()));
//			 }
//			 else
//				 currentTaskProperty.set(getTableColumn(),
//					getCharacterBuffer().toString());
			
			// The basic task property can at least STORE this values ...
			// No further semantic information is derived.
			currentTaskProperty.set(getTableColumn(),
					getCharacterBuffer().toString());
		}
		if (qName.equals("tr"))
		{
			// put current taskproperty to list of properties but skip empty
			// lines
			if (currentTaskProperty.getName() != null
					&& currentTaskProperty.getName().trim().length() > 0)
				taskProperties.put(currentTaskProperty.getName(), currentTaskProperty);
		}
	}

	
}
