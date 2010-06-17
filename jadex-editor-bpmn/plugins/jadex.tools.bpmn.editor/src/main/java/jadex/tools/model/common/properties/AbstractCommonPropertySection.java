package jadex.tools.model.common.properties;

import jadex.tools.bpmn.editor.JadexBpmnEditor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditorPlugin;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public abstract class AbstractCommonPropertySection extends
		AbstractPropertySection
{

	/** The composite that holds the section parts */
	protected Composite sectionComposite;
	
	/** The modelElement, may be null. */
	protected EModelElement modelElement;
	
	/** all controls/resources/... for this section */
	private Set<Object> disposableObjects;
	

	// ---- constructor ----
	
	public AbstractCommonPropertySection()
	{
		super();
		this.disposableObjects = new HashSet<Object>();
	}

	// ---- method overrides ----
	
	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		sectionComposite = getWidgetFactory().createComposite(parent);
		sectionComposite.setLayout(new FillLayout());
		
		disposableObjects.add(sectionComposite);
	}

	/**
	 * Add a object to dispose on dispose of this control
	 * @param toDispose
	 */
	protected void addDisposable(Object toDispose)
	{
		synchronized (disposableObjects)
		{
			disposableObjects.add(toDispose);
		}
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		for (Object toDispose : disposableObjects)
		{
			if (toDispose instanceof Control)
			{
				Control control = (Control) toDispose;
				if (control != null && !control.isDisposed()) 
				{
					control.dispose();
				}
			}
			
			else if (toDispose instanceof Resource)
			{
				Resource resource = (Resource) toDispose;
				if (resource != null && !resource.isDisposed()) 
				{
					resource.dispose();
				}
			}
			
			else if (toDispose instanceof Widget)
			{
				Widget widget = (Widget) toDispose;
				if (widget != null && !widget.isDisposed())
				{
					widget.dispose();
				}
			}
			
			else if (toDispose instanceof IBaseLabelProvider)
			{
				IBaseLabelProvider provider = (IBaseLabelProvider) toDispose;
				if (provider != null)
				{
					provider.dispose();
				}
			}
			
			else if (toDispose instanceof IContentProvider)
			{
				IContentProvider provider = (IContentProvider) toDispose;
				if (provider != null)
				{
					provider.dispose();
				}
			}
			
			else
			{
				throw new RuntimeException("Unsupported class to dispose: " + toDispose);
			}
		}
		disposableObjects.clear();
		
		super.dispose();
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
			if (unknownInput instanceof EModelElement)
			{
				EModelElement elm = (EModelElement) unknownInput;
				modelElement = (EModelElement) elm;
				
				return;
			}
		}

		// fall through
		modelElement = null;

	}

	protected void changed(Control[] changed)
	{
		sectionComposite.changed(changed);
	}

	// ---- helper methods ----
	
	/**
	 * Refreshes the graphical selection after a modify operation.
	 * 
	 * @generated NOT
	 */
	public void refreshSelectedEditPart()
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
							public void run()
							{
								part.refresh();
							}
						});
					}
				}
		}
	}

	/**
	 * Create a group with all existing controls in sectionComposite 
	 * and replace the section composite with it
	 * @param groupLabel
	 * @return created Group
	 */
	protected Group groupExistingControls(String groupLabel)
	{
		// The layout of the section composite
		Layout sectionLayout = sectionComposite.getLayout();
		Control[] children = sectionComposite.getParent().getChildren();
		Group sectionGroup = getWidgetFactory().createGroup(
				sectionComposite.getParent(), groupLabel);
		sectionGroup.setLayout(sectionLayout);
		sectionComposite = sectionGroup;
		for (int i = 0; i < children.length; i++)
		{
			children[i].setParent(sectionGroup);
		}
		
		addDisposable(sectionGroup);
		return sectionGroup;
	}

//	/**
//	 * Dummy method for empty composites
//	 */
//	protected static Composite createEmptyComposite(Composite parent, AbstractPropertySection section)
//	{
//		Composite newComposite = section.getWidgetFactory().createComposite(parent/*, SWT.BORDER*/);
//		
//		// The layout of the composite
//		GridLayout layout = new GridLayout(1, false);
//		newComposite.setLayout(layout);
//		
//		//section.getWidgetFactory().createCLabel(newComposite, "---- empty composite ----");
//		
//		return newComposite;
//	}
	
	
	// ---- getter / setter ----
	
	/** 
	 * Access the model element 
	 */
	public EModelElement getEModelElement()
	{
		return modelElement;
	}
	
	/**
	 * Utility class thats simplify the execution of
	 * AbstractTransactionalCommand.
	 * 
	 * @generated NOT
	 */
	protected abstract class AbstractGpmnEditingSupport extends EditingSupport
	{
		private CellEditor editor;

		public AbstractGpmnEditingSupport(TableViewer viewer)
		{
			super(viewer);
			this.editor = new TextCellEditor(viewer.getTable());
		}

		public AbstractGpmnEditingSupport(TableViewer viewer, CellEditor editor)
		{
			super(viewer);
			this.editor = editor;
		}

		protected boolean canEdit(Object element)
		{
			return (element instanceof EObject);
			// return true;
		}

		protected CellEditor getCellEditor(Object element)
		{
			return editor;
		}

		protected void setValue(Object element, Object value)
		{
			ModifyEObjectCommand command = getTransactionalEditCommand(element,
					value);

			try
			{
				command.execute(null, null);
			}
			catch (ExecutionException ex)
			{
				BpmnDiagramEditorPlugin.getInstance().getLog().log(
						new Status(IStatus.ERROR,
								JadexBpmnEditor.ID, IStatus.ERROR,
								ex.getMessage(), ex));
			}

			getViewer().update(element, null);
			refreshSelectedEditPart();
		}

		protected abstract ModifyEObjectCommand getTransactionalEditCommand(
				Object element, Object value);
	}
}


