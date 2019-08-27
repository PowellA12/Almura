/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */

package com.almuradev.almura.feature.menu.main;

import com.almuradev.almura.asm.ClientStaticAccess;
import com.almuradev.almura.core.client.config.ClientConfiguration;
import com.almuradev.almura.core.client.config.category.GeneralCategory;
import com.almuradev.almura.feature.hud.HUDType;
import com.almuradev.almura.feature.speed.FirstLaunchOptimization;
import com.almuradev.toolbox.config.map.MappedConfiguration;
import com.google.common.base.Enums;
import com.google.inject.Inject;
import net.malisis.ego.font.FontOptions;
import net.malisis.ego.font.FontOptions.FontOptionsBuilder;
import net.malisis.ego.gui.MalisisGui;
import net.malisis.ego.gui.UIConstants.Button;
import net.malisis.ego.gui.component.container.UIContainer;
import net.malisis.ego.gui.component.decoration.UILabel;
import net.malisis.ego.gui.component.decoration.UITooltip;
import net.malisis.ego.gui.component.interaction.UIButton;
import net.malisis.ego.gui.component.interaction.UICheckBox;
import net.malisis.ego.gui.component.interaction.UISlider;
import net.malisis.ego.gui.component.layout.RowLayout;
import net.malisis.ego.gui.element.size.Size;
import net.malisis.ego.gui.element.size.Size.ISize;
import net.malisis.ego.gui.render.background.DirtBackground;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

public class OptionsMenu extends MalisisGui
{
	@Inject
	private static MappedConfiguration<ClientConfiguration> configAdapter = null;

	private static final ISize CONTROL_SIZE = Size.of(150, 20);
	private static final int CONTROL_PADDING = 5;

	private UISlider<Integer> sliderOriginHudOpacity;

	private boolean isOrigin()
	{
		return configAdapter.get().general.hud.equalsIgnoreCase(HUDType.ORIGIN);
	}

	@Override
	public void construct()
	{
		showParentOnClose = true;
		ClientStaticAccess.configAdapter.load();
		saveConfig();
		setBackground(new DirtBackground(screen));

		UILabel title = UILabel.builder()
							   .text("almura.menu_button.options")
							   .topCenter(0, 20)
							   .textColor(0xFFFFFF)
							   .build();

		addToScreen(title);
		addToScreen(leftColumn(configAdapter.get().general));
		addToScreen(rightColumn(configAdapter.get().general));

		UIButton buttonDone = UIButton.builder()
									  .text("gui.done")
									  .centered()
									  .y(260) //lazy, should be below columns
									  .size(Button.DEFAULT)
									  .onClick(this::close)
									  .build();

		addToScreen(buttonDone);
	}

	public UIContainer leftColumn(GeneralCategory general)
	{
		FontOptionsBuilder foBuilder = FontOptions.builder()
												  .color(TextFormatting.WHITE)
												  .when(c -> ((UICheckBox) c).isHovered())
												  .color(0xFFFFA0);

		UIContainer container = UIContainer.builder()
										   .leftOfCenter(-2)
										   .y(60)
										   .size(Size::sizeOfContent)
										   .layout(c -> new RowLayout(c, CONTROL_PADDING))
										   .build();

		UIButton.builder()
				.parent(container)
				.text("Load Optimized Defaults")
				.size(CONTROL_SIZE)
				.onClick(() -> {
					FirstLaunchOptimization.optimizeGame();
					close();
				})
				.build();

		UIButton.builder()
				.parent(container)
				.text("HUD: {HUD}")
				.bind("HUD", () -> StringUtils.capitalize(general.hud))
				.size(CONTROL_SIZE)
				.onClick(() -> {
					if (isOrigin())
					{
						general.hud = HUDType.VANILLA;
						sliderOriginHudOpacity.setAlpha(128);
						sliderOriginHudOpacity.setTooltip("Only available when Origin HUD is in use.");
						sliderOriginHudOpacity.setEnabled(false);
					//	sliderOriginHudOpacity.setVisible(false);
					}
					else
					{
						general.hud = HUDType.ORIGIN;
						sliderOriginHudOpacity.setAlpha(255);
						sliderOriginHudOpacity.setTooltip((UITooltip) null);
						sliderOriginHudOpacity.setEnabled(true);
					//	sliderOriginHudOpacity.setVisible(true);
					}
					saveConfig();
				})
				.build();

		UICheckBox.builder()
				  .parent(container)
				  .text("Display World Compass Widget")
				  .fontOptionsBuilder(foBuilder)
				  .checked(general.displayWorldCompassWidget)
				  .onChange(b -> {
					  general.displayWorldCompassWidget = b;
					  saveConfig();
				  })
				  .build();

		UICheckBox.builder()
				  .parent(container)
				  .text("Display Location Widget")
				  .fontOptionsBuilder(foBuilder)
				  .checked(general.displayLocationWidget)
				  .onChange(b -> {
					  general.displayLocationWidget = b;
					  saveConfig();
				  })
				  .build();

		UICheckBox.builder()
				  .parent(container)
				  .text("Display Numeric HUD Values")
				  .fontOptionsBuilder(foBuilder)
				  .checked(general.displayNumericHUDValues)
				  .onChange(b -> {
					  general.displayNumericHUDValues = b;
					  saveConfig();
				  })
				  .build();

		UICheckBox.builder()
				  .parent(container)
				  .text("Display Entity Names")
				  .fontOptionsBuilder(foBuilder)
				  .checked(general.displayNames)
				  .onChange(b -> {
					  general.displayNames = b;
					  saveConfig();
				  })
				  .build();

		UICheckBox.builder()
				  .parent(container)
				  .text("Display Entity Healthbars")
				  .fontOptionsBuilder(foBuilder)
				  .checked(general.displayHealthbars)
				  .onChange(b -> {
					  general.displayHealthbars = b;
					  saveConfig();
				  })
				  .build();

		UICheckBox.builder()
				  .parent(container)
				  .text("Disable Offhand Torch Placement")
				  .fontOptionsBuilder(foBuilder)
				  .checked(general.disableOffhandTorchPlacement)
				  .onChange(b -> {
					  general.disableOffhandTorchPlacement = b;
					  saveConfig();
				  })
				  .build();

		return container;
	}

