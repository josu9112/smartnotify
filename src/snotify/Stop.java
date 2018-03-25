package snotify;

public class Stop {

	private String time;
	private String stopName;
	private int delay;
	
	public Stop(String time, String stopName) {
		this.time = time;
		this.stopName = stopName;
		this.delay = 0;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	public String getTime() {
		return this.time;
	}

	public String getStopName() {
		return stopName;
	}
	
}
