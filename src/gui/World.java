package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import logic.Action;
import logic.Element;
import logic.Room;

@SuppressWarnings("serial")
public class World extends JPanel {	

	private ImageIcon[] walkRight = new ImageIcon[3];
	private ImageIcon[] walkLeft = new ImageIcon[3];
	private ImageIcon[] walkUp = new ImageIcon[3];
	private ImageIcon[] walkDown = new ImageIcon[3];
	private ImageIcon[] explosion = new ImageIcon[19];
	private ImageIcon[] death = new ImageIcon[3];
	private ImageIcon[] soldier = new ImageIcon[3];
	private ImageIcon[] fireball = new ImageIcon[4];
	private ImageIcon vortex = new ImageIcon("img/black_hole.png");
	private ImageIcon life = new ImageIcon("img/life.png");
	private ImageIcon diamond = new ImageIcon("img/diamond.png");
	private ImageIcon hole = new ImageIcon("img/hole.png");	
	private ImageIcon exit = new ImageIcon("img/dungeon_exit.png");	
	Action currentAction = null;
	private int currentFrame = 0;
	private int frameExplosion = 0;
	private int posX = 0;
	private int posY = 11 * pixels;
	private int speed = 5;
	private int fireballSpeed = 10;
	private int fireballCount = 0;
	private int fireballEnd = 0;
	private int tempDest = 0;
	private String direction = "up";
	private final static int pixels = 50;
	private final static int offset = 10;
	private final static int NUM_COLS = 12;
	private final static int NUM_ROWS = 12;
	private Room[][] maze = null;

	public World(){

		Dimension d = new Dimension();

		d.width = NUM_COLS * pixels;
		d.height = NUM_ROWS * pixels;
		this.setPreferredSize(d);		
		loadSprites();

	}

