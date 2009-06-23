package jadex.bdi.interpreter.bpmn.parser.impl.daimler;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.IBpmnEndState;
import jadex.bdi.interpreter.bpmn.model.IBpmnTransition;
import jadex.bdi.interpreter.bpmn.model.ParsedStateMachine;
import jadex.bdi.interpreter.bpmn.model.SelfParsingElement;
import jadex.bdi.interpreter.bpmn.model.state.RoutingPointState;
import jadex.bdi.interpreter.bpmn.model.state.XORGatewayState;
import jadex.bdi.interpreter.bpmn.model.state.task.CompoundTask;
import jadex.bdi.interpreter.bpmn.model.transition.ConditionalFlow;
import jadex.bdi.interpreter.bpmn.model.transition.SequenceFlow;
import jadex.bdi.interpreter.bpmn.model.transition.AbstractStateTransition;
import jadex.bdi.interpreter.bpmn.parser.BpmnParserException;
import jadex.bdi.interpreter.bpmn.parser.BpmnPlanParseException;
import jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml.AEM_XmlHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.helpers.DefaultHandler;


/**
 * The {@link BpmnHandlerBase} is a convenience class to ease the evolution of
 * BPMN-Plan-XML. It stores the information contained in the BPMN-Plan-XML.
 * E.g.: The states and transitions, the id for the "start-state" and the 
 * ability to build the state machine with this information.
 * 
 * The {@link BpmnHandlerBase} instantiate a second handler class to parse the XML
 * content. The instantiated class is based on the version of the BpmnPlan.<br>
 * Currently only the version 1.1 ({@link BpmnHandler1_1}) is supported.
 * 
 * @author cwiech8, claas altschaffel
 * Partial based on class provided by Daimler
 * 
 * <p>
 * This file is property of DaimlerCrysler.
 * </p>
 */
public class BpmnHandlerBase extends AEM_XmlHandler
{
	
	// ---- attributes ----
	
	/** The handler for a BpmnPlan */
	private DefaultHandler bpmnHandler;

	/** The map for the states (string-id -> impl) */
	private HashMap parsedStates;

	/** The list of transitions between states */
	private ArrayList parsedTransitions;

	/** The id of the start state */
	private String startStateId;

	/** The current state to parse */
	private IBpmnState currentParsedState;

	/** The current transition to parse */
	private IBpmnTransition currentParsedTransition;

	// this is only needed to:
	// - transform relative URLs for compound tasks (never used)
	// - error messages
	/** root URL of the BpmnPlan XML file */
	private URL xmlFileRootURL;
	


	// ---- constructor ----
	
	/**
	 * Creates a default {@link BpmnHandlerBase}
	 */
	public BpmnHandlerBase()
	{
		super();
		parsedStates = new HashMap();
		parsedTransitions = new ArrayList();
	}

	
	// ---- methods ----
	
	/**
	 * Initialize this BpmnHandlerBase with a Handler dependent
	 * on the BPMN netVersion.
	 * <p>
	 * This method returns always a {@link BpmnHandler1_1}, yet.
	 * 
	 * @param netVersion to create Handler for
	 * @return BpmnHandler to parse a BpmnPlan XML
	 * @throws BpmnParserException if no parser for net version is available
	 */
	public DefaultHandler initializeHandler(String netVersion) throws BpmnParserException
	{
		// TODO: Use IBpmnHandler interface instead of DefaultHandler / 
		// BpmnHandler1_1 combination and classloader to load the handler.
		
		if (netVersion.equals("1.1"))
		{
			bpmnHandler = new BpmnHandler1_1(this);
			return bpmnHandler;
		}
		else if (netVersion.equals("2.0"))
		{
			bpmnHandler = new BpmnHandler1_1(this);
			return bpmnHandler;
		}
		else 
		{
			throw new BpmnParserException("No parser for net version:" + netVersion);
		}

	}
	
	/**
	 * Access the BpmnHandler
	 * @return the instantiated BpmnHandler
	 */
	public DefaultHandler getBpmnHandler() 
	{
		return bpmnHandler;
	}

	/**
	 * Set the BpmnHandler to use for parsing
	 * @param handler
	 */
	public void setBpmnHandler(DefaultHandler handler) 
	{
		this.bpmnHandler = handler;
	}
	
