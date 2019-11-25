package representation;

public class Board {
	
	public enum File {H,G,F,E,D,C,B,A};
	public enum directionNS {S,P,N};
	public enum directionEO {E,P,O};
	
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
	
	public static final long[][] movingBook = { //ATTENZIONE: ritorna pure la pedina stessa 
			{0L,1282L,0L,5128L,0L,20512L,0L,16512L,131330L,0L,656394L,0L,2625576L,0L,10502304L,0L,0L,84018432L,0L,336073728L,0L,1344294912L,0L,1082146816L,8606842880L,0L,43017437184L,0L,172069748736L,0L,688278994944L,0L,0L,5506231959552L,0L,22024927838208L,0L,88099711352832L,0L,70919573733376L,564058054983680L,0L,2819190763290624L,0L,11276763053162496L,0L,45107052212649984L,0L,0L,360856417701199872L,0L,1443425670804799488L,0L,5773702683219197952L,0L,4647785184190529536L,72620543991349248L,0L,291045125918818304L,0L,1164180503675273216L,0L,4656722014701092864L,0L},
			{0L,656650L,0L,2757674L,0L,11030696L,0L,10502304L,84018434L,0L,352982282L,0L,1411929128L,0L,1352683680L,0L,0L,43034215690L,0L,180726928426L,0L,722907713704L,0L,688279011488L,5506232091904L,0L,23133046838528L,0L,92532187354112L,0L,88649477672960L,0L,0L,2820290359459840L,0L,11844119981326336L,0L,47376479925305344L,0L,45107053296877568L,360856426375020544L,0L,1516047357609771008L,0L,6064189430439084032L,0L,5809732168775106560L,0L,0L,363108260464558080L,0L,1452996000401588224L,0L,5811984001606352896L,0L,4656793070640037888L,360856417701199872L,0L,1516047314307776512L,0L,6064189257231106048L,0L,5809731480238161920L,0L},
			{0L,269092106L,0L,1093276714L,0L,78139560L,0L,278937760L,34443756802L,0L,137791935754L,0L,559757677608L,0L,35712422048L,0L,0L,17635220260106L,0L,71648982733866L,0L,5120954224808L,0L,18280465055904L,2257306045777160L,0L,9030332301579552L,0L,36684279159739522L,0L,2340449291358216L,0L,0L,1155741794966310912L,0L,4695587732446658816L,0L,335606856077018112L,0L,1198028557903728640L,360856426375544832L,0L,1516047357611868160L,0L,6064189430447603712L,0L,5809732168775630848L,0L,0L,363108260732993536L,0L,1452996001492107264L,0L,5811984001673461760L,0L,4656793070908473344L,360856452060938240L,0L,1516047451746729984L,0L,6064189815576854528L,0L,5809731514597900288L,0L},
			{0L,146297980202L,0L,585208829098L,0L,146107027626L,0L,584394490024L,18726141433090L,0L,74904582640906L,0L,19251455350056L,0L,74802503111840L,0L,0L,9587784430519562L,0L,38352245823771690L,0L,9575270162518184L,0L,38298877298229408L,1227236404958987528L,0L,4908946727954421024L,0L,1261663377821291650L,0L,4902256843937566728L,0L,0L,1155741932405264418L,0L,4695588282202472840L,0L,335606864666952738L,0L,1198028592263467144L,360874018561593600L,0L,1516117726356063232L,0L,6064190529959235840L,0L,5809736566822159360L,0L,0L,372115459989962752L,0L,1489024798519984128L,0L,5812546951629111296L,0L,4659044870731071488L,1513777956952997888L,0L,6127733471314968576L,0L,6136247409899995136L,0L,6097961891890462720L,0L},
			{0L,70515042157866L,0L,585208829098L,0L,1245618655402L,0L,4982441001128L,9025925396174082L,0L,36103701601604874L,0L,19251455350056L,0L,637752456533152L,0L,0L,4621273802857907466L,0L,38352245823771690L,0L,81632864200446120L,0L,326529253449941152L,1227236404958987528L,0L,4908946727954421024L,0L,1261663377821291650L,0L,4902256843937566728L,0L,0L,1155741932405264418L,0L,4695588282202472840L,0L,335606864666952738L,0L,1198028592263467144L,360874018561593632L,0L,1516117726356063360L,0L,6064190529959235840L,0L,5809736566822159362L,0L,0L,372115459989979136L,0L,1489024798519984128L,0L,5812546951629111552L,0L,4659044870731072512L,1513777956955095040L,0L,6127733471323357184L,0L,6136247409899995136L,0L,6097961891890593792L,0L},
			{0L,36662262014543274L,0L,2252385022514346L,0L,9008444873396394L,0L,36596729413386410L,4692769537861506306L,0L,324334077753316618L,0L,1152940756062197032L,0L,4684381364921849248L,0L,0L,4621273802866296074L,0L,38352245823771690L,0L,81632864200446120L,0L,326529253450072224L,1227236406032729352L,0L,4908946727954421024L,0L,1261663377821291650L,0L,4902256843954343944L,0L,0L,1155742482161078306L,0L,4695588282202472840L,0L,335606864666952738L,0L,1198028600853401736L,360944387305771296L,0L,1516117726356063360L,0L,6064190529959235840L,0L,5809737666333787138L,0L,0L,408144257008943234L,0L,1489024798519984136L,0L,5812546951629111584L,0L,4659607820684493954L,6125463975382499584L,0L,6127733471323358208L,0L,6136247409899999232L,0L,6170019485928538368L,0L},
			{0L,36662262014543274L,0L,2252385022514346L,0L,9008444873396394L,0L,108654323451314346L,4692769537861506306L,0L,324334077753316618L,0L,1152940756062197032L,0L,4684381364921849248L,0L,0L,4621273802866296074L,0L,38352245823771690L,0L,81632864200446120L,0L,326529253450072224L,1227236406032729352L,0L,4908946727954421024L,0L,1261663377821291650L,0L,4902256843954343944L,0L,0L,1155742482161078306L,0L,4695588282202472840L,0L,335606864666952738L,0L,1198028600853401736L,360944387305771296L,0L,1516117726356063360L,0L,6064190529959235840L,0L,5809737666333787138L,0L,0L,408144257008943234L,0L,1489024798519984136L,0L,5812546951629111584L,0L,4659607820684493954L,6125463975382499712L,0L,6127733471323358208L,0L,6136247409899999232L,0L,6170019485928538368L,0L}
			};
	public static final int[] deathNoteRed = {0, 2, 0, 4, 0, 3, 0, 1,
			 								1, 0, 3, 0, 4, 0, 2, 0,
			 								0, 2, 0, 4, 0, 3, 0, 1, 
			 								1, 0, 3, 0, 4, 0, 2, 0,
			 								0, 2, 0, 4, 0, 3, 0, 1,
			 								1, 0, 3, 0, 3, 0, 2, 0,
			 								0, 2, 0, 2, 0, 2, 0, 1,
			 								1, 0, 1, 0, 1, 0, 1, 0
			 								};
	public static final String[] deathNoteRDirection = {"","NE","", "NE","","NW","", "NW",
													"NE","","NE", "","NW","","NW", "",
													"","NE","", "NE","","NW","", "NW", 
													"NE","","NE", "","NW","","NW", "",
													"","NE","", "NW","","NW","", "NW",
													"NE","","NE", "","NW","","NW", "",
													"","N","", "N","","N","", "NW",
													"N","","N", "","N","","N", "",
													};
	
