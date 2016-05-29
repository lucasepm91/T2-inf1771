package logic;

import java.util.ArrayList;

public class Room {

	ArrayList<Element> elements = null;
	ArrayList<Senses> senses = null;
	
	public Room(){
	
	}
	
	public void addElement(Element e){
		
		if(elements == null)
			elements = new ArrayList<Element>();
		
		elements.add(e);
	}
	
	public boolean removeElement(String e){
		
		for(int i = 0; i < elements.size();i++){
			if(elements.get(i).getType().equalsIgnoreCase(e)){
				elements.remove(i);
				return true;
			}			
		}	
		return false;
	}
	
	public void addSense(Senses s){
		
		if(senses == null)
			senses = new ArrayList<Senses>();
		
		senses.add(s);
	}
	
	public void removeSense(Senses s){
		
		senses.remove(s);
	}
	
	public ArrayList<Element> getElements() {
		return elements;
	}
	
	public ArrayList<Senses> getSenses() {
		return senses;
	}
			
	
}
