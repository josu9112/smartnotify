package snotify;

/**
 * @author John Sundemo & Rustam Stanikzai
 *	Class Trip is used to receive information about a trip from a destination to another
 */
/**
 * @author John
 *
 */
/**
 * @author John
 *
 */
/**
 * @author John
 *
 */
public class Trip {

	private Token token;
	private final String baseRequestAddress = "https://api.vasttrafik.se/bin/rest.exe/v2/trip";
	private String originId;
	private String originCoordLat;
	private String originCoordLong;
	private String originCoordName;
	private String destId;
	private String destCoordLat;
	private String destCoordLong;
	private String destCoordName;
	private String viaId;
	private String date; //YYYY-MM-DD
	private String time; //HH:MM
	private int searchForArrival;
	private int useVasttag;
	private int useLongDistanceTrain;
	private int useRegionalTrain;
	private int useBus;
	private int useMedical;
	private int originMedicalConnection;
	private int destMedicalConnection;
	private int wheelChairSpace;
	private int strollerSpace;
	private int lowFloor;
	private int rampOrLift;
	private int useBoat;
	private int useTram;
	private int usePublicTransportation;
	private int excludeDR;
	private int maxWalkDist;
	private double walkSpeed;
	private int originWalk;
	private int destWalk;
	private int onlyWalk;
	private int originBike;
	private int maxBikeDist;
	private String bikeCriterion;
	private String bikeProfile;
	private int onlyBike;
	private int originCar;
	private int originCarWithParking;
	private int maxCarDist;
	private int onlyCar;
	private int maxChanges;
	private int addiotionalChangeTime;
	private int disregardDefaultChangeMargin;
	private int needJourneyDetail;
	private int needGeo;
	private int needltinerary;
	private int numTrips;
	
	
	/**
	 * Constructs Trip. All parameters are initialized and set to default valuse. Use set methods to give parameters values.
	 * @param token Access-token from Västtrafik.
	 */
	public Trip(Token token) {
		this.token = token;
		resetAllParameters();
	}
	
	
	/**
	 * Resets all parameters to default values. Use set methods to set parameters.
	 */
	public void resetAllParameters() {
		this.originId = null;
		this.originCoordLat = null;
		this.originCoordLong = null;
		this.originCoordName = null;
		this.destId = null;
		this.destCoordLat = null;
		this.destCoordLong = null;
		this.destCoordName = null;
		this.viaId = null;
		this.date = null; 
		this.time = null;
		this.searchForArrival = 0;
		this.useVasttag = 1;
		this.useLongDistanceTrain = 1;
		this.useRegionalTrain = 1;
		this.useBus = 1;
		this.useMedical = 0;
		this.originMedicalConnection = 0;
		this.destMedicalConnection = 0;
		this.wheelChairSpace = 0;
		this.strollerSpace = 0;
		this.lowFloor = 0;
		this.rampOrLift = 0;
		this.useBoat = 1;
		this.useTram = 1;
		this.usePublicTransportation = 1;
		this.excludeDR = 0;
		this.maxWalkDist = 0;
		this.walkSpeed = 0.5;
		this.originWalk = 1;
		this.destWalk = 1;
		this.onlyWalk = 0;
		this.originBike = 0;
		this.maxBikeDist = 0;
		this.bikeCriterion = null;
		this.bikeProfile = null;
		this.onlyBike = 1;
		this.originCar = 0;
		this.originCarWithParking = 0;
		this.maxCarDist = 0;
		this.onlyCar = 0;
		this.maxChanges = 0;
		this.addiotionalChangeTime = 0;
		this.disregardDefaultChangeMargin = 0;
		this.needJourneyDetail = 0;
		this.needGeo = 0;
		this.needltinerary = 0;
		this.numTrips = 10;
	}


	/**
	 * Sets origin stop id
	 * @param originId Origin stop id
	 */
	public void setOriginId(String originId) {
		this.originId = originId;
	}


	/**
	 * Sets origin latitude of center coordinate in the WGS84 system
	 * @param originCoordLat Origin latitude of center coordinate in the WGS84 system (ex. 57.714292)
	 */
	public void setOriginCoordLat(String originCoordLat) {
		this.originCoordLat = originCoordLat;
	}


	/**
	 * Sets origin longitude of center coordinate in the WGS84 system
	 * @param originCoordLong Origin longitude of center coordinate in the WGS84 system (ex. 11.9788944)
	 */
	public void setOriginCoordLong(String originCoordLong) {
		this.originCoordLong = originCoordLong;
	}


