/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.editor.properties.template.AbstractComboPropertySection;
import jadex.tools.bpmn.editor.properties.template.AbstractParameterTablePropertySection;
import jadex.tools.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;
import jadex.tools.bpmn.runtime.task.IJadexTaskProvider;
import jadex.tools.bpmn.runtime.task.IParameterMetaInfo;
import jadex.tools.bpmn.runtime.task.ITaskMetaInfo;
import jadex.tools.bpmn.runtime.task.PreferenceTaskProviderProxy;
import jadex.tools.model.common.properties.table.MultiColumnTable;
import jadex.tools.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import org.eclipse.emf.ecore.xml.type.internal.RegEx.RegularExpression;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

	protected static final String ACTIVITY_TASK_IMPLEMENTATION_GROUP = "Task implementation";
	
	// ---- attributes ----
	
	protected Text taskMetaInfoText;
	
	protected IJadexTaskProvider taskProvider;
	
	/** The table add default parameter button */
	protected Button addDefaultButton;

	// ---- constructor ----

	/**
	 * Default constructor, initializes super class
	 */
	public JadexUserTaskImplComboSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_ACTIVITY_CLASS_DETAIL);
		this.taskProvider = new PreferenceTaskProviderProxy();
	}

	// ---- override methods ----
	
	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		this.taskProvider = null;
		// dispose is done in superclass, see addDisposable
		super.dispose();
	}

	
	/**
	 * @see jadex.tools.bpmn.editor.properties.template.AbstractComboPropertySection#getComboItems()
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

		addUserTaskMetaInfoText();
		
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
			
			public void widgetSelected(SelectionEvent e)
			{
				String taskClassName = ((CCombo) e.getSource()).getText();
				updateTaskMetaInfo(taskClassName);
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		
		
		//GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		//gridData.widthHint = 80;

		// Create and configure the "Add" button
		Button addDefaultParameterButton = new Button(sectionComposite, SWT.PUSH | SWT.CENTER);
		addDefaultParameterButton.setText("Add default Parameter");
		addDefaultParameterButton.setToolTipText("Adds default parameter for selected class at parameter tables end");
		//addDefaultParameter.setLayoutData(gridData);
		addDefaultParameterButton.addSelectionListener(new SelectionAdapter()
		{
			/** 
			 * Add a list of default parameter to the parameter table
			 * @generated NOT 
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String taskClassName = cCombo.getText();
				updateTaskParameterTable(taskClassName);
			}
		});
		addDefaultButton = addDefaultParameterButton;
		addDisposable(addDefaultParameterButton);

	}

	// ---- methods ----
	
	protected void addUserTaskMetaInfoText()
	{
		
		sectionComposite = groupExistingControls(ACTIVITY_TASK_IMPLEMENTATION_GROUP);
		
		Layout sectionLayout = sectionComposite.getLayout();
		
		//taskMetaInfoLabel = getWidgetFactory().createCLabel(sectionComposite, "test", SWT.WRAP | SWT.MULTI );
		taskMetaInfoText = getWidgetFactory().createText(sectionComposite, "", SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL );
		addDisposable(taskMetaInfoText);
		
		if (sectionLayout instanceof GridLayout)
		{
			GridData labelData = new GridData();
			labelData.horizontalSpan = ((GridLayout) sectionLayout).numColumns;
			
			labelData.widthHint = 600; 
			labelData.horizontalAlignment = SWT.FILL;
			labelData.heightHint = 100;

			taskMetaInfoText.setLayoutData(labelData);
		}

	}
	
	protected void updateTaskMetaInfo(String taskClassName)
	{
		
		String metaInfo;
		metaInfo = createTaskMetaInfoString(taskProvider.getTaskMetaInfo(taskClassName));
		
		taskMetaInfoText.setText(metaInfo);
	}
	
	protected String createTaskMetaInfoString(ITaskMetaInfo taskMetaInfo)
	{
		if (taskMetaInfo == null)
		{
			return "";
		}
		
		StringBuffer info = new StringBuffer();
		info.append(taskMetaInfo.getDescription() + "\n");
		
		IParameterMetaInfo[] params = taskMetaInfo.getParameterMetaInfos();
		if (params == null)
			return info.toString();
		for (int i = 0; i < params.length; i++)
		{
			info.append("\n" + "Parameter: " + params[i].getName() + "\n");
			info.append("\t" + "Direction: " + params[i].getDirection() + "\n");
			info.append("\t" + "Class: " + params[i].getClazz() + "\n");
			info.append("\t" + "Initial value: " + params[i].getInitialValue() + "\n");
			info.append("\t" + "Description: " + params[i].getDescription() + "\n");
			
			//info.append("\t" + params[i].toString());
		}
		
		return info.toString();
	}
	
	protected void updateTaskParameterTable(String taskClassName)
	{
		ITaskMetaInfo metaInfo = taskProvider.getTaskMetaInfo(taskClassName);
		IParameterMetaInfo[] taskParameter = metaInfo.getParameterMetaInfos();

		MultiColumnTable parameterTable = JadexBpmnPropertiesUtil
					.getJadexEAnnotationTable(
							modelElement,
							JadexBpmnPropertiesUtil
									.getTableAnnotationIdentifier(
											JadexCommonParameterSection.PARAMETER_ANNOTATION_IDENTIFIER,
											JadexCommonParameterSection.PARAMETER_ANNOTATION_DETAIL_IDENTIFIER)); 
		if (parameterTable != null) 
		{	
			parameterTable = addTaskParamterTable(parameterTable, taskParameter);
		}
		else
		{
			parameterTable = createNewParameterTable(taskParameter);
		}

		JadexBpmnPropertiesUtil
				.updateJadexEAnnotationTable(
						modelElement,
						JadexBpmnPropertiesUtil
								.getTableAnnotationIdentifier(
										JadexCommonParameterSection.PARAMETER_ANNOTATION_IDENTIFIER,
										JadexCommonParameterSection.PARAMETER_ANNOTATION_DETAIL_IDENTIFIER),
						parameterTable);
		
		TableViewer viewer = JadexCommonParameterSection.getParameterTableViewerFor(modelElement);
		if (viewer != null)
		{
			viewer.refresh();
		}
	}
	
	/**
	 * Create a new Table for meta info
	 * @param parameterMetaInfo
	 * @return
	 */
	protected MultiColumnTable createNewParameterTable(IParameterMetaInfo[] parameterMetaInfo)
	{
		MultiColumnTable newTable = new MultiColumnTable(parameterMetaInfo.length, JadexCommonParameterSection.UNIQUE_PARAMETER_ROW_ATTRIBUTE);
		for (int i = 0; i < parameterMetaInfo.length; i++)
		{
			String[] columnValues = new String[] {
					parameterMetaInfo[i].getDirection(),
					parameterMetaInfo[i].getName(),
					parameterMetaInfo[i].getClazz().getName(),
					parameterMetaInfo[i].getInitialValue() };
			newTable.add(newTable.new MultiColumnTableRow(
					columnValues,
					JadexCommonParameterSection.UNIQUE_PARAMETER_ROW_ATTRIBUTE));

		}
		
		return newTable;
	}
	
	/**
	 * Update the table with meta info
	 * 
	 * @param table
	 * @param metaInfo
	 */
	protected MultiColumnTable updateTaskParamterTable(MultiColumnTable table, IParameterMetaInfo[] metaInfo)
	{
		MultiColumnTable newTable = createNewParameterTable(metaInfo);
		int typeIndex = AbstractParameterTablePropertySection.getDefaultIndexForColumn(AbstractParameterTablePropertySection.TYPE_COLUMN);
		int valueIndex = AbstractParameterTablePropertySection.getDefaultIndexForColumn(AbstractParameterTablePropertySection.VALUE_COLUMN);
		
		for (MultiColumnTableRow row : table.getRowList())
		{
			int rowIndex = newTable.indexOf(row);
			if (rowIndex > -1)
			{
				MultiColumnTableRow newRow = newTable.get(rowIndex);
				
				if (newRow.getColumnValueAt(typeIndex).equals(row.getColumnValueAt(typeIndex)))
				{
					// types are equal (add the old value)
					newRow.setColumnValueAt(valueIndex, row.getColumnValueAt(valueIndex));
				}
				
			}
		}
		
		
		return newTable;
	}
	
	/**
	 * Update the table with meta info
	 * 
	 * @param table
	 * @param metaInfo
	 */
	protected MultiColumnTable addTaskParamterTable(MultiColumnTable table, IParameterMetaInfo[] metaInfo)
	{
		MultiColumnTable newTable = createNewParameterTable(metaInfo);
		
		for (MultiColumnTableRow row : newTable.getRowList())
		{
			table.add(row);
		}
		
		return table;
	}
	
	
	
}
