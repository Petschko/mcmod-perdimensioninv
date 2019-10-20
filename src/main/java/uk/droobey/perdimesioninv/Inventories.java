package uk.droobey.perdimesioninv;

public class Inventories {

	public SavedInventory originInventory = null;
    public SavedInventory destinationInventory = null;
    
    public int originInventoryIndex = -1;
	public int destinationInventoryIndex = -1;
	
	public Inventories(SavedInventory originInventory,SavedInventory destinationInventory,int originInventoryIndex,int destinationInventoryIndex) {
		
	
		this.originInventory=originInventory;
		this.destinationInventory=destinationInventory;
		
		this.originInventoryIndex=originInventoryIndex;
		this.destinationInventoryIndex=destinationInventoryIndex;
	}
}

