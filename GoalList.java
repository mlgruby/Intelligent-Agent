package uk.ac.rhul.cs.dice.vacuum;

import java.util.ArrayList;

public class GoalList {
	
	// The data structure where the goals are stored internally
	private ArrayList<Goal> goalList;
	// The reference to the nearest Goal to the user
	private Goal nearestGoal;
	BeliefList beliefList;

	//Default constructor
	public GoalList(BeliefList beliefList) {
		this.beliefList = beliefList;
		goalList = new ArrayList<Goal>();
	}

	public Goal GetNearestGoal(int currentX, int currentY) {
		for (Goal goal : goalList) {
			goal.setDistance(currentX, currentY);

			if (nearestGoal == null)
				nearestGoal = goal;

			if (goal.getDistance() <= nearestGoal.getDistance()	&& goal.getPriority() <= nearestGoal.getPriority()) // get nearest goal to user
				if (goal.getX() != beliefList.getUserLocationBelief().getX()
				|| goal.getY() != beliefList.getUserLocationBelief().getY()) // goal location should not be the USER location
					nearestGoal = goal; // set the goal nearest to user only if USER is not on top of goal
			
		}
		return nearestGoal;
	}

	public void AddGoal(Goal goal) {
		if(!goalList.isEmpty()) {
			for (Goal goalListItem : goalList) {
				if(goalListItem.getX() == goal.getX() && goalListItem.getY() == goal.getY()) {
					System.out.println("This goal already exists");
					return;
				}
			}
		}
		goalList.add(goal);
	}

	public void RemoveGoal(Goal goal) {
		if (!goalList.isEmpty()) {
			goalList.remove(goal);
			if (nearestGoal.equals(goal))
				nearestGoal = null;
		}
	}
}
