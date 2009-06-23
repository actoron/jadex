package jadex.bdi.interpreter.bpmn.model.state;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.runtime.IBpmnPlanContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;


public class XORGatewayState extends AbstractState {

	// ---- attributes ----
	
	/** A map of "String in Jadex-OQL conditions" -> "IBpmnState" */
    protected Map conditionalSuccessors;
    
    
//    // TODO: move to execution-task to provide model view of plan
//    /** The selected successorId after execute */
//    protected String successorId;
//    
//    // TODO: move to Abstract state and provide functionality for all states
//    /** The selected successor after execute */
//    protected IBpmnState successor;

    // ---- constructors ----
    
    public XORGatewayState() 
    {
    	this.conditionalSuccessors = new HashMap();
//    	this.successor = null;
    }

    // ---- methods ----
    
    /** Add a conditional successor */
    public void addSuccessor(String condition, IBpmnState successor)
	{
		this.conditionalSuccessors.put(condition, successor);
	}
    
    // ---- overrides ----
    
//    /**
//     * Get the id of the XOR Successor
//     * @return id of the successor if finished, else null
//     */
//    public String getNextStateId() 
//    {
//    	return successorId;    	
//    }

    /** 
     * This method throw a Runtime Exception if its called.
     * @throws RuntimeException - ALWAYS!!
     */
    public void addOutgoingEdge(IBpmnState successor)
	{
		throw new RuntimeException("Method not supported by "+this.getClass().getName());
	}
	
    /**
     * This method returns an unsorted List of all conditional successors for this XOR Gateway.
     * The List is backed by the condition map values - be careful.
     */
	public List getOutgoingEdges()
	{
		ArrayList ret = new ArrayList();
		Iterator successorStates = conditionalSuccessors.values().iterator();
		while (successorStates.hasNext())
		{
			IBpmnState state = (IBpmnState) successorStates.next();
			ret.add(state.getId());
		}
		return ret;
	}
    
    public IBpmnPlanContext execute(IBpmnPlanContext body) 
    {
//    	setFinished(false);
//    	this.successorId = null;

    	// check conditions and set successor
    	Iterator conds = conditionalSuccessors.keySet().iterator();
    	IBpmnState successor = null;
    	while (successor == null && conds.hasNext())
		{
    		String cond = (String) conds.next();
			Boolean b = body.evalJadexOQLCondition(cond);
			if (b.booleanValue())
			{
				successor = (IBpmnState) conditionalSuccessors.get(cond);
			}
		}
    	
//    	setFinished(true);
    	return body;
    }
    
    /**
     * 
     * /
    public void execute() {
        // compute successor
        // ArrayList<IContextVariable> allContexts =
        // getThePlan().getTheContext();
        int i = 0;
        try {
            for (i = 0; i < conditions.size(); i++) {
                
            	StringReader sr = new StringReader(conditions.get(i));
                ContextLexer conditionLexer = new ContextLexer(sr);
                ContextParser conditionParser = new ContextParser(
                        conditionLexer);
                // String sRes = conditionParser.condition();
                String sRes = conditionParser.condition(getTheScope());
                
//                System.out.println(">>>>>>>>>>>>>>>>Condition To compile: " + sRes);
//                System.out.println();
                ICondition condi = getThePlan().createCondition(sRes);
//                System.out.println("<<<<<<<<<<<<<<<OK");
//                System.out.println();
                // condi.setParameter("$LocalBase", allContexts);
                Object o = condi.execute();
                // System.out.println(">>>Compiled condition expression: " +
                // sRes + " ----> " + o);
                if (o instanceof Boolean) {
                    if (((Boolean) o).booleanValue()) {
                        iSelectedSuccessor = i;
                        // System.out.println("Selected successor: " +
                        // iSelectedSuccessor);
                        break;
                    }
                }
                
            }
        } catch (TokenStreamException err) {
            System.err.println("Error compiling condition \""
                    + conditions.get(i) + "\"" + " - XORGatewayID: "
                    + getTheID());
            System.err.println(err.getMessage());
        } catch (RecognitionException err) {
            System.err.println("Error compiling condition \""
                    + conditions.get(i) + "\"" + " - XORGatewayID: "
                    + getTheID());
            System.err.println(err.getMessage());
        }
    }
    */
    
    // ---- getter / setter ----
    
    public Map getXorConditions() 
    {
        return conditionalSuccessors;
    }
    
    public void setXorCondtions(Map conditions)
    {
    	this.conditionalSuccessors = conditions;
    }
    
    
    
    // ---- self parsing element overrides ----
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) 
    {
        // nothing to do here for XORGatewayState
    }

    public void endElement(String uri, String localName, String qName) 
    {
        // nothing to do here for XORGatewayState
    }

}
