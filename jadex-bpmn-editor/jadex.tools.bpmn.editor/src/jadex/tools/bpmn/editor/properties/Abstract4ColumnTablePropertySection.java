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
public abstract class Abstract4ColumnTablePropertySection extends AbstractJadexPropertySection
{

	// ---- constants ----
	
//	/**
//	 * the firstAttribute column label
//	 */
//	private final static String DEFAULT_FIRST_COLUMN = "First"; // //$NON-NLS-1$
//	
//	/**
//	 * the secondAttribute column label
//	 */
//	private final static String DEFAULT_SECOND_COLUMN = "Second"; // //$NON-NLS-1$
//	
//	/**
//	 * the secondAttribute column label
//	 */
//	private final static String DEFAULT_THIRD_COLUMN = "Third"; // //$NON-NLS-1$
//	
//	/**
//	 * the secondAttribute column label
//	 */
//	private final static String DEFAULT_FOURTH_COLUMN = "Fourth"; // //$NON-NLS-1$

	protected final static int[] DEFAULT_COLUMN_WEIGHT = new int[] { 1, 1, 1, 8 };
	
	protected final static String[] DEFAULT_PARAMETER_VALUES = new String[]{ "first", "second", "third", "fourth" };
	
	// ---- attributes ----
	
	/** The viewer/editor for parameter */ 
	private TableViewer tableViewer;
	
	/** The table add element button */
	private Button addButton;
	
	/** The table delete element button */
	private Button delButton;
	
	/** The label string for the tableViewer */
	private String tableViewerLabel;
	
//	/** the first column label */
//	private String firstColumn; 
//	
//	/** the second column label */
//	private String secondColumn; 
//	
//	/** the third column label */
//	private String thirdColumn; 
//	
//	/** the fourth column label */
//	private String fourthColumn; 
	
	private String[] columnNames;
	
	/** the columns weight */
	private int[] columsWeight;
	
	private String[] defaultListElementAttributeValues;
	
	// ---- constructor ----
	
