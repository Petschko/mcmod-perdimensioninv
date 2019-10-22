package uk.droobey.perdimesioninv;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;


public class SavedInventory {

	    public ItemStack[] mainInventory = new ItemStack[36];
	    public ItemStack[] armorInventory = new ItemStack[4];
	    public ItemStack[] baubleInventory = new ItemStack[7];
	    
	    int dimensionHash;

	    public SavedInventory(int dimensionHash, InventoryPlayer inventory) {
	        this.dimensionHash = dimensionHash;
	        this.loadFromPlayerInventory(inventory);
	  
	    }

	    private SavedInventory() {
	    }

	    public SavedInventory(int dimensionHash) {
	        this.dimensionHash = dimensionHash;
	    }

	    public int getDimensionHash() {
	        return this.dimensionHash;
	    }
	    
	    public Boolean isEmpty() {
	    	for (int i = 0; i < this.mainInventory.length; ++i) {
	    		if (this.mainInventory[i] == null) continue;
	    		if (Item.getIdFromItem(this.mainInventory[i].getItem())!=0){
	    			return false;
	    		}
	    	}
	    	

	    	for (int i = 0; i < this.armorInventory.length; ++i) {
	    		if (this.armorInventory[i] == null) continue;
	    		if (Item.getIdFromItem(this.armorInventory[i].getItem())!=0){
	    			return false;
	    		}
	    		}
	    	return true;
	    	
	    	
	    }

	    public static SavedInventory generateFromCompound(NBTTagCompound compound) {
	        SavedInventory si = new SavedInventory();
	        si.readFromNBT(compound);
	        return si;
	    }
	    

	    public void clearNBT(EntityPlayer player) {
	    	 NBTTagList nbtList = ((NBTTagCompound)player.getEntityData().getTag("PlayerPersisted")).getTagList("DimensionInventorys", 10);
	    	    
	    	    if (nbtList == null)
	    	    {
	    	    return;
	    	    }
	    	    for (int i = 0; i < nbtList.tagCount(); i++)
	    	    {
	    	      NBTTagCompound compound = nbtList.getCompoundTagAt(i);
	    	      if (compound != null)
	    	      {
	    	    	  if(compound.getInteger("dimensionHash")>-9999) {
	    	    		  nbtList.removeTag(i);
	    	    	  }
	    	      }
	    	      
	    	    }
	    	 ((NBTTagCompound)player.getEntityData().getTag("PlayerPersisted")).setTag("DimensionInventorys", nbtList);
	    
	    }
	    public void writeToNBT(NBTTagCompound compound) {
	    	
	    	if(!this.isEmpty()) {
	        NBTTagCompound nbttagcompound;
	        int i;
	        compound.setInteger("dimensionHash", this.dimensionHash);
	        
	        NBTTagList items = new NBTTagList();
	        
	        for (i = 0; i < this.mainInventory.length; ++i) {
	            if (this.mainInventory[i] == null) continue;
	            nbttagcompound = new NBTTagCompound();
	            nbttagcompound.setByte("Slot", (byte)i);
	            this.mainInventory[i].writeToNBT(nbttagcompound);
	            items.appendTag((NBTBase)nbttagcompound);
	        }
	        
	     
	        
	      /*  if ( InventoryHandler.includeBaubles) {
		        i = 0;
		        do {
		            if (i >= this.baublesInventory.length) {
		                compound.setTag("baubles", (NBTBase)items);
		                return;
		            }
		            if (this.baubleInventory[i] != null) {
		                nbttagcompound = new NBTTagCompound();
		                nbttagcompound.setByte("Slot", (byte)(i + 100));
		                this.baubleInventory[i].writeToNBT(nbttagcompound);
		                items.appendTag((NBTBase)nbttagcompound);
		            }
		            ++i;
		        } while(true);
		        } */
	        
	        i = 0;
	        do {
	            if (i >= this.armorInventory.length) {
	                compound.setTag("items", (NBTBase)items);
	                return;
	            }
	            if (this.armorInventory[i] != null) {
	                nbttagcompound = new NBTTagCompound();
	                nbttagcompound.setByte("Slot", (byte)(i + 100));
	                this.armorInventory[i].writeToNBT(nbttagcompound);
	                items.appendTag((NBTBase)nbttagcompound);
	            }
	            ++i;
	        } while (true);
	        
	
	    	}else {
	    		
	    		  perdimesioninv.writedebug("not saving NBT for "+this.dimensionHash+" -empty inventory");
	    	}
	    }

