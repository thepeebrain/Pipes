package com.github.lazylazuli.pipes;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Registry
{
	public static CreativeTabs newCreativeTab(String label, Item icon)
	{
		return new CreativeTabs(label)
		{
			@Override
			public ItemStack getTabIconItem()
			{
				return new ItemStack(icon);
			}
		};
	}
	
	public static void registerBlocks(FMLPreInitializationEvent event, Block... blocks)
	{
		Logger logger = LogManager.getLogger();
		logger.info("Registering Blocks:");
		for (Block block : blocks)
		{
			if (event.getSide() == Side.CLIENT)
			{
				logger.info("\tRegistering " + block.getUnlocalizedName()
													.substring(5));
				final ItemBlock itemBlock = new ItemBlock(block);
				GameRegistry.register(block);
				GameRegistry.register(itemBlock, block.getRegistryName());
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation
						(block.getRegistryName(), "inventory"));
				LogManager.getLogger()
						  .info("");
			}
		}
		logger.info("Done.");
	}
	
	public static void registerItems(FMLPreInitializationEvent event, Item... items)
	{
		Logger logger = LogManager.getLogger();
		logger.info("Registering Blocks:");
		for (Item item : items)
		{
			logger.info("\tRegistering " + item.getUnlocalizedName()
											   .substring(5));
			if (event.getSide() == Side.CLIENT)
			{
				GameRegistry.register(item);
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(),
						"inventory"));
			}
		}
		logger.info("Done.");
	}
}
