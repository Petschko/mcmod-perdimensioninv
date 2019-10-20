package uk.droobey.perdimesioninv;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;

public class DICommand extends CommandBase {


	@Override
	public String getName() {
		 return "pmi";

	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/pmi [reload|clear|clearstored]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 0 && args[0].equals("reload")) {
			perdimesioninv.instance.inventoryHandler.syncConfig();
			sender.sendMessage((ITextComponent)new TextComponentString("Reloaded Dimension Inventories Config Settings..."));
            return;
        }
		/*
		if (args.length == 1 && args[0].startsWith("clear")) {
			//clear current players stored inventory from nbt
			EntityPlayerMP en = (EntityPlayerMP) sender.getCommandSenderEntity();
	    	 NBTTagCompound  compound = (NBTTagCompound) ((NBTTagCompound) en.getEntityData().getTag("PlayerPersisted")).getTag("DimensionInventorys");
	    	 SavedInventory inv = new SavedInventory( en.dimension );
	    	 inv.clearNBT(en);
	    	 if (args[0].equals("clear")) {
	    		 en.inventory.clear();
	    	 }
		}
		
		if (args.length == 2 && args[0].startsWith("clear")) {
			//clear current players stored inventory from nbt
			EntityPlayerMP en = (EntityPlayerMP) sender.getCommandSenderEntity();
	    	 NBTTagCompound  compound = (NBTTagCompound) ((NBTTagCompound) en.getEntityData().getTag("PlayerPersisted")).getTag("DimensionInventorys");
	    	 SavedInventory inv = new SavedInventory( Integer.parseInt(args[1]) );
	    	 inv.clearNBT(en);
	    	 if (args[0].equals("clear")) {
	    		 en.inventory.clear();
	    	 }
		}
		
		if (args.length == 3 && args[0].startsWith("clear")) {
			
			//clear current players stored inventory from nbt
			PlayerList plist = sender.getServer().getPlayerList();
			EntityPlayerMP en = plist.getPlayerByUsername(args[3]);
	    	 NBTTagCompound  compound = (NBTTagCompound) ((NBTTagCompound) en.getEntityData().getTag("PlayerPersisted")).getTag("DimensionInventorys");
	    	 SavedInventory inv = new SavedInventory( Integer.parseInt(args[1]) );
	    	 inv.clearNBT(en);
	    	 if (args[0].equals("clear")) {
	    		 en.inventory.clear();
	    	 }
		}
		
		/*
		if (args.length == 2 && args[0].equals("tp")) {
			EntityPlayerMP en = (EntityPlayerMP) sender.getCommandSenderEntity();
			 en.dismountRidingEntity();
		     en.removePassengers();
		     
		     if (en.dimension != Integer.parseInt(args[2])) {
		    	 WorldServer worldDst = en.getServer().getWorld(Integer.parseInt(args[2]));
		    	 if (worldDst == null)
		         {
		    		 sender.sendMessage((ITextComponent)new TextComponentString("Could not find world"));
		         }
		    	 en.changeDimension(Integer.parseInt(args[2]));
		    	 NBTTagCompound  compound = (NBTTagCompound) ((NBTTagCompound) en.getEntityData().getTag("PlayerPersisted")).getTag("DimensionInventorys");
		    	 if(compound.hasKey("lastPos")) {
		    		 int[] pos = compound.getIntArray("lastPos");
		    	 en.setPosition(pos[0], pos[1], pos[2]);
		    	 }
		     }else {
		    	// en.setPosition(x, y, z);
		    	 
		     }
		}
		 */
	}



}
