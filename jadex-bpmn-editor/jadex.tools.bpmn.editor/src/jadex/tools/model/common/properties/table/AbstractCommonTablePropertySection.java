/**
 * 
 */
package jadex.tools.model.common.properties.table;

import jadex.tools.bpmn.editor.JadexBpmnEditor;
import jadex.tools.model.common.properties.AbstractCommonPropertySection;
import jadex.tools.model.common.properties.ModifyEObjectCommand;
import jadex.tools.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.gmf.runtime.draw2d.ui.figures.FigureUtilities;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Claas
 *
 */
public abstract class AbstractCommonTablePropertySection extends
		AbstractCommonPropertySection
{

	/** The label string for the tableViewer */
	protected String tableViewerLabel;
	
	/** The viewer/editor */ 
	protected TableViewer tableViewer;
	
	/** The table add element button */
	protected Button addButton;
	
	/** The table delete element button */
	protected Button delButton;
	
	/** The table up button */
	protected Button upButton;
	
	/** The table down button */
	protected Button downButton;
	
	/** The table clear button */
	protected Button clearButton;

	/**
	 * @param tableViewerLabel
	 */
	protected AbstractCommonTablePropertySection(String tableViewerLabel)
	{
		super();
		this.tableViewerLabel = tableViewerLabel;
	}
	
	
	// ---- abstract methods ----
	
	
	protected abstract ModifyEObjectCommand getAddCommand();
	protected abstract ModifyEObjectCommand getDeleteCommand();
	protected abstract ModifyEObjectCommand getUpCommand();
	protected abstract ModifyEObjectCommand getDownCommand();
	protected abstract ModifyEObjectCommand getClearCommand();
	protected abstract IStructuredContentProvider getTableContentProvider();
	
	/**
	 * Abstract method to create the table columns
	 * @param viewer
	 */
	protected abstract void createColumns(TableViewer viewer);
	
	// ---- overrides ----
	
	/**
	 * @see org.eclipse.gmf.runtime.diagram.ui.properties.sections.AbstractModelerPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		// dispose of buttons is handled in superclass via control list
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
			upButton.setEnabled(true);
			downButton.setEnabled(true);
			clearButton.setEnabled(true);
			return;
		}

		// fall through
		tableViewer.setInput(null);
		addButton.setEnabled(false);
		delButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		clearButton.setEnabled(false);
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
		GridLayout layout = new GridLayout(6, false);
		tableComposite.setLayout(layout);
		
		GridData tableLayoutData = new GridData(GridData.FILL_BOTH);
		tableLayoutData.grabExcessHorizontalSpace = true;
		tableLayoutData.grabExcessVerticalSpace = true;
		tableLayoutData.minimumHeight = 150;
		tableLayoutData.heightHint = 150;
		tableLayoutData.horizontalSpan = 6;

		// create the table
		TableViewer viewer = createTable(tableComposite, tableLayoutData);
		setupTableLayout(viewer);
		
		// get content provider from implementation class
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

		controls.add(viewer.getControl());
		
		return viewer;
	}
	
	
	
	/**
	 * Create the cell modifier command to update {@link EAnnotation}
	 */
	private void setupTableNavigation(final TableViewer viewer)
	{
//		CellNavigationStrategy naviStrat = new CellNavigationStrategy();
//
//				// from Snippet059CellNavigationIn33 
//				TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(
//						viewer, new FocusCellOwnerDrawHighlighter(viewer));
//				try
//				{
//					Field f = focusCellManager.getClass().getSuperclass().getDeclaredField("navigationStrategy");
//					f.setAccessible(true);
//					f.set(focusCellManager, naviStrat);
//				}
//				catch (SecurityException e)
//				{
//					e.printStackTrace();
//				}
//				catch (NoSuchFieldException e)
//				{
//					e.printStackTrace();
//				}
//				catch (IllegalArgumentException e)
//				{
//					e.printStackTrace();
//				}
//				catch (IllegalAccessException e)
//				{
//					e.printStackTrace();
//				}
		
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
		
//		viewer.getColumnViewerEditor().addEditorActivationListener(
//				new ColumnViewerEditorActivationListener() {
//
//					public void afterEditorActivated(
//							ColumnViewerEditorActivationEvent event) {
//
//					}
//
//					public void afterEditorDeactivated(
//							ColumnViewerEditorDeactivationEvent event) {
//
//					}
//
//					public void beforeEditorActivated(
//							ColumnViewerEditorActivationEvent event) {
//						ViewerCell cell = (ViewerCell) event.getSource();
//						viewer.getTable().showColumn(
//								viewer.getTable().getColumn(cell.getColumnIndex()));
//					}
//
//					public void beforeEditorDeactivated(
//							ColumnViewerEditorDeactivationEvent event) {
//
//					}
//
//				});
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
		add.addSelectionListener(new TableButtonSelectionAdapter()
		{
			@Override
			protected ModifyEObjectCommand getButtonCommand()
			{
				return getAddCommand();
			}
		});
		addButton = add;
		controls.add(add);

		// Create and configure the "Delete" button
		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText("Delete");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		delete.setLayoutData(gridData);
		delete.addSelectionListener(new TableButtonSelectionAdapter()
		{
			
			@Override
			protected ModifyEObjectCommand getButtonCommand()
			{
				return getDeleteCommand();
			}
		});
		delButton = delete;
		controls.add(delete);
		
		// Create and configure the "UP / Down" buttons3
		Button up = new Button(parent, SWT.PUSH | SWT.CENTER);
		up.setText("Up");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 40;
		up.setLayoutData(gridData);
		up.addSelectionListener(new TableButtonSelectionAdapter()
		{
			@Override
			protected ModifyEObjectCommand getButtonCommand()
			{
				return getUpCommand();
			}
		});
		upButton = up;
		controls.add(up);
		
		Button down = new Button(parent, SWT.PUSH | SWT.CENTER);
		down.setText("Down");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 40;
		down.setLayoutData(gridData);
		down.addSelectionListener(new TableButtonSelectionAdapter()
		{
			@Override
			protected ModifyEObjectCommand getButtonCommand()
			{
				return getDownCommand();
			}
		});
		downButton = down;
		controls.add(down);
		
		Button clear = new Button(parent, SWT.PUSH | SWT.CENTER);
		clear.setText("Clear");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 40;
		clear.setLayoutData(gridData);
		clear.addSelectionListener(new TableButtonSelectionAdapter()
		{
			@Override
			protected ModifyEObjectCommand getButtonCommand()
			{
				return getClearCommand();
			}
		});
		clearButton = clear;
		controls.add(clear);
	}
	
	/**
	 * Default column weights implementation (same column size)
	 * @param columns
	 * @return int[] with column size
	 */
	protected int[] getColumnWeights(TableColumn[] columns)
	{
		int[] weights = new int[columns.length];
		for (int i = 0; i < weights.length; i++)
		{
			weights[i] = 1;
		}
		return weights;
	}
	
	// ---- internal used classes ----
	
	/**
	 * Label provider in charge of rendering the keys and columnValues of the annotation
	 * attached to the object. Currently based on CommonLabelProvider.
	 */
	protected class MultiColumnTableLabelProvider extends ColumnLabelProvider
			implements ITableLabelProvider
	{
	
		// ---- attributes ----
		
		/** 
		 * The index for this column
		 */
		private int columIndex;
		
		// ---- constructors ----
		
		/**
		 * empty constructor, sets column index to -1
		 */
		public MultiColumnTableLabelProvider()
		{
			super();
			this.columIndex = -1;
		}
		
		/**
		 * @param columIndex the column index to provide the label for
		 */
		public MultiColumnTableLabelProvider(int columIndex)
		{
			super();
			assert columIndex >= 0 : "column index < 0";
			this.columIndex = columIndex;
		}
		
		// ---- ColumnLabelProvider overrides ----
		
		@Override
		public Image getImage(Object element)
		{
			if (columIndex >= 0)
			{
				return this.getColumnImage(element, columIndex);
			}
			else
			{
				return super.getImage(element);
			}
		}
	
		@Override
		public String getText(Object element)
		{
			if (columIndex >= 0)
			{
				return this.getColumnText(element, columIndex);
			}
			else
			{
				return super.getText(element);
			}
		}
		
		// ---- ITableLabelProvider implementation ----
		
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
				return ((MultiColumnTableRow) element).getColumnValues()[columnIndex];
			}
			return super.getText(element);
		}
		
	}
	
	/**
	 * Abstract EditingSupport in charge of edit the table 
	 * cell and update the corresponding model
	 * 
	 * @author Claas
	 *
	 */
	protected abstract class MultiColumnTableEditingSupport extends EditingSupport {
		
		protected CellEditor editor;
		protected int attributeIndex;

		protected MultiColumnTableEditingSupport(TableViewer viewer, int attributeIndex)
		{
			this(viewer, attributeIndex, new TextCellEditor(viewer.getTable()));
		}
		
		protected MultiColumnTableEditingSupport(TableViewer viewer, int attributeIndex, CellEditor editor)
		{
			super(viewer);
			this.editor = editor;
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
			return ((MultiColumnTableRow) element).getColumnValues()[attributeIndex];
		}
		
		/**
		 * Create the transactional command to update the model
		 * @param tableViewerRow
		 * @param value
		 * @return command to execute
		 */
		protected abstract ModifyEObjectCommand getSetValueCommand(final MultiColumnTableRow tableViewerRow, final Object value);
		
		/**
		 * Update the element value with transactional command
		 * @param element to update
		 * @param value to set
		 */
		protected void doSetValue(Object element, Object value)
		{
			
			final MultiColumnTableRow tableViewerRow = (MultiColumnTableRow) element;
			final String newValue = value.toString();

			// modify the Model
			ModifyEObjectCommand command = getSetValueCommand(tableViewerRow, newValue);
			
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

		}
	}
	
	/**
	 * Helper to ease the command execution for Buttons
	 * @author Claas
	 *
	 */
	protected abstract class TableButtonSelectionAdapter extends SelectionAdapter
	{
		/**
		 * Have to be overridden in implementations to configure the button action
		 * @return command to execute on button click
		 */
		protected abstract ModifyEObjectCommand getButtonCommand();
		
		/** 
		 * Get the execute command, execute it and refresh the view
		 * @generated NOT 
		 */
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			ModifyEObjectCommand command = getButtonCommand();
			if (command != null)
			{
				try
				{
					command.execute(null, null);

					refresh();
					refreshSelectedEditPart();
				}
				catch (ExecutionException ex)
				{
					BpmnDiagramEditorPlugin.getInstance().getLog().log(
							new Status(IStatus.ERROR, JadexBpmnEditor.ID,
									IStatus.ERROR, ex.getMessage(), ex));
				}
			}
		}
	}
	
}
