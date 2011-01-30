package jadex.simulation.analysis.buildingBlocks.simulation.netLogo;

import java.awt.Frame;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.media.ExtendedCachingControl;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.commons.collections15.ExtendedProperties;
import org.nlogo.api.ClassManager;
import org.nlogo.app.App;
import org.nlogo.awt.UserCancelException;
import org.nlogo.headless.HeadlessWorkspace;
import org.nlogo.lite.InterfaceComponent;
import org.nlogo.workspace.ExtensionPrimitiveManager;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bdi.runtime.ICapability;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;
import jadex.simulation.analysis.common.dataObjects.IAExperimentResult;
import jadex.simulation.analysis.common.dataObjects.IAParameter;
import jadex.simulation.analysis.common.dataObjects.IAParameterCollection;

/**
 * Implementation of a NetLogo service for (single) experiments.
 */
public class NetLogoExperimentService extends BasicService implements IExecuteExperimentService {

	InterfaceComponent comp = null;
	JTextArea compLite = new JTextArea();
	
	/**
	 * Create a new netLogo Simulation Service
	 * 
	 * @param comp
	 *            The active component.
	 */
	public NetLogoExperimentService(ICapability cap) {
		super(cap.getServiceProvider().getId(), IExecuteExperimentService.class, null);
//		Map prop = getPropertyMap();
//		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.buildingBlocks.execution.ExecutionServiceView");
//		setPropertyMap(prop);
	}
	
	public Set<String> supportedModels() {
		Set<String> result = new HashSet<String>();
		result.add("netLogo");
		return result;
	}

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(final IAExperimentJob expJob) {
		final Future res = new Future();
		if ((Boolean)expJob.getExperimentalFrame().getExperimentParameter("visualisation").getValue())
		{		
			try 
	        {
				java.awt.EventQueue.invokeAndWait
                ( new Runnable()
                    { public void run() {
                        try {
                        	String filePre = new File("..").getCanonicalPath() + "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/model";
							String fileName = filePre + "/netLogo/" + expJob.getModel().getName();
                          comp.open(fileName);
                        }
                        catch(Exception ex) {
                          ex.printStackTrace();
                        }
                    } } ) ;
            IAParameterCollection inputPara = expJob.getModel().getInputParameters();
            for (IAParameter parameter : inputPara.values()) {
				String comm = "set " + parameter.getName() + " " + parameter.getValue().toString();
				comp.command(comm);
			}

            Integer rep = 0;
			Integer replicationen = (Integer) expJob.getExperimentalFrame().getExperimentParameter("wiederholungen").getValue();
			
			while(rep < replicationen)
			{
				comp.command("setup");
		        comp.command("go");
		        expJob.getExperimentResult().setResultParamterValue("ticks", comp.report("ticks"));
				rep++;
			}
			}
        	catch(Exception ex) {
        		ex.printStackTrace();
        	}
		} else
		{
			HeadlessWorkspace workspace =
				HeadlessWorkspace.newInstance();
		try {
			String filePre = new File("..").getCanonicalPath() + "/sodekovs-simulationcontrol/src/main/java/jadex/simulation/analysis/model";
			String FileName = filePre + "/netLogo/" + expJob.getModel().getName();
			compLite.append("### netLogo 4.1.2 ###");
			compLite.append("Open file" + FileName + "\n");
			
			workspace.open(FileName);
			 IAParameterCollection inputPara = expJob.getModel().getInputParameters();
	            for (IAParameter parameter : inputPara.values()) {
					String comm = "set " + parameter.getName() + " " + parameter.getValue().toString();
					workspace.command(comm);
				}
	          
	            Integer rep = 0;
				Integer replicationen = (Integer) expJob.getExperimentalFrame().getExperimentParameter("wiederholungen").getValue();
				
				while(rep < replicationen)
				{
					compLite.append("Start " +  expJob.getModel().getName() + "\n");
					comp.command("setup");
			        comp.command("go");
			        expJob.getExperimentResult().setResultParamterValue("ticks", comp.report("ticks"));
					compLite.append("End " +  expJob.getModel().getName() + "\n");
					rep++;
				}
			
			workspace.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		}
		res.setResult(expJob);
		return res;
	}

	@Override
	public IFuture getView(JFrame frame) {
		comp = new InterfaceComponent(frame);
		return new Future(comp);
	}

	@Override
	public IFuture getView() {
		return new Future(comp);
	}
}
