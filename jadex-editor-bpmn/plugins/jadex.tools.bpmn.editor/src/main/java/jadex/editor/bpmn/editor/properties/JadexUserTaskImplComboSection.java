/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractComboPropertySection;
import jadex.editor.bpmn.editor.properties.template.AbstractParameterTablePropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;
import jadex.editor.bpmn.runtime.task.IEditorParameterMetaInfo;
import jadex.editor.bpmn.runtime.task.IEditorTaskMetaInfo;
import jadex.editor.bpmn.runtime.task.IEditorTaskProvider;
import jadex.editor.bpmn.runtime.task.PreferenceTaskProviderProxy;
import jadex.editor.model.common.properties.table.MultiColumnTable;
import jadex.editor.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.xml.type.internal.RegEx.RegularExpression;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Claas Altschaffel
 * 
 */
public class JadexUserTaskImplComboSection extends AbstractComboPropertySection
{

	// ---- constants ----

	private static final String DESCRIPTION 	= "Description:  ";

	private static final String INITIAL_VALUE 	= "Initial value:";

	private static final String CLASS 			= "Class:        ";

	private static final String DIRECTION 		= "Direction:    ";

	private static final String PARAMETER 		= "Parameter: ";

	protected static final String ACTIVITY_TASK_IMPLEMENTATION_GROUP = "Task implementation";


	// ---- attributes ----

	protected StyledText taskMetaInfoText;
	
	protected StyledText taskMetaInfoParameter;

	protected IEditorTaskProvider taskProvider;

	/** The table add default parameter button */
	protected Button addDefaultButton;

	// ---- constructor ----

