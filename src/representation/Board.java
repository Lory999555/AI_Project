package representation;

public class Board {
	
	public static final long b_d = 0x00000000000000ffL; // down
	public static final long b_u = 0xff00000000000000L; // up
	public static final long b_r = 0x0101010101010101L; // right
	public static final long b_l = 0x8080808080808080L; // left
	
	public static final long b2_d = 0x000000000000ffffL; // down
	public static final long b2_u = 0xffff000000000000L; // up
	public static final long b2_r = 0x0303030303030303L; // right
	public static final long b2_l = 0xC0C0C0C0C0C0C0C0L; // left
	
	public static final String[] SQUARE_NAMES =  
		{"H8","H7","H6","H5","H4","H3","H2","H1",
		"G8","G7","G6","G5","G4","G3","G2","G1",
		"F8","F7","F6","F5","F4","F3","F2","F1",
		"E8","E7","E6","E5","E4","E3","E2","E1",
		"D8","D7","D6","D5","D4","D3","D2","D1",
		"C8","C7","C6","C5","C4","C3","C2","C1",
		"B8","B7","B6","B5","B4","B3","B2","B1",
		"A8","A7","A6","A5","A4","A3","A2","A1"};
	
	public static final byte[] BIT_TABLE = {
			63, 30, 3, 32, 25, 41, 22, 33, 
			15, 50, 42, 13, 11, 53, 19, 34, 
			61, 29, 2, 51, 21, 43, 45, 10, 
			18, 47, 1, 54, 9, 57,0, 35,
			62,31, 40, 4, 49, 5, 52, 26, 
			60, 6, 23, 44, 46, 27, 56, 16, 
			7,39, 48, 24, 59, 14, 12, 55, 
			38,28, 58, 20, 37, 17, 36, 8};
	
	public static final int ONE = 1;
	public static final int TW0 = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 7;
	public static final int NINE = 7;
	public static final int TEN = 7;
	public static final int ELEVEN = 7;
	public static final int TWELVE = 7;
		
	public static final int VAL_ONE = 1;
	public static final int VAL_TW0 = 2;
	public static final int VAL_THREE = 3;
	public static final int VAL_FOUR = 4;
	public static final int VAL_FIVE = 5;
	public static final int VAL_SIX = 6;
	public static final int VAL_SEVEN = 7;
	public static final int VAL_EIGHT = 8;
	public static final int VAL_NINE = 9;
	public static final int VAL_TEN = 10;
	public static final int VAL_ELEVEN = 11;
	public static final int VAL_TWELVE = 12;
}
