package jadex.simulation.analysis.buildingBlocks.generalAnalysis;

import jadex.commons.future.IFuture;
import jadex.bridge.service.IService;
import jadex.simulation.analysis.common.services.IAnalysisService;
import jadex.simulation.analysis.common.services.IAnalysisSessionService;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

public interface IGeneralAnalysisService extends IAnalysisService{

	public void registerView(JComponent view);
	
	public IFuture getView(Frame owner);
	
	public void signal(ActionEvent ar);
	
	public void registerListener(ActionListener listener);

}
