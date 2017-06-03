package com.github.lazylazuli.pipes;

import net.minecraft.block.BlockHopper;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

import static com.github.lazylazuli.pipes.InventoryUtils.canExtractItemFromSlot;
import static com.github.lazylazuli.pipes.InventoryUtils.getInventoryAtPosition;
import static com.github.lazylazuli.pipes.InventoryUtils.isInventoryEmpty;
import static com.github.lazylazuli.pipes.InventoryUtils.isInventoryFull;

public class TileEntityPipe extends TileEntity implements IHopper, ITickable, ISidedInventory
{
	private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long tickedGameTime;
	
	public EnumFacing getInput()
	{
		return BlockPipe.getInput(world.getBlockState(pos));
	}
	
	public EnumFacing getOutput()
	{
		return BlockPipe.getOutput(world.getBlockState(pos));
	}
	
	public void update()
	{
		if (world != null && !world.isRemote)
		{
			--transferCooldown;
			tickedGameTime = world.getTotalWorldTime();
			
			if (!isOnTransferCooldown())
			{
				setTransferCooldown(0);
				updatePipe();
			}
		}
	}
	
	private boolean updatePipe()
	{
		boolean flag = false;
		
		if (!isEmpty())
		{
			flag = transferItemsOut(getOutput());
		}

//		if (!isInventoryFull(this))
//		{
//			flag = pullItem(getInput()) || flag;
//		}
		
		if (flag)
		{
			setTransferCooldown(8);
			markDirty();
			return true;
		}
		
		return false;
	}
	
	private boolean transferItemsOut(EnumFacing output)
	{
		IInventory inv = getInventoryAtSide(this, output);
		
		if (inv == null)
		{
			dispenseStack(output);
			return true;
		} else
		{
			EnumFacing side = output.getOpposite();
			
			if (isInventoryFull(inv, side))
			{
				return false;
			} else
			{
				ItemStack stack = getStackInSlot(0);
				if (!stack.isEmpty())
				{
					stack = stack.copy();
					
					if (putStackInInventoryAllSlots(this, inv, decrStackSize(0, 1), side))
					{
						inv.markDirty();
						return true;
					}
					
					setInventorySlotContents(0, stack);
				}
				
				return false;
			}
		}
	}
	
	private void dispenseStack(EnumFacing output)
	{
		double d0 = pos.getX() + 0.5D + (0.7D * (double) output.getFrontOffsetX());
		double d1 = pos.getY() + 0.5D + (0.7D * (double) output.getFrontOffsetY());
		double d2 = pos.getZ() + 0.5D + (0.7D * (double) output.getFrontOffsetZ());
		IPosition iposition = new PositionImpl(d0, d1, d2);
		ItemStack stack = getStackInSlot(0);
		ItemStack dispenseStack = stack.splitStack(1);
		
		doDispense(dispenseStack, output, iposition);
		
		setInventorySlotContents(0, stack);
	}
	
	private void doDispense(ItemStack stack, EnumFacing output, IPosition position)
	{
		double d0 = position.getX();
		double d1 = position.getY();
		double d2 = position.getZ();
		
		if (output.getAxis() == EnumFacing.Axis.Y)
		{
			d1 = d1 - 0.125D;
		} else
		{
			d1 = d1 - 0.4125D;
		}
		
		EntityItem entityitem = new EntityItem(world, d0, d1, d2, stack);
		double d3 = world.rand.nextDouble() * 0.1D + 0.2D;
		double speed = 2;
		entityitem.motionX = (double) output.getFrontOffsetX() * d3;
		entityitem.motionY = (double) output.getFrontOffsetY() * d3;
		entityitem.motionZ = (double) output.getFrontOffsetZ() * d3;
		entityitem.motionX += world.rand.nextGaussian() * 0.007499999832361937D * speed;
		entityitem.motionY += world.rand.nextGaussian() * 0.007499999832361937D * speed;
		entityitem.motionZ += world.rand.nextGaussian() * 0.007499999832361937D * speed;
		world.spawnEntity(entityitem);
	}
	
