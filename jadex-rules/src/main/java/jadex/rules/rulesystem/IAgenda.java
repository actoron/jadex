package jadex.rules.rulesystem;

import java.util.Collection;
import java.util.List;


/**
 *  The agenda interface for a rule system.
 */
public interface IAgenda
{
	/**
	 *  Fire one activated rule.
	 */
	public void	fireRule();
	
	/**
	 *  Get the current activations.
	 *  @return The activations.
	 */
	public Collection getActivations();
	
	/**
	 *  Test if the agenda is empty.
	 *  @return True if is empty.
	 */
	public boolean isEmpty();
	
	/**
	 *  Get the state of the agenda.
	 *  Changes whenever the activation list changes.
	 */
	public int getState();
	
	//-------- debugging methods --------
	
	/**
	 *  The last activation. Represents the last rule that has been
	 *  executed (or that is currently executing, if still running).
	 *  @return null, when no rule has been executed or when fireRule
	 *    has been called on an empty agenda.	
	 */ 
	public Activation getLastActivation();

	/**
	 *  Get the next activation.
	 *  @return The next activation.
	 */ 
	public Activation getNextActivation();

	/**
	 *  Set the next activation.
	 */ 
	public void	setNextActivation(Activation next);
	
	/**
	 *  Get the history mode.
	 */
	public boolean	isHistoryEnabled();
	
	/**
	 *  Set the history mode.
	 */
	public void	setHistoryEnabled(boolean enabled);

	/**
	 *  Get the history.
	 *  @return null, if history not enabled.
	 */
	public List	getHistory();

	/**
	 *  Add an agenda listener.
	 */
	public void	addAgendaListener(IAgendaListener listener);
	
	/**
	 *  Remove an agenda listener.
	 */
	public void	removeAgendaListener(IAgendaListener listener);
}
