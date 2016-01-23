package uk.ac.rhul.cs.dice.vacuum;

public class Belief {

	private String type;
	private int x;
	int y;
	private String orientation = "";
	private String color = "";
	

	public Belief(int x, int y, String type) {
		this.type = type;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public String getType() {
		return type;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	
	public String getOrientation() {
		return orientation;
	}
}
