package com.github.lazylazuli.pipes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class TileEntityPipe extends TileEntity implements ISidedInventory, ITickable
{
	private NonNullList<ItemStack> pipeStack = NonNullList.withSize(1, ItemStack.EMPTY);
	
	private int transferCooldown = -1;
	
	@Override
	public void update()
	{
		if (world != null && !world.isRemote)
		{
			--transferCooldown;
			
			if (!isOnTransferCooldown())
			{
				setTransferCooldown(0);
				updateHopper();
			}
		}
	}
	
	private void updateHopper()
	{
		if (world != null && !world.isRemote)
		{
			if (!isOnTransferCooldown())
			{
				boolean flag = false;
				
				if (!isEmpty())
				{
					flag = transferItemsOut();
				}
				
				if (flag)
				{
					setTransferCooldown(8);
					markDirty();
					return;
				}
			}
			
			return;
		} else
		{
			return;
		}
	}
	
	private boolean transferItemsOut()
	{
		IInventory inventory = getInventoryForPipeTransfer();
		
		if (inventory == null)
		{
			dispenseStack();
			return true;
		} else
		{
			EnumFacing facing = BlockPipe.getOutput(world.getBlockState(pos));
			
			if (isInventoryFull(inventory, facing.getOpposite()))
			{
				return false;
			} else
			{
				for (int i = 0; i < getSizeInventory(); ++i)
				{
					if (!getStackInSlot(i).isEmpty())
					{
						ItemStack stack = getStackInSlot(i).copy();
						ItemStack stack1 = TileEntityHopper.putStackInInventoryAllSlots(this, inventory, decrStackSize
								(i, 1), facing.getOpposite());
						
						if (stack1.isEmpty())
						{
							inventory.markDirty();
							return true;
						}
						
						setInventorySlotContents(i, stack);
					}
				}
				
				return false;
			}
		}
	}
	
	private IInventory getInventoryForPipeTransfer()
	{
		BlockPipe block = (BlockPipe) getBlockType();
		EnumFacing facing = world.getBlockState(pos)
								 .getValue(block.flow)
								 .getOutput();
		return TileEntityHopper.getInventoryAtPosition(getWorld(), pos.getX() + (double) facing.getFrontOffsetX(), pos
				.getY() + (double) facing.getFrontOffsetY(), pos.getZ() + (double) facing.getFrontOffsetZ());
	}
	
	private void dispenseStack()
	{
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = BlockPipe.getOutput(state);
		IPosition iposition = getDispensePosition(state);
		ItemStack stack = getStackInSlot(0);
		ItemStack dispenseStack = stack.splitStack(1);
		doDispense(world, dispenseStack, facing, iposition);
		setInventorySlotContents(0, stack);
	}
	
	private IPosition getDispensePosition(IBlockState state)
	{
		EnumFacing facing = BlockPipe.getOutput(state);
		double d0 = pos.getX() + 0.5D + (0.7D * (double) facing.getFrontOffsetX());
		double d1 = pos.getY() + 0.5D + (0.7D * (double) facing.getFrontOffsetY());
		double d2 = pos.getZ() + 0.5D + (0.7D * (double) facing.getFrontOffsetZ());
		return new PositionImpl(d0, d1, d2);
	}
	
	private void doDispense(World worldIn, ItemStack stack, EnumFacing facing, IPosition position)
	{
		double d0 = position.getX();
		double d1 = position.getY();
		double d2 = position.getZ();
		
		if (facing.getAxis() == EnumFacing.Axis.Y)
		{
			d1 = d1 - 0.125D;
		} else
		{
			d1 = d1 - 0.4125D;
		}
		
		EntityItem entityitem = new EntityItem(worldIn, d0, d1, d2, stack);
		double d3 = worldIn.rand.nextDouble() * 0.1D + 0.2D;
		double speed = 2;
		entityitem.motionX = (double) facing.getFrontOffsetX() * d3;
		entityitem.motionY = (double) facing.getFrontOffsetY() * d3;
		entityitem.motionZ = (double) facing.getFrontOffsetZ() * d3;
		entityitem.motionX += worldIn.rand.nextGaussian() * 0.007499999832361937D * speed;
		entityitem.motionY += worldIn.rand.nextGaussian() * 0.007499999832361937D * speed;
		entityitem.motionZ += worldIn.rand.nextGaussian() * 0.007499999832361937D * speed;
		worldIn.spawnEntity(entityitem);
	}
	
	private boolean isInventoryFull(IInventory inventory, EnumFacing side)
	{
		if (inventory instanceof ISidedInventory)
		{
			ISidedInventory sided = (ISidedInventory) inventory;
			int[] aint = sided.getSlotsForFace(side);
			
			for (int i : aint)
				if (!isInventorySlotFull(inventory, i))
					return false;
		} else
		{
			int size = inventory.getSizeInventory();
			
			for (int i = 0; i < size; ++i)
				if (!isInventorySlotFull(inventory, i))
					return false;
		}
		
		return true;
	}
	
	private boolean isInventorySlotFull(IInventory inventory, int slot)
	{
		ItemStack stack = inventory.getStackInSlot(slot);
		
		return !stack.isEmpty() && stack.getCount() == stack.getMaxStackSize();
	}
	
	private boolean isOnTransferCooldown()
	{
		return this.transferCooldown > 0;
	}
	
	private void setTransferCooldown(int ticks)
	{
		transferCooldown = ticks;
	}
	
	/*
	NBT
	 */
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		pipeStack = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, pipeStack);
		
		transferCooldown = compound.getInteger("TransferCooldown");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		ItemStackHelper.saveAllItems(compound, pipeStack);
		
		compound.setInteger("TransferCooldown", transferCooldown);
		
		return compound;
	}
	
	/*
	IINVENTORY IMPLEMENTATION
	 */
	
	@Override
	public int getSizeInventory()
	{
		return pipeStack.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		for (ItemStack itemstack : pipeStack)
			if (!itemstack.isEmpty())
				return false;
		
		return true;
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return pipeStack.get(0);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return ItemStackHelper.getAndSplit(pipeStack, index, count);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(pipeStack, index);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		pipeStack.set(index, stack);
		
		if (stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{
	
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
	
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
		pipeStack.clear();
	}
	
	@Override
	public String getName()
	{
		return null;
	}
	
	@Override
	public boolean hasCustomName()
	{
		return false;
	}
	
	/*
	ISIDEDINVENTORY IMPLEMENTATION
	 */
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return new int[1];
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		BlockPipe blockPipe = (BlockPipe) getBlockType();
		return world.getBlockState(pos)
					.getValue(blockPipe.flow)
					.getInput() == direction;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return false;
	}
}
