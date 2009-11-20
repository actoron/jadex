/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.JadexBpmnDiagramMessages;
import jadex.tools.bpmn.editor.JadexBpmnEditor;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.xml.type.internal.RegEx.RegularExpression;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.ui.services.util.CommonLabelProvider;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditorPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public class JadexUserTaskActivityPropertySection extends AbstractPropertySection
{

	// ---- constants ----
	
	/**
	 * the name column label
	 */
	private final static String NAME_COLUMN = "Name"; //$NON-NLS-1$
	
	/**
	 * the type column label
	 */
	private final static String TYPE_COLUMN = "Type"; //$NON-NLS-1$
	
	/**
	 * the value column label
	 */
	private final static String VALUE_COLUMN = "Value"; //$NON-NLS-1$
	
	/**
	 * the direction column label
	 */
	private final static String DIRECTION_COLUMN = "Direction"; //$NON-NLS-1$
	
	/**
	 * parameter direction values 
	 */
	public final static String[] DIRECTION_VALUES = new String[] {"inout", "in", "out"};
	
	/**
	 * default parameter direction
	 */
	public final static String DIRECTION_DEFAULT = "inout";
	
	
	
	// ---- attributes ----

	/** The Combo for implementing class */
	private CCombo classImplCombo;
	
	/** The viewer/editor for parameter */ 
	private TableViewer tableViewer;
	
	/** The table add element button */
	private Button addButton;
	
	/** The table delete element button */
	private Button delButton;

	/** The activity (task) that holds task implementation class and parameters, may be null. */
	private Activity activity;

	// ---- methods ----

	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		GridLayout layout = new GridLayout(2, true);
		parent.setLayout(layout);

		
		createTaskClassComposite(parent);

		createEmptyComposite(parent);
		
		createParameterTableComposite(parent);
		
		createEmptyComposite(parent);

		
	}

	
	/**
	 * Manages the input.
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection)
		{
			Object unknownInput = ((IStructuredSelection) selection)
					.getFirstElement();
			if (unknownInput instanceof IGraphicalEditPart
					&& (((IGraphicalEditPart) unknownInput)
							.resolveSemanticElement() != null))
			{
				unknownInput = ((IGraphicalEditPart) unknownInput)
						.resolveSemanticElement();
			}
			if (unknownInput instanceof Activity)
			{
				Activity act = (Activity) unknownInput;
				updateControls(act);
				
				activity = (Activity) act;
				return;
			}
		}
		
		// fall through
		activity = null;
		classImplCombo.setText("");
		classImplCombo.setEnabled(false);
		
		tableViewer.setInput(null);
		addButton.setEnabled(false);
		delButton.setEnabled(false);
	}

	/**
	 * Update the controls for this property section with values from {@link EAnnotation}
	 * @param act
	 */
	protected void updateControls(Activity act)
	{
		// update the class values
		String[] predefinedItems = getClassCompositeItemsForActivity(act);
		classImplCombo.setItems(predefinedItems);
		
		EAnnotation ea = act.getEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
		if (ea != null)
		{
			String value = (String) ea.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS);
			int valueIndex = -1;
			
			// search value in items
			String[] items = classImplCombo.getItems();
			for (int i = 0; i < items.length; i++)
			{
				if(items[i].equals(value))
				{
					valueIndex = i;
				}
			}
			
			// add the value to the items list
			if (valueIndex == -1 )
			{
				classImplCombo.add(value, 0);
				valueIndex = 0;
			}
			
			classImplCombo.select(0);
			tableViewer.setInput(act);
			
		}
		
		classImplCombo.setEnabled(true);
		addButton.setEnabled(true);
		delButton.setEnabled(true);
		
	}
	
	/**
	 * Update 
	 * @param key
	 * @param value
	 */
	private void updateActivtyEAnnotation(final String key, final String value)
	{
		// we can only update an activity
		if(activity == null)
		{
			return;
		}
		
		
		// create the TransactionalCommand
		ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
				activity, JadexBpmnDiagramMessages.ActivityParamterListSection_update_eannotation_command_name)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor arg0, IAdaptable arg1)
					throws ExecutionException
			{
				EAnnotation annotation = activity.getEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
				if (annotation == null)
				{
					annotation = EcoreFactory.eINSTANCE.createEAnnotation();
					annotation.setSource(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
					annotation.setEModelElement(activity);
					annotation.getDetails().put(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS, "");
					annotation.getDetails().put(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST, "");
				}
				
				annotation.getDetails().put(key, value);
				
				return CommandResult.newOKCommandResult();
			}
		};
		// execute command
		try
		{
			command.execute(new NullProgressMonitor(), null);
		}
		catch (ExecutionException exception)
		{
			JadexBpmnPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, JadexBpmnPlugin.PLUGIN_ID,
							IStatus.ERROR, exception.getMessage(),
							exception));
		}
	}

	// ---- control creation methods ----
	
	/**
	 * Dummy method for empty composites
	 */
	protected Composite createEmptyComposite(Composite parent)
	{
		Composite newComposite = getWidgetFactory().createComposite(parent/*, SWT.BORDER*/);
		
		// The layout of the composite
		GridLayout layout = new GridLayout(1, false);
		newComposite.setLayout(layout);
		
		//getWidgetFactory().createCLabel(newComposite, "---- empty composite ----");
		
		return newComposite;
	}
	
	/**
	 * Create a combo for task class selection in parent
	 *  
	 * @param parent
	 */
	protected Composite createTaskClassComposite(Composite parent)
	{
		Composite taskComposite = getWidgetFactory().createComposite(parent/*, SWT.BORDER*/);
		
		// The layout of the task composite
		GridLayout layout = new GridLayout(1, false);
		taskComposite.setLayout(layout);
		
		getWidgetFactory().createCLabel(taskComposite, JadexBpmnDiagramMessages.ActivityParameterListSection_ImplementationClass_label);

		final CCombo combo = getWidgetFactory().createCCombo(taskComposite, SWT.NONE);
		
		GridData data = new GridData(SWT.FILL);
		data.minimumWidth = 400;
		data.widthHint = 400;
		combo.setLayoutData(data);
		
		
		String[] items = getClassCompositeItemsForActivity(activity);
		combo.setItems(items);
		combo.setText(combo.getItem(0));
		combo.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String text = combo.getText();
				String newText = text.substring(0, e.start) + e.text
						+ text.substring(e.end);
				
				// don't allow non word characters
				RegularExpression re = new RegularExpression("\\w*");
				if (!re.matches(newText))
				{
					e.doit = false;
				}
			}
		});
		combo.addTraverseListener(new TraverseListener()
		{
			public void keyTraversed(TraverseEvent e)
			{
				if (e.detail == SWT.TRAVERSE_RETURN)
				{
					e.doit = false;
					e.detail = SWT.TRAVERSE_NONE;
					String newText = combo.getText();

					// check if we have a valid class name
					if (newText.endsWith(".class"))
					{
						combo.add(newText);
						combo.setSelection(new Point(0, newText
								.length()));
					}

				}
			}
		});
		
		combo.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				updateActivtyEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS, combo.getText());
			}
		});
		
		return classImplCombo = combo;
	}
	
	/**
	 * Create the string array for class composite
	 * @param act
	 * @return
	 */
	protected String[] getClassCompositeItemsForActivity(Activity act)
	{
		// FIXME: use real class names or get from runtime!
		return new String[] { 
				"Test.class", 
				"SomeTask.class", 
				"MessageTask.class", 
				"OneMoreTestTask.class" };
	}
	
	/**
	 * Creates the controls of the Parameter page section. Creates a table
	 * containing all Parameter of the selected {@link ParameterizedVertex}.
	 * 
	 * We use our own layout
	 * 
	 * @generated NOT
	 */
	protected TableViewer createParameterTableComposite(Composite parent)
	{
		Composite tableComposite = getWidgetFactory().createComposite(parent/*, SWT.BORDER*/);

		// The layout of the table composite
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 5;
		tableComposite.setLayout(layout);
		
		GridData gridData = new GridData(GridData.FILL_BOTH
				| GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 200;
		gridData.heightHint = 200;
		tableComposite.setLayoutData(gridData);
		
		GridData tableLayoutData = new GridData(GridData.FILL_BOTH);
		tableLayoutData.grabExcessHorizontalSpace = true;
		tableLayoutData.grabExcessVerticalSpace = true;
		tableLayoutData.horizontalSpan = 3;

		// create the table
		getWidgetFactory().createLabel(tableComposite, JadexBpmnDiagramMessages.ActivityParameterListSection_ParameterTable_label);
		TableViewer viewer = createTable(tableComposite, tableLayoutData);

		// create cell modifier command
		createCellModifier(viewer);

		// create buttons
		createButtons(tableComposite);
		
		return tableViewer = viewer;

	}
	
	/**
	 * Create the parameter edit table
	 * @param parent
	 * 
	 */
	private TableViewer createTable(Composite parent, GridData tableLayoutData)
	{
		String[] columns = new String[] { NAME_COLUMN, TYPE_COLUMN,
				VALUE_COLUMN, DIRECTION_COLUMN };
		int[] columnWeight = new int[] { 2, 2, 4, 1 };

		// the displayed table
		TableViewer viewer = new TableViewer(getWidgetFactory().createTable(parent,
				SWT.SINGLE | /*SWT.H_SCROLL | SWT.V_SCROLL |*/ SWT.FULL_SELECTION | SWT.BORDER));

		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);

		viewer.getTable().setLayoutData(tableLayoutData);

		Font tableFont = viewer.getTable().getFont();
		TableLayout tableLayout = new TableLayout();
		for (int i = 0; i < columns.length; i++)
		{
			TableColumn column = new TableColumn(viewer.getTable(),
					SWT.LEFT);
			column.setText(columns[i]);

			tableLayout.addColumnData(new ColumnWeightData(columnWeight[i],
					FigureUtilities.getTextWidth(columns[i], tableFont), true));
		}
		viewer.getTable().setLayout(tableLayout);

		viewer.setContentProvider(new ParameterListContentProvider());
		viewer.setLabelProvider(new ParameterListLabelProvider());
		viewer.setColumnProperties(columns);

		return viewer;
	}
	
	/**
	 * Create the cell modifier command to update {@link EAnnotation}
	 */
	private void createCellModifier(TableViewer viewer)
	{
		TableViewerEditor.create(viewer,
				new ColumnViewerEditorActivationStrategy(viewer),
				TableViewerEditor.KEEP_EDITOR_ON_DOUBLE_CLICK
						| TableViewerEditor.TABBING_HORIZONTAL
						| TableViewerEditor.TABBING_CYCLE_IN_ROW);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[] {
				new TextCellEditor(viewer.getTable()), // name (text)
				new TextCellEditor(viewer.getTable()), // type
				new TextCellEditor(viewer.getTable()), // value
				new ComboBoxCellEditor(
						viewer.getTable(),
						DIRECTION_VALUES, 
						SWT.READ_ONLY) // direction
		};
		viewer.setCellEditors(editors);

		// create the modify command
		viewer.setCellModifier(new ICellModifier()
		{
			/**
			 * Can modify all columns if model element exist. [Can only modify
			 * the column named NAME else.]
			 * @generated NOT
			 */
			public boolean canModify(Object element, String property)
			{
				if (element instanceof TaskParameter)
				{
					return true;
				}
				return false;
			}

			/**
			 * @return the value of the property for the given element.
			 * @generated NOT
			 */
			public Object getValue(Object element, String property)
			{
				if (element instanceof TaskParameter)
				{
					TaskParameter param = (TaskParameter) element;
					if (NAME_COLUMN.equals(property))
					{
						return param.getName();
					}

					if (TYPE_COLUMN.equals(property))
					{
						return param.getType();
					}

					if (VALUE_COLUMN.equals(property))
					{
						return param.getValue();
					}

					if (DIRECTION_COLUMN.equals(property))
					{
						String value = param.getDirection();
						for (int i = 0; i < DIRECTION_VALUES.length; i++)
						{
							if (DIRECTION_VALUES[i].equals(value))
							{
								return new Integer(i);
							}
						}
						
						// fall through
						return new Integer(0);
					}

				}
				// fall through
				return null;
			}

			/**
			 * modifies the value of the TaskParameter according to the value
			 * given by the CellEditor.
			 */
			public void modify(Object element, String property,
					final Object value)
			{

				if (element instanceof TableItem)
				{

					if ((((TableItem) element).getData()) instanceof TaskParameter)
					{
						final TaskParameter param = (TaskParameter) ((TableItem) element)
								.getData();
						final String fproperty = property;

						// modify the element itself
						if (value != null)
						{
							// modify the Parameter
							ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
									activity,
									JadexBpmnDiagramMessages.ActivityParamterListSection_update_command_name)
							{
								@Override
								protected CommandResult doExecuteWithResult(
										IProgressMonitor monitor,
										IAdaptable info)
										throws ExecutionException
								{
									
									List params = getTaskParameterList();
									TaskParameter paramToChange = (TaskParameter) params.get(params.indexOf(param));

									if (NAME_COLUMN.equals(fproperty))
									{
										paramToChange.setName((String) value);
									}
									else if (TYPE_COLUMN.equals(fproperty))
									{
										paramToChange.setType((String) value);
									}
									else if (VALUE_COLUMN.equals(fproperty))
									{
										paramToChange.setValue((String) value);
									}
									else if (DIRECTION_COLUMN.equals(fproperty))
									{
										if (value instanceof Integer)
										{
											paramToChange
													.setDirection(DIRECTION_VALUES[((Integer) value)
																	.intValue()]);
										}
									}
									else
									{
										throw new UnsupportedOperationException(
												"Invalid edit column");
									}

									updateTaskParameterList(params);
									
									return CommandResult.newOKCommandResult();
								}
							};
							try
							{
								command.execute(null, null);
								refresh();
								refreshSelection();
							}
							catch (ExecutionException e)
							{
								BpmnDiagramEditorPlugin
										.getInstance()
										.getLog()
										.log(
												new Status(
														IStatus.ERROR,
														JadexBpmnEditor.ID,
														IStatus.ERROR, e
																.getMessage(),
														e));
							}
						}

					}
				}

			}
		});
	}
	
	/**
	 * Create the Add and Delete button
	 * @param parent
	 * @generated NOT
	 */
	private void createButtons(Composite parent)
	{

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;

		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Add");
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter()
		{
			/** 
			 * Add a ContextElement to the Context and refresh the view
			 * @generated NOT 
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// modify the Activity annotation
				ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
						activity,
						JadexBpmnDiagramMessages.ActivityParamterListSection_add_command_name)
				{
					@Override
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						TaskParameter newElement = new TaskParameter("newParameter", "Object", "null", DIRECTION_DEFAULT);

						List params = getTaskParameterList();
						params.add(newElement);
						updateTaskParameterList(params);
						
						return CommandResult.newOKCommandResult(newElement);
					}
				};
				try
				{
					command.execute(null, null);
					refresh();
					refreshSelection();
				}
				catch (ExecutionException ex)
				{
					BpmnDiagramEditorPlugin.getInstance().getLog().log(
							new Status(IStatus.ERROR,
									JadexBpmnEditor.ID, IStatus.ERROR,
									ex.getMessage(), ex));
				}
			}
		});
		addButton = add;

		// Create and configure the "Delete" button
		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText("Delete");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		delete.setLayoutData(gridData);
		delete.addSelectionListener(new SelectionAdapter()
		{
			/** 
			 * Remove selected ContextElement from the Context and refresh the view
			 * @generated NOT 
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// modify the Activity annotation
				ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
						activity,
						JadexBpmnDiagramMessages.ActivityParamterListSection_delete_command_name)
				{
					@Override
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						TaskParameter element = (TaskParameter) ((IStructuredSelection) tableViewer
								.getSelection()).getFirstElement();
						
						List params = getTaskParameterList();
						params.remove(element);
						updateTaskParameterList(params);
						
						return CommandResult.newOKCommandResult(null);
					}
				};
				try
				{
					command.execute(null, null);
					refresh();
					refreshSelection();
				}
				catch (ExecutionException ex)
				{
					BpmnDiagramEditorPlugin.getInstance().getLog().log(
							new Status(IStatus.ERROR,
									JadexBpmnEditor.ID, IStatus.ERROR,
									ex.getMessage(), ex));
				}
			}
		});
		delButton = delete;
	}
	
	// ---- converter and help methods ----
	
	/**
	 * Refreshes the graphical selection after a modify operation.
	 * 
	 * @generated NOT
	 */
	private void refreshSelection()
	{
		if (getSelection() instanceof IStructuredSelection)
		{
			IStructuredSelection sel = (IStructuredSelection) getSelection();
			if (null != sel)
				for (Object selElt : sel.toList())
				{
					if (selElt instanceof EditPart)
					{
						final EditPart part = (EditPart) selElt;
						Display.getCurrent().asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								part.refresh();
							}
						});
					}
					
					//if (selElt instanceof EditPart)
					//{
					//	((EditPart) selElt).refresh();
					//}
				}
		}
	}
	
	/**
	 * Retrieve the EAnnotation from the activity and converts it to a {@link TaskParameter} list
	 * @param act
	 * @return
	 */
	private List<TaskParameter> getTaskParameterList()
	{
		EAnnotation ea = activity.getEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
		if (ea != null)
		{
			String value = (String) ea.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST);
			return convertTaskParameterString(value);
		}
		
		return new ArrayList<TaskParameter>(0);
	}
	
	/**
	 * Updates the EAnnotation for the activity task parameter list
	 * @param params
	 */
	private void updateTaskParameterList(List<TaskParameter> params)
	{
		updateActivtyEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST, convertTaskParameterList(params));
		
		// HACK? Should use notification?
		Display.getCurrent().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				tableViewer.refresh();
			}
		});
		
	}
	
	
	/**
	 * Convert a string representation of a TaskParameter list into a TaskParameter list 
	 * @param stringToConvert
	 * @return
	 */
	protected List<TaskParameter> convertTaskParameterString(String stringToConvert)
	{
		StringTokenizer listTokens = new StringTokenizer(stringToConvert, TaskParameter.LIST_ELEMENT_DELIMITER);
		List<TaskParameter> params = new ArrayList<TaskParameter>(listTokens.countTokens());
		int i = 0;
		while (listTokens.hasMoreTokens())
		{
			String paramElement = listTokens.nextToken();
			StringTokenizer paramTokens = new StringTokenizer(paramElement,TaskParameter.PARAMETER_ELEMENT_DELIMITER);
			// require 4 tokens: name, type, value, direction
			if(paramTokens.countTokens() == 4)
			{
				String name = paramTokens.nextToken();
				String type = paramTokens.nextToken();
				String value = paramTokens.nextToken();
				String direction = paramTokens.nextToken();
				params.add(new TaskParameter(name, type, value, direction));
			}
			else
			{
				BpmnDiagramEditorPlugin.getInstance().getLog().log(
						new Status(IStatus.ERROR,
								JadexBpmnEditor.ID, IStatus.ERROR,
								JadexBpmnDiagramMessages.ActivityParameterListSection_WrongElementDelimiter_message + " \""+paramElement+"\"", null));
			}
			// update token index
			i++;
		}
		return params;
	}
	
	/**
	 * Convert a list of TaskParameter into a string representation
	 * @param arrayToConvert
	 * @return
	 */
	protected String convertTaskParameterList(List<TaskParameter> params)
	{
		StringBuffer buffer = new StringBuffer();
		for (TaskParameter taskParameter : params)
		{
			if (buffer.length() != 0)
			{
				buffer.append(TaskParameter.LIST_ELEMENT_DELIMITER);
			}
			//buffer.append(TaskParameter.PARAMETER_ELEMENT_DELEMITER);
			buffer.append(taskParameter.getName());
			buffer.append(TaskParameter.PARAMETER_ELEMENT_DELIMITER);
			buffer.append(taskParameter.getType());
			buffer.append(TaskParameter.PARAMETER_ELEMENT_DELIMITER);
			buffer.append(taskParameter.getValue());
			buffer.append(TaskParameter.PARAMETER_ELEMENT_DELIMITER);
			buffer.append(taskParameter.getDirection());
		}

		return buffer.toString();
	}
	
	// ---- internal used classes ----
	
	/**
	 * Simple content provider that reflects the ContextElemnts 
	 * of an Context given as an input. Marked as dynamic / static.
	 */
	private class ParameterListContentProvider implements IStructuredContentProvider {

		/**
		 * Generate the content for the table.
		 * 
		 * @return Object[] that contains TaskParameter objects.
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Activity)
			{
				EAnnotation ea = ((Activity)inputElement).getEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
				inputElement = ea;
			}
			
			if (inputElement instanceof EAnnotation)
			{
				String parameterListString = ((EAnnotation) inputElement).getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST);
				return convertTaskParameterString(parameterListString).toArray();
			}
			return new Object[] { null };
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
			// nothing to dispose.
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			// no actions taken.
		}

	}
	
	/**
	 * Label provider in charge of rendering the keys and values of the annotation
	 * attached to the object. Currently based on CommonLabelProvider.
	 */
	protected class ParameterListLabelProvider extends CommonLabelProvider
			implements ITableLabelProvider
	{

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			return super.getImage(element);
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof TaskParameter)
			{
				TaskParameter param = (TaskParameter) element;
				switch (columnIndex)
				{
				case 0:
					return param.getName();
				case 1:
					return param.getType();
				case 2:
					return param.getValue();
				case 3:
					return param.getDirection();
				
				default:
					return super.getText(param);
				}
			}
			return super.getText(element);
		}
		
	}
	
	/**
	 * Internal representation of a jadex runtime TaskParameter
	 * 
	 * @author Claas Altschaffel
	 */
	protected class TaskParameter {
		
		// ---- constants ----
		
		public static final String LIST_ELEMENT_DELIMITER = "<*>";
		
		public static final String PARAMETER_ELEMENT_DELIMITER = "#|#";
		
		// ---- attributes ----
		
		private String name;
		private String type;
		private String value;
		private String direction;
		
		// ---- constructors ----
		
		/** default constructor */
		public TaskParameter(String name, String type, String value,
				String direction)
		{
			super();
			
			assert name != null;
			assert type != null;
			assert value != null;
			assert direction != null;
			
			this.name = name;
			this.type = type;
			this.value = value;
			this.direction = direction;
		}

		// ---- overrides ----
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof TaskParameter))
			{
				return false;
			}
			
			return name.equals(((TaskParameter) obj).getName())
					&& type.equals(((TaskParameter) obj).getType())
					&& value.equals(((TaskParameter) obj).getValue())
					&& direction.equals(((TaskParameter) obj).getDirection());
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return name.hashCode() * 31
					+ type.hashCode() * 31
					+ value.hashCode() * 31
					+ direction.hashCode() * 31;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return name + PARAMETER_ELEMENT_DELIMITER + type
					+ PARAMETER_ELEMENT_DELIMITER + value
					+ PARAMETER_ELEMENT_DELIMITER + direction;
		}
		
		// ---- getter / setter ----

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		

		/**
		 * @param name the name to set
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * @return the type
		 */
		public String getType()
		{
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(String type)
		{
			this.type = type;
		}

		/**
		 * @return the value
		 */
		public String getValue()
		{
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value)
		{
			this.value = value;
		}

		/**
		 * @return the direction
		 */
		public String getDirection()
		{
			return direction;
		}

		/**
		 * @param direction the direction to set
		 */
		public void setDirection(String direction)
		{
			this.direction = direction;
		}

	}
	
	
	
}


