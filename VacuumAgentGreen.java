package uk.ac.rhul.cs.dice.vacuum;

import java.util.List;
import java.util.Vector;

import org.javatuples.Pair;

import uk.ac.rhul.cs.dice.platform.action.Action;
import uk.ac.rhul.cs.dice.platform.action.Percept;
import uk.ac.rhul.cs.dice.platform.agent.AgentBrain;

public class VacuumAgentGreen extends VacuumAgent {
private static final long serialVersionUID = -565563161972920536L;
	
	private String agentObserved;
	int n = 0;
	int curLocationX;
	int curLocationY;
	int prevLocationX = -1;
	int prevLocationY = -1;
	String dirtType;
	String curOrientation = "EAST";
	String prevOrientation = "EAST";
	private BeliefList beliefList;
	private Vector<String> routeToGoal;
	private GoalList goalList;
	boolean movedOrTurned = false;
	boolean isMoved = false;
	boolean isGoalReached = false;
	public VacuumAgentGreen(AgentBrain brain, int x, int y, String direction, String type) {
		super(brain);
		cleanerType = type;
		curLocationX = x;
		curLocationY = y;
		beliefList = new BeliefList();
		beliefList.setUserLocationBelief(0, 0);
		this.goalList = new GoalList(beliefList);
		routeToGoal = new Vector<String>();		
	}	


	@SuppressWarnings("unchecked")
	@Override
	protected void observe(List<Percept> percepts) throws Exception {
		agentObserved = "";
		
		for (Percept percept : percepts) {
            Action action = percept.getPerceptContent();
            Pair<String, String> payload = (Pair<String, String>) action.getPayload();
            
            // process observation coming from the environment
            // message format: loc(ag0,0,6,NORTH)#user(0,0)#agent(ag1,WHITE,1,6,WEST)#dirt(ORANGE,1,6)
            if (action.getActionType().equals(VacuumActionType.SENSING_ACT.toString())) {
            	String area = payload.getValue1();
            	
            	// get location
            	String obs[] = area.split("#");
            	for (int i = 0; i < obs.length; i++)
            		if (obs[i].startsWith("agent")) {
            			String ag[] = obs[i].substring(6, obs[i].length() - 1).split(",");
            			agentObserved = ag[0] + "#" + ag[1];
            			agentLog.add("Seen " + ag[1] + " agent " + ag[0]);
            			beliefList.setAgentLocationBelief(Integer.parseInt(ag[2]),
            					Integer.parseInt(ag[3]), ag[1], ag[4]);
            		} else if (obs[i].startsWith("loc")) {
						String ag[] = obs[i].substring(4, obs[i].length() - 1).split(",");
						this.curLocationX = Integer.parseInt(ag[1]);
						this.curLocationY = Integer.parseInt(ag[2]);
						this.prevOrientation = curOrientation;
						this.curOrientation = ag[3];
						beliefList.setSelfLocationBelief(Integer.parseInt(ag[1]), Integer.parseInt(ag[2]), ag[3]);
					} else if (obs[i].startsWith("user")) {
						String ag[] = obs[i].substring(5, obs[i].length() - 1).split(",");
						beliefList.setUserLocationBelief(Integer.parseInt(ag[0]),Integer.parseInt(ag[1]));
					} else if (obs[i].startsWith("dirt")) {
						String ag[] = obs[i].substring(5, obs[i].length() - 1).split(",");
						if(ag[0].equals("GREEN")) { // add only green dirt
							Goal goal = new Goal(Integer.parseInt(ag[1]),Integer.parseInt(ag[2]),1);
							System.out.println("X: " + ag[1] + " Y: " + ag[2]);
							goal.setColor(ag[0]);
							goalList.AddGoal(goal);
						}
					} 
            }

            // message received from another agent
            if (action.getActionType().equals(VacuumActionType.SPEECH_ACT.toString())) {
            	VacuumWorldApp.log(getBrain().getAgentId() + " received message from " + payload.getValue0() + ": " + payload.getValue1());
            	agentLog.add("Received message from " + payload.getValue0() + ": " + payload.getValue1());
            }
		}
	}

	private Boolean turnToWest() {
		if(curOrientation.equals("NORTH"))
			routeToGoal.add("TURN_LEFT");
		else if(curOrientation.equals("SOUTH"))
			routeToGoal.add("TURN_RIGHT");
		else if(curOrientation.equals("EAST"))
			routeToGoal.add("TURN_LEFT");
		else
			return true;
		
		return false;
	}
	private Boolean turnToEast() {
		if(curOrientation.equals("NORTH"))
			routeToGoal.add("TURN_RIGHT");
		else if(curOrientation.equals("SOUTH"))
			routeToGoal.add("TURN_LEFT");
		else if(curOrientation.equals("WEST"))
			routeToGoal.add("TURN_RIGHT");
		else
			return true;
		
		return false;
	}
	
