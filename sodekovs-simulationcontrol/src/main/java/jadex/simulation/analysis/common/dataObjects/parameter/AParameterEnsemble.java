package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.ABasicDataObject;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AParameterEnsemble extends ABasicDataObject implements IAParameterEnsemble
{
	private Map<String, IAParameter> parametersMap;

	public AParameterEnsemble()
	{
		super();
		synchronized (mutex)
		{
			parametersMap = Collections.synchronizedMap(new HashMap<String, IAParameter>());
		}

	}

	@Override
	public Boolean isFeasable()
	{
		synchronized (mutex)
		{
			Boolean result = true;
			for (IAParameter para : getParameters().values())
			{
				if (!para.isFeasable())
					result = false;
			}
			return result;
		}
	}

	@Override
	public void addParameter(IAParameter parameter)
	{
		synchronized (mutex)
		{
			parametersMap.put(parameter.getName(), parameter);
		}
	}

	@Override
	public void removeParameter(String name)
	{
		synchronized (mutex)
		{
			parametersMap.remove(name);
		}
	}

	@Override
	public void clearParameters()
	{
		synchronized (mutex)
		{
			parametersMap.clear();
		}
	}

	@Override
	public boolean containsParameter(String name)
	{
		return parametersMap.containsKey(name);
	}

	@Override
	public IAParameter getParameter(String name)
	{
		return parametersMap.get(name);
	}

	@Override
	public Map<String, IAParameter> getParameters()
	{
		return parametersMap;
	}

	@Override
	public boolean hasParameters()
	{
		return parametersMap.isEmpty();
	}

	@Override
	public Integer numberOfParameters()
	{
		return parametersMap.size();
	}

	@Override
	public Object getValue(String name)
	{
		return parametersMap.get(name).getValue();
	}

	@Override
	public void setValue(String name, Object value)
	{
		synchronized (mutex)
		{
			parametersMap.get(name).setValue(value);
		}
	}

	@Override
	public JComponent getView()
	{
		synchronized (mutex)
		{
			final JComponent component = new JPanel(new GridBagLayout());
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Insets insets = new Insets(2, 2, 2, 2);
					int x = 0;

					for (IAParameter parameter : getParameters().values())
					{
						component.add(parameter.getView(),
								new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						x++;
					}
					component.add(new JPanel(),
							new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

					component.updateUI();
					component.validate();
				}
			});

			return component;
		}
		//		
	}

	/**
	 * Test View
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		AParameterEnsemble ens = new AParameterEnsemble();
		ens.addParameter(new ABasicParameter("Parameter", Double.class, 5.0));
		ens.addParameter(new ABasicParameter("Parameter2", Double.class, 5.0));
		ens.addParameter(new ABasicParameter("Parameter3", Double.class, 5.0));

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 786);
		frame.add(ens.getView());
		frame.setVisible(true);
	}
}
