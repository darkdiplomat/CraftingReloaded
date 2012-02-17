import java.util.Random;

public class CRActions {
	CRData CRD;
	Random rand = new Random();
	public CRActions(CRData CRD){
		this.CRD = CRD;
	}
	
	private final String LVLM = "[§bYour §e%s §6LEVEL§b:§2 %s §6XP§b:§2 %s§f]";
	private final String EXPM = "[§eXP§b to next level:§2 %s§f]";
	
	private final String LVLT = "[§bYour §eTOTAL §bLEVEL:§2 %s§f]";
	private final String EXPT = "[§bYour §eTOTAL §bXP:§2 %s§f]";
	
	public boolean LevelExpCheck(Player player, String type){
		int lvl = CRD.getLevel(player.getName(), type);
		long xp = CRD.getXP(player.getName(), type);
		String t = tf(type);
		long expto = CRD.getBaseExp(type, (lvl+1)) - xp;
		if(expto < 0){
			expto = 0;
		}
		player.sendMessage(String.format(LVLM, t, String.valueOf(lvl), String.valueOf(xp)));
		player.sendMessage(String.format(EXPM, String.valueOf(expto)));
		return true;
	}
	
	public boolean TotalLevelExpCheck(Player player){
		int tlvl = CRD.TotalLevel(player.getName());
		long txp = CRD.TotalXP(player.getName());
		player.sendMessage(String.format(LVLT, String.valueOf(tlvl)));
		player.sendMessage(String.format(EXPT, String.valueOf(txp)));
		return true;
	}
	
	public boolean AllLevelExpCheck(Player player){
		String[] skills = new String[]{"B", "C", "E", "F", "M", "T", "W"};
		for(String type : skills){
			String t = tf(type);
			int lvl = CRD.getLevel(player.getName(), type);
			long xp = CRD.getXP(player.getName(), type);
			player.sendMessage(String.format(LVLM, t, String.valueOf(lvl), String.valueOf(xp)));
		}
		return true;
	}
	
