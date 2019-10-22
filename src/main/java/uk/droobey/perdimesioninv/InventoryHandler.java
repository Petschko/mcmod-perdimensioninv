package uk.droobey.perdimesioninv;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import uk.droobey.perdimesioninv.SavedInventory;
import uk.droobey.perdimesioninv.Inventories;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import org.apache.logging.log4j.util.Strings;

public class InventoryHandler
{
  HashSet<InventoryGroup> groups;
  boolean firstRun;
  Configuration configuration;
  public static int oldDimension;
  
  public boolean includeHealth = false;
  public boolean includeHunger = false;
  private boolean excludeVanillaDims;
  private int defaultInventory;
  public static boolean includeBaubles = true;
  
  public InventoryHandler()
  {
    this.groups = new HashSet();
    this.firstRun = true;
  }
  
  public void preInit(FMLPreInitializationEvent event)
  {
    this.configuration = new Configuration(event.getSuggestedConfigurationFile());
    syncConfig();
  }
  
  public void syncConfig()
  {
    this.configuration.load();
    boolean seperateByDefault = this.configuration.getBoolean("seperateByDefault", "Settings", false, "false = All Dimensions share their inventory by default, true = All Dimensions have their own inventory by default.");
    excludeVanillaDims = this.configuration.getBoolean("excludeVanillaDims", "Settings", true, "Exclude vanillia dimensions (nether,the end) from having their own inventory.");
    defaultInventory = this.configuration.getInt("defaultInventory", "Settings", 0, -9999, 9999, "If dimension not in a group, use the inventory from this dimension. If not found, falls back to overworld (dim0)");
    
    //boolean removeUnregisteredDimensions = this.configuration.getBoolean("removeUnregisteredDimensions", "Settings", false, "If dimensions not registered, but inventory is stored, delete stored inventory.");
    
    includeHealth = this.configuration.getBoolean("includeHealth", "Settings", false, "Also switch health when changing dimension groups");
    includeHunger = this.configuration.getBoolean("includeHunger", "Settings", false, "Also switch hunger when changing dimension groups");

    includeBaubles = this.configuration.getBoolean("includeBaubles", "ModSupport", false, "If installed, also switch Baubles mod inventory");
    
    String baseString = this.configuration.getString("Groups", "Settings", "", "Groups seperated by |, dimensions seperated by \",\" ");
    
    this.groups = new HashSet();
    
    if (!Strings.isEmpty(baseString))
    {
      String[] stringGroups = new String[0];
      
      
      stringGroups = baseString.split("\\|");
      
      for (String stringGroup : stringGroups)
      {
        String[] dimensions = stringGroup.split(",");
        
        InventoryGroup ir = new InventoryGroup();
        
        for (String dimension : dimensions)
        {
          if (Strings.isNotEmpty(dimension))
          {
            ir.addDimension(Integer.parseInt(dimension));
          }
        }
        this.groups.add(ir);
      }
    }
    
    Integer[] dimensions = DimensionManager.getIDs();
    
    ArrayList<Integer> dimensionsLeft = new ArrayList();
    
    for (Integer dimension : dimensions)
    {
    	
    	
      if (getDimensionGroup(dimension.intValue()) == null)
      {

    	
    	  dimensionsLeft.add(dimension);
        	
        	
        }else {    
        	perdimesioninv.writedebug("dim"+dimension+" group"+this.groups.toString());
        	InventoryGroup ir = new InventoryGroup();
            ir.addDimension(dimension.intValue());
            this.groups.add(ir);
        }
        
      }
    
    if ((!seperateByDefault) && (!dimensionsLeft.isEmpty()))
    {
    	
      InventoryGroup ir = getDimensionGroup(defaultInventory);
      if( ir !=null) {
      
    }else {
    	perdimesioninv.logger.warn("Could not find dim "+defaultInventory+" in inventory groups, falling back to Overworld! (dim0)");
    	ir = getDimensionGroup(0);
    }
      
      for (Integer dimension : dimensionsLeft)
      {
    	  
        ir.addDimension(dimension.intValue());
      }
      
      this.groups.add(ir);
    }else if ((seperateByDefault) && (!dimensionsLeft.isEmpty())) {
    	
    	 for (Integer dimension : dimensionsLeft)
         {
    		 InventoryGroup ir = new InventoryGroup();
    		 ir.addDimension(dimension.intValue());
    		 this.groups.add(ir);
         }
    	
    }
    

    	 perdimesioninv.writedebug("groups "+this.groups.toString());

   
    perdimesioninv.writedebug("dims "+dimensionsLeft.toString());
    if (this.configuration.hasChanged())
    {
      this.configuration.save();
    }
  }
  
