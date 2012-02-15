import java.util.logging.Logger;

public class CraftingReloaded extends Plugin {
	Logger log = Logger.getLogger("Minecraft");
	CRData CRD;
	CRActions CRA;
	static CRListener CRL;
	
	public void disable() {
		CRD.Disabler();
		log.info("[CraftingReloaded] Version 3.0b7 Disabled!");
	}
	public void enable() {
		log.info("[CraftingReloaded] Version 3.0b7 Enabled!");
	}
	public void initialize(){
		CRD = new CRData();
		CRA = new CRActions(CRD);
		CRL = new CRListener(CRD, CRA);
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
		etc.getLoader().addListener(PluginLoader.Hook.TAME, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.ENTITY_RIGHTCLICKED, CRL, this, PluginListener.Priority.LOW);
		etc.getLoader().addListener(PluginLoader.Hook.FOODLEVEL_CHANGE, CRL, this, PluginListener.Priority.LOW);
		CRD.ReloadOnline();
		log.info("[CraftingReloaded] Version 3.0 Beta 6 Initialized!");
	}
}
