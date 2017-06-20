package com.github.lazylazuli.pipes.client.gui.inventory;

import com.github.lazylazuli.pipes.common.inventory.ContainerWindowedPipe;
import com.github.lazylazuli.pipes.common.tile.TilePipe;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiWindowedPipe extends GuiContainer
{
	private final ResourceLocation texture = new ResourceLocation("lazylazulipipes",
			"textures/gui/container/pipe" + ".png"
	);
	private final TilePipe te;
	
	public GuiWindowedPipe(TilePipe te)
	{
		super(new ContainerWindowedPipe(te));
		this.te = te;
		this.xSize = 176;
		this.ySize = 166;
	}
	
	public void updateScreen()
	{
		super.updateScreen();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		ITextComponent text = te.getDisplayName();
		
		if (text != null)
		{
			String s = text.getFormattedText();
			fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 61, 4210752);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		mc.getTextureManager()
		  .bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop + 55, 0, 0, 176, 56);
	}
}
