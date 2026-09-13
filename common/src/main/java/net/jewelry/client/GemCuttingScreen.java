package net.jewelry.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/// The stonecutter screen, verbatim, drawing cut gems in the grid instead of recipe results.
/// Reuses the vanilla stonecutter textures, so any resource pack restyling the stonecutter restyles this too.
@Environment(EnvType.CLIENT)
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
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int scrollerY = (int) (41.0F * this.scrollAmount);
        Identifier scroller = this.shouldScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.drawGuiTexture(scroller, x + 119, y + 15 + scrollerY, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
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
            context.drawGuiTexture(texture, cellX, cellY - 1, ENTRY_WIDTH, ENTRY_HEIGHT);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseClicked = false;
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
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int top = this.y + LIST_OFFSET_Y;
            int bottom = top + SCROLLBAR_AREA_HEIGHT;
            this.scrollAmount = ((float) mouseY - top - 7.5F) / (bottom - top - 15.0F);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) (this.scrollAmount * this.getMaxScroll() + 0.5) * LIST_COLUMNS;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
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
