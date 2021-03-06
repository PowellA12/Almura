/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package com.almuradev.content.type.itemgroup.processor;

import com.almuradev.content.type.itemgroup.ItemGroup;
import com.almuradev.content.type.itemgroup.ItemGroupConfig;
import com.almuradev.toolbox.config.processor.TaggedConfigProcessor;
import com.almuradev.toolbox.config.tag.ConfigTag;
import ninja.leaping.configurate.ConfigurationNode;

public final class SortProcessor implements TaggedConfigProcessor<ItemGroup.Builder, ConfigTag> {
    private static final ConfigTag TAG = ConfigTag.create(ItemGroupConfig.SORT);

    @Override
    public ConfigTag tag() {
        return TAG;
    }

    @Override
    public void processTagged(final ConfigurationNode config, final ItemGroup.Builder builder) {
        builder.sort(config.getBoolean());
    }
}
