package jadex.simulation.helper;

import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.shop.IShop;
import jadex.bdi.runtime.impl.ElementFlyweight;
import jadex.bdi.runtime.impl.ExternalAccessFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.IFuture;
import jadex.commons.ISuspendable;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.rules.state.IOAVState;
import jadex.simulation.model.ObservedEvent;

/**
 * Check whether an expression is true, defined for an Object
 * 
 * @author Ante Vilenica
 * 
 */
public class EvaluateExpression {

	/**
	 * Evaluate expression. Works for ISpaceObjects, but not for BDI Agents.
	 * 
	 * @param space
	 * @param expression
	 * @param objectName
	 * @param objectType
	 * @return
	 */
	public static boolean evaluate(AbstractEnvironmentSpace space, final String expression, String objectName, String objectType) {

		if (objectType.equalsIgnoreCase(Constants.ISPACE_OBJECT)) {
			ISpaceObject object = space.getSpaceObjectsByType(objectName)[0];

			SimpleValueFetcher fetcher = new SimpleValueFetcher();
			fetcher.setValue("$object", object);
			fetcher.setValue("$space", space);

			// String expression = "$object.getProperty(\"ore\") >= 10";
			return evaluateExpression(fetcher, expression);

		} else if (objectType.equalsIgnoreCase(Constants.BDI_AGENT)) {
			System.err.println("#EvaluateExpression# Expressions can not be evaluated for BDI Agents. ->" + expression);
		} else {
			System.err.println("#EvaluateExpression# Expressions can only be evaluated for ISpaceObjects. ->" + expression);
		}
		return false;
	}

	/**
	 * Evaluate expression for given fetcher and expression.
	 * 
	 * @param fetcher
	 * @param expression
	 * @return
	 */
	public static boolean evaluateExpression(IValueFetcher fetcher, String expression) {
		// String expression = "$object.getProperty(\"ore\") >= 10";
		Object val = SJavaParser.evaluateExpression(expression, fetcher);

		if (val instanceof Boolean) {
			if ((Boolean) val) {
				return true;
			}
		} else {
			System.err.println("#EvaluateExpression# Could not evaluate invalid expression: " + val);
		}
		return false;
	}
}
