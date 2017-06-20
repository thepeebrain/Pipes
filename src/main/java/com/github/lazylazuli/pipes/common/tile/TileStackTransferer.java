package com.github.lazylazuli.pipes.common.tile;

import com.github.lazylazuli.pipes.common.inventory.IInventoryIO;
import net.minecraft.block.BlockHopper;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.github.lazylazuli.lib.common.inventory.InventoryUtils.canExtractItemFromSlot;
import static com.github.lazylazuli.lib.common.inventory.InventoryUtils.getInventoryAtPosition;
import static com.github.lazylazuli.lib.common.inventory.InventoryUtils.insertStack;
import static com.github.lazylazuli.lib.common.inventory.InventoryUtils.isInventoryEmpty;
import static com.github.lazylazuli.lib.common.inventory.InventoryUtils.isInventoryFull;

public abstract class TileStackTransferer extends TileInventory implements IHopper, IInventoryIO, ITickable
{
	public static final int FIELD_TRANSFER_COOLDOWN = 0;
	
	protected int transferCooldown = -1;
	protected long tickedGameTime;
	
	public TileStackTransferer(int size)
	{
		super(size);
	}
	
	@Override
	public void update()
	{
		if (world != null && !world.isRemote)
		{
			--transferCooldown;
			tickedGameTime = world.getTotalWorldTime();
			
			if (!isOnTransferCooldown())
			{
				setField(FIELD_TRANSFER_COOLDOWN, 0);
				
				boolean flag = false;
				
				if (!isEmpty())
				{
					flag = transferItemsOut();
				}

//		if (!isInventoryFull(this))
//		{
//			flag = pullItem() || flag;
//		}
				
				if (flag)
				{
					setField(FIELD_TRANSFER_COOLDOWN, 8);
					markDirty();
				}
			}
		}
	}
	
	public int getTransferCooldown()
	{
		return transferCooldown;
	}
	
	public long getLastUpdateTime() // Forge
	{
		return tickedGameTime;
	}
	
	public boolean isOnTransferCooldown()
	{
		return transferCooldown > 0;
	}
	
	public IPosition getBlockCenter()
	{
		return new PositionImpl(getXPos() + 0.5D, getYPos() + 0.5D, getZPos() + 0.5D);
	}
	
	public IPosition getBlockCenterWithOffset(EnumFacing side, double factor)
	{
		double x = getXPos() + (factor * (double) side.getFrontOffsetX());
		double y = getYPos() + (factor * (double) side.getFrontOffsetY());
		double z = getZPos() + (factor * (double) side.getFrontOffsetZ());
		
		return new PositionImpl(x, y, z);
	}
	
	// IInventory
	
	@Override
	public int getField(int id)
	{
		switch (id)
		{
		default:
			return 0;
		case FIELD_TRANSFER_COOLDOWN:
			return transferCooldown;
		}
	}
	
	@Override
	public void setField(int id, int value)
	{
		switch (id)
		{
		default:
			return;
		case FIELD_TRANSFER_COOLDOWN:
			transferCooldown = value;
			break;
		}
	}
	
	@Override
	public int getFieldCount()
	{
		return 1;
	}
	
	// IHopper
	
	@Override
	public double getXPos()
	{
		return pos.getX() + 0.5D;
	}
	
	@Override
	public double getYPos()
	{
		return pos.getY() + 0.5D;
	}
	
	@Override
	public double getZPos()
	{
		return pos.getZ() + 0.5D;
	}
	
	// Dispensing
	
