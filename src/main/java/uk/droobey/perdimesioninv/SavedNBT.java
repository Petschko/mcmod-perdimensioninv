package uk.droobey.perdimesioninv;

import java.util.logging.Logger;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class SavedNBT {
	int dimensionHash;
	String tag;
	Object data;
	
	public SavedNBT(int dimensionHash, String tag) {
        this.dimensionHash = dimensionHash;
        this.tag=tag;
    }
	
	public SavedNBT(int dimensionHash, String tag, Object data) {
        this.dimensionHash = dimensionHash;
        this.tag=tag;
        this.data=data;
    }
	
	public SavedNBT(int dimensionHash) {
        this.dimensionHash = dimensionHash;
    }
	
	public int getDimensionHash() {
        return this.dimensionHash;
    }
	
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger("dimensionHash", this.dimensionHash);
		NBTTagList items = new NBTTagList();
		switch(this.data.getClass().getName()){
		case "String":
			new NBTTagCompound().setString(this.tag, (String) this.data);
		break;
		default:
			throw new RuntimeException("Failed to write NBT- "+this.data.getClass().getName()+" is not a valid NBT tag type!");
		
			
		}
		
	}
	
}
