/**
 * 
 */
package deco4mas.distributed.convergence;

/**
 * Interface for classes that wanted to be informed about {@link ConstraintChangeEvent} in {@link ConvergenceMicroAgent}s.
 * 
 * @author Thomas Preisler
 */
public interface IMicroAgentConvergenceListener {
	
	public void constraintChangend(ConstraintChangeEvent event);
}