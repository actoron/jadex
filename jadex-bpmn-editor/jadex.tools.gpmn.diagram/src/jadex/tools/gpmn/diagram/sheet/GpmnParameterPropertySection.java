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
import jadex.tools.gpmn.DirectionType;
import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.Parameter;
import jadex.tools.gpmn.ParameterizedVertex;
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
public class GpmnParameterPropertySection extends AbstractCommonTablePropertySection
{
	/**
	 * @generated NOT
	 */
	private final static String DIRECTION_COLUMN = "Direction"; //$NON-NLS-1$
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
	private final static String[] DIRECTION_VALUES = DirectionType.LITERAL_VALUES
			.toArray(new String[DirectionType.LITERAL_VALUES.size()]);

	private final static String[] DEFAULT_COLUMN_NAMES
		= new String[] { DIRECTION_COLUMN, NAME_COLUMN, TYPE_COLUMN, VALUE_COLUMN  };
	
	public static final int[] DEFAULT_COLUMN_WEIGHTS = new int[] {1,1,1,8};

	/**
	 * @param tableViewerLabel
	 */
	public GpmnParameterPropertySection()
	{
		super("Parameter");
	}

	protected int[] getColumnWeights(TableColumn[] columns)
	{
		return DEFAULT_COLUMN_WEIGHTS;
	}
	
	/**
	 * Create the parameter edit table
	 * @param parent
	 * 
	 */
	@Override
	protected void createColumns(TableViewer viewer) 
	{
		
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(DIRECTION_COLUMN);
		ComboBoxCellEditor editor = new ComboBoxCellEditor(((TableViewer) viewer)
				.getTable(), DIRECTION_VALUES);
		column.setEditingSupport(new ParameterEditingSupport(viewer, editor)
		{
			@Override
			protected Object getValue(Object element)
			{
				int index =  DirectionType.VALUES.indexOf(((Parameter) element)
						.getDirection());
				//if (index != -1)
				//{
					return Integer.valueOf(index);
				//}
				//return Integer.valueOf(0);
				
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyParamterCommand((EObject)element, DIRECTION_COLUMN, value);
			}

			
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				return ((Parameter) element).getDirection().getLiteral();
			}
		});
		
		column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(NAME_COLUMN);
		column.setEditingSupport(new ParameterEditingSupport(viewer)
		{
			@Override
			protected Object getValue(Object element)
			{
				return ((Parameter) element).getName();
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyParamterCommand((EObject)element, NAME_COLUMN, value);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				return ((Parameter) element).getName();
			}
		});

		column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(TYPE_COLUMN);
		column.setEditingSupport(new ParameterEditingSupport(viewer)
		{
			@Override
			protected Object getValue(Object element)
			{
				return ((Parameter) element).getType();
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyParamterCommand((EObject)element, TYPE_COLUMN, value);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				return ((Parameter) element).getType();
			}
		});
		
		column = new TableViewerColumn(viewer, SWT.LEFT);
		column.getColumn().setText(VALUE_COLUMN);
		column.setEditingSupport(new ParameterEditingSupport(viewer)
		{
			@Override
			protected Object getValue(Object element)
			{
				return ((Parameter) element).getValue();
			}

			@Override
			protected ModifyEObjectCommand getTransactionalEditCommand(
					Object element, Object value)
			{
				return new ModifyParamterCommand((EObject)element, VALUE_COLUMN, value);
			}
		});
		column.setLabelProvider(new ColumnLabelProvider()
		{
			public String getText(Object element)
			{
				return ((Parameter) element).getValue();
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
				GpmnDiagramMessages.GpmnParamterListSection_add_element_command_name)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				Parameter newElement = GpmnFactory.eINSTANCE
						.createParameter();
				newElement.setName("Added Parameter");
				((ParameterizedVertex) modelElement).getParameter().add(newElement);
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
				GpmnDiagramMessages.GpmnParamterListSection_remove_element_command_name)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				Parameter element = (Parameter) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				
				((ParameterizedVertex) modelElement).getParameter().remove(element);
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
				"Parameter MoveUp command")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				Parameter param = (Parameter) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				
				EList<Parameter> parameters = ((ParameterizedVertex) modelElement).getParameter();
				int index = parameters.indexOf(param);
				if (0 < index && index < parameters.size())
				{
					parameters.move(index-1, param);
				}
				
				((Context) modelElement).getElements(). remove(param);
				
				
				return CommandResult.newOKCommandResult(null);
			}
		};
		
		return command;
	}
	
	/**
	 * @see jadex.tools.model.common.properties.table.AbstractCommonTablePropertySection#getDeleteCommand()
	 */
	@Override
	protected ModifyEObjectCommand getDownCommand()
	{
		// modify the ContextElement
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"Parameter MoveUp command")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				Parameter param = (Parameter) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				
				EList<Parameter> parameters = ((ParameterizedVertex) modelElement).getParameter();
				int index = parameters.indexOf(param);
				if (0 <= index && index < parameters.size()-1)
				{
					parameters.move(index+1, param);
				}
				
				((Context) modelElement).getElements(). remove(param);
				
				
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
		return new ParameterTableContentProvider();
	}



	protected abstract class ParameterEditingSupport extends AbstractGpmnEditingSupport
	{

		/**
		 * @param viewer
		 * @param editor
		 */
		protected ParameterEditingSupport(TableViewer viewer, CellEditor editor)
		{
			super(viewer, editor);
		}

		/**
		 * @param viewer
		 */
		protected ParameterEditingSupport(TableViewer viewer)
		{
			super(viewer);
		}
		
		protected boolean canEdit(Object element)
		{
			return (element instanceof Parameter);
		}
		
	}
	
	
	protected class ModifyParamterCommand extends ModifyEObjectCommand
	{

		private EObject elementToUpdate;
		private String property;
		private Object value;
		
		/**
		 * @param elementToUpdate
		 * @param property
		 * @param value
		 */
		protected ModifyParamterCommand(EObject element, String property, Object value)
		{
			super(element, GpmnDiagramMessages.GpmnParamterListSection_update_element_command_name);
			
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
				((Parameter) elementToUpdate).setName((String) value);
			}
			else if (TYPE_COLUMN.equals(property))
			{
				((Parameter) elementToUpdate).setType((String) value);
			}
			else if (VALUE_COLUMN.equals(property))
			{
				((Parameter) elementToUpdate).setValue((String) value);
			}
			else if (DIRECTION_COLUMN.equals(property))
			{
				if (value instanceof Integer)
				{
					((Parameter) elementToUpdate)
							.setDirection(DirectionType.VALUES
									.get(((Integer) value)
											.intValue()));
				}
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
	protected class ParameterTableContentProvider implements IStructuredContentProvider {

		/**
		 * Generate the content for the table.
		 * 
		 * @return Object[] that contains ContextElement objects.
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof ParameterizedVertex)
			{
				EList<Parameter> parameter = ((ParameterizedVertex) inputElement).getParameter();
				return parameter.toArray(new Object[parameter.size()]);
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
