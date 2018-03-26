package snotify;

public class Stop {

	private String time;
	private String stopName;
	private int delay;
	private String stopId;
	
	public Stop(String time, String stopName, String stopId) {
		this.time = time;
		this.stopName = stopName;
		this.delay = 0;
		this.stopId = stopId;
	}
	
	public String getStopId() {
		return this.stopId;
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