	protected boolean transferItemsOut()
	{
		EnumFacing output = getOutput();
		IInventory inv = getInventoryAtSide(output);
		
		if (inv == null)
		{
			dispenseStack(output, 0, 1, 2);
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
	
	protected void dispenseStack(EnumFacing side, int index, int amount, int speed)
	{
		IPosition pos = getBlockCenterWithOffset(side, 0.7D);
		
		ItemStack stack = getStackInSlot(index);
		ItemStack dispenseStack = stack.splitStack(amount);
		
		dispenseStack(dispenseStack, side, pos, speed);
		
		setInventorySlotContents(index, stack);
	}
	
	private void dispenseStack(ItemStack stack, EnumFacing output, IPosition position, int speed)
	{
		World world = getWorld();
		
		double d0 = position.getX();
		double d1 = position.getY();
		double d2 = position.getZ();
		
		EntityItem entityitem = new EntityItem(world, d0, d1, d2, stack);
		
		double d3 = world.rand.nextDouble() * 0.1D + 0.2D;
		
		entityitem.motionX = (double) output.getFrontOffsetX() * d3;
		entityitem.motionY = (double) output.getFrontOffsetY() * d3;
		entityitem.motionZ = (double) output.getFrontOffsetZ() * d3;
		entityitem.motionX += world.rand.nextGaussian() * 0.007499999832361937D * speed;
		entityitem.motionY += world.rand.nextGaussian() * 0.007499999832361937D * speed;
		entityitem.motionZ += world.rand.nextGaussian() * 0.007499999832361937D * speed;
		
		world.spawnEntity(entityitem);
	}
	
	// Pulling
	
	protected boolean pullItem()
	{
		EnumFacing input = getInput();
		IInventory inv = getInventoryAtSide(input);
		inv = inv instanceof TileEntityHopper || inv instanceof TilePipe ? inv : null;
		
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
			
			if (inv instanceof TilePipe)
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
	
	//
	
	private boolean putStackInInventoryAllSlots(IInventory invFrom, IInventory invTo, ItemStack stack, @Nullable
			EnumFacing side)
	{
		boolean wasEmpty = invTo.isEmpty();
		boolean didTransfer = insertStack(invTo, stack, side);
		
		if (didTransfer && wasEmpty)
		{
			updateCooldowns(invFrom, invTo);
		}
		
		return didTransfer;
	}
	
	private void updateCooldowns(IInventory invFrom, IInventory invTo)
	{
		if (invTo instanceof TileEntityHopper || invTo instanceof TilePipe)
		{
			boolean mayTransfer = mayTransfer(invTo);
			
			if (!mayTransfer)
			{
				int k = 0;
				
				if (invFrom != null && (invFrom instanceof TileEntityHopper || invFrom instanceof TilePipe))
				{
					long updateTime = getLastUpdateTime(invFrom);
					long updateTime1 = getLastUpdateTime(invTo);
					
					if (updateTime1 >= updateTime)
					{
						k = 1;
					}
				}
				
				if (invTo instanceof TilePipe)
				{
					invTo.setField(FIELD_TRANSFER_COOLDOWN, 8 - k);
				} else
				{
					((TileEntityHopper) invTo).setTransferCooldown(8 - k);
				}
			}
		}
	}
	
	private boolean mayTransfer()
	{
		return getTransferCooldown() > 8;
	}
	
	public IInventory getInventoryAtSide(EnumFacing side)
	{
		return getInventoryAtPosition(
				getWorld(),
				getXPos() + side.getFrontOffsetX(),
				getYPos() + side.getFrontOffsetY(),
				getZPos() + side.getFrontOffsetZ()
		);
	}
	
	public static boolean mayTransfer(IInventory inv)
	{
		if (inv instanceof TileEntityHopper)
		{
			return ((TileEntityHopper) inv).mayTransfer();
		}
		
		return inv instanceof TileStackTransferer && ((TileStackTransferer) inv).mayTransfer();
	}
	
	public static long getLastUpdateTime(IInventory inv)
	{
		if (inv instanceof TileEntityHopper)
		{
			return ((TileEntityHopper) inv).getLastUpdateTime();
		}
		
		return inv instanceof TileStackTransferer ? ((TileStackTransferer) inv).getLastUpdateTime() : -1;
	}
}