	private UIContainer rightColumn(GeneralCategory general)
	{
		UIContainer container = UIContainer.builder()
										   .rightOfCenter(2)
										   .y(60)
										   .size(Size::sizeOfContent)
										   .layout(c -> new RowLayout(c, CONTROL_PADDING))
										   .build();

		boolean isOrigin = isOrigin();
		sliderOriginHudOpacity = UISlider.builder(0, 255)
										 .parent(container)
										 .text("HUD Opacity: {value}")
										 .value(general.originHudOpacity)
										 .size(CONTROL_SIZE)
										 .enabled(isOrigin)
										 .alpha(isOrigin ? 255 : 128)
										 .tooltip(isOrigin ? null : "Only available when Origin HUD is in use.")
										 .onChange(i -> {
											 general.originHudOpacity = i;
											 saveConfig();
										 })
										 .build();
		UISlider.builder(Distance.class)
				.parent(container)
				.text("Chest distance: {value}")
				.value(Distance.from(general.chestRenderDistance))
				.size(CONTROL_SIZE)
				.onChange(d -> {
					general.chestRenderDistance = d.value;
					saveConfig();
				})
				.build();

		UISlider.builder(Distance.class)
				.parent(container)
				.text("Sign text distance: {value}")
				.value(Distance.from(general.signTextRenderDistance))
				.size(CONTROL_SIZE)
				.onChange(d -> {
					general.signTextRenderDistance = d.value;
					saveConfig();
				})
				.build();

		UISlider.builder(Distance.class)
				.parent(container)
				.text("Item frame distance: {value}")
				.value(Distance.from(general.itemFrameRenderDistance))
				.size(CONTROL_SIZE)
				.onChange(d -> {
					general.itemFrameRenderDistance = d.value;
					saveConfig();
				})
				.build();

		UISlider.builder(Distance.class)
				.parent(container)
				.text("Player name distance: {value}")
				.value(Distance.from(general.playerNameRenderDistance))
				.size(CONTROL_SIZE)
				.onChange(d -> {
					general.playerNameRenderDistance = d.value;
					saveConfig();
				})
				.build();

		UISlider.builder(Distance.class)
				.parent(container)
				.text("Enemy name distance: {value}")
				.value(Distance.from(general.enemyNameRenderDistance))
				.size(CONTROL_SIZE)
				.onChange(d -> {
					general.enemyNameRenderDistance = d.value;
					saveConfig();
				})
				.build();

		UISlider.builder(Distance.class)
				.parent(container)
				.text("Animal name distance: {value}")
				.value(Distance.from(general.animalNameRenderDistance))
				.size(CONTROL_SIZE)
				.onChange(d -> {
					general.animalNameRenderDistance = d.value;
					saveConfig();
				})
				.build();

		return container;
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}

	private void saveConfig()
	{
		ClientStaticAccess.configAdapter.save();
		//ClientStaticAccess.configAdapter.load();
	}

	public enum Distance
	{
		V0("Disabled", 0),
		V4("4", 4),
		V8("8", 8),
		V16("16", 16),
		V32("32", 32),
		V64("64", 64),
		V128("128", 128);

		public final String name;
		public final int value;

		Distance(String name, int value)
		{
			this.name = name;
			this.value = value;
		}

		@Override
		public String toString()
		{
			return name;
		}

		public static Distance from(int distance)
		{
			return Enums.getIfPresent(Distance.class, "V" + distance)
						.or(V0);
		}
	}
}
