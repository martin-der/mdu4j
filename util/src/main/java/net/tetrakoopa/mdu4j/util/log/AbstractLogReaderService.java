package net.tetrakoopa.mdu4j.util.log;

import java.io.File;
import java.util.LinkedList;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;


public abstract class AbstractLogReaderService {

	private final Tailer tailer;

	private Thread tailerRunner = null;

	private final LinkedList<String> logs = new LinkedList<String>();
	private final static int MAX_LINES = 200;

	private long eventUserDuration = 2000;

	private static class Stamp {
		Long update;
		Long rotation;
		Long userClear;
	}

	private final Stamp stamp = new Stamp();

	public AbstractLogReaderService(File file, long delay) {
		tailer = new Tailer(file, new TailerListener() {

			@Override
			public void init(Tailer trailer) {

			}

			@Override
			public void handle(Exception ex) {
				// TODO Auto-generated method stub

			}

			@Override
			public void handle(String line) {
				handleAddLog(line);
			}

			@Override
			public void fileRotated() {
				stampRotation();
			}

			@Override
			public void fileNotFound() {
				// TODO Auto-generated method stub

			}
		}, delay, true);
	}

	protected void stampRotation() {
		synchronized (this) {
			stamp.rotation = System.currentTimeMillis();
		}
	}

	public static class LogsSnapshot {
		public final String[] lines;
		public final Stamp stamp = new Stamp();

		private LogsSnapshot(String[] lines, Stamp stamp) {
			this.lines = lines;
			this.stamp.update = stamp.update;
			this.stamp.rotation = stamp.rotation;
			this.stamp.userClear = stamp.userClear;
		}
	}

	public LogsSnapshot getLogs() {
		synchronized (this) {
			String logsCopy[] = new String[logs.size()];
			int i = 0;
			for (String line : logs) {
				logsCopy[i++] = line;
			}
			return new LogsSnapshot(logsCopy, this.stamp);
		}
	}

	protected void handleAddLog(String line) {
		synchronized (this) {
			logs.addLast(line);
			if (logs.size() > MAX_LINES) {
				logs.removeFirst();
			}
		}
	}

	public void clear() {
		synchronized (this) {
			logs.clear();
			stamp.userClear = System.currentTimeMillis();
		}
	}

	public boolean rotated(long ms) {
		return rotatedLessThanAgo(eventUserDuration);
	}

	public boolean rotatedLessThanAgo(long ms) {
		synchronized (this) {
			return (stamp.rotation != null) && (stamp.rotation + eventUserDuration < System.currentTimeMillis());
		}
	}

	public final void run() {
		if (tailerRunner == null) {
			tailerRunner = new Thread() {
				@Override
				public void run() {
					tailer.run();
				}
			};
			tailerRunner.start();
		}
	}

	public final void stop() {
		if (tailerRunner != null) {
			tailer.stop();
			try {
				tailerRunner.join();
			} catch (InterruptedException e) {
			}
		}
	}


}
