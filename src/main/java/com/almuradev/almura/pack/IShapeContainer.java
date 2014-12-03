/**
 * This file is part of Almura, All Rights Reserved.
 *
 * Copyright (c) 2014 AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.almura.pack;

import com.almuradev.almura.pack.model.PackShape;

public interface IShapeContainer extends IPackObject {

    PackShape getShape();

    void setShapeFromPack();
}
