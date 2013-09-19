package storageService;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.micro.IPojoMicroAgent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ClientKVGui extends AbstractComponentViewerPanel {

	@Override
	public JComponent getComponent() {
		JPanel panel = new JPanel(new BorderLayout());
		
		// show DB content
		String[] columnNames = {"Key", "Version", "Value"};
		JPanel showAllKVPanel = new JPanel(new BorderLayout());
		final DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
		final JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		showAllKVPanel.add(scrollPane, BorderLayout.CENTER);
		
		final JTextField numberOfData = new JTextField("Total elements in DB: ");
		showAllKVPanel.add(numberOfData, BorderLayout.NORTH);
		
		JButton refresh = new JButton("refresh");
		refresh.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				// add db content to table
				getActiveComponent().scheduleStep(
						new IComponentStep<List<DBEntry>>() {
							public IFuture<List<DBEntry>> execute(
									IInternalAccess ia) {
								ClientKVAgent agent = (ClientKVAgent) ((IPojoMicroAgent) ia)
										.getPojoAgent();
								return agent.getAllFut();
							}
						}).addResultListener(
						new SwingDefaultResultListener<List<DBEntry>>() {
							public void customResultAvailable(List<DBEntry> values) {
								System.out.println("SwingDefaultResultListener");
								numberOfData.setText("Total elements in DB: " + values.size());
								// delete content
								tableModel.setRowCount(0);
								// add content
								Iterator<DBEntry> it = values.iterator();
								while(it.hasNext()) {
									DBEntry dBEntry = it.next();
									String[] newRow = {dBEntry.getKey(), dBEntry.getVersion().toString(),
											dBEntry.getValue().toString()
									};
									tableModel.addRow(newRow);
								}
							}
						});
			}
		});
		showAllKVPanel.add(refresh, BorderLayout.SOUTH);
		panel.add(showAllKVPanel, BorderLayout.NORTH);
		
		// store new KV-pair
		final JPanel storeKVPanel = new JPanel(new BorderLayout());
		final JPanel keyPanel = new JPanel();
		final JLabel keyLabel = new JLabel("Key");
		final JTextField keyTF = new JTextField(20);
		keyPanel.add(keyLabel);
		keyPanel.add(keyTF);
		storeKVPanel.add(keyPanel, BorderLayout.NORTH);
		
		final JPanel valuePanel = new JPanel();
		final JLabel valueLabel = new JLabel("Value");
		final JTextField valueTF = new JTextField(20);
		valuePanel.add(valueLabel);
		valuePanel.add(valueTF);
		storeKVPanel.add(valuePanel, BorderLayout.CENTER);
		
		final JPanel buttonPanel = new JPanel();
		final JButton storeKVButton = new JButton("store");
		storeKVButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				final String key = keyTF.getText();
				final String value = valueTF.getText();
				
				getActiveComponent().scheduleStep(new IComponentStep<Void>() {
					public IFuture<Void> execute(IInternalAccess ia) {
						ClientKVAgent agent = (ClientKVAgent) ((IPojoMicroAgent) ia)
								.getPojoAgent();
						agent.storeKV(key, value);
						return IFuture.DONE;
					}
				});
			}
		});
		final JButton clearButton = new JButton("clear");
		clearButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				keyTF.setText("");
				valueTF.setText("");
			}
		});
		buttonPanel.add(storeKVButton);
		buttonPanel.add(clearButton);
		storeKVPanel.add(buttonPanel, BorderLayout.SOUTH);
		panel.add(storeKVPanel, BorderLayout.SOUTH);
		
		return panel;
	}

}
