package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Score extends JPanel {

	private int cost = 0;
	private int numDiamonds = 0;
	private int hp = 100;	
	JLabel lcost = null;
	JLabel ldiamonds = null;
	JLabel llife = null;
	
	public Score(){
		
		Dimension d = new Dimension();
		int pixels = 50;

		d.width = 100;
		d.height = 12 * pixels;
		this.setPreferredSize(d);
		
	}
	
	public void paintComponent(Graphics g) {			
		
		super.paintComponent(g);
				
		String acumCost = "Custo: " + this.cost;
		String quantDiamonds = "Diamantes: " + this.numDiamonds;
		String life = "HP: " + this.hp;			
		
		if(lcost != null){
			lcost.setText(acumCost);
			ldiamonds.setText(quantDiamonds);
			llife.setText(life);
		}	
		else{
			ldiamonds = new JLabel(quantDiamonds);		
			ldiamonds.setFont(new Font("Arial", Font.BOLD, 14));			

			lcost = new JLabel(acumCost);		
			lcost.setFont(new Font("Arial", Font.BOLD, 14));			

			llife = new JLabel(life);			
			llife.setFont(new Font("Arial", Font.BOLD, 14));			

			this.add(ldiamonds);
			this.add(lcost);
			this.add(llife);
		}		
				
	}
	
	public void setCost(int actionCost){
		
		this.cost = actionCost;
	}
	
	public void addDiamond(){
		
		this.numDiamonds++;
	}
	
	public void setHp(int hp) {
		this.hp = hp;
	}
}
