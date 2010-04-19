package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;
import jadex.tools.model.common.properties.ModifyEObjectCommand;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.properties.sections.AbstractModelerPropertySection;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class AbstractGpmnPropertySection extends AbstractModelerPropertySection implements IPropertySourceProvider
{

	/** The composite that holds the section parts */
	protected Composite sectionComposite;
	
	// ---- IPropertySourceProvider implementation ----
	
	/**
	 * @generated
	 */
	public IPropertySource getPropertySource(Object object)
	{
		if (object instanceof IPropertySource)
		{
			return (IPropertySource) object;
		}
		AdapterFactory af = getAdapterFactory(object);
		if (af != null)
		{
			IItemPropertySource ips = (IItemPropertySource) af.adapt(object,
					IItemPropertySource.class);
			if (ips != null)
			{
				return new PropertySource(object, ips);
			}
		}
		if (object instanceof IAdaptable)
		{
			return (IPropertySource) ((IAdaptable) object)
					.getAdapter(IPropertySource.class);
		}
		return null;
	}

	/**
	 * @generated
	 */
	protected IPropertySourceProvider getPropertySourceProvider()
	{
		return this;
	}

	// ---- methods ----
	
	/* (non-Javadoc)
	 * @see org.eclipse.gmf.runtime.diagram.ui.properties.sections.AbstractModelerPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		if (sectionComposite != null)
		{
			sectionComposite.dispose();
		}
		super.dispose();
	}

	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		sectionComposite = getWidgetFactory().createComposite(parent);
		sectionComposite.setLayout(new FillLayout());
	}
	
	

	/**
	 * @generated NOT
	 */
	protected EModelElement modelElement;
	
	
	/**
	 * @generated
	 */
	protected AdapterFactory getAdapterFactory(Object object)
	{
		if (getEditingDomain() instanceof AdapterFactoryEditingDomain)
		{
			return ((AdapterFactoryEditingDomain) getEditingDomain())
					.getAdapterFactory();
		}
		TransactionalEditingDomain editingDomain = TransactionUtil
				.getEditingDomain(object);
		if (editingDomain != null)
		{
			return ((AdapterFactoryEditingDomain) editingDomain)
					.getAdapterFactory();
		}
		return null;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 * 
	 * @generated NOT
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{

		super.setInput(part, selection);

		if (selection.isEmpty()
				|| false == selection instanceof StructuredSelection)
		{
			this.modelElement = null;
			return;
		}

		final StructuredSelection structuredSelection = ((StructuredSelection) selection);
		if (structuredSelection.size() > 1)
		{
			// We can't edit two Objects at the same time
			this.modelElement = null;
			return;
		}

		Object selectedModelObject = transformSelection(structuredSelection.getFirstElement());
		if(selectedModelObject instanceof EModelElement)
		{
			this.modelElement = (EModelElement) selectedModelObject;
		}

	}

	
	
	/**
	 * Refreshes the graphical selection after a modify operation.
	 * 
	 * @generated NOT
	 */
	protected void refreshSelectedEditPart()
	{
		if (getSelection() instanceof IStructuredSelection)
		{
			IStructuredSelection sel = (IStructuredSelection) getSelection();
			if (null != sel)
				for (Object selElt : sel.toList())
				{
					if (selElt instanceof EditPart)
					{
						((EditPart) selElt).refresh();
					}
				}
		}
	}

	/**
	 * Modify/unwrap selection.
	 * 
	 * @generated NOT
	 */
	protected Object transformSelection(Object selected)
	{
		Object unknownInput = selected;
		if (unknownInput instanceof IGraphicalEditPart
				&& (((IGraphicalEditPart) unknownInput).getPrimaryView() != null))
		{
			unknownInput = ((IGraphicalEditPart) unknownInput).getPrimaryView()
					.getElement();
		}
		if (unknownInput instanceof EModelElement)
		{
			return (EModelElement) unknownInput;
		}

		return selected;
	}
	
	
	
	
	
	
	
	
	
	
	
}
