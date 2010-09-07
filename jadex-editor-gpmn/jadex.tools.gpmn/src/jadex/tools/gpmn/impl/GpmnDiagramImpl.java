/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *
 * $Id$
 */
package jadex.tools.gpmn.impl;

import jadex.tools.gpmn.AbstractPlan;
import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.Context;
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnPackage;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.SubProcess;

import jadex.tools.gpmn.SuppressionEdge;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getPackage <em>Package</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getImports <em>Imports</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getContext <em>Context</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getGoals <em>Goals</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getPlans <em>Plans</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getSubProcesses <em>Sub Processes</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getActivationEdges <em>Activation Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getPlanEdges <em>Plan Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getSuppressionEdges <em>Suppression Edges</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getRevision <em>Revision</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link jadex.tools.gpmn.impl.GpmnDiagramImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GpmnDiagramImpl extends NamedObjectImpl implements GpmnDiagram
{
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2009, Universität Hamburg\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n"; //$NON-NLS-1$

	/**
	 * The default value of the '{@link #getPackage() <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackage()
	 * @generated
	 * @ordered
	 */
	protected static final String PACKAGE_EDEFAULT = ""; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getPackage() <em>Package</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackage()
	 * @generated
	 * @ordered
	 */
	protected String package_ = PACKAGE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getImports() <em>Imports</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImports()
	 * @generated
	 * @ordered
	 */
	protected EList<String> imports;

	/**
	 * The cached value of the '{@link #getContext() <em>Context</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContext()
	 * @generated
	 * @ordered
	 */
	protected Context context;

	/**
	 * The cached value of the '{@link #getGoals() <em>Goals</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGoals()
	 * @generated
	 * @ordered
	 */
	protected EList<Goal> goals;

	/**
	 * The cached value of the '{@link #getPlans() <em>Plans</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPlans()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractPlan> plans;

	/**
	 * The cached value of the '{@link #getSubProcesses() <em>Sub Processes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubProcesses()
	 * @generated
	 * @ordered
	 */
	protected EList<SubProcess> subProcesses;

	/**
	 * The cached value of the '{@link #getActivationEdges() <em>Activation Edges</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActivationEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<ActivationEdge> activationEdges;

	/**
	 * The cached value of the '{@link #getPlanEdges() <em>Plan Edges</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPlanEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<PlanEdge> planEdges;

	/**
	 * The cached value of the '{@link #getSuppressionEdges() <em>Suppression Edges</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuppressionEdges()
	 * @generated
	 * @ordered
	 */
	protected EList<SuppressionEdge> suppressionEdges;

	/**
	 * The default value of the '{@link #getAuthor() <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected static final String AUTHOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected String author = AUTHOR_EDEFAULT;

	/**
	 * This is true if the Author attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean authorESet;

	/**
	 * The default value of the '{@link #getRevision() <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected static final String REVISION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRevision() <em>Revision</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRevision()
	 * @generated
	 * @ordered
	 */
	protected String revision = REVISION_EDEFAULT;

	/**
	 * This is true if the Revision attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean revisionESet;

	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * This is true if the Title attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean titleESet;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * This is true if the Version attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean versionESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected GpmnDiagramImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass()
	{
		return GpmnPackage.Literals.GPMN_DIAGRAM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAuthor()
	{
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAuthor(String newAuthor)
	{
		String oldAuthor = author;
		author = newAuthor;
		boolean oldAuthorESet = authorESet;
		authorESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GPMN_DIAGRAM__AUTHOR, oldAuthor, author, !oldAuthorESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetAuthor()
	{
		String oldAuthor = author;
		boolean oldAuthorESet = authorESet;
		author = AUTHOR_EDEFAULT;
		authorESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GPMN_DIAGRAM__AUTHOR, oldAuthor, AUTHOR_EDEFAULT, oldAuthorESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetAuthor()
	{
		return authorESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRevision()
	{
		return revision;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRevision(String newRevision)
	{
		String oldRevision = revision;
		revision = newRevision;
		boolean oldRevisionESet = revisionESet;
		revisionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GPMN_DIAGRAM__REVISION, oldRevision, revision, !oldRevisionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRevision()
	{
		String oldRevision = revision;
		boolean oldRevisionESet = revisionESet;
		revision = REVISION_EDEFAULT;
		revisionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GPMN_DIAGRAM__REVISION, oldRevision, REVISION_EDEFAULT, oldRevisionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRevision()
	{
		return revisionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitle(String newTitle)
	{
		String oldTitle = title;
		title = newTitle;
		boolean oldTitleESet = titleESet;
		titleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GPMN_DIAGRAM__TITLE, oldTitle, title, !oldTitleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTitle()
	{
		String oldTitle = title;
		boolean oldTitleESet = titleESet;
		title = TITLE_EDEFAULT;
		titleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GPMN_DIAGRAM__TITLE, oldTitle, TITLE_EDEFAULT, oldTitleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetTitle()
	{
		return titleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(String newVersion)
	{
		String oldVersion = version;
		version = newVersion;
		boolean oldVersionESet = versionESet;
		versionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GPMN_DIAGRAM__VERSION, oldVersion, version, !oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetVersion()
	{
		String oldVersion = version;
		boolean oldVersionESet = versionESet;
		version = VERSION_EDEFAULT;
		versionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, GpmnPackage.GPMN_DIAGRAM__VERSION, oldVersion, VERSION_EDEFAULT, oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetVersion()
	{
		return versionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Context getContext()
	{
		return context;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetContext(Context newContext, NotificationChain msgs)
	{
		Context oldContext = context;
		context = newContext;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GpmnPackage.GPMN_DIAGRAM__CONTEXT, oldContext, newContext);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContext(Context newContext)
	{
		if (newContext != context)
		{
			NotificationChain msgs = null;
			if (context != null)
				msgs = ((InternalEObject)context).eInverseRemove(this, GpmnPackage.CONTEXT__GPMN_DIAGRAM, Context.class, msgs);
			if (newContext != null)
				msgs = ((InternalEObject)newContext).eInverseAdd(this, GpmnPackage.CONTEXT__GPMN_DIAGRAM, Context.class, msgs);
			msgs = basicSetContext(newContext, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GPMN_DIAGRAM__CONTEXT, newContext, newContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Goal> getGoals()
	{
		if (goals == null)
		{
			goals = new EObjectContainmentWithInverseEList.Unsettable<Goal>(Goal.class, this, GpmnPackage.GPMN_DIAGRAM__GOALS, GpmnPackage.GOAL__GPMN_DIAGRAM);
		}
		return goals;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetGoals()
	{
		if (goals != null) ((InternalEList.Unsettable<?>)goals).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetGoals()
	{
		return goals != null && ((InternalEList.Unsettable<?>)goals).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<AbstractPlan> getPlans()
	{
		if (plans == null)
		{
			plans = new EObjectContainmentWithInverseEList.Unsettable<AbstractPlan>(AbstractPlan.class, this, GpmnPackage.GPMN_DIAGRAM__PLANS, GpmnPackage.ABSTRACT_PLAN__GPMN_DIAGRAM);
		}
		return plans;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPlans()
	{
		if (plans != null) ((InternalEList.Unsettable<?>)plans).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPlans()
	{
		return plans != null && ((InternalEList.Unsettable<?>)plans).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SubProcess> getSubProcesses()
	{
		if (subProcesses == null)
		{
			subProcesses = new EObjectContainmentWithInverseEList.Unsettable<SubProcess>(SubProcess.class, this, GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES, GpmnPackage.SUB_PROCESS__GPMN_DIAGRAM);
		}
		return subProcesses;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSubProcesses()
	{
		if (subProcesses != null) ((InternalEList.Unsettable<?>)subProcesses).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSubProcesses()
	{
		return subProcesses != null && ((InternalEList.Unsettable<?>)subProcesses).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ActivationEdge> getActivationEdges()
	{
		if (activationEdges == null)
		{
			activationEdges = new EObjectContainmentWithInverseEList.Unsettable<ActivationEdge>(ActivationEdge.class, this, GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES, GpmnPackage.ACTIVATION_EDGE__GPMN_DIAGRAM);
		}
		return activationEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetActivationEdges()
	{
		if (activationEdges != null) ((InternalEList.Unsettable<?>)activationEdges).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetActivationEdges()
	{
		return activationEdges != null && ((InternalEList.Unsettable<?>)activationEdges).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PlanEdge> getPlanEdges()
	{
		if (planEdges == null)
		{
			planEdges = new EObjectContainmentWithInverseEList.Unsettable<PlanEdge>(PlanEdge.class, this, GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES, GpmnPackage.PLAN_EDGE__GPMN_DIAGRAM);
		}
		return planEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetPlanEdges()
	{
		if (planEdges != null) ((InternalEList.Unsettable<?>)planEdges).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetPlanEdges()
	{
		return planEdges != null && ((InternalEList.Unsettable<?>)planEdges).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SuppressionEdge> getSuppressionEdges()
	{
		if (suppressionEdges == null)
		{
			suppressionEdges = new EObjectContainmentWithInverseEList.Unsettable<SuppressionEdge>(SuppressionEdge.class, this, GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES, GpmnPackage.SUPPRESSION_EDGE__GPMN_DIAGRAM);
		}
		return suppressionEdges;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSuppressionEdges()
	{
		if (suppressionEdges != null) ((InternalEList.Unsettable<?>)suppressionEdges).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSuppressionEdges()
	{
		return suppressionEdges != null && ((InternalEList.Unsettable<?>)suppressionEdges).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPackage()
	{
		return package_;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPackage(String newPackage)
	{
		String oldPackage = package_;
		package_ = newPackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GpmnPackage.GPMN_DIAGRAM__PACKAGE, oldPackage, package_));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getImports()
	{
		if (imports == null)
		{
			imports = new EDataTypeUniqueEList.Unsettable<String>(String.class, this, GpmnPackage.GPMN_DIAGRAM__IMPORTS);
		}
		return imports;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetImports()
	{
		if (imports != null) ((InternalEList.Unsettable<?>)imports).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetImports()
	{
		return imports != null && ((InternalEList.Unsettable<?>)imports).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case GpmnPackage.GPMN_DIAGRAM__CONTEXT:
				if (context != null)
					msgs = ((InternalEObject)context).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GpmnPackage.GPMN_DIAGRAM__CONTEXT, null, msgs);
				return basicSetContext((Context)otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__GOALS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getGoals()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__PLANS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getPlans()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getSubProcesses()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getActivationEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getPlanEdges()).basicAdd(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getSuppressionEdges()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs)
	{
		switch (featureID)
		{
			case GpmnPackage.GPMN_DIAGRAM__CONTEXT:
				return basicSetContext(null, msgs);
			case GpmnPackage.GPMN_DIAGRAM__GOALS:
				return ((InternalEList<?>)getGoals()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__PLANS:
				return ((InternalEList<?>)getPlans()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES:
				return ((InternalEList<?>)getSubProcesses()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES:
				return ((InternalEList<?>)getActivationEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES:
				return ((InternalEList<?>)getPlanEdges()).basicRemove(otherEnd, msgs);
			case GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES:
				return ((InternalEList<?>)getSuppressionEdges()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType)
	{
		switch (featureID)
		{
			case GpmnPackage.GPMN_DIAGRAM__PACKAGE:
				return getPackage();
			case GpmnPackage.GPMN_DIAGRAM__IMPORTS:
				return getImports();
			case GpmnPackage.GPMN_DIAGRAM__CONTEXT:
				return getContext();
			case GpmnPackage.GPMN_DIAGRAM__GOALS:
				return getGoals();
			case GpmnPackage.GPMN_DIAGRAM__PLANS:
				return getPlans();
			case GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES:
				return getSubProcesses();
			case GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES:
				return getActivationEdges();
			case GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES:
				return getPlanEdges();
			case GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES:
				return getSuppressionEdges();
			case GpmnPackage.GPMN_DIAGRAM__AUTHOR:
				return getAuthor();
			case GpmnPackage.GPMN_DIAGRAM__REVISION:
				return getRevision();
			case GpmnPackage.GPMN_DIAGRAM__TITLE:
				return getTitle();
			case GpmnPackage.GPMN_DIAGRAM__VERSION:
				return getVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue)
	{
		switch (featureID)
		{
			case GpmnPackage.GPMN_DIAGRAM__PACKAGE:
				setPackage((String)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__IMPORTS:
				getImports().clear();
				getImports().addAll((Collection<? extends String>)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__CONTEXT:
				setContext((Context)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__GOALS:
				getGoals().clear();
				getGoals().addAll((Collection<? extends Goal>)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__PLANS:
				getPlans().clear();
				getPlans().addAll((Collection<? extends AbstractPlan>)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES:
				getSubProcesses().clear();
				getSubProcesses().addAll((Collection<? extends SubProcess>)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES:
				getActivationEdges().clear();
				getActivationEdges().addAll((Collection<? extends ActivationEdge>)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES:
				getPlanEdges().clear();
				getPlanEdges().addAll((Collection<? extends PlanEdge>)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES:
				getSuppressionEdges().clear();
				getSuppressionEdges().addAll((Collection<? extends SuppressionEdge>)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__AUTHOR:
				setAuthor((String)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__REVISION:
				setRevision((String)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__TITLE:
				setTitle((String)newValue);
				return;
			case GpmnPackage.GPMN_DIAGRAM__VERSION:
				setVersion((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID)
	{
		switch (featureID)
		{
			case GpmnPackage.GPMN_DIAGRAM__PACKAGE:
				setPackage(PACKAGE_EDEFAULT);
				return;
			case GpmnPackage.GPMN_DIAGRAM__IMPORTS:
				unsetImports();
				return;
			case GpmnPackage.GPMN_DIAGRAM__CONTEXT:
				setContext((Context)null);
				return;
			case GpmnPackage.GPMN_DIAGRAM__GOALS:
				unsetGoals();
				return;
			case GpmnPackage.GPMN_DIAGRAM__PLANS:
				unsetPlans();
				return;
			case GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES:
				unsetSubProcesses();
				return;
			case GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES:
				unsetActivationEdges();
				return;
			case GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES:
				unsetPlanEdges();
				return;
			case GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES:
				unsetSuppressionEdges();
				return;
			case GpmnPackage.GPMN_DIAGRAM__AUTHOR:
				unsetAuthor();
				return;
			case GpmnPackage.GPMN_DIAGRAM__REVISION:
				unsetRevision();
				return;
			case GpmnPackage.GPMN_DIAGRAM__TITLE:
				unsetTitle();
				return;
			case GpmnPackage.GPMN_DIAGRAM__VERSION:
				unsetVersion();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID)
	{
		switch (featureID)
		{
			case GpmnPackage.GPMN_DIAGRAM__PACKAGE:
				return PACKAGE_EDEFAULT == null ? package_ != null : !PACKAGE_EDEFAULT.equals(package_);
			case GpmnPackage.GPMN_DIAGRAM__IMPORTS:
				return isSetImports();
			case GpmnPackage.GPMN_DIAGRAM__CONTEXT:
				return context != null;
			case GpmnPackage.GPMN_DIAGRAM__GOALS:
				return isSetGoals();
			case GpmnPackage.GPMN_DIAGRAM__PLANS:
				return isSetPlans();
			case GpmnPackage.GPMN_DIAGRAM__SUB_PROCESSES:
				return isSetSubProcesses();
			case GpmnPackage.GPMN_DIAGRAM__ACTIVATION_EDGES:
				return isSetActivationEdges();
			case GpmnPackage.GPMN_DIAGRAM__PLAN_EDGES:
				return isSetPlanEdges();
			case GpmnPackage.GPMN_DIAGRAM__SUPPRESSION_EDGES:
				return isSetSuppressionEdges();
			case GpmnPackage.GPMN_DIAGRAM__AUTHOR:
				return isSetAuthor();
			case GpmnPackage.GPMN_DIAGRAM__REVISION:
				return isSetRevision();
			case GpmnPackage.GPMN_DIAGRAM__TITLE:
				return isSetTitle();
			case GpmnPackage.GPMN_DIAGRAM__VERSION:
				return isSetVersion();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString()
	{
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (package: "); //$NON-NLS-1$
		result.append(package_);
		result.append(", imports: "); //$NON-NLS-1$
		result.append(imports);
		result.append(", author: "); //$NON-NLS-1$
		if (authorESet) result.append(author); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", revision: "); //$NON-NLS-1$
		if (revisionESet) result.append(revision); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", title: "); //$NON-NLS-1$
		if (titleESet) result.append(title); else result.append("<unset>"); //$NON-NLS-1$
		result.append(", version: "); //$NON-NLS-1$
		if (versionESet) result.append(version); else result.append("<unset>"); //$NON-NLS-1$
		result.append(')');
		return result.toString();
	}

} //GpmnDiagramImpl
