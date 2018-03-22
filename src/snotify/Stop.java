package snotify;

public class Stop {

	private String stopId;
	private String stopName;
	
	public Stop(String stopId, String stopName) {
		this.stopId = stopId;
		this.stopName = stopName;
	}

	public String getStopId() {
		return stopId;
	}

	public String getStopName() {
		return stopName;
	}
	
}
