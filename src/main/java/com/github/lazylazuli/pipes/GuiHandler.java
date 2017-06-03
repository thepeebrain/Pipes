package com.github.lazylazuli.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return new ContainerWindowedPipe((TileEntityPipe) world.getTileEntity(new BlockPos(x, y, z)));
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return new GuiWindowedPipe((TileEntityPipe) world.getTileEntity(new BlockPos(x, y, z)));
	}
}
