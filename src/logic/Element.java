package logic;

public class Element {

	private String type;
	private String direction;
	
	public Element(String type){
		
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDirection() {
		return direction;
	}
	
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
}
