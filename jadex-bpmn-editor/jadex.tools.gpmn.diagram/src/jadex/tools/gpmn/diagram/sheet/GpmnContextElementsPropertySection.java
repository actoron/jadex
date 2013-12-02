/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.Context;
import jadex.tools.gpmn.ContextElement;
import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.diagram.part.GpmnDiagramMessages;
import jadex.tools.model.common.properties.ModifyEObjectCommand;
import jadex.tools.model.common.properties.table.AbstractCommonTablePropertySection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @generated NOT
 */
public class GpmnContextElementsPropertySection extends AbstractCommonTablePropertySection
{

	/**
	 * @generated NOT
	 */
	private final static String NAME_COLUMN = "Name"; //$NON-NLS-1$
	/**
	 * @generated NOT
	 */
	private final static String TYPE_COLUMN = "Type"; //$NON-NLS-1$
	/**
	 * @generated NOT
	 */
	private final static String VALUE_COLUMN = "Value"; //$NON-NLS-1$
	/**
	 * @generated NOT
	 */
	private final static String SET_COLUMN = "Set"; //$NON-NLS-1$
	/**
	 * @generated NOT
	 */
	private final static String DYNAMIC_COLUMN = "Dynamic"; //$NON-NLS-1$

	
	public static final int[] DEFAULT_COLUMN_WEIGHTS = new int[] { 2, 2, 4, 1, 1 };

	
	public static final String[] BOOLEAN_VALUES = new String[]{"false", "true"};
	
	/**
	 * @param tableViewerLabel
	 */
	public GpmnContextElementsPropertySection()
	{
		super("Context Elements");
	}

	protected int[] getColumnWeights(TableColumn[] columns)
	{
		return DEFAULT_COLUMN_WEIGHTS;
	}
	
