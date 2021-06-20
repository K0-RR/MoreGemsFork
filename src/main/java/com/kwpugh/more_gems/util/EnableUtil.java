package com.kwpugh.more_gems.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;

public class EnableUtil
{
    public static boolean isEnabled(ItemStack stack)
    {
        return stack.hasTag() && stack.getTag().getBoolean("IsEnabled");
    }

    public static void changeEnabled(PlayerEntity player, Hand hand)
    {
        changeEnabled(player.getMainHandStack());
    }

    public static void changeEnabled(ItemStack stack)
    {
        if(!stack.hasTag())
        {
            stack.setTag(new NbtCompound());
        }
        boolean isEnabled = isEnabled(stack);
        stack.getTag().putBoolean("IsEnabled", !isEnabled);
    }
}