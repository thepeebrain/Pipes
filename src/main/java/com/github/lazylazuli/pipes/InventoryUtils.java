package com.github.lazylazuli.pipes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class InventoryUtils
{
	public static IInventory getInventoryAtPosition(World world, double x, double y, double z)
	{
		IInventory inv = null;
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);
		BlockPos blockpos = new BlockPos(i, j, k);
		IBlockState state = world.getBlockState(blockpos);
		Block block = state.getBlock();
		
		if (block.hasTileEntity(state))
		{
			TileEntity te = world.getTileEntity(blockpos);
			
			if (te instanceof IInventory)
			{
				inv = (IInventory) te;
				
				if (inv instanceof TileEntityChest && block instanceof BlockChest)
				{
					inv = ((BlockChest) block).getContainer(world, blockpos, true);
				}
			}
		}
		
		if (inv == null)
		{
			List<Entity> list = world.getEntitiesInAABBexcluding(null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D,
					x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.HAS_INVENTORY);
			
			if (!list.isEmpty())
			{
				inv = (IInventory) list.get(world.rand.nextInt(list.size()));
			}
		}
		
		return inv;
	}
	
	public static boolean canExtractItemFromSlot(IInventory inv, ItemStack stack, int index, EnumFacing side)
	{
		return !(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canExtractItem(index, stack, side);
	}
	
	public static boolean isInventoryEmpty(IInventory inv)
	{
		int size = inv.getSizeInventory();
		
		for (int i = 0; i < size; ++i)
		{
			if (!isInventorySlotEmpty(inv, i))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isInventoryEmpty(IInventory inv, EnumFacing side)
	{
		if (inv instanceof ISidedInventory)
		{
			ISidedInventory sidedInv = (ISidedInventory) inv;
			int[] aint = sidedInv.getSlotsForFace(side);
			
			for (int i : aint)
			{
				if (!isInventorySlotEmpty(inv, i))
				{
					return false;
				}
			}
			
			return true;
		}
		
		return isInventoryEmpty(inv);
	}
	
	public static boolean isInventorySlotEmpty(IInventory inv, int index)
	{
		return inv.getStackInSlot(index)
				  .isEmpty();
	}
	
	public static boolean isInventoryFull(IInventory inv)
	{
		int size = inv.getSizeInventory();
		
		for (int i = 0; i < size; ++i)
		{
			if (!isInventorySlotFull(inv, i))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isInventoryFull(IInventory inv, EnumFacing side)
	{
		if (inv instanceof ISidedInventory)
		{
			ISidedInventory sidedInv = (ISidedInventory) inv;
			int[] aint = sidedInv.getSlotsForFace(side);
			
			for (int i : aint)
			{
				if (!isInventorySlotFull(inv, i))
				{
					return false;
				}
			}
			
			return true;
		}
		
		return isInventoryFull(inv);
	}
	
	public static boolean isInventorySlotFull(IInventory inv, int index)
	{
		ItemStack stack = inv.getStackInSlot(index);
		return !stack.isEmpty() && stack.getCount() == stack.getMaxStackSize();
	}
	
	public static boolean canInsertItemInSlot(IInventory inv, ItemStack stack, int index, EnumFacing side)
	{
		return inv.isItemValidForSlot(index, stack) && (!(inv instanceof ISidedInventory) || ((ISidedInventory) inv)
				.canInsertItem(index, stack, side));
	}
	
	public static boolean insertStack(IInventory invTo, ItemStack stack, @Nullable EnumFacing side)
	{
		boolean stackInserted = false;
		
		if (invTo instanceof ISidedInventory && side != null)
		{
			ISidedInventory isidedinventory = (ISidedInventory) invTo;
			int[] aint = isidedinventory.getSlotsForFace(side);
			
			for (int i = 0; i < aint.length && !stack.isEmpty(); ++i)
			{
				stackInserted = insertStack(invTo, stack, aint[i], side);
			}
		} else
		{
			int size = invTo.getSizeInventory();
			
			for (int i = 0; i < size && !stack.isEmpty(); ++i)
			{
				stackInserted = insertStack(invTo, stack, i, side);
			}
		}
		
		return stackInserted;
	}
	
	public static boolean insertStack(IInventory inv, ItemStack stack, int index, EnumFacing side)
	{
		ItemStack stackInSlot = inv.getStackInSlot(index);
		boolean stackInserted = false;
		
		if (canInsertItemInSlot(inv, stack, index, side))
		{
			if (stackInSlot.isEmpty())
			{
				inv.setInventorySlotContents(index, stack.copy());
				stack.setCount(0);
				stackInserted = true;
			} else if (canCombine(stackInSlot, stack))
			{
				int spaceLeft = Math.min(stackInSlot.getMaxStackSize(), inv.getInventoryStackLimit()) - stackInSlot
						.getCount();
				if (spaceLeft > 0)
				{
					int i = Math.min(stack.getCount(), spaceLeft);
					stack.shrink(i);
					stackInSlot.grow(i);
					stackInserted = i > 0;
				}
			}
		}
		
		return stackInserted;
	}
	
	public static boolean canCombine(ItemStack stack1, ItemStack stack2)
	{
		if (stack1.getItem() == stack2.getItem())
			if (stack1.getMetadata() == stack2.getMetadata())
				if (stack1.getCount() <= stack1.getMaxStackSize())
					if (ItemStack.areItemStackTagsEqual(stack1, stack2))
						return true;
		return false;
	}
}
