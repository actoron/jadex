/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.navigator;

import jadex.tools.gpmn.diagram.edit.parts.AchieveGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.AssociationEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ContextEditPart;
import jadex.tools.gpmn.diagram.edit.parts.DataObjectEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GenericGpmnElementEditPart;
import jadex.tools.gpmn.diagram.edit.parts.GpmnDiagramEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MaintainGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessageGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.MessagingEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ParallelGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PerformGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.PlanEditPart;
import jadex.tools.gpmn.diagram.edit.parts.ProcessEditPart;
import jadex.tools.gpmn.diagram.edit.parts.QueryGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SequentialGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubGoalEdgeEditPart;
import jadex.tools.gpmn.diagram.edit.parts.SubProcessGoalEditPart;
import jadex.tools.gpmn.diagram.edit.parts.TextAnnotationEditPart;
import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;
import jadex.tools.gpmn.diagram.part.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gmf.runtime.emf.core.GMFEditingDomainFactory;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

/**
 * @generated
 */
public class GpmnNavigatorContentProvider implements ICommonContentProvider
{

	/**
	 * @generated
	 */
	private static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * @generated
	 */
	private Viewer myViewer;

	/**
	 * @generated
	 */
	private AdapterFactoryEditingDomain myEditingDomain;

	/**
	 * @generated
	 */
	private WorkspaceSynchronizer myWorkspaceSynchronizer;

	/**
	 * @generated
	 */
	private Runnable myViewerRefreshRunnable;

	/**
	 * @generated
	 */
	public GpmnNavigatorContentProvider()
	{
		TransactionalEditingDomain editingDomain = GMFEditingDomainFactory.INSTANCE
				.createEditingDomain();
		myEditingDomain = (AdapterFactoryEditingDomain) editingDomain;
		myEditingDomain.setResourceToReadOnlyMap(new HashMap()
		{
			public Object get(Object key)
			{
				if (!containsKey(key))
				{
					put(key, Boolean.TRUE);
				}
				return super.get(key);
			}
		});
		myViewerRefreshRunnable = new Runnable()
		{
			public void run()
			{
				if (myViewer != null)
				{
					myViewer.refresh();
				}
			}
		};
		myWorkspaceSynchronizer = new WorkspaceSynchronizer(editingDomain,
				new WorkspaceSynchronizer.Delegate()
				{
					public void dispose()
					{
					}

					public boolean handleResourceChanged(final Resource resource)
					{
						for (Iterator it = myEditingDomain.getResourceSet()
								.getResources().iterator(); it.hasNext();)
						{
							Resource nextResource = (Resource) it.next();
							nextResource.unload();
						}
						if (myViewer != null)
						{
							myViewer.getControl().getDisplay().asyncExec(
									myViewerRefreshRunnable);
						}
						return true;
					}

					public boolean handleResourceDeleted(Resource resource)
					{
						for (Iterator it = myEditingDomain.getResourceSet()
								.getResources().iterator(); it.hasNext();)
						{
							Resource nextResource = (Resource) it.next();
							nextResource.unload();
						}
						if (myViewer != null)
						{
							myViewer.getControl().getDisplay().asyncExec(
									myViewerRefreshRunnable);
						}
						return true;
					}

					public boolean handleResourceMoved(Resource resource,
							final URI newURI)
					{
						for (Iterator it = myEditingDomain.getResourceSet()
								.getResources().iterator(); it.hasNext();)
						{
							Resource nextResource = (Resource) it.next();
							nextResource.unload();
						}
						if (myViewer != null)
						{
							myViewer.getControl().getDisplay().asyncExec(
									myViewerRefreshRunnable);
						}
						return true;
					}
				});
	}

	/**
	 * @generated
	 */
	public void dispose()
	{
		myWorkspaceSynchronizer.dispose();
		myWorkspaceSynchronizer = null;
		myViewerRefreshRunnable = null;
		for (Iterator it = myEditingDomain.getResourceSet().getResources()
				.iterator(); it.hasNext();)
		{
			Resource resource = (Resource) it.next();
			resource.unload();
		}
		((TransactionalEditingDomain) myEditingDomain).dispose();
		myEditingDomain = null;
	}

