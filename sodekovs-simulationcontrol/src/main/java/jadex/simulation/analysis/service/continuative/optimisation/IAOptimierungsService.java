package jadex.simulation.analysis.service.continuative.optimisation;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface IAOptimierungsService extends IAnalysisSessionService
{
	/**
	 * Ermöglicht die Konfiguration einer Session zur Optimierung durch ein IAParameterEnsemble.
	 * 
	 * @return session UUID
	 */
	public IFuture configurateOptimisation(UUID session, String method, IAParameterEnsemble methodParameter, IAParameterEnsemble solution, IAZielfunktion objective, IAParameterEnsemble config);

	/**
	 * Gibt die unterstützten Verfahren des Services zurück
	 * 
	 * @return Set<String> der Verfahren
	 */
	public IFuture supportedMethods();

	/**
	 * Gibt die Kontrollparameter eines bestimmten Verfahren zurück
	 * 
	 * @return ParameterEnsemble der Kontrollparameter
	 */
	public IFuture getMethodParameter(String methodName);

	/**
	 * Gibt eine Lösungen zu dem gegebenen Experiment. Eine Konfiguration der Session wird vorausgesetzt.
	 * 
	 * @param session
	 *            Session der Optimierung
	 * @param previousSolutions
	 *            Zu optimierendes Experiment
	 * @return Experimente zur Simulation als IAExperimentBatch
	 */
	public IFuture nextSolutions(UUID session,
			IAExperimentBatch previousSolutions);

	/**
	 * Überprüft den Abbruch der Optimierung
	 * 
	 * @param session
	 *            Session der Optimierung
	 * @return true, wenn Abbruchkriterium erreicht
	 */
	public Boolean checkEndofOptimisation(UUID session);

	IAParameterEnsemble getOptimum(UUID session);
}
