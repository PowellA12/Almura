/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package com.almuradev.almura.feature.store.basic.listing;

import static com.google.common.base.Preconditions.checkState;

import com.almuradev.almura.feature.store.listing.SellingItem;
import com.almuradev.almura.shared.feature.FeatureConstants;
import net.minecraft.item.Item;

import java.math.BigDecimal;
import java.time.Instant;

public final class BasicSellingItem extends AbstractStoreItem implements SellingItem {

    public BasicSellingItem(final int record, final Instant created, final Item item, final int metadata, final int quantity, final BigDecimal price,
        final int index) {
        super(record, created, item, metadata, quantity, price, index);

        checkState(quantity >= FeatureConstants.UNLIMITED);

    }

    @Override
    public SellingItem copy() {
        return new BasicSellingItem(this.record, this.created, this.item, this.metadata, this.quantity, this.price, this.index);
    }
}