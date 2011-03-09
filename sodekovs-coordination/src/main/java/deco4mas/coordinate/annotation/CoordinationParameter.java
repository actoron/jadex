package deco4mas.coordinate.annotation;

/**
 * This annotation marks a parameter in a micro agent that is used for the
 * coordination. Currently this annotation is optional and just improves the
 * readability of the code.
 * 
 * @author Thomas Preisler
 */
public @interface CoordinationParameter {

	/**
	 * @return The name of the steps (class name, not full qualified) in which
	 *         the parameter should be used for coordination.
	 */
	String[] steps();
}