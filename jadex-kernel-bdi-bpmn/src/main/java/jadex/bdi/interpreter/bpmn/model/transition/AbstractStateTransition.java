package jadex.bdi.interpreter.bpmn.model.transition;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.IBpmnTransition;
import jadex.bdi.interpreter.bpmn.model.SelfParsingElement;


public abstract class AbstractStateTransition extends SelfParsingElement implements IBpmnTransition
{
	/** The id of this transition */
	protected String id;
	
	/** The source state of this transition,
	 * the string id is needed during parsing*/
	protected String sourceStateId;
	
	/** The target state of this transition, 
	 * the string id is needed during parsing */
	protected String targetStateId;
	
	/** The source state of this transition */
	protected IBpmnState sourceState;
	
	/** The source state of this transition */
	protected IBpmnState targetState;
	
	/** The label of this transition */
	protected String label;
	
	/** The description of this transition */
	protected String description;

	// ----- constructors ------
	
	public AbstractStateTransition()
	{
		targetStateId = null;
		sourceStateId = null;
	}
	
	// ----- getter / setter -------

	/**
	 * @return the sourceState
	 */
	public IBpmnState getSourceState()
	{
		return sourceState;
	}

	/**
	 * @param sourceState the sourceState to set
	 */
	public void setSourceState(IBpmnState sourceState)
	{
		this.sourceState = sourceState;
		this.sourceStateId = sourceState.getId();
	}

	/**
	 * @return the targetState
	 */
	public IBpmnState getTargetState()
	{
		return targetState;
	}

	/**
	 * @param targetState the targetState to set
	 */
	public void setTargetState(IBpmnState targetState)
	{
		this.targetState = targetState;
		this.targetStateId = targetState.getId();
	}
	
	/**
	 * @return the id
	 */
	public String getId()
	{
		// HACK!! currently no id is provided by the parser
		if (id == null)
			id = this.toString();
		
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#getTargetId()
	 */
	public String getTargetId() 
	{
		return targetStateId;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#setTargetId(java.lang.String)
	 */
	public void setTargetId(String targetId) 
	{
		if (this.sourceState != null)
			throw new RuntimeException("Method should only be used by the parser without targetState already set.");
		this.targetStateId = targetId;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#getSourceId()
	 */
	public String getSourceId() 
	{
		return sourceStateId;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#setSourceId(java.lang.String)
	 */
	public void setSourceId(String sourceId) 
	{
		if (this.sourceState != null)
			throw new RuntimeException("Method should only be used by the parser without sourceState already set.");
		this.sourceStateId = sourceId;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#setLabel(java.lang.String)
	 */
	public void setLabel(String label) 
	{
		this.label = label;
	}
	
	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#getDescription()
	 */
	public String getDescription() 
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.interpreter.bpmn.model.transition.IBpmnTransition#setDescription(java.lang.String)
	 */
	public void setDescription(String description) 
	{
		this.description = description;
	}
	
	
}
