package com.github.lazylazuli.pipes.common;

import com.github.lazylazuli.lib.common.item.ItemBlockBase;
import com.github.lazylazuli.lib.common.mod.Proxy;
import com.github.lazylazuli.pipes.common.block.BlockPipeNormal;
import com.github.lazylazuli.pipes.common.block.BlockPipeReverse;
import com.github.lazylazuli.pipes.common.block.BlockPipeWindowed;
import com.github.lazylazuli.pipes.common.tile.TilePipe;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.lazylazuli.pipes.common.PipeObjects.PIPE;
import static com.github.lazylazuli.pipes.common.PipeObjects.PIPE_WINDOWED;

public class CommonProxy extends Proxy
{
	private final Logger log = LogManager.getLogger(Pipes.MODID);
	
	@Override
	protected Logger getLogger()
	{
		return log;
	}
	
	@Override
	protected Block[] getBlocksForTab(CreativeTabs tab)
	{
		return tab == CreativeTabs.REDSTONE ? new Block[] {
				PIPE,
				PIPE_WINDOWED
		} : new Block[0];
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(Pipes.instance, Pipes.GUI_HANDLER);
		GameRegistry.registerTileEntity(TilePipe.class, "tile_pipe");
	}
	
	@Override
	public Block[] getBlocksForRegistry()
	{
		return new Block[] {
				new BlockPipeNormal("pipe"),
				new BlockPipeReverse("pipe_reverse", "pipe"),
				new BlockPipeWindowed("pipe_windowed")
		};
	}
	
	@Override
	public Item[] getItemsForRegistry()
	{
		return new Item[] {
				new ItemBlockBase(PIPE),
				new ItemBlockBase(PIPE_WINDOWED)
		};
	}
}
