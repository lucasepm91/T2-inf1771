package logic;

import java.util.LinkedList;

public class ActionChain {
	
	private static ActionChain chain = null; 
	private LinkedList<Action> steps;
	
	private ActionChain(){
		
		steps = new LinkedList<Action>();
	}
	
	public static ActionChain getInstance(){
		
		if(chain == null){
			chain = new ActionChain();
		}
		
		return chain;
	}
	
	public void addAction(Action a){
		
		steps.add(a);
	}
	
	public Action nextAction(){
		
		Action temp = steps.getFirst();
		
		steps.remove(temp);
		
		return temp;
	}

	public boolean finished(){
				
		return steps.isEmpty();			
	}
}
