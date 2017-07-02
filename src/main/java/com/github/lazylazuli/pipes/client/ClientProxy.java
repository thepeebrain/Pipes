package com.github.lazylazuli.pipes.client;

import com.github.lazylazuli.pipes.common.CommonProxy;
import com.github.lazylazuli.pipes.common.PipeObjects;
import net.minecraftforge.client.event.ModelRegistryEvent;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerModels(ModelRegistryEvent event)
	{
		setModelResourceFor(PipeObjects.PIPE, PipeObjects.PIPE_WINDOWED);
	}
}