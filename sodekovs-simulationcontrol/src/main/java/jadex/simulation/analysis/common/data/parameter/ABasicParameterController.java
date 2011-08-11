package jadex.simulation.analysis.common.data.parameter;

import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.simulation.analysis.common.util.SAnalysisClassLoader;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ABasicParameterController
{
	protected ABasicParameter parameter;
	protected ABasicParameterView view;
	public ABasicParameterController(ABasicParameter parameter, ABasicParameterView view)
	{
		this.parameter = parameter;
		this.view = view;
	}

	public void setName(String name)
	{
		parameter.setName(name);
		
	}

	public void setValue(String text)
	{
		if (text.length() > 0)
		{
			if (parameter.getValueClass().equals(String.class))
			{
				parameter.setValue(text);
				//String
			}
			else
			{
				try
				{
					String[] imports = { "jadex.simulation.analysis.common.data.*", "jadex.simulation.analysis.common.data.parameter.*" };
					IParsedExpression pex = new JavaCCExpressionParser().parseExpression(text, imports, null, SAnalysisClassLoader.getClassLoader());
					Object value = pex.getValue(null);
					if (value.getClass().equals(parameter.getValueClass()))
					{
						if (!(parameter.getValue().equals(value)))
						{
							parameter.setValue(value);
						}

					}
					else
					{
						throw new RuntimeException();
					}
					
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(SwingUtilities.getRoot(view.getComponent()), "Aktueller Wert (" + parameter.getValueClass() + ") ist nicht zulässig!");
				}
			}
		}
		
	}

//	public void setUsage(Boolean bool)
//	{
//		parameter.setUsage(bool);
//	}
}
