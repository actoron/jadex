package jadex.examples.presentationtimer.remotecontrol.ui;

import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.SResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.examples.presentationtimer.common.ICountdownService;
import jadex.examples.presentationtimer.common.State;
import jadex.examples.presentationtimer.remotecontrol.ClientMain;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

public class ClientFrame extends JFrame
{
	
	private JList<ICountdownService>				list;

	private JLabel									timeLabel;

	private JLabel									stateLabel;

	private CDListModel								listmodel;

	private ITerminableIntermediateFuture<State>	stateFut;

	private ITerminableIntermediateFuture<String>	timeFut;

	private ICountdownService	selectedElement;
	
	public ClientFrame()
	{
		if(ClientMain.startedWithMain)
		{
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		this.setSize(new Dimension(600,300));
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(new JPanel() 
		{{
			add(new JList<ICountdownService>() 
			{{
				setBorder(new TitledBorder("Available Countdown Services: "));
				listmodel = new CDListModel();
				setModel(listmodel);
				setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				setCellRenderer(new CDListCellRenderer());
				list = this;
			}});
		}}, BorderLayout.CENTER);
		
		contentPane.add(new JPanel() 
		{{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new JLabel("currentTime") 
			{{
				timeLabel = this;
			}});
			add(new JLabel("currentState") 
			{{
				stateLabel = this;
			}});
			add(new JPanel() 
			{{
				setLayout(new FlowLayout());
				add(new JButton("Start") 
				{{
					addActionListener(action -> {if (selectedElement != null) selectedElement.start();});
				}});
				add(new JButton("Stop") 
				{{
					addActionListener(action -> {if (selectedElement != null) selectedElement.stop();});
				}});
				add(new JButton("Reset")
				{{
					addActionListener(action -> {if (selectedElement != null) selectedElement.reset();});
				}});
			}});
		}}, BorderLayout.LINE_END);
		
		this.setLocationRelativeTo(null);

		list.addListSelectionListener(selEvent -> {
			int firstIndex = selEvent.getFirstIndex();
			if (list.isSelectionEmpty()) {
				if (stateFut != null && !timeFut.isDone()) {
					System.out.println("Selection Empty, terminating");
					stateFut.terminate();
					stateFut = null;
				}
				if (timeFut != null && !timeFut.isDone()) {
					timeFut.terminate();
					stateFut = null;
				}
			} else if (!selEvent.getValueIsAdjusting()) {
				ICountdownService element = list.getSelectedValue();
				selectedElement = element;
				
				if (stateFut != null && !timeFut.isDone()) {
					stateFut.terminate();
				}
				if (timeFut != null && !timeFut.isDone()) {
					timeFut.terminate();
				}
				
				System.out.println("Subscribing");
				element.getTime().addResultListener(timeString -> timeLabel.setText(timeString));
				element.getState().addResultListener(state -> stateLabel.setText(state.toString()));
				
				stateFut = element.registerForState();
				timeFut = element.registerForTime();
				
				stateFut.addIntermediateResultListener(new SwingIntermediateResultListener<State>(
					state -> stateLabel.setText(state.toString())
					));
				timeFut.addIntermediateResultListener(new SwingIntermediateResultListener<String>(
					timeString -> timeLabel.setText(timeString)
					));
				
			}
		});
		
		setTitle("Presentationtimer Client");
	}

	public CDListModel getListmodel()
	{
		return listmodel;
	}
	

}
