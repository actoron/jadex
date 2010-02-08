/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;
import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public abstract class AbstractMultiColumnTablePropertySection extends AbstractJadexPropertySection
{
	
	// ---- attributes ----
	
	/** The viewer/editor for parameter */ 
	private TableViewer tableViewer;
	
	/** The table add element button */
	private Button addButton;
	
	/** The table delete element button */
	private Button delButton;
	
	/** The label string for the tableViewer */
	private String tableViewerLabel;
	
	/** the column names */
	private String[] columnNames;
	
	/** the column weights */
	private int[] columsWeight;
	
	private String[] defaultListElementAttributeValues;
	
	private int uniqueColumnIndex;
	
	private HashSet<String> uniqueColumnValueCash;

	// ---- constructor ----
	
	/**
	 * Protected constructor for subclasses
	 * @param containerEAnnotationName @see {@link AbstractJadexPropertySection}
	 * @param annotationDetailName @see {@link AbstractJadexPropertySection}
	 * @param tableLabel the name of table
	 * @param columns the column of table
	 * @param columnsWeight the weight of columns
	 * @param defaultListElementAttributeValues default columnValues for new elements, may be <code>null</code>
	 */
	protected AbstractMultiColumnTablePropertySection(String containerEAnnotationName, String annotationDetailName, String tableLabel, String[] columns, int[] columnsWeight, String[] defaultListElementAttributeValues, int uniqueColumnIndex)
	{
		super(containerEAnnotationName, annotationDetailName);

		// FIXME: allow -1 as unique column index to support no unique column?
		
		assert (columns != null && columnsWeight != null);
		assert (columns.length == columnsWeight.length);
		assert (uniqueColumnIndex != -1 && uniqueColumnIndex < columns.length);
		
		this.tableViewerLabel = tableLabel;
		this.columsWeight = columnsWeight;
		this.columnNames = columns;
		
		this.uniqueColumnIndex = uniqueColumnIndex;
		this.uniqueColumnValueCash = new HashSet<String>();
		
		if (defaultListElementAttributeValues != null)
		{
			assert (columns.length == defaultListElementAttributeValues.length);
			this.defaultListElementAttributeValues = defaultListElementAttributeValues;
		}
		else
		{
			this.defaultListElementAttributeValues = columns;
		}
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

		// fall through
		tableViewer.setInput(null);
		addButton.setEnabled(false);
		delButton.setEnabled(false);
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh()
	{
		super.refresh();
		if (tableViewer != null && modelElement != null)				
		{
			tableViewer.refresh();
		}
		
	}

	
	// ---- control creation methods ----

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
		tableComposite.setLayout(layout);
		
		GridData tableLayoutData = new GridData(GridData.FILL_BOTH);
		tableLayoutData.grabExcessHorizontalSpace = true;
		tableLayoutData.grabExcessVerticalSpace = true;
		tableLayoutData.minimumHeight = 150;
		tableLayoutData.heightHint = 150;
		tableLayoutData.horizontalSpan = 3;

		// create the table
		TableViewer viewer = createTable(tableComposite, tableLayoutData);

		// create cell modifier command
		setupTableNavigation(viewer);

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
		String[] columns = columnNames;
		int[] weight = columsWeight;

		// the displayed table
		TableViewer viewer = new TableViewer(getWidgetFactory().createTable(parent,
				SWT.FULL_SELECTION | SWT.BORDER));

		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(tableLayoutData);

		Font tableFont = viewer.getTable().getFont();
		TableLayout tableLayout = new TableLayout();
		for (int columnIndex = 0; columnIndex < columns.length; columnIndex++)
		{
			TableViewerColumn column = new TableViewerColumn(viewer,
					SWT.LEFT);
			column.getColumn().setText(columns[columnIndex]);

			column.setEditingSupport(new MultiColumnTableEditingSupport(viewer, columnIndex));

//			column.setLabelProvider(new ColumnLabelProvider() {
//
//				public String getText(Object element) {
//					return ((Person) element).email;
//				}
//
//			});
			

			
			tableLayout.addColumnData(new ColumnWeightData(weight[columnIndex],
					FigureUtilities.getTextWidth(columns[columnIndex], tableFont), true));
		}
		viewer.getTable().setLayout(tableLayout);

		viewer.setContentProvider(new MultiColumnTableContentProvider());
		// FIX ME: use column specific label provider (see above)
		viewer.setLabelProvider(new MultiColumnTableLabelProvider());
		viewer.setColumnProperties(columns);

		return viewer;
	}
	
	/**
	 * Create the cell modifier command to update {@link EAnnotation}
	 */
	private void setupTableNavigation(TableViewer viewer)
	{
		
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
				viewer)
		{
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event)
			{
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED /*&& event.keyCode == SWT.CR*/)
						;
			}
		};

		TableViewerEditor.create(viewer, actSupport,
				TableViewerEditor.TABBING_HORIZONTAL
						| TableViewerEditor.KEYBOARD_ACTIVATION
						| TableViewerEditor.KEEP_EDITOR_ON_DOUBLE_CLICK
						| TableViewerEditor.TABBING_CYCLE_IN_ROW
						);
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
						MultiColumnTableRow newRow = new MultiColumnTableRow(
								defaultListElementAttributeValues);

						synchronized (uniqueColumnValueCash)
						{
							List tableRows = getTableRowList();
							String uniqueValue = createUniqueRowValue(newRow, tableRows);
							newRow.columnValues[uniqueColumnIndex] = uniqueValue;
							addUniqueRowValue(uniqueValue);
							tableRows.add(newRow);
							updateTableRowList(tableRows);
						}

						return CommandResult.newOKCommandResult(newRow);
					}
				};
				try
				{
					command.execute(null, null);
					
					refresh();
					refreshSelectedEditPart();
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
						synchronized (uniqueColumnValueCash)
						{
							MultiColumnTableRow rowToRemove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
									.getSelection()).getFirstElement();
							
							List tableRowList = getTableRowList();
							uniqueColumnValueCash.remove(rowToRemove.columnValues[uniqueColumnIndex]);
							tableRowList.remove(rowToRemove);
							updateTableRowList(tableRowList);
						}

						return CommandResult.newOKCommandResult(null);
					}
				};
				try
				{
					command.execute(null, null);
					
					refresh();
					refreshSelectedEditPart();
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

	
	private String createUniqueRowValue(MultiColumnTableRow row, List<MultiColumnTableRow> table)
	{
		assert (row != null && table != null);
		
		synchronized (uniqueColumnValueCash)
		{
			String uniqueColumnValue = row.columnValues[uniqueColumnIndex];

			int counter = 1;
			String uniqueValueToUse = uniqueColumnValue;
			while (uniqueColumnValueCash.contains(uniqueValueToUse))
			{
				uniqueValueToUse = uniqueColumnValue + counter;
				counter++;
			}

			return uniqueValueToUse;
		}

	}
	
	private boolean addUniqueRowValue(String uniqueValue)
	{
		synchronized (uniqueColumnValueCash)
		{
			return uniqueColumnValueCash.add(uniqueValue);
		}
	}
	
	private boolean removeUniqueRowValue(String uniqueValue)
	{
		synchronized (uniqueColumnValueCash)
		{
			return uniqueColumnValueCash.remove(uniqueValue);
		}
	}
	
	/**
	 * Retrieve the EAnnotation from the modelElement and converts it to a {@link MultiColumnTableRow} list
	 * @param act
	 * @return
	 */
	private List<MultiColumnTableRow> getTableRowList()
	{
		EAnnotation ea = modelElement.getEAnnotation(containerEAnnotationName);
		if (ea != null)
		{
			String value = (String) ea.getDetails().get(annotationDetailName);
			if (value != null)
			{
				return convertParameterString(value);
			}
		}

		return new ArrayList<MultiColumnTableRow>(0);
	}
	
	/**
	 * Updates the EAnnotation for the modelElement task parameter list
	 * @param params
	 */
	private void updateTableRowList(List<MultiColumnTableRow> params)
	{
		updateJadexEAnnotation(annotationDetailName, convertParameterList(params));
	}
	
	
	/**
	 * Convert a string representation of a MultiColumnTableRow list into a
	 * MultiColumnTableRow list
	 * 
	 * @param stringToConvert
	 * @return
	 */
	protected List<MultiColumnTableRow> convertParameterString(
			String stringToConvert)
	{
		StringTokenizer listTokens = new StringTokenizer(stringToConvert, JadexCommonPropertySection.LIST_ELEMENT_DELIMITER);
		List<MultiColumnTableRow> tableRowList = new ArrayList<MultiColumnTableRow>(listTokens.countTokens());
		while (listTokens.hasMoreTokens())
		{
			String parameterElement = listTokens.nextToken();
			StringTokenizer parameterTokens = new StringTokenizer(
					parameterElement,
					JadexCommonPropertySection.LIST_ELEMENT_ATTRIBUTE_DELIMITER,
					true);

			// number of columns is the index that will be used.
			// initialize array with empty strings because we 
			// don't want have check the values after
			String[] attributes = new String[columnNames.length];
			for (int index = 0; index < attributes.length; index++)
			{
				attributes[index] = attributes[index] != null ? attributes[index] : "";
			}
			
			int attributeIndexCounter = 0;	
			String lastToken = null;

			while (parameterTokens.hasMoreTokens())
			{
				String attributeToken = parameterTokens.nextToken();

				if (!attributeToken.equals(LIST_ELEMENT_ATTRIBUTE_DELIMITER))
				{
					attributes[attributeIndexCounter] = attributeToken;
					attributeIndexCounter++;
				}
				// we found a delimiter
				else
				{
					if (lastToken == null)
					{
						// we found a delimiter at the first position
						// add empty string
						attributes[attributeIndexCounter] = "";
						attributeIndexCounter++;
					}
					else if (!parameterTokens.hasMoreTokens())
					{
						// we found a delimiter at the last position, 
						// add empty string
						attributes[attributeIndexCounter] = "";
						attributeIndexCounter++;
					}
					else if (attributeToken.equals(lastToken))
					{
						// we found two delimiter without any content between,
						// add empty string
						attributes[attributeIndexCounter] = "";
						attributeIndexCounter++;
					}
					
				}

				// remember last token
				lastToken = attributeToken;

			} // end while paramTokens

			MultiColumnTableRow newRow = new MultiColumnTableRow(attributes);
			addUniqueRowValue(newRow.getColumnValueAt(uniqueColumnIndex));
			tableRowList.add(newRow);

		} // end while listTokens
		
		return tableRowList;
	}
	
	/**
	 * Convert a list of MultiColumnTableRow into a string representation using  
	 * <code>LIST_ELEMENT_DELIMITER</code> from {@link AbstractJadexPropertySection}
	 * as delimiter
	 * @param arrayToConvert
	 * @return
	 */
	protected String convertParameterList(List<MultiColumnTableRow> params)
	{
		StringBuffer buffer = new StringBuffer();
		for (MultiColumnTableRow multiColumnTableRow : params)
		{
			if (buffer.length() != 0)
			{
				buffer.append(LIST_ELEMENT_DELIMITER);
			}

			buffer.append(convertRowToString(multiColumnTableRow));
		}
		return buffer.toString();
	}
	
	/**
	 * Convert a row of the table to a String representation using 
	 * <code>LIST_ELEMENT_ATTRIBUTE_DELIMITER</code> from 
	 * {@link AbstractJadexPropertySection} as delimiter
	 * @param row to convert
	 * @return String representation of row
	 */
	protected String convertRowToString(MultiColumnTableRow row)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < row.columnValues.length; i++)
		{
			buffer.append(row.columnValues[i]);
			buffer.append(LIST_ELEMENT_ATTRIBUTE_DELIMITER);
		}
		// remove last delimiter
		buffer.delete(buffer.length()
				- LIST_ELEMENT_ATTRIBUTE_DELIMITER.length(), buffer.length());
		//System.out.println(buffer.toString());
		return buffer.toString();
	}
	
	// ---- internal used model classes ----
	
	/**
	 * Simple content provider that reflects the ContextElements 
	 * of an Context given as an input. Marked as dynamic / static.
	 */
	protected class MultiColumnTableContentProvider implements IStructuredContentProvider {

		/**
		 * Generate the content for the table.
		 * 
		 * @return Object[] that contains MultiColumnTableRow objects.
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
				if(parameterListString!=null)
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
	 * Label provider in charge of rendering the keys and columnValues of the annotation
	 * attached to the object. Currently based on CommonLabelProvider.
	 */
	protected class MultiColumnTableLabelProvider extends CommonLabelProvider
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
			if (element instanceof MultiColumnTableRow)
			{
				return ((MultiColumnTableRow) element).columnValues[columnIndex];
			}
			return super.getText(element);
		}
		
	}
	
	/**
	 * Internal representation of a jadex runtime MultiColumnTableRow
	 * 
	 * @author Claas Altschaffel
	 */
	protected class MultiColumnTableRow {
		
		// ---- attributes ----

		private String[] columnValues;
		
		// ---- constructors ----
		
		/** default constructor */
		public MultiColumnTableRow(String[] columnValues)
		{
			super();
			
			this.columnValues = new String[columnValues.length];
			for (int i = 0; i < columnValues.length; i++)
			{
				assert columnValues[i] != null;
				this.columnValues[i] = new String(columnValues[i]);
			}
			
			//this.columnValues = columnValues;
			
		}

		// ---- overrides ----
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof MultiColumnTableRow))
			{
				return false;
			}
			
			boolean returnValue = true;
			for (int i = 0; returnValue && i < this.columnValues.length; i++)
			{
				returnValue =  returnValue &&  this.columnValues[i].equals(((MultiColumnTableRow) obj).columnValues[i]);
			}
			return returnValue;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			int returnHash = 31;
			for (int i = 0; i < this.columnValues.length; i++)
			{
				returnHash = returnHash + this.columnValues[i].hashCode() * 31;
			}
			return returnHash;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append("MultiColumnTableRow(");
			for (int i = 0; i < this.columnValues.length; i++)
			{
				buffer.append("`");
				buffer.append(columnValues[i]);
				buffer.append("´" + ", ");
			}
			// remove last delimiter
			buffer.delete(buffer.length()-", ".length(), buffer.length());
			buffer.append(")");
			System.out.println(buffer.toString());
			return buffer.toString();
		}
		
