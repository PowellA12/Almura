/*
 * This file is part of Almura.
 *
 * Copyright (c) AlmuraDev <https://github.com/AlmuraDev/>
 *
 * All Rights Reserved.
 */
package com.almuradev.almura.feature.exchange.client.gui;

import com.almuradev.almura.shared.client.ui.FontColors;
import com.almuradev.almura.shared.client.ui.component.UIFormContainer;
import com.almuradev.almura.shared.client.ui.component.UISimpleList;
import com.almuradev.almura.shared.client.ui.component.button.UIButtonBuilder;
import com.almuradev.almura.shared.client.ui.screen.SimpleScreen;
import com.almuradev.almura.shared.util.MathUtil;
import net.malisis.core.client.gui.Anchor;
import net.malisis.core.client.gui.GuiRenderer;
import net.malisis.core.client.gui.MalisisGui;
import net.malisis.core.client.gui.component.UIComponent;
import net.malisis.core.client.gui.component.UISlot;
import net.malisis.core.client.gui.component.container.UIBackgroundContainer;
import net.malisis.core.client.gui.component.decoration.UIImage;
import net.malisis.core.client.gui.component.decoration.UILabel;
import net.malisis.core.client.gui.component.interaction.UIButton;
import net.malisis.core.client.gui.component.interaction.UISelect;
import net.malisis.core.client.gui.component.interaction.UITextField;
import net.malisis.core.inventory.MalisisSlot;
import net.malisis.core.renderer.font.FontOptions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.slf4j.Logger;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

@SideOnly(Side.CLIENT)
public final class ExchangeGUI extends SimpleScreen {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
    private static final int innerPadding = 2;
    private int lastUpdate = 0;
    private boolean unlockMouse = true;

    private World world;
    private EntityPlayer player;
    private BlockPos blockpos;
    private UIButton buttonFirstPage, buttonPreviousPage, buttonNextPage, buttonLastPage;
    private UITextField itemSearchField, sellerSearchField;
    private UISimpleList<ResultListElementData> resultsList;
    private final List<MockOffer> offers = new ArrayList<>();
    private final List<ResultListElementData> currentResults = new ArrayList<>();
    private int currentPage;
    private int pages;

    @Inject private static Logger logger;

    public ExchangeGUI(EntityPlayer player, World worldIn, BlockPos blockpos) {
        this.player = player;
        this.world = worldIn;
        this.blockpos = blockpos;

        // ADD MOCK DATA
        offers.add(createMockOffer(ItemTypes.ACACIA_BOAT));
        offers.add(createMockOffer(ItemTypes.ACACIA_BOAT));
        offers.add(createMockOffer(ItemTypes.ACACIA_BOAT));
        offers.add(createMockOffer(ItemTypes.SNOWBALL));
        offers.add(createMockOffer(ItemTypes.WATER_BUCKET));
        offers.add(createMockOffer(ItemTypes.GOLDEN_AXE));
        offers.add(createMockOffer(ItemTypes.STONE));
        offers.add(createMockOffer(ItemTypes.PUMPKIN));
        offers.add(createMockOffer(ItemTypes.END_ROD));
        offers.add(createMockOffer(ItemTypes.APPLE));
        offers.add(createMockOffer(ItemTypes.BAKED_POTATO));
        offers.add(createMockOffer(ItemTypes.CARROT));
    }

    @Override
    public void construct() {
        guiscreenBackground = false;
        Keyboard.enableRepeatEvents(true);

        // Main Panel
        final UIFormContainer form = new UIFormContainer(this, 600, 395, "");
        form.setAnchor(Anchor.CENTER | Anchor.MIDDLE);
        form.setMovable(true);
        form.setClosable(true);
        form.setBorder(FontColors.WHITE, 1, 185);
        form.setBackgroundAlpha(215);
        form.setBottomPadding(3);
        form.setRightPadding(3);
        form.setTopPadding(20);
        form.setLeftPadding(3);

        final UILabel titleLabel = new UILabel(this, "Almura Exchange");
        titleLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(true).scale(1.1F).build());
        titleLabel.setPosition(0, -15, Anchor.CENTER | Anchor.TOP);

