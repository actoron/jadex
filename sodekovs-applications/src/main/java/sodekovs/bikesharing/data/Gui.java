package sodekovs.bikesharing.data;

import java.awt.EventQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.jdesktop.swingx.JXDatePicker;

public class Gui {

	private JFrame frmRealDataExtrator;
	private JTextField txtDbUsername;
	private JTextField txtDbUrl;
	private JPasswordField pwdDbPassword;
	private JLabel lblCity;
	private JLabel lblLink;
	private JComboBox cmbBxLink;
	private JTextField txtOutputfile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frmRealDataExtrator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRealDataExtrator = new JFrame();
		frmRealDataExtrator.setTitle("Real Data Extrator");
		frmRealDataExtrator.setBounds(100, 100, 610, 505);
		frmRealDataExtrator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblDbUrl = new JLabel("DB URL:");

		JLabel lblDbUsername = new JLabel("DB Username:");

		JLabel lblDbPassword = new JLabel("DB Password:");

		txtDbUsername = new JTextField();
		txtDbUsername.setColumns(10);

		txtDbUrl = new JTextField();
		txtDbUrl.setColumns(10);

		pwdDbPassword = new JPasswordField();

		lblCity = new JLabel("City:");

		JComboBox cmbBxCity = new JComboBox();
		cmbBxCity.setModel(new DefaultComboBoxModel(new String[] { "Washington", "London" }));
		cmbBxCity.setSelectedIndex(0);

		lblLink = new JLabel("Link:");

		cmbBxLink = new JComboBox();
		cmbBxLink.setModel(new DefaultComboBoxModel(new String[] { "BY_BIKE", "BY_TRUCK" }));
		cmbBxLink.setSelectedIndex(0);

		JLabel lblWeekdays = new JLabel("Weekdays:");

		JCheckBox chckbxMonday = new JCheckBox("Monday");

		JCheckBox chckbxTuesday = new JCheckBox("Tuesday");

		JCheckBox chckbxWednesday = new JCheckBox("Wednesday");

		JCheckBox chckbxThursday = new JCheckBox("Thursday");

		JCheckBox chckbxFriday = new JCheckBox("Friday");

		JCheckBox chckbxSaturday = new JCheckBox("Saturday");

		JCheckBox chckbxSunday = new JCheckBox("Sunday");

		JLabel lblFromDate = new JLabel("From Date:");

		JXDatePicker dtPckrFrom = new JXDatePicker();

		JLabel lblToDate = new JLabel("To Date:");

		JXDatePicker dtPckrTo = new JXDatePicker();

		JLabel lblOutputFile = new JLabel("Output File Directory:");

		JButton btnNewButton = new JButton("New button");

		txtOutputfile = new JTextField();
		txtOutputfile.setEditable(false);
		txtOutputfile.setColumns(10);

		JButton btnSelectDirectory = new JButton("Select Directory");

		JLabel lblOutput = new JLabel("Output:");

