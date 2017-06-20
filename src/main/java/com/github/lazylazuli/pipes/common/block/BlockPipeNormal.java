package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.lib.common.block.state.BlockState;
import com.github.lazylazuli.pipes.common.PipeObjects;
import com.github.lazylazuli.pipes.common.tile.TilePipe;
import com.github.lazylazuli.pipes.common.util.EnumIO;
import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPipeNormal extends BlockPipeBase implements IBlockPipe, ITileEntityProvider
{
	BlockPipeNormal(String name, String unlocalizedName)
	{
		super(name, unlocalizedName);
	}
	
	public BlockPipeNormal(String name)
	{
		this(name, name);
	}
	
	@Override
	public PropertyEnum<EnumIO> getFlowProperty()
	{
		return FLOW_NORMAL;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public BlockPipeNormal getAssociatedBlock()
	{
		return PipeObjects.PIPE_REVERSE;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
											float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		EnumFacing out = EnumFacing.getDirectionFromEntityLiving(pos, placer);
		EnumFacing in = facing.getOpposite();
		return IBlockIO.constructStateFromFlow(this, EnumIO.fromFacing(in, out));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand
			hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (playerIn.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY)
		{
			return false;
		}
		
		if (worldIn.isRemote)
		{
			return true;
		}
		
		EnumIO flow = EnumIO.fromFacing(IBlockIO.getInput(state), facing);
		
		worldIn.setBlockState(pos, IBlockIO.constructStateFromFlow(this, flow));
		
		return true;
	}
	
	@Override
	public boolean isAssociatedBlock(Block other)
	{
		return other == this || other == getAssociatedBlock();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return IBlockIO.constructStateFromFlow(this, EnumIO.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumIO flow = state.getValue(((BlockPipeNormal) state.getBlock()).getFlowProperty());
		return flow.getId() % 15;
	}
	
	@Override
	protected IProperty<?>[] getProperties()
	{
		return new IProperty[] { getFlowProperty() };
	}
	
	@Override
	protected BlockState createBlockState(ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn)
	{
		return new BlockPipeNormal.State(this, propertiesIn);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TilePipe();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);
		
		if (tileentity instanceof TilePipe)
		{
			InventoryHelper.dropInventoryItems(worldIn, pos, (TilePipe) tileentity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}
		
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack
			stack)
	{
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		if (stack.hasDisplayName())
		{
			TileEntity te = worldIn.getTileEntity(pos);
			
			if (te instanceof TilePipe)
			{
				((TilePipe) te).setCustomName(stack.getDisplayName());
			}
		}
	}
}
