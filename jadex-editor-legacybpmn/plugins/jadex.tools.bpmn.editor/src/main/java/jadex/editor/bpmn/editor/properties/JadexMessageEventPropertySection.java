package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractBpmnPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.stp.bpmn.ActivityType;
import org.eclipse.stp.bpmn.diagram.actions.SetAsThrowingOrCatchingAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * 
 *
 */
public class JadexMessageEventPropertySection extends AbstractBpmnPropertySection
{

	// ---- attributes ----
	
	private CLabel msgtypeLabel;
	private Text msgtypeText;
	
	private CLabel messageLabel;
	private Text messageText;
	
	private CLabel expressionLabel;
	private Text expressionText;
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexMessageEventPropertySection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, JadexBpmnPropertiesUtil.JADEX_EVENT_MESSAGE_DETAIL);
	}

	// ---- methods ----
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Group sectionGroup = getWidgetFactory().createGroup(sectionComposite, JadexBpmnPropertiesUtil.JADEX_EVENT_MESSAGE_DETAIL);
		sectionComposite = sectionGroup;
		addDisposable(sectionComposite);
		
		// The layout of the section composite
		GridLayout layout = new GridLayout(2, false);
		sectionComposite.setLayout(layout);
		
		GridData labelGridData = new GridData();
		labelGridData.minimumWidth = 80;
		labelGridData.widthHint = 80;
		
		GridData textGridData = new GridData();
		textGridData.minimumWidth = 500;
		textGridData.widthHint = 500;

//		combo.addSelectionListener(new SelectionListener()
//		{
//			@Override
//			public void widgetSelected(SelectionEvent e)
//			{
//				int selectionIndex = ((CCombo) e.getSource()).getSelectionIndex();
//				updateControls(selectionIndex);
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e)
//			{
//				widgetSelected(e);
//			}
//		});
		
		msgtypeLabel = getWidgetFactory().createCLabel(sectionComposite, "msgtype"+":"); // //$NON-NLS-0$
		msgtypeLabel.setLayoutData(labelGridData);
		msgtypeText = getWidgetFactory().createText(sectionComposite, "", SWT.BORDER_SOLID);
		msgtypeText.setLayoutData(textGridData);
		msgtypeText.addModifyListener(new ModifyJadexEAnnotation("msgtype", msgtypeText));
		addDisposable(msgtypeLabel);
		addDisposable(msgtypeText);
		
		messageLabel = getWidgetFactory().createCLabel(sectionComposite, "message"+":"); // //$NON-NLS-0$
		messageLabel.setLayoutData(labelGridData);
		messageText = getWidgetFactory().createText(sectionComposite, "", SWT.BORDER_SOLID);
		messageText.setLayoutData(textGridData);
		messageText.addModifyListener(new ModifyJadexEAnnotation("message", messageText));
		addDisposable(messageLabel);
		addDisposable(messageText);
		
		expressionLabel = getWidgetFactory().createCLabel(sectionComposite, "match expression"+":"); // //$NON-NLS-0$
		expressionLabel.setLayoutData(labelGridData);
		expressionText = getWidgetFactory().createText(sectionComposite, "", SWT.BORDER_SOLID);
		expressionText.setLayoutData(textGridData);
		expressionText.addModifyListener(new ModifyJadexEAnnotation("match expression", expressionText));
		addDisposable(expressionLabel);
		addDisposable(expressionText);
	}
	

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		// dispose is done in superclass, see addDisposable
		super.dispose();
	}

	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if (modelElement != null)
		{
			EAnnotation ea = util.getJadexEAnnotation();
			if (ea != null)
			{
				String tmpValue;
				
				tmpValue = (String) ea.getDetails().get("msgtype");
				msgtypeText.setText(tmpValue != null ? tmpValue : "");
				
				tmpValue = (String) ea.getDetails().get("message");
				messageText.setText(tmpValue != null ? tmpValue : "");
				
				tmpValue = (String) ea.getDetails().get("expression");
				expressionText.setText(tmpValue != null ? tmpValue : "");

			}
			else
			{
				msgtypeText.setText("");
				messageText.setText("");
				expressionText.setText("");
			}
			
			updateControls(isModelElementThrowing(modelElement));
			return;
		}
	}

	

	// ---- methods ----
	
	private boolean isModelElementThrowing(EModelElement modelElement)
	{
		if (modelElement instanceof Activity)
		{
			// we have an activity with message type
			Activity model = (Activity) modelElement;
			if (ActivityType.EVENT_INTERMEDIATE_MESSAGE_LITERAL.equals(model
					.getActivityType())
					|| ActivityType.EVENT_START_MESSAGE_LITERAL.equals(model
							.getActivityType())
					|| ActivityType.EVENT_END_MESSAGE_LITERAL.equals(model
							.getActivityType()))
			{

				String annotation = EcoreUtil
						.getAnnotation(
								model,
								SetAsThrowingOrCatchingAction.IS_THROWING_ANNOTATION_SOURCE_AND_KEY_ID,
								SetAsThrowingOrCatchingAction.IS_THROWING_ANNOTATION_SOURCE_AND_KEY_ID);

				if (annotation != null && annotation.equals("true")  //$NON-NLS-1$
						|| !model.getOutgoingMessages().isEmpty())
				{ 
					return true;
				}
				
			}
		}
		return false;
	}
	
	private void updateControls(boolean isThrowing)
	{
		messageLabel.setEnabled(isThrowing);
		messageText.setEnabled(isThrowing);
		
		expressionLabel.setEnabled( ! isThrowing);
		expressionText.setEnabled( ! isThrowing);
	}
	
	// ---- internal classes ----
	
	/**
	 * Tracks the change occurring on the text field.
	 */
	private class ModifyJadexEAnnotation implements ModifyListener
	{
		private String key;
		private Text field;

		public ModifyJadexEAnnotation(String k, Text field)
		{
			key = k;
			this.field = field;
		}

		public void modifyText(ModifyEvent e)
		{
			if (modelElement == null)
			{ 
				// the value was just initialized
				return;
			}
			
			updateJadexEAnnotation(key, field.getText());
		}
	}
	
	
}

