package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.BpmnPlan;
import jadex.tools.gpmn.SubProcess;
import jadex.tools.gpmn.diagram.edit.commands.ChangeBpmnPlanVisibilityCommand;
import jadex.tools.gpmn.diagram.edit.parts.ActivationPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.BpmnPlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.VirtualActivationEdgeEditPart;
import jadex.tools.gpmn.diagram.tools.ActivationPlanSelectToolEx;
import jadex.tools.gpmn.diagram.tools.SGpmnUtilities;
import jadex.tools.gpmn.diagram.tools.VirtualActivationEdgeSelectToolEx;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @generated NOT
 */
public class GpmnViewControlSection extends GpmnCustomPropertySection
{
	public static final String VIEW_CONTROLS_TITLE = "View Controls";
	public static final String DETAIL_DESC = "Detail Level:";
	
	protected static final int HIGH_DETAIL_MODE = 0;
	protected static final int MEDIUM_DETAIL_MODE = 1;
	protected static final int LOW_DETAIL_MODE = 2;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		confGroup.setText(VIEW_CONTROLS_TITLE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		confGroup.setLayout(gridLayout);
		
		Label detailLabel = new Label(confGroup, SWT.NULL);
		addDisposable(detailLabel);
		labels.put(DETAIL_DESC, detailLabel);
		detailLabel.setText(DETAIL_DESC);
		detailLabel.setLayoutData(new GridData());
		
		Scale detailSlider = new Scale(confGroup, SWT.HORIZONTAL);
		addDisposable(detailSlider);
		controls.put(DETAIL_DESC, detailSlider);
		detailSlider.setLayoutData(new GridData());
		detailSlider.setMinimum(0);
		detailSlider.setMaximum(2);
		detailSlider.setPageIncrement(1);
		
		detailSlider.addSelectionListener(new SelectionListener()
		{
			private Integer prevSelect = Integer.valueOf(0);
			
			public void widgetSelected(SelectionEvent e)
			{
				Scale slider = (Scale) controls.get(DETAIL_DESC);
				slider.setSelection(slider.getSelection());
				if (prevSelect != null && slider.getSelection() == prevSelect.intValue())
					return;
				
				switch(slider.getSelection())
				{
					case HIGH_DETAIL_MODE:
						selectHighDetailMode();
						break;
					case MEDIUM_DETAIL_MODE:
						selectMediumDetailMode();
						break;
					case LOW_DETAIL_MODE:
						selectLowDetailMode();
						break;
						
					default:
				}
				
				prevSelect = Integer.valueOf(slider.getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		
		Label dummy = new Label(confGroup, SWT.NULL);
		addDisposable(dummy);
		labels.put(VIEW_CONTROLS_TITLE, dummy);
		dummy.setLayoutData(new GridData());
	}
	
	protected void refreshControls()
	{
	}
	
	protected void selectHighDetailMode()
	{
		GpmnDiagramEditPart diagramPart = (GpmnDiagramEditPart) editPart;
		
		ConnectionEditPart[] connections = (ConnectionEditPart[]) diagramPart.getConnections().toArray(new ConnectionEditPart[0]);
		for (int i = 0; i < connections.length; ++i)
		{
			if (connections[i] instanceof VirtualActivationEdgeEditPart)
			{
				VirtualActivationEdgeSelectToolEx.getExpandCommand(diagramPart, SGpmnUtilities.getPlanFromVirtualEdge((Edge) ((VirtualActivationEdgeEditPart) connections[i]).getNotationView())).execute();
				diagramPart.refresh();
				connections = (ConnectionEditPart[]) diagramPart.getConnections().toArray(new ConnectionEditPart[0]);
				i = 0;
			}
		}
	}
	
	protected void selectMediumDetailMode()
	{
		GpmnDiagramEditPart diagramPart = (GpmnDiagramEditPart) editPart;
		
		EditPart[] parts = (EditPart[]) diagramPart.getPrimaryEditParts().toArray(new EditPart[0]);
		for (int i = 0; i < parts.length; ++i)
		{
			if (parts[i] instanceof ActivationPlanEditPart)
			{
				ActivationPlanSelectToolEx.getHideCommand((ActivationPlanEditPart) parts[i]).execute();
				diagramPart.refresh();
			}
		}
		
		Node[] nodes = (Node[]) diagramPart.getNotationView().getChildren().toArray(new Node[0]);
		for (int i = 0; i < nodes.length; ++i)
		{
			if ((nodes[i].getElement() instanceof BpmnPlan || nodes[i].getElement() instanceof SubProcess) && nodes[i].isVisible() == false)
			{
				dispatchCommand(new ChangeBpmnPlanVisibilityCommand(diagramPart, nodes[i], true));
			}
		}
	}
	
	protected void selectLowDetailMode()
	{
		GpmnDiagramEditPart diagramPart = (GpmnDiagramEditPart) editPart;
		
		Node[] nodes = (Node[]) diagramPart.getNotationView().getChildren().toArray(new Node[0]);
		for (int i = 0; i < nodes.length; ++i)
		{
			if ((nodes[i].getElement() instanceof BpmnPlan || nodes[i].getElement() instanceof SubProcess) && nodes[i].isVisible() == true)
			{
				dispatchCommand(new ChangeBpmnPlanVisibilityCommand(diagramPart, nodes[i], false));
			}
		}
	}
}
