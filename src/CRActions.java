import java.util.Random;


public class CRActions {
	CRData CRD;
	Random rand = new Random();
	public CRActions(CRData CRD){
		this.CRD = CRD;
	}
	
	private final String LVLM = "[§bYour§e %s §blevel is:§2 %s §band Exp is:§2 %s §f]";
	private final String EXPM = "[§bExp to next level:§2 %s §f]";
	
	private final String LVLT = "[§bYour §eTOTAL §blevel is:§2 %s §f]";
	private final String EXPT = "[§bYour §eTOTAL §bexperience is:§2 %s §f]";
	
	public boolean LevelExpCheck(Player player, String type){
		int[] el = CRD.Level(player.getName(), type);
		int lvl = el[0], xp = el[1];
		String t = tf(type);
		int expto = CRD.getBaseExp(type, (lvl+1)) - xp;
		if(expto <= 0 && !(lvl == CRD.maxlevel)){
			CRD.SilentLevel(player.getName(), type);
			return LevelExpCheck(player, type);
		}
		player.sendMessage(String.format(LVLM, t, String.valueOf(lvl), String.valueOf(xp)));
		player.sendMessage(String.format(EXPM, String.valueOf(expto)));
		return true;
	}
	
	public boolean TotalLevelExpCheck(Player player){
		int[] total = CRD.TotalEL(player.getName());
		player.sendMessage(String.format(LVLT, String.valueOf(total[0])));
		player.sendMessage(String.format(EXPT, String.valueOf(total[0])));
		return true;
	}
	
	public boolean AllLevelExpCheck(Player player){
		String[] skills = new String[]{"B", "C", "E", "F", "M", "T", "W"};
		for(String type : skills){
			String t = tf(type);
			int[] el = CRD.Level(player.getName(), type);
			int lvl = el[0], xp = el[1];
			player.sendMessage(String.format(LVLM, t, String.valueOf(lvl), String.valueOf(xp)));
		}
		return true;
	}
	
	public String tf(String type){
		if(type.equals("B")){
			return "Building";
		}
		else if(type.equals("C")){
			return "Combat";
		}
		else if(type.equals("E")){
			return "Excavation";
		}
		else if(type.equals("F")){
			return "Farming";
		}
		else if(type.equals("M")){
			return "Mining";
		}
		else if(type.equals("T")){
			return "Technician";
		}
		else if(type.equals("W")){
			return "WoodCutting";
		}
		return "";
	}
	
	public boolean Build(Player player, int id){
		int exp = CRD.getBET(id);
		CRD.addExp("B", player, exp);
		if(CRD.Bonus1(player.getName(), "B")){
			CRD.addExp("B", player, exp);
		}
		if(CRD.Bonus2(player.getName(), "B")){
			player.giveItem(371, 1);
		}
		if(CRD.Bonus3(player.getName(), "B")){
			player.giveItem(264, 1);
		}
		if(CRD.Bonus4(player.getName(), "B")){
			int add = player.getItemStackInHand().getAmount() + 1;
			player.getItemStackInHand().setAmount(add);
			player.getInventory().update();
		}
		if(CRD.Bonus5(player.getName(), "B")){
			
		}
		return false;
	}
	
	public boolean PVP(Player attack, Player defend, int damage){
		boolean drop1 = false, drop2 = false, extraxp = false;
		int d1 = rand.nextInt(10)+1;
		int d2 = rand.nextInt(15)+5;
		//int dr = rand.nextInt(5)+1;
		if(CRD.Bonus1(attack.getName(), "C")){
			drop1 = true;
		}
		if(CRD.Bonus2(attack.getName(), "C")){
			extraxp = true;
		}
		if(CRD.Bonus3(attack.getName(), "C")){
			if(!CRD.Bonus3(defend.getName(), "C")){
				defend.setHealth(defend.getHealth()-d1);
			}
		}
		if(CRD.Bonus4(attack.getName(), "C")){
			if(!CRD.Bonus3(defend.getName(), "C")){
				defend.setHealth(defend.getHealth()-d2);
			}
		}
		if(CRD.Bonus5(attack.getName(), "C")){
			drop2 = true;
		}
		if(CRD.Bonus5(defend.getName(), "C")){
			return true;
		}
		if(defend.getHealth()-damage <= 0){
			int exp = CRD.getCET("PVP");
			CRD.addExp("C", attack, exp);
			if(drop1){
				//??
			}
			if(drop2){
				//??
			}
			if(extraxp){
				CRD.addExp("C", attack, exp);
			}
		}
		return false;
	}
	
