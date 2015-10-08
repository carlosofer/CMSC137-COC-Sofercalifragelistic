/**
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
/**
 * @author John Carlo Sofer
 *
 */

// yung class para sa window ng game
public class ClansWindow {
	JFrame mainframe = new JFrame("Clash");
	JPanel mainpanel = new JPanel(new GridBagLayout());
	JPanel leftpanel = new JPanel();
	JPanel rightpanel = new JPanel(new GridLayout(5, 1));
	JPanel centerpanel = new JPanel(new GridLayout(45, 45));
	JPanel bottompanel = new JPanel();
	Ground[][] button = new Ground[45][45]; // yung para sa mapa
	BuildButton[] bldg = new BuildButton[5]; // yung para sa menu
	JButton troopbutton = new JButton("Place Troop");

	int bldgflag = 0; // kung may ibibuild o wala
	int troopflag = 0; // kung may ilalagay na troop o wala
	
	public ClansWindow(){
		//mainpanel.setLayout(new GridLayout(400, 400));
		mainframe.add(mainpanel);
		mainpanel.setPreferredSize(new Dimension(1000, 700));
		centerpanel.setPreferredSize(new Dimension(700, 700));
		rightpanel.setPreferredSize(new Dimension(300, 700));
		
		// i-seset yung grid/lupa na paglalagyan ng base
		for(int i=0; i<45; i++){
			for(int j=0; j<45; j++){
				button[i][j] = new Ground();
				button[i][j].setPreferredSize(new Dimension(25, 25));
				button[i][j].setBackground(Color.GREEN);
				button[i][j].row = i;
				button[i][j].col = j;
				button[i][j].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent event){
							Ground src = (Ground)event.getSource();
							Ground gr = button[src.row][src.col];
							if(bldgflag !=0){ // pag may ibibuild
								boolean valid = false;
								// boundrow and boundcol
								// sila yung magdedetermine nung range ng ibibuild na building
								int boundrow = 0;
								int boundcol = 0;
								Building b = null;
								if(bldgflag==1){ // pag town hall
									boundrow = 3;
									boundcol = 3;
									b = new Building("Town Hall", 1, 7, 7, 2000);
								} else if(bldgflag==2){ // pag army camp
									boundrow = 1;
									boundcol = 1;
									b = new Building("Army Camp", 2, 2, 2, 400);
								} else if(bldgflag==3){ // pag gold mine
									boundrow = 1;
									boundcol = 1;
									b = new Building("Gold Mine", 3, 3, 3, 750);
								} else if(bldgflag==4){ // pag elixir collector
									boundrow = 1;
									boundcol = 1;
									b = new Building("Elixir Collector", 4, 3, 3, 750);
								} else if(bldgflag==5){ // pag wall
									boundrow = 0;
									boundcol = 0;
									b = new Building("Wall", 5, 1, 1, 550);
								}
								// i-checheck niya kung out of bounds ba yung ibibuild tapos ichecheck kung tatama ba sa building o troop yung ibibuild
								// magsisimula siya dun sa may boundary ng building o yung pinaka outline ng building, papunta sa center
								check:
								if(b.row%2!=0 && b.col%2!=0){ 
									for(int i = boundrow; i>=0; i--){
										for(int j = boundcol; j>=0; j--){
											if(((src.row-i)>=0 && (src.col-j)>=0) && ((src.row+i)<45 && (src.col+j)<45)){
												if(button[src.row-i][src.col-j].occupiedflag == false && button[src.row-i][src.col-j].steppedflag == false && button[src.row+i][src.col+j].occupiedflag == false && button[src.row+i][src.col+j].steppedflag == false){
													if(button[src.row-i][src.col+j].occupiedflag == false && button[src.row-i][src.col+j].steppedflag == false && button[src.row+i][src.col-j].occupiedflag == false && button[src.row+i][src.col-j].steppedflag == false){
														valid = true;
													} else {
														valid=false;
														break check;
													}
												} else {
													valid=false;
													break check;
												}
											} else {
												valid=false;
												break check;
											}
										}
									}
								} else {
									for(int i = boundrow; i>=0; i--){
										for(int j = boundcol; j>=0; j--){
											if(((src.row-i)>=0 && (src.col-j)>=0) && ((src.row+i)<45 && (src.col+j)<45)){
												if(button[src.row+i][src.col+j].occupiedflag == false && button[src.row+i][src.col+j].steppedflag == false){
													valid = true;
												} else {
													valid=false;
													break check;
												}
											} else {
												valid=false;
												break check;
											}
										}
									}
								}
								
								// pag walang conflict, proceed to build
								// iaassign na yung building sa bawat tiles na range ng building na yun
								// tapos ididisable yung button na naassign ng building
								building:
								if(valid == true){
									if(b.row%2!=0 && b.col%2!=0){
										for(int i = boundrow; i>=0; i--){
											for(int j = boundcol; j>=0; j--){
												button[src.row-i][src.col-j].build(bldgflag, b, button[src.row-i][src.col-j]);
												button[src.row-i][src.col-j].occupiedflag = true;
												button[src.row-i][src.col-j].steppedflag = true;
												button[src.row-i][src.col+j].build(bldgflag, b, button[src.row-i][src.col+j]);
												button[src.row-i][src.col+j].occupiedflag = true;
												button[src.row-i][src.col+j].steppedflag = true;
												button[src.row+i][src.col-j].build(bldgflag, b, button[src.row+i][src.col-j]);
												button[src.row+i][src.col-j].occupiedflag = true;
												button[src.row+i][src.col-j].steppedflag = true;
												button[src.row+i][src.col+j].build(bldgflag, b, button[src.row+i][src.col+j]);
												button[src.row+i][src.col+j].occupiedflag = true;
												button[src.row+i][src.col+j].steppedflag = true;
												button[src.row][src.col].build(bldgflag, b, button[src.row][src.col]);
												button[src.row-i][src.col-j].setEnabled(false);
												button[src.row-i][src.col+j].setEnabled(false);
												button[src.row+i][src.col-j].setEnabled(false);
												button[src.row+i][src.col+j].setEnabled(false);
												button[src.row][src.col].setEnabled(false);
												button[src.row][src.col].occupiedflag = true;
												button[src.row][src.col].steppedflag = true;
											}
										}
									} else {
										for(int i = boundrow; i>=0; i--){
											for(int j = boundcol; j>=0; j--){
												button[src.row+i][src.col+j].build(bldgflag, b, button[src.row+i][src.col+j]);
												button[src.row+i][src.col+j].occupiedflag = true;
												button[src.row+i][src.col+j].steppedflag = true;
												button[src.row][src.col].build(bldgflag, b, button[src.row][src.col]);
												button[src.row+i][src.col+j].setEnabled(false);
												button[src.row][src.col].setEnabled(false);
												button[src.row][src.col].occupiedflag = true;
												button[src.row][src.col].steppedflag = true;
											}
										}
									}
									
									bldgflag = 0;
								}
								
							}
							if(troopflag == 1){
								button[src.row][src.col].setBackground(Color.RED);
								button[src.row][src.col].setEnabled(false);
								troopflag = 0;
								
								/*Thread t = new Thread() {
									public void run(){
										while(true){
											if(button[gr.row][gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
											if(button[gr.row]gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
											if(button[gr.row]gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
											if(button[gr.row]gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
										}
									}
								};*/
								
							}
					}
				});
				centerpanel.add(button[i][j]);
			}
		}
		// yung sa menu buttons
		for(int i=0; i<5; i++){
			bldg[i] = new BuildButton(i+1);
			bldg[i].setText(bldg[i].bldgname);
			bldg[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
						BuildButton src = (BuildButton)event.getSource();
						bldgflag = src.bldgnum;
						if(troopflag!=0)
							troopflag = 0;
				}
			});
			rightpanel.add(bldg[i]);
		}
		troopbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				troopflag = 1;
				if(bldgflag!=0)
					bldgflag = 0;
			}
		});
		rightpanel.add(troopbutton);
		mainpanel.add(centerpanel);
		mainpanel.add(rightpanel);
		
	}
	
	public void showWindow(){
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setResizable(false);
		mainframe.pack();
		mainframe.setVisible(true);
	}
}
