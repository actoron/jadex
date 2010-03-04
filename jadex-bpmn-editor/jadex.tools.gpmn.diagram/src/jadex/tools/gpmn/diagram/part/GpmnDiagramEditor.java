/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.diagram.navigator.GpmnNavigatorItem;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gmf.runtime.common.ui.services.marker.MarkerNavigationService;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.ui.actions.ActionIds;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramDropTargetListener;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDiagramDocument;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDocument;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDocumentProvider;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;

/**
 * @generated
 */
public class GpmnDiagramEditor extends DiagramDocumentEditor implements
		IGotoMarker
{

	/**
	 * On palette creation we don't have access to the (file specific) domain.
	 * For disable palette tools we use this static Hack.
	 * generated NOT
	 */
	public static HashSet staticRemovedElementTypes = new HashSet<IElementType>();
	static
	{
		staticRemovedElementTypes.add(GpmnElementTypes.Process_2001);
		staticRemovedElementTypes.add(GpmnElementTypes.MessageGoal_2008);
		staticRemovedElementTypes.add(GpmnElementTypes.MessagingEdge_4004);
	}

	/**
	 * Added as constant, GMF only generates this in-line
	 * @generated NOT
	 */
	public static final String EDITING_DOMAIN_ID = "jadex.tools.gpmn.diagram.EditingDomain"; //$NON-NLS-1$

	/**
	 * @generated
	 */
	public static final String ID = "jadex.tools.gpmn.diagram.part.GpmnDiagramEditorID"; //$NON-NLS-1$

	/**
	 * @generated
	 */
	public static final String CONTEXT_ID = "jadex.tools.gpmn.diagram.ui.diagramContext"; //$NON-NLS-1$

	/**
	 * @generated
	 */
	public GpmnDiagramEditor()
	{
		super(true);
	}

	/**
	 * @generated
	 */
	protected String getContextID()
	{
		return CONTEXT_ID;
	}

	/**
	 * Override edit domain id
	 * @generated NOT
	 */
	@Override
	protected String getEditingDomainID()
	{
		return EDITING_DOMAIN_ID;
	}

	/**
	 * @generated
	 */
	protected PaletteRoot createPaletteRoot(PaletteRoot existingPaletteRoot)
	{
		PaletteRoot root = super.createPaletteRoot(existingPaletteRoot);
		new GpmnPaletteFactory().fillPalette(root);
		return root;
	}

	/**
	 * @generated
	 */
	protected PreferencesHint getPreferencesHint()
	{
		return GpmnDiagramEditorPlugin.DIAGRAM_PREFERENCES_HINT;
	}

	/**
	 * @generated
	 */
	public String getContributorId()
	{
		return GpmnDiagramEditorPlugin.ID;
	}

	/**
	 * @generated
	 */
	public Object getAdapter(Class type)
	{
		if (type == IShowInTargetList.class)
		{
			return new IShowInTargetList()
			{
				public String[] getShowInTargetIds()
				{
					return new String[] { ProjectExplorer.VIEW_ID };
				}
			};
		}
		return super.getAdapter(type);
	}

	/**
	 * @generated
	 */
	protected IDocumentProvider getDocumentProvider(IEditorInput input)
	{
		if (input instanceof IFileEditorInput
				|| input instanceof URIEditorInput)
		{
			return GpmnDiagramEditorPlugin.getInstance().getDocumentProvider();
		}
		return super.getDocumentProvider(input);
	}

	/**
	 * 
	 */
	public TransactionalEditingDomain getEditingDomain()
	{
		IDocument document = getEditorInput() != null ? getDocumentProvider()
				.getDocument(getEditorInput()) : null;
		if (document instanceof IDiagramDocument)
		{
			return ((IDiagramDocument) document).getEditingDomain();
		}
		return super.getEditingDomain();
	}

	/**
	 * @generated
	 */
	protected void setDocumentProvider(IEditorInput input)
	{
		if (input instanceof IFileEditorInput
				|| input instanceof URIEditorInput)
		{
			setDocumentProvider(GpmnDiagramEditorPlugin.getInstance()
					.getDocumentProvider());
		}
		else
		{
			super.setDocumentProvider(input);
		}
	}

	/**
	 * @generated
	 */
	public void gotoMarker(IMarker marker)
	{
		MarkerNavigationService.getInstance().gotoMarker(this, marker);
	}

	/**
	 * @generated
	 */
	public boolean isSaveAsAllowed()
	{
		return true;
	}

	/**
	 * @generated
	 */
	public void doSaveAs()
	{
		performSaveAs(new NullProgressMonitor());
	}

	/**
	 * @generated
	 */
	protected void performSaveAs(IProgressMonitor progressMonitor)
	{
		Shell shell = getSite().getShell();
		IEditorInput input = getEditorInput();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		IFile original = input instanceof IFileEditorInput ? ((IFileEditorInput) input)
				.getFile()
				: null;
		if (original != null)
		{
			dialog.setOriginalFile(original);
		}
		dialog.create();
		IDocumentProvider provider = getDocumentProvider();
		if (provider == null)
		{
			// editor has been programmatically closed while the dialog was open
			return;
		}
		if (provider.isDeleted(input) && original != null)
		{
			String message = NLS.bind(
					Messages.GpmnDiagramEditor_SavingDeletedFile, original
							.getName());
			dialog.setErrorMessage(null);
			dialog.setMessage(message, IMessageProvider.WARNING);
		}
		if (dialog.open() == Window.CANCEL)
		{
			if (progressMonitor != null)
			{
				progressMonitor.setCanceled(true);
			}
			return;
		}
		IPath filePath = dialog.getResult();
		if (filePath == null)
		{
			if (progressMonitor != null)
			{
				progressMonitor.setCanceled(true);
			}
			return;
		}
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = workspaceRoot.getFile(filePath);
		final IEditorInput newInput = new FileEditorInput(file);
		// Check if the editor is already open
		IEditorMatchingStrategy matchingStrategy = getEditorDescriptor()
				.getEditorMatchingStrategy();
		IEditorReference[] editorRefs = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();
		for (int i = 0; i < editorRefs.length; i++)
		{
			if (matchingStrategy.matches(editorRefs[i], newInput))
			{
				MessageDialog.openWarning(shell,
						Messages.GpmnDiagramEditor_SaveAsErrorTitle,
						Messages.GpmnDiagramEditor_SaveAsErrorMessage);
				return;
			}
		}
		boolean success = false;
		try
		{
			provider.aboutToChange(newInput);
			getDocumentProvider(newInput).saveDocument(progressMonitor,
					newInput,
					getDocumentProvider().getDocument(getEditorInput()), true);
			success = true;
		}
		catch (CoreException x)
		{
			IStatus status = x.getStatus();
			if (status == null || status.getSeverity() != IStatus.CANCEL)
			{
				ErrorDialog.openError(shell,
						Messages.GpmnDiagramEditor_SaveErrorTitle,
						Messages.GpmnDiagramEditor_SaveErrorMessage, x
								.getStatus());
			}
		}
		finally
		{
			provider.changed(newInput);
			if (success)
			{
				setInput(newInput);
			}
		}
		if (progressMonitor != null)
		{
			progressMonitor.setCanceled(!success);
		}
	}

	/**
	 * @generated
	 */
	public ShowInContext getShowInContext()
	{
		return new ShowInContext(getEditorInput(), getNavigatorSelection());
	}

	/**
	 * @generated
	 */
	private ISelection getNavigatorSelection()
	{
		IDiagramDocument document = getDiagramDocument();
		if (document == null)
		{
			return StructuredSelection.EMPTY;
		}
		Diagram diagram = document.getDiagram();
		IFile file = WorkspaceSynchronizer.getFile(diagram.eResource());
		if (file != null)
		{
			GpmnNavigatorItem item = new GpmnNavigatorItem(diagram, file, false);
			return new StructuredSelection(item);
		}
		return StructuredSelection.EMPTY;
	}

	/**
	 * @generated
	 */
	protected void configureGraphicalViewerGen()
	{
		super.configureGraphicalViewer();
		DiagramEditorContextMenuProvider provider = new DiagramEditorContextMenuProvider(
				this, getDiagramGraphicalViewer());
		getDiagramGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu(ActionIds.DIAGRAM_EDITOR_CONTEXT_MENU,
				provider, getDiagramGraphicalViewer());
	}

	/**
	 * - customized preference default
	 * 
	 * @generated NOT
	 */
	protected void configureGraphicalViewer()
	{
		this.configureGraphicalViewerGen();

		DiagramRootEditPart root = (DiagramRootEditPart) getDiagramGraphicalViewer()
				.getRootEditPart();
		setupSnapToGeometry(root);
	}

	/**
	 * @generated
	 */
	protected void initializeGraphicalViewer()
	{
		super.initializeGraphicalViewer();
		getDiagramGraphicalViewer().addDropTargetListener(
				new DropTargetListener(getDiagramGraphicalViewer(),
						LocalSelectionTransfer.getTransfer())
				{

					protected Object getJavaObject(TransferData data)
					{
						return LocalSelectionTransfer.getTransfer()
								.nativeToJava(data);
					}

				});
		getDiagramGraphicalViewer().addDropTargetListener(
				new DropTargetListener(getDiagramGraphicalViewer(),
						LocalTransfer.getInstance())
				{

					protected Object getJavaObject(TransferData data)
					{
						return LocalTransfer.getInstance().nativeToJava(data);
					}

				});
	}

	/**
	 * @generated
	 */
	private abstract class DropTargetListener extends DiagramDropTargetListener
	{

		/**
		 * @generated
		 */
		public DropTargetListener(EditPartViewer viewer, Transfer xfer)
		{
			super(viewer, xfer);
		}

		/**
		 * @generated
		 */
		protected List getObjectsBeingDropped()
		{
			TransferData data = getCurrentEvent().currentDataType;
			Collection uris = new HashSet();

			Object transferedObject = getJavaObject(data);
			if (transferedObject instanceof IStructuredSelection)
			{
				IStructuredSelection selection = (IStructuredSelection) transferedObject;
				for (Iterator it = selection.iterator(); it.hasNext();)
				{
					Object nextSelectedObject = it.next();
					if (nextSelectedObject instanceof GpmnNavigatorItem)
					{
						View view = ((GpmnNavigatorItem) nextSelectedObject)
								.getView();
						nextSelectedObject = view.getElement();
					}
					else if (nextSelectedObject instanceof IAdaptable)
					{
						IAdaptable adaptable = (IAdaptable) nextSelectedObject;
						nextSelectedObject = adaptable
								.getAdapter(EObject.class);
					}

					if (nextSelectedObject instanceof EObject)
					{
						EObject modelElement = (EObject) nextSelectedObject;
						Resource modelElementResource = modelElement
								.eResource();
						uris.add(modelElementResource.getURI().appendFragment(
								modelElementResource
										.getURIFragment(modelElement)));
					}
				}
			}

			List result = new ArrayList();
			for (Iterator it = uris.iterator(); it.hasNext();)
			{
				URI nextURI = (URI) it.next();
				EObject modelObject = getEditingDomain().getResourceSet()
						.getEObject(nextURI, true);
				result.add(modelObject);
			}
			return result;
		}

		/**
		 * @generated
		 */
		protected abstract Object getJavaObject(TransferData data);

	}

	/**
	 * Creates a customized diagram edit domain
	 * @genrated NOT
	 */
	@Override
	protected void createDiagramEditDomain()
	{
		GpmnDiagramEditDomain editDomain = new GpmnDiagramEditDomain(this);
		editDomain.setActionManager(createActionManager());

		//Set<IElementType> types = new HashSet<IElementType>();
		//types.add(GpmnElementTypes.MessageGoal_2008);
		//types.add(GpmnElementTypes.MessagingEdge_4004);
		editDomain.setRemovedElementTypes(staticRemovedElementTypes);

		setEditDomain(editDomain);
	}

	/**
	 * Enable snap to geometry per default for diagrams
	 * @param root
	 */
	private void setupSnapToGeometry(DiagramRootEditPart root)
	{
		// TODO: use preference store like BPMN
		boolean enableSnapToGeometry = true;
		// GpmnDiagramEditorPlugin.getInstance().getPreferenceStore()
		// .getBoolean(GpmnDiagramPreferenceInitializer.PREF_SNAP_TO_GEOMETRY);
		root.getViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
				enableSnapToGeometry);
	}

}
