// for the building/structures
public class Building{
	String bldgname; // name ng building
	int bldgnum; // number ng building
	int row; // number ng rows na inooccupy niya
	int col; // number ng columns na inooccupy niya
	int life; // yung life ng building
	

	// constructor
	public Building(String name, int num, int r, int c, int l){
		this.bldgname = name;
		this.bldgnum = num;
		this.row = r;
		this.col = c;
		this.life = l;
	}	
}