package jadex.examples.presentationtimer.remotecontrol.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.SResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.examples.presentationtimer.common.ICountdownService;
import jadex.examples.presentationtimer.common.State;
import jadex.examples.presentationtimer.remotecontrol.ClientMain;

public class ClientFrame extends JFrame
{
	
	private JList<CDListItem>				list;

	private JLabel									timeLabel;

	private JLabel									stateLabel;

	private CDListModel								listmodel;

	private ITerminableIntermediateFuture<State>	stateFut;

	private ITerminableIntermediateFuture<String>	timeFut;

	private CDListItem	selectedElement;
	
	public ClientFrame()
	{
		if(ClientMain.startedWithMain)
		{
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		this.setSize(new Dimension(600,450));
		this.setResizable(false);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(new JPanel() 
		{{
			setLayout(new BorderLayout());
			add(new JScrollPane() {{
				setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				setViewportView(new JList<CDListItem>() 
				{{
					setBorder(new TitledBorder("Available Countdown Services: "));
					listmodel = new CDListModel();
					setModel(listmodel);
					setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					setCellRenderer(new CDListCellRenderer());
					list = this;
				}});
			}}, BorderLayout.CENTER);
			add(new JPanel() {{
					add(new JLabel("searching..."));
//					add(new JButton("Abort search"));
				}}, BorderLayout.PAGE_END);
		}}, BorderLayout.CENTER);
		
		
		contentPane.add(new JPanel() 
		{{
			setLayout(new BorderLayout());
			add(new JPanel() {{
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				add(Box.createGlue());
				add(new JLabel("currentTime") 
				{{
					timeLabel = this;
					setAlignmentX(CENTER_ALIGNMENT);
					setFont(getFont().deriveFont(20.0f));
				}});
				add(new JLabel("currentState") 
				{{
					stateLabel = this;
					setAlignmentX(CENTER_ALIGNMENT);
				}});
				add(Box.createGlue());
			}}, BorderLayout.CENTER);
			add(new JPanel() 
			{{
				setLayout(new FlowLayout());
				add(new JButton("Start") 
				{{
					addActionListener(action -> {if (selectedElement != null) selectedElement.getService().start();});
				}});
				add(new JButton("Stop") 
				{{
					addActionListener(action -> {if (selectedElement != null) selectedElement.getService().stop();});
				}});
				add(new JButton("Reset")
				{{
					addActionListener(action -> {if (selectedElement != null) selectedElement.getService().reset();});
				}});
			}}, BorderLayout.PAGE_END);
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
				CDListItem element = list.getSelectedValue();
				selectedElement = element;
				ICountdownService service = element.getService();
				
				if (stateFut != null && !timeFut.isDone()) {
					stateFut.terminate();
				}
				if (timeFut != null && !timeFut.isDone()) {
					timeFut.terminate();
				}
				
				System.out.println("Subscribing");
				service.getTime().addResultListener(timeString -> timeLabel.setText(timeString));
				service.getState().addResultListener(state -> stateLabel.setText(state.toString()));
				
				stateFut = service.registerForState();
				timeFut = service.registerForTime();
				
				stateFut.addIntermediateResultListener(new SwingIntermediateResultListener<State>(
					state -> stateLabel.setText(state.toString()),
					SResultListener.ignoreResults(),
					ex -> {if (stateFut != null) stateFut.terminate();} // exception occurs when terminating subscription
					));
				timeFut.addIntermediateResultListener(new SwingIntermediateResultListener<String>(
					timeString -> timeLabel.setText(timeString),
					SResultListener.ignoreResults(),
					ex -> {if (timeFut != null) timeFut.terminate();}
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
