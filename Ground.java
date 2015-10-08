import java.awt.Dimension;
import javax.swing.*;
import java.awt.Color;

// yung class para sa lupa ng base
public class Ground extends JButton{
	boolean occupiedflag = false; // kapag may building o wala
	boolean steppedflag = false; // kapag may building/troop o wala
	int row; // yung placement ng row niya sa map/grid
	int col; // yung placement ng column niya sa map/grid
	Building assocbldg; // yung building na nakatayo sa tile


	
	// constructor
	public Ground(){

	}

	// kapag ibibuild na si building
	public void build(int bnum, Building b, Ground g){
		this.occupiedflag = true;
		this.steppedflag = true;
		this.assocbldg = b;
		if(bnum == 1){ // pag town hall
			this.setBackground(Color.WHITE);
		} else if(bnum == 2){ // pag army camp
			this.setBackground(Color.GRAY);
		} else if(bnum == 3){ // pag gold mine
			this.setBackground(Color.YELLOW);
		} else if(bnum == 4){ // pag elixir collector
			this.setBackground(Color.BLUE);
		} else if(bnum == 5){ // pag wall
			this.setBackground(Color.ORANGE);
		}
	}

	
}