		JTextArea txtrOutput = new JTextArea();
		txtrOutput.setEditable(false);
		txtrOutput.setLineWrap(true);
		GroupLayout groupLayout = new GroupLayout(frmRealDataExtrator.getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGroup(
																				groupLayout
																						.createParallelGroup(Alignment.LEADING)
																						.addGroup(
																								groupLayout
																										.createSequentialGroup()
																										.addGroup(
																												groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblDbUsername)
																														.addComponent(lblDbUrl).addComponent(lblDbPassword))
																										.addGap(7)
																										.addGroup(
																												groupLayout.createParallelGroup(Alignment.LEADING)
																														.addComponent(txtDbUsername, 494, 494, 494)
																														.addComponent(pwdDbPassword, 494, 494, 494)
																														.addComponent(txtDbUrl, GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)))
																						.addGroup(
																								groupLayout
																										.createSequentialGroup()
																										.addGroup(
																												groupLayout
																														.createParallelGroup(Alignment.LEADING)
																														.addGroup(
																																groupLayout
																																		.createParallelGroup(Alignment.LEADING, false)
																																		.addGroup(
																																				groupLayout
																																						.createSequentialGroup()
																																						.addComponent(lblCity)
																																						.addPreferredGap(ComponentPlacement.RELATED)
																																						.addComponent(cmbBxCity, 0,
																																								GroupLayout.DEFAULT_SIZE,
																																								Short.MAX_VALUE))
																																		.addGroup(
																																				groupLayout.createSequentialGroup()
																																						.addComponent(lblWeekdays)
																																						.addPreferredGap(ComponentPlacement.RELATED)
																																						.addComponent(chckbxMonday)
																																						.addPreferredGap(ComponentPlacement.RELATED)
																																						.addComponent(chckbxTuesday)
																																						.addPreferredGap(ComponentPlacement.RELATED)
																																						.addComponent(chckbxWednesday)))
																														.addGroup(
																																groupLayout
																																		.createSequentialGroup()
																																		.addComponent(lblFromDate)
																																		.addPreferredGap(ComponentPlacement.RELATED)
																																		.addComponent(dtPckrFrom, GroupLayout.PREFERRED_SIZE, 231,
																																				GroupLayout.PREFERRED_SIZE)))
																										.addPreferredGap(ComponentPlacement.RELATED)
																										.addGroup(
																												groupLayout
																														.createParallelGroup(Alignment.LEADING)
																														.addGroup(
																																groupLayout
																																		.createSequentialGroup()
																																		.addGroup(
																																				groupLayout
																																						.createParallelGroup(Alignment.TRAILING)
																																						.addComponent(btnSelectDirectory)
																																						.addGroup(
																																								groupLayout
																																										.createSequentialGroup()
																																										.addComponent(lblToDate)
																																										.addPreferredGap(
																																												ComponentPlacement.RELATED)
																																										.addComponent(
																																												dtPckrTo,
																																												GroupLayout.DEFAULT_SIZE,
																																												227, Short.MAX_VALUE)))
																																		.addPreferredGap(ComponentPlacement.RELATED))
																														.addGroup(
																																groupLayout.createSequentialGroup().addComponent(chckbxThursday)
																																		.addPreferredGap(ComponentPlacement.RELATED)
																																		.addComponent(chckbxFriday)
																																		.addPreferredGap(ComponentPlacement.RELATED)
																																		.addComponent(chckbxSaturday)
																																		.addPreferredGap(ComponentPlacement.RELATED)
																																		.addComponent(chckbxSunday))
																														.addGroup(
																																groupLayout
																																		.createSequentialGroup()
																																		.addComponent(lblLink)
																																		.addPreferredGap(ComponentPlacement.RELATED)
																																		.addComponent(cmbBxLink, GroupLayout.PREFERRED_SIZE, 239,
																																				GroupLayout.PREFERRED_SIZE)
																																		.addPreferredGap(ComponentPlacement.RELATED))))).addGap(384))
														.addGroup(
																groupLayout.createSequentialGroup().addComponent(lblOutputFile).addPreferredGap(ComponentPlacement.RELATED)
																		.addComponent(txtOutputfile, GroupLayout.PREFERRED_SIZE, 347, GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(ComponentPlacement.RELATED, 405, Short.MAX_VALUE).addComponent(btnNewButton).addContainerGap())
														.addGroup(groupLayout.createSequentialGroup().addComponent(lblOutput).addContainerGap(549, Short.MAX_VALUE))
														.addGroup(
																groupLayout.createSequentialGroup().addComponent(txtrOutput, GroupLayout.PREFERRED_SIZE, 575, GroupLayout.PREFERRED_SIZE)
																		.addContainerGap()))));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblDbUrl)
										.addComponent(txtDbUrl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblDbUsername)
										.addComponent(txtDbUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(pwdDbPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblDbPassword))
						.addGap(18)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblCity)
										.addComponent(cmbBxCity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblLink)
										.addComponent(cmbBxLink, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblWeekdays).addComponent(chckbxMonday).addComponent(chckbxTuesday).addComponent(chckbxWednesday)
										.addComponent(chckbxThursday).addComponent(chckbxFriday).addComponent(chckbxSaturday).addComponent(chckbxSunday))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblFromDate)
										.addComponent(dtPckrFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(dtPckrTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblToDate))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblOutputFile).addComponent(btnNewButton)
										.addComponent(txtOutputfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnSelectDirectory))
						.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblOutput).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(txtrOutput, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE).addContainerGap(211, Short.MAX_VALUE)));
		frmRealDataExtrator.getContentPane().setLayout(groupLayout);
	}
}
