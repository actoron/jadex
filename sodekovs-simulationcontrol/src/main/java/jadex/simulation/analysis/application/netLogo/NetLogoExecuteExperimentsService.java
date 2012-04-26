package jadex.simulation.analysis.application.netLogo;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.GnuLogger;
import jadex.simulation.analysis.common.GnuLogger2;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.nlogo.headless.HeadlessWorkspace;
import org.nlogo.lite.InterfaceComponent;

/**
 * Implementation of a NetLogo service for a experiment object.
 */
public class NetLogoExecuteExperimentsService extends
		ABasicAnalysisSessionService implements IAExecuteExperimentsService {
	
	Logger logger;

	/**
	 * Create a new netLogo Simulation Service
	 * 
	 * @param comp
	 *            The active generalComp.
	 */
	public NetLogoExecuteExperimentsService(IExternalAccess access) {
		super(access, IAExecuteExperimentsService.class, true);
		logger = Logger.getLogger("sweeping");
		FileHandler fh = null;

		try
		{
			String dir = System.getProperty("user.home") + "\\logs";
			File f = new File(dir);
			if (!f.isDirectory())
				f.mkdir();
			fh = new FileHandler("%h/logs/" + "sweeping" + ".log", true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		fh.setFormatter(new GnuLogger2());
		logger.addHandler(fh);
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
	}

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(String session, final IAExperiment exp) {
		final Future res = new Future();

		if (session == null)
			session = (String) createSession(null).get(susThread);

		if ((Boolean) exp.getExperimentParameter("Visualisierung").getValue()) {
			// NetLogoSessionView view =
			// ((NetLogoSessionView)sessionViews.get(session));
			// final JFrame frame =
			// (JFrame)SwingUtilities.getRoot(taskview.getComponent());
			final JFrame frame = new JFrame();

			final InterfaceComponent comp = new InterfaceComponent(frame);
			frame.add(comp);
			((NetLogoSessionView) sessionViews.get(session)).showFull(comp);

			try {
				java.awt.EventQueue.invokeAndWait(new Runnable() {
					public void run() {
						try {
							frame.setSize(800, 800);
							frame.setVisible(true);
							String filePre = new File("..").getCanonicalPath()
									+ "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/netLogo/models/";
							String fileName = filePre
									+ exp.getModel().getName() + ".nlogo";
							comp.open(fileName);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				});
				IAParameterEnsemble inputPara = exp.getModel()
						.getInputParameters();
				for (IAParameter parameter : inputPara.getParameters().values()) {
					String comm = "set " + parameter.getName() + " "
							+ parameter.getValue().toString();
					// System.out.println(comm);
					comp.command(comm);
				}

				Integer rep = 0;
				Integer replicationen = (Integer) exp.getExperimentParameter(
						"Wiederholungen").getValue();

				Double tick = 0.0;
				while (rep < replicationen) {
					comp.command("setup");
					comp.command("go");
					tick += (Double) comp.report("ticks");
					rep++;
				}
				// TODO Hack
				exp.getResultParameter("ticks").setValue(tick / 30);
				frame.dispose();
				res.setResult(exp);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			// JTextArea compLite = new JTextArea();
			// ((NetLogoSessionView)sessionViews.get(session)).showLite(compLite);
			HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
			try {
				String filePre = new File("..").getCanonicalPath()
						+ "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/application/netLogo/models/";
				String FileName = filePre + exp.getModel().getName() + ".nlogo";
				// compLite.append("### netLogo 4.1.2 ###");
				// compLite.append("Open file" + FileName + "\n");

				workspace.open(FileName);
				IAParameterEnsemble inputPara = exp.getConfigParameters();
				for (IAParameter parameter : inputPara.getParameters().values()) {
					String comm = "set " + parameter.getName() + " "
							+ parameter.getValue().toString();
					workspace.command(comm);
					System.out.print(parameter.getName() + " "
							+ parameter.getValue().toString() + " ");
				}

				Integer rep = 0;
				Integer replicationen = (Integer) exp.getExperimentParameter(
						"Wiederholungen").getValue();

				Double tick = 0.0;
				logger.info(((Integer)exp.getConfigParameter("population").getValue()).toString());
				while (rep < replicationen) {
					// compLite.append("Start " + exp.getModel().getName() +
					// "\n");
					workspace.command("setup");
					workspace.command("go");

					Double result = (Double) workspace.report("ticks");
					tick += result;
					System.out.print(" >" + result);
					
					logger.info(" " + result);
					// compLite.append("End " + exp.getModel().getName() +
					// "\n");
					rep++;
				}
				logger.info("\n");
				System.out.println(" = " + tick);

				// TODO Hack
				exp.getResultParameter("ticks").setValue(tick / replicationen);

				workspace.dispose();
				res.setResult(exp);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return res;
		// return new Future();
	}

	@Override
	public Set<Modeltype> supportedModels() {
		Set<Modeltype> result = new HashSet<Modeltype>();
		result.add(Modeltype.NetLogo);
		return result;
	}

}
