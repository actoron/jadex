package jadex.examples.presentationtimer.display.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import jadex.examples.presentationtimer.common.ICountdownController;
import jadex.examples.presentationtimer.common.ICountdownService.ICountdownListener;
import jadex.examples.presentationtimer.common.State;
import jadex.examples.presentationtimer.display.Main;

public class CountDownView extends JFrame {
	
	
	protected JLabel countdownLabel;
	
	private Timer timer;
	
	private ICountdownListener listener;
	
	/**
	 * Time the countdown will be reset to.
	 */
	private int countDownTime;

	protected int infoTimeLeft;

	protected int warningTimeLeft;
	
	private State state;

	protected JButton startStopButton;
	
	public CountDownView(final int countDownTime, int infoTimeLeft, int warningTimeLeft) {
		this.countDownTime = countDownTime;
		this.infoTimeLeft = infoTimeLeft;
		this.warningTimeLeft = warningTimeLeft;
		this.state = new State(false,false,false);

		this.setSize(300, 300);
		
		formatter = new DecimalFormat("00");

		Container contentPane = this.getContentPane();

		contentPane.setLayout(new BorderLayout());

		contentPane.add(new JLabel("00:00") {
			{
				setForeground(Color.WHITE);
				setBackground(Color.BLACK);
				addComponentListener(new ComponentAdapter() {

					@Override
					public void componentResized(ComponentEvent e) {
						super.componentResized(e);
						int stringWidth = getFontMetrics(getFont()).stringWidth("00:00");
						int componentWidth = getWidth();
						double ratio = (double) componentWidth / (double) stringWidth;
						int newFontSize = (int) (getFont().getSize() * ratio);
						int height = getHeight();
						int fontSizeToUse = Math.min(newFontSize, height);
						setFont(new Font(getFont().getName(), Font.PLAIN, fontSizeToUse));
						
						setOpaque(true);
					}
					
				});
				countdownLabel = this;
			}
		}, BorderLayout.CENTER);
		contentPane.add(new JButton("Start") {{
			addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						toggleButton();
					}
				}
			});
			addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					toggleButton();
				}
				
			});
			startStopButton = this;
		}
		}, BorderLayout.SOUTH);
		
		
		this.setLocationRelativeTo(null);
		this.setTitle("Countdown (" + countDownTime + "s, Warnung bei " + infoTimeLeft + " und " + warningTimeLeft + "s)");
		
		this.setBackground(Color.BLACK);
		
		if (Main.startedWithMain) {
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		
		

		
	}
	
	private void toggleButton() {
		startStopButton.setEnabled(false);
		if (state.isRunning) {
			stopCountDown();
			startStopButton.setText("Reset and Start");
		} else {
			startCountDown();
			startStopButton.setText("Stop");
		}
		startStopButton.setEnabled(true);
	}

	private void startCountDown() {
		countdownLabel.setForeground(Color.WHITE);
		countdownLabel.setBackground(Color.BLACK);
//		countdownLabel.setText("Go!");
		refreshView(countDownTime);
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(createCountDownTask(countDownTime), 1000, 1000);
		state.isRunning = true;
		state.isInfo = false;
		state.isWarn = false;
		informStatus(state);
	}
	
	private void stopCountDown() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		state.isRunning = false;
		informStatus(state);
	}
	
	public ICountdownListener getListener() {
		return listener;
	}

	public void setListener(ICountdownListener listener) {
		this.listener = listener;
	}

	private TimerTask createCountDownTask(final int countDownTime) {
		
		return new TimerTask() {
			
			boolean blinking;
			
			int timeLeft = countDownTime;
			
			@Override
			public void run() {
				timeLeft--;
				if (timeLeft > 0) {
					refreshView(timeLeft);
					
					if (timeLeft < infoTimeLeft) {
						if (timeLeft < warningTimeLeft) {
							state.isWarn = true;
							informStatus(state);
							// red
							countdownLabel.setBackground(Color.RED);
							countdownLabel.setForeground(Color.black);
						} else {
							state.isInfo = true;
							informStatus(state);
							// yellow
							countdownLabel.setBackground(Color.YELLOW);
							countdownLabel.setForeground(Color.black);
						}
					} else {
//						countdownLabel.setForeground(Color.WHITE);
//						countdownLabel.setBackground(Color.BLACK);
					}
				} else {
					// blink?
					if (timeLeft == 0) {
//						System.out.println("time elapsed: " + (System.currentTimeMillis() - timeStarted)/1000);
					}
					if (blinking) {
						countdownLabel.setBackground(Color.RED);
						countdownLabel.setForeground(Color.BLACK);
					} else {
						countdownLabel.setForeground(Color.WHITE);
						countdownLabel.setBackground(Color.BLACK);
					}
					blinking = !blinking;
					refreshView(0);
//					if (timer != null) {
//						timer.cancel();
//						timer = null;
//					}
				}
			}
		};
	}

	protected void refreshView(int timeLeft) {
		String timeString = formatter.format(timeLeft/60) + ":" + formatter.format(timeLeft%60);
		countdownLabel.setText(timeString);
		informTime(timeString);
	}
	
	protected void informTime(String timeString) {
		if (listener != null) {
			listener.timeChanged(timeString);
		}
	}
	protected void informStatus(State state) {
		if (listener != null) {
			listener.stateChanged(state);
		}
	}

	private DecimalFormat formatter;
	
	private ICountdownController controller = new ICountdownController() {
		
		@Override
		public void start() {
			if (!state.isRunning) {
				SwingUtilities.invokeLater(() -> {
					toggleButton();
				});
			}
		}

		@Override
		public void stop() {
			if (state.isRunning) {
				SwingUtilities.invokeLater(() -> {
					toggleButton();
				});
			}
		}

		@Override
		public void reset() {
		}
	};

	public ICountdownController getController() {
		return controller;
	}
	
}
