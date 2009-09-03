package jadex.wfms.simulation.gui;

import java.util.Set;

import jadex.wfms.simulation.Simulator;

import javax.swing.JFrame;

public class SimulationWindow extends JFrame
{
	private Simulator simulator;
	
	public SimulationWindow(Simulator simulator)
	{
		super("Process Simulator");
		this.simulator = simulator;
		
		//Set modelNames = simulator.getClientService().getProcessDefinitionService(simulator).getGpmnModelNames(simulator);
		
	}
	
	
}