	/**
	 * @throws BpmnPlanParseException
	 */
	public void buildStateMachine() throws BpmnPlanParseException
	{
		//System.out.println("Parsed " + theStates.size() + " states and " + theTransitions.size() + " transitions!");
		IBpmnTransition currentTransition;
		IBpmnState sourceState;
		IBpmnState targetState;
		
		ArrayList stateKeysToDelete = new ArrayList();
		
		// iterate over all transitions 
		for (int i = 0; i < parsedTransitions.size(); i++)
		{
			currentTransition = (IBpmnTransition) parsedTransitions.get(i);
			targetState = (IBpmnState) parsedStates.get(currentTransition.getTargetId());
			sourceState = (IBpmnState) parsedStates.get(currentTransition.getSourceId());
			// validate parsed transition
			if (targetState == null || sourceState == null)
			{
				if (sourceState != null)
				{
					throw new BpmnPlanParseException("Transition with unknown head found (Tail is " + sourceState.getId() + ")");
				}
				else if (targetState != null)
				{
					throw new BpmnPlanParseException("Transition with unknown tail found (Head is " + targetState.getId() + ")");
				}
				else
				{
					throw new BpmnPlanParseException("Transition with unknown head and tail found");
				}
			}
			
			// set outgoing / incoming transition in states
			currentTransition.setSourceState(sourceState);
			currentTransition.setTargetState(targetState);
			sourceState.addOutgoingEdge(currentTransition);
			targetState.addIncommingEdge(currentTransition);
			
			// TO DO: (c. wiech) check configuration here whether to transform the whole state Machine already
			// at this point or later during execution
			
			// check target for compound task and its sub-states
			if (targetState instanceof CompoundTask) 
			{
				IBpmnState compundTargetState = targetState;
				ParsedStateMachine psm = ((CompoundTask) targetState).getTheParsedSubStateMachine();
				Iterator stateIterator = psm.getStateMap().entrySet().iterator();
				
				// add all sub-states to parsed states
				while (stateIterator.hasNext()) 
				{
					Entry stateEntry = (Entry) stateIterator.next();
					if (parsedStates.containsKey(stateEntry.getKey()))
					{
						// check object reference, this have to be the same state!
						IBpmnState parsedState = (IBpmnState) parsedStates.get(stateEntry.getKey());
						if (parsedState != null && parsedState != stateEntry.getValue()) 
						{
							//System.err.println("\nERROR: Already defined state with ID '" + stateEntry.getKey() + "' found in compund task '" + targetState.getTheID() + "' in Plan '" + getRootFileURL() + "'!!!\n");
							throw new BpmnPlanParseException("Already defined state with ID '" + stateEntry.getKey() + "' found in compund task '" + targetState.getId() + "' in Plan '" + getRootFileURL() + "'");
						}
					}
					parsedStates.put(stateEntry.getKey(), stateEntry.getValue());
				}
				
				// replace the transition target with sub-start-state from compound task
				stateKeysToDelete.add(compundTargetState.getId());
				targetState = (IBpmnState) parsedStates.get(psm.getStartStateId());
				currentTransition.setTargetState(targetState);
			}

			// check source for compound task and its sub-states
			if (sourceState instanceof CompoundTask) 
			{
				ParsedStateMachine psm = ((CompoundTask) sourceState).getTheParsedSubStateMachine();
				Iterator stateIterator = psm.getStateMap().entrySet().iterator();
				
				// TODO: remove? it's already done in the targetState if statement
				// add all sub-states to parsed states
				while (stateIterator.hasNext()) 
				{
					Entry stateEntry = (Entry) stateIterator.next();
					IBpmnState parsedState = (IBpmnState) parsedStates.get(stateEntry.getKey());
					
					// check object reference, this have to be the same state!
					if (parsedState != null && parsedState != stateEntry.getValue()) 
					{
						//System.err.println("\nERROR: Already defined state with ID '" + stateEntry.getKey() + "' found in compund task '" + sourceState.getTheID() + "' in Plan '" + getRootFileURL() + "'!!!\n");
						throw new BpmnPlanParseException("Already defined state with ID '" + stateEntry.getKey() + "' found in compund task '" + sourceState.getId() + "' in Plan '" + getRootFileURL() + "'");
					}
					parsedStates.put(stateEntry.getKey(), stateEntry.getValue());
				}

				// update all sub-end-states with the source state as successor
				String[] ends = psm.getEndNodeIDs();
				for (int j = 0; j < ends.length; j++) 
				{
					IBpmnEndState subEndState = (IBpmnEndState) parsedStates.get(ends[j]);
					subEndState.setEndOfSubProcess(true);
					IBpmnTransition newTransition = new SequenceFlow();
					newTransition.setSourceState(subEndState);
					newTransition.setTargetState(targetState);
					subEndState.addOutgoingEdge(newTransition);
				}
			}
			
//			// check and set XOR Gateway conditions from conditional flows
//			if (sourceState instanceof XORGatewayState && currentTransition instanceof ConditionalFlow)
//			{
//				String condition = ((ConditionalFlow) currentTransition).getCondition();
//				if (condition != null)
//					((XORGatewayState) sourceState).addSuccessor(condition, targetState);
//				else
//				{
//					System.err.println("WARNING: No condition specified for conditional flow from "
//							+ sourceState.getId() + " to " + targetState.getId()
//							+ "!\n Replacing it with alsway-true-condition \";\"");
//					
//					((XORGatewayState) sourceState).addSuccessor("true", targetState);
//				}
//			}
			
		}
		
		// TO DO: maybe call this in a loop until no routing point is removed?
		// delete routing points to reduce overhead
		// TODO: this procedure can be improved ...
		Iterator stateIterator = parsedStates.values().iterator();
		while (stateIterator.hasNext())
		{
			IBpmnState rpState = (IBpmnState) stateIterator.next();
			if (rpState instanceof RoutingPointState 
					&& !stateKeysToDelete.contains(rpState.getId()))
			{
				// skip / remove routing point
				stateKeysToDelete.add(rpState.getId());
				
				// - remove incoming edges from their source  
				// - remove outgoing edges from their source    
				// - add a new edge between each source/target combination
				IBpmnTransition[] rpIncommingEdges = (IBpmnTransition[]) rpState.getIncommingEdges().toArray(new IBpmnTransition[rpState.getIncommingEdges().size()]);
				IBpmnTransition[] rpOutgoingEdges 	= (IBpmnTransition[]) rpState.getOutgoingEdges().toArray(new IBpmnTransition[rpState.getOutgoingEdges().size()]);
				for (int in = 0; in < rpIncommingEdges.length; in++)
				{
					for (int out = 0; out < rpOutgoingEdges.length; out++)
					{
						// remove edges
						rpIncommingEdges[in].getSourceState().removeIncommingEdge(rpIncommingEdges[in]);
						rpOutgoingEdges[out].getTargetState().removeIncommingEdge(rpOutgoingEdges[out]);
						
						// create a new edge for each incommping / outgoing combination
						// TODO: respect flow class!
						IBpmnTransition introducedTransition = new SequenceFlow();
						introducedTransition.setSourceState(rpIncommingEdges[in].getSourceState());
						introducedTransition.setTargetState(rpOutgoingEdges[out].getTargetState());
						parsedTransitions.add(introducedTransition);
					}
				}

				// remove all transitions that contains the routing point 
				// as source or target
				for (int j = 0; j < parsedTransitions.size(); j++ )
				{
					currentTransition = (IBpmnTransition) parsedTransitions.get(j);
					if (currentTransition.getSourceId().equals(rpState.getId()) ||
							currentTransition.getTargetId().equals(rpState.getId()))
					{
						parsedTransitions.remove(j);
						j--;
					}
				}

			}
		}
		
		
//		String successorId;
//		Iterator stateIds = parsedStates.keySet().iterator();
//		while (stateIds.hasNext())
//		{
//			sourceState = (IBpmnState) parsedStates.get(stateIds.next());
//			// iterate over all successors
//			for (int i = 0; i < sourceState.getSuccessorIds().size(); i++)
//			{
//				successorId = (String) sourceState.getSuccessorIds().get(i);
//				targetState = (IBpmnState) parsedStates.get(successorId);
//				if (targetState instanceof RoutingPointState 
//						&& !keysToDelete.contains(targetState.getId()))
//				{
//					// skip / remove routing point
//					sourceState.getSuccessorIds().set(i, targetState.getNextStateId());
//					keysToDelete.add(targetState.getId());
//					// iterate over transitions
//					for (int j = 0; j < parsedTransitions.size(); j++ )
//					{
//						currentTransition = (IBpmnTransition) parsedTransitions.get(j);
//						// remove transitions from current source to removed routing point
//						if (currentTransition.getSourceId().equals(sourceState.getId())
//								&& currentTransition.getTargetId().equals(targetState.getId())
//								&& currentTransition instanceof SequenceFlow)
//						{
//							parsedTransitions.remove(j);
//							j--;
//						}
//						// remove reflexive transitions of removed routing point
//						if (currentTransition.getSourceId().equals(targetState.getId())
//								&& currentTransition.getTargetId().equals(targetState.getNextStateId())
//								&& currentTransition instanceof SequenceFlow)
//						{
//							parsedTransitions.remove(j);
//							j--;
//						}
//					}
//					
//					// add new sequence transition
//					SequenceFlow sf = new SequenceFlow();
//					sf.setSourceId(sourceState.getId());
//					sf.setTargetId(targetState.getNextStateId());
//					parsedTransitions.add(sf);
//					i--;
//				}
//			}
//		}
		
		// remove not needed states
		for (int i = 0; i < stateKeysToDelete.size(); i++)
		{
			parsedStates.remove(stateKeysToDelete.get(i));
		}


	}