	public static final String[] deathNoteBDirection = {"","S","", "S","","S","", "S",
													"SE","","S", "","S","","S", "",
													"","SE","", "SE","","SW","", "SW", 
													"SE","","SE", "","SE","","SW", "",
													"","SE","", "SE","","SW","", "SW",
													"SE","","SE", "","SW","","SW", "",
													"","SE","","SE","","SW","","SW",
													"SE","","SE", "","SW","","SW", "",
													};

	
	
	public static final int[] deathNoteBlack = {0, 1, 0, 1, 0, 1, 0, 1,
												1, 0, 2, 0, 2, 0, 2, 0,
												0, 2, 0, 3, 0, 3, 0, 1, 
												1, 0, 3, 0, 4, 0, 2, 0,
												0, 2, 0, 4, 0, 3, 0, 1,
												1, 0, 3, 0, 4, 0, 2, 0,
												0, 2, 0, 4, 0, 3, 0, 1,
												1, 0, 3, 0, 4, 0, 2, 0
												};

	
	
	// ritorna il numero della casella in cui è posizionato il pedone
	public static int getSquare(long position) {
		long b = position ^ (position - 1);
		int fold = (int) (b ^ (b >>> 32));
		return Board.BIT_TABLE[(fold * 0x783a9b23) >>> 26];
	}
	
	
	// Ritorna la posizione esatta del pedone sottoforma di stringa (ES A8) a
	// partire da una bitboard
	public static String DeBruijn(long position) {
		return Board.SQUARE_NAMES[getSquare(position)];
	}
	