	/**
	 * Sets name of the address at the specified origin coordinate
	 * @param originCoordName Name of the address at the specified origin coordinate
	 */
	public void setOriginCoordName(String originCoordName) {
		this.originCoordName = originCoordName;
	}


	/**
	 * Sets destination stop id
	 * @param destId Destination stop id
	 */
	public void setDestId(String destId) {
		this.destId = destId;
	}


	/**
	 * Sets destination latitude of center coordinate in the WGS84 system 
	 * @param destCoordLat Destination latitude of center coordinate in the WGS84 system (ex. 57.714292)
	 */
	public void setDestCoordLat(String destCoordLat) {
		this.destCoordLat = destCoordLat;
	}


	/**
	 * Sets destination longitude of center coordinate in the WGS84 system
	 * @param destCoordLong Destination longitude of center coordinate in the WGS84 system (ex. 11.9788944)
	 */
	public void setDestCoordLong(String destCoordLong) {
		this.destCoordLong = destCoordLong;
	}


	/**
	 * Sets name of the address at the specified destination coordinate
	 * @param destCoordName Name of the address at the specified destination coordinate
	 */
	public void setDestCoordName(String destCoordName) {
		this.destCoordName = destCoordName;
	}


	/**
	 * Sets via stop/station id
	 * @param viaId Via stop/station id
	 */
	public void setViaId(String viaId) {
		this.viaId = viaId;
	}


	/**
	 * Sets date of the trip
	 * @param date Date of the trip in format YYYY-MM-DD
	 */
	public void setDate(String date) {
		this.date = date;
	}


	/**
	 * Sets time of the trip
	 * @param time Time of the trip in format HH:MM
	 */
	public void setTime(String time) {
		this.time = time;
	}


	/**
	 * To specify that the given time and date is not the departure time but the latest time to arrive at the destination, set this parameter to true.
	 * @param searchForArrival Time of arrival == true. Time of departure == false (default).
	 */
	public void setSearchForArrival(boolean searchForArrival) {
		this.searchForArrival = (searchForArrival) ? 1 : 0;
	}


	/**
	 * To exclude trips with Västtågen, set this parameter to false.
	 * @param useVasttag Exclude Västtågen == false. Include Västtågen == true (default).
	 */
	public void setUseVasttag(boolean useVasttag) {
		this.useVasttag = (useVasttag) ? 1 : 0;
	}


	/**
	 * 	To exclude trips with long distance trains, set this parameter to false.
	 * @param useLongDistanceTrain Exclude long distance trains == false. Include long distance trains == true (default).
	 */
	public void setUseLongDistanceTrain(boolean useLongDistanceTrain) {
		this.useLongDistanceTrain = (useLongDistanceTrain) ? 1 : 0;
	}


	/**
	 * To exclude trips with regional trains, set this parameter to false.
	 * @param useRegionalTrain Exclude regional trains == false. Include regional trains == true (default).
	 */
	public void setUseRegionalTrain(boolean useRegionalTrain) {
		this.useRegionalTrain = (useRegionalTrain) ? 1 : 0;
	}


	/**
	 * To exclude trips with buses, set this parameter to false.
	 * @param useBus Exclude buses == false. Include buses == true (default).
	 */
	public void setUseBus(boolean useBus) {
		this.useBus = (useBus) ? 1 : 0;
	}


	/**
	 * To include medical transport lines trips with buses, set this parameter to true.
	 * @param useMedical Include medical transport lines == true. Doesn't matter == false (default).
	 */
	public void setUseMedical(boolean useMedical) {
		this.useMedical = (useMedical) ? 1 : 0;
	}


	/**
	 * 	To search for medical transport connections from the origin, set this parameter to true.
	 * @param originMedicalConnection Search for medical transport connections == true. Doesn't matter == false (default).
	 */
	public void setOriginMedicalConnection(boolean originMedicalConnection) {
		this.originMedicalConnection = (originMedicalConnection) ? 1 : 0;
	}


	/**
	 * To search for medical transport connections from the destination, set this parameter to true.
	 * @param destMedicalConnection Search for medical transport connections == true. Doesn't matter == false (default).
	 */
	public void setDestMedicalConnection(boolean destMedicalConnection) {
		this.destMedicalConnection = (destMedicalConnection) ? 1 : 0;
	}


	/**
	 * To search for trips where at least one wheelchair space is present in the vehicle, set this parameter to true.
	 * @param wheelChairSpace Search for at least on wheelcharspace == true. Doesn't matter == false (default).
	 */
	public void setWheelChairSpace(boolean wheelChairSpace) {
		this.wheelChairSpace = (wheelChairSpace) ? 1 : 0;
	}


