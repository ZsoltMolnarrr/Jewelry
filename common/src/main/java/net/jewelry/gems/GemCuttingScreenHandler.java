package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.util.SoundHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/// The Jeweler's Kit screen: the stonecutter's handler with the recipe list replaced by the `gem_cut`
/// registry. Put a raw gem in, pick one of its cuts, take the cut gem out; each take consumes one raw gem.
///
/// Layout, slot indices, quick-move rules and the selected-index property are the stonecutter's, so the
/// screen can reuse its textures unchanged.
public class GemCuttingScreenHandler extends AbstractContainerMenu {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(JewelryMod.ID, "gem_cutting");
    public static final MenuType<GemCuttingScreenHandler> HANDLER_TYPE =
            new MenuType<>(GemCuttingScreenHandler::new, FeatureFlags.VANILLA_SET);

    public static final int INPUT_ID = 0;
    public static final int OUTPUT_ID = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = 29;
    private static final int HOTBAR_START = 29;
    private static final int HOTBAR_END = 38;

    private final ContainerLevelAccess context;
    private final DataSlot selectedCut = DataSlot.standalone();
    private final Level world;
    private List<Holder<GemCut>> availableCuts = new ArrayList<>();
    private List<ItemStack> availablePreviews = new ArrayList<>();
    private ItemStack inputStack = ItemStack.EMPTY;
    private long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    Runnable contentsChangedListener = () -> {};
    public final Container input = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            GemCuttingScreenHandler.this.slotsChanged(this);
            GemCuttingScreenHandler.this.contentsChangedListener.run();
        }
    };
    final ResultContainer output = new ResultContainer();

    public GemCuttingScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public GemCuttingScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(HANDLER_TYPE, syncId);
        this.context = context;
        this.world = playerInventory.player.level();
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player, stack.getCount());
                ItemStack remaining = GemCuttingScreenHandler.this.inputSlot.remove(1);
                if (!remaining.isEmpty()) {
                    GemCuttingScreenHandler.this.populateResult();
                }
                context.execute((world, pos) -> {
                    long time = world.getGameTime();
                    if (GemCuttingScreenHandler.this.lastTakeTime != time) {
                        world.playSound(null, pos, SoundHelper.JEWELRY_WORKBENCH, SoundSource.BLOCKS, 1.0F, 1.0F);
                        GemCuttingScreenHandler.this.lastTakeTime = time;
                    }
                });
                super.onTake(player, stack);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }

        this.addDataSlot(this.selectedCut);
    }

    // MARK: State for the screen

    public int getSelectedCut() {
        return this.selectedCut.get();
    }

    public List<Holder<GemCut>> getAvailableCuts() {
        return this.availableCuts;
    }

    /// One cut gem stack per available cut, in the same order — what the grid draws and the tooltip shows.
    public List<ItemStack> getAvailablePreviews() {
        return this.availablePreviews;
    }

    public int getAvailableCutCount() {
        return this.availableCuts.size();
    }

    public boolean canCraft() {
        return this.inputSlot.hasItem() && !this.availableCuts.isEmpty();
    }

    public void setContentsChangedListener(Runnable listener) {
        this.contentsChangedListener = listener;
    }

    // MARK: Cut lookup

    /// Cuts that apply to `stack`: a raw gem (no `cut` component yet) whose item some registered cut targets.
    public static List<Holder<GemCut>> cutsFor(Level world, ItemStack stack) {
        if (stack.isEmpty() || GemCut.of(stack).isPresent()) {
            return List.of();
        }
        return GemCutRegistry.from(world).listElements()
                .filter(entry -> entry.value().gem().value() == stack.getItem())
                .sorted(Comparator.comparing(entry -> entry.key().identifier()))
                .map(entry -> (Holder<GemCut>) entry)
                .toList();
    }

    // MARK: ScreenHandler

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, JewelryBlocks.JEWELERS_KIT.block());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isInBounds(id)) {
            this.selectedCut.set(id);
            this.populateResult();
        }
        return true;
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableCuts.size();
    }

    @Override
    public void slotsChanged(Container inventory) {
        ItemStack stack = this.inputSlot.getItem();
        // Item AND components: a cut gem of the same item must not be treated as more of the raw gem
        if (!ItemStack.isSameItemSameComponents(stack, this.inputStack)) {
            this.inputStack = stack.copy();
            this.updateInput(stack);
        }
    }

    private void updateInput(ItemStack stack) {
        this.availableCuts = new ArrayList<>(cutsFor(this.world, stack));
        this.availablePreviews = this.availableCuts.stream().map(GemCut::stack).toList();
        this.selectedCut.set(-1);
        this.outputSlot.set(ItemStack.EMPTY);
    }

    void populateResult() {
        if (!this.availableCuts.isEmpty() && this.isInBounds(this.selectedCut.get())) {
            var cut = this.availableCuts.get(this.selectedCut.get());
            this.outputSlot.set(GemCut.stack(cut));
        } else {
            this.outputSlot.set(ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    @Override
    public MenuType<?> getType() {
        return HANDLER_TYPE;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.output && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (slotIndex == OUTPUT_ID) {
                stack.getItem().onCraftedBy(stack, player);
                if (!this.moveItemStackTo(stack, INVENTORY_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, result);
            } else if (slotIndex == INPUT_ID) {
                if (!this.moveItemStackTo(stack, INVENTORY_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!cutsFor(this.world, stack).isEmpty()) {
                if (!this.moveItemStackTo(stack, INPUT_ID, INPUT_ID + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= INVENTORY_START && slotIndex < INVENTORY_END) {
                if (!this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= HOTBAR_START && slotIndex < HOTBAR_END
                    && !this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }
            slot.setChanged();
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
            this.broadcastChanges();
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.output.removeItemNoUpdate(1);
        this.context.execute((world, pos) -> this.clearContainer(player, this.input));
    }
}
