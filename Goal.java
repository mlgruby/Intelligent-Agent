package uk.ac.rhul.cs.dice.vacuum;

public class Goal {

	private int x; // X position of goal in grid
	private int y; // Y position of goal in grid
	
	private int priority;
	private int distance; // distance of goal from the USER
	private String color = ""; // Colour for dirt

	public Goal(int x, int y, int priority) {
		this.priority = priority;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPriority() {
		return priority;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	

	// Calculates the distance (Kind of Manhattan distance) the Goal is from the user
	public void setDistance(int curLocationX, int curLocationY) {
		distance = 0;

		if (x < curLocationX) {
			distance += curLocationX - x;
		} else if (x > curLocationX) {
			distance += x - curLocationX;
		}

		if (y < curLocationY) {
			distance += curLocationY - y;
		} else if (y > curLocationY) {
			distance += y - curLocationY;
		}
	}

	public int getDistance() {
		return distance;
	}
}