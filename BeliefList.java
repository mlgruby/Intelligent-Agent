package uk.ac.rhul.cs.dice.vacuum;

public class BeliefList {
	private Belief userLocationBelief = null;
	private Belief agentLocationBelief = null;
	private Belief selfLocationBelief = null;

	public Belief getUserLocationBelief() {
		return userLocationBelief;
	}

	public void setUserLocationBelief(int x, int y) {
		this.userLocationBelief = new Belief(x, y, "USER");
	}
	
	public Belief getAgentLocationBelief() {
		return agentLocationBelief;
	}
	
	public void setAgentLocationBelief(int x, int y, String color, String orientation) {
		this.agentLocationBelief = new Belief(x, y, "dirt");
		agentLocationBelief.setColor(color);
		agentLocationBelief.setOrientation(orientation);		
	}
	
	public Belief getAgentLocation() {
		return agentLocationBelief;
	}
	
	public void setSelfLocationBelief(int x, int y, String orientation) {
		this.selfLocationBelief = new Belief(x, y, "self");
		selfLocationBelief.setOrientation(orientation);
	}
}
