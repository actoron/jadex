/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;
import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.ui.services.util.CommonLabelProvider;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditorPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public abstract class Abstract1ColumnTablePropertySection extends AbstractJadexPropertySection
{

	// ---- constants ----
	
	/**
	 * the value column label
	 */
	private final static String DEFAULT_COLUMN = "Value"; // //$NON-NLS-1$
	

	
	// ---- attributes ----

	/** The viewer/editor for parameter */ 
	private TableViewer tableViewer;
	
	/** The table add element button */
	private Button addButton;
	
	/** The table delete element button */
	private Button delButton;
	
	/** The label string for the tableViewer */
	private String tableViewerLabel;
	
	/** The name for the column */
	private String tableColumnName; 

	
	// ---- constructor ----
	
	/**
	 * Protected constructor for subclasses
	 * @param containerEAnnotationName the {@link EAnnotation} that holds this parameter table
	 */
	protected Abstract1ColumnTablePropertySection(String containerEAnnotationName, String annotationDetailName, String tableLabel, String tableColumnName)
	{
		super(containerEAnnotationName, annotationDetailName);
		this.tableViewerLabel = tableLabel;
		this.tableColumnName = tableColumnName != null ? tableColumnName : DEFAULT_COLUMN;
	}


	// ---- methods ----

	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Group sectionGroup = getWidgetFactory().createGroup(sectionComposite, tableViewerLabel);
		sectionGroup.setLayout(new FillLayout(SWT.VERTICAL));
		createParameterTableComposite(sectionGroup);
	}

	
	/**
	 * Manages the input.
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		
		if (modelElement != null)
		{
			tableViewer.setInput(modelElement);
			addButton.setEnabled(true);
			delButton.setEnabled(true);

			return;
		}
		
		tableViewer.setInput(null);
		addButton.setEnabled(false);
		delButton.setEnabled(false);
	}

	

	// ---- control creation methods ----

	/**
	 * Creates the controls of the Parameter page section. Creates a table
	 * containing all Parameter of the selected {@link ParameterizedVertex}.
	 * 
	 * @generated NOT
	 */
	protected TableViewer createParameterTableComposite(Composite parent)
	{
		Composite tableComposite = getWidgetFactory().createComposite(parent/*, SWT.BORDER*/);

		// The layout of the table composite
		GridLayout compositeLayout = new GridLayout(3, false);
		tableComposite.setLayout(compositeLayout);

		// the layout of the table in table composite
		GridData tableLayoutData = new GridData(GridData.FILL_BOTH);
		tableLayoutData.grabExcessHorizontalSpace = true;
		tableLayoutData.grabExcessVerticalSpace = true;
		tableLayoutData.minimumHeight = 150;
		tableLayoutData.heightHint = 150;
		tableLayoutData.horizontalSpan = 3;

		// create the table
		//getWidgetFactory().createLabel(tableComposite, tableViewerLabel);
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
		String[] columns = new String[] { tableColumnName };
		int[] columnWeight = new int[] { 1 };

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

		viewer.setContentProvider(new GeneralParameterListContentProvider());
		viewer.setLabelProvider(new GeneralParameterListLabelProvider());
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
				new TextCellEditor(viewer.getTable()) // value
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
				if (element instanceof GeneralParameter)
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
				if (element instanceof GeneralParameter)
				{
					GeneralParameter param = (GeneralParameter) element;

					if (tableColumnName.equals(property))
					{
						return param.getValue();
					}
				}
				// fall through
				return null;
			}

			/**
			 * modifies the value of the GeneralParameter according to the value
			 * given by the CellEditor.
			 */
			public void modify(Object element, String property,
					final Object value)
			{

				if (element instanceof TableItem)
				{

					if ((((TableItem) element).getData()) instanceof GeneralParameter)
					{
						final GeneralParameter param = (GeneralParameter) ((TableItem) element)
								.getData();
						final String fproperty = property;

						// modify the element itself
						if (value != null)
						{
							// modify the Parameter
							ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
									modelElement,
									"Update EModelElement parameter list")
							{
								@Override
								protected CommandResult doExecuteWithResult(
										IProgressMonitor monitor,
										IAdaptable info)
										throws ExecutionException
								{
									
									List params = getParameterList();
									GeneralParameter paramToChange = (GeneralParameter) params.get(params.indexOf(param));

									if (tableColumnName.equals(fproperty))
									{
										paramToChange.setValue((String) value);
									}
									else
									{
										throw new UnsupportedOperationException(
												Messages.JadexCommonPropertySection_InvalidEditColumn_Message);
									}

									updateParameterList(params);
									
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
		add.setText(Messages.JadexCommonPropertySection_ButtonAdd_Label);
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
				// modify the EModelElement annotation
				ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
						modelElement,
						"Add EModelElement parameter element")
				{
					@Override
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						GeneralParameter newElement = new GeneralParameter("new value");

						List params = getParameterList();
						params.add(newElement);
						updateParameterList(params);
						
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
		delete.setText(Messages.JadexCommonPropertySection_ButtonDelete_Label);
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
				// modify the EModelElement annotation
				ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
						modelElement,
						"Delete EModelElement parameter element")
				{
					@Override
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						GeneralParameter element = (GeneralParameter) ((IStructuredSelection) tableViewer
								.getSelection()).getFirstElement();
						
						List params = getParameterList();
						params.remove(element);
						updateParameterList(params);
						
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
	 * Retrieve the EAnnotation from the modelElement and converts it to a {@link GeneralParameter} list
	 * @param act
	 * @return
	 */
	private List<GeneralParameter> getParameterList()
	{
		EAnnotation ea = modelElement.getEAnnotation(containerEAnnotationName);
		if (ea != null)
		{
			String value = (String) ea.getDetails().get(annotationDetailName);
			return convertParameterString(value);
		}
		
		return new ArrayList<GeneralParameter>(0);
	}
	
	/**
	 * Updates the EAnnotation for the modelElement task parameter list
	 * @param params
	 */
	private void updateParameterList(List<GeneralParameter> params)
	{
		updateJadexEAnnotation(annotationDetailName, convertParameterList(params));
		
		// HACK? Should use notification?
		Display.getCurrent().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (tableViewer != null && modelElement != null)				
				{
					tableViewer.refresh();
				}
			}
		});
		
	}
	
	
	/**
	 * Convert a string representation of a GeneralParameter list into a GeneralParameter list 
	 * @param stringToConvert
	 * @return
	 */
	protected List<GeneralParameter> convertParameterString(String stringToConvert)
	{
		StringTokenizer listTokens = new StringTokenizer(stringToConvert, LIST_ELEMENT_DELIMITER);
		List<GeneralParameter> params = new ArrayList<GeneralParameter>(listTokens.countTokens());
		int i = 0;
		while (listTokens.hasMoreTokens())
		{
			String paramElement = listTokens.nextToken();
			params.add(new GeneralParameter(paramElement));
			
			// update token index
			i++;
		}
		return params;
	}
	
	/**
	 * Convert a list of GeneralParameter into a string representation
	 * @param arrayToConvert
	 * @return
	 */
	protected String convertParameterList(List<GeneralParameter> params)
	{
		StringBuffer buffer = new StringBuffer();
		for (GeneralParameter generalParameter : params)
		{
			if (buffer.length() != 0)
			{
				buffer.append(LIST_ELEMENT_DELIMITER);
			}

			buffer.append(generalParameter.getValue());

		}

		return buffer.toString();
	}
	
	// ---- internal used model classes ----
	
	/**
	 * Simple content provider that reflects the ContextElemnts 
	 * of an Context given as an input. Marked as dynamic / static.
	 */
	protected class GeneralParameterListContentProvider implements IStructuredContentProvider {

		/**
		 * Generate the content for the table.
		 * 
		 * @return Object[] that contains GeneralParameter objects.
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof EModelElement)
			{
				EAnnotation ea = ((EModelElement)inputElement).getEAnnotation(containerEAnnotationName);
				inputElement = ea;
			}
			
			if (inputElement instanceof EAnnotation)
			{
				String parameterListString = ((EAnnotation) inputElement).getDetails().get(annotationDetailName);
				if(parameterListString != null)
					return convertParameterString(parameterListString).toArray();
			}
			
			return new Object[] {};
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
	protected class GeneralParameterListLabelProvider extends CommonLabelProvider
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
			if (element instanceof GeneralParameter)
			{
				GeneralParameter param = (GeneralParameter) element;
				switch (columnIndex)
				{
				case 0:
					return param.getValue();
				
				default:
					return super.getText(param);
				}
			}
			return super.getText(element);
		}
		
	}
	
	/**
	 * Internal representation of a jadex runtime GeneralParameter
	 * 
	 * @author Claas Altschaffel
	 */
	protected class GeneralParameter {
		
		// ---- attributes ----
		
		private String value;
		
		// ---- constructors ----
		
		/** default constructor */
		public GeneralParameter(String value)
		{
			super();
			
			assert value != null;
			
			this.value = value;
		}

		// ---- overrides ----
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof GeneralParameter))
			{
				return false;
			}
			
			return value.equals(((GeneralParameter) obj).getValue());
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return value.hashCode();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return value;
		}
		
		// ---- getter / setter ----

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

	}

}


