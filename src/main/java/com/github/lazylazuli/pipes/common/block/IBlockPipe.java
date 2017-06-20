package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.lib.common.block.BlockBase;
import com.github.lazylazuli.lib.common.block.state.BlockState;
import com.github.lazylazuli.lib.common.block.state.BlockStateTile;
import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public interface IBlockPipe extends IBlockIO
{
	AxisAlignedBB AABB_DN = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.5D, 0.625D);
	AxisAlignedBB AABB_DS = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5D, 1.0D);
	
	AxisAlignedBB AABB_DW = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 0.5D, 0.625D);
	AxisAlignedBB AABB_DE = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 0.5D, 0.625D);
	
	AxisAlignedBB AABB_UN = new AxisAlignedBB(0.375D, 0.25D, 0.0D, 0.625D, 1.0D, 0.625D);
	AxisAlignedBB AABB_US = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.0D, 1.0D);
	
	AxisAlignedBB AABB_UW = new AxisAlignedBB(0.0D, 0.25D, 0.375D, 0.625D, 1.0D, 0.625D);
	AxisAlignedBB AABB_UE = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 1.0D, 1.0D, 0.625D);
	
	AxisAlignedBB AABB_SW = new AxisAlignedBB(0, 0.25D, 0.375D, 0.625D, 0.5D, 1.0D);
	AxisAlignedBB AABB_SE = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 1, 0.5D, 1.0D);
	
	AxisAlignedBB AABB_NW = new AxisAlignedBB(0, 0.25D, 0.0D, 0.625D, 0.5D, 0.625D);
	AxisAlignedBB AABB_NE = new AxisAlignedBB(0.375D, 0.25D, 0.0D, 1, 0.5D, 0.625D);
	
	AxisAlignedBB AABB_X = new AxisAlignedBB(0.375D, 0.25D, 0.0D, 0.625D, 0.5D, 1.0D);
	AxisAlignedBB AABB_Y = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
	AxisAlignedBB AABB_Z = new AxisAlignedBB(0.0D, 0.25D, 0.375D, 1.0D, 0.5D, 0.625D);
	
	@MethodsReturnNonnullByDefault
	@ParametersAreNonnullByDefault
	class State extends BlockState implements BlockStateTile
	{
		<B extends BlockBase & IBlockPipe> State(B blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn)
		{
			super(blockIn, propertiesIn);
		}
		
		IBlockPipe getBlockPipe()
		{
			return (IBlockPipe) getBlock();
		}
		
		@Override
		public AxisAlignedBB getBoundingBox(@Nullable IBlockAccess blockAccess, @Nullable BlockPos pos)
		{
			switch (getValue(getBlockPipe().getFlowProperty()))
			{
			default:
			case DU:
			case UD:
				return AABB_Y;
			case WE:
			case EW:
				return AABB_Z;
			case NS:
			case SN:
				return AABB_X;
			case NW:
			case WN:
				return AABB_NW;
			case NE:
			case EN:
				return AABB_NE;
			case SW:
			case WS:
				return AABB_SW;
			case SE:
			case ES:
				return AABB_SE;
			case DN:
			case ND:
				return AABB_DN;
			case DS:
			case SD:
				return AABB_DS;
			case DW:
			case WD:
				return AABB_DW;
			case DE:
			case ED:
				return AABB_DE;
			case UN:
			case NU:
				return AABB_UN;
			case US:
			case SU:
				return AABB_US;
			case UW:
			case WU:
				return AABB_UW;
			case UE:
			case EU:
				return AABB_UE;
			}
		}
		
		@Override
		public MapColor getMapColor(IBlockAccess blockAccess, BlockPos pos)
		{
			return MapColor.STONE;
		}
		
		@Override
		public boolean isFullCube()
		{
			return false;
		}
		
		@Override
		public boolean isOpaqueCube()
		{
			return false;
		}
		
		
		// COMPARATOR
		
		@Override
		public boolean hasComparatorInputOverride()
		{
			return true;
		}
		
		@Override
		public int getComparatorInputOverride(World worldIn, BlockPos pos)
		{
			return Container.calcRedstone(worldIn.getTileEntity(pos));
		}
		// TILE
		
		@Override
		public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param)
		{
			super.onBlockEventReceived(worldIn, pos, id, param);
			return BlockStateTile.super.onBlockEventReceived(worldIn, pos, id, param);
		}
	}
	
	@MethodsReturnNonnullByDefault
	class StateStraight extends State
	{
		<B extends BlockBase & IBlockPipe> StateStraight(B blockIn, ImmutableMap<IProperty<?>, Comparable<?>>
				propertiesIn)
		{
			super(blockIn, propertiesIn);
		}
		
		@Override
		public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos)
		{
			switch (getValue(getBlockPipe().getFlowProperty()))
			{
			default:
			case DU:
			case UD:
				return AABB_Y;
			case WE:
			case EW:
				return AABB_Z;
			case NS:
			case SN:
				return AABB_X;
			}
		}
	}
}