	/**
	 * Create the edit table
	 * @param parent
	 * 
	 */
	@Override
	protected void createColumns(TableViewer viewer) 
	{
		
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(NAME_COLUMN);
		column.setEditingSupport(new ContextElementEditingSupport(viewer)
		{
			@Override
			protected Object getValue(Object element)
			{
				return ((ContextElement) element).getName();
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyContextElementCommand((EObject)element, NAME_COLUMN, value);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				return ((ContextElement) element).getName();
			}
		});

		column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(TYPE_COLUMN);
		column.setEditingSupport(new ContextElementEditingSupport(viewer)
		{
			@Override
			protected Object getValue(Object element)
			{
				return ((ContextElement) element).getType();
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyContextElementCommand((EObject)element, TYPE_COLUMN, value);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				return ((ContextElement) element).getType();
			}
		});
		
		column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(VALUE_COLUMN);
		column.setEditingSupport(new ContextElementEditingSupport(viewer)
		{
			@Override
			protected Object getValue(Object element)
			{
				return ((ContextElement) element).getInitialValue();
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyContextElementCommand((EObject)element, VALUE_COLUMN, value);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				return ((ContextElement) element).getInitialValue();
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(SET_COLUMN);
		ComboBoxCellEditor editor = new ComboBoxCellEditor(((TableViewer) viewer)
				.getTable(), BOOLEAN_VALUES);
		column.setEditingSupport(new ContextElementEditingSupport(viewer, editor)
		{
			@Override
			protected Object getValue(Object element)
			{
				if (BOOLEAN_VALUES[0].equals(String.valueOf(((ContextElement) element).isSet())))
				{
					return Integer.valueOf(0);
				}
				return Integer.valueOf(1);
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				
				
				return new ModifyContextElementCommand((EObject)element, SET_COLUMN, BOOLEAN_VALUES[((Integer) value).intValue()]);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				//return ((ContextElement) element).isSet();
				return String.valueOf(((ContextElement) element).isSet());
			}
		});
		
		
		
		
		
		
		
		
		
		column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(DYNAMIC_COLUMN);
		editor = new ComboBoxCellEditor(((TableViewer) viewer)
				.getTable(), BOOLEAN_VALUES);
		column.setEditingSupport(new ContextElementEditingSupport(viewer, editor)
		{
			@Override
			protected Object getValue(Object element)
			{
				if (BOOLEAN_VALUES[0].equals(String.valueOf(((ContextElement) element).isDynamic())))
				{
					return Integer.valueOf(0);
				}
				return Integer.valueOf(1);
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyContextElementCommand((EObject)element, DYNAMIC_COLUMN, BOOLEAN_VALUES[((Integer) value).intValue()]);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				//return ((ContextElement) element).isDynamic();
				return String.valueOf(((ContextElement) element).isDynamic());
			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	/* (non-Javadoc)
	 * @see jadex.tools.gpmn.diagram.sheet.AbstractGpmnTablePropertySection#getAddCommand()
	 */
	@Override
	protected ModifyEObjectCommand getAddCommand()
	{
		// modify the ContextElement
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				GpmnDiagramMessages.GpmnContextElementsListSection_add_element_command_name)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				ContextElement newElement = GpmnFactory.eINSTANCE
						.createContextElement();
				newElement.setName("name");
				((Context) modelElement).getElements().add(newElement);
				return CommandResult.newOKCommandResult(newElement);
			}
		};
		
		return command;
	}

	/**
	 * @see jadex.tools.model.common.properties.table.AbstractCommonTablePropertySection#getDeleteCommand()
	 */
	@Override
	protected ModifyEObjectCommand getDeleteCommand()
	{
		// modify the ContextElement
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				GpmnDiagramMessages.GpmnContextElementsListSection_remove_element_command_name)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				ContextElement element = (ContextElement) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				
				((Context) modelElement).getElements().remove(element);
				return CommandResult.newOKCommandResult(null);
			}
		};
		
		return command;
	}
	
	/**
	 * @see jadex.tools.model.common.properties.table.AbstractCommonTablePropertySection#getDeleteCommand()
	 */
	@Override
	protected ModifyEObjectCommand getUpCommand()
	{
		// modify the ContextElement
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"ContextElement MoveUp command")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				ContextElement element = (ContextElement) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				
				EList<ContextElement> elements = ((Context) modelElement).getElements();
				int index = elements.indexOf(element);
				if (0 < index && index < elements.size())
				{
					elements.move(index-1, element);
				}
				
				((Context) modelElement).getElements(). remove(element);
				
				
				return CommandResult.newOKCommandResult(null);
			}
		};
		
		return command;
	}
	
	protected ModifyEObjectCommand getDownCommand()
	{
		// modify the ContextElement
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"ContextElement MoveUp command")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				ContextElement element = (ContextElement) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				
				EList<ContextElement> elements = ((Context) modelElement).getElements();
				int index = elements.indexOf(element);
				if (0 <= index && index < elements.size()-1)
				{
					elements.move(index+1, element);
				}
				
				((Context) modelElement).getElements(). remove(element);
				
				
				return CommandResult.newOKCommandResult(null);
			}
		};
		
		return command;
	}

	/**
	 * @see jadex.tools.model.common.properties.table.AbstractCommonTablePropertySection#getTableContentProvider()
	 */
	@Override
	protected IStructuredContentProvider getTableContentProvider()
	{
		return new GpmnContextElementsTableContentProvider();
	}



	protected abstract class ContextElementEditingSupport extends AbstractGpmnEditingSupport
	{

		/**
		 * @param viewer
		 * @param editor
		 */
		protected ContextElementEditingSupport(TableViewer viewer, CellEditor editor)
		{
			super(viewer, editor);
		}

		/**
		 * @param viewer
		 */
		protected ContextElementEditingSupport(TableViewer viewer)
		{
			super(viewer);
		}
		
		protected boolean canEdit(Object element)
		{
			return (element instanceof ContextElement);
		}
		
	}
	
	
	protected class ModifyContextElementCommand extends ModifyEObjectCommand
	{

		private EObject elementToUpdate;
		private String property;
		private Object value;
		
		/**
		 * @param elementToUpdate
		 * @param property
		 * @param value
		 */
		protected ModifyContextElementCommand(EObject element, String property, Object value)
		{
			super(element, GpmnDiagramMessages.GpmnContextElementsListSection_update_element_command_name);
			
			this.elementToUpdate = element;
			this.property = property;
			this.value = value;
		}
		
		@Override
		protected CommandResult doExecuteWithResult(
				IProgressMonitor monitor,
				IAdaptable info)
				throws ExecutionException
		{

			if (NAME_COLUMN.equals(property))
			{
				((ContextElement) elementToUpdate).setName((String) value);
			}
			else if (TYPE_COLUMN.equals(property))
			{
				((ContextElement) elementToUpdate).setType((String) value);
			}
			else if (VALUE_COLUMN.equals(property))
			{
				((ContextElement) elementToUpdate).setInitialValue((String) value);
			}
			else if (SET_COLUMN.equals(property))
			{
				((ContextElement) elementToUpdate).setSet(Boolean.parseBoolean((String)value));
			}
			else if (DYNAMIC_COLUMN.equals(property))
			{
				((ContextElement) elementToUpdate).setDynamic(Boolean.parseBoolean((String)value));
			}
			else
			{
				throw new UnsupportedOperationException(
						"Invalid edit column: " + property);
			}

			return CommandResult.newOKCommandResult();
		}
		
	}
	
	/**
	 * Simple content provider that reflects the ContextElemnts 
	 * of an Context given as an input. Marked as dynamic / static.
	 */
	public class GpmnContextElementsTableContentProvider implements IStructuredContentProvider {

		/**
		 * Generate the content for the table.
		 * 
		 * @return Object[] that contains ContextElement objects.
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Context)
			{
				EList<ContextElement> contextElements = ((Context) inputElement).getElements();
				return contextElements.toArray(new Object[contextElements.size()]);
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

}