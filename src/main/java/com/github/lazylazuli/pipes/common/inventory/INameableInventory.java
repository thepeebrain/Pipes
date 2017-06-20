package com.github.lazylazuli.pipes.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public interface INameableInventory extends IInventory
{
	String getCustomName();
	
	String getDefaultName();
	
	void setCustomName(String name);
	
	default boolean hasCustomName()
	{
		return getCustomName() != null && !getCustomName().isEmpty();
	}
	
	default String getName()
	{
		return hasCustomName() ? getCustomName() : getDefaultName();
	}
	
	default ITextComponent getDisplayName()
	{
		return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
	}
}
