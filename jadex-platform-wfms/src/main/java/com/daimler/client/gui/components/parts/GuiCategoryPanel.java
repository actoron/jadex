// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components.parts;

import jadex.bpmn.model.MParameter;
import jadex.wfms.client.IWorkitem;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.daimler.client.gui.components.AbstractInputPanel;
import com.daimler.util.swing.SpringUtils;
import com.daimler.util.swing.SwingUtils;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class GuiCategoryPanel extends JPanel
{
	private IWorkitem workitem;

	private String theCategoryName;

	private boolean enabled = true;

	private boolean changed = false;

	private Color theBackgroundColor;

	private ArrayList theInputFields;

	/*public GuiCategoryPanel(Collection parameters, String categoryName)
	{
		this(new HashMap(), parameters, categoryName, (new JPanel()).getBackground());
	}*/

	public GuiCategoryPanel(IWorkitem workitem, String categoryName, Color bgColor)
	{
		super();
		this.workitem = workitem;
		this.theCategoryName = categoryName;
		this.theBackgroundColor = bgColor;
		this.setBackground(Color.WHITE);
		init();
	}

	private void init()
	{
		// create all input fields for every task in the list of taskproperties
		// order them by their weight
		// add them to this panel
		theInputFields = new ArrayList();
		Set paramNames = workitem.getParameterNames();
		for(Iterator it = paramNames.iterator(); it.hasNext(); )
		{
			// System.out.println("Creating panel for: " +
			// theCategoryProperties.get(i).getTheName());
			String paramName = (String) it.next();
			Object initVal = workitem.getParameterValue(paramName);
			AbstractInputPanel inputField = InputFieldFactory.createInputPanel(paramName, paramName, "", "",
					theBackgroundColor, true, workitem.getParameterType(paramName), initVal);
			if (workitem.isReadOnly(paramName))
			{
				System.out.println("Read only: " + paramName);
				inputField.setEditable(false);
				System.out.println(inputField.isEditable());
			}
			theInputFields.add(inputField);
		}
		AbstractInputPanel[] fields = (AbstractInputPanel[]) theInputFields
				.toArray(new AbstractInputPanel[0]);
		Arrays.sort(fields);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		int iFields = -1;
		if (fields.length > 0)
		{
			iFields = fields[0].getFieldCategory();
		}
		int iCounter = 0;
		JPanel theAddPanel = new JPanel(new SpringLayout());
		theAddPanel.setBackground(Color.WHITE);
		for (int i = 0; i < fields.length; i++)
		{
			if (iFields == fields[i].getFieldCategory())
			{
				if (fields[i].getLabelPanel() != null)
				{
					theAddPanel.add(fields[i].getLabelPanel());
				}
				theAddPanel.add(fields[i]);
				iCounter++;
			} else
			{
				SpringUtils.makeCompactGrid(theAddPanel, iCounter, iFields, 0,
						0, 3, 3);
				add(theAddPanel);
				iFields = fields[i].getFieldCategory();
				theAddPanel = new JPanel(new SpringLayout());
				if (fields[i].getLabelPanel() != null)
				{
					theAddPanel.add(fields[i].getLabelPanel());
				}
				theAddPanel.add(fields[i]);
				iCounter = 1;
			}
		}
		SpringUtils.makeCompactGrid(theAddPanel, iCounter, iFields, 0, 0, 3, 3);
		add(theAddPanel);
		setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEmptyBorder(5, 0, 5, 0), theCategoryName));
	}

	public String getTheCategoryName()
	{
		return theCategoryName;
	}
	
	public Map getTheFetchedData()
	{
        Map ret = new HashMap(theInputFields.size());
        Iterator it = theInputFields.iterator();
        while (it.hasNext())
        {
        	AbstractInputPanel panel = (AbstractInputPanel) it.next();
        	if (panel.isEditable())
            	ret.put(panel.getName(), panel.getCurrentValue());
        }
        return ret;
    }

	/**
	 * Checks all TaskProperties in this category whether they have proper
	 * values filled in.
	 * 
	 * @return true if all TaskProperties are filled
	 */
	public final boolean isCategoryFilled()
	{
		boolean bFilled = true;
		for (int j = 0; j < theInputFields.size(); j++)
		{
			AbstractInputPanel ip;
			ip = (AbstractInputPanel) theInputFields.get(j);
			if (ip.isInputRequired())
			{
				boolean bTaskFilled = ip.isValueFilled();
				bFilled = bFilled & bTaskFilled;
				if (!bTaskFilled)
				{
					ip.markError();
				} else
				{
					ip.markOK();
				}
			}
			/*boolean bValidValue = false;
			try
			{
				bValidValue = ip.getTheAccessible().getTheDomain()
						.isLegalValue(ip.getTheCurrentValue());
			} catch (DomainException err)
			{
				// do nothing here
			}
			if (!bValidValue)
			{
				bFilled = false;
				ip.markError();
			} else
			{*/
				// mark it only as OK if it is not a required field that might
				// be marked false already
				if (!ip.isInputRequired())
				{
					ip.markOK();
				}
			//}
		}
		return bFilled;
	}

	/**
	 * Enables or disables all components in this categorypanel recursivly.
	 * 
	 * @param enable
	 *            true if the panel should be enabled
	 */
	public final void setEnabled(final boolean enable)
	{
		enabled = enable;
		Component[] comps = getComponents();
		for (int i = 0; i < comps.length; i++)
		{
			SwingUtils.setContainerEnabled(comps[i], enable);
		}
		super.setEnabled(enable);
	}

	/**
	 * Returns the state of the categorypanel and all its components.
	 * 
	 * @return true if the categorypanel is enabled
	 */
	public final boolean isEnabled()
	{
		return enabled;
	}

}
