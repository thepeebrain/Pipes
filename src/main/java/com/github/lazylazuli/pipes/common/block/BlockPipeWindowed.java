package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.lib.common.block.BlockBase;
import com.github.lazylazuli.lib.common.block.state.BlockState;
import com.github.lazylazuli.pipes.common.EnumFlow;
import com.github.lazylazuli.pipes.common.Pipes;
import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.github.lazylazuli.pipes.common.EnumFlow.DU;
import static com.github.lazylazuli.pipes.common.EnumFlow.EW;
import static com.github.lazylazuli.pipes.common.EnumFlow.NS;
import static com.github.lazylazuli.pipes.common.EnumFlow.SN;
import static com.github.lazylazuli.pipes.common.EnumFlow.UD;
import static com.github.lazylazuli.pipes.common.EnumFlow.WE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPipeWindowed extends BlockPipe
{
	public static final PropertyEnum<EnumFlow> FLOW = PropertyEnum.create("flow", EnumFlow.class,
			DU, NS, WE, UD, SN, EW
	);
	
	public BlockPipeWindowed(String name)
	{
		super(name);
		setDefaultState(blockState.getBaseState()
								  .withProperty(getFlowProperty(), DU));
	}
	
	@Override
	public PropertyEnum<EnumFlow> getFlowProperty()
	{
		return FLOW;
	}
	
	@Override
	protected IBlockState constructStateFromFlow(EnumFlow flow)
	{
		if (!FLOW.getAllowedValues()
				 .contains(flow))
			flow = NS;
		return getDefaultState().withProperty(FLOW, flow);
	}
	
	@Override
	public boolean isAssociatedBlock(Block other)
	{
		return this == other;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return constructStateFromFlow(EnumFlow.values()[meta * 5]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FLOW)
					.ordinal() / 5;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		EnumFacing out = facing;
		EnumFacing in = facing.getOpposite();
		return constructStateFromFlow(EnumFlow.get(in, out));
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
		return new BlockPipeWindowed.State(this, propertiesIn);
	}
	
	private class State extends BlockPipe.State
	{
		public State(BlockBase blockIn,
				ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn)
		{
			super(blockIn, propertiesIn);
		}
		
		@Override
		public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos)
		{
			switch (getValue(FLOW))
			{
			default:
			case DU:
			case UD:
				return Y_AABB;
			case WE:
			case EW:
				return Z_AABB;
			case NS:
			case SN:
				return X_AABB;
			}
		}
	}
}
