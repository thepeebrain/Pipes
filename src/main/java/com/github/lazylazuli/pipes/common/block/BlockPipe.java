package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.lib.common.block.BlockBase;
import com.github.lazylazuli.lib.common.block.state.BlockState;
import com.github.lazylazuli.lib.common.block.state.BlockStateTile;
import com.github.lazylazuli.pipes.common.EnumFlow;
import com.github.lazylazuli.pipes.common.PipeObjects;
import com.github.lazylazuli.pipes.common.tile.TileEntityPipe;
import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPipe extends BlockBase implements ITileEntityProvider
{
	public static final PropertyEnum<EnumFlow> NORMAL = PropertyEnum.create("flow", EnumFlow.class,
			flow -> flow != null && flow.ordinal() < 15
	);
	
	protected static final AxisAlignedBB DN_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.5D, 0.625D);
	protected static final AxisAlignedBB DS_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5D, 1.0D);
	
	protected static final AxisAlignedBB DW_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 0.5D, 0.625D);
	protected static final AxisAlignedBB DE_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 0.5D, 0.625D);
	
	protected static final AxisAlignedBB UN_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.0D, 0.625D, 1.0D, 0.625D);
	protected static final AxisAlignedBB US_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.0D, 1.0D);
	
	protected static final AxisAlignedBB UW_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.375D, 0.625D, 1.0D, 0.625D);
	protected static final AxisAlignedBB UE_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 1.0D, 1.0D, 0.625D);
	
	protected static final AxisAlignedBB SW_AABB = new AxisAlignedBB(0, 0.25D, 0.375D, 0.625D, 0.5D, 1.0D);
	protected static final AxisAlignedBB SE_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 1, 0.5D, 1.0D);
	
	protected static final AxisAlignedBB NW_AABB = new AxisAlignedBB(0, 0.25D, 0.0D, 0.625D, 0.5D, 0.625D);
	protected static final AxisAlignedBB NE_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.0D, 1, 0.5D, 0.625D);
	
	protected static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.0D, 0.625D, 0.5D, 1.0D);
	protected static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
	protected static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.375D, 1.0D, 0.5D, 0.625D);
	
	public BlockPipe(String name, String unlocalizedName)
	{
		super(Material.IRON, name, unlocalizedName);
		
		setHardness(3);
		setResistance(8);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.REDSTONE);
		setDefaultState(blockState.getBaseState()
								  .withProperty(getFlowProperty(), (EnumFlow) getFlowProperty().getAllowedValues()
																							   .toArray()[0]));
	}
	
	public BlockPipe(String name)
	{
		this(name, name);
	}
	
	public PropertyEnum<EnumFlow> getFlowProperty()
	{
		return NORMAL;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		EnumFacing out = EnumFacing.getDirectionFromEntityLiving(pos, placer);
		EnumFacing in = facing.getOpposite();
		return constructStateFromFlow(EnumFlow.get(in, out));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand
			hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (playerIn.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY)
			return false;
		
		if (worldIn.isRemote)
			return true;
		
		EnumFlow flow = EnumFlow.get(getInput(state), facing);
		
		if (flow != null)
			worldIn.setBlockState(pos, constructStateFromFlow(flow));
		
		return true;
	}
	
	@Override
	public boolean isAssociatedBlock(Block other)
	{
		return other == PipeObjects.PIPE || other == PipeObjects.PIPE_REVERSE;
	}
	
	protected IBlockState constructStateFromFlow(@Nullable EnumFlow flow)
	{
		PropertyEnum<EnumFlow> property = getFlowProperty();
		
		if (flow == null)
		{
			return getDefaultState();
		}
		
		if (!property.getAllowedValues()
					 .contains(flow))
		{
			BlockPipe block;
			
			if (this == PipeObjects.PIPE)
			{
				block = PipeObjects.PIPE_REVERSE;
			} else
			{
				block = PipeObjects.PIPE;
			}
			
			return block.constructStateFromFlow(flow);
		}
		
		return getDefaultState().withProperty(property, flow);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return constructStateFromFlow(EnumFlow.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumFlow flow = state.getValue(((BlockPipe) state.getBlock()).getFlowProperty());
		return flow.ordinal() % 15;
	}
	
	@Override
	protected IProperty<?>[] getProperties()
	{
		return new IProperty[] { getFlowProperty() };
	}
	
	@Override
	protected BlockState createBlockState(ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn)
	{
		return new State(this, propertiesIn);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityPipe();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);
		
		if (tileentity instanceof TileEntityPipe)
		{
			InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityPipe) tileentity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}
		
		super.breakBlock(worldIn, pos, state);
	}
	
	public static EnumFacing getInput(IBlockState state)
	{
		BlockPipe block = (BlockPipe) state.getBlock();
		return state.getValue(block.getFlowProperty())
					.getInput();
	}
	
	public static EnumFacing getOutput(IBlockState state)
	{
		BlockPipe block = (BlockPipe) state.getBlock();
		return state.getValue(block.getFlowProperty())
					.getOutput();
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack
			stack)
	{
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		if (stack.hasDisplayName())
		{
			TileEntity te = worldIn.getTileEntity(pos);
			
			if (te instanceof TileEntityPipe)
			{
				((TileEntityPipe) te).setCustomName(stack.getDisplayName());
			}
		}
	}
	
	class State extends BlockState implements BlockStateTile
	{
		public State(BlockBase blockIn,
				ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn)
		{
			super(blockIn, propertiesIn);
		}
		
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
		
		@Override
		public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos)
		{
			switch (getValue(getFlowProperty()))
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
			case NW:
			case WN:
				return NW_AABB;
			case NE:
			case EN:
				return NE_AABB;
			case SW:
			case WS:
				return SW_AABB;
			case SE:
			case ES:
				return SE_AABB;
			case DN:
			case ND:
				return DN_AABB;
			case DS:
			case SD:
				return DS_AABB;
			case DW:
			case WD:
				return DW_AABB;
			case DE:
			case ED:
				return DE_AABB;
			case UN:
			case NU:
				return UN_AABB;
			case US:
			case SU:
				return US_AABB;
			case UW:
			case WU:
				return UW_AABB;
			case UE:
			case EU:
				return UE_AABB;
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
		
		@Override
		public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param)
		{
			super.onBlockEventReceived(worldIn, pos, id, param);
			return BlockStateTile.super.onBlockEventReceived(worldIn, pos, id, param);
		}
	}
}
