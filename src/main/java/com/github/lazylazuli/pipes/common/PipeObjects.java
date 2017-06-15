package com.github.lazylazuli.pipes.common;

import com.github.lazylazuli.pipes.common.block.BlockPipe;
import com.github.lazylazuli.pipes.common.block.BlockPipeReverse;
import com.github.lazylazuli.pipes.common.block.BlockPipeWindowed;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Pipes.MODID)
@GameRegistry.ObjectHolder(Pipes.MODID)
public class PipeObjects
{
	@GameRegistry.ObjectHolder("pipe")
	public static final BlockPipe PIPE = null;
	
	@GameRegistry.ObjectHolder("pipe_reverse")
	public static final BlockPipeReverse PIPE_REVERSE = null;
	
	@GameRegistry.ObjectHolder("pipe_windowed")
	public static final BlockPipeWindowed PIPE_WINDOWED = null;
}
