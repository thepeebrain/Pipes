package com.github.lazylazuli.pipes;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
	void preInit(FMLPreInitializationEvent event)
	{
		Registry.registerBlocks(event, Pipes.PIPE, Pipes.PIPE_REVERSE);
		GameRegistry.registerTileEntity(TileEntityPipe.class, "tileentitypipe");
		GameRegistry.addRecipe(new ItemStack(Pipes.PIPE, 3), " I ", " I ", " I ", 'I', Items.IRON_INGOT);
		Pipes.PIPE.setCreativeTab(CreativeTabs.REDSTONE);
	}
}