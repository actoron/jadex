package jadex.base.service.message;

import java.util.TimerTask;

public class Timer {

	private java.util.Timer _timer;
	private TimerListener _listener;
	private int _delay;
	private TimerTask task;

	public Timer(int delay, TimerListener listener) {
		_timer = new java.util.Timer();
		_listener = listener;
		_delay = delay;
	}

	public void start() {
		synchronized (this) {
			if (task == null) {
				restart();
			}
		}
	}

	public void restart() {
		synchronized (this) {
			if (task != null) {
				task.cancel();
			}
			task = new TimerTask() {
				@Override
				public void run() {
					_listener.actionPerformed();
				}
			};
			_timer.scheduleAtFixedRate(task, _delay, _delay);
		}
	}

	public void stop() {
		synchronized (this) {
			if (task != null) {
				task.cancel();
			}
		}
	}
}
