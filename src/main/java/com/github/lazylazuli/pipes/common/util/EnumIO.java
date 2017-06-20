package com.github.lazylazuli.pipes.common.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public enum EnumIO implements IStringSerializable
{
	DU(0, DOWN, UP),
	DN(1, DOWN, NORTH),
	DS(2, DOWN, SOUTH),
	UN(3, UP, NORTH),
	US(4, UP, SOUTH),
	NS(5, NORTH, SOUTH),
	NW(6, NORTH, WEST),
	NE(7, NORTH, EAST),
	SW(8, SOUTH, WEST),
	SE(9, SOUTH, EAST),
	WE(10, WEST, EAST),
	WD(11, WEST, DOWN),
	WU(12, WEST, UP),
	ED(13, EAST, DOWN),
	EU(14, EAST, UP),
	UD(15, UP, DOWN),
	ND(16, NORTH, DOWN),
	SD(17, SOUTH, DOWN),
	NU(18, NORTH, UP),
	SU(19, SOUTH, UP),
	SN(20, SOUTH, NORTH),
	WN(21, WEST, NORTH),
	EN(22, EAST, NORTH),
	WS(23, WEST, SOUTH),
	ES(24, EAST, SOUTH),
	EW(25, EAST, WEST),
	DW(26, DOWN, WEST),
	UW(27, UP, WEST),
	DE(28, DOWN, EAST),
	UE(29, UP, EAST);
	
	private final int id;
	
	private final EnumFacing input;
	
	private final EnumFacing output;
	
	EnumIO(int id, EnumFacing input, EnumFacing output)
	{
		this.id = id;
		this.input = input;
		this.output = output;
	}
	
	public int getId()
	{
		return id;
	}
	
	@Override
	public String getName()
	{
		return (input.name() + output.name()).toLowerCase();
	}
	
	public EnumFacing getInput()
	{
		return input;
	}
	
	public EnumFacing getOutput()
	{
		return output;
	}
	
	public static EnumIO fromFacing(EnumFacing input, EnumFacing output)
	{
		for (EnumIO flow : EnumIO.values())
		{
			if (flow.input == input && flow.output == output)
			{
				return flow;
			}
		}
		
		if (input == output)
		{
			throw new IllegalArgumentException(String.format("Output cannot be the same as input: %s", input));
		} else
		{
			throw new IllegalStateException("This should not have happened...?");
		}
	}
	
	public static EnumIO fromFacing(EnumFacing input)
	{
		return fromFacing(input, input.getOpposite());
	}
}
