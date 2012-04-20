package jadex.simulation.analysis.application.opt4j;

import org.opt4j.common.completer.IndividualCompleterModule;
import org.opt4j.core.optimizer.IndividualCompleter;

public class SimulationCompleterModule extends IndividualCompleterModule {
	
	@Override
	public void config() {
		//TODO right?
		bind(SimulationCompleter.class).in(SINGLETON);
		bind(IndividualCompleter.class).to(SimulationCompleter.class);
		addOptimizerStateListener(SimulationCompleter.class);
	}
}
