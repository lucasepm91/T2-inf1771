package logic;

import java.util.ArrayList;
import java.util.Map;

import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

public class Prolog {

	private static Prolog prolog = null;
	private String direction = "up";
	private Room[][] maze;
	private int pixels = 50;	
	private boolean end = false;	
	
	private Prolog(){
		
	}
	
	public static Prolog getInstance(){
		
		if(prolog == null)
			prolog = new Prolog();
		
		return prolog;
	}
	
	public boolean checkProlog(){
		
		ActionChain chain = ActionChain.getInstance();
						
		if(end == false){			
			Query q2 = new Query("proximo_passo(P,X,Y,XR,YR)");
			q2.open();
			Map<String, Term> solution = q2.getSolution();			
			Query q3 = null;

			if(solution.get("P").toString().equalsIgnoreCase("pegar_diamante")){			
				String x = solution.get("X").toString();
				String y = solution.get("Y").toString();
				int xDest = Integer.parseInt(x) * pixels;
				int yDest = Integer.parseInt(y) * pixels;
				Action act;
				
				q3 = new Query("pegar_diamante("+x+","+y+")");
				if(q3.hasSolution()){
					Query queryNum = new Query("num_diamantes(D)");
					Map<String, Term> solutionD;
					int num_diamonds;
					int currentCost = consultCost();
					int currentEnergy = consultEnergy();
					
					queryNum.open();
					solutionD = queryNum.getSolution();
					num_diamonds = solutionD.get("D").intValue();
					System.out.println("proximo_passo(pegar_diamante) em X: "+x+", Y: "+y);
					System.out.println("Número de diamantes coletados: "+num_diamonds);					
					act = new Action("pickDiamond",this.direction,this.direction,xDest,yDest,currentEnergy,currentCost);
					chain.addAction(act);
					queryNum.close();
				}
				q3.close();
			}
			else
				if(solution.get("P").toString().equalsIgnoreCase("pegar_energia")){			
					String x = solution.get("X").toString();
					String y = solution.get("Y").toString();
					int xDest = Integer.parseInt(x) * pixels;
					int yDest = Integer.parseInt(y) * pixels;
					Action act;

					q3 = new Query("pegar_energia("+x+","+y+")");
					if(q3.hasSolution()){
						int currentCost = consultCost();
						int currentEnergy = consultEnergy();
						
						System.out.println("proximo_passo(pegar_energia) em X: "+x+", Y: "+y);
						act = new Action("pickEnergy",this.direction,this.direction,xDest,yDest,currentEnergy,currentCost);
						chain.addAction(act);
					}				

					q3.close();
				}
				else
					if(solution.get("P").toString().equalsIgnoreCase("sair")){	
						System.out.println("proximo_passo(sair)");
						end = true;						
					}
					else
						if(solution.get("P").toString().equalsIgnoreCase("ir_para_saida")){			
							String x = solution.get("X").toString();
							String y = solution.get("Y").toString();
							String xm = solution.get("XR").toString();
							String ym = solution.get("YR").toString();
											
							exitPath(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xm),Integer.parseInt(ym));
						}
						else
							if(solution.get("P").toString().equalsIgnoreCase("fimDeJogo")){	
								System.out.println("proximo_passo(fimDeJogo)");
								end = true;								
							}
							else
								if(solution.get("P").toString().equalsIgnoreCase("atacar_diamante")){			
									String x = solution.get("X").toString();
									String y = solution.get("Y").toString();
									String xr = solution.get("XR").toString();
									String yr = solution.get("YR").toString();
									String aux;
									int xDest = Integer.parseInt(x) * pixels;
									int yDest = Integer.parseInt(y) * pixels;
									Action act;
									Query q4 = new Query("tem_inimigo("+xr+","+yr+","+"_)");

									q3 = new Query("atacar_diamante("+x+","+y+","+xr+","+yr+")");
									if(q3.hasSolution()){
										int currentCost = consultCost();
										int currentEnergy = consultEnergy();
										
										System.out.println("proximo_passo(atacar_diamante) X: "+xr+", Y: "+yr);
										
										aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
										act = new Action("magic",aux,this.direction,xDest,yDest,currentEnergy,currentCost);
										this.direction = aux;
										chain.addAction(act);
									}				

									q3.close();
									
									if(!q4.hasSolution()){
										int currentCost = consultCost();
										int currentEnergy = consultEnergy();
										
										Action act2 = null;
										
										aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
										act2 = new Action("explosion",aux,this.direction,xDest,yDest,currentEnergy,currentCost);
										chain.addAction(act2);
									}
								}
								else
									if(solution.get("P").toString().equalsIgnoreCase("entrar_casa_inimigo")){			
										String x = solution.get("X").toString();
										String y = solution.get("Y").toString();
										String xr = solution.get("XR").toString();
										String yr = solution.get("YR").toString();
										String aux;
										int xDest = Integer.parseInt(xr) * pixels;
										int yDest = Integer.parseInt(yr) * pixels;
										Action act;
										
										q3 = new Query("entrar_casa_inimigo("+x+","+y+","+xr+","+yr+")");
										if(q3.hasSolution()){										
											int currentCost = consultCost();
											int currentEnergy = consultEnergy();
											
											System.out.println("proximo_passo(entrar_casa_inimigo) X: "+xr+", Y: "+yr);
											
											aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
											act = new Action("walk",aux,this.direction,xDest,yDest,currentEnergy,currentCost);
											this.direction = aux;
											chain.addAction(act);
										}				

										q3.close();
									}
									else
										if(solution.get("P").toString().equalsIgnoreCase("entrar_casa_diamante")){			
											String x = solution.get("X").toString();
											String y = solution.get("Y").toString();
											String xr = solution.get("XR").toString();
											String yr = solution.get("YR").toString();
											String aux;
											int xDest = Integer.parseInt(xr) * pixels;
											int yDest = Integer.parseInt(yr) * pixels;
											Action act;

											q3 = new Query("entrar_casa_diamante("+x+","+y+","+xr+","+yr+")");
											if(q3.hasSolution()){
												int currentCost = consultCost();
												int currentEnergy = consultEnergy();
												aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
												System.out.println("proximo_passo(entrar_casa_diamante) X: "+xr+", Y: "+yr);
												
												aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
												act = new Action("walk",aux,this.direction,xDest,yDest,currentEnergy,currentCost);
												this.direction = aux;
												chain.addAction(act);
											}				

											q3.close();
										}
										else
											if(solution.get("P").toString().equalsIgnoreCase("fugir")){			
												String x = solution.get("X").toString();
												String y = solution.get("Y").toString();
												String xr = solution.get("XR").toString();
												String yr = solution.get("YR").toString();
												String aux;
												int xDest = Integer.parseInt(xr) * pixels;
												int yDest = Integer.parseInt(yr) * pixels;
												Action act;
												
												q3 = new Query("fugir("+x+","+y+","+xr+","+yr+")");
												if(q3.hasSolution()){
													int currentCost = consultCost();
													int currentEnergy = consultEnergy();
													
													System.out.println("proximo_passo(fugir) para X: "+xr+", Y: "+yr);													
													aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
													act = new Action("walk",aux,this.direction,xDest,yDest,currentEnergy,currentCost);
													this.direction = aux;
													chain.addAction(act);
												}				

												q3.close();
											}
											else
												if(solution.get("P").toString().equalsIgnoreCase("andar_seguro")){			
													String x = solution.get("X").toString();
													String y = solution.get("Y").toString();													
													String xr = solution.get("XR").toString();
													String yr = solution.get("YR").toString();
													String aux;
													int xDest = Integer.parseInt(xr) * pixels;
													int yDest = Integer.parseInt(yr) * pixels;
													Action act;
													
													q3 = new Query("andar_seguro("+x+","+y+","+xr+","+yr+")");
													if(q3.hasSolution()){
														int currentCost = consultCost();
														int currentEnergy = consultEnergy();
														
														System.out.println("proximo_passo(andar_seguro) para X: "+xr+", Y: "+yr);														
														aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
														act = new Action("walk",aux,this.direction,xDest,yDest,currentEnergy,currentCost);
														this.direction = aux;
														chain.addAction(act);
													}				

													q3.close();
												}
												else
													if(solution.get("P").toString().equalsIgnoreCase("procurar_energia")){			
														String x = solution.get("X").toString();
														String y = solution.get("Y").toString();
														String xm = solution.get("XR").toString();
														String ym = solution.get("YR").toString();
														
														energyPath(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xm),Integer.parseInt(ym));
													}
													else
														if(solution.get("P").toString().equalsIgnoreCase("morrer")){
															System.out.println("proximo_passo(morrer)");
															end = true;															
														}
														else
															if(solution.get("P").toString().equalsIgnoreCase("andar_verificada")){
																String x = solution.get("X").toString();
																String y = solution.get("Y").toString();
																String xm = solution.get("XR").toString();
																String ym = solution.get("YR").toString();
																
																walkPath(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xm),Integer.parseInt(ym));															
															}
															else
																if(solution.get("P").toString().equalsIgnoreCase("andar")){
																	String x = solution.get("X").toString();
																	String y = solution.get("Y").toString();													
																	String xr = solution.get("XR").toString();
																	String yr = solution.get("YR").toString();
																	String aux;
																	int xDest = Integer.parseInt(xr) * pixels;
																	int yDest = Integer.parseInt(yr) * pixels;
																	Action act;
																	
																	q3 = new Query("andar("+x+","+y+","+xr+","+yr+")");
																	if(q3.hasSolution()){
																		int currentCost = consultCost();
																		int currentEnergy = consultEnergy();
																		
																		System.out.println("proximo_passo(andar) para X: "+xr+", Y: "+yr);																		
																		aux = checkDirection(Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(xr),Integer.parseInt(yr));
																		act = new Action("walk",aux,this.direction,xDest,yDest,currentEnergy,currentCost);
																		this.direction = aux;
																		chain.addAction(act);
																	}				

																	q3.close();
																}

			q2.close();
			
		}
		return this.end;
	}
	
	public boolean loadPrologFile(){
		
		Query q1 = new Query("consult", new Term[] {new Atom("Prolog/prolog.pl")});
		System.out.println( "consult " + (q1.hasSolution() ? "succeeded" : "failed"));
		
		if(q1.hasSolution()){
			return true;
		}
		return false;
	}
	
	public void loadValidPositions(){
		
		Query q2;
		
		for(int j = 0;j < 12; j++)
			for(int i = 0; i < 12; i++){
				q2 = new Query("assert(valida("+i+","+j+"))");				
				q2.open();
				q2.getSolution();
				q2.close();
			}
	}
	
	public void loadElements(){
		
		Query qe = null;
		
		for(int j = 0;j < 12;j++)
			for(int i = 0;i < 12;i++){
				ArrayList<Element> arr = maze[i][j].getElements();
				ArrayList<Senses> arrS = maze[i][j].getSenses();
				if(arr != null){
					for(int z = 0;z < arr.size();z++){
						if(arr.get(z).getType().equalsIgnoreCase("death")){
							int pwr = 50;
							qe = new Query("assert(inimigo("+i+","+j+","+pwr+"))");								
							qe.open();
							qe.getSolution();
							qe.close();
						}
						else
							if(arr.get(z).getType().equalsIgnoreCase("soldier")){
								int pwr = 20;
								qe = new Query("assert(inimigo("+i+","+j+","+pwr+"))");									
								qe.open();
								qe.getSolution();
								qe.close();
							}
							else
								if(arr.get(z).getType().equalsIgnoreCase("diamond")){									
									qe = new Query("assert(diamante("+i+","+j+"))");
									
									qe.open();
									qe.getSolution();
									qe.close();									
								}
								else
									if(arr.get(z).getType().equalsIgnoreCase("energy")){									
										qe = new Query("assert(moeda_energia("+i+","+j+"))");
										
										qe.open();
										qe.getSolution();
										qe.close();										
									}
									else
										if(arr.get(z).getType().equalsIgnoreCase("hole")){									
											qe = new Query("assert(buraco("+i+","+j+"))");
											qe.open();
											qe.getSolution();
											qe.close();
										}
										else
											if(arr.get(z).getType().equalsIgnoreCase("vortex")){									
												qe = new Query("assert(vortice("+i+","+j+"))");
												qe.open();
												qe.getSolution();
												qe.close();
											}
					}
				}
				
				if(arrS != null && arrS.size() > 0){					
					for(int z = 0;z < arrS.size();z++){
						if(arrS.get(z).equals(Senses.NOISE)){
							qe = new Query("assert(barulho("+i+","+j+"))");							
							qe.open();
							qe.getSolution();
							qe.close();							
						}
						else
							if(arrS.get(z).equals(Senses.FLASH)){
								qe = new Query("assert(flash("+i+","+j+"))");								
								qe.open();
								qe.getSolution();
								qe.close();								
							}
							else
								if(arrS.get(z).equals(Senses.BREEZE)){
									qe = new Query("assert(brisa("+i+","+j+"))");									
									qe.open();
									qe.getSolution();
									qe.close();									
								}	
								else
									if(arrS.get(z).equals(Senses.DIAMONDGLOW)){
										qe = new Query("assert(brilho_diamante("+i+","+j+"))");
										qe.open();
										qe.getSolution();
										qe.close();
									}	
									else
										if(arrS.get(z).equals(Senses.ENERGYGLOW)){
											qe = new Query("assert(brilho_energia("+i+","+j+"))");
											qe.open();
											qe.getSolution();
											qe.close();
										}		
					}					
				}
			}		
	}
	
	private String checkDirection(int x,int y,int xn,int yn){
		
		int resX = x - xn;
		int resY = y - yn;		
		
		if(resX == 0 && resY < 0){			
			return "down";
		}
		else
			if(resX == 0 && resY > 0){
				return "up";
			}
			else
				if(resX < 0 && resY== 0){
					return "right";
				}
				else
					if(resX > 0 && resY == 0){
						return "left";
					}
		
		return this.direction;
	}
	
	public void setMaze(Room[][] maze) {
				
		this.maze = maze;
	}
		
	
	private void energyPath(int x,int y,int xr,int yr){
		
		Query rooms = new Query("visitada(X,Y),not(ruim(X,Y))");
		Map<String, Term>[] solutionR = rooms.allSolutions();
		ArrayList<Node> provided = new ArrayList<Node>();
		ArrayList<Node> path = null;
		Node start = null;
		Node end = null;
		AStar astar = null;		
		
		for(int i = 0;i < solutionR.length;i++){			
			Node n = new Node(solutionR[i].get("X").intValue(),solutionR[i].get("Y").intValue());			
			if(solutionR[i].get("X").intValue() == xr && solutionR[i].get("Y").intValue() == yr)
				end = n;
			else
				if(solutionR[i].get("X").intValue() == x && solutionR[i].get("Y").intValue() == y)
					start = n;
			provided.add(n);
		}
		
		astar = new AStar(provided,start,end);		
		astar.search();		
		path = astar.getPath();		
		breakPathEnergy(path,x,y);
		rooms.close();
				
	}
	
	private void exitPath(int x,int y,int xr,int yr){
		
		Query rooms = new Query("visitada(X,Y),not(ruim(X,Y))");
		Map<String, Term>[] solutionR = rooms.allSolutions();
		ArrayList<Node> provided = new ArrayList<Node>();
		ArrayList<Node> path = null;
		Node start = null;
		Node end = null;
		AStar astar = null;		
		
		for(int i = 0;i < solutionR.length;i++){			
			Node n = new Node(solutionR[i].get("X").intValue(),solutionR[i].get("Y").intValue());			
			if(solutionR[i].get("X").intValue() == xr && solutionR[i].get("Y").intValue() == yr)
				end = n;
			else
				if(solutionR[i].get("X").intValue() == x && solutionR[i].get("Y").intValue() == y)
					start = n;
			provided.add(n);
		}
		
		astar = new AStar(provided,start,end);
		astar.search();		
		path = astar.getPath();			
		breakPathExit(path,x,y);
		rooms.close();		
		
	}
	
	private void walkPath(int x,int y,int xr,int yr){
		
		Query rooms = new Query("visitada(X,Y),not(ruim(X,Y))");
		Map<String, Term>[] solutionR = rooms.allSolutions();
		ArrayList<Node> provided = new ArrayList<Node>();
		ArrayList<Node> path = null;
		Node start = null;
		Node end = new Node(xr,yr);
		AStar astar = null;		
		
		for(int i = 0;i < solutionR.length;i++){			
			Node n = new Node(solutionR[i].get("X").intValue(),solutionR[i].get("Y").intValue());			
			
			if(solutionR[i].get("X").intValue() == x && solutionR[i].get("Y").intValue() == y)
				start = n;
			
			provided.add(n);
		}
		provided.add(end);
				
		astar = new AStar(provided,start,end);
		astar.search();		
		path = astar.getPath();		
		breakPathWalk(path,x,y);
		rooms.close();		
		
	}
	
	private void breakPathEnergy(ArrayList<Node> path,int xStart,int yStart){
		
		ActionChain chain = ActionChain.getInstance();
		String directionPath = "Caminho para energia: ";
		String resDir;
		Node n = null;
		Action a = null;
		int previousX = xStart;
		int previousY = yStart;
		int currentCost = 0;
		int currentEnergy = 0;			
		Query walk = null;
		
		for(int i = 0;i < path.size();i++){
			n = path.get(i);
			if(n.getX() != xStart || n.getY() != yStart){
				walk = new Query("andar_seguro("+previousX+","+previousY+","+n.getX()+","+n.getY()+")");
				walk.open();
				walk.getSolution();
				walk.close();
				currentCost = consultCost();
				currentEnergy = consultEnergy();
				resDir = checkDirection(previousX,previousY,n.getX(),n.getY());
				a = new Action("walk",resDir,this.direction,n.getX()*pixels,n.getY()*pixels,currentEnergy,currentCost);
				chain.addAction(a);
				previousX = n.getX();
				previousY = n.getY();
				directionPath += resDir + "|"; 				
			}
		}
		
		System.out.println(directionPath);
				
	}
	
	private void breakPathExit(ArrayList<Node> path,int xStart,int yStart){
		
		ActionChain chain = ActionChain.getInstance();
		String directionPath = "Caminho para saída: ";
		String resDir;
		Node n = null;
		Action a = null;
		int previousX = xStart;
		int previousY = yStart;
		int currentCost = 0;
		int currentEnergy = 0;			
		Query walk = null;		
		
		for(int i = 0;i < path.size();i++){
			n = path.get(i);
			if(n.getX() != xStart || n.getY() != yStart){
				walk = new Query("andar_seguro("+previousX+","+previousY+","+n.getX()+","+n.getY()+")");
				walk.open();
				walk.getSolution();
				walk.close();
				currentCost = consultCost();
				currentEnergy = consultEnergy();
				resDir = checkDirection(previousX,previousY,n.getX(),n.getY());
				a = new Action("walk",resDir,this.direction,n.getX()*pixels,n.getY()*pixels,currentEnergy,currentCost);
				chain.addAction(a);
				previousX = n.getX();
				previousY = n.getY();
				directionPath += resDir + "|"; 					
			}
		}
				
		System.out.println(directionPath);
				
	}
	
	private void breakPathWalk(ArrayList<Node> path,int xStart,int yStart){
		
		ActionChain chain = ActionChain.getInstance();
		String directionPath = "Caminho para casa verificada: ";
		String resDir;
		Node n = null;
		Action a = null;
		int previousX = xStart;
		int previousY = yStart;
		int currentCost = 0;
		int currentEnergy = 0;			
		Query walk = null;		
		
		for(int i = 0;i < path.size();i++){
			n = path.get(i);
			if(n.getX() != xStart || n.getY() != yStart){
				walk = new Query("andar_verificada("+previousX+","+previousY+","+n.getX()+","+n.getY()+")");
				walk.open();
				walk.getSolution();
				walk.close();
				currentCost = consultCost();
				currentEnergy = consultEnergy();
				resDir = checkDirection(previousX,previousY,n.getX(),n.getY());
				a = new Action("walk",resDir,this.direction,n.getX()*pixels,n.getY()*pixels,currentEnergy,currentCost);
				chain.addAction(a);
				previousX = n.getX();
				previousY = n.getY();
				directionPath += resDir + "|"; 					
			}
		}
				
		System.out.println(directionPath);
				
	}
	
	private int consultEnergy(){
		
		Query energy = new Query("energia(E)");
		Map<String, Term> solutionE = null;
		
		energy.open();
		solutionE = energy.getSolution();
		energy.close();
		if(solutionE != null){
			return solutionE.get("E").intValue();			
		}
		
		return 0;
	}
	
	private int consultCost(){
		
		Query cost = new Query("custo(C)");
		Map<String, Term> solutionC = null;
		
		cost.open();
		solutionC = cost.getSolution();
		cost.close();
		if(solutionC != null){
			return solutionC.get("C").intValue();			
		}
		
		return 0;
	}
	
	
}