  private List<SavedInventory> loadSavedInventorys(NBTTagList list)
  {
    ArrayList<SavedInventory> inventoryList = new ArrayList();
    
    for (int i = 0; i < list.tagCount(); i++)
    {
      NBTTagCompound compound = list.getCompoundTagAt(i);
      
      if (compound != null)
      {
        inventoryList.add(SavedInventory.generateFromCompound(compound));
      }
    }
    
    return inventoryList;
  }
  
 
  private SavedInventory saveInventory(EntityPlayer player, int originHash) {
	  if (!player.getEntityData().hasKey("PlayerPersisted"))
	    {
	      player.getEntityData().setTag("PlayerPersisted", new NBTTagCompound());
	    }
	    NBTTagList nbtList = ((NBTTagCompound)player.getEntityData().getTag("PlayerPersisted")).getTagList("DimensionInventorys", 10);
	    
	    if (nbtList == null)
	    {
	      nbtList = new NBTTagList();
	    }
	    List<SavedInventory> inventoryList = loadSavedInventorys(nbtList);
	    
	    
	    SavedInventory originInventory = null;

	    int originInventoryIndex = -1;
	    
	    float originHealth = player.getHealth();
	    int originFoodLevel =player.getFoodStats().getFoodLevel();
	    for (int index = 0; index < inventoryList.size(); index++)
	    {
	    	SavedInventory savedInventory = (SavedInventory)inventoryList.get(index);
	    	  if (savedInventory.getDimensionHash() == originHash)
	          {
	    		  originInventory = savedInventory;
		            originInventoryIndex = index;
		            
	          }
	    }
	    
	    if (originInventory == null)
        {

          originInventory = new SavedInventory(originHash, player.inventory);

          
        }else {
        	
        	nbtList.removeTag(originInventoryIndex);
        }
	    
	
		  NBTTagCompound originSave = new NBTTagCompound();
		  
		  if(includeHealth) {
		    	

			    originSave.setFloat("Health", originHealth);
			    }
			    
			    if(includeHunger) {
			    	 originSave.setInteger("foodLevel",originFoodLevel);
			   
			    }
			    return originInventory;  
  }
  
  
  private Inventories getInventories(EntityPlayer player, int originHash, int destinationHash) {
	  if (!player.getEntityData().hasKey("PlayerPersisted"))
	    {
	      player.getEntityData().setTag("PlayerPersisted", new NBTTagCompound());
	    }
	    NBTTagList nbtList = ((NBTTagCompound)player.getEntityData().getTag("PlayerPersisted")).getTagList("DimensionInventorys", 10);
	    
	    if (nbtList == null)
	    {	
	      nbtList = new NBTTagList();
	    }
	    List<SavedInventory> inventoryList = loadSavedInventorys(nbtList);
	    
	    
	    SavedInventory originInventory = null;
	    SavedInventory destinationInventory = null;

	    int originInventoryIndex = -1;
	    @SuppressWarnings("unused")
		int destinationInventoryIndex = -1;
	    
	    
	    perdimesioninv.writedebug("old dim "+oldDimension);
	    

	    for (int index = 0; index < inventoryList.size(); index++)
	    {
	    	  SavedInventory savedInventory = (SavedInventory)inventoryList.get(index);
	          perdimesioninv.writedebug("get inv "+index+" dim"+savedInventory.getDimensionHash());
	     
	          if (savedInventory.getDimensionHash() == originHash)
	          {
	            originInventory = savedInventory;
	            originInventoryIndex = index;
	            perdimesioninv.writedebug("Origin dim: "+savedInventory.getDimensionHash());
	            
	          }
	          else if (savedInventory.getDimensionHash() == destinationHash)
	          {
	            destinationInventory = savedInventory;
	            destinationInventoryIndex = index;
	            perdimesioninv.writedebug("Destination dim: "+savedInventory.getDimensionHash());
	          }
	          
	         
	    }
	    
	    if (originInventory == null)
        {
        	perdimesioninv.writedebug("Origin dim null");
          originInventory = new SavedInventory(originHash, player.inventory);

          
        }
        else
        {
          nbtList.removeTag(originInventoryIndex);
        }
	    
	    
	    if (destinationInventory == null)
	    {
	    	perdimesioninv.writedebug("Dest dim null");

	      destinationInventory = new SavedInventory(destinationHash);
	     
	    }
	    return new Inventories(originInventory, destinationInventory, originInventoryIndex, destinationInventoryIndex);
  }
  
