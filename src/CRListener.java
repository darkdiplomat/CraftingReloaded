import java.util.ArrayList;
import java.util.Random;

public class CRListener extends PluginListener {
	private CraftingReloaded CR;
	private CRData CRD;
	private CRActions CRA;
	protected ArrayList<Block> AntiBlockFarm;
	private final PluginLoader loader = etc.getLoader();
	
	public CRListener(CraftingReloaded CR, CRData CRD, CRActions CRA){
		this.CR = CR;
		this.CRD = CRD;
		this.CRA = CRA;
		AntiBlockFarm = new ArrayList<Block>();
	}
	
	public void onLogin(Player player){
		if(CRD.keyExists(player.getName())){
			CRD.loadEXP(player.getName());
		}
		else if(player.canUseCommand("/skills")){
			CRD.setInitialEXP(player.getName());
		}
	}
	
	public void onDisconnect(Player player){
		if(player.canUseCommand("/skills")){
			CRD.SaveSingle(player.getName());
		}
	}
	
	public boolean onCommand(Player player, String[] cmd){
		if(cmd[0].equalsIgnoreCase("/skills")){
			if(player.canUseCommand("/skills")){
				if(cmd.length > 1){
					if((cmd[1].equalsIgnoreCase("building") || (cmd[1].equalsIgnoreCase("B")))){
						return CRA.LevelExpCheck(player, "B");
					}
					else if((cmd[1].equalsIgnoreCase("combat")) || (cmd[1].equalsIgnoreCase("C"))){
						return CRA.LevelExpCheck(player, "C");
					}
					else if((cmd[1].equalsIgnoreCase("excavating"))||(cmd[1].equalsIgnoreCase("E"))){
						return CRA.LevelExpCheck(player, "E");
					}
					else if((cmd[1].equalsIgnoreCase("farming"))||(cmd[1].equalsIgnoreCase("F"))){
						return CRA.LevelExpCheck(player, "F");
					}
					else if((cmd[1].equalsIgnoreCase("mining"))||(cmd[1].equalsIgnoreCase("M"))){
						return CRA.LevelExpCheck(player, "M");
					}
					else if((cmd[1].equalsIgnoreCase("technician"))||(cmd[1].equalsIgnoreCase("T"))){
						return CRA.LevelExpCheck(player, "T");
					}
					else if((cmd[1].equalsIgnoreCase("woodcutting"))||(cmd[1].equalsIgnoreCase("W"))){
						return CRA.LevelExpCheck(player, "W");
					}
					else if((cmd[1].equalsIgnoreCase("all")) || (cmd[1].equalsIgnoreCase("a"))){
						return CRA.AllLevelExpCheck(player);
					}
					else if((cmd[1].equalsIgnoreCase("total"))||(cmd[1].equalsIgnoreCase("to"))){
						return CRA.TotalLevelExpCheck(player);
					}
				}
				else{
					player.sendMessage("\u00A76----[\u00A73CraftingReloaded V"+CR.version+"\u00A76]----");
					if(player.isAdmin() && !CR.isLatest()){
						player.sendMessage("\u00A76----[\u00A73There is an update! V"+CR.CurrVer+"\u00A76]----");
					}
					player.sendMessage("\u00A7a/skills building\u00A7b        - displays \u00A76BUILDING\u00A7b level and XP");
					player.sendMessage("\u00A7a/skills combat\u00A7b        - displays \u00A76COMBAT\u00A7b level and XP");
					player.sendMessage("\u00A7a/skills excavating\u00A7b   - displays \u00A76EXCAVATING\u00A7b level and XP");
					player.sendMessage("\u00A7a/skills farming\u00A7b        - displays \u00A76FARMING\u00A7b level and XP");
					player.sendMessage("\u00A7a/skills mining\u00A7b          - displays \u00A76MINING\u00A7b level");
					player.sendMessage("\u00A7a/skills technician\u00A7b    - displays \u00A76TECHNICIAN\u00A7b level and XP");
					player.sendMessage("\u00A7a/skills woodcutting\u00A7b  - displays \u00A76WOODCUTTING\u00A7b level and XP");
					player.sendMessage("\u00A7a/skills all\u00A7b              - displays \u00A76ALL\u00A7b levels and XP");
					player.sendMessage("\u00A7a/skills total\u00A7b           - displays \u00A76TOTAL\u00A7b level and XP");
					player.sendMessage("\u00A7aAliases:\u00A7e b, c, e, f, m, t, w, a, to");
					return true;
				}
			}
		}
		else if((cmd[0].equalsIgnoreCase("/#save-all"))||(cmd[0].equalsIgnoreCase("/#stop"))){
			if(player.isOp()){
				CRD.SaveItNow();
			}
		}
		return false;
	}
	
	public boolean onConsoleCommand(String[] cmd){
		if((cmd[0].equalsIgnoreCase("save-all")) || (cmd[0].equalsIgnoreCase("stop"))){
			CRD.SaveItNow();
		}
		return false;
	}
	
