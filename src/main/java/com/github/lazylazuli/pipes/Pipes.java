package com.github.lazylazuli.pipes;

import com.github.lazylazuli.lazylazulilib.Registry;
import com.github.lazylazuli.lazylazulilib.Stack;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Pipes.MODID, version = Pipes.VERSION, acceptedMinecraftVersions = Pipes.MCVERSION, updateJSON = Pipes
		.UPDATE)
public final class Pipes
{
	public static final String MODID = "pipes";
	public static final String VERSION = "@version@";
	public static final String MCVERSION = "@mcversion@";
	public static final String UPDATE = "@update@";
	
	@Mod.Instance
	public static Pipes instance;
	
	public static GuiHandler guiHandler = new GuiHandler();
	
	public static final BlockPipe PIPE = new BlockPipe("pipe", BlockPipe.NORMAL);
	public static final BlockPipe PIPE_REVERSE = new BlockPipe("pipe_reverse", BlockPipe.REVERSE);
	public static final BlockPipe PIPE_WINDOWED = new BlockWindowedPipe("pipe_windowed");
	
	public static final Registry REGISTRY = new Registry(MODID);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		REGISTRY.registerBlocks(event, Pipes.PIPE, Pipes.PIPE_REVERSE, Pipes.PIPE_WINDOWED);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(Pipes.instance, Pipes.guiHandler);
		
		GameRegistry.registerTileEntity(TileEntityPipe.class, "tileentitypipe");
		
		GameRegistry.addRecipe(Stack.of(Pipes.PIPE), "III", 'I', Items.IRON_INGOT);
		GameRegistry.addShapelessRecipe(Stack.of(Pipes.PIPE_WINDOWED), Pipes.PIPE, Blocks.GLASS_PANE);
		
		Pipes.PIPE.setCreativeTab(CreativeTabs.REDSTONE);
		Pipes.PIPE_WINDOWED.setCreativeTab(CreativeTabs.REDSTONE);
	}
}
