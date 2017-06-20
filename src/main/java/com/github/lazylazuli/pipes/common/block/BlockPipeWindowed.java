package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.lib.common.block.state.BlockState;
import com.github.lazylazuli.pipes.common.Pipes;
import com.github.lazylazuli.pipes.common.util.EnumIO;
import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.github.lazylazuli.pipes.common.util.EnumIO.DU;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPipeWindowed extends BlockPipeBase
{
	public BlockPipeWindowed(String name)
	{
		super(name);
		setDefaultState(blockState.getBaseState()
								  .withProperty(getFlowProperty(), DU));
	}
	
	@Override
	public boolean isAssociatedBlock(Block other)
	{
		return other == this;
	}
	
	@Nullable
	@Override
	public BlockPipeNormal getAssociatedBlock()
	{
		return null;
	}
	
	@Override
	public PropertyEnum<EnumIO> getFlowProperty()
	{
		return FLOW_STRAIGHT;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return IBlockIO.constructStateFromFlow(this, EnumIO.values()[meta * 5]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(getFlowProperty())
					.getId() / 5;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
											float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return IBlockIO.constructStateFromFlow(this, EnumIO.fromFacing(facing.getOpposite()));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand
			hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (!worldIn.isRemote)
		{
			playerIn.openGui(Pipes.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Override
	protected BlockState createBlockState(ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn)
	{
		return new StateStraight(this, propertiesIn);
	}
}