	/**
	 * Protected constructor for subclasses
	 * @param containerEAnnotationName the {@link EAnnotation} that holds this parameter table
	 */
	protected Abstract4ColumnTablePropertySection(String containerEAnnotationName, String annotationDetailName, String tableLabel, String[] columns, int[] columnsWeight, String[] defaultListElementAttributeValues)
	{
		super(containerEAnnotationName, annotationDetailName);
		this.containerEAnnotationName = containerEAnnotationName;
		this.annotationDetailName = annotationDetailName;
		this.tableViewerLabel = tableLabel;
		
		if (columns != null && columnsWeight != null)
		{
			assert (columns.length == 4) && (columnsWeight.length == 4);
//			this.firstColumn = columns[0];
//			this.secondColumn = columns[1];
//			this.thirdColumn = columns[2];
//			this.fourthColumn = columns[3];
			this.columsWeight = columnsWeight;
			this.columnNames = columns;
			this.defaultListElementAttributeValues = defaultListElementAttributeValues;
		}
		else
		{
//			this.firstColumn = DEFAULT_FIRST_COLUMN;
//			this.secondColumn = DEFAULT_SECOND_COLUMN;
//			this.thirdColumn = DEFAULT_THIRD_COLUMN;
//			this.fourthColumn = DEFAULT_FOURTH_COLUMN;
			this.columsWeight = DEFAULT_COLUMN_WEIGHT;
			this.defaultListElementAttributeValues = DEFAULT_PARAMETER_VALUES;
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

			column.setEditingSupport(new General4ParameterEditingSupport(viewer, columnIndex));

			tableLayout.addColumnData(new ColumnWeightData(weight[columnIndex],
					FigureUtilities.getTextWidth(columns[columnIndex], tableFont), true));
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
		
//		TableViewerEditor.create(viewer,
//				new ColumnViewerEditorActivationStrategy(viewer),
//				TableViewerEditor.KEEP_EDITOR_ON_DOUBLE_CLICK
//						| TableViewerEditor.TABBING_HORIZONTAL
//						| TableViewerEditor.TABBING_CYCLE_IN_ROW);
//
////		// Create the cell editors
////		CellEditor[] editors = new CellEditor[] {
////				new TextCellEditor(viewer.getTable()), // firstAttribute (text)
////				new TextCellEditor(viewer.getTable()) // secondAttribute
////		};
//		// Create the cell editors
//		CellEditor[] editors = new CellEditor[columnNames.length];
//		for (int i = 0; i < editors.length; i++)
//		{
//			editors[i] = new TextCellEditor(viewer.getTable());
//		}
//		
//		viewer.setCellEditors(editors);
//
//		// create the modify command
//		viewer.setCellModifier(new ICellModifier()
//		{
//			/**
//			 * Can modify all columns if model element exist. [Can only modify
//			 * the column named NAME else.]
//			 * @generated NOT
//			 */
//			public boolean canModify(Object element, String property)
//			{
//				if (element instanceof General4Parameter)
//				{
//					return true;
//				}
//				return false;
//			}
//
//			/**
//			 * @return the secondAttribute of the property for the given element.
//			 * @generated NOT
//			 */
//			public Object getValue(Object element, String property)
//			{
//				if (element instanceof General4Parameter)
//				{
//					General4Parameter param = (General4Parameter) element;
//					if (firstColumn.equals(property))
//					{
//						return param.getFirstAttribute();
//					}
//
//					if (secondColumn.equals(property))
//					{
//						return param.getSecondAttribute();
//					}
//					
//					if (thirdColumn.equals(property))
//					{
//						return param.getThirdAttribute();
//					}
//					
//					if (fourthColumn.equals(property))
//					{
//						return param.getFourthAttribute();
//					}
//				}
//				// fall through
//				return null;
//			}
//
//			/**
//			 * modifies the secondAttribute of the General4Parameter according to the secondAttribute
//			 * given by the CellEditor.
//			 */
//			public void modify(Object element, String property,
//					final Object value)
//			{
//
//				if (element instanceof TableItem)
//				{
//
//					if ((((TableItem) element).getData()) instanceof General4Parameter)
//					{
//						final General4Parameter param = (General4Parameter) ((TableItem) element)
//								.getData();
//						final String fproperty = property;
//
//						// modify the element itself
//						if (value != null)
//						{
//							// modify the Parameter
//							ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
//									modelElement,
//									"Update EModelElement parameter list")
//							{
//								@Override
//								protected CommandResult doExecuteWithResult(
//										IProgressMonitor monitor,
//										IAdaptable info)
//										throws ExecutionException
//								{
//									
//									List params = getParameterList();
//									General4Parameter paramToChange = (General4Parameter) params.get(params.indexOf(param));
//
//									if (firstColumn.equals(fproperty))
//									{
//										paramToChange.setFirstAttribute((String) value);
//									}
//									else if (secondColumn.equals(fproperty))
//									{
//										paramToChange.setSecondAttribute((String) value);
//									}
//									else if (thirdColumn.equals(fproperty))
//									{
//										paramToChange.setThirdAttribute((String) value);
//									}
//									else if (fourthColumn.equals(fproperty))
//									{
//										paramToChange.setFourthAttribute((String) value);
//									}
//									else
//									{
//										throw new UnsupportedOperationException(
//												Messages.JadexCommonPropertySection_InvalidEditColumn_Message);
//									}
//
//									updateParameterList(params);
//									
//									return CommandResult.newOKCommandResult();
//								}
//							};
//							try
//							{
//								command.execute(null, null);
//								refresh();
//								refreshSelection();
//							}
//							catch (ExecutionException e)
//							{
//								BpmnDiagramEditorPlugin
//										.getInstance()
//										.getLog()
//										.log(
//												new Status(
//														IStatus.ERROR,
//														JadexBpmnEditor.ID,
//														IStatus.ERROR, e
//																.getMessage(),
//														e));
//							}
//						}
//
//					}
//				}
//
//			}
//		});
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
						General4Parameter newElement = new General4Parameter(
								defaultListElementAttributeValues[0],
								defaultListElementAttributeValues[1],
								defaultListElementAttributeValues[2],
								defaultListElementAttributeValues[3]
						);

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
						General4Parameter element = (General4Parameter) ((IStructuredSelection) tableViewer
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

	/**
	 * Retrieve the EAnnotation from the modelElement and converts it to a {@link General4Parameter} list
	 * @param act
	 * @return
	 */
	private List<General4Parameter> getParameterList()
	{
		EAnnotation ea = modelElement.getEAnnotation(containerEAnnotationName);
		if(ea != null)
		{
			String value = (String) ea.getDetails().get(annotationDetailName);
			if(value!=null)
				return convertParameterString(value);
		}
		
		return new ArrayList<General4Parameter>(0);
	}
	
	/**
	 * Updates the EAnnotation for the modelElement task parameter list
	 * @param params
	 */
	private void updateParameterList(List<General4Parameter> params)
	{
		updateJadexEAnnotation(annotationDetailName, convertParameterList(params));
		
//		// HACK? Should use notification?
//		Display.getCurrent().asyncExec(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				if (tableViewer != null && modelElement != null)				
//				{
//					tableViewer.refresh();
//				}
//			}
//		});
		
	}
	
	
	/**
	 * Convert a string representation of a General4Parameter list into a General4Parameter list 
	 * @param stringToConvert
	 * @return
	 */
	protected List<General4Parameter> convertParameterString(String stringToConvert)
	{
		StringTokenizer listTokens = new StringTokenizer(stringToConvert, JadexCommonPropertySection.LIST_ELEMENT_DELIMITER);
		List<General4Parameter> params = new ArrayList<General4Parameter>(listTokens.countTokens());
		while(listTokens.hasMoreTokens())
		{
			String paramElement = listTokens.nextToken();
			StringTokenizer paramTokens = new StringTokenizer(paramElement,JadexCommonPropertySection.LIST_ELEMENT_ATTRIBUTE_DELIMITER, true);
			
			String first = null;
			String second = null;
			String third = null;
			String fourth = null;
			
			int count = 1;
			String lastToken = null;
			
			while (paramTokens.hasMoreTokens())
			{
				String token = paramTokens.nextToken();
				
				if (!token.equals(LIST_ELEMENT_ATTRIBUTE_DELIMITER))
				{
					switch (count)
					{
						case 1:
							first = token;
							break;
							
						case 2:
							second = token;
							break;
							
						case 3:
							third = token;
							break;
							
						case 4:
							fourth = token;
							break;
							
						default:
							break;
					}
					
					count++;
				}
				// we found a delim
				else
				{
					
					if (lastToken == null)
					{
						// we found a delim at the first position, count up.
						count++;
					}
					
					else if (token.equals(lastToken))
					{
						// we found two delims without any content between them, add empty string
						switch (count)
						{
							case 1:
								first = "";
								break;
								
							case 2:
								second = "";
								break;
							case 3:
								third = "";
								break;
								
							case 4:
								fourth = "";
								break;
								
							default:
								break;
						}
						count++;
					}

				}
				
				// remember last token 
				lastToken = token;
				
			} // end while
			
			first = first != null ? first : "";
			second = second != null ? second : "";
			third = third != null ? third : "";
			fourth = fourth != null ? fourth : "";
			params.add(new General4Parameter(first, second, third, fourth));
			
		}
		return params;
	}
	
	/**
	 * Convert a list of General4Parameter into a string representation
	 * @param arrayToConvert
	 * @return
	 */
	protected String convertParameterList(List<General4Parameter> params)
	{
		StringBuffer buffer = new StringBuffer();
		for (General4Parameter general4Parameter : params)
		{
			if (buffer.length() != 0)
			{
				buffer.append(LIST_ELEMENT_DELIMITER);
			}

			buffer.append(general4Parameter.getFirstAttribute());
			buffer.append(LIST_ELEMENT_ATTRIBUTE_DELIMITER);
			buffer.append(general4Parameter.getSecondAttribute());
			buffer.append(LIST_ELEMENT_ATTRIBUTE_DELIMITER);
			buffer.append(general4Parameter.getThirdAttribute());
			buffer.append(LIST_ELEMENT_ATTRIBUTE_DELIMITER);
			buffer.append(general4Parameter.getFourthAttribute());

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
		 * @return Object[] that contains General4Parameter objects.
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
			if (element instanceof General4Parameter)
			{
				General4Parameter param = (General4Parameter) element;
				switch (columnIndex)
				{
				case 0:
					return param.getFirstAttribute();
				case 1:
					return param.getSecondAttribute();
				case 2:
					return param.getThirdAttribute();
				case 3:
					return param.getFourthAttribute();
				
				default:
					return super.getText(param);
				}
			}
			return super.getText(element);
		}
		
	}
	
	/**
	 * Internal representation of a jadex runtime General4Parameter
	 * 
	 * @author Claas Altschaffel
	 */
	protected class General4Parameter {
		
		// ---- attributes ----
		
		private String firstAttribute;
		private String secondAttribute;
		private String thirdAttribute;
		private String fourthAttribute;
		
		// ---- constructors ----
		
		/** default constructor */
		public General4Parameter(String first,String second, String third, String fourth)
		{
			super();
			
			assert first != null;
			assert second != null;
			assert third != null;
			assert fourth != null;
			
			this.firstAttribute = first;
			this.secondAttribute = second;
			this.thirdAttribute = third;
			this.fourthAttribute = fourth;
		}

		// ---- overrides ----
		
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof General4Parameter))
			{
				return false;
			}
			
			return firstAttribute.equals(((General4Parameter) obj).getFirstAttribute())
					&& secondAttribute.equals(((General4Parameter) obj).getSecondAttribute())
					&& thirdAttribute.equals(((General4Parameter) obj).getThirdAttribute())
					&& fourthAttribute.equals(((General4Parameter) obj).getFourthAttribute());
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return firstAttribute.hashCode() * 31
					+ secondAttribute.hashCode() * 31
					+ thirdAttribute.hashCode() * 31
					+ fourthAttribute.hashCode() * 31;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return firstAttribute + LIST_ELEMENT_ATTRIBUTE_DELIMITER + secondAttribute + LIST_ELEMENT_ATTRIBUTE_DELIMITER + thirdAttribute + LIST_ELEMENT_ATTRIBUTE_DELIMITER + fourthAttribute;
		}
		
		// ---- getter / setter ----
		
		/**
		 * @return the firstAttribute
		 */
		public String getFirstAttribute()
		{
			return firstAttribute;
		}

		/**
		 * @param firstAttribute the firstAttribute to set
		 */
		public void setFirstAttribute(String firstAttribute)
		{
			this.firstAttribute = firstAttribute;
		}

		/**
		 * @return the secondAttribute
		 */
		public String getSecondAttribute()
		{
			return secondAttribute;
		}

		/**
		 * @param secondAttribute the secondAttribute to set
		 */
		public void setSecondAttribute(String secondAttribute)
		{
			this.secondAttribute = secondAttribute;
		}

		/**
		 * @return the thirdAttribute
		 */
		public String getThirdAttribute()
		{
			return thirdAttribute;
		}

		/**
		 * @param thirdAttribute the thirdAttribute to set
		 */
		public void setThirdAttribute(String thirdAttribute)
		{
			this.thirdAttribute = thirdAttribute;
		}

		/**
		 * @return the fourthAttribute
		 */
		public String getFourthAttribute()
		{
			return fourthAttribute;
		}

		/**
		 * @param fourthAttribute the fourthAttribute to set
		 */
		public void setFourthAttribute(String fourthAttribute)
		{
			this.fourthAttribute = fourthAttribute;
		}
		
		

		

	}
	