	public boolean onBlockPlace(Player player, Block bp, Block bc, Item itemInHand){
		if(!player.getMode()){
			if(!isProtected(player, bc, "create")){
				if(bp != null && bp.getType() != 0){
					if(player.canUseCommand("/skills")){
						AntiBlockFarm.add(bp);
						if(CRD.buildblock(bp.getType())){
							return CRA.Build(player, bp.getType());
						}
						else if(CRD.farmblock(bp.getType())){
							return CRA.farm(player, bp.getType());
						}
						else if(CRD.techblock(bp.getType())){
							return CRA.tech(player, bp.getType());
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean onItemUse(Player player, Block bp, Block bc, Item itemInHand){
		if(player.getCreativeMode() == 0){
			int id = 0;
			if(itemInHand != null){
				id = itemInHand.getItemId();
			}
			if(!isBlockPlacingItem(id)){ return false; }
			if(!isProtected(player, bc, "create")){
				if(player.canUseCommand("/skills")){
					if(itemInHand.getItemId() == 355){
						bp.setType(26);
					}
					if(bp == null || bp.getType() == 0){
						if(itemInHand != null){
							bp.setType(getCorrectType(itemInHand.getItemId()));
						}
					}
					AntiBlockFarm.add(bp);
					if(CRD.buildblock(bp.getType())){
						return CRA.Build(player, bp.getType());
					}
					else if(CRD.farmblock(bp.getType())){
						return CRA.farm(player, bp.getType());
					}
					else if(CRD.techblock(bp.getType())){
						return CRA.tech(player, bp.getType());
					}
				}
			}
		}
		return false;
	}
	
	public boolean onBlockDestroy(Player player, Block block){
		if(!player.getMode()){
			if(player.canUseCommand("/skills")){
				if(!isProtected(player, block, "destroy")){
					int id = block.getType();
					if((id == 57) && (block.getData() == 7) && CRD.farmblock(57)){
						return CRA.farm(player, block.getType());
					}
					else if(block.getStatus() == 2){
						if(!(isAntiFarm(block))){
							if(CRD.excavateblock(id)){
								return CRA.excavate(player, block);
							}
							else if (CRD.mineblock(id)){
								return CRA.mine(player, block);
							}
							else if (CRD.farmblock(id)){
								return CRA.farm(player, id);
							}
							else if(CRD.woodblock(id)){
								return CRA.woodcut(player, block);
							}
						}
						else{
							AntiBlockFarm.remove(block);
						}
					}
					else{
						switch(block.getType()){
						case 31:
						case 37:
						case 38:
						case 39:
						case 40:
							if(!isAntiFarm(block)){
								if(CRD.farmblock(id)){
									return CRA.farm(player, id);
								}
							}
						case 83:
							if(CRD.farmblock(id)){
								if(!isAntiFarm(block)){
									Block block2 = player.getWorld().getBlockAt(block.getX(), block.getY()+1, block.getZ());
									if(block2 != null){
										for(int y = (block.getY()+1); y < (block.getY()+5); y++){
											block2 = player.getWorld().getBlockAt(block.getX(), y, block.getZ());
											if(block2 != null){
												if(!isAntiFarm(block)){
													if(block2.getType() == 83){
														CRA.farm(player, id);
													}
												}
											}
										}
									}
								}
								else{
									for(int y = block.getY(); y < (block.getY()+5); y++){
										Block block2 = player.getWorld().getBlockAt(block.getX(), y, block.getZ());
										if(block2 != null){
											if(!isAntiFarm(block)){
												if(block2.getType() == 83){
													CRA.farm(player, id);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount){
		if (type.equals(PluginLoader.DamageType.ENTITY)){
			if(attacker.isPlayer()){
				Player player = attacker.getPlayer();
				if((!player.getMode()) && (!player.isDamageDisabled()) && player.canUseCommand("/skills")){
					if(defender.isPlayer()){
						Player defending = defender.getPlayer();
						if(!isPVPDisabled(player)){
							if(CRD.combatent("PVP")){
								if(!defending.getMode() && !defending.isDamageDisabled()){
									return CRA.PVP(player, defending, amount);
								}
							}
						}
					}
					else if(defender.isMob()){
						Mob mob = new Mob((OEntityLiving)defender.getEntity());
						if(CRD.combatent(mob.getName())){
							if(CRD.combatent(mob.getName())){
								return CRA.MobAttack(player, mob, amount);
							}
						}
					}
					else if(defender.isAnimal()){
						Mob animal = new Mob((OEntityLiving)defender.getEntity());
						if(CRD.combatent(animal.getName())){
							return CRA.AnimalSlaughter(player, animal, amount);
						}
					}
				}
			}
			else if(defender.isPlayer() && attacker.isMob()){
				Player player = defender.getPlayer();
				Mob mob = new Mob((OEntityLiving)attacker.getEntity());
				if(!player.getMode() && !player.isDamageDisabled() && player.canUseCommand("/skills") && CRD.combatent(mob.getName())){
					return CRA.MobDefend(player, mob, amount);
				}
			}
		}
		return false;
	}
	
	public PluginLoader.HookResult onEntityRightClick(Player player, BaseEntity entity, Item item){
	    try{
    		if(entity != null && entity.isAnimal()){
    			Block block = player.getWorld().getBlockAt((int)entity.getX(), (int)entity.getY(), (int)entity.getZ());
    			if(!isProtected(player, block, "interact")){
    				Mob mob = new Mob((OEntityLiving)entity.getEntity());
    				if(item.getType() == Item.Type.Wheat){
    					CRD.addExp("F", player, 1);
    				}
    				else if(item.getType() == Item.Type.Bucket){
    					if(mob.getName().equals("Cow")){
    						CRD.addExp("F", player, 1);
    					}
    				}
    			}
    		}
	    }
	    catch(Exception e){ }
		return PluginLoader.HookResult.DEFAULT_ACTION;
	}
	
	public int onFoodLevelChange(Player player, int oldFoodLevel, int newFoodLevel){
		if(player.canUseCommand("/skills")){
			if(CRD.Bonus3(player.getName(), "F")){
				return oldFoodLevel;
			}
		}
		return newFoodLevel;
	}
	
	public boolean onEat(Player player, Item item){
		if(isFood(item.getType())){
			if(CRD.Bonus2(player.getName(), "F")){
				Random random = new Random();
				int newlevel = player.getFoodLevel() + (random.nextInt(3)+1);
				player.setFoodLevel(newlevel);
			}
		}
		return false;
	}
	
	private boolean isProtected(Player player, Block block, String type){
		if(block == null){
			block = player.getWorld().getBlockAt((int)player.getX(), (int)player.getY(), (int)player.getZ());
		}
		boolean protect = false;
		Plugin Realms = loader.getPlugin("Realms");
		Plugin Cuboids2 = loader.getPlugin("Cuboids2");
		Plugin CuboidPlugin = loader.getPlugin("CuboidPlugin");
		if(Realms != null && Realms.isEnabled()){
			if(!(Boolean)etc.getLoader().callCustomHook("Realms-PermissionCheck", new Object[] {type, player, block})){
				protect = true;
			}
		}
		if(Cuboids2 != null && Cuboids2.isEnabled()){
			if(!(Boolean)etc.getLoader().callCustomHook("CuboidAPI", new Object[] {"CAN_MODIFY", player.getName(), block })){
				protect = true;
			}
		}
		if(CuboidPlugin != null && CuboidPlugin.isEnabled()){
			if(!(Boolean)etc.getLoader().callCustomHook("CuboidPlugin-PermissionCheck", new Object[] {player, block})){
				protect = true;
			}
		}
		return protect;
	}
	
	private boolean isPVPDisabled(Player player){
		boolean nopvp = false;
		PluginLoader loader = etc.getLoader();
		Plugin Realms = loader.getPlugin("Realms");
		Plugin Cuboids2 = loader.getPlugin("Cuboids2");
		Plugin SafePVP = loader.getPlugin("SafePVP");
	    if(Realms != null && Realms.isEnabled()){
	        if(!(Boolean)etc.getLoader().callCustomHook("Realms-ZoneFlagCheck", new Object[] {"PVP", player})){
	            nopvp = true;
	        }
	    }
	    if(Cuboids2 != null && Cuboids2.isEnabled() && !nopvp){
	        String area = (String)etc.getLoader().callCustomHook("CuboidAPI", new Object[] { "AREA_GET_NAME_LOCAL", player.getName() });
	        if(area != null){
	            if(!(Boolean)etc.getLoader().callCustomHook("CuboidAPI", new Object[] {"AREA_GET_FLAG", area, player.getWorld(), "allowPvp" })){
	                nopvp = true;
	            }
	        }
	    }
	    if(SafePVP != null && SafePVP.isEnabled() && !nopvp){
	        if(!(Boolean)etc.getLoader().callCustomHook("PVPCheck", new Object[]{player.getName()})){
	            nopvp = true;
	        }
	    }
		return nopvp;
	}
	
	private int getCorrectType(int id){
		switch (id){
		case 324: return 64;
		case 330: return 71;
		case 356: return 93;
		case 362: return 105;
		default: return 0;
		}
	}
	
	private boolean isBlockPlacingItem(int id){
		switch(id){
		case 321:
		case 323:
		case 324:
		case 330:
		case 331:
		case 338:
		case 355:
		case 356:
		case 379:
		case 380:
			return true;
		default: return false;
		}
	}
	
	private boolean isAntiFarm(Block block){
		return AntiBlockFarm.contains(block);
	}
	
	private boolean isFood(Item.Type type){
		switch(type){
		case Bread:
		case Cookie:
		case MelonSlice:
		case MushroomSoup:
		case RawChicken:
		case CookedChicken:
		case RawBeef:
		case Steak:
		case Pork:
		case GrilledPork:
		case RawFish:
		case CookedFish:
		case Apple:
		case RottenFlesh:
		case SpiderEye:
			return true;
		default:
			return false;
		}
	}
}
