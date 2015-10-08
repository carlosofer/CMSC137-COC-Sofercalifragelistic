import java.awt.Dimension;
import javax.swing.*;

// para dun sa menu ng pagbibuild. baka aysuin ko pa 'to'
public class BuildButton extends JButton{
	int bldgnum;
	String bldgname;
	int bldgrow;
	int bldgcol;
	public BuildButton(int num){
		// Town Hall
		if(num==1){
			bldgnum = num;
			bldgname = "Town Hall";
			bldgrow = 7;
			bldgcol = 7;
		} else if(num==2){ // Army Camp
			bldgnum = num;
			bldgname = "Army Camp";
			bldgrow = 2;
			bldgcol = 2;
		} else if(num==3){ // Gold Mine
			bldgnum = num;
			bldgname = "Gold Mine";
			bldgrow = 3;
			bldgcol = 3;
		} else if(num==4){ // Elixir Collector
			bldgnum = num;
			bldgname = "Elixir Collector";
			bldgrow = 3;
			bldgcol = 3;
		} else if(num==5){ // Wall
			bldgnum = num;
			bldgname = "Wall";
			bldgrow = 1;
			bldgcol = 1;
		}
	}

	// kapag ireready na siyang ilagay sa base
	public int BuildFlag(){
		return this.bldgnum;
	}
}