package uk.droobey.perdimesioninv;

	import java.util.HashSet;
	import net.minecraft.nbt.NBTTagCompound;

	public class InventoryGroup
	{
	  HashSet<Integer> dimensions;
	  
	  public InventoryGroup()
	  {
	    this.dimensions = new HashSet();
	  }
	  
	  public void writeToNBT(NBTTagCompound nbt)
	  {
	    int dimensionIndex = 0;
	    for (Integer dimension : this.dimensions)
	    {
	      nbt.setInteger("dimension" + dimensionIndex, dimension.intValue());
	      dimensionIndex++;
	    }
	  }
	  
	  public void readFromNBT(NBTTagCompound nbt)
	  {
	    int dimensionIndex = 0;
	    
	    while (nbt.hasKey("dimension" + dimensionIndex))
	    {
	      this.dimensions.add(Integer.valueOf(nbt.getInteger("dimension" + dimensionIndex)));
	      dimensionIndex++;
	    }
	  }
	  
	  public void addDimension(int dimension)
	  {
	    this.dimensions.add(Integer.valueOf(dimension));
	  }
	  
	  public void removeDimension(int dimension)
	  {
	    this.dimensions.remove(Integer.valueOf(dimension));
	  }
	  
	  public boolean containsDimension(int dimension)
	  {
	    return this.dimensions.contains(Integer.valueOf(dimension));
	  }
	  
	  public int getDimensionHash()
	  {
	    return this.dimensions.hashCode();
	  }
	  
	  public int hashCode()
	  {
	    int prime = 31;
	    int result = 1;
	    result = 31 * result + (this.dimensions == null ? 0 : this.dimensions.hashCode());
	    return result;
	  }
	  
	  
	  public String toString()
	  {
		  String o="";
		  boolean f=true;
	
		  for (Integer dimension : this.dimensions)
		    {
			  if(f) {
				  o=o+dimension;
				  f=false;
			  }else {
		    o=o+","+dimension;
			  }
		    }
		  return o;
	  }
	  
	  public boolean equals(Object obj)
	  {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	    InventoryGroup other = (InventoryGroup)obj;
	    if (this.dimensions == null)
	    {
	      if (other.dimensions != null) {
	        return false;
	      }
	    } else if (!this.dimensions.equals(other.dimensions))
	      return false;
	    return true;
	  }
	}

	

