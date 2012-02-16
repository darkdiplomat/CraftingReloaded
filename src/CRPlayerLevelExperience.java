
public class CRPlayerLevelExperience {
	private String PlayerName;
	private int BLVL = 0,
				CLVL = 0,
				ELVL = 0,
				FLVL = 0,
				MLVL = 0,
				TLVL = 0,
				WLVL = 0;
	
	private long BXP = 0,
				 CXP = 0,
				 EXP = 0,
				 FXP = 0,
				 MXP = 0,
				 TXP = 0,
				 WXP = 0;
	
	public CRPlayerLevelExperience(String PlayerName){
		this.PlayerName = PlayerName;
	}
	
	public String getPlayerName(){
		return PlayerName;
	}
	
	/*Building XP/Level*/
	public void BAddLevel(int gain){ BLVL += gain; }
	
	public void BAddXP(long gain){ BXP += gain; }
	
	public int getBLVL(){ return BLVL; }
	
	public long getBXP(){ return BXP; }
	
	/*Combat XP/Level*/
	public void CAddLevel(int gain){ CLVL += gain; }
	
	public void CAddXP(long gain){ CXP += gain; }
	
	public int getCLVL(){ return CLVL; }
	
	public long getCXP(){ return CXP; }

	/*Excavating XP/Level*/
	public void EAddLevel(int gain){ ELVL += gain; }
	
	public void EAddXP(long gain){ EXP += gain; }
	
	public int getELVL(){ return ELVL; }
	
	public long getEXP(){ return EXP; }
	
	/*Farming XP/Level*/
	public void FAddLevel(int gain){ FLVL += gain; }
	
	public void FAddXP(long gain){ FXP += gain; }
	
	public int getFLVL(){ return FLVL; }
	
	public long getFXP(){ return FXP; }
	
	/*Mining XP/Level*/
	public void MAddLevel(int gain){ MLVL += gain; }
	
	public void MAddXP(long gain){ MXP += gain; }
	
	public int getMLVL(){ return MLVL; }
	
	public long getMXP(){ return MXP; }
	
	/*Technician XP/Level*/
	public void TAddLevel(int gain){ TLVL += gain; }
	
	public void TAddXP(long gain){ TXP += gain; }
	
	public int getTLVL(){ return TLVL; }
	
	public long getTXP(){ return TXP; }
	
	/*Woodcutting XP/Level*/
	public void WAddLevel(int gain){ WLVL += gain; }
	
	public void WAddXP(long gain){ WXP += gain; }
	
	public int getWLVL(){ return WLVL; }
	
	public long getWXP(){ return WXP; }
}
