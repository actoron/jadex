package jadex.wfms.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import jadex.bpmn.BpmnModelLoader;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.gpmn.model.MGpmnModel;
import jadex.gpmn.model.MPlan;
import jadex.gpmn.model.MProcess;
import jadex.wfms.IWfms;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IClientService;
import jadex.wfms.service.IModelRepositoryService;
import jadex.wfms.service.IProcessDefinitionService;

public class Simulator implements IClient
{
	private IClientService clientService;
	
	public Simulator(IClientService clientService)
	{
		this.clientService = clientService;
	}
	
	public IClientService getClientService()
	{
		return clientService;
	}
	
	public void test()
	{
		MGpmnModel gpmnModel = (MGpmnModel) clientService.getProcessDefinitionService(this).getProcessModel(this, "dipp");
		System.err.println(gpmnModel);
		ProcessTreeModel model = new ProcessTreeModel();
		try
		{
			model.setRootModel(gpmnModel);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame frame = new JFrame("Simulator");
		JTree tree = new JTree(model);
		frame.add(tree);
		frame.pack();
		frame.setSize(300,300);
		frame.setVisible(true);
		int row = 0;
		while (row < tree.getRowCount())
			tree.collapseRow(row++);
	}
	
	public String getUserName()
	{
		return "TestUser";
	}
}
