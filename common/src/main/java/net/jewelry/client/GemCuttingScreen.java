package net.jewelry.client;

import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/// The stonecutter screen (1.21.11 rendition), verbatim, drawing cut gems in the grid instead of recipe results.
/// Reuses the vanilla stonecutter textures, so any resource pack restyling the stonecutter restyles this too.
// No `@Environment`: Architectury turns it into `@OnlyIn`, which NeoForge 21.11+ warns about at load.
public class GemCuttingScreen extends HandledScreen<GemCuttingScreenHandler> {
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/stonecutter/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/stonecutter/scroller_disabled");
    private static final Identifier RECIPE_SELECTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_selected");
    private static final Identifier RECIPE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe_highlighted");
    private static final Identifier RECIPE_TEXTURE = Identifier.ofVanilla("container/stonecutter/recipe");
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/stonecutter.png");
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

    public GemCuttingScreen(GemCuttingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
        this.titleY--;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
        int scrollerY = (int) (41.0F * this.scrollAmount);
        Identifier scroller = this.shouldScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        int scrollerLeft = x + 119;
        int scrollerTop = y + 15 + scrollerY;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, scroller, scrollerLeft, scrollerTop, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
        if (mouseX >= scrollerLeft && mouseX < scrollerLeft + SCROLLBAR_WIDTH && mouseY >= scrollerTop && mouseY < scrollerTop + SCROLLBAR_HEIGHT) {
            context.setCursor(this.mouseClicked ? StandardCursors.RESIZE_NS : StandardCursors.POINTING_HAND);
        }
        int listX = this.x + LIST_OFFSET_X;
        int listY = this.y + LIST_OFFSET_Y;
        int end = this.scrollOffset + LIST_COLUMNS * LIST_ROWS;
        this.renderCutBackground(context, mouseX, mouseY, listX, listY, end);
        this.renderCutIcons(context, listX, listY, end);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        super.drawMouseoverTooltip(context, x, y);
        if (this.canCraft) {
            int listX = this.x + LIST_OFFSET_X;
            int listY = this.y + LIST_OFFSET_Y;
            int end = this.scrollOffset + LIST_COLUMNS * LIST_ROWS;
            var previews = this.handler.getAvailablePreviews();
            for (int i = this.scrollOffset; i < end && i < this.handler.getAvailableCutCount(); i++) {
                int index = i - this.scrollOffset;
                int cellX = listX + index % LIST_COLUMNS * ENTRY_WIDTH;
                int cellY = listY + index / LIST_COLUMNS * ENTRY_HEIGHT + 2;
                if (x >= cellX && x < cellX + ENTRY_WIDTH && y >= cellY && y < cellY + ENTRY_HEIGHT) {
                    context.drawItemTooltip(this.textRenderer, previews.get(i), x, y);
                }
            }
        }
    }

    private void renderCutBackground(DrawContext context, int mouseX, int mouseY, int listX, int listY, int end) {
        for (int i = this.scrollOffset; i < end && i < this.handler.getAvailableCutCount(); i++) {
            int index = i - this.scrollOffset;
            int cellX = listX + index % LIST_COLUMNS * ENTRY_WIDTH;
            int cellY = listY + index / LIST_COLUMNS * ENTRY_HEIGHT + 2;
            Identifier texture;
            if (i == this.handler.getSelectedCut()) {
                texture = RECIPE_SELECTED_TEXTURE;
            } else if (mouseX >= cellX && mouseY >= cellY && mouseX < cellX + ENTRY_WIDTH && mouseY < cellY + ENTRY_HEIGHT) {
                texture = RECIPE_HIGHLIGHTED_TEXTURE;
            } else {
                texture = RECIPE_TEXTURE;
            }
            int cellTop = cellY - 1;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, cellX, cellTop, ENTRY_WIDTH, ENTRY_HEIGHT);
            if (mouseX >= cellX && mouseY >= cellTop && mouseX < cellX + ENTRY_WIDTH && mouseY < cellTop + ENTRY_HEIGHT) {
                context.setCursor(StandardCursors.POINTING_HAND);
            }
        }
    }

    private void renderCutIcons(DrawContext context, int listX, int listY, int end) {
        var previews = this.handler.getAvailablePreviews();
        for (int i = this.scrollOffset; i < end && i < this.handler.getAvailableCutCount(); i++) {
            int index = i - this.scrollOffset;
            int cellX = listX + index % LIST_COLUMNS * ENTRY_WIDTH;
            int cellY = listY + index / LIST_COLUMNS * ENTRY_HEIGHT + 2;
            context.drawItem(previews.get(i), cellX, cellY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (this.canCraft) {
            int listX = this.x + LIST_OFFSET_X;
            int listY = this.y + LIST_OFFSET_Y;
            int end = this.scrollOffset + LIST_COLUMNS * LIST_ROWS;
            for (int i = this.scrollOffset; i < end; i++) {
                int index = i - this.scrollOffset;
                double dx = mouseX - (listX + index % LIST_COLUMNS * ENTRY_WIDTH);
                double dy = mouseY - (listY + index / LIST_COLUMNS * ENTRY_HEIGHT);
                if (dx >= 0.0 && dy >= 0.0 && dx < ENTRY_WIDTH && dy < ENTRY_HEIGHT
                        && this.handler.onButtonClick(this.client.player, i)) {
                    MinecraftClient.getInstance().getSoundManager()
                            .play(PositionedSoundInstance.master(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.client.interactionManager.clickButton(this.handler.syncId, i);
                    return true;
                }
            }
            int barX = this.x + 119;
            int barY = this.y + 9;
            if (mouseX >= barX && mouseX < barX + SCROLLBAR_WIDTH && mouseY >= barY && mouseY < barY + SCROLLBAR_AREA_HEIGHT) {
                this.mouseClicked = true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int top = this.y + LIST_OFFSET_Y;
            int bottom = top + SCROLLBAR_AREA_HEIGHT;
            this.scrollAmount = ((float) click.y() - top - 7.5F) / (bottom - top - 15.0F);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) (this.scrollAmount * this.getMaxScroll() + 0.5) * LIST_COLUMNS;
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
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
            this.scrollAmount = MathHelper.clamp(this.scrollAmount - step, 0.0F, 1.0F);
            this.scrollOffset = (int) (this.scrollAmount * maxScroll + 0.5) * LIST_COLUMNS;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && this.handler.getAvailableCutCount() > LIST_COLUMNS * LIST_ROWS;
    }

    protected int getMaxScroll() {
        return (this.handler.getAvailableCutCount() + LIST_COLUMNS - 1) / LIST_COLUMNS - LIST_ROWS;
    }

    private void onInventoryChange() {
        this.canCraft = this.handler.canCraft();
        if (!this.canCraft) {
            this.scrollAmount = 0.0F;
            this.scrollOffset = 0;
        }
    }
}
