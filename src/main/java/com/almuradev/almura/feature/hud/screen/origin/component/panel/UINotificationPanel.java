/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package com.almuradev.almura.feature.hud.screen.origin.component.panel;

import com.almuradev.almura.feature.notification.ClientNotificationManager;
import com.almuradev.almura.feature.notification.type.PopupNotification;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.util.FontColors;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@SuppressWarnings("deprecation")
public final class UINotificationPanel extends AbstractPanel {

    private final ClientNotificationManager manager;
    public  UILabel notificationLabel, notificationTitle;
    public int timer;

    public UINotificationPanel(MalisisGui gui, int width, int height, ClientNotificationManager manager) {
        super(gui, width, height);

        this.manager = manager;

        this.notificationTitle = new UILabel(gui, "");
        this.notificationTitle.setPosition(0, 0, Anchor.TOP | Anchor.CENTER);
        this.notificationTitle.setFontOptions(FontColors.RED_FO);

        this.notificationLabel = new UILabel(gui, "");
        this.notificationLabel.setPosition(0, 13, Anchor.TOP | Anchor.CENTER);
        this.notificationLabel.setFontOptions(FontColors.WHITE_FO);

        this.add(this.notificationTitle,this.notificationLabel);
    }

    @Override
    public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick) {
        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.world == null) {
            return;
        }
        super.drawForeground(renderer, mouseX, mouseY, partialTick);
    }

    public void displayPopup() {
        final PopupNotification notification = manager.getCurrent();
        if (notification != null) {
            this.notificationTitle.setText(this.manager.getCurrent().getTitle());
            this.notificationLabel.setText(this.manager.getCurrent().getMessage());
        }
    }

    public void destroyPopup() {
        // I'm here because I can be.
    }
}
