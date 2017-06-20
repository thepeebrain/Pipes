package com.github.lazylazuli.pipes.client;

import com.github.lazylazuli.pipes.common.CommonProxy;
import com.github.lazylazuli.pipes.common.PipeObjects;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
	
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		
		setModelResourceFor(PipeObjects.PIPE, PipeObjects.PIPE_WINDOWED);
	}
}