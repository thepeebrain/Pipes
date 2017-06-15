package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.pipes.common.EnumFlow;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;

public class BlockPipeReverse extends BlockPipe
{
	public static final PropertyEnum<EnumFlow> REVERSE = PropertyEnum.create("flow", EnumFlow.class,
			flow -> flow != null && flow.ordinal() >= 15
	);
	
	public BlockPipeReverse(String name, String unlocalizedName)
	{
		super(name, unlocalizedName);
		setCreativeTab(null);
	}
	
	@Override
	public PropertyEnum<EnumFlow> getFlowProperty()
	{
		return REVERSE;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return constructStateFromFlow(EnumFlow.values()[meta + 15]);
	}
}
