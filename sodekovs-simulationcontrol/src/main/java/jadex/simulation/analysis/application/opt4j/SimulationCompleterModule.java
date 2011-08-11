package jadex.simulation.analysis.application.opt4j;

import org.opt4j.common.completer.CompleterModule;
import org.opt4j.common.completer.SequentialCompleter;
import org.opt4j.core.optimizer.Completer;

public class SimulationCompleterModule extends CompleterModule
{
	@Override
	public void config()
	{
		setType(CompleterModule.Type.PARALLEL);
		bind(Completer.class).to(SimulationCompleter.class).in(SINGLETON);
	}
}
