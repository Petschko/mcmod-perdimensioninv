package uk.droobey.perdimesioninv;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
//serverSideOnly = true,acceptableRemoteVersions="*"
@Mod(modid="perdimensioninv", name="Per Dimension Inventorys", version=Reference.MOD_VERSION,serverSideOnly = true,acceptableRemoteVersions="*")

public class perdimesioninv {

	 @Mod.Instance("DimensionInventorys")
	  public static perdimesioninv instance;
	  public static InventoryHandler inventoryHandler;
	  public static Logger logger;
	  private static boolean isDebug;
	  @Mod.EventHandler
	  public void preInit(FMLPreInitializationEvent event)
	  {
	    perdimesioninv.inventoryHandler = new InventoryHandler();
	    perdimesioninv.inventoryHandler.preInit(event);
	    
	    logger = event.getModLog();
	    logger.info(Loader.instance().activeModContainer().getModId()+" "+Loader.instance().activeModContainer().getDisplayVersion()+" loading...");
	    isDebug=checkDebug();
	    writedebug("Debugging mode detected, outputting debug messages");
	  }
	  
	  @Mod.EventHandler
	  public void init(FMLInitializationEvent event)
	  {
	    MinecraftForge.EVENT_BUS.register(this.inventoryHandler);
	  }
	  
	
	  @Mod.EventHandler
	  public void serverStarting(FMLServerStartingEvent event)
	  {
	    event.registerServerCommand(new DICommand());
	  }
	
	  public static void writedebug(String message) {
		  if(isDebug) {
			  logger.info("DEBUG "+message);
		  }
	  }
	  
	  private boolean checkDebug() {
		  boolean isDeObf=false;
		try {
	            World.class.getMethod("getBlock", Integer.TYPE, Integer.TYPE, Integer.TYPE);
	            isDeObf = true;
	        }
	        catch (Throwable ex) {
	            isDeObf = false;
	        }
		return((Loader.instance().activeModContainer().getDisplayVersion().contains("-dev"))||isDeObf||System.getProperty("debug.log")!=null||java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0);
	  }

}