	private Boolean turnToSouth() {
		if(curOrientation.equals("NORTH"))
			routeToGoal.add("TURN_RIGHT");
		else if(curOrientation.equals("WEST"))
			routeToGoal.add("TURN_LEFT");
		else if(curOrientation.equals("EAST"))
			routeToGoal.add("TURN_RIGHT");
		else
			return true;
		
		return false;
	}

	private Boolean turnToNorth() {
		if(curOrientation.equals("SOUTH"))
			routeToGoal.add("TURN_RIGHT");
		else if(curOrientation.equals("EAST"))
			routeToGoal.add("TURN_LEFT");
		else if(curOrientation.equals("WEST"))
			routeToGoal.add("TURN_RIGHT");
		else
			return true;
		
		return false;
	}


	@Override
	protected String decide() throws Exception {
		String nextMovement = "";
		// tell action format: TELL#agentId#message
		if (!agentObserved.equals("")) {
			String obs[] = agentObserved.split("#"); 
			return "TELL#" + obs[0] + "#Hi " + obs[1] + " agent " + obs[0];
		}
		else {
			if (this.n == 0) { // if n is not know, find n and add goal to goallist
				Goal goal;
				// check if agent is moving or not
				if (this.curLocationX == this.prevLocationX && this.curLocationY == this.prevLocationY) {
					isMoved = false;
				} else {
					isMoved = true;
				}
				
				if (isMoved) { // Basic grid exploration starts here
					routeToGoal.add("MOVE");
					this.prevLocationX = curLocationX;
					this.prevLocationY = curLocationY;
				} else { 
					n = curLocationX;
					// adds goal at corner -- This will move agent to nearest corner towards EAST
					if (curLocationY <= (n / 2)) { // Go to the top corner if it is nearer
						goal = new Goal(n, 0, 2); // add goal at top right corner
						System.out.println("addedGoalToCorner() : x " + n + " y 0 ");
					} else { // Go to the bottom corner if it is nearer
						goal = new Goal(n, n, 2); // add  goal at bottom right corner
						System.out.println("addedGoalToCorner() : x " + n + " y " + n);
					}
					goalList.AddGoal(goal);
					
					if (curLocationY > n/2){ // start grid exploration if starting position in upper half of grid
						for (int i = 0; i <= n; i++) {
							for (int j = 0; j <= n; j++) {
								goalList.AddGoal(new Goal(j, i, 3));
//								System.out.println("X: " + j + " Y: " + i);
							}
						}
					} else { // start grid exploration if starting position in lower half of grid
						for (int i = n; i >= 0; i--) {
							for (int j = 0; j <= n; j++) {
								goalList.AddGoal(new Goal(j, i, 3));
							}
						}
					} 
				}
		} else { // move to next goal if n is known
			Goal goal = goalList.GetNearestGoal(curLocationX, curLocationY);
			// check if goal already achieved
			if (goal == null){
				isGoalReached = true;
			} else if (goal.getX() == curLocationX && goal.getY() == curLocationY){
				isGoalReached =  true;
			}				
			else {
				isGoalReached =  false;
			}
			
			if (isGoalReached) {
				// if green dirt then resest the colour and clean green dirt
				if(/*goal.getColor().equals("ORANGE") || */goal.getColor().equals("GREEN")) {
					goal.setColor("");
//					System.out.println("got ya!!");
					return "CLEAN";
				}
				goalList.RemoveGoal(goal);
				goal = goalList.GetNearestGoal(curLocationX, curLocationY);
				if (!routeToGoal.isEmpty()) {
					routeToGoal.clear();
				}
			}
			
			if(isGoalReached) {
				System.out.println(" VacuumAgentExplorer::decide() : No goal or Moving to next goal.");
				return nextMovement;
			}
			
			if(routeToGoal.isEmpty()) {
				// checks if agent moved or turned
				if (this.curLocationX == this.prevLocationX && 
						this.curLocationY == this.prevLocationY && 
						curOrientation == prevOrientation) {
					movedOrTurned = false;
				} else {
					movedOrTurned = true;
				}
				
				if(movedOrTurned) { // move or turn towards goal only if moving
					prevLocationX = curLocationX;
					prevLocationY = curLocationY;
					prevOrientation = curOrientation;
					if(goal.getX() < curLocationX) {
						if(turnToWest())
							routeToGoal.add("MOVE");
					} else if(goal.getX() > curLocationX) {
						if(turnToEast())
							routeToGoal.add("MOVE");
					} else if(goal.getY() > curLocationY) {
						if(turnToSouth())
							routeToGoal.add("MOVE");
					} else if(goal.getY() < curLocationY) {
						if(turnToNorth())
							routeToGoal.add("MOVE");
					}
				}
			}
			
		}
			
		if (!routeToGoal.isEmpty()) { // if goal is empty then take next goal and move
			nextMovement = routeToGoal.get(0);
			routeToGoal.remove(0);
		}
		
		if (nextMovement.equals("")) // if no goal in list
			System.out.println(" VacuumAgentExplorer::decide() : No goal, nothing to do.");
		else
			System.out.println(" VacuumAgentExplorer::decide() : "+ nextMovement);
		}
		return nextMovement;
	}
}
