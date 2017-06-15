package com.github.lazylazuli.pipes.common;

import com.github.lazylazuli.lib.common.item.ItemBlockBase;
import com.github.lazylazuli.lib.common.registry.BlockRegistry;
import com.github.lazylazuli.lib.common.registry.ItemRegistry;
import com.github.lazylazuli.pipes.common.block.BlockPipe;
import com.github.lazylazuli.pipes.common.block.BlockPipeReverse;
import com.github.lazylazuli.pipes.common.block.BlockPipeWindowed;
import com.github.lazylazuli.pipes.common.tile.TileEntityPipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy extends com.github.lazylazuli.lib.common.CommonProxy implements BlockRegistry, ItemRegistry
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(Pipes.instance, Pipes.GUI_HANDLER);
		GameRegistry.registerTileEntity(TileEntityPipe.class, "tile_pipe");
	}
	
	@Override
	public Block[] getBlocksForRegistry()
	{
		return new Block[] {
				new BlockPipe("pipe"),
				new BlockPipeReverse("pipe_reverse", "pipe"),
				new BlockPipeWindowed("pipe_windowed")
		};
	}
	
	@Override
	public Item[] getItemsForRegistry()
	{
		return new Item[] {
				new ItemBlockBase(PipeObjects.PIPE),
				new ItemBlockBase(PipeObjects.PIPE_WINDOWED)
		};
	}
}