//		/**
//		 * @see java.lang.Object#toString()
//		 */
//		@Override
//		public String toString()
//		{
//			StringBuffer buffer = new StringBuffer();
//			for (int i = 0; i < this.columnValues.length; i++)
//			{
//				buffer.append(columnValues[i]);
//				buffer.append(LIST_ELEMENT_ATTRIBUTE_DELIMITER);
//			}
//			// remove last delimiter
//			buffer.delete(buffer.length()-LIST_ELEMENT_ATTRIBUTE_DELIMITER.length(), buffer.length());
//			System.out.println(buffer.toString());
//			return buffer.toString();
//		}
		
		// ---- getter / setter ----
		
		/**
		 * @return the columnValues
		 */
		public String[] getColumnValues()
		{
			return columnValues;
		}

		/**
		 * @param columnValues the columnValues to set
		 */
		public void setColumnValues(String[] values)
		{
			this.columnValues = values;
		}

		/**
		 * @param columnIndex to get value
		 * @return the value at index
		 */
		public String getColumnValueAt(int columnIndex)
		{
			return columnValues[columnIndex];
		}

		/**
		 * @param columnIndex to set the value
		 * @param value the value to set
		 * 
		 */
		public void setColumnValueAt(int columnIndex, String value)
		{
			this.columnValues[columnIndex] = value;
		}

	}
	
	protected class MultiColumnTableEditingSupport extends EditingSupport {
		
		private CellEditor editor;
		private int attributeIndex;

		public MultiColumnTableEditingSupport(TableViewer viewer, int attributeIndex)
		{
			super(viewer);
			this.editor = new TextCellEditor(viewer.getTable());
			this.attributeIndex = attributeIndex;
		}

		/**
		 * Can edit all columns.
		 * @generated NOT
		 */
		public boolean canEdit(Object element)
		{
			if (element instanceof MultiColumnTableRow)
			{
				return true;
			}
			return false;
		}
		
		protected CellEditor getCellEditor(Object element)
		{
			return editor;
		}

		protected void setValue(Object element, Object value)
		{
			doSetValue(element, value);
			// refresh the table viewer element
			getViewer().update(element, null);
			// refresh the graphical edit part
			refreshSelectedEditPart();
		}

		/**
		 * Get the value for atttributeIndex
		 * @param element to get the value from
		 */
		protected Object getValue(Object element)
		{
			return ((MultiColumnTableRow) element).columnValues[attributeIndex];
		}
		
		/**
		 * Update the element value with transactional command
		 * @param element to update
		 * @param value to set
		 */
		protected void doSetValue(Object element, Object value)
		{
			
			final MultiColumnTableRow editRow = (MultiColumnTableRow) element;
			final String newValue = value.toString();
			
			// modify the Model
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

					List tableRowList = getTableRowList();
					MultiColumnTableRow rowToEdit = (MultiColumnTableRow) tableRowList.get(tableRowList.indexOf(editRow));

					
					
					if (attributeIndex == uniqueColumnIndex)
					{
						if (!newValue.equals(rowToEdit.columnValues[attributeIndex]))
						{
							synchronized (uniqueColumnValueCash)
							{
								removeUniqueRowValue(rowToEdit.columnValues[uniqueColumnIndex]);
								
								rowToEdit.columnValues[attributeIndex] = newValue;
								String newUniqueValue = createUniqueRowValue(rowToEdit, tableRowList);
								rowToEdit.columnValues[attributeIndex] = newUniqueValue;
								
								addUniqueRowValue(newUniqueValue);
								
								updateTableRowList(tableRowList);

								// update the corresponding table element
								editRow.columnValues[attributeIndex] = newUniqueValue;
							}

							return CommandResult.newOKCommandResult();
						}
					}
					
					rowToEdit.columnValues[attributeIndex] = newValue;
					updateTableRowList(tableRowList);
					
					// update the corresponding table element
					editRow.columnValues[attributeIndex] = newValue;
										
					return CommandResult.newOKCommandResult();
				}
			};
			
			try
			{
				command.setReuseParentTransaction(true);
				command.execute(null, null);
				
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

			// update the corresponding table element
			// don't use this!
			//((MultiColumnTableRow) element).columnValues[attributeIndex] = newValue;

		}
	}

	
}


