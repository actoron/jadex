package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
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
			private Integer prevSelect = new Integer(0);
			
			public void widgetSelected(SelectionEvent e)
			{
				Scale slider = (Scale) controls.get(DETAIL_DESC);
				slider.setSelection(slider.getSelection());
				if (prevSelect != null && slider.getSelection() == prevSelect.intValue())
					return;
				
				System.out.println(slider.getSelection());
				
				GpmnDiagramEditPart diagramPart = (GpmnDiagramEditPart) editPart;
				
				for (Object part : diagramPart.getChildren())
					System.out.println(part);
				
				prevSelect = new Integer(slider.getSelection());
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
	
}