	/**
	 * To search for trips with space for stroller, baby carriage or rollator in the vehicle, set this parameter to true.
	 * @param strollerSpace Search for space for stroller == true. Doesn't matter == false (default).
	 */
	public void setStrollerSpace(boolean strollerSpace) {
		this.strollerSpace = (strollerSpace) ? 1 : 0;
	}


	/**
	 * To search for trips where the vehicle is equipped with a low floor section, but not necessarily a ramp or lift, set this parameter to true.
	 * @param lowFloor Search for vehicle is equipped with a low floor section == true. Doesn't matter == false (default). 
	 */
	public void setLowFloor(boolean lowFloor) {
		this.lowFloor = (lowFloor) ? 1 : 0;
	}


	/**
	 * To search for trips where the vehicle is equipped with ramp or lift that allows fully barrier-free boarding and alighting, set this parameter to true.
	 * @param rampOrLift Search for vehicle is equipped with ramp or lift == true. Doesn't matter == false (default).
	 */
	public void setRampOrLift(boolean rampOrLift) {
		this.rampOrLift = (rampOrLift) ? 1 : 0;
	}


	/**
	 * 	To exclude trips with boats, set this parameter to false.
	 * @param useBoat Exclude boats == false. Include boats == true (default).
	 */
	public void setUseBoat(boolean useBoat) {
		this.useBoat = (useBoat) ? 1 : 0;
	}


	/**
	 * to exclude trips with trams, set this parameter to 0.
	 * @param useTram Exclude trams == false. Include trams == true (default).
	 */
	public void setUseTram(boolean useTram) {
		this.useTram = (useTram) ? 1 : 0;
	}


	/**
	 * 	To exclude trips with public transportation, set this parameter to false.
	 * @param usePublicTransportation Exclude public transportation == false. Include public transportation == true (default).
	 */
	public void setUsePublicTransportation(boolean usePublicTransportation) {
		this.usePublicTransportation = (usePublicTransportation) ? 1 : 0;
	}


	/**
	 * To exclude journeys which require tel. registration, set this parameter to true.
	 * @param excludeDR Exclude == true. Include = false.
	 */
	public void setExcludeDR(boolean excludeDR) {
		this.excludeDR = (excludeDR) ? 1 : 0;
	}


	/**
	 * Sets maximum walking distance from/to the coordinate in meters
	 * @param maxWalkDist Maximum walking distance from/to the coordinate in meters
	 */
	public void setMaxWalkDist(int maxWalkDist) {
		this.maxWalkDist = maxWalkDist;
	}


	/**
	 * Sets walking speed given in percent of normal speed
	 * @param walkSpeed Walking speed given in percent of normal speed. (0.5 default)
	 */
	public void setWalkSpeed(double walkSpeed) {
		this.walkSpeed = walkSpeed;
	}


	/**
	 * 	To exclude trips with walks from/to coordinates, set this to false.
	 * @param originWalk Exlude == false. Include == true.
	 */
	public void setOriginWalk(boolean originWalk) {
		this.originWalk = (originWalk) ? 1 : 0;
	}


	/**
	 * To exclude trips with walks from/to coordinates, set this to false.
	 * @param destWalk Exclude == false. Include == true. 
	 */
	public void setDestWalk(boolean destWalk) {
		this.destWalk = (destWalk) ? 1 : 0;
	}


	/**
	 * 	To search for walk-only trips, set this to true.
	 * @param onlyWalk Walk-only == true. Not walk-only == false.
	 */
	public void setOnlyWalk(boolean onlyWalk) {
		this.onlyWalk = (onlyWalk) ? 1 : 0;
	}


	/**
	 * To search for trips with a bike ride from the origin to a nearby stop, where the journey continues using public transport, set this to true.
	 * @param originBike Use bike to nearby stop == true. Not use bike to nearby stop == false.
	 */
	public void setOriginBike(boolean originBike) {
		this.originBike = (originBike) ? 1 : 0;
	}


	/**
	 * Sets maximum biking distance from/to the coordinate in meters
	 * @param maxBikeDist Maximum biking distance from/to the coordinate in meters
	 */
	public void setMaxBikeDist(int maxBikeDist) {
		this.maxBikeDist = maxBikeDist;
	}


