package net.jewelry.gems;

import net.jewelry.JewelryMod;
import net.jewelry.blocks.JewelryBlocks;
import net.jewelry.util.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/// The Jeweler's Kit screen: the stonecutter's handler with the recipe list replaced by the `gem_cut`
/// registry. Put a raw gem in, pick one of its cuts, take the cut gem out; each take consumes one raw gem.
///
/// Layout, slot indices, quick-move rules and the selected-index property are the stonecutter's, so the
/// screen can reuse its textures unchanged.
public class GemCuttingScreenHandler extends ScreenHandler {
    public static final Identifier ID = Identifier.of(JewelryMod.ID, "gem_cutting");
    public static final ScreenHandlerType<GemCuttingScreenHandler> HANDLER_TYPE =
            new ScreenHandlerType<>(GemCuttingScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    public static final int INPUT_ID = 0;
    public static final int OUTPUT_ID = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = 29;
    private static final int HOTBAR_START = 29;
    private static final int HOTBAR_END = 38;

    private final ScreenHandlerContext context;
    private final Property selectedCut = Property.create();
    private final World world;
    private List<RegistryEntry<GemCut>> availableCuts = new ArrayList<>();
    private List<ItemStack> availablePreviews = new ArrayList<>();
    private ItemStack inputStack = ItemStack.EMPTY;
    private long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    Runnable contentsChangedListener = () -> {};
    public final Inventory input = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            GemCuttingScreenHandler.this.onContentChanged(this);
            GemCuttingScreenHandler.this.contentsChangedListener.run();
        }
    };
    final CraftingResultInventory output = new CraftingResultInventory();

    public GemCuttingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public GemCuttingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(HANDLER_TYPE, syncId);
        this.context = context;
        this.world = playerInventory.player.getWorld();
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                stack.onCraftByPlayer(player.getWorld(), player, stack.getCount());
                ItemStack remaining = GemCuttingScreenHandler.this.inputSlot.takeStack(1);
                if (!remaining.isEmpty()) {
                    GemCuttingScreenHandler.this.populateResult();
                }
                context.run((world, pos) -> {
                    long time = world.getTime();
                    if (GemCuttingScreenHandler.this.lastTakeTime != time) {
                        world.playSound(null, pos, SoundHelper.JEWELRY_WORKBENCH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        GemCuttingScreenHandler.this.lastTakeTime = time;
                    }
                });
                super.onTakeItem(player, stack);
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

        this.addProperty(this.selectedCut);
    }

    // MARK: State for the screen

    public int getSelectedCut() {
        return this.selectedCut.get();
    }

    public List<RegistryEntry<GemCut>> getAvailableCuts() {
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
        return this.inputSlot.hasStack() && !this.availableCuts.isEmpty();
    }

    public void setContentsChangedListener(Runnable listener) {
        this.contentsChangedListener = listener;
    }

    // MARK: Cut lookup

    /// Cuts that apply to `stack`: a raw gem (no `cut` component yet) whose item some registered cut targets.
    public static List<RegistryEntry<GemCut>> cutsFor(World world, ItemStack stack) {
        if (stack.isEmpty() || GemCut.of(stack).isPresent()) {
            return List.of();
        }
        return GemCutRegistry.from(world).streamEntries()
                .filter(entry -> entry.value().gem().value() == stack.getItem())
                .sorted(Comparator.comparing(entry -> entry.registryKey().getValue()))
                .map(entry -> (RegistryEntry<GemCut>) entry)
                .toList();
    }

    // MARK: ScreenHandler

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, JewelryBlocks.JEWELERS_KIT.block());
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
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
    public void onContentChanged(Inventory inventory) {
        ItemStack stack = this.inputSlot.getStack();
        // Item AND components: a cut gem of the same item must not be treated as more of the raw gem
        if (!ItemStack.areItemsAndComponentsEqual(stack, this.inputStack)) {
            this.inputStack = stack.copy();
            this.updateInput(stack);
        }
    }

    private void updateInput(ItemStack stack) {
        this.availableCuts = new ArrayList<>(cutsFor(this.world, stack));
        this.availablePreviews = this.availableCuts.stream().map(GemCut::stack).toList();
        this.selectedCut.set(-1);
        this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
    }

    void populateResult() {
        if (!this.availableCuts.isEmpty() && this.isInBounds(this.selectedCut.get())) {
            var cut = this.availableCuts.get(this.selectedCut.get());
            this.outputSlot.setStackNoCallbacks(GemCut.stack(cut));
        } else {
            this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return HANDLER_TYPE;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (slotIndex == OUTPUT_ID) {
                stack.getItem().onCraftByPlayer(stack, player.getWorld(), player);
                if (!this.insertItem(stack, INVENTORY_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(stack, result);
            } else if (slotIndex == INPUT_ID) {
                if (!this.insertItem(stack, INVENTORY_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!cutsFor(this.world, stack).isEmpty()) {
                if (!this.insertItem(stack, INPUT_ID, INPUT_ID + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= INVENTORY_START && slotIndex < INVENTORY_END) {
                if (!this.insertItem(stack, HOTBAR_START, HOTBAR_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= HOTBAR_START && slotIndex < HOTBAR_END
                    && !this.insertItem(stack, INVENTORY_START, INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            slot.markDirty();
            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, stack);
            this.sendContentUpdates();
        }
        return result;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.output.removeStack(1);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }
}