	/**
	 * Get a Map of all parsed states
	 * @return Map of parsed states
	 */
	public Map getParsedStates() 
	{
		return parsedStates;
	}
	
	/**
	 * Get a list of all parsed transitions
	 * @return List of parsed transitions
	 */
	public List getParsedTransitions() 
	{
		return parsedTransitions;
	}
	
	/**
	 * Returns the current parsed element, e.g. a Subclass of {@link SelfParsingElement}.
	 * @return {@link IBpmnState} or {@link AbstractStateTransition}, dependent of what element was parsed
	 */
	public SelfParsingElement getCurrentParsedElement()
	{
		if (currentParsedState != null) 
		{
			return (SelfParsingElement) currentParsedState;
		}
		if (currentParsedTransition != null) 
		{
			return (SelfParsingElement) currentParsedTransition;
		}
		
		return null;
	}

	// ---- getter / setter ----
	
	/**
	 * Get the current parsed state
	 * @return the current parsed {@link IBpmnState}
	 */
	public IBpmnState getCurrentParsedState() 
	{
		return currentParsedState;
	}

	/**
	 * Set the current parsed {@link IBpmnState}
	 * @param currentState parsed (to parse?)
	 */
	public void setCurrentParsedState(IBpmnState currentState) 
	{
		this.currentParsedState = currentState;
	}

