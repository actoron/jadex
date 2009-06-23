package jadex.bdi.interpreter.bpmn.parser.impl.daimler;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.IBpmnTransition;
import jadex.bdi.interpreter.bpmn.model.SelfParsingElement;
import jadex.bdi.interpreter.bpmn.model.state.AbstractState;
import jadex.bdi.interpreter.bpmn.model.state.EndLinkState;
import jadex.bdi.interpreter.bpmn.model.state.IntermediateErrorState;
import jadex.bdi.interpreter.bpmn.model.state.RoutingPointState;
import jadex.bdi.interpreter.bpmn.model.state.StartLinkState;
import jadex.bdi.interpreter.bpmn.model.state.XORGatewayState;
import jadex.bdi.interpreter.bpmn.model.state.task.BasicTask;
import jadex.bdi.interpreter.bpmn.model.state.task.CompoundTask;
import jadex.bdi.interpreter.bpmn.model.transition.ConditionalFlow;
import jadex.bdi.interpreter.bpmn.model.transition.DataFlow;
import jadex.bdi.interpreter.bpmn.model.transition.SequenceFlow;
import jadex.bdi.interpreter.bpmn.parser.BpmnPlanParseException;

import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * The {@link BpmnHandler1_1} parses the content version 1.1 of 
 * BPMN-Plan description 'net' xml files.<br>
 * It works together with the {@link BpmnHandlerBase} which is 
 * the container for the parsed xml content. 
 *
 * @author cwiech8, claas altschaffel
 * Partial based on class provided by Daimler
 * <p>
 * This file is property of DaimlerCrysler.
 * </p>
 */
public class BpmnHandler1_1 extends DefaultHandler
{

	// ---- attributes -----
	
	/** The HandlerBase content container */
	private BpmnHandlerBase handlerBase;

	// ---- constructors ----
	
	/** Create a BpmnHandler with provided container */
	public BpmnHandler1_1(BpmnHandlerBase base)
	{
		handlerBase = base;
	}

	// ---- DefaultHandler method implementation ----

