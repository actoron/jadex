/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.runtime.task.IRuntimeTaskProvider;
import jadex.tools.bpmn.runtime.task.ParameterMetaInfo;
import jadex.tools.bpmn.runtime.task.StaticJadexRuntimeTaskProvider;
import jadex.tools.bpmn.runtime.task.TaskMetaInfo;

import org.eclipse.emf.ecore.xml.type.internal.RegEx.RegularExpression;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexUserTaskImplComboSection extends
		AbstractComboPropertySection
{

	// ---- constants ----
	
//	protected static final String[] comboItems = new String[] {
//		"jadex.bpmnbdi.task.WriteBeliefTask.class",
//		"jadex.bpmn.runtime.task.PrintTask.class",
//		"jadex.bpmn.runtime.task.InvokeMethodTask.class",
//		"jadex.bpmn.runtime.task.CreateComponentTask.class",
//		"jadex.bpmn.runtime.task.DestroyComponentTask.class",
//		"jadex.wfms.client.task.WorkitemTask.class"
//	};

	protected static final String ACTIVITY_TASK_IMPLEMENTATION_GROUP = "Task implementation";
	
	// ---- attributes ----
	
	//protected CLabel taskMetaInfoLabel;
	protected Text taskMetaInfoText;
	
	protected IRuntimeTaskProvider taskProvider;

	// ---- constructor ----

	/**
	 * Default constructor, initializes super class
	 */
	public JadexUserTaskImplComboSection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_ACTIVITY_CLASS_DETAIL);
		this.taskProvider = new StaticJadexRuntimeTaskProvider();
	}

	// ---- override methods ----
	
	/**
	 * @see jadex.tools.bpmn.editor.properties.AbstractComboPropertySection#getComboItems()
	 */
	@Override
	protected String[] getComboItems()
	{
		//return comboItems;
		return taskProvider.getAvailableTaskImplementations();
	}


	/**
	 * Manages the input.
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if (modelElement != null && cCombo != null)
		{
			updateTaskMetaInfo(cCombo.getText());
		}
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.editor.properties.AbstractComboPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage)
	{
		
		super.createControls(parent, tabbedPropertySheetPage);

		addUserTaskMetaInfo();
		
		// Add some listeners to the abstract combo
		
		cCombo.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String text = cCombo.getText();
				String newText = text.substring(0, e.start) + e.text
						+ text.substring(e.end);
				
				// don't allow non word characters
				RegularExpression re = new RegularExpression("\\w*"); //$NON-NLS-1$
				if (!re.matches(newText))
				{
					e.doit = false;
				}
			}
		});
		
		cCombo.addTraverseListener(new TraverseListener()
		{
			public void keyTraversed(TraverseEvent e)
			{
				if (e.detail == SWT.TRAVERSE_RETURN)
				{
					e.doit = false;
					e.detail = SWT.TRAVERSE_NONE;
					String newText = cCombo.getText();

					// check if we have a valid class name
					if(newText.endsWith(".class")) //$NON-NLS-1$
					{
						cCombo.add(newText);
						cCombo.setSelection(new Point(0, newText
								.length()));
					}

				}
			}
		});
		
		cCombo.addSelectionListener(new SelectionListener()
		{
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String taskClassName = ((CCombo) e.getSource()).getText();
				updateTaskMetaInfo(taskClassName);
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		
		
	}

	// ---- methods ----
	
	protected void addUserTaskMetaInfo()
	{
		
		sectionComposite = groupExistingControls(ACTIVITY_TASK_IMPLEMENTATION_GROUP);
		
		Layout sectionLayout = sectionComposite.getLayout();
		
		//taskMetaInfoLabel = getWidgetFactory().createCLabel(sectionComposite, "test", SWT.WRAP | SWT.MULTI );
		taskMetaInfoText = getWidgetFactory().createText(sectionComposite, "", SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL );
		
		if (sectionLayout instanceof GridLayout)
		{

			// extend the section layout with a new column
			//((GridLayout) sectionLayout).numColumns = ((GridLayout) sectionLayout).numColumns +1;
			
			GridData labelData = new GridData();
			labelData.horizontalSpan = ((GridLayout) sectionLayout).numColumns;
			
			labelData.widthHint = 600; 
			labelData.horizontalAlignment = SWT.FILL;
			labelData.heightHint = 100;

			//taskMetaInfoLabel.setLayoutData(labelData);
			taskMetaInfoText.setLayoutData(labelData);
		}

	}
	
	protected void updateTaskMetaInfo(String taskClassName)
	{
		
		String metaInfo;
		metaInfo = createTaskMetaInfoString(taskProvider.getTaskMetaInfoFor(taskClassName));
		
		//taskMetaInfoLabel.setText(metaInfo);
		taskMetaInfoText.setText(metaInfo);
	}
	
	protected String createTaskMetaInfoString(TaskMetaInfo taskMetaInfo)
	{
		if (taskMetaInfo == null)
		{
			return "";
		}
		
		StringBuffer info = new StringBuffer();
		info.append(taskMetaInfo.getDescription() + "\n");
		
		ParameterMetaInfo[] params = taskMetaInfo.getParameterMetaInfos();
		for (int i = 0; i < params.length; i++)
		{
			info.append("\n" + "Parameter:" + params[i].getName() + "\n");
			info.append("\t" + "Direction: " + params[i].getDirection() + "\n");
			info.append("\t" + "Class: " + params[i].getClazz() + "\n");
			info.append("\t" + "Initial value: " + params[i].getInitialValue() + "\n");
			info.append("\t" + "Description: " + params[i].getDescription() + "\n");
			
			//info.append("\t" + params[i].toString());
		}
		
		return info.toString();
	}
	
	protected void createTaskParameter(String taskClassName)
	{
		TaskMetaInfo metaInfo = taskProvider.getTaskMetaInfoFor(taskClassName);
		ParameterMetaInfo[] taskParameter = metaInfo.getParameterMetaInfos();
		// TODO: implement
	}
	
}