	public boolean pullItem(EnumFacing input)
	{
		IInventory inv = getValidInventoryAtInput(this, input);
		
		if (inv != null)
		{
			if (isInventoryEmpty(inv, input.getOpposite()))
			{
				return false;
			}
			
			if (inv instanceof TileEntityHopper)
			{
				int meta = ((TileEntityHopper) inv).getBlockMetadata();
				if (BlockHopper.isEnabled(meta) && BlockHopper.getFacing(meta) == input.getOpposite())
				{
					int size = inv.getSizeInventory();
					
					for (int i = 0; i < size; ++i)
					{
						if (pullItemFromSlot(inv, i, input.getOpposite()))
						{
							inv.markDirty();
							return true;
						}
					}
				}
			}
			
			if (inv instanceof TileEntityPipe)
			{
				ISidedInventory sidedInv = (ISidedInventory) inv;
				int[] aint = sidedInv.getSlotsForFace(input.getOpposite());
				
				for (int i : aint)
				{
					if (pullItemFromSlot(inv, i, input.getOpposite()))
					{
						inv.markDirty();
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean pullItemFromSlot(IInventory inv, int index, EnumFacing side)
	{
		ItemStack itemstack = inv.getStackInSlot(index);
		
		if (!itemstack.isEmpty() && canExtractItemFromSlot(inv, itemstack, index, side))
		{
			ItemStack itemstack1 = itemstack.copy();
			
			if (putStackInInventoryAllSlots(inv, this, inv.decrStackSize(index, 1), side.getOpposite()))
			{
				return true;
			}
			
			inv.setInventorySlotContents(index, itemstack1);
		}
		
		return false;
	}
	
	public boolean putStackInInventoryAllSlots(IInventory invFrom, IInventory invTo, ItemStack stack, @Nullable
			EnumFacing side)
	{
		boolean wasEmpty = invTo.isEmpty();
		boolean didTransfer = InventoryUtils.insertStack(invTo, stack, side);
		
		if (didTransfer && wasEmpty)
		{
			updateCooldowns(invFrom, invTo);
		}
		
		return didTransfer;
	}
	
	private static boolean mayTransfer(IInventory inv)
	{
		return inv instanceof TileEntityHopper ? ((TileEntityHopper) inv).mayTransfer() : inv instanceof
				TileEntityPipe && ((TileEntityPipe) inv).mayTransfer();
	}
	
	private static long getLastUpdateTime(IInventory inv)
	{
		return inv instanceof TileEntityHopper ? ((TileEntityHopper) inv).getLastUpdateTime() : inv instanceof
																										TileEntityPipe
																								? ((TileEntityPipe)
				inv).getLastUpdateTime() : -1;
	}
	
	private void updateCooldowns(IInventory invFrom, IInventory invTo)
	{
		if (invTo instanceof TileEntityHopper || invTo instanceof TileEntityPipe)
		{
			boolean mayTransfer = mayTransfer(invTo);
			
			if (!mayTransfer)
			{
				int k = 0;
				
				if (invFrom != null && (invFrom instanceof TileEntityHopper || invFrom instanceof TileEntityPipe))
				{
					long updateTime = getLastUpdateTime(invFrom);
					long updateTime1 = getLastUpdateTime(invTo);
					
					if (updateTime1 >= updateTime)
					{
						k = 1;
					}
				}
				
				if (invTo instanceof TileEntityPipe)
				{
					((TileEntityPipe) invTo).setTransferCooldown(8 - k);
				} else
				{
					((TileEntityHopper) invTo).setTransferCooldown(8 - k);
				}
			}
		}
	}
	
	private static IInventory getValidInventoryAtInput(IHopper hopper, EnumFacing side)
	{
		IInventory inv = getInventoryAtSide(hopper, side);
		return inv instanceof TileEntityHopper || inv instanceof TileEntityPipe ? inv : null;
	}
	
	private static IInventory getInventoryAtSide(IHopper hopper, EnumFacing side)
	{
		return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos() + side.getFrontOffsetX(), hopper.getYPos() +
				side.getFrontOffsetY(), hopper.getZPos() + side.getFrontOffsetZ());
	}
	
	public double getXPos()
	{
		return pos.getX() + 0.5D;
	}
	
	public double getYPos()
	{
		return pos.getY() + 0.5D;
	}
	
	public double getZPos()
	{
		return pos.getZ() + 0.5D;
	}
	
	public void setTransferCooldown(int ticks)
	{
		transferCooldown = ticks;
	}
	
	private boolean isOnTransferCooldown()
	{
		return transferCooldown > 0;
	}
	
	private boolean mayTransfer()
	{
		return transferCooldown > 8;
	}
	
	protected NonNullList<ItemStack> getItems()
	{
		return inventory;
	}
	
	public long getLastUpdateTime() { return tickedGameTime; } // Forge
	
	// IINVENTORY
	
	@Override
	public int getSizeInventory()
	{
		return inventory.size();
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return ItemStackHelper.getAndSplit(getItems(), index, count);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		return ItemStackHelper.getAndRemove(getItems(), index);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		getItems().set(index, stack);
		
		if (stack.getCount() > getInventoryStackLimit())
		{
			stack.setCount(getInventoryStackLimit());
		}
		
		setTransferCooldown(8);
		markDirty();
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
		getItems().clear();
	}
	
	@Override
	public boolean isEmpty()
	{
		return isInventoryEmpty(this);
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return getItems().get(index);
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return side == getInput() || side == getOutput() ? new int[1] : new int[0];
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		return direction == getInput();
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return direction == getOutput();
	}
	
	// NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
		
		this.transferCooldown = compound.getInteger("TransferCooldown");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setInteger("TransferCooldown", this.transferCooldown);
		
		return compound;
	}
}