	/**
	 * Get the current parsed state transition
	 * @return the current parsed {@link AbstractStateTransition}
	 */
	public IBpmnTransition getCurrentParsedTransition() 
	{
		return currentParsedTransition;
	}

	/**
	 * Set the current parsed {@link AbstractStateTransition}
	 * @param currentTransition parsed (to parse?)
	 */
	public void setCurrentParsedTransition(IBpmnTransition currentTransition) 
	{
		this.currentParsedTransition = currentTransition;
	}

	/**
	 * Get the start state (node) id for parsed BPMN plan
	 * @return the id of the parsed start node
	 */
	public String getStartStateId() 
	{
		return startStateId;
	}

	/**
	 * Set the start state id for parsed BPMN plan
	 * @param stateId to set as start state (node)
	 */
	public void setStartStateId(String stateId) 
	{
		this.startStateId = stateId;
	}

	/**
	 * Get the root URL of parsed BPMN plan XML file
	 * @return the URL for parsed file
	 */
	public URL getRootFileURL() 
	{
		return xmlFileRootURL;
	}

	/**
	 * Set the root URL of parsed BPMN plan XML file
	 * @param rootURL
	 */
	public void setRootFileURL(URL rootURL) 
	{
		this.xmlFileRootURL = rootURL;
	}

}
