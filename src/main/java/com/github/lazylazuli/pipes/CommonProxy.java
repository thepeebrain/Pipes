package com.github.lazylazuli.pipes;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
	void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(Pipes.instance, Pipes.guiHandler);
		Registry.registerBlocks(event, Pipes.PIPE, Pipes.PIPE_REVERSE, Pipes.PIPE_WINDOWED);
		GameRegistry.registerTileEntity(TileEntityPipe.class, "tileentitypipe");
		GameRegistry.addRecipe(new ItemStack(Pipes.PIPE, 1), "III", 'I', Items.IRON_INGOT);
		GameRegistry.addShapelessRecipe(new ItemStack(Pipes.PIPE_WINDOWED, 1), Pipes.PIPE, Blocks.GLASS_PANE);
		Pipes.PIPE.setCreativeTab(CreativeTabs.REDSTONE);
		Pipes.PIPE_WINDOWED.setCreativeTab(CreativeTabs.REDSTONE);
	}
}