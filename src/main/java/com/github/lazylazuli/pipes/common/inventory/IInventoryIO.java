package com.github.lazylazuli.pipes.common.inventory;

import com.github.lazylazuli.pipes.common.util.EnumIO;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IInventoryIO extends ISidedInventory
{
	EnumIO getIO();
	
	default EnumFacing getInput()
	{
		return getIO().getInput();
	}
	
	default EnumFacing getOutput()
	{
		return getIO().getOutput();
	}
	
	default boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		return direction == getInput();
	}
	
	default boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return direction == getOutput();
	}
}