	public String tf(String type){
		if(type.equals("B")){
			return "BUILDING";
		}
		else if(type.equals("C")){
			return "COMBAT";
		}
		else if(type.equals("E")){
			return "EXCAVATING";
		}
		else if(type.equals("F")){
			return "FARMING";
		}
		else if(type.equals("M")){
			return "MINING";
		}
		else if(type.equals("T")){
			return "TECHNICIAN";
		}
		else if(type.equals("W")){
			return "WOODCUTTING";
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
			player.giveItemDrop(371, 1);
		}
		if(CRD.Bonus3(player.getName(), "B")){
			player.giveItemDrop(264, 1);
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
		int resist = rand.nextInt(5)+1;
		int d1 = rand.nextInt(3)+1;
		int d2 = rand.nextInt(5)+3;
		int blood = rand.nextInt(3)+1;
		int bones = rand.nextInt(3)+1;
		int dh = defend.getHealth();
		int ed = 0;
		if(CRD.Bonus5(defend.getName(), "C")){
			return true;
		}
		if(CRD.Bonus2(defend.getName(), "C")){
			ed -= resist;
		}
		if(CRD.Bonus3(attack.getName(), "C")){
			if(!CRD.Bonus3(defend.getName(), "C")){
				ed += d1;
			}
		}
		if(CRD.Bonus4(attack.getName(), "C")){
			if(!CRD.Bonus4(defend.getName(), "C")){
				ed += d2;
			}
		}
		if(CRD.Bonus5(attack.getName(), "C")){
			//drop2 = true;
		}
		if(ed > 0){
			if(dh - ed < 1){
				ed = 1;
			}
			else{
				ed -= dh;
			}
		}
		if(ed < 0){
			ed *= -1;
		}
		defend.setHealth(ed);
		if(defend.getHealth()-damage <= 0){
			int exp = CRD.getCET("PVP");
			CRD.addExp("C", attack, exp);
			if(CRD.Bonus1(attack.getName(), "C")){
				defend.getWorld().dropItem(defend.getLocation(), 352, bones);
				defend.getWorld().dropItem(defend.getLocation(), 351, blood, 1);
			}
			if(CRD.Bonus2(attack.getName(), "C")){
				CRD.addExp("C", attack, exp);
			}
		}
		return false;
	}
	
	public boolean MobAttack(Player player, Mob mob, int damage){
		int d1 = rand.nextInt(5)+1;
		int d2 = rand.nextInt(7)+3;
		int blood = rand.nextInt(3)+1;
		int bones = rand.nextInt(3)+1;
		if(CRD.Bonus3(player.getName(), "C")){
			if(mob.getHealth()-d1 < 1){
				mob.setHealth(1);
			}
			else{
				mob.setHealth(mob.getHealth()-d1);
			}
		}
		if(CRD.Bonus4(player.getName(), "C")){
			if(mob.getHealth()-d2 < 1){
				mob.setHealth(1);
			}
			else{
				mob.setHealth(mob.getHealth()-d2);
			}
		}
		
		if(mob.getHealth()-damage <= 0){
			if(CRD.combatent(mob.getName())){
				int exp = CRD.getCET(mob.getName());
				CRD.addExp("C", player, exp);
				if(CRD.Bonus1(player.getName(), "C")){
					mob.getWorld().dropItem(mob.getLocation(), 352, bones);
					mob.getWorld().dropItem(mob.getLocation(), 331, blood);
				}
				if(CRD.Bonus2(player.getName(), "C")){
					CRD.addExp("C", player, exp);
				}
				if(CRD.Bonus5(player.getName(), "C")){
					mob.dropLoot();
				}
			}
		}
		return false;
	}
	
	public boolean MobDefend(Player player, Mob mob, int damage){
		int mh = mob.getHealth();
		int ph = player.getHealth();
		int res = (int)Math.ceil(damage*0.25);
		if(CRD.Bonus5(player.getName(), "C")){
			return true;
		}
		if(CRD.Bonus1(player.getName(), "C")){
			ph += res;
			player.setHealth(ph);
		}
		if(CRD.Bonus2(player.getName(), "C")){
			mh -= res;
			mob.setHealth(mh);
		}
		if(CRD.Bonus3(player.getName(), "C")){
			ph += res;
			player.setHealth(ph);
		}
		if(CRD.Bonus4(player.getName(), "C")){
			mh -= res;
			mob.setHealth(mh);
		}
		if(mh < 1){
			mob.dropLoot();
		}
		return false;
	}
	
	public boolean AnimalSlaughter(Player player, Mob Animal, int damage){
		boolean drop1 = false, drop2 = false, extraxp = false;
		int d1 = rand.nextInt(10)+1;
		int d2 = rand.nextInt(15)+5;
		int blood = rand.nextInt(3)+1;
		int bones = rand.nextInt(3)+1;
		if(CRD.Bonus1(player.getName(), "F")){
			drop1 = true;
		}
		if(CRD.Bonus2(player.getName(), "F")){
			extraxp = true;
		}
		if(CRD.Bonus3(player.getName(), "F")){
			if(Animal.getHealth()-d1 < 1){
				Animal.setHealth(1);
			}
			else{
				Animal.setHealth(Animal.getHealth()-d1);
			}
		}
		if(CRD.Bonus4(player.getName(), "F")){
			if(Animal.getHealth()-d2 < 1){
				Animal.setHealth(1);
			}
			else{
				Animal.setHealth(Animal.getHealth()-d2);
			}
		}
		if(CRD.Bonus5(player.getName(), "F")){
			drop2 = true;
		}
		if(Animal.getHealth()-damage <= 0){
			if(CRD.combatent(Animal.getName())){
				int exp = CRD.getCET(Animal.getName());
				CRD.addExp("F", player, exp);
				if(drop1){
					Animal.getWorld().dropItem(Animal.getLocation(), 352, bones);
					Animal.getWorld().dropItem(Animal.getLocation(), 331, blood);
				}
				if(drop2){
					Animal.dropLoot();
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
			player.giveItemDrop(331, 1);
		}
		if(CRD.Bonus3(player.getName(), "T")){
			player.giveItemDrop(371, 1);
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
