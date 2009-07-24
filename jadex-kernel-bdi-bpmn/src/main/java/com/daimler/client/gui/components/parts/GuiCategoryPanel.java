// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components.parts;

import jadex.bpmn.model.MParameter;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	private List categoryParameters;

	private String theCategoryName;

	private boolean enabled = true;

	private boolean changed = false;

	private Color theBackgroundColor;

	private ArrayList theInputFields;

	public GuiCategoryPanel(List parameters, String categoryName)
	{
		this(parameters, categoryName, (new JPanel()).getBackground());
	}

	public GuiCategoryPanel(List parameters, String categoryName, Color bgColor)
	{
		super();
		this.categoryParameters = parameters;
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
		for (int i = 0; i < categoryParameters.size(); i++)
		{
			// System.out.println("Creating panel for: " +
			// theCategoryProperties.get(i).getTheName());
			MParameter parameter = (MParameter) categoryParameters.get(i);
			theInputFields.add(InputFieldFactory.createInputPanel(parameter
					.getName(), parameter.getName(), "", "",
					theBackgroundColor, true, parameter.getClazz()));
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