	/**
	 * Overrides DefaultHandler (To be called by the xmlParser).
	 * <p>
	 * Parses nodes, edges or descriptions. When an other element is found
	 * this element is treated as a {@link SelfParsingElement} and the
	 * {@link SelfParsingElement#startElement(String, String, String, Attributes)}
	 * method is called.
	 * </p>
	 * @param name
	 * @exception SAXException
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		// parse a node (a state)
		if (qName.equals("node")) 
		{
			String nodeType = attributes.getValue("type");
			
			try {
				handlerBase.setCurrentParsedState(BpmnHandler1_1.getNewNodeInstanceForType(nodeType));
				handlerBase.getCurrentParsedElement().setCharacterBuffer(handlerBase.getCharacterBuffer());
			} catch (BpmnPlanParseException e) {
				e.printStackTrace();
				return;
			}
				
			for (int i = 0; i < attributes.getLength(); i++) 
			{
				// set id
				if (attributes.getQName(i).equalsIgnoreCase("id"))
				{
					
					// this currently only validates the user provided id
					// and was done with the grammar 
					
					// TODO: move to some constant string elsewhere or use external validation?
					String utfUmlauts = "\\u00FC\\u00C4\\u00E4\\u00DC\\u00F6\\u00D6\\u00DF";
					String idPattern ="^[a-zA-Z"+utfUmlauts+"][\\w"+utfUmlauts+"]*";
					
			        if (!Pattern.matches(idPattern, attributes.getValue(i)))
			        {
			        	System.err.println("Error parsing id \""
								+ attributes.getValue(i) + "\" in file "
								+ handlerBase.getRootFileURL());
			        	handlerBase.getCurrentParsedState().setId(attributes.getValue(i));
			        }
			        handlerBase.getCurrentParsedState().setId(attributes.getValue(i));

					
				}
				// set label
				else if (attributes.getQName(i).equalsIgnoreCase("label"))
				{
					handlerBase.getCurrentParsedState().setLabel(attributes.getValue(i));
				}
			}
			// set id as label if no or empty  label is set
			if (handlerBase.getCurrentParsedState().getLabel() == null 
					|| handlerBase.getCurrentParsedState().getLabel().trim().length() == 0)
			{
				handlerBase.getCurrentParsedState().setLabel(attributes.getValue("id"));
			}

		}

		else if (qName.equals("edge")) 
		{
			try {
				String edgeType = attributes.getValue("type");
				handlerBase.setCurrentParsedTransition(BpmnHandler1_1.getNewTransitionInstanceForType(edgeType));
				handlerBase.getCurrentParsedElement().setCharacterBuffer(handlerBase.getCharacterBuffer());
				handlerBase.getCurrentParsedTransition().setTargetId(attributes.getValue("head"));
				handlerBase.getCurrentParsedTransition().setSourceId(attributes.getValue("tail"));
			} catch (BpmnPlanParseException e) {
				e.printStackTrace();
				return;
			}
		}

		else if (qName.equals("description")) 
		{
			handlerBase.getCharacterBuffer().clear();
		}
		
		// we don't have a node, edge or description
		// this element is 'self-parsing', call parse method
		else if  (handlerBase.getCurrentParsedElement() != null) 
		{
			handlerBase.getCurrentParsedElement().startElement(uri, localName, qName, attributes);
		}

	}

	/**
	 * Overrides DefaultHandler (To be called by the xmlParser). At the moment only
	 * the description will be filled by this method.
	 *
	 * @param ch
	 *            A Character Array
	 * @param start
	 *            start of content in Array
	 * @param length
	 *            length of content in Array
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		handlerBase.getCharacterBuffer().append(new String(ch, start, length));
	}

	/**
	 * Overrides DefaultHandler (To be called by the xmlParser).
	 * <p>
	 * Parses nodes, edges or descriptions. When an other element is found
	 * this element is treated as a {@link SelfParsingElement} and the
	 * {@link SelfParsingElement#endElement(String, String, String)}
	 * method is called.
	 * </p>
	 * @param name
	 * @exception SAXException
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		
		if (qName.equals("node"))
		{
			handlerBase.getParsedStates().put(
					handlerBase.getCurrentParsedState().getId(), 
					handlerBase.getCurrentParsedState());
			if (handlerBase.getCurrentParsedState() instanceof StartLinkState) 
			{
				handlerBase.setStartStateId(handlerBase.getCurrentParsedState().getId());
			}
			handlerBase.setCurrentParsedState(null);
		}
		else if (qName.equals("edge"))
		{
			handlerBase.getParsedTransitions().add(handlerBase.getCurrentParsedTransition());
			handlerBase.setCurrentParsedTransition(null);
		}
		if (qName.equals("description")) 
		{
			if (handlerBase.getCurrentParsedState() != null)
			{
				handlerBase.getCurrentParsedState().setDescription(handlerBase.getCharacterBuffer().toString());
			}
			else if (handlerBase.getCurrentParsedTransition() != null)
			{
				handlerBase.getCurrentParsedTransition().setDescription(handlerBase.getCharacterBuffer().toString());
			}
		}
		// we don't have a node, edge or description
		// this element is 'self-parsing', call parse method
		else if (handlerBase.getCurrentParsedElement() != null) 
		{
			handlerBase.getCurrentParsedElement().endElement(uri, localName, qName);
		}

	}

	/**
	 * End parsing, only calls super.
	 * @see DefaultHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	/**
	 * Create a transition instance 
	 * @param transitionType, a transition type from {@link IBpmnTransition}
	 * @return {@link IBpmnTransition} instance for type
	 * @throws BpmnPlanParseException if the transition type is unknown
	 */
	public static IBpmnTransition getNewTransitionInstanceForType(String transitionType) throws BpmnPlanParseException
	{
		
		IBpmnTransition newTransition = null;
		
		if (transitionType.equalsIgnoreCase(IBpmnTransition.SEQUENCE_FLOW))
		{
			newTransition = new SequenceFlow();
		}
		else if (transitionType.equalsIgnoreCase(IBpmnTransition.DATA_FLOW))
		{
			newTransition = new DataFlow();
		}
		else if (transitionType.equalsIgnoreCase(IBpmnTransition.CONDITIONAL_FLOW))
		{
			newTransition = new ConditionalFlow();
		}
		else {
			throw new BpmnPlanParseException("transition type currently not supported: " + transitionType);
		}
		
		return newTransition;
		
	}

	/**
	 * Create a node instance 
	 * @param nodeType, a state type from {@link IBpmnState}
	 * @return {@link IBpmnState} node instance for type
	 * @throws BpmnPlanParseException if the node type is unknown 
	 */
	public static IBpmnState getNewNodeInstanceForType(String nodeType) throws BpmnPlanParseException
		{
			AbstractState newState = null;
			
			if (nodeType.equalsIgnoreCase(IBpmnState.STARTLINK))
			{
				newState =  new StartLinkState();
			}
			else if (nodeType.equalsIgnoreCase(IBpmnState.ENDLINK))
			{
				newState =  new EndLinkState();
			}
			else if (nodeType.equalsIgnoreCase(IBpmnState.TASK))
			{
				newState =  new BasicTask();
			}
			else if (nodeType.equalsIgnoreCase(IBpmnState.ROUTING_POINT))
			{
				newState =  new RoutingPointState();
			}
			else if (nodeType.equalsIgnoreCase(IBpmnState.XOR_GATEWAY))
			{
				newState = new XORGatewayState();
			}
			else if (nodeType.equalsIgnoreCase(IBpmnState.INTERMEDIATE_ERROR))
			{
				newState =  new IntermediateErrorState();
			}
			else if (nodeType.equalsIgnoreCase(IBpmnState.COMPOUND_TASK))
			{
				newState =  new CompoundTask();
			}
	//		if (nodeType.equalsIgnoreCase(IBpmnState.LOCAL_CONTEXT))
	//		{
	//			return new LocalContextNode(plan);
	//		}
	//		if (nodeType.equalsIgnoreCase(IBpmnState.CONTEXT))
	//		{
	//			return new LocalContextNode(plan);
	//		}
			else {
				throw new BpmnPlanParseException("nodeType currently not supported: " + nodeType);
			}
			
			return newState;
		}

}
