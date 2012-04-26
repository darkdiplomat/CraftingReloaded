import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

public class CraftingReloaded extends Plugin {
	Logger log = Logger.getLogger("Minecraft");
	CRData CRD;
	CRActions CRA;
	CRListener CRL;
	public final String version = "3.0b11";
	public String CurrVer = "3.0b11";
	
	public void disable() {
		CRD.Disabler();
		log.info("[CraftingReloaded] Version "+version+" Disabled!");
	}
	public void enable() {
		log.info("[CraftingReloaded] Version "+version+" Enabled!");
		if(!isLatest()){
			log.info("[CraftingReloaded] There is an update available! "+CurrVer);
		}
	}
	public void initialize(){
		CRD = new CRData(this);
		CRA = new CRActions(CRD);
		CRL = new CRListener(this, CRD, CRA);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND , CRL, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.ENTITY_RIGHTCLICKED, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.FOODLEVEL_CHANGE, CRL, this, PluginListener.Priority.LOW);
		CRD.ReloadOnline();
		log.info("[CraftingReloaded] Version "+version+" Initialized!");
	}
	
	public boolean isLatest(){
	    try {
	        URL url = new URL("http://visualillusionsent.net/cmod_plugins/versions.php?plugin=CraftingReloaded");
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			if ((inputLine = in.readLine()) != null) {
				CurrVer = inputLine;
			}
			in.close();
		} catch (Exception E) {
			return true;
		}
		return (version.equals(CurrVer));
	}
}
