package jadex.simulation.analysis.application.desmoJ;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.superClasses.events.service.AServiceEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.Reportable;
import desmoj.core.simulator.TimeInstant;

/**
 * Implementation of a DesmoJ service for (single) experiment.
 */
public class DesmoJExecuteExperimentsService extends ABasicAnalysisSessionService implements IAExecuteExperimentsService
{

	/**
	 * Create a new DesmoJ Simulation Service
	 * 
	 * @param comp
	 *            The active generalComp.
	 */
	public DesmoJExecuteExperimentsService(IExternalAccess access)
	{
		super(access, IAExecuteExperimentsService.class, true);

	}

	// -------- methods --------

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(String session, IAExperiment exp)
	{
		final Future res = new Future();
		DesmoJSessionView view = (DesmoJSessionView) sessionViews.get(session);

		// TODO find Model class
		view.addText("***** DESMO-J version 2.2.0 ***** " + "\n");

		Integer executions = 0;
		Integer replicationen = (Integer) exp.getExperimentParameter("Wiederholungen").getValue()-1;
//		view.addText("Experiment: " + exp.getModel().getName() + "\n");
//		view.addText("Replications: " + replicationen + "\n");
		
		//reflection to get Model and Fields
		Class modelClass = null;
		try
		{
			String filePre = new File("..").getCanonicalPath() +exp.getModel().getModelpath();
			String FileName = filePre + exp.getModel().getName();
			modelClass = Class.forName(FileName);
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		IAParameterEnsemble input = exp.getConfigParameters();
		Map<String, Field> fieldSet = new HashMap<String, Field>();
		for (Map.Entry<String, IAParameter> para : input.getParameters().entrySet())
		{
			Field fld;
			try
			{
				fld = modelClass.getField(para.getKey());
				fieldSet.put(para.getKey(),fld);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		while (executions < replicationen)
		{
			Experiment experiment = new Experiment(exp.getModel().getName() + "_experiment1", false);
			Model model = null;
			try
			{
				model = (Model) modelClass.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			
			//set Fields of Model (reflection) ONLY DOUBLES
			for (Map.Entry<String, IAParameter> para : input.getParameters().entrySet())
			{
				try
				{
					Field instancefield = fieldSet.get(para.getKey());
					Double value = (Double)para.getValue().getValue();
					instancefield.setDouble(model, value);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			model.connectToExperiment(experiment);
			
			experiment.setShowProgressBar(false);
			experiment.stop(new TimeInstant(1000.0));
			experiment.traceOff(new TimeInstant(0.0));
			experiment.debugOff(new TimeInstant(0.0));
			view.addText("Durchgang: " + executions + "\n");
			view.addText(exp.getModel().getName() + " starts at simulation time 0.0" + "\n");
			experiment.start();
			view.addText(" ...please wait... " + "\n");
			experiment.finish();
			view.addText(exp.getModel().getName() + " stopped at simulation time 15000" + "\n");
			
			//set Output Parameters
			for (Reportable report : model.getReportables())
			{
				String[] headings = report.createReporter().getColumnTitles();
				String[] results = report.createReporter().getEntries();
				for (int i = 0; i < headings.length; i++)
				{
					for (Map.Entry<String, IAParameter> para : exp.getResultParameters().getParameters().entrySet())
					{
						if (headings[i].equals(para.getKey()))
						{
							IAParameter  parameter = para.getValue();
							parameter.setValue(results);
						}
					}
					
				}
			};
			
			// exp.getOutputParameter("zeit").setValue(expDesmo.getStopTime().getTimeAsDouble());
//			((ASummaryParameter) exp.getOutputParameter("Truck Wait Times")).addValue((rd.nextDouble() * 10));
			view.addText("\n");
			executions++;
			//TODO: Outputparameter auslesen
		}

		view.addText("Ausführung abgeschlossen");
		res.setResult(exp);
		return res;
	}

	@Override
	public Set<Modeltype> supportedModels()
	{
		Set<Modeltype> result = new HashSet<Modeltype>();
		result.add(Modeltype.DesmoJ);
		return result;
	}
}