	public boolean MobCombat(Player player, Mob mob, int damage){
		boolean drop1 = false, drop2 = false, extraxp = false;
		int d1 = rand.nextInt(10)+1;
		int d2 = rand.nextInt(15)+5;
		int dr = rand.nextInt(5)+1;
		if(CRD.Bonus1(player.getName(), "C")){
			drop1 = true;
		}
		if(CRD.Bonus2(player.getName(), "C")){
			extraxp = true;
		}
		if(CRD.Bonus3(player.getName(), "C")){
			mob.setHealth(mob.getHealth()-d1);
		}
		if(CRD.Bonus4(player.getName(), "C")){
			mob.setHealth(mob.getHealth()-d2);
		}
		if(CRD.Bonus5(player.getName(), "C")){
			drop2 = true;
		}
		if(mob.getHealth()-damage <= 0){
			if(CRD.combatent(mob.getName())){
				int exp = CRD.getCET(mob.getName());
				CRD.addExp("C", player, exp);
				if(drop1){
					mob.dropLoot();
				}
				if(drop2){
					mob.getWorld().dropItem(mob.getLocation(), 356, dr);
				}
				if(extraxp){
					CRD.addExp("C", player, exp);
				}
			}
		}
		return false;
	}
	
	public boolean AnimalSlaughter(Player player, Mob Animal, int damage){
		boolean drop1 = false, drop2 = false, extraxp = false;
		int d1 = rand.nextInt(10)+1;
		int d2 = rand.nextInt(15)+5;
		int dr = rand.nextInt(5)+1;
		if(CRD.Bonus1(player.getName(), "F")){
			drop1 = true;
		}
		if(CRD.Bonus2(player.getName(), "F")){
			extraxp = true;
		}
		if(CRD.Bonus3(player.getName(), "F")){
			Animal.setHealth(Animal.getHealth()-d1);
		}
		if(CRD.Bonus4(player.getName(), "F")){
			Animal.setHealth(Animal.getHealth()-d2);
		}
		if(CRD.Bonus5(player.getName(), "F")){
			drop2 = true;
		}
		if(Animal.getHealth()-damage <= 0){
			if(CRD.combatent(Animal.getName())){
				int exp = CRD.getCET(Animal.getName());
				CRD.addExp("F", player, exp);
				if(drop1){
					Animal.dropLoot();
				}
				if(drop2){
					Animal.getWorld().dropItem(Animal.getLocation(), 356, dr);
				}
				if(extraxp){
					CRD.addExp("F", player, exp);
				}
			}
		}
		return false;
	}
	
