package com.github.lazylazuli.pipes.common.tile;

import com.github.lazylazuli.lib.common.inventory.InventoryUtils;
import com.github.lazylazuli.pipes.common.inventory.INameableInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

public abstract class TileInventory extends TileEntity implements INameableInventory
{
	private final NonNullList<ItemStack> inventory;
	
	private String customName = "";
	
	public TileInventory(int size)
	{
		inventory = NonNullList.withSize(size, ItemStack.EMPTY);
	}
	
	public NonNullList<ItemStack> getItems()
	{
		return inventory;
	}
	
	// IINVENTORY
	
	@Override
	public int getSizeInventory()
	{
		return inventory.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return InventoryUtils.isInventoryEmpty(this);
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return inventory.get(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return ItemStackHelper.getAndSplit(inventory, index, count);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(inventory, index);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		inventory.set(index, stack);
		
		if (stack.getCount() > getInventoryStackLimit())
		{
			stack.setCount(getInventoryStackLimit());
		}
		
		markDirty();
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getField(int id)
	{
		return 0;
	}
	
	@Override
	public void setField(int id, int value)
	{
	
	}
	
	@Override
	public int getFieldCount()
	{
		return 0;
	}
	
	@Override
	public void clear()
	{
		inventory.clear();
	}
	
	@Override
	public String getCustomName()
	{
		return customName;
	}
	
	@Override
	public void setCustomName(String name)
	{
		customName = name;
	}
	
	@Override
	public void openInventory(EntityPlayer player) {}
	
	@Override
	public void closeInventory(EntityPlayer player) {}
	
	// TILE ENTITY
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		ItemStackHelper.loadAllItems(compound, inventory);
		
		if (compound.hasKey("Fields"))
		{
			int[] fields = compound.getIntArray("Fields");
			
			for (int i = 0; i < fields.length; i++)
			{
				setField(i, fields[i]);
			}
		}
		
		if (compound.hasKey("CustomName", 8))
		{
			customName = compound.getString("CustomName");
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		ItemStackHelper.saveAllItems(compound, inventory);
		
		if (getFieldCount() > 0)
		{
			int[] fields = new int[getFieldCount() + 1];
			
			for (int i = 0; i < fields.length; i++)
			{
				fields[i] = getField(i);
			}
			
			compound.setIntArray("Fields", fields);
		}
		
		if (hasCustomName())
		{
			compound.setString("CustomName", getCustomName());
		}
		
		return compound;
	}
}
