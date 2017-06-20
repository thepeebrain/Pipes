package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.lib.common.block.BlockBase;
import com.github.lazylazuli.lib.common.block.state.BlockState;
import com.github.lazylazuli.pipes.common.tile.TilePipe;
import com.github.lazylazuli.pipes.common.util.EnumIO;
import com.google.common.collect.ImmutableMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class BlockPipeBase extends BlockBase implements IBlockPipe, ITileEntityProvider
{
	BlockPipeBase(String name, String unlocalizedName)
	{
		super(Material.IRON, name, unlocalizedName);
		
		setHardness(3);
		setResistance(8);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState()
								  .withProperty(
										  getFlowProperty(),
										  (EnumIO) getFlowProperty().getAllowedValues()
																	.toArray()[0]
								  ));
	}
	
	BlockPipeBase(String name)
	{
		this(name, name);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public abstract BlockPipeBase getAssociatedBlock();
	
	@Override
	public abstract PropertyEnum<EnumIO> getFlowProperty();
	
	//
	
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
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
