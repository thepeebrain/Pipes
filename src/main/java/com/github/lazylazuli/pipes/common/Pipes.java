package com.github.lazylazuli.pipes.common;

import com.github.lazylazuli.lib.common.LazyLazuliMod;
import com.github.lazylazuli.lib.common.Proxy;
import com.github.lazylazuli.pipes.common.network.GuiHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Pipes.MODID, version = Pipes.VERSION, acceptedMinecraftVersions = Pipes.MCVERSION, dependencies =
		"required-after:lazylazulilib", updateJSON = Pipes
		.UPDATE)
public final class Pipes extends LazyLazuliMod
{
	public static final String MODID = "lazylazulipipes";
	public static final String VERSION = "@version@";
	public static final String MCVERSION = "@mcversion@";
	public static final String UPDATE = "@update@";
	
	@Mod.Instance
	public static Pipes instance;
	
	@SidedProxy(
			clientSide = "com.github.lazylazuli.pipes.client.ClientProxy",
			serverSide = "com.github.lazylazuli.pipes.common.CommonProxy"
	)
	public static Proxy proxy;
	
	@Override
	public Proxy getProxy()
	{
		return proxy;
	}
	
	public static final GuiHandler GUI_HANDLER = new GuiHandler();
	
	@Override
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
	}
}