	public boolean excavate(Player player, Block block){
		if(toolrequired(block.getType())){
			if(!tool(player.getItemInHand(), block.getType())){
				return false;
			}
		}
		int exp = CRD.getBET(block.getType());
		CRD.addExp("E", player, exp);
		if(CRD.Bonus1(player.getName(), "E")){
			if(GDSG(block.getType())){
				double randitem = Math.random();
				if(randitem <= 14.285){
					block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), 371, 1, block.getData());
				}
				else if(randitem > 14.285 && randitem <= 27.57){
					block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), 361, 1, block.getData());
				}
				else if(randitem > 27.57 && randitem <= 42.855){
					block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), 362, 1, block.getData());
				}
				else if(randitem > 42.855 && randitem <= 57.14){
					block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), 352, 1, block.getData());
				}
				else if(randitem > 57.14 && randitem <= 71.425){
					block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), 264, 1, block.getData());
				}
				else if(randitem > 71.425 && randitem <= 85.71){
					block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), 289, 1, block.getData());
				}
				else if(randitem > 85.71){
					block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), 348, 1, block.getData());
				}
			}
			else{
				CRD.addExp("E", player, exp);
			}
		}
		if(CRD.Bonus2(player.getName(), "E")){
			if(tool(player.getItemInHand(), block.getType())){
				int dam = player.getItemStackInHand().getDamage();
				player.getItemStackInHand().setDamage(dam-1);
			}
		}
		if(CRD.Bonus3(player.getName(), "E")){
			CRD.addExp("E", player, exp);
		}
		if(CRD.Bonus4(player.getName(), "E")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), block.getType(), 1, block.getData());
		}
		if(CRD.Bonus5(player.getName(), "E")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), block.getType(), 1, block.getData());
		}
		return false;
	}
	
	public boolean farm(Player player, int ID){
		int exp = CRD.getBET(ID);
		CRD.addExp("F", player, exp);
		return false;
	}
	
	public boolean mine(Player player, Block block){
		int blockType = block.getType();
		if(toolrequired(block.getType())){
			if(!tool(player.getItemInHand(), block.getType())){
				return false;
			}
		}
		if (blockType == 16) {
			blockType = 263;
		}
		if ((blockType == 73) || (blockType == 74)) {
			blockType = 331;
		}
		if (blockType == 1) {
			blockType = 4;
		}
		if (blockType == 56) {
			blockType = 264;
		}
		int exp = CRD.getBET(block.getType());
		CRD.addExp("M", player, exp);
		if(CRD.Bonus1(player.getName(), "M")){
			if(tool(player.getItemInHand(), block.getType())){
				int dam = player.getItemStackInHand().getDamage();
				player.getItemStackInHand().setDamage(dam-1);
			}
		}
		if(CRD.Bonus2(player.getName(), "M")){
			CRD.addExp("M", player, exp);
		}
		if(CRD.Bonus3(player.getName(), "M")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), blockType, 1, block.getData());
		}
		if(CRD.Bonus4(player.getName(), "M")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), block.getType(), 1, block.getData());
		}
		if(CRD.Bonus5(player.getName(), "M")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), blockType, 1, block.getData());
		}
		return false;
	}
	
	public boolean tech(Player player, int ID){
		int exp = CRD.getBET(ID);
		CRD.addExp("T", player, exp);
		if(CRD.Bonus1(player.getName(), "T")){
			CRD.addExp("T", player, exp);
		}
		if(CRD.Bonus2(player.getName(), "T")){
			player.giveItem(331, 1);
		}
		if(CRD.Bonus3(player.getName(), "T")){
			player.giveItem(371, 1);
		}
		if(CRD.Bonus4(player.getName(), "T")){
			player.getInventory().setSlot(player.getItemInHand(), player.getItemStackInHand().getAmount()+1, player.getItemStackInHand().getDamage(), player.getItemStackInHand().getSlot());
		}
		if(CRD.Bonus5(player.getName(), "T")){
			CRD.addExp("T", player, exp);
		}
		return false;
	}
	
	public boolean woodcut(Player player, Block block){
		if(toolrequired(block.getType())){
			if(!tool(player.getItemInHand(), block.getType())){
				return false;
			}
		}
		int exp = CRD.getBET(block.getType());
		CRD.addExp("W", player, exp);
		if(CRD.Bonus1(player.getName(), "W")){
			if(tool(player.getItemInHand(), block.getType())){
				int dam = player.getItemStackInHand().getDamage();
				player.getItemStackInHand().setDamage(dam-1);
			}
		}
		if(CRD.Bonus2(player.getName(), "W")){
			CRD.addExp("W", player, exp);
		}
		if(CRD.Bonus3(player.getName(), "W")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), block.getType(), 1, block.getData());
		}
		if(CRD.Bonus4(player.getName(), "W")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), block.getType(), 1, block.getData());
		}
		if(CRD.Bonus5(player.getName(), "W")){
			block.getWorld().dropItem(block.getX(), block.getY(), block.getZ(), block.getType(), 1, block.getData());
		}
		return false;
	}
	
	public boolean toolrequired(int id){
		if((CRD.RA(id))||(CRD.RPA(id))||(CRD.RS(id))){
			return true;
		}
		return false;
	}
	
	public boolean tool(int Tool, int ID){
		if(CRD.RA(ID)){
			return isAxe(Tool);
		}
		else if(CRD.RS(ID)){
			return isShovel(Tool);	
		}
		else if(CRD.RPA(ID)){
			return isPickAxe(Tool);
		}
		return false;
	}
	
	public boolean GDSG(int ID){
		switch(ID){
		case 2:
		case 3:
		case 12:
		case 13:
			return true;
		default: return false;
		}
	}
	
	public boolean isPickAxe(int Tool){
		switch(Tool){
		case 257:
		case 270:
		case 274:
		case 278:
		case 285:
			return true;
		default: return false;
		}
	}
	
	public boolean isShovel(int Tool){
		switch(Tool){
		case 256:
		case 269:
		case 273:
		case 277:
		case 284:
			return true;
		default: return false;
		}
	}
	
	public boolean isAxe(int Tool){
		switch(Tool){
		case 258:
		case 271:
		case 275:
		case 279:
		case 286:
			return true;
		default: return false;
		}
	}
}