        // Item Search Area
        final UIFormContainer searchArea = new UIFormContainer(this, 295, 310, "");
        searchArea.setPosition(0, 10, Anchor.LEFT | Anchor.TOP);
        searchArea.setMovable(false);
        searchArea.setClosable(false);
        searchArea.setBorder(FontColors.WHITE, 1, 185);
        searchArea.setBackgroundAlpha(215);
        searchArea.setPadding(3, 3);

        final UILabel itemSearchLabel = new UILabel(this, "Item Name:");
        itemSearchLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).scale(1.1F).build());
        itemSearchLabel.setPosition(0, 0, Anchor.LEFT | Anchor.TOP);

        this.itemSearchField = new UITextField(this, "", false);
        this.itemSearchField.setSize(145, 0);
        this.itemSearchField.setPosition(itemSearchLabel.getWidth() + innerPadding, itemSearchLabel.getY(), Anchor.LEFT | Anchor.TOP);
        this.itemSearchField.setEditable(true);
        this.itemSearchField.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());

        // Seller Search Area
        final UILabel sellerSearchLabel = new UILabel(this, "Seller:");
        sellerSearchLabel.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).scale(1.1F).build());
        sellerSearchLabel.setPosition(itemSearchField.getX() - sellerSearchLabel.getWidth() + innerPadding, itemSearchLabel.getY() + 17,
                Anchor.LEFT | Anchor.TOP);

        this.sellerSearchField = new UITextField(this, "", false);
        this.sellerSearchField.setSize(145, 0);
        this.sellerSearchField.setPosition(itemSearchField.getX(), sellerSearchLabel.getY(), Anchor.LEFT | Anchor.TOP);
        this.sellerSearchField.setEditable(true);
        this.sellerSearchField.setFontOptions(FontOptions.builder().from(FontColors.WHITE_FO).shadow(false).build());

        // Sort combobox
        final UISelect<SortType> comboBoxSortType = new UISelect<>(this, 80, Arrays.asList(SortType.values()));
        comboBoxSortType.setLabelFunction(type -> type == null ? "" : type.displayName); // Because that's reasonable.
        comboBoxSortType.selectFirst();
        comboBoxSortType.setPosition(-innerPadding, sellerSearchLabel.getY(), Anchor.RIGHT | Anchor.TOP);

        this.resultsList = new UISimpleList<>(this, searchArea.getWidth() - 10, 250);
        this.resultsList.setPosition(innerPadding, getPaddedY(sellerSearchField, 4));
        this.resultsList.setComponentFactory((g, e) -> new ResultListElement(this, e));
        this.resultsList.setElementSpacing(1);
        this.resultsList.register(this);

        // Search button
        final UIButton buttonSearch = new UIButtonBuilder(this)
                .width(80)
                .anchor(Anchor.RIGHT | Anchor.TOP)
                .position(-innerPadding, 0)
                .text("Search")
                .onClick(() -> {
                    final String itemName = itemSearchField.getText();
                    final String username = sellerSearchField.getText();

                    this.currentResults.clear();

                    logger.info(String.format("Searching for criteria: itemname=%s, playerName=%s", itemName, username));

                    this.getResults(itemName, username, comboBoxSortType.getSelectedValue())
                            .forEach(r -> this.currentResults.add(new ResultListElementData(resultsList, r)));
                    this.pages = (Math.max(this.currentResults.size() - 1, 1) / 10) + 1;

                    this.setPage(1);

                    if (this.currentResults.isEmpty()) {
                        logger.info(String.format("No offers found matching criteria: itemname=%s, playerName=%s", itemName, username));
                    } else {
                        this.currentResults.forEach(r -> {
                            logger.info(String.format("Found %d of %s for %1.2f/ea by %s submitted on %s", r.offer.quantity,
                                    r.offer.item.getTranslation().get(), r.offer.pricePer, r.offer.playerName,
                                    formatter.format(r.offer.instant)));
                        });
                    }
                })
                .build("button.search");

        // Bottom Page Control - first button
        this.buttonFirstPage = new UIButtonBuilder(this)
                .size(20)
                .anchor(Anchor.LEFT | Anchor.BOTTOM)
                .position(0, 0)
                .text("|<")
                .enabled(false)
                .onClick(() -> setPage(1))
                .build("button.first");

        // Bottom Page Control - previous button
        this.buttonPreviousPage = new UIButtonBuilder(this)
                .size(20)
                .anchor(Anchor.LEFT | Anchor.BOTTOM)
                .position(getPaddedX(this.buttonFirstPage, innerPadding), 0)
                .text("<")
                .enabled(false)
                .onClick(() -> setPage(--this.currentPage))
                .build("button.previous");

        // Bottom Page Control - last button
        this.buttonLastPage = new UIButtonBuilder(this)
                .size(20)
                .anchor(Anchor.RIGHT | Anchor.BOTTOM)
                .position(0,0)
                .text(">|")
                .enabled(false)
                .onClick(() -> setPage(this.pages))
                .build("button.last");

        // Bottom Page Control - next button
        this.buttonNextPage = new UIButtonBuilder(this)
                .size(20)
                .anchor(Anchor.RIGHT | Anchor.BOTTOM)
                .position(getPaddedX(this.buttonLastPage, innerPadding, Anchor.RIGHT),0)
                .text(">")
                .enabled(false)
                .onClick(() -> setPage(++this.currentPage))
                .build("button.next");

        // Add Elements of Search Area
        searchArea.add(itemSearchLabel, itemSearchField, sellerSearchLabel, sellerSearchField, buttonSearch, comboBoxSortType, buttonFirstPage,
                buttonPreviousPage, buttonNextPage, buttonLastPage, resultsList);

        // Economy Pane
        final UIFormContainer economyActionArea = new UIFormContainer(this, 295, 48, "");
        economyActionArea.setPosition(0, getPaddedY(searchArea, innerPadding), Anchor.LEFT | Anchor.TOP);
        economyActionArea.setMovable(false);
        economyActionArea.setClosable(false);
        economyActionArea.setBorder(FontColors.WHITE, 1, 185);
        economyActionArea.setBackgroundAlpha(215);
        economyActionArea.setBottomPadding(3);
        economyActionArea.setRightPadding(3);
        economyActionArea.setTopPadding(3);
        economyActionArea.setLeftPadding(3);

        // Bottom Economy Pane - buyStack button
        final UIButton buttonBuyStack = new UIButtonBuilder(this)
                .width(40)
                .anchor(Anchor.LEFT | Anchor.BOTTOM)
                .position(0,0)
                .text("Buy Stack")
                .listener(this)
                .build("button.buyStack");

        // Bottom Economy Pane - buyStack button
        final UIButton buttonBuySingle = new UIButtonBuilder(this)
                .width(40)
                .anchor(Anchor.CENTER | Anchor.BOTTOM)
                .position(0,0)
                .text("Buy 1")
                .listener(this)
                .build("button.buySingle");

        // Bottom Economy Pane - buyStack button
        final UIButton buttonBuyQuantity = new UIButtonBuilder(this)
                .width(40)
                .anchor(Anchor.RIGHT | Anchor.BOTTOM)
                .position(0,0)
                .text("Buy Quantity")
                .listener(this)
                .build("button.buyQuantity");

        economyActionArea.add(buttonBuyStack, buttonBuySingle, buttonBuyQuantity);

        // Seller Inventory Area
        final UIFormContainer sellerInventoryArea = new UIFormContainer(this, 295, 28, "");
        sellerInventoryArea.setPosition(0, 10, Anchor.RIGHT | Anchor.TOP);
        sellerInventoryArea.setMovable(false);
        sellerInventoryArea.setClosable(false);
        sellerInventoryArea.setBorder(FontColors.WHITE, 1, 185);
        sellerInventoryArea.setBackgroundAlpha(215);
        sellerInventoryArea.setBottomPadding(3);
        sellerInventoryArea.setRightPadding(3);
        sellerInventoryArea.setTopPadding(3);
        sellerInventoryArea.setLeftPadding(3);

        final ItemStack airStack = ItemStack.of(ItemTypes.AIR, 1);

        final UISlot exchangeSlot1 = new UISlot(this, new MalisisSlot());
        exchangeSlot1.setPosition(0, 0, Anchor.LEFT | Anchor.MIDDLE);
        exchangeSlot1.setTooltip("Exchange Slot 1");

        final UISlot exchangeSlot2 = new UISlot(this, new MalisisSlot());
        exchangeSlot2.setPosition(exchangeSlot1.getX() + exchangeSlot1.getWidth() + 10, 0, Anchor.LEFT | Anchor.MIDDLE);
        exchangeSlot2.setTooltip("Exchange Slot 2");

        final UISlot exchangeSlot3 = new UISlot(this, new MalisisSlot());
        exchangeSlot3.setPosition(exchangeSlot2.getX() + exchangeSlot2.getWidth() + 10, 0, Anchor.LEFT | Anchor.MIDDLE);
        exchangeSlot3.setTooltip("Exchange Slot 3");

        final UISlot exchangeSlot4 = new UISlot(this, new MalisisSlot());
        exchangeSlot4.setPosition(exchangeSlot3.getX() + exchangeSlot3.getWidth() + 10, 0, Anchor.LEFT | Anchor.MIDDLE);
        exchangeSlot4.setTooltip("Exchange Slot 4");

        final UISlot exchangeSlot5 = new UISlot(this, new MalisisSlot());
        exchangeSlot5.setPosition(exchangeSlot4.getX() + exchangeSlot4.getWidth() + 10, 0, Anchor.LEFT | Anchor.MIDDLE);
        exchangeSlot5.setTooltip("Exchange Slot 5");

        final UISlot exchangeSlot6 = new UISlot(this, new MalisisSlot());
        exchangeSlot6.setPosition(exchangeSlot5.getX() + exchangeSlot5.getWidth() + 10, 0, Anchor.LEFT | Anchor.MIDDLE);
        exchangeSlot6.setTooltip("Exchange Slot 6");

        sellerInventoryArea.add(exchangeSlot1, exchangeSlot2, exchangeSlot3, exchangeSlot4, exchangeSlot5, exchangeSlot6);

        // Inventory Area Section (right pane)
        final UIFormContainer inventoryArea = new UIFormContainer(this, 295, 330, "");
        inventoryArea.setPosition(0, 40, Anchor.RIGHT | Anchor.TOP);
        inventoryArea.setMovable(false);
        inventoryArea.setClosable(false);
        inventoryArea.setBorder(FontColors.WHITE, 1, 185);
        inventoryArea.setBackgroundAlpha(215);
        inventoryArea.setBottomPadding(3);
        inventoryArea.setRightPadding(3);
        inventoryArea.setTopPadding(3);
        inventoryArea.setLeftPadding(3);


        // Bottom Economy Pane - buyStack button
        final UIButton buttonList = new UIButtonBuilder(this)
                .width(40)
                .anchor(Anchor.LEFT | Anchor.BOTTOM)
                .position(0,0)
                .text("List for Sale")
                .listener(this)
                .build("button.listForSaleButton");

        // Bottom Economy Pane - buyStack button
        final UIButton buttonSetPrice = new UIButtonBuilder(this)
                .width(40)
                .anchor(Anchor.CENTER | Anchor.BOTTOM)
                .position(0,0)
                .text("Set Price")
                .listener(this)
                .build("button.setPrice");

        // Bottom Economy Pane - buyStack button
        final UIButton buttonRemoveItem = new UIButtonBuilder(this)
                .width(40)
                .anchor(Anchor.RIGHT | Anchor.BOTTOM)
                .position(0,0)
                .text("Remove Item")
                .listener(this)
                .build("button.removeItem");

        inventoryArea.add(buttonList, buttonSetPrice, buttonRemoveItem);

        form.add(titleLabel, searchArea, economyActionArea, sellerInventoryArea, inventoryArea);

        addToScreen(form);
    }

    protected Consumer<Task> openWindow(String details) {  // Scheduler
        return task -> {
            if (details.equalsIgnoreCase("purchaseRequest")) {
                // Packet to send request.
            }
        };
    }

    @Override
    public void update(int mouseX, int mouseY, float partialTick) {
        super.update(mouseX, mouseY, partialTick);
        if (this.unlockMouse && this.lastUpdate == 25) {
            Mouse.setGrabbed(false); // Force the mouse to be visible even though Mouse.isGrabbed() is false.  //#BugsUnited.
            this.unlockMouse = false; // Only unlock once per session.
        } else if (this.unlockMouse) {
            this.lastUpdate++;
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
        this.lastUpdate = 0; // Reset the timer when key is typed.
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        this.lastUpdate = 0; // Reset the timer when mouse is pressed.
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // Can't stop the game otherwise the Sponge Scheduler also stops.
    }

    private void setPage(int page) {
        page = MathUtil.squashi(page, 1, this.pages);

        this.currentPage = page;

        this.buttonFirstPage.setEnabled(page != 1);
        this.buttonPreviousPage.setEnabled(page != 1);
        this.buttonNextPage.setEnabled(page != this.pages);
        this.buttonLastPage.setEnabled(page != this.pages);

        this.resultsList.setElements(this.currentResults.stream().skip((page - 1) * 10).limit(10).collect(Collectors.toList()));
    }

    public List<MockOffer> getResults(String itemName, String username, SortType sort) {
        return this.offers
                .stream()
                .filter(o -> {
                    final String itemDisplayName = o.item.getTranslation().get().toLowerCase();

                    if (!itemName.isEmpty() && !itemDisplayName.contains(itemName.toLowerCase())) {
                        return false;
                    }

                    if (!username.isEmpty() && !o.playerName.toLowerCase().contains(username.toLowerCase())) {
                        return false;
                    }

                    return true;
                })
                .sorted(sort.comparator)
                .collect(Collectors.toList());
    }

    public MockOffer createMockOffer(ItemType type) {
        return new MockOffer(Instant.now(), ItemStack.of(type, 1),
                ThreadLocalRandom.current().nextInt(0, 999), ThreadLocalRandom.current().nextDouble(0, 999),
                UUID.randomUUID(), "player" + ThreadLocalRandom.current().nextInt(0, 999));
    }

    public class MockOffer {
        public final Instant instant;
        public final ItemStack item;
        public final int quantity;
        public final double pricePer;
        public final UUID playerUuid;
        public final String playerName;

        public MockOffer(final Instant instant, final ItemStack item, final int quantity,
                         final double pricePer, final UUID playerUuid, final String playerName) {
            this.instant = instant;
            this.item = item;
            this.quantity = quantity;
            this.pricePer = pricePer;
            this.playerUuid = playerUuid;
            this.playerName = playerName;
        }
    }

    private static final class ResultListElement extends UIBackgroundContainer {

        private static final int BORDER_COLOR = org.spongepowered.api.util.Color.ofRgb(128, 128, 128).getRgb();
        private static final int INNER_COLOR = org.spongepowered.api.util.Color.ofRgb(0, 0, 0).getRgb();
        private static final int INNER_HOVER_COLOR = org.spongepowered.api.util.Color.ofRgb(40, 40, 40).getRgb();
        private static final int INNER_SELECTED_COLOR = org.spongepowered.api.util.Color.ofRgb(65, 65, 65).getRgb();

        private final ResultListElementData elementData;
        private final UIImage image;
        private final UILabel itemLabel, quantityLabel, sellerLabel;

        private ResultListElement(MalisisGui gui, ResultListElementData elementData) {
            super(gui);

            this.elementData = elementData;

            this.parent = this.elementData.getParent();

            // Set padding
            this.setPadding(3, 3);

            // Set colors
            this.setColor(INNER_COLOR);
            this.setBorder(BORDER_COLOR, 1, 255);

            this.setSize(0, 24);

            // Add components
            this.image = new UIImage(gui, (net.minecraft.item.ItemStack) (Object) elementData.offer.item);
            this.image.setPosition(0, 0, Anchor.LEFT | Anchor.MIDDLE);

            this.itemLabel = new UILabel(gui, TextSerializers.LEGACY_FORMATTING_CODE.serialize(elementData.itemText));
            this.itemLabel.setPosition(getPaddedX(this.image, 4), 0, Anchor.LEFT | Anchor.TOP);

            this.quantityLabel = new UILabel(gui, TextSerializers.LEGACY_FORMATTING_CODE.serialize(elementData.quantityText));
            this.quantityLabel.setPosition(getPaddedX(this.image, 4), 0, Anchor.LEFT | Anchor.BOTTOM);

            this.sellerLabel = new UILabel(gui, TextSerializers.LEGACY_FORMATTING_CODE.serialize(elementData.sellerText));
            this.sellerLabel.setPosition(-innerPadding, 0, Anchor.RIGHT | Anchor.MIDDLE);

            this.add(this.image, this.itemLabel, this.quantityLabel, this.sellerLabel);
        }

        private ResultListElementData getElementData() {
            return this.elementData;
        }

        @Override
        public boolean onClick(int x, int y) {
            final UIComponent component = getComponentAt(x, y);
            if (this.equals(component)) {
                this.elementData.getParent().select(this.elementData);
            }
            return true;
        }

        @Override
        public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick) {
            if (this.parent instanceof UISimpleList) {
                final UISimpleList parent = (UISimpleList) this.parent;

                final int width = parent.getContentWidth() - (parent.getScrollBar().isEnabled() ? parent.getScrollBar().getRawWidth() + 1 : 0);

                setSize(width, getHeight());


                if (parent.isSelected(this.elementData)) {
                    this.setColor(INNER_SELECTED_COLOR);
                } else if (this.isHovered()) {
                    this.setColor(INNER_HOVER_COLOR);
                } else {
                    this.setColor(INNER_COLOR);
                }

                super.drawBackground(renderer, mouseX, mouseY, partialTick);
            }
        }
    }

    private static final class ResultListElementData {

        public final UISimpleList<ResultListElementData> parent;
        public final Text itemText;
        public final Text sellerText;
        public final Text quantityText;
        public final MockOffer offer;

        private ResultListElementData(UISimpleList<ResultListElementData> parent, MockOffer offer) {
            this.parent = parent;
            this.offer = offer;
            this.itemText = Text.of(TextColors.WHITE, offer.item.getTranslation().get());
            this.sellerText = Text.of(TextStyles.ITALIC, TextColors.GRAY, "by ", offer.playerName);
            this.quantityText = Text.of(TextColors.GRAY, "x ", offer.quantity);
        }

        public UISimpleList<ResultListElementData> getParent() {
            return this.parent;
        }
    }

    public enum SortType {
        OLDEST("Oldest", (a, b) -> a.instant.compareTo(b.instant)),
        NEWEST("Newest", (a, b) -> b.instant.compareTo(a.instant)),
        PRICE_ASC("Price (Asc.)", (a, b) -> Double.compare(a.pricePer, b.pricePer)),
        PRICE_DESC("Price (Desc.)", (a, b) -> Double.compare(b.pricePer, a.pricePer)),
        ITEM_ASC("Item (Asc.)", (a, b) -> a.item.getTranslation().get().compareTo(b.item.getTranslation().get())),
        ITEM_DESC("Item (Desc.)", (a, b) -> b.item.getTranslation().get().compareTo(a.item.getTranslation().get())),
        PLAYER_ASC("Player (Asc.)", (a, b) -> a.playerName.compareTo(b.playerName)),
        PLAYER_DESC("Player (Desc.)", (a, b) -> b.playerName.compareTo(a.playerName));

        public final String displayName;
        public final Comparator<MockOffer> comparator;
        SortType(String displayName, Comparator<MockOffer> comparator) {
            this.displayName = displayName;
            this.comparator = comparator;
        }
    }
}
