/**
 * This file is part of Almura, All Rights Reserved.
 *
 * Copyright (c) AlmuraDev <http://github.com/AlmuraDev/>
 */
package com.almuradev.almura.client.gui.menu;

import com.almuradev.almura.Almura;
import com.almuradev.almura.FileSystem;
import com.almuradev.almura.client.gui.SimpleGui;
import com.almuradev.almura.client.gui.components.UIAnimatedBackground;
import com.almuradev.almura.client.gui.components.UIForm;
import com.almuradev.almura.client.gui.util.FontRenderOptionsConstants;
import com.google.common.eventbus.Subscribe;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.GuiTexture;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.util.Locale;

public class DynamicMainMenu extends SimpleGui {

    public DynamicMainMenu(SimpleGui parent) {
        super(parent);
    }

    @Override
    public void construct() {

        // Create the form
        final UIForm form = new UIForm(this, 200, 225, "Almura", false);
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);

        // Create the logo
        final UIImage logoImage = new UIImage(this, new GuiTexture(ALMURA_LOGO_LOCATION), null);
        logoImage.setAnchor(Anchor.CENTER | Anchor.TOP);
        logoImage.setSize(55, 85);

        final int padding = 4;

        // Create the singleplayer button
        final UIButton singleplayerButton = new UIButton(this, TextSerializers.LEGACY_FORMATTING_CODE.serialize(Text.of(TextColors.BLUE, "Singleplayer")));
        singleplayerButton.setSize(180, 16);
        singleplayerButton.setPosition(0, getPaddedY(logoImage, padding * 2), Anchor.CENTER | Anchor.TOP);
        singleplayerButton.setName("button.singleplayer");
        singleplayerButton.register(this);

        // Create the multiplayer button
        final UIButton multiplayerButton = new UIButton(this, TextSerializers.LEGACY_FORMATTING_CODE.serialize(Text.of(TextColors.AQUA, "Multiplayer")));
        multiplayerButton.setSize(180, 16);
        multiplayerButton.setPosition(0, getPaddedY(singleplayerButton, padding), Anchor.CENTER | Anchor.TOP);
        multiplayerButton.setName("button.multiplayer");
        multiplayerButton.register(this);

        // Create the options button
        final UIButton optionsButton = new UIButton(this, "Options");
        optionsButton.setSize(50, 16);
        optionsButton.setPosition(10, getPaddedY(multiplayerButton, padding), Anchor.LEFT | Anchor.TOP);
        optionsButton.setName("button.options");
        optionsButton.register(this);

        // Create the Configuration button
        final UIButton configurationButton = new UIButton(this, "Configuration");
        configurationButton.setSize(76, 16);
        configurationButton.setPosition(0, getPaddedY(multiplayerButton, padding), Anchor.CENTER | Anchor.TOP);
        configurationButton.setName("button.configuration");
        configurationButton.register(this);

        // Create the Mods button
        final UIButton aboutButton = new UIButton(this, "About");
        aboutButton.setSize(50, 16);
        aboutButton.setPosition(-10, getPaddedY(multiplayerButton, padding), Anchor.RIGHT | Anchor.TOP);
        aboutButton.setName("button.about");
        aboutButton.register(this);

        // Create the quit button
        final UIButton quitButton = new UIButton(this, "Quit");
        quitButton.setSize(50, 16);
        quitButton.setPosition(0, getPaddedY(configurationButton, padding + 10), Anchor.CENTER | Anchor.TOP);
        quitButton.setName("button.quit");
        quitButton.register(this);

        // Create the copyright label
        final UILabel copyrightLabel = new UILabel(this, TextSerializers.LEGACY_FORMATTING_CODE.serialize(Text.of(TextColors.GRAY, "Copyright AlmuraDev "
                + "2012 - 2016")));
        copyrightLabel.setPosition(0, -9, Anchor.CENTER | Anchor.BOTTOM);
        copyrightLabel.setFontRenderOptions(FontRenderOptionsConstants.FRO_SCALE_070);

        // Create the trademark label
        final UILabel trademarkLabel = new UILabel(this, TextSerializers.LEGACY_FORMATTING_CODE.serialize(Text.of(TextColors.GRAY, "Minecraft is a "
                + "registered trademark of Mojang AB")));
        trademarkLabel.setPosition(0, -1, Anchor.CENTER | Anchor.BOTTOM);
        trademarkLabel.setFontRenderOptions(FontRenderOptionsConstants.FRO_SCALE_070);

        form.getContentContainer().add(logoImage, singleplayerButton, multiplayerButton, optionsButton, configurationButton,
                aboutButton, quitButton, copyrightLabel, trademarkLabel);

        addToScreen(new UIAnimatedBackground(this));
        addToScreen(form);
    }

    @Override
    public void onClose() {
        super.onClose();
        mc.shutdown();
    }

    @Subscribe
    public void onButtonClick(UIButton.ClickEvent event) {
        switch (event.getComponent().getName().toLowerCase(Locale.ENGLISH)) {
            case "button.singleplayer":
                mc.displayGuiScreen(new GuiWorldSelection(this));
                break;
            case "button.multiplayer":
                new DynamicServerMenu(this).display();
                break;
            case "button.options":
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case "button.configuration":
                new DynamicConfigurationMenu(this).display();
                break;
            case "button.about":
                new DynamicAboutMenu(this).display();
                break;
            case "button.quit":
                close();
        }
    }
}