	/**
	 * Default constructor, initializes super class
	 */
	public JadexUserTaskImplComboSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
				JadexBpmnPropertiesUtil.JADEX_ACTIVITY_CLASS_DETAIL);
		this.taskProvider = new PreferenceTaskProviderProxy();
	}

	// ---- override methods ----

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		this.taskProvider.dispose();
		this.taskProvider = null;
		// dispose is done in superclass, see addDisposable
		super.dispose();
	}

	/**
	 * @see jadex.editor.bpmn.editor.properties.template.AbstractComboPropertySection#getComboItems()
	 */
	@Override
	protected String[] getComboItems()
	{
		// return comboItems;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.tools.bpmn.editor.properties.AbstractComboPropertySection#
	 * createControls(org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage)
	{

		super.createControls(parent, tabbedPropertySheetPage);

		if (rightButton != null)
		{
			Button oldButton = rightButton;

			// Create and configure the "Refresh" button
			Button refreshButton = getWidgetFactory().createButton(
					sectionComposite, "Refresh", SWT.PUSH | SWT.CENTER);
			refreshButton.setLayoutData(oldButton.getLayoutData());
			refreshButton
					.setToolTipText("Refresh the workspace classloader and task list");
			// addDefaultParameter.setLayoutData(gridData);
			refreshButton.addSelectionListener(new SelectionAdapter()
			{
				/**
				 * Add a list of default parameter to the parameter table
				 * 
				 * @generated NOT
				 */
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if (taskProvider != null)
					{
						cCombo.setEnabled(false);
						taskProvider.refresh();
						cCombo.setItems(getComboItems());
						sectionComposite.changed(new Control[] { cCombo });
						cCombo.setEnabled(true);
					}
				}
			});
			super.rightButton = refreshButton;
			addDisposable(refreshButton);

			oldButton.dispose();
			oldButton = null;

			sectionComposite.pack(true);
			sectionComposite.layout(true);
			sectionComposite.redraw();

		}

		// add the MetaInfo frame
		addUserTaskMetaInfoText();

		//
		// Add some listeners to the abstract combo
		//
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
					if (newText.endsWith(".class")) //$NON-NLS-1$
					{
						cCombo.add(newText);
						cCombo.setSelection(new Point(0, newText.length()));
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

		// GridData gridData = new
		// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// gridData.widthHint = 80;

		// Create and configure the "Add" button
		Button addDefaultParameterButton = new Button(sectionComposite,
				SWT.PUSH | SWT.CENTER);
		addDefaultParameterButton.setText("Add default Parameter");
		addDefaultParameterButton
				.setToolTipText("Adds default parameter for selected class at parameter tables end");
		// addDefaultParameter.setLayoutData(gridData);
		addDefaultParameterButton.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * Add a list of default parameter to the parameter table
			 * 
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

		// taskMetaInfoText = getWidgetFactory().createText(sectionComposite,
		// "", SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP );
		taskMetaInfoText = new StyledText(sectionComposite, SWT.READ_ONLY
				| SWT.MULTI | SWT.NO_SCROLL | SWT.WRAP );
		addDisposable(taskMetaInfoText);
		
		taskMetaInfoParameter = new StyledText(sectionComposite, SWT.READ_ONLY
				| SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER );
		addDisposable(taskMetaInfoParameter);

		if (sectionLayout instanceof GridLayout)
		{
			GridData labelData;
			
			labelData = new GridData();
			labelData.horizontalSpan = ((GridLayout) sectionLayout).numColumns;
			labelData.widthHint = 600;
			labelData.horizontalAlignment = SWT.FILL;
			labelData.heightHint = 45;
			taskMetaInfoText.setLayoutData(labelData);
			
			
			labelData = new GridData();
			labelData.horizontalSpan = ((GridLayout) sectionLayout).numColumns;
			labelData.widthHint = 600;
			labelData.horizontalAlignment = SWT.FILL;
			labelData.heightHint = 100;

			taskMetaInfoParameter.setLayoutData(labelData);
		}

		// set text layout options
		// since 3.6 :-(
		//taskMetaInfoText.setTabStops(new int[] { 30, 80, 120, 160 });

	}

	protected void updateTaskMetaInfo(String taskClassName)
	{

			createTaskMetaInfoStyledText(taskProvider
					.getTaskMetaInfo(taskClassName), taskMetaInfoText);
			
			createTaskMetaInfoStyledParameter(taskProvider
					.getTaskMetaInfo(taskClassName), taskMetaInfoParameter);

	}

	/**
	 * Setup the Description StyledText
	 * 
	 * @param taskMetaInfo
	 * @param textfield
	 */
	protected void createTaskMetaInfoStyledText(
			IEditorTaskMetaInfo taskMetaInfo, StyledText textfield)
	{		
		
		if (taskMetaInfo == null)
		{
			textfield.setText("");
			textfield.setStyleRanges(new StyleRange[0]);
			return;
		}
		
		StyledStringBuffer info = new StyledStringBuffer();

		String description = taskMetaInfo.getDescription() + "\n\n";
		StyleRange style = new StyleRange();
		style.fontStyle = SWT.BOLD | SWT.ITALIC;
		info.append(description, style);

		try {
			textfield.setText(info.toString());
			textfield.setStyleRanges(info.getStyleRanges());
		}
		catch (IllegalArgumentException iae)
		{
			iae.printStackTrace();
		}
	}
	
	/**
	 * Setup the Parameter StyledText
	 * 
	 * @param taskMetaInfo
	 * @param textfield
	 */
	protected void createTaskMetaInfoStyledParameter(
			IEditorTaskMetaInfo taskMetaInfo, StyledText textfield)
	{		
		
		if (taskMetaInfo == null)
		{
			textfield.setText("");
			textfield.setStyleRanges(new StyleRange[0]);
			return;
		}
		
		StyledStringBuffer info = new StyledStringBuffer();
		
		StyleRange style;

		style = new StyleRange();
		style.fontStyle = SWT.BOLD | SWT.ITALIC;
		style.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		
		IEditorParameterMetaInfo[] params = taskMetaInfo.getParameterMetaInfos();
		if (params != null)
		{
			for (int i = 0; i < params.length; i++)
			{
				
				info.append(PARAMETER, new StyleRange(style));
				info.append(params[i].getName() + "\n", null);
				
				info.append("\t" + DIRECTION, new StyleRange(style));
				info.append("\t" + params[i].getDirection() + "\n", null);
				
				info.append("\t" + CLASS, new StyleRange(style));
				info.append("\t\t" + params[i].getClazz().getName() + "\n", null);
				
				info.append("\t" + INITIAL_VALUE, new StyleRange(style));
				info.append("\t" + params[i].getInitialValue() + "\n", null);
				
				info.append("\t" + DESCRIPTION, new StyleRange(style));
				info.append("\t" + params[i].getDescription() + "\n", null);
	
				info.append("\n", null);
				
			}
		}

		try {
			textfield.setText(info.toString());
			textfield.setStyleRanges(info.getStyleRanges());
			
			// set the wrapped line indent
			// depends on the amount of \t in the generated string
			textfield.setWrapIndent(85);
		}
		catch (IllegalArgumentException iae)
		{
			iae.printStackTrace();
		}
	}
	
	
	

	protected void updateTaskParameterTable(String taskClassName)
	{
		IEditorTaskMetaInfo metaInfo = taskProvider
				.getTaskMetaInfo(taskClassName);
		IEditorParameterMetaInfo[] taskParameter = metaInfo
				.getParameterMetaInfos();

		MultiColumnTable parameterTable = JadexBpmnPropertiesUtil
				.getJadexEAnnotationTable(
						modelElement,
						JadexBpmnPropertiesUtil
								.getTableAnnotationIdentifier(
										JadexCommonParameterSection.PARAMETER_ANNOTATION_IDENTIFIER,
										JadexCommonParameterSection.PARAMETER_ANNOTATION_DETAIL_IDENTIFIER));

		if (parameterTable != null)
		{
			// TO DO: remove this BUGFIX in future versions?
			fixUniqueColumnIndexBugInCorruptedBpmnDiagrams(parameterTable,
					JadexCommonParameterSection.UNIQUE_PARAMETER_ROW_ATTRIBUTE);

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

		TableViewer viewer = JadexCommonParameterSection
				.getParameterTableViewerFor(modelElement);
		if (viewer != null)
		{
			viewer.refresh();
		}
	}

	/**
	 * Create a new Table for meta info
	 * 
	 * @param parameterMetaInfo
	 * @return
	 */
	protected MultiColumnTable createNewParameterTable(
			IEditorParameterMetaInfo[] parameterMetaInfo)
	{
		MultiColumnTable newTable = new MultiColumnTable(
				parameterMetaInfo.length,
				JadexCommonParameterSection.UNIQUE_PARAMETER_ROW_ATTRIBUTE);
		for (int i = 0; i < parameterMetaInfo.length; i++)
		{
			String[] columnValues = new String[] {
					parameterMetaInfo[i].getDirection(),
					parameterMetaInfo[i].getName(),
					parameterMetaInfo[i].getClazz().getName(),
					parameterMetaInfo[i].getInitialValue() };
			newTable.add(newTable.new MultiColumnTableRow(columnValues,
					newTable));

		}

		return newTable;
	}

//	/**
//	 * Update the table with meta info
//	 * 
//	 * @param table
//	 * @param metaInfo
//	 */
//	protected MultiColumnTable updateTaskParamterTable(MultiColumnTable table,
//			IEditorParameterMetaInfo[] metaInfo)
//	{
//		MultiColumnTable newTable = createNewParameterTable(metaInfo);
//		int typeIndex = AbstractParameterTablePropertySection
//				.getDefaultIndexForColumn(AbstractParameterTablePropertySection.TYPE_COLUMN);
//		int valueIndex = AbstractParameterTablePropertySection
//				.getDefaultIndexForColumn(AbstractParameterTablePropertySection.VALUE_COLUMN);
//
//		for (MultiColumnTableRow row : table.getRowList())
//		{
//			int rowIndex = newTable.indexOf(row);
//			if (rowIndex > -1)
//			{
//				MultiColumnTableRow newRow = newTable.get(rowIndex);
//
//				if (newRow.getColumnValueAt(typeIndex).equals(
//						row.getColumnValueAt(typeIndex)))
//				{
//					// types are equal (add the old value)
//					newRow.setColumnValueAt(valueIndex,
//							row.getColumnValueAt(valueIndex));
//				}
//
//			}
//		}
//
//		return newTable;
//	}

	/**
	 * Update the table with meta info
	 * 
	 * @param table
	 * @param metaInfo
	 */
	protected MultiColumnTable addTaskParamterTable(MultiColumnTable table,
			IEditorParameterMetaInfo[] metaInfo)
	{
		boolean hasUniqueValueChanged = false;
		
		MultiColumnTable newTable = createNewParameterTable(metaInfo);

		for (MultiColumnTableRow row : newTable.getRowList())
		{
			// save the original unique value
			String uniqueColumnValue = row.getColumnValueAt(row.getUniqueColumnIndex());
			table.add(row);
			if (!uniqueColumnValue.equals(row.getColumnValueAt(row.getUniqueColumnIndex())));
			{
				hasUniqueValueChanged = true;
			}
		}

		if (hasUniqueValueChanged)
		{
			displayUniqueValueChangedWarning();
		}
		
		return table;
	}

	/**
	 * Display a warning message that a unique 
	 * value has changed during add default
	 */
	private void displayUniqueValueChangedWarning()
	{
		Display.getCurrent().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				MessageDialog
						.openWarning(
								Display.getCurrent().getActiveShell(),
								"Unique value change",
								"During the \"Add Default\" action at least one added parameter was changed due to unique value restrictions. Please check the added parameter carefully");
			}
		});
	}
	
	// ---- BUGFIX METHOD ----

	

	/**
	 * Needed to fix legacy BPMN files corrupted through a bug
	 * 
	 * @param parameterTable
	 * @return
	 */
	private MultiColumnTable fixUniqueColumnIndexBugInCorruptedBpmnDiagrams(
			MultiColumnTable parameterTable, int correctUniqueColumnIndex)
	{
		if (parameterTable.getUniqueColumn() != correctUniqueColumnIndex)
		{
			List<MultiColumnTableRow> rows = parameterTable.getRowList();
			parameterTable = new MultiColumnTable(rows.size(),
					correctUniqueColumnIndex);
			for (MultiColumnTableRow multiColumnTableRow : rows)
			{
				parameterTable.add(multiColumnTableRow);
			}

		}
		return parameterTable;
	}

	// ---- internal used styled string buffer ----
	
	/**
	 * Internal used String buffer that use styles for 
	 * SWT {@link StyledText}
	 * 
	 * @author Claas
	 */
	class StyledStringBuffer{
		
		private int nextOffset;
		private StringBuffer buffer;
		private List<StyleRange> styleRanges;
		
		public StyledStringBuffer()
		{
			super();
			this.nextOffset = 0;
			this.buffer = new StringBuffer();
			this.styleRanges = new ArrayList<StyleRange>();
		}
		
		public int append(String string, StyleRange style)
		{
			if (string != null && !string.isEmpty())
			{
				
				int lastOffset = nextOffset;
				buffer.append(string);
				if (style == null)
				{
					style = new StyleRange();
				}
				style.start = nextOffset;
				style.length = string.length();
				styleRanges.add(style);
				
				nextOffset = lastOffset + style.length;
				//System.err.println("Add style range ("+style.start+":"+style.length+") -> " + nextOffset);

			}
			
			return nextOffset;
		}
		
		public String toString()
		{
			return buffer.toString();
		}
		
		public StyleRange[] getStyleRanges()
		{
			return styleRanges.toArray(new StyleRange[styleRanges.size()]);
		}
		
	}
	
}
