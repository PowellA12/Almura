/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package com.almuradev.content.type.block.processor;

import com.almuradev.content.type.block.BlockConfig;
import com.almuradev.content.type.block.BlockContentProcessor;
import com.almuradev.content.type.block.BlockStateDefinition;
import com.almuradev.content.type.block.ContentBlock;
import com.almuradev.toolbox.config.tag.ConfigTag;
import ninja.leaping.configurate.ConfigurationNode;

import static com.almuradev.toolbox.config.ConfigurationNodes.whenRealBoolean;

public final class OpaqueProcessor implements BlockContentProcessor.State.Any {
    private static final ConfigTag TAG = ConfigTag.create(BlockConfig.State.OPAQUE);

    @Override
    public ConfigTag tag() {
        return TAG;
    }

    @Override
    public void processState(final ConfigurationNode config, final ContentBlock.Builder<ContentBlock, BlockStateDefinition, BlockStateDefinition.Builder<BlockStateDefinition>> builder, final BlockStateDefinition.Builder<BlockStateDefinition> definition) {
        whenRealBoolean(config, definition::opaque);
    }
}
