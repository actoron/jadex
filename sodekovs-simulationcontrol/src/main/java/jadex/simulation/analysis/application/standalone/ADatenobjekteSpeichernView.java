package jadex.simulation.analysis.application.standalone;

import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.data.IADataObject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ADatenobjekteSpeichernView extends JPanel
{
	final protected ADatenobjekteSpeichernService service;
	protected JList list;
	protected JSplitPane splitP;

	public ADatenobjekteSpeichernView(ADatenobjekteSpeichernService dservice)
	{
		super(new GridBagLayout());
		this.service = dservice;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Map<String, IADataObject> map = (Map<String, IADataObject>) service.getElements().get(new ThreadSuspendable(this));

				splitP = new JSplitPane();
				JPanel leftPanel = new JPanel(new GridBagLayout());
				final Insets insets = new Insets(1, 1, 1, 1);

				// list
				DefaultListModel listModel = new DefaultListModel();
				for (String parameterName : map.keySet())
				{
					listModel.addElement(parameterName);
				}
				;
				list = new JList(listModel);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.setLayoutOrientation(JList.VERTICAL);
				list.setVisibleRowCount(-1);
				list.setFixedCellWidth(200);
				list.setSelectedIndex(-1);
				list.setPreferredSize(new Dimension(150, 250));
				list.addListSelectionListener(new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e)
					{
						Map<String, IADataObject> map = (Map<String, IADataObject>) service.getElements().get(new ThreadSuspendable(this));
						String sel = (String) list.getSelectedValue();
						if (sel != null)
						{
							if (map.containsKey(sel))
							{
								splitP.setRightComponent(((IADataObject) map.get(sel)).getView().getComponent());
								splitP.revalidate();
								splitP.repaint();
							}
						}
					}
				});

				leftPanel.add(list,
						new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				splitP.setLeftComponent(leftPanel);

				if (list.getSelectedIndex() != -1)
				{
					Map<String, IADataObject> map2 = (Map<String, IADataObject>) service.getElements().get(new ThreadSuspendable(this));
					String sel = (String) list.getSelectedValue();
					if (sel != null)
					{
						if (map2.containsKey(sel))
						{
							splitP.setRightComponent(((IADataObject) map2.get(sel)).getView().getComponent());
							splitP.revalidate();
							splitP.repaint();
						}
					}
				}
				else
				{
					JPanel freepanel = new JPanel();
					freepanel.setName("No Parameter selected");
					freepanel.setPreferredSize(new Dimension(550, 300));
					splitP.setRightComponent(freepanel);
				}
				// ((JSplitPane) componentP).setDividerLocation(200);
				splitP.setPreferredSize(new Dimension(750, 400));

				splitP.revalidate();
				splitP.repaint();
			}
		});
	}

	public void addObject(final String name)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Map<String, IADataObject> map = (Map<String, IADataObject>) service.getElements().get(new ThreadSuspendable(this));
				
				if (!((DefaultListModel) list.getModel()).contains(name))
				{
					((DefaultListModel) list.getModel()).addElement(name);
					list.revalidate();
					list.repaint();
				}
				
				map.get(name);
			}
		});

	}

}