  private void switchedGroup(EntityPlayer player, int originHash, int destinationHash)
  {
	 Inventories invs = getInventories(player,originHash,destinationHash);
    SavedInventory originInventory = invs.originInventory;
    SavedInventory destinationInventory = invs.destinationInventory;


    
    NBTTagList nbtList = ((NBTTagCompound)player.getEntityData().getTag("PlayerPersisted")).getTagList("DimensionInventorys", 10);
    
    
    
    perdimesioninv.writedebug("sg old dim "+oldDimension);
    
    float originHealth = player.getHealth();
    int originFoodLevel =player.getFoodStats().getFoodLevel();

    
    if (destinationInventory.isEmpty())
    {
    	perdimesioninv.writedebug("sg Dest dim null");
    	player.inventory.clear();
      destinationInventory = new SavedInventory(destinationHash);
     
    }
    

 
   // originInventory.loadFromPlayerInventory(player.inventory);

    destinationInventory.putInPlayerInventory(player.inventory);
    
    NBTTagCompound originSave = new NBTTagCompound();
    if(includeHealth) {
    	

    originSave.setFloat("Health", originHealth);
    }
    
    if(includeHunger) {
    	 originSave.setInteger("foodLevel",originFoodLevel);
   
    }
    originSave.setIntArray("lastPos", new int[] {(int) player.posX,(int) player.posY,(int) player.posZ}  );
    

    originInventory.writeToNBT(originSave);
    nbtList.appendTag(originSave);
    

    ((NBTTagCompound)player.getEntityData().getTag("PlayerPersisted")).setTag("DimensionInventorys", nbtList);
  }
  
  private InventoryGroup getDimensionGroup(int dimension)
  {
    for (InventoryGroup group : this.groups)
    {
      if (group.containsDimension(dimension))
      {
        return group;
      }
    }
    return null;
  }
  
  
  //ded
  @SubscribeEvent
  public void playerDeath(LivingDeathEvent event) {
	  if(event.getEntity() instanceof EntityPlayer){
		  oldDimension=event.getEntity().dimension;
	  }
	  
  }
  
  @SubscribeEvent
  public void playerRespawn(PlayerRespawnEvent event)
  {

      if (this.firstRun)
      {
        this.firstRun = false;
        syncConfig();
      }
      int deathDimension = oldDimension;
      int spawnDimension = event.player.dimension;
      perdimesioninv.writedebug("respawn "+deathDimension+">"+spawnDimension);
      if (deathDimension != spawnDimension)
      {
        switchedDimension(event.player, deathDimension, spawnDimension);
      }
    
  }
  
  private void switchedDimension(EntityPlayer player, int originDimension, int destinationDimension)
  {
	  perdimesioninv.writedebug("dimension switch");
    if (this.firstRun)
    {
      this.firstRun = false;
      syncConfig();
    }
    
    int originHash = -1;
    int destinationHash = -1;

    
    if((excludeVanillaDims) && (destinationDimension==1||destinationDimension==-1)) {
  	  perdimesioninv.writedebug("Vanilla dimension - dont change anything");
  	return;  
    }
    
    //if(player.isServerWorld()) {
   //return; 
  //}
    InventoryGroup originGroup = getDimensionGroup(originDimension);
    InventoryGroup destinationGroup = getDimensionGroup(destinationDimension);
    
    if ((originGroup == null) || (destinationGroup == null))
    {
      syncConfig();
      
      originGroup = getDimensionGroup(originDimension);
      destinationGroup = getDimensionGroup(destinationDimension);
      
      
    
      
      if ((originGroup == null) || (destinationGroup == null))
      {
    	  perdimesioninv.writedebug("Dimensions are in no Inventory Group (" + originDimension + "->" + destinationDimension + ")");
    	  return;
        
      }
    }
    
    originHash = originGroup.getDimensionHash();
    destinationHash = destinationGroup.getDimensionHash();
    
    if (originHash != destinationHash)
    {
      switchedGroup(player, originHash, destinationHash);
    }
  }
  
  @SubscribeEvent
  public void onCommand(CommandEvent event) {
	  if (event.getSender() instanceof EntityPlayer) {
		  EntityPlayer player = (EntityPlayer) event.getSender();
		  if(event.getCommand().getName().startsWith("tp")) {
			  
			  saveInventory(player,player.dimension);
			  
	  perdimesioninv.writedebug("Command: "+event.getCommand()+" in"+player.dimension);
		  }
	  }
  }
  
  @SubscribeEvent
  public void onEntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {
	  perdimesioninv.writedebug("tp dim: entity type "+event.getEntity().getClass().getTypeName());
	  if (event.getEntity() instanceof EntityPlayer) {
		  EntityPlayer player = (EntityPlayer) event.getEntity();
		  Inventories invs = getInventories(player,player.dimension,event.getDimension());
		  
		  SavedInventory originInventory = invs.originInventory;
		  
	        perdimesioninv.writedebug("Before DimTP Origin dim "+player.dimension+": "+ originInventory.getDimensionHash());
	        originInventory.loadFromPlayerInventory(player.inventory);
	      }

 
  }
  
  @SubscribeEvent
  public void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
  {
	  perdimesioninv.writedebug("dim change from "+event.fromDim+ " current "+event.player.dimension);
	  
	
	  
      int originDimension = event.fromDim;
      int destinationDimension = event.toDim;
      
      if (originDimension != destinationDimension)
      {
        switchedDimension(event.player, originDimension, destinationDimension);
      }
    
  }
}
