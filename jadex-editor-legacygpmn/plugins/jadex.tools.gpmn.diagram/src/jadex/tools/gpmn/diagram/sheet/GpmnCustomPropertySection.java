package jadex.tools.gpmn.diagram.sheet;

import jadex.editor.common.model.properties.AbstractCommonPropertySection;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public abstract class GpmnCustomPropertySection extends AbstractCommonPropertySection
{
	protected Map<String, Label> labels = new HashMap<String, Label>();
	protected Map<String, Control> controls = new HashMap<String, Control>();
	
	protected Group confGroup;
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		parent.setLayout(new FillLayout());
		
		confGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		addDisposable(confGroup);
		confGroup.setLayout(new FillLayout());
		((FillLayout) confGroup.getLayout()).type = SWT.HORIZONTAL;
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		refreshControls();
	}
	
	@Override
	public boolean shouldUseExtraSpace()
	{
		return true;
	}

	protected void setTextControlValue(Text control, String value)
	{
		if (value != null)
		{
			control.setText(value);
		}
	}
	
	protected Text addLabeledTextControl(Composite parent, String id)
	{
		return addLabeledTextControl(parent, id, SWT.MULTI | SWT.BORDER);
	}
	
	protected abstract void refreshControls();
	
	protected Text addLabeledTextControl(Composite parent, String id, int style)
	{
		Label label = new Label(parent, SWT.LEFT);
		addDisposable(label);
		labels.put(id, label);
		label.setText(id);
		label.setLayoutData(new GridData());
		
		Text text = new Text(parent, style);
		addDisposable(text);
		controls.put(id, text);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gd);
		
		return text;
	}
	
	protected void dispatchCommand(ICommand cmd)
	{
		try
		{
			cmd.execute(null, null);
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
	}
	
	protected static final String conv(String text)
	{
		return text == null ? "" : text;
	}
	
	protected void updateSectionValues()
	{
	}
}
