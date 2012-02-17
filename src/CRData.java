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
	private CraftingReloaded CR;
	
	private Logger log = Logger.getLogger("Minecraft");
	
	private HashMap<String, CRPlayerLevelExperience> PLVLXPT;
	
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

	private long[] building, combat, excavating, farming, mining, technician, woodcutting;
	int maxlevel = 100, savedelay = 15, BPILR = 5, B1LVL = 5, B2LVL = 15, B3LVL = 25, B4LVL = 35, B5LVL = 45;
	double B1PS = 0.005, B2PS = 0.005, B3PS = 0.005, B4PS = 0.005, B5PS = 0.005, B1PI = 0.005, B2PI = 0.005, B3PI = 0.005, B4PI = 0.005, B5PI = 0.005;
	
	boolean CMySQL = false, MySQL = false;
	
	public CRData(CraftingReloaded CR){
		this.CR = CR;
		PLVLXPT = new HashMap<String, CRPlayerLevelExperience>();
		
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
		makeDir();
		setSettings();
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
			long xp = 0, i;
			BufferedWriter out = new BufferedWriter(new FileWriter(Dir+EL));
			out.write("#CraftingReloaded Experience File#"); out.newLine();
			out.write("#Building EXP#"); out.newLine();
			for(i = 1; i < maxlevel; i++){
				xp = (i*150L);
				out.write("Building"+String.valueOf(i)+"="+String.valueOf(xp)); out.newLine();
			}
			out.write("#Combat EXP#"); out.newLine();
			for(i = 1; i < maxlevel; i++){
				xp = (i*150L);
				out.write("Combat"+String.valueOf(i)+"="+String.valueOf(xp)); out.newLine();
			}
			out.write("#Excavating EXP#"); out.newLine();
			for(i = 1; i < maxlevel; i++){
				xp = (i*150L);
				out.write("Excavating"+String.valueOf(i)+"="+String.valueOf(xp)); out.newLine();
			}
			out.write("#Farming EXP#"); out.newLine();
			for(i = 1; i < maxlevel; i++){
				xp = (i*150L);
				out.write("Farming"+String.valueOf(i)+"="+String.valueOf(xp)); out.newLine();
			}
			out.write("#Mining EXP#"); out.newLine();
			for(i = 1; i < maxlevel; i++){
				xp = (i*150L);
				out.write("Mining"+String.valueOf(i)+"="+String.valueOf(xp)); out.newLine();
			}
			out.write("#Technician EXP#"); out.newLine();
			for(i = 1; i < maxlevel; i++){
				xp = (i*150L);
				out.write("Technician"+String.valueOf(i)+"="+String.valueOf(xp)); out.newLine();
			}
			out.write("#WoodCutting EXP#"); out.newLine();
			for(i = 1; i < maxlevel; i++){
				xp = (i*150L);
				out.write("WoodCutting"+String.valueOf(i)+"="+String.valueOf(xp)); out.newLine();
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
		long xp;
		for(int i = 1; i < maxlevel; i++){
			xp = xploadednull("Building", i, ETP.getLong("Building"+i));
			building[i] = xp;
			xp = xploadednull("Combat", i, ETP.getLong("Combat"+i));
			combat[i] = xp;
			xp = xploadednull("Excavating", i, ETP.getLong("Excavating"+i));
			excavating[i] = xp;
			xp = xploadednull("farming", i, ETP.getLong("Farming"+i));
			farming[i] = xp;
			xp = xploadednull("Mining", i, ETP.getLong("Mining"+i));
			mining[i] = xp;
			xp = xploadednull("Technician", i, ETP.getLong("Technician"+i));
			technician[i] = xp;
			xp = xploadednull("WoodCutting", i, ETP.getLong("WoodCutting"+i));
			woodcutting[i] = xp;
		}
	}
	
	private long xploadednull(String Skill, int lvl, long xp){
		long newxp = xp;
		int oldlvl = lvl-1;
		if(xp <= 0){
			log.warning("[CraftingReloaded] Loaded 0 for XP To: "+Skill+lvl);
			log.warning("[CraftingReloaded] Reseting to 150+ last loaded XP for Skill:"+Skill);
			if(Skill.equals("Building")){
				newxp = building[oldlvl];
			}
			else if(Skill.equals("Combat")){
				newxp = combat[oldlvl];
			}
			else if(Skill.equals("Excavating")){
				newxp = excavating[oldlvl];
			}
			else if(Skill.equals("Farming")){
				newxp = farming[oldlvl];
			}
			else if(Skill.equals("Mining")){
				newxp = mining[oldlvl];
			}
			else if(Skill.equals("Technician")){
				newxp = technician[oldlvl];
			}
			else if(Skill.equals("WoodCutting")){
				newxp = woodcutting[oldlvl];
			}
			newxp += 150;
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
    				World world = etc.getServer().getWorld(Integer.valueOf(blockloc[4]));
    				Block block = new Block();
    				block.setType(Integer.valueOf(blockloc[0]));
    				block.setX(Integer.valueOf(blockloc[1]));
    				block.setY(Integer.valueOf(blockloc[2]));
    				block.setZ(Integer.valueOf(blockloc[3]));
    				block.setWorld(world);
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
						World world = etc.getServer().getWorld(Integer.valueOf(blockloc[4]));
						Block block = new Block();
	    				block.setType(Integer.valueOf(blockloc[0]));
	    				block.setX(Integer.valueOf(blockloc[1]));
	    				block.setY(Integer.valueOf(blockloc[2]));
	    				block.setZ(Integer.valueOf(blockloc[3]));
	    				block.setWorld(world);
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
			maxlevel = 1000000000;
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
		
		building = new long[(maxlevel+1)];
		combat = new long[(maxlevel+1)];
		excavating = new long[(maxlevel+1)];
		farming = new long[(maxlevel+1)];
		mining = new long[(maxlevel+1)];
		technician = new long[(maxlevel+1)];
		woodcutting = new long[(maxlevel+1)];
		
		building[0] = 0;
		combat[0] = 0;
		excavating[0] = 0;
		farming[0] = 0;
		mining[0] = 0;
		technician[0] = 0;
		woodcutting[0] = 0;
		
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
	
	public long getBaseExp(String type, int L){
		long xp = 0;
		if(L > maxlevel){
			L = maxlevel;
		}
		if(type.equals("B")){
			xp = building[L];
		}
		else if(type.equals("C")){
			xp = combat[L];
		}
		else if(type.equals("E")){
			xp = excavating[L];
		}
		else if(type.equals("F")){
			xp = farming[L];
		}
		else if(type.equals("M")){
			xp = mining[L];
		}
		else if(type.equals("T")){
			xp = technician[L];
		}
		else if(type.equals("W")){
			xp = woodcutting[L];
		}
		if(xp <= 0){
			log.warning("[CraftingReloaded] - Let DarkDiplomat know you had an error at getBaseExp(L:823) For Skill: "+type+" Level: "+L);
		}
		return xp;
	}
	
	private CRPlayerLevelExperience getPLXP(String name){
		if(!keyExists(name)){
			setInitialEXP(name);
		}
		else if(!PLVLXPT.containsKey(name)){
			loadEXP(name);
		}
		return PLVLXPT.get(name);
	}
	
	public void addExp(String Type, Player player, long expgain){
		int lvl, xpcheck;
		long exp, levelxp;
		CRPlayerLevelExperience PLXP = getPLXP(player.getName());
		if (Type.equals("B")){
			lvl = getLevel(player.getName(), Type);
			exp = getXP(player.getName(), Type) + expgain;
			xpcheck = lvl+1;
			levelxp = getBaseExp(Type, xpcheck);
			if ((exp >= levelxp) && (lvl < maxlevel)){
				String level = String.valueOf(xpcheck);
				player.sendMessage("§6BUILDING §bLEVELED UP! Level:§e "+level);
				PLXP.BAddLevel(1);
			}
			PLXP.BAddXP(expgain);
		}
		else if (Type.equals("C")){
			lvl = getLevel(player.getName(), Type);
			exp = getXP(player.getName(), Type) + expgain;
			xpcheck = lvl+1;
			levelxp = getBaseExp(Type, xpcheck);
			if ((exp >= levelxp) && (lvl < maxlevel)){
				String level = String.valueOf(xpcheck);
				player.sendMessage("§6COMBAT §bLEVELED UP! Level:§e "+level);
				PLXP.CAddLevel(1);
			}
			PLXP.CAddXP(expgain);
		}
		else if (Type.equals("E")){
			lvl = getLevel(player.getName(), Type);
			exp = getXP(player.getName(), Type) + expgain;
			xpcheck = lvl+1;
			levelxp = getBaseExp(Type, xpcheck);
			if ((exp >= levelxp) && (lvl < maxlevel)){
				String level = String.valueOf(xpcheck);
				player.sendMessage("§6EXCAVATING §bLEVELED UP! Level:§e "+level);
				PLXP.EAddLevel(1);
			}
			PLXP.EAddXP(expgain);
		}
		else if (Type.equals("F")){
			lvl = getLevel(player.getName(), Type);
			exp = getXP(player.getName(), Type) + expgain;
			xpcheck = lvl+1;
			levelxp = getBaseExp(Type, xpcheck);
			if ((exp >= levelxp) && (lvl < maxlevel)){
				String level = String.valueOf(xpcheck);
				player.sendMessage("§6FARMING §bLEVELED UP! Level:§e "+level);
				PLXP.FAddLevel(1);
			}
			PLXP.FAddXP(expgain);
		}
		else if (Type.equals("M")){
			lvl = getLevel(player.getName(), Type);
			exp = getXP(player.getName(), Type) + expgain;
			xpcheck = lvl+1;
			levelxp = getBaseExp(Type, xpcheck);
			if ((exp >= levelxp) && (lvl < maxlevel)){
				String level = String.valueOf(xpcheck);
				player.sendMessage("§6MINING §bLEVELED UP! Level:§e "+level);
				PLXP.MAddLevel(1);
			}
			PLXP.MAddXP(expgain);
		}
		else if (Type.equals("T")){
			lvl = getLevel(player.getName(), Type);
			exp = getXP(player.getName(), Type) + expgain;
			xpcheck = lvl+1;
			levelxp = getBaseExp(Type, xpcheck);
			if ((exp >= levelxp) && (lvl < maxlevel)){
				String level = String.valueOf(xpcheck);
				player.sendMessage("§6TECHNICIAN §bLEVELED UP! Level:§e "+level);
				PLXP.TAddLevel(1);
			}
			PLXP.TAddXP(expgain);
		}
		else if (Type.equals("W")){
			lvl = getLevel(player.getName(), Type);
			exp = getXP(player.getName(), Type) + expgain;
			xpcheck = lvl+1;
			levelxp = getBaseExp(Type, xpcheck);
			if ((exp >= levelxp) && (lvl < maxlevel)){
				String level = String.valueOf(xpcheck);
				player.sendMessage("§6WOODCUTTING §bLEVELED UP! Level:§e "+level);
				PLXP.WAddLevel(1);
			}
			PLXP.WAddXP(expgain);
		}
		PLVLXPT.put(player.getName(), PLXP);
	}
	
	public boolean Maxed(String player, String Type){
		if(Type.equals("B")){
			return getLevel(player, Type) >= maxlevel;
		}
		else if(Type.equals("C")){
			return getLevel(player, Type) >= maxlevel;
		}
		else if(Type.equals("E")){
			return getLevel(player, Type) >= maxlevel;
		}
		else if(Type.equals("F")){
			return getLevel(player, Type) >= maxlevel;
		}
		else if(Type.equals("M")){
			return getLevel(player, Type) >= maxlevel;
		}
		else if(Type.equals("T")){
			return getLevel(player, Type) >= maxlevel;
		}
		else if(Type.equals("W")){
			return getLevel(player, Type) >= maxlevel;
		}
		return false;	
	}
	
	public int getLevel(String player, String Type){
		int LVL = 0;
		CRPlayerLevelExperience PLXP = getPLXP(player);
		if(Type.equals("B")){
			LVL = resetlvl(PLXP.getBXP(), "B");
		}
		else if(Type.equals("C")){
			LVL = resetlvl(PLXP.getCXP(), "C");
		}
		else if(Type.equals("E")){
			LVL = resetlvl(PLXP.getEXP(), "E");
		}
		else if(Type.equals("F")){
			LVL = resetlvl(PLXP.getFXP(), "F");
		}
		else if(Type.equals("M")){
			LVL = resetlvl(PLXP.getMXP(), "M");
		}
		else if(Type.equals("T")){
			LVL = resetlvl(PLXP.getTXP(), "T");
		}
		else if(Type.equals("W")){
			LVL = resetlvl(PLXP.getWXP(), "W");
		}
		return LVL;
	}
	
	public long getXP(String player, String Type){
		long XP = 0;
		CRPlayerLevelExperience PLXP = getPLXP(player);
		if(Type.equals("B")){
			XP = PLXP.getBXP();
		}
		else if(Type.equals("C")){
			XP = PLXP.getCXP();
		}
		else if(Type.equals("E")){
			XP = PLXP.getEXP();
		}
		else if(Type.equals("F")){
			XP = PLXP.getFXP();
		}
		else if(Type.equals("M")){
			XP = PLXP.getMXP();
		}
		else if(Type.equals("T")){
			XP = PLXP.getTXP();
		}
		else if(Type.equals("W")){
			XP = PLXP.getWXP();
		}
		return XP;
	}
	
	public int TotalLevel(String name){
		int total = 0;
		total += getLevel(name, "B");
		total += getLevel(name, "C");
		total += getLevel(name, "E");
		total += getLevel(name, "F");
		total += getLevel(name, "M");
		total += getLevel(name, "T");
		total += getLevel(name, "W");
		return total;
	}
	
	public long TotalXP(String name){
		long total = 0;
		total += getXP(name, "B");
		total += getXP(name, "C");
		total += getXP(name, "E");
		total += getXP(name, "F");
		total += getXP(name, "M");
		total += getXP(name, "T");
		total += getXP(name, "W");
		return total;
	}
	
	public boolean Bonus1(String player, String Type){
		double per = Math.random();
		int LVL = getLevel(player, Type);
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
		int LVL = getLevel(player, Type);
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
		int LVL = getLevel(player, Type);
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
		int LVL = getLevel(player, Type);
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
		int LVL = getLevel(player, Type);
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
	
	private int resetlvl(long xp, String skill){
		int newlvl = 0;
		long rxp = getBaseExp(skill, (newlvl+1));
		while ((xp >= rxp) && (newlvl < maxlevel)){
			newlvl++;
			rxp = getBaseExp(skill, (newlvl+1));
		}
		return newlvl;
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
					if (conn != null && !conn.isClosed()){
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
			if (conn != null){
				try{
					PreparedStatement ps = conn.prepareStatement("INSERT INTO CraftingReloaded (Player,BLVL,BEXP,CLVL,CEXP,ELVL,EEXP,FLVL,FEXP,MLVL,MEXP,TLVL,TEXP,WLVL,WEXP) Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					ps.setString(1, name);
					ps.setInt(2, 0);
					ps.setLong(3, 0);
					ps.setInt(4, 0);
					ps.setLong(5, 0);
					ps.setInt(6, 0);
					ps.setLong(7, 0);
					ps.setInt(8, 0);
					ps.setLong(9, 0);
					ps.setInt(10, 0);
					ps.setLong(11, 0);
					ps.setInt(12, 0);
					ps.setLong(13, 0);
					ps.setInt(14, 0);
					ps.setLong(15, 0);
					ps.executeUpdate();
				} catch (SQLException ex) {
					log.severe("[CraftingReloaded] - Unable to save data for "+name+" to CraftingReloaded!");
				}finally{
					try{
						if (conn != null && !conn.isClosed()){
							conn.close();
						}
					}catch (SQLException sqle) {
						log.severe("[CraftingReloaded] - Could not close connection to SQL");
		    		}
				}
			}
		}
		else{
			PELP = new PropertiesFile(PDir+name+PEL);
			PELP.setInt("BLVL", 0); PELP.setLong("BEXP", 0);
			PELP.setInt("CLVL", 0); PELP.setLong("CEXP", 0);
			PELP.setInt("ELVL", 0); PELP.setLong("EEXP", 0);
			PELP.setInt("FLVL", 0); PELP.setLong("FEXP", 0);
			PELP.setInt("MLVL", 0); PELP.setLong("MEXP", 0);
			PELP.setInt("TLVL", 0); PELP.setLong("TEXP", 0);
			PELP.setInt("WLVL", 0); PELP.setLong("WEXP", 0);
		}
		CRPlayerLevelExperience PLXP = new CRPlayerLevelExperience(name);
		PLVLXPT.put(name, PLXP);
	}
	
	public void ReloadOnline(){
		ArrayList<Player> pload = new ArrayList<Player>();
		pload.addAll(etc.getServer().getPlayerList());
		for(Player player : pload){
			if(player.canUseCommand("/skills")){
				if(keyExists(player.getName())){
					loadEXP(player.getName());
				}
				else{
					setInitialEXP(player.getName());
				}
			}
		}
	}
	
	public void loadEXP(String name){
		CRPlayerLevelExperience PLXP;
		int blvl = 0, clvl = 0, elvl = 0, flvl = 0, mlvl = 0, tlvl = 0, wlvl = 0;
		long bexp = 0, cexp = 0, eexp = 0, fexp = 0,  mexp = 0, texp = 0, wexp = 0;
		if(MySQL){
			Connection conn = getMySQLConn();
			if(conn != null){
	    		try{
	    			PreparedStatement ps = conn.prepareStatement("SELECT * FROM CraftingReloaded WHERE Player = ?");
	    			ps.setString(1, name);
	    			ResultSet rs = ps.executeQuery();
	    			if(rs.next()){
	    				blvl = rs.getInt("BLVL");
	    				bexp = rs.getLong("BEXP");
	    				clvl = rs.getInt("CLVL");
	    				cexp = rs.getLong("CEXP");
	    				elvl = rs.getInt("ELVL");
	    				eexp = rs.getLong("EEXP");
	    				flvl = rs.getInt("FLVL");
	    				fexp = rs.getLong("FEXP");
	    				mlvl = rs.getInt("MLVL");
	    				mexp = rs.getLong("MEXP");
	    				tlvl = rs.getInt("TLVL");
	    				texp = rs.getLong("TEXP");
	    				wlvl = rs.getInt("WLVL");
	    				wexp = rs.getLong("WEXP");
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
		}
		else{
			PELP = new PropertiesFile(PDir+name+PEL);
			blvl = PELP.getInt("BLVL"); bexp = PELP.getLong("BEXP");
			clvl = PELP.getInt("CLVL"); cexp = PELP.getLong("CEXP");
			elvl = PELP.getInt("ELVL"); eexp = PELP.getLong("EEXP");
			flvl = PELP.getInt("FLVL"); fexp = PELP.getLong("FEXP");
			mlvl = PELP.getInt("MLVL"); mexp = PELP.getLong("MEXP");
			tlvl = PELP.getInt("TLVL"); texp = PELP.getLong("TEXP");
			wlvl = PELP.getInt("WLVL"); wexp = PELP.getLong("WEXP");
		}
		
		PLXP = new CRPlayerLevelExperience(name);
		PLXP.BAddLevel(blvl); PLXP.BAddXP(bexp);
		PLXP.CAddLevel(clvl); PLXP.CAddXP(cexp);
		PLXP.EAddLevel(elvl); PLXP.EAddXP(eexp);
		PLXP.FAddLevel(flvl); PLXP.FAddXP(fexp);
		PLXP.MAddLevel(mlvl); PLXP.MAddXP(mexp);
		PLXP.TAddLevel(tlvl); PLXP.TAddXP(texp);
		PLXP.WAddLevel(wlvl); PLXP.WAddXP(wexp);
		PLVLXPT.put(name, PLXP);
	}
	
	public void SaveSingle(String player){
		CRPlayerLevelExperience PLXP = getPLXP(player);
		PLVLXPT.remove(player);
		int blvl = PLXP.getBLVL(),
			clvl = PLXP.getCLVL(),
			elvl = PLXP.getELVL(), 
			flvl = PLXP.getFLVL(), 
			mlvl = PLXP.getMLVL(), 
			tlvl = PLXP.getTLVL(), 
			wlvl = PLXP.getWLVL();
		
		long bexp = PLXP.getBXP(),
			 cexp = PLXP.getCXP(),
			 eexp = PLXP.getEXP(),
			 fexp = PLXP.getFXP(),
			 mexp = PLXP.getMXP(),
			 texp = PLXP.getTXP(),
			 wexp = PLXP.getWXP();
		
		if(MySQL){
			Connection conn = getMySQLConn();
			PreparedStatement ps = null;
			if(conn != null){
				try{
					ps = conn.prepareStatement("UPDATE CraftingReloaded SET BLVL = ?, BEXP = ?, CLVL = ?, CEXP = ?, ELVL = ?, EEXP = ?, FLVL = ?, FEXP = ?, MLVL = ?, MEXP = ?, TLVL = ?, TEXP = ?, WLVL = ?, WEXP = ? WHERE Player = ?");
					ps.setInt(1, blvl);
					ps.setLong(2, bexp);
					ps.setInt(3, clvl);
					ps.setLong(4, cexp);
					ps.setInt(5, elvl);
					ps.setLong(6, eexp);
					ps.setInt(7, flvl);
					ps.setLong(8, fexp);
					ps.setInt(9, mlvl);
					ps.setLong(10, mexp);
					ps.setInt(11, tlvl);
					ps.setLong(12, texp);
					ps.setInt(13, wlvl);
					ps.setLong(14, wexp);
					ps.setString(15, player);
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
		}
		else{
			PELP = new PropertiesFile(PDir+player+PEL);
			PELP.setInt("BLVL", blvl); PELP.setLong("BEXP", bexp);
			PELP.setInt("CLVL", clvl); PELP.setLong("CEXP", cexp);
			PELP.setInt("ELVL", elvl); PELP.setLong("EEXP", eexp);
			PELP.setInt("FLVL", flvl); PELP.setLong("FEXP", fexp);
			PELP.setInt("MLVL", mlvl); PELP.setLong("MEXP", mexp);
			PELP.setInt("TLVL", tlvl); PELP.setLong("TEXP", texp);
			PELP.setInt("WLVL", wlvl); PELP.setLong("WEXP", wexp);
		}
	}
	
	public void SaveAll(){
		etc.getServer().messageAll("[§5SKILLS§f]§d - Saving all LEVELS and EXPERIENCE!");
		log.info("[CraftingReloaded] - Saving all LEVELS and EXPERIENCE!");
		CRPlayerLevelExperience PLXP;
			if(MySQL){
				Connection conn = getMySQLConn();
				PreparedStatement ps = null;
				if(conn != null){
					try{
						ps = conn.prepareStatement("UPDATE CraftingReloaded SET BLVL = ?, BEXP = ?, CLVL = ?, CEXP = ?, ELVL = ?, EEXP = ?, FLVL = ?, FEXP = ?, MLVL = ?, MEXP = ?, TLVL = ?, TEXP = ?, WLVL = ?, WEXP = ? WHERE Player = ?");
						for(String key : PLVLXPT.keySet()){
							PLXP = PLVLXPT.get(key);
							
							ps.setInt(1, PLXP.getBLVL());
							ps.setLong(2, PLXP.getBXP());
							ps.setInt(3, PLXP.getCLVL());
							ps.setLong(4, PLXP.getCXP());
							ps.setInt(5, PLXP.getELVL());
							ps.setLong(6, PLXP.getEXP());
							ps.setInt(7, PLXP.getFLVL());
							ps.setLong(8, PLXP.getFXP());
							ps.setInt(9, PLXP.getMLVL());
							ps.setLong(10, PLXP.getMXP());
							ps.setInt(11, PLXP.getTLVL());
							ps.setLong(12, PLXP.getTXP());
							ps.setInt(13, PLXP.getWLVL());
							ps.setLong(14, PLXP.getWXP());
							ps.setString(15, key);
							ps.addBatch();
						}
						ps.executeBatch();
					} catch (SQLException ex) {
						log.warning("[CraftingReloaded] - Unable to update data!");
					}finally{
						try{
							if (conn != null){
								conn.close();
							}
						}catch (SQLException sqle) {
							log.warning("[CraftingReloaded] - Could not close connection to SQL");
						}
					}
				}
			}
			else{
				for(String key : PLVLXPT.keySet()){
					PELP = new PropertiesFile(PDir+key+PEL);
					PLXP = PLVLXPT.get(key);
					
					PELP.setInt("BLVL", PLXP.getBLVL()); PELP.setLong("BEXP", PLXP.getBXP());
					PELP.setInt("CLVL", PLXP.getCLVL()); PELP.setLong("CEXP", PLXP.getCXP());
					PELP.setInt("ELVL", PLXP.getELVL()); PELP.setLong("EEXP", PLXP.getEXP());
					PELP.setInt("FLVL", PLXP.getFLVL()); PELP.setLong("FEXP", PLXP.getFXP());
					PELP.setInt("MLVL", PLXP.getMLVL()); PELP.setLong("MEXP", PLXP.getMXP());
					PELP.setInt("TLVL", PLXP.getTLVL()); PELP.setLong("TEXP", PLXP.getTXP());
					PELP.setInt("WLVL", PLXP.getWLVL()); PELP.setLong("WEXP", PLXP.getWXP());
				}
			}
		UpdateAntiBlockFarm();
		etc.getServer().messageAll("[§5SKILLS§f]§d - Save Complete!");
		log.info("[CraftingReloaded] - Save Complete!");
	}
	
	public void TRUNCATETABLE(){
		Connection conn = getMySQLConn();
		PreparedStatement ps = null;
		if(conn != null){
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
	}
	
	public void UpdateAntiBlockFarm(){
		CRL = CR.CRL;
		AntiBlockFarm.clear();
		AntiBlockFarm.addAll(CRL.AntiBlockFarm);
		if(MySQL){
			TRUNCATETABLE();
			Connection conn = getMySQLConn();
			PreparedStatement ps = null;
			if(conn != null){
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
