/**
 * 
 */
package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.ParameterizedVertex;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Claas
 *
 */
public abstract class AbstractGpmnTablePropertySection extends
		AbstractGpmnPropertySection
{

	/** The label string for the tableViewer */
	protected String tableViewerLabel;
	
	/** The viewer/editor */ 
	protected TableViewer tableViewer;
	
	/** The table add element button */
	protected Button addButton;
	
	/** The table delete element button */
	protected Button delButton;
	
	

	/**
	 * @param tableViewerLabel
	 */
	protected AbstractGpmnTablePropertySection(String tableViewerLabel)
	{
		super();
		this.tableViewerLabel = tableViewerLabel;
	}
	
	
	// ---- abstract methods ----
	
	
	protected abstract ModifyGpmnElementCommand getAddCommand();
	protected abstract ModifyGpmnElementCommand getDeleteCommand();
	protected abstract IStructuredContentProvider getTableContentProvider();
	
	// ---- overrides ----
	
	/**
	 * @see org.eclipse.gmf.runtime.diagram.ui.properties.sections.AbstractModelerPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		if (tableViewer != null)
		{
			tableViewer.getControl().dispose();
		}
		if (addButton != null)
		{
			addButton.dispose();
		}
		if (delButton != null)
		{
			delButton.dispose();
		}

		super.dispose();
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
	
	/**
	 * Creates the controls of the ContextProperty page section. Creates a table
	 * containing all ContextElements of the selected Context.
	 * 
	 * We use our own layout
	 * 
	 * @generated NOT
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage)
	{
		super.createControls(parent, tabbedPropertySheetPage);
		
		Group sectionGroup = getWidgetFactory().createGroup(sectionComposite, tableViewerLabel);
		sectionGroup.setLayout(new FillLayout(SWT.VERTICAL));
		createTableViewer(sectionGroup);
	}
	
	/**
	 * Creates the controls of the Parameter page section. Creates a table
	 * containing all Parameter of the selected {@link ParameterizedVertex}.
	 * 
	 * We use our own layout
	 * 
	 * @generated NOT
	 */
	protected TableViewer createTableViewer(Composite parent)
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

		setupTableLayout(viewer);

		viewer.setContentProvider(getTableContentProvider());
		
		// create cell modifier command
		setupTableNavigation(viewer);

		// create buttons
		createButtons(tableComposite);
		
		return tableViewer = viewer;

	}


	/**
	 * @param viewer
	 */
	protected void setupTableLayout(TableViewer viewer)
	{
		TableColumn[] columns = viewer.getTable().getColumns();
		int[] columnWeights = getColumnWeights(columns);

		Font tableFont = viewer.getTable().getFont();
		TableLayout tableLayout = new TableLayout();
		for (int columnIndex = 0; columnIndex < columns.length; columnIndex++)
		{
			tableLayout.addColumnData(new ColumnWeightData(columnWeights[columnIndex],
					FigureUtilities.getTextWidth(columns[columnIndex].getText(), tableFont), true));
		}
		viewer.getTable().setLayout(tableLayout);
	}
	
	/**
	 * Create the parameter edit table
	 * @param parent
	 * 
	 */
	protected TableViewer createTable(Composite parent, GridData tableLayoutData)
	{

		// the displayed table
		TableViewer viewer = new TableViewer(getWidgetFactory().createTable(parent,
				SWT.FULL_SELECTION | SWT.BORDER));

		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(tableLayoutData);

		createColumns(viewer);

		return viewer;
	}
	
	protected abstract void createColumns(TableViewer viewer);
	
	/**
	 * Create the cell modifier command to update {@link EAnnotation}
	 */
	private void setupTableNavigation(final TableViewer viewer)
	{
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer,new FocusCellOwnerDrawHighlighter(viewer));
		
		ColumnViewerEditorActivationStrategy editorActivationSupport = new ColumnViewerEditorActivationStrategy(
				viewer)
		{
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event)
			{
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						//|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
						;
			}
		};

		TableViewerEditor.create(viewer, focusCellManager, editorActivationSupport,
				TableViewerEditor.TABBING_HORIZONTAL | TableViewerEditor.TABBING_VERTICAL
						| TableViewerEditor.KEYBOARD_ACTIVATION
						| TableViewerEditor.KEEP_EDITOR_ON_DOUBLE_CLICK
						//| TableViewerEditor.TABBING_CYCLE_IN_ROW
						| TableViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
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
				ModifyGpmnElementCommand command = getAddCommand();
				
//				// modify the EModelElement annotation
//				ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
//						modelElement,
//						"Add EModelElement parameter element")
//				{
//					@Override
//					protected CommandResult doExecuteWithResult(
//							IProgressMonitor monitor, IAdaptable info)
//							throws ExecutionException
//					{
//						MultiColumnTableRow newRow = new MultiColumnTableRow(
//								defaultListElementAttributeValues);
//
//						HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
//						synchronized (uniqueValueCash)
//						{
//							List<MultiColumnTableRow> tableRows = getTableRowList();
//							String uniqueValue = createUniqueRowValue(newRow, tableRows);
//							newRow.columnValues[uniqueColumnIndex] = uniqueValue;
//							addUniqueRowValue(uniqueValue);
//							tableRows.add(newRow);
//							updateTableRowList(tableRows);
//						}
//
//						return CommandResult.newOKCommandResult(newRow);
//					}
//				};
				try
				{
					command.execute(null, null);
					
					refresh();
					refreshSelectedEditPart();
				}
				catch (ExecutionException ex)
				{
					GpmnDiagramEditorPlugin.getInstance().getLog().log(
							new Status(IStatus.ERROR, GpmnDiagramEditorPlugin.ID,
									IStatus.ERROR, ex.getMessage(), ex));
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
				
				ModifyGpmnElementCommand command = getDeleteCommand();
				
//				// modify the EModelElement annotation
//				ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
//						modelElement,
//						"Delete EModelElement parameter element")
//				{
//					@Override
//					protected CommandResult doExecuteWithResult(
//							IProgressMonitor monitor, IAdaptable info)
//							throws ExecutionException
//					{
//						HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
//						synchronized (uniqueValueCash)
//						{
//							MultiColumnTableRow rowToRemove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
//									.getSelection()).getFirstElement();
//							
//							List<MultiColumnTableRow> tableRowList = getTableRowList();
//							//modelElementUniqueColumnValueCash.remove(rowToRemove.columnValues[uniqueColumnIndex]);
//							uniqueValueCash.remove(rowToRemove.columnValues[uniqueColumnIndex]);
//							tableRowList.remove(rowToRemove);
//							updateTableRowList(tableRowList);
//						}
//
//						return CommandResult.newOKCommandResult(null);
//					}
//				};
				try
				{
					command.execute(null, null);
					
					refresh();
					refreshSelectedEditPart();
				}
				catch (ExecutionException ex)
				{
					GpmnDiagramEditorPlugin.getInstance().getLog().log(
							new Status(IStatus.ERROR, GpmnDiagramEditorPlugin.ID,
									IStatus.ERROR, ex.getMessage(), ex));
				}
			}
		});
		
		delButton = delete;
	}
	
	
	protected int[] getColumnWeights(TableColumn[] columns)
	{
		int[] weights = new int[columns.length];
		for (int i = 0; i < weights.length; i++)
		{
			weights[i] = 1;
		}
		return weights;
	}
	
}