	    public void readFromNBT(NBTTagCompound compound) {
	        this.dimensionHash = compound.getInteger("dimensionHash");
	        NBTTagList items = compound.getTagList("items", 10);
	        
	        int i = 0;
	        while (i < items.tagCount()) {
	            NBTTagCompound nbttagcompound = items.getCompoundTagAt(i);
	            int j = nbttagcompound.getByte("Slot") & 255;
	            ItemStack itemstack = new ItemStack((NBTTagCompound)nbttagcompound);
	            if (itemstack != null) {
	                if (j >= 0 && j < this.mainInventory.length) {
	                    this.mainInventory[j] = itemstack;
	                }
	                if (j >= 100 && j < this.armorInventory.length + 100) {
	                    this.armorInventory[j - 100] = itemstack;
	                }
	            }
	            ++i;
	        }
	        
	       /* if ( InventoryHandler.includeBaubles) {
	        	NBTTagList baubles = compound.getTagList("baubles", 10);
	        	i=0;
	        	while (i < baubles.tagCount()) {
	        		NBTTagCompound nbttagcompound = items.getCompoundTagAt(i);
	        		int j = nbttagcompound.getByte("Slot") & 255;
	        		ItemStack itemstack = new ItemStack((NBTTagCompound)nbttagcompound);
		            if (itemstack != null) {
		            	if (j >= 0 && j < this.baubleInventory.length) {
		            		this.baubleInventory[j]=itemstack;
		            	}
		            }

	        	}
	        }*/
	      
	    }

	    

	    
	    public void loadFromPlayerInventory(InventoryPlayer player) {
	    	perdimesioninv.writedebug("loading player inventory for dim"+this.dimensionHash);
	        for (int mainSlot = 0; mainSlot < player.mainInventory.size(); ++mainSlot) {
	            this.mainInventory[mainSlot] = (ItemStack)player.mainInventory.get(mainSlot).copy();
	        }
	        int armorSlot = 0;
	        while (armorSlot < player.armorInventory.size()) {
	            this.armorInventory[armorSlot] = (ItemStack)player.mainInventory.get(armorSlot).copy();
	            ++armorSlot;
	        }
	        
	       /* if ( InventoryHandler.includeBaubles) {
	       
	        	 BaublesContainer container = (BaublesContainer)BaublesApi.getBaublesHandler(player.player);
	        	 int baubleSlot = 0;
	 	        while (baubleSlot < container.getSlots()) {
	 	            this.baubleInventory[baubleSlot] = (ItemStack)container.getStackInSlot(baubleSlot);
	 	            ++baubleSlot;
	 	        }
	        }*/

	    }

	    public void putInPlayerInventory(InventoryPlayer player) {
	        for (int mainSlot = 0; mainSlot < this.mainInventory.length; ++mainSlot) {

	        	if((ItemStack)this.mainInventory[mainSlot]!=null) {
	            player.mainInventory.set(mainSlot,   (ItemStack)this.mainInventory[mainSlot]);
	        	}
	        }
	        int armorSlot = 0;
	        while (armorSlot < this.armorInventory.length) {
	        	if((ItemStack)this.armorInventory[armorSlot]!=null) {
	            player.armorInventory.set(armorSlot,  (ItemStack)this.armorInventory[armorSlot]);
	        	}
	            ++armorSlot;
	        }
	        
	    /*  if ( InventoryHandler.includeBaubles) {
	        	BaublesApi.getBaublesHandler(player.player);
	        	 BaublesContainer container = (BaublesContainer)BaublesApi.getBaublesHandler(player.player);
	             for (int i=0; i < container.getSlots(); i++){
	            	 ItemStack stack = container.getStackInSlot(i);
	            	 container.setStackInSlot(i, stack);
	             }
	        }*/

	    

	    }
	}