	/**
	 * @generated
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		myViewer = viewer;
	}

	/**
	 * @generated
	 */
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	/**
	 * @generated
	 */
	public void restoreState(IMemento aMemento)
	{
	}

	/**
	 * @generated
	 */
	public void saveState(IMemento aMemento)
	{
	}

	/**
	 * @generated
	 */
	public void init(ICommonContentExtensionSite aConfig)
	{
	}

	/**
	 * @generated
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof IFile)
		{
			IFile file = (IFile) parentElement;
			URI fileURI = URI.createPlatformResourceURI(file.getFullPath()
					.toString(), true);
			Resource resource = myEditingDomain.getResourceSet().getResource(
					fileURI, true);
			Collection result = new ArrayList();
			result
					.addAll(createNavigatorItems(selectViewsByType(resource
							.getContents(), GpmnDiagramEditPart.MODEL_ID),
							file, false));
			return result.toArray();
		}

		if (parentElement instanceof GpmnNavigatorGroup)
		{
			GpmnNavigatorGroup group = (GpmnNavigatorGroup) parentElement;
			return group.getChildren();
		}

		if (parentElement instanceof GpmnNavigatorItem)
		{
			GpmnNavigatorItem navigatorItem = (GpmnNavigatorItem) parentElement;
			if (navigatorItem.isLeaf() || !isOwnView(navigatorItem.getView()))
			{
				return EMPTY_ARRAY;
			}
			return getViewChildren(navigatorItem.getView(), parentElement);
		}

		/*
		 * Due to plugin.xml restrictions this code will be called only for views representing
		 * shortcuts to this diagram elements created on other diagrams. 
		 */
		if (parentElement instanceof IAdaptable)
		{
			View view = (View) ((IAdaptable) parentElement)
					.getAdapter(View.class);
			if (view != null)
			{
				return getViewChildren(view, parentElement);
			}
		}

