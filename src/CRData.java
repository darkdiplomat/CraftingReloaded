import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CRData {
	
	private Logger log = Logger.getLogger("Minecraft");
	
	private HashMap<String, int[]> PBuild;
	private HashMap<String, int[]> PComb;
	private HashMap<String, int[]> PExc;
	private HashMap<String, int[]> PFarm;
	private HashMap<String, int[]> PMine;
	private HashMap<String, int[]> PTech;
	private HashMap<String, int[]> PWood;
	
	private HashMap<Integer, Integer> BET;
	private HashMap<String, Integer> CET;
	
	private ArrayList<Integer> BuildBlocks;
	private ArrayList<Integer> ExcBlocks;
	private ArrayList<Integer> FarmBlocks;
	private ArrayList<Integer> MineBlocks;
	private ArrayList<Integer> TechBlocks;
	private ArrayList<Integer> WoodBlocks;
	private ArrayList<String> CombatEntities;
	private ArrayList<Block> AntiBlockFarm;
	private ArrayList<Integer> RequirePickAxe;
	private ArrayList<Integer> RequireShovel;
	private ArrayList<Integer> RequireAxe;
	
	private final String Dir = "plugins/config/CraftingReloaded/";
	private final String PDir = Dir+"PlayerEXP/";
	private final String Set = "CRSettings.ini";
	private final String EL = "CRExpLvlTable.txt";
	private final String PEL = /*playername*/".txt";
	private final String CETF = "CRCombatEXPTable.txt";
	private final String BETF = "CRBlockEXPTable.txt";
	private final String ABFF = "AntiBlockFarm.txt";
	
	private String UserName = "root";
	private String PassWord = "root";
	private String DataBase = "jdbc:mysql://localhost:3306/minecraft";
	private String Driver = "com.mysql.jdbc.Driver";
	
	private PropertiesFile Settings;
	private PropertiesFile PELP;
	private PropertiesFile ETP;
	
	private Timer SaveIt;
	
	private CRListener CRL;
	
	int[] building, combat, excavating, farming, mining, technician, woodcutting;
	int maxlevel = 100, savedelay = 15, BPILR = 5, B1LVL = 5, B2LVL = 15, B3LVL = 25, B4LVL = 35, B5LVL = 45;
	double B1PS = 0.005, B2PS = 0.005, B3PS = 0.005, B4PS = 0.005, B5PS = 0.005, B1PI = 0.005, B2PI = 0.005, B3PI = 0.005, B4PI = 0.005, B5PI = 0.005;
	
	boolean CMySQL = false, MySQL = false;
	
	public CRData(){
		PBuild = new HashMap<String, int[]>();
		PComb = new HashMap<String, int[]>();
		PExc = new HashMap<String, int[]>();
		PFarm = new HashMap<String, int[]>();
		PMine = new HashMap<String, int[]>();
		PTech = new HashMap<String, int[]>();
		PWood = new HashMap<String, int[]>();
		
		BET = new HashMap<Integer, Integer>();
		CET = new HashMap<String, Integer>();
		
		BuildBlocks = new ArrayList<Integer>();
		ExcBlocks = new ArrayList<Integer>();
		FarmBlocks = new ArrayList<Integer>();
		MineBlocks = new ArrayList<Integer>();
		TechBlocks = new ArrayList<Integer>();
		WoodBlocks = new ArrayList<Integer>();
		CombatEntities = new ArrayList<String>();
		AntiBlockFarm = new ArrayList<Block>();
		RequirePickAxe = new ArrayList<Integer>();
		RequireAxe = new ArrayList<Integer>();
		RequireShovel = new ArrayList<Integer>();
		
		SaveIt = new Timer();
		
		loadSettings();
	}
	
	private void loadSettings(){
		setSettings();
		makeDir();
		ETP = new PropertiesFile((Dir+EL));
		populateBuildBlocks();
		populateFarmBlocks();
		populateMineBlocks();
		populateExcBlocks();
		populateTechBlocks();
		populateWoodBlocks();
		populateEXPTable();
		populateRequirePickAxe();
		populateRequireAxe();
		populateRequireShovel();
		populateCE();
		populateCET();
		populateBET();
		populateAntiBlockFarmData();
		SaveIt.schedule(new SaveTime(), savedelay*60000);
		
	}
	
	private void makeDir(){
		File dirfol = new File(Dir);
		File pdirfol = new File(PDir);
		File expfile = new File(Dir+EL);
		File BETfile = new File(Dir+BETF);
		File CETfile = new File(Dir+CETF);
		if (!dirfol.exists()){
			dirfol.mkdirs();
		}
		if(!pdirfol.exists()){
			pdirfol.mkdir();
		}
		if(!expfile.exists()){
			makeEXPFile();
		}
		if(!BETfile.exists()){
			makeBETFile();
		}
		if(!CETfile.exists()){
			makeCETFile();
		}
	}
	
	private void makeEXPFile(){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Dir+EL));
			out.write("#CraftingReloaded Experience File#"); out.newLine();
			out.write("#Building EXP#"); out.newLine();
			for(int i = 1; i < maxlevel; i++){
				out.write("Building"+i+"="+(i*50)); out.newLine();
			}
			out.write("#Combat EXP#"); out.newLine();
			for(int i = 1; i < maxlevel; i++){
				out.write("Combat"+i+"="+(i*50)); out.newLine();
			}
			out.write("#Excavating EXP#"); out.newLine();
			for(int i = 1; i < maxlevel; i++){
				out.write("Excavating"+i+"="+(i*50)); out.newLine();
			}
			out.write("#Farming EXP#"); out.newLine();
			for(int i = 1; i < maxlevel; i++){
				out.write("Farming"+i+"="+(i*50)); out.newLine();
			}
			out.write("#Mining EXP#"); out.newLine();
			for(int i = 1; i < maxlevel; i++){
				out.write("Mining"+i+"="+(i*50)); out.newLine();
			}
			out.write("#Technician EXP#"); out.newLine();
			for(int i = 1; i < maxlevel; i++){
				out.write("Technician"+i+"="+(i*50)); out.newLine();
			}
			out.write("#WoodCutting EXP#"); out.newLine();
			for(int i = 1; i < maxlevel; i++){
				out.write("WoodCutting"+i+"="+(i*50)); out.newLine();
			}
			out.close();
		}catch (IOException e){
			log.severe("[CraftingReloaded] - Unable to Create Exp File!");
		}
	}
	
	private void makeSettingsFile(){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Dir+Set));
			out.write("#CraftingReloaded Settings File#"); out.newLine();
			out.write("#Building Block IDs Sperated by a Comma ','"); out.newLine();
			out.write("BuildBlockIDs=1,4,5,17,18,19,20,22,24,26,30,35,41,42,44,45,47,48,49,53,54,57,64,65,67,71,79,80,85,87,88,89,91,96,98,101,102,103,107,108,109,112,113,114,121"); out.newLine();
			out.write("#Excavation Block IDs Sperated by a Comma ','"); out.newLine();
			out.write("ExcBlockIDs=1,2,3,12,13,24,48,78,79,87,88,89,98,121"); out.newLine();
			out.write("#Farming Block IDs Sperated by a Comma ','"); out.newLine();
			out.write("FarmBlockIDs=18,31,37,38,39,40,59,83,81,86,99,100,103,104,105,106"); out.newLine();
			out.write("#Mining Block IDs Sperated by a Comma ','"); out.newLine();
			out.write("MineBlockIDs=14,15,16,21,73,74"); out.newLine();
			out.write("#Technician Block IDs Sperated by a Comma ','"); out.newLine();
			out.write("#Note Some IDs may require to be the actual Item ID (ie: RedStoneRepeater)"); out.newLine();
			out.write("TechBlockIDs=23,27,28,29,33,55,66,69,70,72,76,77"); out.newLine();
			out.write("#Woodcutting Block IDs Sperated by a Comma ','"); out.newLine();
			out.write("WoodBlockIDs=17"); out.newLine();
			out.write("#Combat Entity Names Sperated by a Comma ',' (for pvp use PVP)"); out.newLine();
			out.write("CombatEntities=PVP,Chicken,Cow,Pig,Sheep,Squid,Enderman,ZombiePig,Wolf,CaveSpider,Creeper,Ghast,Giant,Silverfish,Skeleton,Slime,Spider,Zombie"); out.newLine();
			out.write("#Maximum Allowed Level (up to 100)"); out.newLine();
			out.write("MaxLevel=100"); out.newLine();
			out.write("#Blocks that require a PickAxe for EXP Gain#"); out.newLine();
			out.write("RPA=1,4,13,14,15,16,21,24,48,49,56,73,74,87,89,98,112,121"); out.newLine();
			out.write("#Blocks that require an Axe for EXP Gain#"); out.newLine();
			out.write("RA=17"); out.newLine();
			out.write("#Blocks that require a Shovel for EXP Gain#"); out.newLine();
			out.write("RS=2,3,12,13,82"); out.newLine();
			out.write("#MySQL Settings#"); out.newLine();
			out.write("Use-MySQL="+MySQL); out.newLine();
			out.write("Use-CanaryMySQLConn="+CMySQL); out.newLine();
			out.write("UserName="+UserName); out.newLine();
			out.write("Password="+PassWord); out.newLine();
			out.write("DataBase="+DataBase); out.newLine();
			out.write("Driver="+Driver); out.newLine();
			out.write("#Save Delay in Minutes#"); out.newLine();
			out.write("SaveDelay="+savedelay); out.newLine();
			out.write("#Bonus Percent Incrimental Rate (in levels)#"); out.newLine();
			out.write("BPILR="+BPILR); out.newLine();
			out.write("#Bonuses Start At Level#"); out.newLine();
			out.write("B1LVL="+B1LVL); out.newLine();
			out.write("B2LVL="+B2LVL); out.newLine();
			out.write("B3LVL="+B3LVL); out.newLine();
			out.write("B4LVL="+B4LVL); out.newLine();
			out.write("B5LVL="+B5LVL); out.newLine();
			out.write("#Bonus Starting Percent#"); out.newLine();
			out.write("B1PS="+B1PS); out.newLine();
			out.write("B2PS="+B2PS); out.newLine();
			out.write("B3PS="+B3PS); out.newLine();
			out.write("B4PS="+B4PS); out.newLine();
			out.write("B5PS="+B5PS); out.newLine();
			out.write("#Bonus Percent Increase Incrimenter (amount of extra chance per BPILR)#"); out.newLine();
			out.write("B1PI="+B1PI); out.newLine();
			out.write("B2PI="+B2PI); out.newLine();
			out.write("B3PI="+B3PI); out.newLine();
			out.write("B4PI="+B4PI); out.newLine();
			out.write("B5PI="+B5PI); out.newLine();
			out.close();
		}catch (IOException e){
			log.severe("[CraftingReloaded] - Unable to Create Settings File!");
		}
	}
	
	private void makeCETFile(){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Dir+CETF));
			out.write("#CraftingReloaded Comabat EXP Table File#"); out.newLine();
			out.write("#Format = MobName=EXP for PVP use PVP=EXP"); out.newLine();
			out.write("Chicken=1"); out.newLine();
			out.write("Cow=1"); out.newLine();
			out.write("Pig=1"); out.newLine();
			out.write("Sheep=1"); out.newLine();
			out.write("Squid=1"); out.newLine();
			out.write("Enderman=2"); out.newLine();
			out.write("ZombiePig=2"); out.newLine();
			out.write("Wolf=2"); out.newLine();
			out.write("CaveSpider=3"); out.newLine();
			out.write("Creeper=2"); out.newLine();
			out.write("Ghast=2"); out.newLine();
			out.write("Giant=3"); out.newLine();
			out.write("Silverfish=2"); out.newLine();
			out.write("Skeleton=2"); out.newLine();
			out.write("Slime=3"); out.newLine();
			out.write("Spider=2"); out.newLine();
			out.write("Zombie=2"); out.newLine();
			out.close();
		}catch (IOException e){
			log.severe("[CraftingReloaded] - Unable to Create CombatEXPTable File!");
		}
	}
	
	private void makeBETFile(){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Dir+BETF));
			out.write("#CraftingReloaded Block EXP Table File#"); out.newLine();
			out.write("#Format is ID=EXP"); out.newLine();
			out.write("1=1"); out.newLine();
			out.write("2=1"); out.newLine();
			out.write("3=1"); out.newLine();
			out.write("4=1"); out.newLine();
			out.write("5=1"); out.newLine();
			out.write("12=1"); out.newLine();
			out.write("13=1"); out.newLine();
			out.write("14=1"); out.newLine();
			out.write("15=1"); out.newLine();
			out.write("16=1"); out.newLine();
			out.write("17=1"); out.newLine();
			out.write("18=1"); out.newLine();
			out.write("19=1"); out.newLine();
			out.write("20=1"); out.newLine();
			out.write("21=1"); out.newLine();
			out.write("22=1"); out.newLine();
			out.write("23=1"); out.newLine();
			out.write("24=1"); out.newLine();
			out.write("25=1"); out.newLine();
			out.write("26=1"); out.newLine();
			out.write("27=1"); out.newLine();
			out.write("28=1"); out.newLine();
			out.write("29=1"); out.newLine();
			out.write("30=1"); out.newLine();
			out.write("31=1"); out.newLine();
			out.write("33=1"); out.newLine();
			out.write("35=1"); out.newLine();
			out.write("37=1"); out.newLine();
			out.write("38=1"); out.newLine();
			out.write("39=1"); out.newLine();
			out.write("40=1"); out.newLine();
			out.write("41=1"); out.newLine();
			out.write("42=1"); out.newLine();
			out.write("44=1"); out.newLine();
			out.write("45=1"); out.newLine();
			out.write("47=1"); out.newLine();
			out.write("48=1"); out.newLine();
			out.write("49=1"); out.newLine();
			out.write("53=1"); out.newLine();
			out.write("54=1"); out.newLine();
			out.write("55=1"); out.newLine();
			out.write("56=1"); out.newLine();
			out.write("57=1"); out.newLine();
			out.write("59=1"); out.newLine();
			out.write("64=1"); out.newLine();
			out.write("65=1"); out.newLine();
			out.write("66=1"); out.newLine();
			out.write("67=1"); out.newLine();
			out.write("69=1"); out.newLine();
			out.write("70=1"); out.newLine();
			out.write("71=1"); out.newLine();
			out.write("72=1"); out.newLine();
			out.write("73=1"); out.newLine();
			out.write("74=1"); out.newLine();
			out.write("76=1"); out.newLine();
			out.write("77=1"); out.newLine();
			out.write("78=1"); out.newLine();
			out.write("79=1"); out.newLine();
			out.write("80=1"); out.newLine();
			out.write("81=1"); out.newLine();
			out.write("82=1"); out.newLine();
			out.write("83=1"); out.newLine();
			out.write("84=1"); out.newLine();
			out.write("85=1"); out.newLine();
			out.write("86=1"); out.newLine();
			out.write("87=1"); out.newLine();
			out.write("88=1"); out.newLine();
			out.write("89=1"); out.newLine();
			out.write("91=1"); out.newLine();
			out.write("93=1"); out.newLine();
			out.write("96=1"); out.newLine();
			out.write("98=1"); out.newLine();
			out.write("99=1"); out.newLine();
			out.write("100=1"); out.newLine();
			out.write("101=1"); out.newLine();
			out.write("102=1"); out.newLine();
			out.write("103=1"); out.newLine();
			out.write("104=1"); out.newLine();
			out.write("105=1"); out.newLine();
			out.write("106=1"); out.newLine();
			out.write("107=1"); out.newLine();
			out.write("108=1"); out.newLine();
			out.write("109=1"); out.newLine();
			out.write("112=1"); out.newLine();
			out.write("113=1"); out.newLine();
			out.write("114=1"); out.newLine();
			out.write("121=1"); out.newLine();
			out.close();
		}catch (IOException e){
			log.severe("[CraftingReloaded] - Unable to Create BlockEXPTable File!");
		}
	}
	
	private void populateEXPTable(){
		int xp;
		for(int i = 1; i < maxlevel; i++){
			xp = xploadednull("Building", i, ETP.getInt("Building"+i));
			building[i] = xp;
			xp = xploadednull("Combat", i, ETP.getInt("Combat"+i));
			combat[i] = xp;
			xp = xploadednull("Excavating", i, ETP.getInt("Excavating"+i));
			excavating[i] = xp;
			xploadednull("farming", i, ETP.getInt("Farming"+i));
			farming[i] = xp;
			xploadednull("Mining", i, ETP.getInt("Mining"+i));
			mining[i] = xp;
			xploadednull("Technician", i, ETP.getInt("Technician"+i));
			technician[i] = xp;
			xploadednull("WoodCutting", i, ETP.getInt("WoodCutting"+i));
			woodcutting[i] = xp;
		}
	}
	
	private int xploadednull(String Skill, int lvl, int xp){
		int newxp = xp;
		if(xp <= 0){
			log.warning("[CraftingReloaded] Loaded 0 for XP To: "+Skill+lvl);
			newxp = 0;
		}
		return newxp;
	}
	
	private void populateBuildBlocks(){
		String build = Settings.getString("BuildBlockIDs");
		String[] buildsplit = build.split(",");
		int block;
		for(int i = 0; i < buildsplit.length; i++){
			try{
				block = Integer.parseInt(buildsplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with BuildBlockID "+buildsplit[i]+" at split '"+i+"'");
				continue;
			}
			BuildBlocks.add(block);
		}
	}
	
	private void populateExcBlocks(){
		String exc = Settings.getString("ExcBlockIDs");
		String[] excsplit = exc.split(",");
		int block;
		for(int i = 0; i < excsplit.length; i++){
			try{
				block = Integer.parseInt(excsplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with ExcBlockID:'"+excsplit[i]+"' at split '"+i+"'");
				continue;
			}
			ExcBlocks.add(block);
		}
	}
	
	private void populateFarmBlocks(){
		String farm = Settings.getString("FarmBlockIDs");
		String[] farmsplit = farm.split(",");
		int block;
		for(int i = 0; i < farmsplit.length; i++){
			try{
				block = Integer.parseInt(farmsplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with FarmBlockID:'"+farmsplit[i]+"' at split '"+i+"'");
				continue;
			}
			FarmBlocks.add(block);
		}
	}
	
	private void populateMineBlocks(){
		String mine = Settings.getString("MineBlockIDs");
		String[] minesplit = mine.split(",");
		int block;
		for(int i = 0; i < minesplit.length; i++){
			try{
				block = Integer.parseInt(minesplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with MineBlockID:'"+minesplit[i]+"' at split '"+i+"'");
				continue;
			}
			MineBlocks.add(block);
		}
	}
	
	private void populateTechBlocks(){
		String tech = Settings.getString("TechBlockIDs");
		String[] techsplit = tech.split(",");
		int block;
		for(int i = 0; i < techsplit.length; i++){
			try{
				block = Integer.parseInt(techsplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with TechBlockID:'"+techsplit[i]+"' at split '"+i+"'");
				continue;
			}
			TechBlocks.add(block);
		}
	}
	
	private void populateWoodBlocks(){
		String wood = Settings.getString("WoodBlockIDs");
		String[] woodsplit = wood.split(",");
		int block;
		for(int i = 0; i < woodsplit.length; i++){
			try{
				block = Integer.parseInt(woodsplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with WoodBlockID:'"+woodsplit[i]+"' at split '"+i+"'");
				continue;
			}
			WoodBlocks.add(block);
		}
	}
	
	private void populateRequirePickAxe(){
		String PA = Settings.getString("RPA");
		String[] PAsplit = PA.split(",");
		int block;
		for(int i = 0; i < PAsplit.length; i++){
			try{
				block = Integer.parseInt(PAsplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with RPA BlockID:'"+PAsplit[i]+"' at split '"+i+"'");
				continue;
			}
			RequirePickAxe.add(block);
		}
	}
	
	private void populateRequireAxe(){
		String A = Settings.getString("RA");
		String[] Asplit = A.split(",");
		int block;
		for(int i = 0; i < Asplit.length; i++){
			try{
				block = Integer.parseInt(Asplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with RA BlockID:'"+Asplit[i]+"' at split '"+i+"'");
				continue;
			}
			RequireAxe.add(block);
		}
	}
	
	private void populateRequireShovel(){
		String S = Settings.getString("RS");
		String[] Ssplit = S.split(",");
		int block;
		for(int i = 0; i < Ssplit.length; i++){
			try{
				block = Integer.parseInt(Ssplit[i]);
			}catch (NumberFormatException nfe){
				log.severe("[CraftingReloaded] - Issue with RA BlockID:'"+Ssplit[i]+"' at split '"+i+"'");
				continue;
			}
			RequireShovel.add(block);
		}
	}
	
	private void populateAntiBlockFarmData(){
		if(MySQL){
			Connection conn = getMySQLConn();
			PreparedStatement ps = null;
			try{
				ps = conn.prepareStatement("SELECT * FROM CRAntiBlockFarm");
				ResultSet rs = ps.executeQuery();
    			while(rs.next()){
    				String[] blockloc = rs.getString("Block").split(",");
    				Block block = new Block();
    				block.setType(Integer.valueOf(blockloc[0]));
    				block.setX(Integer.valueOf(blockloc[1]));
    				block.setY(Integer.valueOf(blockloc[2]));
    				block.setZ(Integer.valueOf(blockloc[3]));
    				AntiBlockFarm.add(block);
    			}
			} catch (SQLException ex) {
				log.severe("[CraftingReloaded] - Unable to load AntiBlockFarm data!");
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException sqle) {
					log.severe("[CraftingReloaded] - Could not close connection to SQL");
				}
			}
			
		}
		else{
			try{
				Scanner scanner = new Scanner(new File(Dir+ABFF));
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(!line.contains("#")){
						String[] blockloc = line.split(",");
	    				Block block = new Block();
	    				block.setType(Integer.valueOf(blockloc[0]));
	    				block.setX(Integer.valueOf(blockloc[1]));
	    				block.setY(Integer.valueOf(blockloc[2]));
	    				block.setZ(Integer.valueOf(blockloc[3]));
						AntiBlockFarm.add(block);
					}
				}
				scanner.close();
			} catch (Exception e) {
				log.severe("Exception while reading AntiBlockFarm File!");
			}
		}
	}
	
	private void setSettings(){
		File setfile = new File(Dir+Set);
		if(!setfile.exists()){
			makeSettingsFile();
		}
		Settings = new PropertiesFile(Dir+Set);
		try{
			maxlevel = Settings.getInt("MaxLevel") + 1;
		}catch(Exception e){
			maxlevel = 2000000001;
		}
		if(maxlevel <= 0){
			log.warning("[CraftingReloaded] MaxLevel was 0 or less! Defaulting to 100!");
			maxlevel = 101;
		}
		UserName = Settings.getString("UserName");
		PassWord = Settings.getString("Password");
		DataBase = Settings.getString("DataBase");
		Driver = Settings.getString("Driver");
		MySQL = Settings.getBoolean("Use-MySQL");
		CMySQL = Settings.getBoolean("Use-CanaryMySQLConn");
		savedelay = Settings.getInt("SaveDelay");
		BPILR = Settings.getInt("BPILR");
		B1LVL = Settings.getInt("B1LVL");
		B2LVL = Settings.getInt("B2LVL");
		B3LVL = Settings.getInt("B3LVL");
		B4LVL = Settings.getInt("B4LVL");
		B5LVL = Settings.getInt("B5LVL");
		B1PS = Settings.getDouble("B1PS");
		B2PS = Settings.getDouble("B2PS");
		B3PS = Settings.getDouble("B3PS");
		B4PS = Settings.getDouble("B4PS");
		B5PS = Settings.getDouble("B5PS");
		B1PI = Settings.getDouble("B1PI");
		B2PI = Settings.getDouble("B2PI");
		B3PI = Settings.getDouble("B3PI");
		B4PI = Settings.getDouble("B4PI");
		B5PI = Settings.getDouble("B5PI");
		
		building = new int[(maxlevel+1)];
		combat = new int[(maxlevel+1)];
		excavating = new int[(maxlevel+1)];
		farming = new int[(maxlevel+1)];
		mining = new int[(maxlevel+1)];
		technician = new int[(maxlevel+1)];
		woodcutting = new int[(maxlevel+1)];
		
		if(MySQL && !CMySQL){
			try {
				Class.forName(Driver);
			}catch (ClassNotFoundException cnfe) {
				log.severe("[CraftingReloaded] - Unable to find driver class: " + Driver);
				log.severe("[CraftingReloaded] - Disabling SQL");
				MySQL = false;
			}
		}
		if(MySQL){
			CreateTables();
		}
	}
	
	private void populateBET(){
		try {
			int i = 1;
		    BufferedReader in = new BufferedReader(new FileReader(Dir+BETF));
		    String line;
		    while ((line = in.readLine()) != null){
		    	if (!line.contains("#")){
		    		String[] IE = line.split("=");
		    		int ID = 0, EXP = 0;
		    		try{
		    			ID = Integer.parseInt(IE[0]);
		    			EXP = Integer.parseInt(IE[1]);
		    		}catch(NumberFormatException nfe){
		    			log.info("[CraftingReloaded] - There was an issue with BlockEXPTable.txt at line:"+String.valueOf(i));
		    			i++;
		    			continue;
		    		}
		    		BET.put(ID, EXP);
		    	}
		    	else{
		    		i++;
		    	}
		    }
		    in.close();
		} catch (IOException e) {
			log.severe("[CraftingReloaded] - Unable to load CRBlockEXPTable.txt");
		}
	}
	
	private void populateCET(){
		try {
			int i = 1;
		    BufferedReader in = new BufferedReader(new FileReader(Dir+CETF));
		    String line;
		    while ((line = in.readLine()) != null){
		    	if (!line.contains("#")){
		    		String[] EE = line.split("=");
		    		int EXP = 0;
		    		try{
		    			EXP = Integer.parseInt(EE[1]);
		    		}catch(NumberFormatException nfe){
		    			log.info("[CraftingReloaded] - There was an issue with CombatEXPTable.txt at line:"+String.valueOf(i));
		    			continue;
		    		}
		    		CET.put(EE[0], EXP);
		    	}
		    	else{
		    		i++;
		    	}
		    }
		    in.close();
		} catch (IOException e) {
			log.severe("[CraftingReloaded] - Unable to load CRCombatEXPTable.txt");
		}
	}
	
	private void populateCE(){
		String CENT = Settings.getString("CombatEntities");
		String[] CES = CENT.split(",");
		for(int i = 0; i < CES.length; i++){
			CombatEntities.add(CES[i]);
		}
	}
	
	private void CreateTables(){
		String T1 = ("CREATE TABLE IF NOT EXISTS `CraftingReloaded` (`Player` varchar(32) NOT NULL, `BEXP` varchar(32) NOT NULL, `BLVL` varchar(32) NOT NULL, `CEXP` varchar(32) NOT NULL,`CLVL` varchar(32) NOT NULL, `EEXP` varchar(32) NOT NULL,`ELVL` varchar(32) NOT NULL, `FEXP` varchar(32) NOT NULL,`FLVL` varchar(32) NOT NULL, `MEXP` varchar(32) NOT NULL,`MLVL` varchar(32) NOT NULL, `TEXP` varchar(32) NOT NULL,`TLVL` varchar(32) NOT NULL, `WEXP` varchar(32) NOT NULL,`WLVL` varchar(32) NOT NULL, PRIMARY KEY (`Player`))");
		String T2 = ("CREATE TABLE IF NOT EXISTS `CRAntiBlockFarm` (`id` int(255) NOT NULL AUTO_INCREMENT, `Block` text NOT NULL, PRIMARY KEY (`id`))");
		Connection conn = getMySQLConn();
		Statement st = null;
		try{
			st = conn.createStatement();
			st.addBatch(T1);
			st.addBatch(T2);
			st.executeBatch();
		}catch (SQLException sqle) {
			log.log(Level.SEVERE, "[CraftingReloaded] MySQL exception at CRData.CreateTable() ", sqle);
		}
		finally{
			try {
				if(st != null && !st.isClosed()){
					st.close();
				}
				if(conn != null && !conn.isClosed()){
					conn.close();
				}
			} catch (SQLException SQLE) {
			}
		}
	}
	
	public boolean buildblock(int ID){
		return BuildBlocks.contains(ID);
	}
	
	public boolean excavateblock(int ID){
		return ExcBlocks.contains(ID);
	}
	
	public boolean farmblock(int ID){
		return FarmBlocks.contains(ID);
	}
	
	public boolean mineblock(int ID){
		return MineBlocks.contains(ID);
	}
	
	public boolean techblock(int ID){
		return TechBlocks.contains(ID);
	}
	
	public boolean woodblock(int ID){
		return WoodBlocks.contains(ID);
	}
	
	public boolean combatent(String name){
		return CombatEntities.contains(name);
	}
	
	public boolean RPA(int ID){
		return RequirePickAxe.contains(ID);
	}
	
	public boolean RA(int ID){
		return RequireAxe.contains(ID);
	}
	
	public boolean RS(int ID){
		return RequireShovel.contains(ID);
	}
	
	public boolean AntiFarm(Block block){
		return AntiBlockFarm.contains(block);
	}
	
	public void addAntiFarm(Block block){
		AntiBlockFarm.add(block);
	}
	
	public void removeAntiFarm(Block block){
		AntiBlockFarm.remove(block);
	}
	
	public int getBET(int ID){
		if(BET.containsKey(ID)){
			return BET.get(ID);
		}
		return 1;
	}
	
	public int getCET(String MobName){
		if(CET.containsKey(MobName)){
			return CET.get(MobName);
		}
		return 1;
	}
	
	public int getBaseExp(String type, int L){
		if(type.equals("B")){
			return building[L];
		}
		else if(type.equals("C")){
			return combat[L];
		}
		else if(type.equals("E")){
			return excavating[L];
		}
		else if(type.equals("F")){
			return farming[L];
		}
		else if(type.equals("M")){
			return mining[L];
		}
		else if(type.equals("T")){
			return technician[L];
		}
		else if(type.equals("W")){
			return woodcutting[L];
		}
		return 0;
	}
	
	public void addExp(String Type, Player player, int expgain){
		boolean levelup = false;
		if (Type.equals("B")){
			int[] el = PBuild.get(player.getName());
			int lvl = el[0];
			int exp = el[1] + expgain;
			int level = building[(lvl+1)];
			while ((exp >= level) && (lvl < maxlevel)){
				if(level <= 0){ break; }
				lvl ++;
				level = building[(lvl+1)];
				levelup = true;
			}
			if(levelup){
				player.sendMessage("�bLEVEL UP! �6Building Level:�e "+lvl);
			}
			el[0] = lvl;
			el[1] = exp;
			PBuild.remove(player.getName());
			PBuild.put(player.getName(), el);
			return;
		}
		else if (Type.equals("C")){
			int[] el = PComb.get(player.getName());
			int lvl = el[0];
			int exp = el[1] + expgain;
			int level = combat[(lvl+1)];
			while ((exp >= level) && (lvl < maxlevel)){
				if(level <= 0){ break; }
				lvl ++;
				level = combat[(lvl+1)];
				levelup = true;
			}
			if(levelup){
				player.sendMessage("�bLEVEL UP! �6Combat Level:�e "+lvl);
			}
			el[0] = lvl;
			el[1] = exp;
			PComb.remove(player.getName());
			PComb.put(player.getName(), el);
			return;
		}
		else if (Type.equals("E")){
			int[] el = PExc.get(player.getName());
			int lvl = el[0];
			int exp = el[1] + expgain;
			int level = excavating[(lvl+1)];
			while ((exp >= level) && (lvl < maxlevel)){
				if(level <= 0){ break; }
				lvl++;
				level = excavating[(lvl+1)];
				levelup = true;
			}
			if(levelup){
				player.sendMessage("�bLEVEL UP! �6Excavation Level:�e "+lvl);
			}
			el[0] = lvl;
			el[1] = exp;
			PExc.remove(player.getName());
			PExc.put(player.getName(), el);
			return;
		}
		else if (Type.equals("F")){
			int[] el = PFarm.get(player.getName());
			int lvl = el[0];
			int exp = el[1] + expgain;
			int level = farming[(lvl+1)];
			while ((exp >= level) && (lvl < maxlevel)){
				if(level <= 0){ break; }
				lvl++;
				level = farming[(lvl+1)];
				levelup = true;
			}
			if(levelup){
				player.sendMessage("�bLEVEL UP! �6Farming Level:�e "+lvl);
			}
			el[0] = lvl;
			el[1] = exp;
			PFarm.remove(player.getName());
			PFarm.put(player.getName(), el);
			return;
		}
		else if (Type.equals("M")){
			int[] el = PMine.get(player.getName());
			int lvl = el[0];
			int exp = el[1] + expgain;
			int level = mining[(lvl+1)];
			while ((exp >= level) && (lvl < maxlevel)){
				if(level <= 0){ break; }
				lvl ++;
				level = mining[(lvl+1)];
				log.info(String.valueOf(mining[(lvl+1)]));
				levelup = true;
			}
			if(levelup){
				player.sendMessage("�bLEVEL UP! �6Mining Level:�e "+lvl);
			}
			el[0] = lvl;
			el[1] = exp;
			PMine.remove(player.getName());
			PMine.put(player.getName(), el);
			return;
		}
		else if (Type.equals("T")){
			int[] el = PTech.get(player.getName());
			int lvl = el[0];
			int exp = el[1] + expgain;
			int level = technician[(lvl+1)];
			while ((exp >= level) && (lvl < maxlevel)){
				if(level <= 0){ break; }
				lvl ++;
				level = technician[(lvl+1)];
				levelup = true;
			}
			if(levelup){
				player.sendMessage("�bLEVEL UP! �6Technician Level:�e "+lvl);
			}
			el[0] = lvl;
			el[1] = exp;
			PTech.remove(player.getName());
			PTech.put(player.getName(), el);
			return;
		}
		else if (Type.equals("W")){
			int[] el = PWood.get(player.getName());
			int lvl = el[0];
			int exp = el[1] + expgain;
			int level = woodcutting[(lvl+1)];
			while ((exp >= level) && (lvl < maxlevel)){
				if(level <= 0){ break; }
				lvl ++;
				level = woodcutting[(lvl+1)];
				levelup = true;
			}
			if(levelup){
				player.sendMessage("�bLEVEL UP! �6Woodcutting Level:�e "+lvl);
			}
			el[0] = lvl;
			el[1] = exp;
			PWood.remove(player.getName());
			PWood.put(player.getName(), el);
			return;
		}
	}
	
	public void SilentLevel(String player, String Type){
		if (Type.equals("B")){
			int[] el = PBuild.get(player);
			int lvl = el[0];
			el[0] = lvl+1;
			PBuild.remove(player);
			PBuild.put(player, el);
			return;
		}
		else if (Type.equals("C")){
			int[] el = PComb.get(player);
			int lvl = el[0];
			el[0] = lvl+1;
			PComb.remove(player);
			PComb.put(player, el);
			return;
		}
		else if (Type.equals("E")){
			int[] el = PExc.get(player);
			int lvl = el[0];
			el[0] = lvl+1;
			PExc.remove(player);
			PExc.put(player, el);
			return;
		}
		else if (Type.equals("F")){
			int[] el = PFarm.get(player);
			int lvl = el[0];
			el[0] = lvl+1;
			PFarm.remove(player);
			PFarm.put(player, el);
			return;
		}
		else if (Type.equals("M")){
			int[] el = PMine.get(player);
			int lvl = el[0];
			el[0] = lvl+1;
			PMine.remove(player);
			PMine.put(player, el);
			return;
		}
		else if (Type.equals("T")){
			int[] el = PTech.get(player);
			int lvl = el[0];
			el[0] = lvl+1;
			PTech.remove(player);
			PTech.put(player, el);
			return;
		}
		else if (Type.equals("W")){
			int[] el = PWood.get(player);
			int lvl = el[0];
			el[0] = lvl+1;
			PWood.remove(player);
			PWood.put(player, el);
			return;
		}
	}
	
	public boolean Maxed(String player, String Type){
		int LVL = 0;
		if(Type.equals("B")){
			LVL = PBuild.get(player)[0];
		}
		else if(Type.equals("C")){
			LVL = PComb.get(player)[0];
		}
		else if(Type.equals("E")){
			LVL = PExc.get(player)[0];
		}
		else if(Type.equals("F")){
			LVL = PFarm.get(player)[0];
		}
		else if(Type.equals("M")){
			LVL = PMine.get(player)[0];
		}
		else if(Type.equals("T")){
			LVL = PTech.get(player)[0];
		}
		else if(Type.equals("W")){
			LVL = PWood.get(player)[0];
		}
		if(LVL >= maxlevel){
			return true;
		}
		return false;	
	}
	
	public boolean Bonus1(String player, String Type){
		double per = Math.random();
		int LVL = 0;
		if(Type.equals("B")){
			LVL = PBuild.get(player)[0];
		}
		else if(Type.equals("C")){
			LVL = PComb.get(player)[0];
		}
		else if(Type.equals("E")){
			LVL = PExc.get(player)[0];
		}
		else if(Type.equals("F")){
			LVL = PFarm.get(player)[0];
		}
		else if(Type.equals("M")){
			LVL = PMine.get(player)[0];
		}
		else if(Type.equals("T")){
			LVL = PTech.get(player)[0];
		}
		else if(Type.equals("W")){
			LVL = PWood.get(player)[0];
		}
		if(LVL > B1LVL){
			int BP = 0;
			while(LVL > BPILR){
				BP++;
				LVL -= BPILR;
			}
			double percent = B1PS;
			while(BP > 0){
				percent += B1PI;
				BP--;
			}
			if(percent > per){
				return true;
			}
		}
		return false;
	}
	
	public boolean Bonus2(String player, String Type){
		double per = Math.random();
		int LVL = 0;
		if(Type.equals("B")){
			LVL = PBuild.get(player)[0];
		}
		else if(Type.equals("C")){
			LVL = PComb.get(player)[0];
		}
		else if(Type.equals("E")){
			LVL = PExc.get(player)[0];
		}
		else if(Type.equals("F")){
			LVL = PFarm.get(player)[0];
		}
		else if(Type.equals("M")){
			LVL = PMine.get(player)[0];
		}
		else if(Type.equals("T")){
			LVL = PTech.get(player)[0];
		}
		else if(Type.equals("W")){
			LVL = PWood.get(player)[0];
		}
		if(LVL > B2LVL){
			int BP = 0;
			while(LVL > BPILR){
				BP++;
				LVL -= BPILR;
			}
			double percent = B2PS;
			while(BP > 0){
				percent += B2PI;
				BP--;
			}
			if(percent > per){
				return true;
			}
		}
		return false;
	}
	
	public boolean Bonus3(String player, String Type){
		double per = Math.random();
		int LVL = 0;
		if(Type.equals("B")){
			LVL = PBuild.get(player)[0];
		}
		else if(Type.equals("C")){
			LVL = PComb.get(player)[0];
		}
		else if(Type.equals("E")){
			LVL = PExc.get(player)[0];
		}
		else if(Type.equals("F")){
			LVL = PFarm.get(player)[0];
		}
		else if(Type.equals("M")){
			LVL = PMine.get(player)[0];
		}
		else if(Type.equals("T")){
			LVL = PTech.get(player)[0];
		}
		else if(Type.equals("W")){
			LVL = PWood.get(player)[0];
		}
		if(LVL > B3LVL){
			int BP = 0;
			while(LVL > BPILR){
				BP++;
				LVL -= BPILR;
			}
			double percent = B3PS;
			while(BP > 0){
				percent += B3PI;
				BP--;
			}
			if(percent > per){
				return true;
			}
		}
		return false;
	}
	
	public boolean Bonus4(String player, String Type){
		double per = Math.random();
		int LVL = 0;
		if(Type.equals("B")){
			LVL = PBuild.get(player)[0];
		}
		else if(Type.equals("C")){
			LVL = PComb.get(player)[0];
		}
		else if(Type.equals("E")){
			LVL = PExc.get(player)[0];
		}
		else if(Type.equals("F")){
			LVL = PFarm.get(player)[0];
		}
		else if(Type.equals("M")){
			LVL = PMine.get(player)[0];
		}
		else if(Type.equals("T")){
			LVL = PTech.get(player)[0];
		}
		else if(Type.equals("W")){
			LVL = PWood.get(player)[0];
		}
		if(LVL > B4LVL){
			int BP = 0;
			while(LVL > BPILR){
				BP++;
				LVL -= BPILR;
			}
			double percent = B4PS;
			while(BP > 0){
				percent += B4PI;
				BP--;
			}
			if(percent > per){
				return true;
			}
		}
		return false;
	}
	
	public boolean Bonus5(String player, String Type){
		double per = Math.random();
		int LVL = 0;
		if(Type.equals("B")){
			LVL = PBuild.get(player)[0];
		}
		else if(Type.equals("C")){
			LVL = PComb.get(player)[0];
		}
		else if(Type.equals("E")){
			LVL = PExc.get(player)[0];
		}
		else if(Type.equals("F")){
			LVL = PFarm.get(player)[0];
		}
		else if(Type.equals("M")){
			LVL = PMine.get(player)[0];
		}
		else if(Type.equals("T")){
			LVL = PTech.get(player)[0];
		}
		else if(Type.equals("W")){
			LVL = PWood.get(player)[0];
		}
		if(LVL > B5LVL){
			int BP = 0;
			while(LVL > BPILR){
				BP++;
				LVL -= BPILR;
			}
			double percent = B5PS;
			while(BP > 0){
				percent += B5PI;
				BP--;
			}
			if(percent > per){
				return true;
			}
		}
		return false;
	}
	
	public int[] TotalEL(String name){
		int[] total = new int[]{0, 0};
		int[] at = null;
		at = PBuild.get(name);
		total[0] += at[0]; total[1] += at[1];
		at = PComb.get(name);
		total[0] += at[0]; total[1] += at[1];
		at = PExc.get(name);
		total[0] += at[0]; total[1] += at[1];
		at = PFarm.get(name);
		total[0] += at[0]; total[1] += at[1];
		at = PMine.get(name);
		total[0] += at[0]; total[1] += at[1];
		at = PTech.get(name);
		total[0] += at[0]; total[1] += at[1];
		at = PWood.get(name);
		total[0] += at[0]; total[1] += at[1];
		return total;
	}
	
	public int[] Level(String name, String type){
		int[] el = null;
		if(type.equals("C")){
			el = PComb.get(name);
		}
		else if(type.equals("B")){
			el = PBuild.get(name);
		}
		else if(type.equals("E")){
			el = PExc.get(name);
		}
		else if(type.equals("F")){
			el = PFarm.get(name);
		}
		else if(type.equals("M")){
			el = PMine.get(name);
		}
		else if(type.equals("T")){
			el = PTech.get(name);
		}
		else if(type.equals("W")){
			el = PWood.get(name);
		}
		return el;
	}
	
	private Connection getMySQLConn(){
		Connection conn = null;
		if(CMySQL){
			conn = etc.getSQLConnection();
		}
		else{
			try {
				conn = DriverManager.getConnection(DataBase, UserName, PassWord);
			} catch (SQLException e) {
				log.severe("[CraftingReloaded] - Unable to set MySQL Connection");
			}
		}
		return conn;
	}
	
	public boolean keyExists(String name){
		if (MySQL){
			boolean exists = false;
			Connection conn = getMySQLConn();
    		try{
    			PreparedStatement ps = conn.prepareStatement("SELECT * FROM CraftingReloaded WHERE Player = ?");
    			ps.setString(1, name);
    			ResultSet rs = ps.executeQuery();
    			if (rs.next()){
    				exists = true;
    			}
    		} catch (SQLException ex) {
				log.severe("[CraftingReloaded] - Unable to verify existance for " + name + "!");
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException sqle) {
					log.severe("[CraftingReloaded] - Could not close connection to SQL");
				}
			}
    		return exists;
		}else{
			File PFile = new File(PDir+name+PEL);
			return PFile.exists();
		}
	}
	
	public void setInitialEXP(String name){
		if(MySQL){
			Connection conn = getMySQLConn();
    		try{
    			PreparedStatement ps = conn.prepareStatement("INSERT INTO CraftingReloaded (Player,BLVL,BEXP,CLVL,CEXP,ELVL,EEXP,FLVL,FEXP,MLVL,MEXP,TLVL,TEXP,WLVL,WEXP) Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
    			ps.setString(1, name);
    			ps.setInt(2, 0);
    			ps.setInt(3, 0);
    			ps.setInt(4, 0);
    			ps.setInt(5, 0);
    			ps.setInt(6, 0);
    			ps.setInt(7, 0);
    			ps.setInt(8, 0);
    			ps.setInt(9, 0);
    			ps.setInt(10, 0);
    			ps.setInt(11, 0);
    			ps.setInt(12, 0);
    			ps.setInt(13, 0);
    			ps.setInt(14, 0);
    			ps.setInt(15, 0);
    			ps.executeUpdate();
    		} catch (SQLException ex) {
    			log.severe("[CraftingReloaded] - Unable to save data for "+name+" to CraftingReloaded!");
    		}finally{
    			try{
    				if (conn != null){
    					conn.close();
    				}
    			}catch (SQLException sqle) {
    				log.severe("[CraftingReloaded] - Could not close connection to SQL");
        		}
    		}
		}
		else{
			PELP = new PropertiesFile(PDir+name+PEL);
			PELP.setInt("BLVL", 0); PELP.setInt("BEXP", 0);
			PELP.setInt("CLVL", 0); PELP.setInt("CEXP", 0);
			PELP.setInt("ELVL", 0); PELP.setInt("EEXP", 0);
			PELP.setInt("FLVL", 0); PELP.setInt("FEXP", 0);
			PELP.setInt("MLVL", 0); PELP.setInt("MEXP", 0);
			PELP.setInt("TLVL", 0); PELP.setInt("TEXP", 0);
			PELP.setInt("WLVL", 0); PELP.setInt("WEXP", 0);
		}
		int[] init = new int[]{0, 0};
		PBuild.put(name, init);
		PComb.put(name, init);
		PExc.put(name, init);
		PFarm.put(name, init);
		PMine.put(name, init);
		PTech.put(name, init);
		PWood.put(name, init);
	}
	
	public void ReloadOnline(){
		ArrayList<Player> pload = new ArrayList<Player>();
		pload.addAll(etc.getServer().getPlayerList());
		for(int i = 0; i < pload.size(); i++){
			Player player = pload.get(i);
			loadEXP(player.getName());
		}
	}
	
	public void loadEXP(String name){
		int blvl = 0, bexp = 0, clvl = 0, cexp = 0, elvl = 0, eexp = 0, flvl = 0, fexp = 0, mlvl = 0, mexp = 0, tlvl = 0, texp = 0, wlvl = 0, wexp = 0;
		if(MySQL){
			Connection conn = getMySQLConn();
    		try{
    			PreparedStatement ps = conn.prepareStatement("SELECT * FROM CraftingReloaded WHERE Player = ?");
    			ps.setString(1, name);
    			ResultSet rs = ps.executeQuery();
    			if(rs.next()){
    				blvl = rs.getInt("BLVL");
    				bexp = rs.getInt("BEXP");
    				clvl = rs.getInt("CLVL");
    				cexp = rs.getInt("CEXP");
    				elvl = rs.getInt("ELVL");
    				eexp = rs.getInt("EEXP");
    				flvl = rs.getInt("FLVL");
    				fexp = rs.getInt("FEXP");
    				mlvl = rs.getInt("MLVL");
    				mexp = rs.getInt("MEXP");
    				tlvl = rs.getInt("TLVL");
    				texp = rs.getInt("TEXP");
    				wlvl = rs.getInt("WLVL");
    				wexp = rs.getInt("WEXP");
    			}
    		} catch (SQLException ex) {
    			log.severe("[CraftingReloaded] - Unable to load data for "+name);
    		}finally{
    			try{
    				if (conn != null){
    					conn.close();
    				}
    			}catch (SQLException sqle) {
    				log.severe("[CraftingReloaded] - Could not close connection to SQL");
        		}
    		}
		}
		else{
			PELP = new PropertiesFile(PDir+name+PEL);
			blvl = PELP.getInt("BLVL"); bexp = PELP.getInt("BEXP");
			clvl = PELP.getInt("CLVL"); cexp = PELP.getInt("CEXP");
			elvl = PELP.getInt("ELVL"); eexp = PELP.getInt("EEXP");
			flvl = PELP.getInt("FLVL"); fexp = PELP.getInt("FEXP");
			mlvl = PELP.getInt("MLVL"); mexp = PELP.getInt("MEXP");
			tlvl = PELP.getInt("TLVL"); texp = PELP.getInt("TEXP");
			wlvl = PELP.getInt("WLVL"); wexp = PELP.getInt("WEXP");
		}
		PBuild.put(name, new int[]{blvl, bexp});
		PComb.put(name, new int[]{clvl, cexp});
		PExc.put(name, new int[]{elvl, eexp});
		PFarm.put(name, new int[]{flvl, fexp});
		PMine.put(name, new int[]{mlvl, mexp});
		PTech.put(name, new int[]{tlvl, texp});
		PWood.put(name, new int[]{wlvl, wexp});
	}
	
	public void SaveSingle(Player player){
		String name = player.getName();
		int[] B = PBuild.get(name);
		PBuild.remove(name);
		int[] C = PComb.get(name);
		PComb.remove(name);
		int[] E = PExc.get(name);
		PExc.remove(name);
		int[] F = PFarm.get(name);
		PFarm.remove(name);
		int[] M = PMine.get(name);
		PMine.remove(name);
		int[] T = PTech.get(name);
		PTech.remove(name);
		int[] W = PWood.get(name);
		PWood.remove(name);
		if(MySQL){
			Connection conn = getMySQLConn();
			PreparedStatement ps = null;
			try{
				ps = conn.prepareStatement("UPDATE CraftingReloaded SET BLVL = ?, BEXP = ?, CLVL = ?, CEXP = ?, ELVL = ?, EEXP = ?, FLVL = ?, FEXP = ?, MLVL = ?, MEXP = ?, TLVL = ?, TEXP = ?, WLVL = ?, WEXP = ? WHERE Player = ? LIMIT 1");
				ps.setInt(1, B[0]);
				ps.setInt(2, B[1]);
				ps.setInt(3, C[0]);
				ps.setInt(4, C[1]);
				ps.setInt(5, E[0]);
				ps.setInt(6, E[1]);
				ps.setInt(7, F[0]);
				ps.setInt(8, F[1]);
				ps.setInt(9, M[0]);
				ps.setInt(10, M[1]);
				ps.setInt(11, T[0]);
				ps.setInt(12, T[1]);
				ps.setInt(13, W[0]);
				ps.setInt(14, W[1]);
				ps.setString(15, name);
				ps.executeUpdate();
			} catch (SQLException ex) {
				log.severe("[CraftingReloaded] - Unable to update data for " + player + "!");
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException sqle) {
					log.severe("[CraftingReloaded] - Could not close connection to SQL");
				}
			}
		}
		else{
			PELP = new PropertiesFile(PDir+name+PEL);
			PELP.setInt("BLVL", B[0]); PELP.setInt("BEXP", B[1]);
			PELP.setInt("CLVL", C[0]); PELP.setInt("CEXP", C[1]);
			PELP.setInt("ELVL", E[0]); PELP.setInt("EEXP", E[1]);
			PELP.setInt("FLVL", F[0]); PELP.setInt("FEXP", F[1]);
			PELP.setInt("MLVL", M[0]); PELP.setInt("MEXP", M[1]);
			PELP.setInt("TLVL", T[0]); PELP.setInt("TEXP", T[1]);
			PELP.setInt("WLVL", W[0]); PELP.setInt("WEXP", W[1]);
		}
	}
	
	public void SaveAll(){
		etc.getServer().messageAll("[�5SKILLS�f]�d - Saving all LEVELS and EXPERIENCE!");
		log.info("[CraftingReloaded] - Saving all LEVELS and EXPERIENCE!");
		int[] el;
		for(String name : PBuild.keySet()){
			el = PBuild.get(name);
 			SaveItNow(name, "BLVL", "BEXP", el[0], el[1]);
		}
		for(String name : PComb.keySet()){
			el = PComb.get(name);
 			SaveItNow(name, "CLVL", "CEXP", el[0], el[1]);
		}
		for(String name : PExc.keySet()){
			el = PExc.get(name);
 			SaveItNow(name, "ELVL", "EEXP", el[0], el[1]);
		}
		for(String name : PFarm.keySet()){
			el = PFarm.get(name);
 			SaveItNow(name, "FLVL", "FEXP", el[0], el[1]);
		}
		for(String name : PMine.keySet()){
			el = PMine.get(name);
 			SaveItNow(name, "MLVL", "MEXP", el[0], el[1]);
		}
		for(String name : PTech.keySet()){
			el = PTech.get(name);
 			SaveItNow(name, "TLVL", "TEXP", el[0], el[1]);
		}
		for(String name : PWood.keySet()){
			el = PWood.get(name);
 			SaveItNow(name, "WLVL", "WEXP", el[0], el[1]);
		}
		UpdateAntiBlockFarm();
		etc.getServer().messageAll("[�5SKILLS�f]�d - Save Complete!");
		log.info("[CraftingReloaded] - Save Complete!");
	}
	
	public void SaveItNow(String name, String LVL, String EXP, int level, int exp){
		if(MySQL){
			Connection conn = getMySQLConn();
			PreparedStatement ps = null;
			try{
				ps = conn.prepareStatement("UPDATE CraftingReloaded SET "+LVL+" = ?, "+EXP+" = ? WHERE Player = ? LIMIT 1");
				ps.setInt(1, level);
				ps.setInt(2, exp);
				ps.setString(3, name);
				ps.executeUpdate();
			} catch (SQLException ex) {
				log.severe("[CraftingReloaded] - Unable to update "+LVL+" and "+EXP+" for " + name + "!");
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException sqle) {
					log.severe("[CraftingReloaded] - Could not close connection to SQL");
				}
			}
		}
		else{
			PELP = new PropertiesFile(PDir+name+PEL);
			PELP.setInt(LVL, level);
			PELP.setInt(EXP, exp);
		}
	}
	
	public void TRUNCATETABLE(){
		Connection conn = getMySQLConn();
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("TRUNCATE TABLE CRAntiBlockFarm");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException ex) {
			log.severe("[CraftingReloaded] - Unable to TRUNCATE AntiBlockFarm table!");
		}finally{
			try{
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sqle) {
				log.severe("[CraftingReloaded] - Could not close connection to SQL");
			}
		}
	}
	
	public void UpdateAntiBlockFarm(){
		CRL = CraftingReloaded.CRL;
		AntiBlockFarm.clear();
		AntiBlockFarm.addAll(CRL.AntiBlockFarm);
		if(MySQL){
			TRUNCATETABLE();
			Connection conn = getMySQLConn();
			PreparedStatement ps = null;
			try{
				for(Block block : AntiBlockFarm){
					ps = conn.prepareStatement("INSERT INTO CRAntiBlockFarm (Block) VALUES(?)");
					ps.setString(1, BTS(block));
					ps.executeUpdate();
				}
			} catch (SQLException ex) {
				log.log(Level.SEVERE,"[CraftingReloaded] - Unable to save data to AntiBlockFarm table!", ex);
			}finally{
				try{
					if (conn != null){
						conn.close();
					}
				}catch (SQLException sqle) {
					log.severe("[CraftingReloaded] - Could not close connection to SQL");
				}
			}
		}
		else{
			try {
				File ABF = new File(Dir+ABFF);
				if(!ABF.exists()){
					ABF.createNewFile();
				}
				else{
					ABF.delete();
					ABF.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(ABF));
				for(Block block : AntiBlockFarm){
					bw.write(BTS(block));
					bw.newLine();
				}
				bw.close();
			} catch (Exception ex) {
				log.severe("Exception while writing new block to " + ABFF);
			}
		}
	}
	
	public void Disabler(){
		SaveIt.cancel();
		SaveAll();
	}
	
	public class SaveTime extends TimerTask{
		public SaveTime(){}
		
		public void run(){
			SaveAll();
			UpdateAntiBlockFarm();
			if(etc.getLoader().getPlugin("CraftingReloaded").isEnabled()){
				SaveIt.schedule(new SaveTime(), savedelay*60000);
			}
		}
	}
	
	public String BTS(Block block){
		StringBuffer BlockBuf = new StringBuffer();
		BlockBuf.append(block.getType());
		BlockBuf.append(",");
		BlockBuf.append(block.getX());
		BlockBuf.append(",");
		BlockBuf.append(block.getY());
		BlockBuf.append(",");
		BlockBuf.append(block.getZ());
		BlockBuf.append(",");
		BlockBuf.append(block.getWorld().getType().getId());
		return BlockBuf.toString();
	}
}