	private void loadSprites() {

		walkRight[0] = new ImageIcon("img/strago_right1.png");
		walkRight[1] = new ImageIcon("img/strago_right2.png");
		walkRight[2] = new ImageIcon("img/strago_right3.png");

		walkLeft[0] = new ImageIcon("img/strago_left1.png");
		walkLeft[1] = new ImageIcon("img/strago_left2.png");
		walkLeft[2] = new ImageIcon("img/strago_left3.png");

		walkUp[0] = new ImageIcon("img/strago_up1.png");
		walkUp[1] = new ImageIcon("img/strago_up2.png");
		walkUp[2] = new ImageIcon("img/strago_up3.png");

		walkDown[0] = new ImageIcon("img/strago_down1.png");
		walkDown[1] = new ImageIcon("img/strago_down2.png");
		walkDown[2] = new ImageIcon("img/strago_down3.png");		

		explosion[0] = new ImageIcon("img/explosion1.png");
		explosion[1] = new ImageIcon("img/explosion1.png");
		explosion[2] = new ImageIcon("img/explosion2.png");
		explosion[3] = new ImageIcon("img/explosion3.png");
		explosion[4] = new ImageIcon("img/explosion4.png");
		explosion[5] = new ImageIcon("img/explosion5.png");
		explosion[6] = new ImageIcon("img/explosion6.png");
		explosion[7] = new ImageIcon("img/explosion7.png");
		explosion[8] = new ImageIcon("img/explosion8.png");
		explosion[9] = new ImageIcon("img/explosion9.png");
		explosion[10] = new ImageIcon("img/explosion10.png");
		explosion[11] = new ImageIcon("img/explosion11.png");
		explosion[12] = new ImageIcon("img/explosion12.png");
		explosion[13] = new ImageIcon("img/explosion13.png");
		explosion[14] = new ImageIcon("img/explosion14.png");
		explosion[15] = new ImageIcon("img/explosion15.png");
		explosion[16] = new ImageIcon("img/explosion16.png");		
		
		death[0] = new ImageIcon("img/death_down.png");
		death[1] = new ImageIcon("img/death_left.png");
		death[2] = new ImageIcon("img/death_right.png");
		
		soldier[0] = new ImageIcon("img/soldier_down.png");
		soldier[1] = new ImageIcon("img/soldier_left.png");
		soldier[2] = new ImageIcon("img/soldier_right.png");
		
		fireball[0] = new ImageIcon("img/fireball_right.png");
		fireball[1] = new ImageIcon("img/fireball_left.png");
		fireball[2] = new ImageIcon("img/fireball_up.png");
		fireball[3] = new ImageIcon("img/fireball_down.png");

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		int rectWidth = pixels;
		int rectHeight = pixels;

		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				// coloca cor nos quadrados
				int x = i * rectWidth;
				int y = j * rectHeight;
				Color terrainColor = Color.LIGHT_GRAY;

				if(i == 0 || i == 11|| j == 0 || j == 11)
					terrainColor = Color.gray;

				g.setColor(terrainColor);
				g.drawRect(y, x, rectWidth, rectHeight);
				g.fillRect(y, x, rectWidth, rectHeight);

			}
		}

		// Faz as bordas nos quadrados
		int width = NUM_COLS * pixels;
		int height = NUM_ROWS * pixels;

		g.setColor(Color.BLACK);
		for (int i = 0; i < NUM_ROWS; i++)
			g.drawLine(0, i * pixels, width, i *pixels);

		for (int i = 0; i < NUM_COLS; i++)
			g.drawLine(i * pixels, 0, i * pixels, height);

		
		for(int j = 0;j < 12;j++)
			for(int i = 0;i < 12;i++){
				ArrayList<Element> arr = maze[i][j].getElements();
				if(arr != null){
					if(arr.size() > 0){
						for(int z = 0;z < arr.size();z++){
							if(arr.get(z).getType().equalsIgnoreCase("death")){
								if(arr.get(z).getDirection().equalsIgnoreCase("down"))
									death[0].paintIcon(this,g,i * pixels + offset,j * pixels + offset);
								else
									if(arr.get(z).getDirection().equalsIgnoreCase("left"))
										death[1].paintIcon(this,g,i * pixels + offset,j * pixels + offset);
									else
										if(arr.get(z).getDirection().equalsIgnoreCase("right"))
											death[2].paintIcon(this,g,i * pixels + offset,j * pixels + offset);
							}
							else
								if(arr.get(z).getType().equalsIgnoreCase("soldier")){
									if(arr.get(z).getDirection().equalsIgnoreCase("down"))
										soldier[0].paintIcon(this,g,i * pixels + offset,j * pixels + offset);
									else
										if(arr.get(z).getDirection().equalsIgnoreCase("left"))
											soldier[1].paintIcon(this,g,i * pixels + offset,j * pixels + offset);
										else
											if(arr.get(z).getDirection().equalsIgnoreCase("right"))
												soldier[2].paintIcon(this,g,i * pixels + offset,j * pixels + offset);
								}
								else
									if(arr.get(z).getType().equalsIgnoreCase("vortex")){
										vortex.paintIcon(this,g,i * pixels,j * pixels);
									}
									else
										if(arr.get(z).getType().equalsIgnoreCase("energy")){
											life.paintIcon(this,g,i * pixels + offset,j * pixels + offset);
										}
										else
											if(arr.get(z).getType().equalsIgnoreCase("hole")){
												hole.paintIcon(this,g,i * pixels + offset,j * pixels + offset);
											}
											else
												if(arr.get(z).getType().equalsIgnoreCase("diamond")){
													diamond.paintIcon(this,g,i * pixels + offset,j * pixels + offset);
												}
						}
					}
				}
			}				

		exit.paintIcon(this,g,1,11 * pixels + 2);		
		
		if(currentAction == null){
			if(direction.equalsIgnoreCase("right"))
				walkRight[1].paintIcon(this, g,posX + offset,posY + offset);
			else if(direction.equalsIgnoreCase("left"))
				walkLeft[1].paintIcon(this, g,posX + offset,posY + offset);
			else if(direction.equalsIgnoreCase("up"))
				walkUp[1].paintIcon(this, g,posX + offset,posY + offset);
			else if(direction.equalsIgnoreCase("down"))
				walkDown[1].paintIcon(this, g,posX + offset,posY + offset);
		}
		else{
			if(currentAction.getType().equalsIgnoreCase("walk")){
				if(currentAction.getDirection().equalsIgnoreCase("up")){
					
					currentFrame = currentAction.getCurrentFrame();
					walkUp[currentFrame].paintIcon(this,g,posX + offset,posY + offset);
					if(currentFrame == currentAction.getTotFrames() - 1)
						currentAction.setCurrentFrame(0);
					else
						currentAction.setCurrentFrame(currentFrame + 1);
					posY -= speed;
					
				}
				else if(currentAction.getDirection().equalsIgnoreCase("down")){
					
					currentFrame = currentAction.getCurrentFrame();					
					walkDown[currentFrame].paintIcon(this,g,posX + offset,posY + offset);
					if(currentFrame == currentAction.getTotFrames() - 1)
						currentAction.setCurrentFrame(0);
					else
						currentAction.setCurrentFrame(currentFrame + 1);
					posY += speed;
					
				}
				else if(currentAction.getDirection().equalsIgnoreCase("left")){
					
					currentFrame = currentAction.getCurrentFrame();									
					walkLeft[currentFrame].paintIcon(this,g,posX + offset,posY + offset);
					if(currentFrame == currentAction.getTotFrames() - 1)
						currentAction.setCurrentFrame(0);
					else
						currentAction.setCurrentFrame(currentFrame + 1);
					posX -= speed;
					
				}
				else if(currentAction.getDirection().equalsIgnoreCase("right")){
					
					currentFrame = currentAction.getCurrentFrame();										
					walkRight[currentFrame].paintIcon(this,g,posX + offset,posY + offset);
					if(currentFrame == currentAction.getTotFrames() - 1)
						currentAction.setCurrentFrame(0);
					else
						currentAction.setCurrentFrame(currentFrame + 1);
					posX += speed;
					
				}
				direction = currentAction.getDirection();
				
			}
			else
				if(currentAction.getType().equalsIgnoreCase("explosion")){
					if(currentAction.getDirection().equalsIgnoreCase("up")){
						tempDest = currentAction.getDestY() - pixels;
						frameExplosion = currentAction.getCurrentFrame();
						walkUp[1].paintIcon(this,g,posX + offset,posY + offset);
						explosion[frameExplosion].paintIcon(this,g,posX,tempDest);
						if(frameExplosion == currentAction.getTotFrames() - 1)
							frameExplosion = 0;
						else
							currentAction.setCurrentFrame(frameExplosion + 1);
						
						if(frameExplosion == 6){
							int xE = posX / pixels;
							int yE = tempDest / pixels;
							
							if(!maze[xE][yE].removeElement("soldier"))
								maze[xE][yE].removeElement("death");
						}
					}
					else if(currentAction.getDirection().equalsIgnoreCase("down")){
						tempDest = currentAction.getDestY() + pixels;
						frameExplosion = currentAction.getCurrentFrame();
						walkDown[1].paintIcon(this,g,posX + offset,posY + offset);
						explosion[frameExplosion].paintIcon(this,g,posX,tempDest);
						if(frameExplosion == currentAction.getTotFrames() - 1)
							frameExplosion = 0;
						else
							currentAction.setCurrentFrame(frameExplosion + 1);
						
						if(frameExplosion == 6){
							int xE = posX / pixels;
							int yE = tempDest / pixels;
							
							if(!maze[xE][yE].removeElement("soldier"))
								maze[xE][yE].removeElement("death");
						}
					}
					else if(currentAction.getDirection().equalsIgnoreCase("left")){
						tempDest = currentAction.getDestX() - pixels;
						frameExplosion = currentAction.getCurrentFrame();
						walkLeft[1].paintIcon(this,g,posX + offset,posY + offset);
						explosion[frameExplosion].paintIcon(this,g,tempDest,posY);
						if(frameExplosion == currentAction.getTotFrames() - 1)
							frameExplosion = 0;
						else
							currentAction.setCurrentFrame(frameExplosion + 1);
						
						if(frameExplosion == 6){
							int xE = tempDest / pixels;
							int yE = posY / pixels;
							
							if(!maze[xE][yE].removeElement("soldier"))
								maze[xE][yE].removeElement("death");
						}
					}
					else if(currentAction.getDirection().equalsIgnoreCase("right")){
						tempDest = currentAction.getDestX() + pixels;						
						frameExplosion = currentAction.getCurrentFrame();
						walkRight[1].paintIcon(this,g,posX + offset,posY + offset);
						explosion[frameExplosion].paintIcon(this,g,tempDest,posY);
						if(frameExplosion == currentAction.getTotFrames() - 1)
							frameExplosion = 0;
						else
							currentAction.setCurrentFrame(frameExplosion + 1);
						
						if(frameExplosion == 6){
							int xE = tempDest / pixels;
							int yE = posY / pixels;
							
							if(!maze[xE][yE].removeElement("soldier"))
								maze[xE][yE].removeElement("death");
						}
					}
					direction = currentAction.getDirection();					
				}
				else
					if(currentAction.getType().equalsIgnoreCase("pickDiamond")){
						int xD = currentAction.getDestX() / pixels;
						int yD = currentAction.getDestY() / pixels;
												
						maze[xD][yD].removeElement("diamond");						
						
						if(direction.equalsIgnoreCase("right"))
							walkRight[1].paintIcon(this, g,posX + offset,posY + offset);
						else if(direction.equalsIgnoreCase("left"))
							walkLeft[1].paintIcon(this, g,posX + offset,posY + offset);
						else if(direction.equalsIgnoreCase("up"))
							walkUp[1].paintIcon(this, g,posX + offset,posY + offset);
						else if(direction.equalsIgnoreCase("left"))
							walkDown[1].paintIcon(this, g,posX + offset,posY + offset);
						
						direction = currentAction.getDirection();
					}
					else
						if(currentAction.getType().equalsIgnoreCase("pickEnergy")){
							int xE = currentAction.getDestX() / pixels;
							int yE = currentAction.getDestY() / pixels;
													
							maze[xE][yE].removeElement("energy");
							
							if(direction.equalsIgnoreCase("right"))
								walkRight[1].paintIcon(this, g,posX + offset,posY + offset);
							else if(direction.equalsIgnoreCase("left"))
								walkLeft[1].paintIcon(this, g,posX + offset,posY + offset);
							else if(direction.equalsIgnoreCase("up"))
								walkUp[1].paintIcon(this, g,posX + offset,posY + offset);
							else if(direction.equalsIgnoreCase("left"))
								walkDown[1].paintIcon(this, g,posX + offset,posY + offset);
							
							direction = currentAction.getDirection();
						}
						else
							if(currentAction.getType().equalsIgnoreCase("magic")){									
								if(currentAction.getDirection().equalsIgnoreCase("up")){
									if(fireballCount == 0){
										fireballCount = posY;
									}
									else
										fireballCount -= fireballSpeed;
																			
									walkUp[1].paintIcon(this,g,posX + offset,posY + offset);
									fireball[2].paintIcon(this,g,posX + 15,fireballCount);
									
									if(fireballCount == currentAction.getDestY() - pixels + offset){
										fireballCount = 0;
										fireballEnd = 1;
									}	
								}
								else
									if(currentAction.getDirection().equalsIgnoreCase("down")){
										if(fireballCount == 0){
											fireballCount = posY;
										}
										else
											fireballCount += fireballSpeed;
										
										walkDown[1].paintIcon(this,g,posX + offset,posY + offset);
										fireball[3].paintIcon(this,g,posX + 15,fireballCount);
										
										if(fireballCount == currentAction.getDestY() + pixels - offset){
											fireballCount = 0;
											fireballEnd = 1;
										}	
									}
									else
										if(currentAction.getDirection().equalsIgnoreCase("left")){
											if(fireballCount == 0){
												fireballCount = posX;
											}
											else												
												fireballCount -= fireballSpeed;										
											
											walkLeft[1].paintIcon(this,g,posX + offset,posY + offset);
											fireball[1].paintIcon(this,g,fireballCount,posY + 15);
											
											if(fireballCount == currentAction.getDestX() - pixels + offset){
												fireballCount = 0;
												fireballEnd = 1;
											}	
										}
										else
											if(currentAction.getDirection().equalsIgnoreCase("right")){
												if(fireballCount == 0){
													fireballCount = posX;
												}
												else
													fireballCount += fireballSpeed;
												
												walkRight[1].paintIcon(this,g,posX + offset,posY + offset);
												fireball[0].paintIcon(this,g,fireballCount+15,posY + 15);
												
												if(fireballCount == currentAction.getDestX() + pixels - offset){
													fireballCount = 0;
													fireballEnd = 1;
												}													
											}
								direction = currentAction.getDirection();
							}							
		}			
		
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public String getDirection() {
		return direction;
	}

	public Action getAction(){		
		return currentAction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public void setAction(Action a){
		this.currentAction = a;
	}
	
	public void setMaze(Room[][] maze) {
		this.maze = maze;
	}
	
	public int getFireballEnd() {
		return fireballEnd;
	}
	
	public void setFireballEnd(int fireballEnd) {
		this.fireballEnd = fireballEnd;
	}
}
