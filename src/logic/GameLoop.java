package logic;

import gui.Score;
import gui.World;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.Timer;

public class GameLoop {

	private static boolean animationEnd = false;	
	private static boolean finish = false;	
	private static Timer timer;
	
	public static void main(String[] arg) {
	
		JFrame f = new JFrame();
		World w = new World(); 
		Score s = new Score();
		Room[][] maze = new Room[12][12];		
		
		for(int j = 0;j < 12; j++)
			for(int i = 0;i < 12;i++)
				maze[i][j] = new Room();
		
		if(!loadFromFile(maze)){
			System.out.println("Erro no arquivo do mapa");
			System.exit(0);
		}
				
		w.setMaze(maze);		
		
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add (w,BorderLayout.CENTER);
		f.getContentPane().add (s,BorderLayout.EAST);
		f.setVisible(true);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		int delay = 1000/40;
		
		Prolog.getInstance().setMaze(maze);
		Prolog.getInstance().loadPrologFile();
		Prolog.getInstance().loadValidPositions();
		Prolog.getInstance().loadElements();
	
		ActionListener taskPerformer = new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent evt) {				
	
				animationEnd = animationEnded(w,s);
				
				if(finish == true && ActionChain.getInstance().finished() && animationEnd == true)
					((Timer)evt.getSource()).stop();
				else	
					if(animationEnd == true){
						updateWorld(w);
						animationEnd = false;
					}				
				
				f.repaint();										
			}
	
		};
	
		timer = new Timer(delay,taskPerformer);
		timer.setInitialDelay(500);
		timer.start();
		
		while(finish == false){
			finish = Prolog.getInstance().checkProlog();			
		}
	}	

	private static boolean animationEnded(World w,Score s){

		Action a = w.getAction();

		if(a == null)
			return true;

		if(a.getType().contains("walk")){
			if(w.getPosX() == a.getDestX() && w.getPosY() == a.getDestY()){	
				s.setHp(a.getEnergy());
				s.setCost(a.getCost());
				return true;
			}
		}
		else
			if(a.getType().equalsIgnoreCase("explosion")){
				if(a.getCurrentFrame() == a.getTotFrames() - 1){
					s.setHp(a.getEnergy());
					s.setCost(a.getCost());
					return true;
				}
			}
		else
			if(a.getType().equalsIgnoreCase("pickDiamond")){
				if(a.getCurrentFrame() == a.getTotFrames() - 1){
					s.setHp(a.getEnergy());
					s.setCost(a.getCost());
					s.addDiamond();					
					return true;
				}
			}
			else
				if(a.getType().equalsIgnoreCase("pickEnergy")){
					if(a.getCurrentFrame() == a.getTotFrames() - 1){
						s.setHp(a.getEnergy());
						s.setCost(a.getCost());
						return true;
					}
				}
				else
					if(a.getType().equalsIgnoreCase("magic")){
						if(w.getFireballEnd() == 1){
							w.setFireballEnd(0);
							s.setHp(a.getEnergy());
							s.setCost(a.getCost());
							return true;
						}
					}		

		return false;
	}

	private static void updateWorld(World w) {

		ActionChain chain = ActionChain.getInstance();
		Action a = null;		

		if(!chain.finished()){
			a = chain.nextAction();				
		}		
		w.setAction(a);		
	}
		
	private static boolean loadFromFile(Room[][] maze){
		
		Scanner s = null;
		char[] line = new char[24];
		
		try {
			s = new Scanner(new FileReader("mapa/mapa.txt"));
			//s = new Scanner(new FileReader("mapa/mapa2.txt"));
			//s = new Scanner(new FileReader("mapa/mapa3.txt"));			
		} catch (FileNotFoundException e) {			
			return false;
		}
		
		for (int j = 0; j < 12; j++) {
			for (int i = 0; i < 12; i++) {
				if(i==0){
					line = s.nextLine().toCharArray();
				}    
				if(line[i] == "O".toCharArray()[0]){
					Element e = new Element("diamond");
					e.setDirection(null);
					
					maze[i][j].addElement(e);
					maze[i][j].addSense(Senses.DIAMONDGLOW);
				}
				else
					if(line[i] == "U".toCharArray()[0]){
						Element e = new Element("energy");
						e.setDirection(null);

						maze[i][j].addElement(e);
						maze[i][j].addSense(Senses.ENERGYGLOW);
					}
					else
						if(line[i] == "d".toCharArray()[0]){							
							Element e = new Element("soldier");
							if(j < 4)
								e.setDirection("down");
							else
								if(i < 6)
									e.setDirection("right");
								else
									e.setDirection("left");

							maze[i][j].addElement(e);
							if(i > 0)
								maze[i-1][j].addSense(Senses.NOISE);
							if(i < 11)
								maze[i+1][j].addSense(Senses.NOISE);
							if(j > 0)
								maze[i][j-1].addSense(Senses.NOISE);
							if(j < 11)
								maze[i][j+1].addSense(Senses.NOISE);
						}
						else
							if(line[i] == "D".toCharArray()[0]){							
								Element e = new Element("death");
								if(j < 4)
									e.setDirection("down");
								else
									if(i < 6)
										e.setDirection("right");
									else
										e.setDirection("left");

								maze[i][j].addElement(e);
								if(i > 0)
									maze[i-1][j].addSense(Senses.NOISE);
								if(i < 11)
									maze[i+1][j].addSense(Senses.NOISE);
								if(j > 0)
									maze[i][j-1].addSense(Senses.NOISE);
								if(j < 11)
									maze[i][j+1].addSense(Senses.NOISE);
							}
							else
								if(line[i] == "P".toCharArray()[0]){
									Element e = new Element("hole");
									e.setDirection(null);
									
									maze[i][j].addElement(e);
									if(i > 0)
										maze[i-1][j].addSense(Senses.BREEZE);
									if(i < 11)
										maze[i+1][j].addSense(Senses.BREEZE);
									if(j > 0)
										maze[i][j-1].addSense(Senses.BREEZE);
									if(j < 11)
										maze[i][j+1].addSense(Senses.BREEZE);
								}
								else
									if(line[i] == "T".toCharArray()[0]){
										Element e = new Element("vortex");
										e.setDirection(null);
										
										maze[i][j].addElement(e);
										if(i > 0)
											maze[i-1][j].addSense(Senses.FLASH);
										if(i < 11)
											maze[i+1][j].addSense(Senses.FLASH);
										if(j > 0)
											maze[i][j-1].addSense(Senses.FLASH);
										if(j < 11)
											maze[i][j+1].addSense(Senses.FLASH);
									}
									else
										if(line[i] == "M".toCharArray()[0]){
											Element e = new Element("diamond");
											Element e1 = new Element("death");
											e.setDirection(null);
																						
											maze[i][j].addElement(e1);
											maze[i][j].addElement(e);
											maze[i][j].addSense(Senses.DIAMONDGLOW);
											
											if(j < 4)
												e1.setDirection("down");
											else
												if(i < 6)
													e1.setDirection("right");
												else
													e1.setDirection("left");

											e.setDirection(null);
											
											if(i > 0)
												maze[i-1][j].addSense(Senses.NOISE);
											if(i < 11)
												maze[i+1][j].addSense(Senses.NOISE);
											if(j > 0)
												maze[i][j-1].addSense(Senses.NOISE);
											if(j < 11)
												maze[i][j+1].addSense(Senses.NOISE);
										}
										else
											if(line[i] == "R".toCharArray()[0]){
												Element e = new Element("diamond");
												Element e1 = new Element("soldier");
												e.setDirection(null);
																							
												maze[i][j].addElement(e1);
												maze[i][j].addElement(e);
												maze[i][j].addSense(Senses.DIAMONDGLOW);
												
												if(j < 4)
													e1.setDirection("down");
												else
													if(i < 6)
														e1.setDirection("right");
													else
														e1.setDirection("left");
												
												e.setDirection(null);
												
												if(i > 0)
													maze[i-1][j].addSense(Senses.NOISE);
												if(i < 11)
													maze[i+1][j].addSense(Senses.NOISE);
												if(j > 0)
													maze[i][j-1].addSense(Senses.NOISE);
												if(j < 11)
													maze[i][j+1].addSense(Senses.NOISE);
											}
			}
		}
		s.close();
		return true;
	}
	
}