	protected class General4ParameterEditingSupport extends EditingSupport {
		
		private CellEditor editor;
		private int attributeIndex;

		public General4ParameterEditingSupport(TableViewer viewer, int attributeIndex)
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
			if (element instanceof General4Parameter)
			{
				return true;
			}
			return false;
		}
		
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		protected void setValue(Object element, Object value) {
			doSetValue(element, value);
			// refresh the table viewer element
			getViewer().update(element, null);
			// refresh the graphical edit part
			refreshSelectedEditPart();
		}

		protected Object getValue(Object element)
		{
			String ret;
			switch (attributeIndex)
			{
				case 0:
					ret = ((General4Parameter) element).firstAttribute;
					break;
				case 1:
					ret = ((General4Parameter) element).secondAttribute;
					break;
				case 2:
					ret = ((General4Parameter) element).thirdAttribute;
					break;
				case 3:
					ret = ((General4Parameter) element).fourthAttribute;
					break;

				default:
					ret = "";
					break;
			}
			return ret;
		}
		
		protected void doSetValue(Object element, Object value)
		{
			
			final General4Parameter param = (General4Parameter) element;
			final String newValue = value.toString();
			
			// modify the Model
			final ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
					modelElement,
					"Update EModelElement parameter list")
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor,
						IAdaptable info)
						throws ExecutionException
				{
					
					List parameterList = getParameterList();
					General4Parameter paramToChange = (General4Parameter) parameterList.get(parameterList.indexOf(param));

					switch (attributeIndex)
					{
						case 0:
							paramToChange.firstAttribute = newValue;
							break;
						case 1:
							paramToChange.secondAttribute = newValue;
							break;
						case 2:
							paramToChange.thirdAttribute = newValue;
							break;
						case 3:
							paramToChange.fourthAttribute = newValue;
							break;

						default:
							throw new UnsupportedOperationException(
									Messages.JadexCommonPropertySection_InvalidEditColumn_Message);
					}

					updateParameterList(parameterList);
					
					return CommandResult.newOKCommandResult();
				}
			};
			
//			Display.getCurrent().asyncExec(new Runnable()
//			{
//				@Override
//				public void run()
//				{
					try
					{
						//command.setReuseParentTransaction(true);
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
//				}
//			});
			
			
			
			// update the corresponding table element
			switch (attributeIndex)
			{
				case 0:
					((General4Parameter) element).firstAttribute = value.toString();
					break;
				case 1:
					((General4Parameter) element).secondAttribute = value.toString();
					break;
				case 2:
					((General4Parameter) element).thirdAttribute = value.toString();
					break;
				case 3:
					((General4Parameter) element).fourthAttribute = value.toString();
					break;

				default:
					break;
			}
		}
	}
	
	


	
}


