
public class CRPlayerLevelExperience {
	private String PlayerName;
	private int BLVL = 0, BXP = 0,
		CLVL = 0, CXP = 0, 
		ELVL = 0, EXP = 0, 
		FLVL = 0, FXP = 0,
		MLVL = 0, MXP = 0, 
		TLVL = 0, TXP = 0, 
		WLVL = 0, WXP = 0;
	
	public CRPlayerLevelExperience(String PlayerName){
		this.PlayerName = PlayerName;
	}
	
	public String getPlayerName(){
		return PlayerName;
	}
	
	/*Building XP/Level*/
	public void BAddLevel(int gain){ BLVL += gain; }
	
	public void BAddXP(int gain){ BXP += gain; }
	
	public int getBLVL(){ return BLVL; }
	
	public int getBXP(){ return BXP; }
	
	/*Combat XP/Level*/
	public void CAddLevel(int gain){ CLVL += gain; }
	
	public void CAddXP(int gain){ CXP += gain; }
	
	public int getCLVL(){ return CLVL; }
	
	public int getCXP(){ return CXP; }

	/*Excavating XP/Level*/
	public void EAddLevel(int gain){ ELVL += gain; }
	
	public void EAddXP(int gain){ EXP += gain; }
	
	public int getELVL(){ return ELVL; }
	
	public int getEXP(){ return EXP; }
	
	/*Farming XP/Level*/
	public void FAddLevel(int gain){ FLVL += gain; }
	
	public void FAddXP(int gain){ FXP += gain; }
	
	public int getFLVL(){ return FLVL; }
	
	public int getFXP(){ return FXP; }
	
	/*Mining XP/Level*/
	public void MAddLevel(int gain){ MLVL += gain; }
	
	public void MAddXP(int gain){ MXP += gain; }
	
	public int getMLVL(){ return MLVL; }
	
	public int getMXP(){ return MXP; }
	
	/*Technician XP/Level*/
	public void TAddLevel(int gain){ TLVL += gain; }
	
	public void TAddXP(int gain){ TXP += gain; }
	
	public int getTLVL(){ return TLVL; }
	
	public int getTXP(){ return TXP; }
	
	/*Woodcutting XP/Level*/
	public void WAddLevel(int gain){ WLVL += gain; }
	
	public void WAddXP(int gain){ WXP += gain; }
	
	public int getWLVL(){ return WLVL; }
	
	public int getWXP(){ return WXP; }
}
