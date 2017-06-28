package com.github.lazylazuli.pipes.common.block;

import com.github.lazylazuli.lib.common.block.BlockBase;
import com.github.lazylazuli.pipes.common.util.EnumIO;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import static com.github.lazylazuli.pipes.common.util.EnumIO.DU;
import static com.github.lazylazuli.pipes.common.util.EnumIO.EW;
import static com.github.lazylazuli.pipes.common.util.EnumIO.NS;
import static com.github.lazylazuli.pipes.common.util.EnumIO.SN;
import static com.github.lazylazuli.pipes.common.util.EnumIO.UD;
import static com.github.lazylazuli.pipes.common.util.EnumIO.WE;

public interface IBlockIO
{
	PropertyEnum<EnumIO> FLOW_NORMAL = PropertyEnum.create("flow",
			EnumIO.class,
			flow -> flow != null && flow.ordinal() < 15
	);
	
	PropertyEnum<EnumIO> FLOW_REVERSE = PropertyEnum.create("flow",
			EnumIO.class, flow -> !FLOW_NORMAL.getAllowedValues().contains(flow)
	);
	
	PropertyEnum<EnumIO> FLOW_STRAIGHT = PropertyEnum.create("flow", EnumIO.class, DU, NS, WE, UD, SN, EW);
	
	PropertyEnum<EnumIO> getFlowProperty();
	
	<B extends BlockBase & IBlockIO> B getAssociatedBlock();
	
	static PropertyEnum<EnumIO> getFlowProperty(IBlockState state)
	{
		Block block = state.getBlock();
		
		if (block instanceof IBlockIO)
		{
			return ((IBlockIO) block).getFlowProperty();
		}
		
		throw new IllegalStateException(String.format("Block is not an instance of %s. Found: %s",
				IBlockIO.class.getSimpleName(),
				block.getRegistryName()
		));
	}
	
	static EnumIO getIO(IBlockState state)
	{
		return state.getValue(getFlowProperty(state));
	}
	
	static EnumFacing getInput(IBlockState state)
	{
		return getIO(state).getInput();
	}
	
	static EnumFacing getOutput(IBlockState state)
	{
		return getIO(state).getOutput();
	}
	
	static <B extends BlockBase & IBlockIO> IBlockState constructStateFromFlow(B block, EnumIO flow)
	{
		PropertyEnum<EnumIO> property = block.getFlowProperty();
		
		if (property.getAllowedValues().contains(flow))
		{
			return block.getDefaultState().withProperty(property, flow);
		}
		
		B other = block.getAssociatedBlock();
		
		if (other == null)
		{
			return block.getDefaultState();
		}
		
		property = other.getFlowProperty();
		
		if (property.getAllowedValues().contains(flow))
		{
			return other.getDefaultState().withProperty(property, flow);
		}
		
		throw new IllegalStateException("This should not have happened!");
	}
}
