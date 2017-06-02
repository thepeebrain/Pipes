package com.github.lazylazuli.pipes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Pipes.MODID, version = Pipes.VERSION, acceptedMinecraftVersions = Pipes.MCVERSION)
public final class Pipes
{
	public static final String MODID = "pipes";
	public static final String VERSION = "@version@";
	public static final String MCVERSION = "@mcversion@";
	
	public static final String CLIENT_PROXY = "com.github.lazylazuli.pipes.ClientProxy";
	public static final String COMMON_PROXY = "com.github.lazylazuli.pipes.CommonProxy";
	
	@Mod.Instance
	public static Pipes instance;
	
	@SidedProxy(clientSide = Pipes.CLIENT_PROXY, serverSide = Pipes.COMMON_PROXY)
	public static CommonProxy proxy;
	
	public static final BlockPipe PIPE = new BlockPipe("pipe", BlockPipe.NORMAL);
	public static final BlockPipe PIPE_REVERSE = new BlockPipe("pipe_reverse", BlockPipe.REVERSE);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
	}
}
