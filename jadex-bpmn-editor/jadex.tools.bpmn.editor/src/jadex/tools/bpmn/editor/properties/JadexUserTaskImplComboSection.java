/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.emf.ecore.xml.type.internal.RegEx.RegularExpression;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexUserTaskImplComboSection extends
		AbstractComboPropertySection
{

	// ---- constants ----
	
	private static final String[] comboItems = new String[] {
		"LARS:replace_value_in_AbstractComboPropertySection.class",
		"LARS:replace_value_in_AbstractComboPropertySection.class",
		"LARS:replace_value_in_AbstractComboPropertySection.class",
		"LARS:replace_value_in_AbstractComboPropertySection.class",
		"LARS:replace_value_in_AbstractComboPropertySection.class"
	};
	
	// ---- attributes ----


	// ---- constructor ----

	/**
	 * Default constructor, initializes super class
	 */
	public JadexUserTaskImplComboSection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_ACTIVITY_CLASS_DETAIL,
				comboItems /*, Messages.JadexUserTaskImplComboSection_ImplementationClass_label*/);
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.editor.properties.AbstractComboPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		
		super.createControls(parent, aTabbedPropertySheetPage);
		
		// Add some listeners to the abstract combo
		
		cCombo.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String text = cCombo.getText();
				String newText = text.substring(0, e.start) + e.text
						+ text.substring(e.end);
				
				// don't allow non word characters
				RegularExpression re = new RegularExpression("\\w*"); //$NON-NLS-1$
				if (!re.matches(newText))
				{
					e.doit = false;
				}
			}
		});
		
		cCombo.addTraverseListener(new TraverseListener()
		{
			public void keyTraversed(TraverseEvent e)
			{
				if (e.detail == SWT.TRAVERSE_RETURN)
				{
					e.doit = false;
					e.detail = SWT.TRAVERSE_NONE;
					String newText = cCombo.getText();

					// check if we have a valid class name
					if (newText.endsWith(".class")) //$NON-NLS-1$
					{
						cCombo.add(newText);
						cCombo.setSelection(new Point(0, newText
								.length()));
					}

				}
			}
		});
		
		
	}

	// ---- methods ----
	
	

}
