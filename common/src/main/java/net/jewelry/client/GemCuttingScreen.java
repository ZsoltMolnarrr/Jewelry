package net.jewelry.client;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/// The stonecutter screen (26.1 rendition), verbatim, drawing cut gems in the grid instead of recipe results.
/// Reuses the vanilla stonecutter textures, so any resource pack restyling the stonecutter restyles this too.
// No `@Environment`: Architectury turns it into `@OnlyIn`, which NeoForge 21.11+ warns about at load.
public class GemCuttingScreen extends AbstractContainerScreen<GemCuttingScreenHandler> {
    private static final Identifier SCROLLER_TEXTURE = Identifier.withDefaultNamespace("container/stonecutter/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.withDefaultNamespace("container/stonecutter/scroller_disabled");
    private static final Identifier RECIPE_SELECTED_TEXTURE = Identifier.withDefaultNamespace("container/stonecutter/recipe_selected");
    private static final Identifier RECIPE_HIGHLIGHTED_TEXTURE = Identifier.withDefaultNamespace("container/stonecutter/recipe_highlighted");
    private static final Identifier RECIPE_TEXTURE = Identifier.withDefaultNamespace("container/stonecutter/recipe");
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/stonecutter.png");
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int SCROLLBAR_HEIGHT = 15;
    private static final int LIST_COLUMNS = 4;
    private static final int LIST_ROWS = 3;
    private static final int ENTRY_WIDTH = 16;
    private static final int ENTRY_HEIGHT = 18;
    private static final int SCROLLBAR_AREA_HEIGHT = 54;
    private static final int LIST_OFFSET_X = 52;
    private static final int LIST_OFFSET_Y = 14;
    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;

    public GemCuttingScreen(GemCuttingScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
        this.titleLabelY--;
    }

    // 26.1: screens extract render state; the background hook calls up, and the tooltip pass is the base class's.
    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        int x = this.leftPos;
        int y = this.topPos;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        int scrollerY = (int) (41.0F * this.scrollAmount);
        Identifier scroller = this.shouldScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        int scrollerLeft = x + 119;
        int scrollerTop = y + 15;
        context.blitSprite(RenderPipelines.GUI_TEXTURED, scroller, scrollerLeft, scrollerTop + scrollerY, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
        if (mouseX >= scrollerLeft && mouseY >= scrollerTop && mouseX < scrollerLeft + SCROLLBAR_WIDTH && mouseY < scrollerTop + SCROLLBAR_AREA_HEIGHT) {
            if (this.shouldScroll()) {
                context.requestCursor(this.mouseClicked ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
            } else {
                context.requestCursor(CursorTypes.NOT_ALLOWED);
            }
        }
        int listX = this.leftPos + LIST_OFFSET_X;
        int listY = this.topPos + LIST_OFFSET_Y;
        int end = this.scrollOffset + LIST_COLUMNS * LIST_ROWS;
        this.renderCutBackground(context, mouseX, mouseY, listX, listY, end);
        this.renderCutIcons(context, listX, listY, end);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor context, int x, int y) {
        super.extractTooltip(context, x, y);
        if (this.canCraft) {
            int listX = this.leftPos + LIST_OFFSET_X;
            int listY = this.topPos + LIST_OFFSET_Y;
            int end = this.scrollOffset + LIST_COLUMNS * LIST_ROWS;
            var previews = this.menu.getAvailablePreviews();
            for (int i = this.scrollOffset; i < end && i < this.menu.getAvailableCutCount(); i++) {
                int index = i - this.scrollOffset;
                int cellX = listX + index % LIST_COLUMNS * ENTRY_WIDTH;
                int cellY = listY + index / LIST_COLUMNS * ENTRY_HEIGHT + 2;
                if (x >= cellX && x < cellX + ENTRY_WIDTH && y >= cellY && y < cellY + ENTRY_HEIGHT) {
                    context.setTooltipForNextFrame(this.font, previews.get(i), x, y);
                }
            }
        }
    }

    private void renderCutBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, int listX, int listY, int end) {
        for (int i = this.scrollOffset; i < end && i < this.menu.getAvailableCutCount(); i++) {
            int index = i - this.scrollOffset;
            int cellX = listX + index % LIST_COLUMNS * ENTRY_WIDTH;
            int cellY = listY + index / LIST_COLUMNS * ENTRY_HEIGHT + 2;
            Identifier texture;
            if (i == this.menu.getSelectedCut()) {
                texture = RECIPE_SELECTED_TEXTURE;
            } else if (mouseX >= cellX && mouseY >= cellY && mouseX < cellX + ENTRY_WIDTH && mouseY < cellY + ENTRY_HEIGHT) {
                texture = RECIPE_HIGHLIGHTED_TEXTURE;
            } else {
                texture = RECIPE_TEXTURE;
            }
            int cellTop = cellY - 1;
            context.blitSprite(RenderPipelines.GUI_TEXTURED, texture, cellX, cellTop, ENTRY_WIDTH, ENTRY_HEIGHT);
            if (mouseX >= cellX && mouseY >= cellTop && mouseX < cellX + ENTRY_WIDTH && mouseY < cellTop + ENTRY_HEIGHT) {
                context.requestCursor(CursorTypes.POINTING_HAND);
            }
        }
    }

    private void renderCutIcons(GuiGraphicsExtractor context, int listX, int listY, int end) {
        var previews = this.menu.getAvailablePreviews();
        for (int i = this.scrollOffset; i < end && i < this.menu.getAvailableCutCount(); i++) {
            int index = i - this.scrollOffset;
            int cellX = listX + index % LIST_COLUMNS * ENTRY_WIDTH;
            int cellY = listY + index / LIST_COLUMNS * ENTRY_HEIGHT + 2;
            context.item(previews.get(i), cellX, cellY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (this.canCraft) {
            int listX = this.leftPos + LIST_OFFSET_X;
            int listY = this.topPos + LIST_OFFSET_Y;
            int end = this.scrollOffset + LIST_COLUMNS * LIST_ROWS;
            for (int i = this.scrollOffset; i < end; i++) {
                int index = i - this.scrollOffset;
                double dx = mouseX - (listX + index % LIST_COLUMNS * ENTRY_WIDTH);
                double dy = mouseY - (listY + index / LIST_COLUMNS * ENTRY_HEIGHT);
                if (dx >= 0.0 && dy >= 0.0 && dx < ENTRY_WIDTH && dy < ENTRY_HEIGHT
                        && this.menu.clickMenuButton(this.minecraft.player, i)) {
                    Minecraft.getInstance().getSoundManager()
                            .play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    return true;
                }
            }
            int barX = this.leftPos + 119;
            int barY = this.topPos + 9;
            if (mouseX >= barX && mouseX < barX + SCROLLBAR_WIDTH && mouseY >= barY && mouseY < barY + SCROLLBAR_AREA_HEIGHT) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int top = this.topPos + LIST_OFFSET_Y;
            int bottom = top + SCROLLBAR_AREA_HEIGHT;
            this.scrollAmount = ((float) click.y() - top - 7.5F) / (bottom - top - 15.0F);
            this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) (this.scrollAmount * this.getMaxScroll() + 0.5) * LIST_COLUMNS;
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        this.mouseClicked = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        if (this.shouldScroll()) {
            int maxScroll = this.getMaxScroll();
            float step = (float) verticalAmount / maxScroll;
            this.scrollAmount = Mth.clamp(this.scrollAmount - step, 0.0F, 1.0F);
            this.scrollOffset = (int) (this.scrollAmount * maxScroll + 0.5) * LIST_COLUMNS;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && this.menu.getAvailableCutCount() > LIST_COLUMNS * LIST_ROWS;
    }

    protected int getMaxScroll() {
        return (this.menu.getAvailableCutCount() + LIST_COLUMNS - 1) / LIST_COLUMNS - LIST_ROWS;
    }

    private void onInventoryChange() {
        this.canCraft = this.menu.canCraft();
        if (!this.canCraft) {
            this.scrollAmount = 0.0F;
            this.scrollOffset = 0;
        }
    }
}
