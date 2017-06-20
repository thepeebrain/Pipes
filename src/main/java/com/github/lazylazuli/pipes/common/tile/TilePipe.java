package com.github.lazylazuli.pipes.common.tile;

import com.github.lazylazuli.pipes.common.block.IBlockIO;
import com.github.lazylazuli.pipes.common.util.EnumIO;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TilePipe extends TileStackTransferer
{
	public TilePipe()
	{
		super(1);
	}
	
	/*
	TileEntity
	 */
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return !oldState.getBlock()
						.isAssociatedBlock(newSate.getBlock());
	}
	
	// TileInventory
	
	@Override
	public IPosition getBlockCenterWithOffset(EnumFacing side, double factor)
	{
		double x = pos.getX() + 0.5D + (factor * (double) side.getFrontOffsetX());
		double y = pos.getY() + 0.5D + (factor * (double) side.getFrontOffsetY());
		double z = pos.getZ() + 0.5D + (factor * (double) side.getFrontOffsetZ());
		
		
		if (side.getAxis() != EnumFacing.Axis.Y)
		{
			y -= 0.4125D;
		}
		
		return new PositionImpl(x, y, z);
	}
	
	/*
	IInventory
	 */
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		super.setInventorySlotContents(index, stack);
		setField(FIELD_TRANSFER_COOLDOWN, 8);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 16;
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}
	
	// ISidedInventory
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return side == getInput() || side == getOutput() ? new int[1] : new int[0];
	}
	
	// IInventoryIO
	
	@Override
	public EnumIO getIO()
	{
		return IBlockIO.getIO(world.getBlockState(pos));
	}
	
	// INameableInventory
	
	@Override
	public String getDefaultName()
	{
		return "container.pipe";
	}
}