package logic;

public class Action {

	private String type;
	private int currentFrame;
	private int totFrames = 0;
	private int destX;
	private int destY;
	private String direction;
	private String prevDirection;
	private int cost;
	private int energy;
	
	public Action(String type,String direction,String prevDirection,
					int destX,int destY,int energy,int cost){
		
		this.currentFrame = 0;
		this.destX = destX;
		this.destY = destY;
		this.direction = direction;
		this.prevDirection = prevDirection;
		this.type = type;
		this.cost = cost;
		this.energy = energy;
		
		if(type.equalsIgnoreCase("walk")){
			totFrames = 3;
		}
		else
			if(type.equalsIgnoreCase("explosion")){
				totFrames = 17;
			}
			else
				if(type.equalsIgnoreCase("pickDiamond")){
					totFrames = 1;
				}
				else
					if(type.equalsIgnoreCase("pickEnergy")){
						totFrames = 1;
					}
					else
						if(type.equalsIgnoreCase("magic")){
							totFrames = 1;
						}
	}
	
	public String getType() {
		return type;
	}
	
	public int getCurrentFrame() {
		return currentFrame;
	}
	
	public int getTotFrames() {
		return totFrames;
	}
	
	public int getDestX() {
		return destX;
	}
	
	public int getDestY() {
		return destY;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public String getPrevDirection() {
		return prevDirection;
	}
	
	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getEnergy() {
		return energy;
	}
}