		return EMPTY_ARRAY;
	}

	/**
	 * @generated
	 */
	private Object[] getViewChildren(View view, Object parentElement)
	{
		switch (GpmnVisualIDRegistry.getVisualID(view))
		{

			case GpmnDiagramEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				result
						.addAll(getForeignShortcuts((Diagram) view,
								parentElement));
				GpmnNavigatorGroup links = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_GpmnDiagram_1000_links,
						"icons/linksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getChildrenByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ProcessEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(AchieveGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(MaintainGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(PerformGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(QueryGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(SequentialGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(ParallelGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(MessageGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(SubProcessGoalEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry.getType(PlanEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry.getType(ContextEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(TextAnnotationEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(DataObjectEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getChildrenByType(Collections.singleton(view),
						GpmnVisualIDRegistry
								.getType(GenericGpmnElementEditPart.VISUAL_ID));
				result.addAll(createNavigatorItems(connectedViews,
						parentElement, false));
				connectedViews = getDiagramLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				links.addChildren(createNavigatorItems(connectedViews, links,
						false));
				connectedViews = getDiagramLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				links.addChildren(createNavigatorItems(connectedViews, links,
						false));
				connectedViews = getDiagramLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				links.addChildren(createNavigatorItems(connectedViews, links,
						false));
				connectedViews = getDiagramLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessagingEdgeEditPart.VISUAL_ID));
				links.addChildren(createNavigatorItems(connectedViews, links,
						false));
				connectedViews = getDiagramLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				links.addChildren(createNavigatorItems(connectedViews, links,
						false));
				if (!links.isEmpty())
				{
					result.add(links);
				}
				return result.toArray();
			}

			case ProcessEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_Process_2001_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_Process_2001_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessagingEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessagingEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case AchieveGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_AchieveGoal_2002_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_AchieveGoal_2002_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case MaintainGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_MaintainGoal_2003_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_MaintainGoal_2003_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case PerformGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_PerformGoal_2004_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_PerformGoal_2004_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case QueryGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_QueryGoal_2005_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_QueryGoal_2005_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case SequentialGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_SequentialGoal_2006_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_SequentialGoal_2006_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case ParallelGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_ParallelGoal_2007_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_ParallelGoal_2007_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case MessageGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_MessageGoal_2008_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_MessageGoal_2008_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessagingEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessagingEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case SubProcessGoalEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_SubProcessGoal_2009_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_SubProcessGoal_2009_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case PlanEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_Plan_2010_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_Plan_2010_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case ContextEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_Context_2011_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case TextAnnotationEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_TextAnnotation_2012_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case DataObjectEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_DataObject_2013_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case GenericGpmnElementEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup incominglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_GenericGpmnElement_2014_incominglinks,
						"icons/incomingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup outgoinglinks = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_GenericGpmnElement_2014_outgoinglinks,
						"icons/outgoingLinksNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AssociationEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubGoalEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				connectedViews = getIncomingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				incominglinks.addChildren(createNavigatorItems(connectedViews,
						incominglinks, true));
				connectedViews = getOutgoingLinksByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnEdgeEditPart.VISUAL_ID));
				outgoinglinks.addChildren(createNavigatorItems(connectedViews,
						outgoinglinks, true));
				if (!incominglinks.isEmpty())
				{
					result.add(incominglinks);
				}
				if (!outgoinglinks.isEmpty())
				{
					result.add(outgoinglinks);
				}
				return result.toArray();
			}

			case AssociationEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup target = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_Association_4001_target,
						"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup source = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_Association_4001_source,
						"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ProcessEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AchieveGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MaintainGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PerformGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(QueryGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SequentialGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ParallelGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubProcessGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnElementEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ContextEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(TextAnnotationEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(DataObjectEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				if (!target.isEmpty())
				{
					result.add(target);
				}
				if (!source.isEmpty())
				{
					result.add(source);
				}
				return result.toArray();
			}

			case SubGoalEdgeEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup target = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_SubGoalEdge_4002_target,
						"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup source = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_SubGoalEdge_4002_source,
						"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AchieveGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MaintainGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PerformGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(QueryGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SequentialGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ParallelGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubProcessGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnElementEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AchieveGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MaintainGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PerformGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(QueryGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SequentialGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ParallelGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubProcessGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnElementEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				if (!target.isEmpty())
				{
					result.add(target);
				}
				if (!source.isEmpty())
				{
					result.add(source);
				}
				return result.toArray();
			}

			case PlanEdgeEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup target = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_PlanEdge_4003_target,
						"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup source = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_PlanEdge_4003_source,
						"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AchieveGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MaintainGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PerformGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(QueryGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SequentialGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ParallelGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubProcessGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnElementEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AchieveGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MaintainGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PerformGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(QueryGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SequentialGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ParallelGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubProcessGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnElementEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				if (!target.isEmpty())
				{
					result.add(target);
				}
				if (!source.isEmpty())
				{
					result.add(source);
				}
				return result.toArray();
			}

			case MessagingEdgeEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup target = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_MessagingEdge_4004_target,
						"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup source = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_MessagingEdge_4004_source,
						"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ProcessEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ProcessEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				if (!target.isEmpty())
				{
					result.add(target);
				}
				if (!source.isEmpty())
				{
					result.add(source);
				}
				return result.toArray();
			}

			case GenericGpmnEdgeEditPart.VISUAL_ID:
			{
				Collection result = new ArrayList();
				GpmnNavigatorGroup target = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_GenericGpmnEdge_4005_target,
						"icons/linkTargetNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				GpmnNavigatorGroup source = new GpmnNavigatorGroup(
						Messages.NavigatorGroupName_GenericGpmnEdge_4005_source,
						"icons/linkSourceNavigatorGroup.gif", parentElement); //$NON-NLS-1$
				Collection connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AchieveGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MaintainGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PerformGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(QueryGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SequentialGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ParallelGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubProcessGoalEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksTargetByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnElementEditPart.VISUAL_ID));
				target.addChildren(createNavigatorItems(connectedViews, target,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(AchieveGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MaintainGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PerformGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(QueryGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SequentialGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(ParallelGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(MessageGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(SubProcessGoalEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(PlanEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				connectedViews = getLinksSourceByType(Collections
						.singleton(view), GpmnVisualIDRegistry
						.getType(GenericGpmnElementEditPart.VISUAL_ID));
				source.addChildren(createNavigatorItems(connectedViews, source,
						true));
				if (!target.isEmpty())
				{
					result.add(target);
				}
				if (!source.isEmpty())
				{
					result.add(source);
				}
				return result.toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	/**
	 * @generated
	 */
	private Collection getLinksSourceByType(Collection edges, String type)
	{
		Collection result = new ArrayList();
		for (Iterator it = edges.iterator(); it.hasNext();)
		{
			Edge nextEdge = (Edge) it.next();
			View nextEdgeSource = nextEdge.getSource();
			if (type.equals(nextEdgeSource.getType())
					&& isOwnView(nextEdgeSource))
			{
				result.add(nextEdgeSource);
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection getLinksTargetByType(Collection edges, String type)
	{
		Collection result = new ArrayList();
		for (Iterator it = edges.iterator(); it.hasNext();)
		{
			Edge nextEdge = (Edge) it.next();
			View nextEdgeTarget = nextEdge.getTarget();
			if (type.equals(nextEdgeTarget.getType())
					&& isOwnView(nextEdgeTarget))
			{
				result.add(nextEdgeTarget);
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection getOutgoingLinksByType(Collection nodes, String type)
	{
		Collection result = new ArrayList();
		for (Iterator it = nodes.iterator(); it.hasNext();)
		{
			View nextNode = (View) it.next();
			result.addAll(selectViewsByType(nextNode.getSourceEdges(), type));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection getIncomingLinksByType(Collection nodes, String type)
	{
		Collection result = new ArrayList();
		for (Iterator it = nodes.iterator(); it.hasNext();)
		{
			View nextNode = (View) it.next();
			result.addAll(selectViewsByType(nextNode.getTargetEdges(), type));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection getChildrenByType(Collection nodes, String type)
	{
		Collection result = new ArrayList();
		for (Iterator it = nodes.iterator(); it.hasNext();)
		{
			View nextNode = (View) it.next();
			result.addAll(selectViewsByType(nextNode.getChildren(), type));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection getDiagramLinksByType(Collection diagrams, String type)
	{
		Collection result = new ArrayList();
		for (Iterator it = diagrams.iterator(); it.hasNext();)
		{
			Diagram nextDiagram = (Diagram) it.next();
			result.addAll(selectViewsByType(nextDiagram.getEdges(), type));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection selectViewsByType(Collection views, String type)
	{
		Collection result = new ArrayList();
		for (Iterator it = views.iterator(); it.hasNext();)
		{
			View nextView = (View) it.next();
			if (type.equals(nextView.getType()) && isOwnView(nextView))
			{
				result.add(nextView);
			}
		}
		return result;
	}

	/**
	 * @generated
	 */
	private boolean isOwnView(View view)
	{
		return GpmnDiagramEditPart.MODEL_ID.equals(GpmnVisualIDRegistry
				.getModelID(view));
	}

	/**
	 * @generated
	 */
	private Collection createNavigatorItems(Collection views, Object parent,
			boolean isLeafs)
	{
		Collection result = new ArrayList();
		for (Iterator it = views.iterator(); it.hasNext();)
		{
			result
					.add(new GpmnNavigatorItem((View) it.next(), parent,
							isLeafs));
		}
		return result;
	}

	/**
	 * @generated
	 */
	private Collection getForeignShortcuts(Diagram diagram, Object parent)
	{
		Collection result = new ArrayList();
		for (Iterator it = diagram.getChildren().iterator(); it.hasNext();)
		{
			View nextView = (View) it.next();
			if (!isOwnView(nextView)
					&& nextView.getEAnnotation("Shortcut") != null) { //$NON-NLS-1$
				result.add(nextView);
			}
		}
		return createNavigatorItems(result, parent, false);
	}

	/**
	 * @generated
	 */
	public Object getParent(Object element)
	{
		if (element instanceof GpmnAbstractNavigatorItem)
		{
			GpmnAbstractNavigatorItem abstractNavigatorItem = (GpmnAbstractNavigatorItem) element;
			return abstractNavigatorItem.getParent();
		}
		return null;
	}

	/**
	 * @generated
	 */
	public boolean hasChildren(Object element)
	{
		return element instanceof IFile || getChildren(element).length > 0;
	}

}