	/**
	 * Optimize for either the fastest route or a route that is made up of a larger percentage of bike road, where "F" is used to indicate tha fastest 
	 * route with mimimized travel time, and "D" is used to indicate dedicated bike roads to maximize use of bike roads.	
	 * @param bikeCriterion "F" == Fastest route. "D" == Maximize use of bike roads.
	 */
	public void setBikeCriterion(String bikeCriterion) {
		this.bikeCriterion = bikeCriterion;
	}


	/**
	 * Determines the altitude profile of the route, based on a setting for how fast the user can bike when it is steep,
	 *  where "E" is used to indicate easy with minimized steepnes, "N" is used to indicate normal, and "P" is used to indicate powerful to allow more steepness.
	 * @param bikeProfile "E" == Indicate easy with minimized steepnes. "N" == Indicate normal steepness. "P" == Indicate powerful to allow more steepness.
	 */
	public void setBikeProfile(String bikeProfile) {
		this.bikeProfile = bikeProfile;
	}


	/**
	 * To search for bike-only trips, set this to true.
	 * @param onlyBike Bike-only == true. Not Bike-only == false.
	 */
	public void setOnlyBike(boolean onlyBike) {
		this.onlyBike = (onlyBike) ? 1 : 0;
	}


	/**
	 * To search for trips where customer travels by car from the origin and is dropped 
	 * off at a stop to continue the trip using public transport, set this to true.
	 * @param originCar Dropped of by car at stop id == true. Not needed == false.
	 */
	public void setOriginCar(boolean originCar) {
		this.originCar = (originCar) ? 1 : 0;
	}


	/**
	 * To search for trips where the customer travels by car from the origin, 
	 * parks at a commuter parking and walks to a nearby stop to continue the trip using public transport, set this to true.
	 * @param originCarWithParking Needs commuter parking by nearby stop == true. Not needed = false.
	 */
	public void setOriginCarWithParking(boolean originCarWithParking) {
		this.originCarWithParking = (originCarWithParking) ? 1 : 0;
	}


	/**
	 * Sets maximum car distance from/to the coordinate in meters.
	 * @param maxCarDist Maximum car distance from/to the coordinate in meters.
	 */
	public void setMaxCarDist(int maxCarDist) {
		this.maxCarDist = maxCarDist;
	}


	/**
	 * To search for car-only trips, set this to true.
	 * @param onlyCar Car-only == true. Not Car-only == false.
	 */
	public void setOnlyCar(boolean onlyCar) {
		this.onlyCar = (onlyCar) ? 1 : 0;
	}


	/**
	 * Sets maximum number of changes in the trip.
	 * @param maxChanges maximum number of changes in the trip.
	 */
	public void setMaxChanges(int maxChanges) {
		this.maxChanges = maxChanges;
	}


	/**
	 * To prolong the minimal change times in minutes between the public transport legs of the returned journeys
	 * @param addiotionalChangeTime Additional change time in minutes.
	 */
	public void setAddiotionalChangeTime(int addiotionalChangeTime) {
		this.addiotionalChangeTime = addiotionalChangeTime;
	}


	/**
	 * To ignore the default change margin, set this to true.
	 * @param disregardDefaultChangeMargin Ignore the default change margin == true. Not to ignore == false.
	 */
	public void setDisregardDefaultChangeMargin(boolean disregardDefaultChangeMargin) {
		this.disregardDefaultChangeMargin = (disregardDefaultChangeMargin) ? 1 : 0;
	}


	/**
	 * If the reference URL for the journey detail service is not needed in the respons, set this to false.
	 * @param needJourneyDetail Needs URL for journey detail == true. Not needed == false.
	 */
	public void setNeedJourneyDetail(boolean needJourneyDetail) {
		this.needJourneyDetail = (needJourneyDetail) ? 1 : 0;
	}


	/**
	 * If a reference link for each leg of the resulting trips, which can be used to request the geometry, is needed, set this to true.
	 * @param needGeo Need geometry == true. Not needed == false.
	 */
	public void setNeedGeo(boolean needGeo) {
		this.needGeo = (needGeo) ? 1 : 0;
	}


	/**
	 * If a reference link for each leg of the resulting trips, which can be used to request the itinerary, is needed, set this to true
	 * @param needltinerary Need itinerary == true. Not needed == false.
	 */
	public void setNeedltinerary(boolean needltinerary) {
		this.needltinerary = (needltinerary) ? 1 : 0;
	}


	/**
	 * Sets the number of trips in the returned result.
	 * @param numTrips Number of trips in the returned result.
	 */
	public void setNumTrips(int numTrips) {
		this.numTrips = numTrips;
	}
	
	
	
	
	
	
}