	// ritorna l'indice di una casella della scacchiera a partire dalla stringa passata
	public static int stringToSquare(String file,String rank) {
		return File.valueOf(file).ordinal()*8 + (8- Integer.parseInt(rank));
	}
	
	// ritorna una bitBoard a partire dall'indice di una casella della scacchiera
	public static long squareToBitboard( int square) {
		return 1L << square;
	}
	
	// data una posizione, la direzione e la distanza ritorna la casella dove la pedina effettuerà lo spostamento.
	// utilizzata se lo spostamento è verso una direzione N,S,E,O
	public static int toSquare( int square,String NSEO,int dist) {
		return (((enumNSValue(NSEO)-1)*1)*dist)*8 +((enumEOValue(NSEO)-1)*1)*(dist)+ square ;
	}
	
	// data una posizione, la direzione e la distanza ritorna la casella dove la pedina effettuerà lo spostamento
	// utilizzata se lo spostamento è verso una direzionen NE,NO,SE,SO
	public static int toSquare( int square,String NS,String EO,int dist) {
		return ((enumNSValue(NS)-1)*dist)*8 + (enumEOValue(EO)-1)*(dist)+square ;
	}
	
	//ritorna il valore dell'enum passato. Se l'enum non esiste cattura l'errore e ritorna 1
	public static int enumNSValue(String ns) {
		try {
			return  directionNS.valueOf(ns).ordinal();
		}catch(IllegalArgumentException e) {
			return 1;
		}
	}
	
	//ritorna il valore dell'enum passato. Se l'enum non esiste cattura l'errore e ritorna 1
	public static int enumEOValue(String eo) {
		try {
			return  directionEO.valueOf(eo).ordinal();
		}catch(IllegalArgumentException e) {
			return 1;
		}
	}
	

	// flippa di 180 la BitBoard passata
	public static long flip180(long x) {

		// flipping vertically
		long k1 = 0x00FF00FF00FF00FFL;
		long k2 = 0x0000FFFF0000FFFFL;
		x = ((x >>> 8) & k1) | ((x & k1) << 8);
		x = ((x >>> 16) & k2) | ((x & k2) << 16);
		x = (x >>> 32) | (x << 32);

		// mirroring horizontally
		long k3 = 0x5555555555555555L;
		long k4 = 0x3333333333333333L;
		long k5 = 0x0f0f0f0f0f0f0f0fL;
		x = ((x >>> 1) & k3) + 2 * (x & k3);
		x = ((x >>> 2) & k4) + 4 * (x & k4);
		x = ((x >>> 4) & k5) + 16 * (x & k5);

		return x;
	}


}
