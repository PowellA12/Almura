/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package com.almuradev.almura.asm.mixin.accessors.item;

import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemSlab.class)
public interface ItemSlabAccessor {
    //public-f net.minecraft.item.ItemSlab field_150949_c # singleSlab
    @Accessor("singleSlab") BlockSlab accessor$getSingleSlab();
    @Final @Accessor("singleSlab") void accessor$setSingleSlab(BlockSlab slab);
    //public-f net.minecraft.item.ItemSlab field_179226_c # doubleSlab
    @Accessor("doubleSlab") BlockSlab accessor$getDoubleSlab();
    @Final @Accessor("doubleSlab") void accessor$setDoubleSlab(BlockSlab block);
}
