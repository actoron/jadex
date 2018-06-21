package jadex.examples.presentationtimer.display.ui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jadex.examples.presentationtimer.common.ICountdownGUIService;
import jadex.examples.presentationtimer.common.ICountdownService.ICountdownListener;
import jadex.examples.presentationtimer.common.State;
import jadex.examples.presentationtimer.display.Main;

public class ConfigureFrame extends JFrame {

	protected JTextField countDownTimeTF;
	protected JTextField infoTimeTF;
	protected JTextField warningTimeTF;
	protected JLabel fehlerLabel;
	
	private ICountdownGUIService guiService;

	public ConfigureFrame(ICountdownGUIService guiService) {
		this.guiService = guiService;
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(4, 2));

		contentPane.add(new JLabel("Countdownzeit (mm:ss): "));
		contentPane.add(new JTextField("08:00") {
			{
				countDownTimeTF = this;
			}
		});

		contentPane.add(new JLabel("Zeit für gelbe Ansicht (mm:ss): "));
		contentPane.add(new JTextField("01:00") {
			{
				infoTimeTF = this;
			}
		});

		contentPane.add(new JLabel("Zeit für rote Ansicht (mm:ss): "));
		contentPane.add(new JTextField("00:15") {
			{
				warningTimeTF = this;
			}
		});

		contentPane.add(new JLabel() {
			{
				fehlerLabel = this;
			}
		});
		contentPane.add(new JButton("Starten") {
			{
				addActionListener(onOkClickListener);
			}
		});

		this.pack();
		this.setTitle("Countdown konfigurieren");
		if (Main.startedWithMain) {
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
	}

	private ActionListener onOkClickListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				int cdTime = parseTimeString(countDownTimeTF.getText());
				int infoTime = parseTimeString(infoTimeTF.getText());
				int warnTime = parseTimeString(warningTimeTF.getText());
				
				if (cdTime > infoTime && infoTime > warnTime) {
					dispose();
					CountDownView countDownView = new CountDownView(cdTime, infoTime, warnTime);
					
					guiService.setController(countDownView.getController());
					countDownView.setListener(new ICountdownListener() {

						@Override
						public void timeChanged(String timeString)
						{
							guiService.informTimeUpdated(timeString);
						}

						@Override
						public void stateChanged(State state)
						{
							guiService.informStateUpdated(state);
						}
					});
					
					countDownView.setVisible(true);
					
					
				} else {
					fehlerLabel.setText("info und warnzeit müssen größer als Gesamtzeit sein!");
				}
				
				

			} catch (NumberFormatException nfe) {
				fehlerLabel
						.setText("Fehler beim parsen der Werte!");
			}
		}

		private int parseTimeString(String text) {
			String[] timeArr = text.split(":");
			if (timeArr.length != 2) {
				throw new NumberFormatException("");
			} else {
				int minutes = Integer.parseInt(timeArr[0]);
				int seconds = Integer.parseInt(timeArr[1]);
				return minutes*60 + seconds;
			}
		}
	};
}
