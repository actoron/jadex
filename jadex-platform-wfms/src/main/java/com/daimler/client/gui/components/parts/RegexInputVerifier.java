package com.daimler.client.gui.components.parts;

import java.awt.Toolkit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class RegexInputVerifier extends InputVerifier
{

	private Matcher matcher;

	public RegexInputVerifier(String regExpr)
	{
		Pattern pattern = Pattern.compile(regExpr);
		this.matcher = pattern.matcher("");
	}

	public RegexInputVerifier(Pattern pattern)
	{
		this.matcher = pattern.matcher("");
	}

	/**
	 * Return <code>true</code> only if the untrimmed user input matches the
	 * regular expression provided to the constructor.
	 * 
	 * @param aComponent
	 *            must be a <code>JTextComponent</code>.
	 */
	public boolean verify(JComponent aComponent)
	{
		boolean result = false;
		JTextComponent textComponent = (JTextComponent) aComponent;
		matcher.reset(textComponent.getText());
		if (matcher.matches())
		{
			result = true;
		} else
		{
			Toolkit.getDefaultToolkit().beep();
		}
		return result;
	}
}
