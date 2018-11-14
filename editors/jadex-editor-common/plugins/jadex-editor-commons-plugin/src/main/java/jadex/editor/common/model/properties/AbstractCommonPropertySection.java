package jadex.editor.common.model.properties;

import jadex.editor.common.internal.CommonsActivator;

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
import org.eclipse.swt.custom.ScrolledComposite;
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

/**
 * 
 */
public abstract class AbstractCommonPropertySection extends AbstractPropertySection
{
	public static final int PROPERTY_SECTION_SCROLL_INCREMENT = 10;

	/** The tabbed property sheet page. */
	protected TabbedPropertySheetPage tabbedPage;
	
	/** The composite that holds the section parts */
	protected Composite sectionComposite;
	
	/** The Edit Part, may be null */
	protected EditPart editPart;
	
	/** The modelElement, may be null. */
	protected EModelElement modelElement;
	
	/** The lastModelElement, may be null. */
	protected EModelElement lastModelElement;
	
	/** all controls/resources/... for this section */
	private Set<Object> disposableObjects;
	
	/** The composite that holds the root section composite */
	private Composite rootComposite;
	
	/** The ScrolledComposite for the property section - static because there is only one instance */
	private static ScrolledComposite propertyComposite;
	
	private final Set<EditPart> refreshPending = new HashSet<EditPart>();
	
	// ---- constructor ----
	
	public AbstractCommonPropertySection()
	{
		this.disposableObjects = new HashSet<Object>();
	}

	// ---- abstract methods ----
	
	/**
	 * This method should contain all "update" code for controls
	 * currently used in setInput(). This method is called at the
	 * end of setInput().
	 */
	protected abstract void updateSectionValues();
	
	// ---- method overrides ----
	
	
	public void refresh()
	{
		super.refresh();
		//System.err.println("Refresh called by: " + Thread.currentThread());
	}
	
	/**
	 * Creates the UI of the section.
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPage)
	{
		super.createControls(parent, tabbedPage);
		
		this.tabbedPage = tabbedPage;
		sectionComposite = getWidgetFactory().createComposite(parent); //.createScrolledComposite(parent, SWT.DEFAULT); //
		sectionComposite.setLayout(new FillLayout());

		disposableObjects.add(sectionComposite);
		// save a reference to the first section composite
		rootComposite = sectionComposite;
		
		increasePropertyViewScrolling(parent);
	}

	/**
	 * @param parent
	 */
	private void increasePropertyViewScrolling(Composite parent)
	{
		if (propertyComposite != null && !propertyComposite.isDisposed())
		{
			if (propertyComposite.getVerticalBar().getIncrement() == PROPERTY_SECTION_SCROLL_INCREMENT)
				return;
		}
		
		Composite composite = parent;
		int maxDepth = 10;
		int depth = 0;
		while (depth < maxDepth && !(composite instanceof ScrolledComposite))
		{
			depth++;
			composite = composite.getParent();
		}
		
		if (composite instanceof ScrolledComposite)
		{
			propertyComposite = (ScrolledComposite) composite;
			propertyComposite.getVerticalBar().setIncrement(PROPERTY_SECTION_SCROLL_INCREMENT);
			propertyComposite.getHorizontalBar().setIncrement(PROPERTY_SECTION_SCROLL_INCREMENT);
		}
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
	public void dispose()
	{
		synchronized (disposableObjects)
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
					throw new RuntimeException("Unsupported class to dispose: "
							+ toDispose);
				}
			}
			disposableObjects.clear();

		}
		
		super.dispose();
	}

	
	/**
	 * Manages the input.
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		//System.out.println("SetInput called by: " + Thread.currentThread());
		
		super.setInput(part, selection);
		if(selection instanceof IStructuredSelection)
		{
			Object unknownInput = ((IStructuredSelection)selection).getFirstElement();
			
			if(unknownInput instanceof IGraphicalEditPart && (((IGraphicalEditPart) unknownInput)
				.resolveSemanticElement() != null))
			{
				editPart = (IGraphicalEditPart)unknownInput;
				unknownInput = ((IGraphicalEditPart)unknownInput).resolveSemanticElement();
			}
			
			if(unknownInput instanceof EModelElement)
			{
				EModelElement elm = (EModelElement)unknownInput;
				modelElement = (EModelElement)elm;
				
				updateSectionValues();
				return;
			}
			if(unknownInput instanceof EditPart)
			{
				editPart = (EditPart)unknownInput;
			}
		}

		// fall through
		modelElement = null;
	}

	protected void changed(Control[] changed)
	{
		rootComposite.changed(changed);
	}

	// ---- helper methods ----
	
	/**
	 * Refreshes the graphical selection after a modify operation.
	 * 
	 * @generated NOT
	 */
	public void refreshSelectedEditPart()
	{
		if(getSelection() instanceof IStructuredSelection)
		{
			IStructuredSelection sel = (IStructuredSelection) getSelection();
			if(null != sel)
			{
				for (Object selElt : sel.toList())
				{
					if (selElt instanceof EditPart)
					{
						final EditPart part = (EditPart) selElt;
						
						if (!refreshPending.contains(part))
						{
							refreshPending.add(part);
							Display.getCurrent().asyncExec(new Runnable()
							{
								public void run()
								{
									part.refresh();
									refreshPending.remove(part);
								}
							});
						}
					}
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
			ModifyEObjectCommand command = getTransactionalEditCommand(element, value);

			try
			{
				command.execute(null, null);
			}
			catch (ExecutionException ex)
			{
				CommonsActivator.getDefault().getLog().log(new Status(IStatus.ERROR,
					CommonsActivator.PLUGIN_ID, IStatus.ERROR, ex.getMessage(), ex));
			}

			getViewer().update(element, null);
			refreshSelectedEditPart();
		}

		protected abstract ModifyEObjectCommand getTransactionalEditCommand(Object element, Object value);
	}
}


