package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.pipes.common.PipeObjects;
import com.github.lazylazuli.pipes.common.util.EnumIO;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPipeReverse extends BlockPipeNormal
{
	public BlockPipeReverse(String name, String unlocalizedName)
	{
		super(name, unlocalizedName);
	}
	
	@Override
	public BlockPipeNormal getAssociatedBlock()
	{
		return PipeObjects.PIPE;
	}
	
	@Override
	public PropertyEnum<EnumIO> getFlowProperty()
	{
		return FLOW_REVERSE;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return IBlockIO.constructStateFromFlow(this, EnumIO.values()[meta + 15]);
	}
}
