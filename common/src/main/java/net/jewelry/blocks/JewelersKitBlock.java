package net.jewelry.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class JewelersKitBlock extends Block {
    public JewelersKitBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    // MARK: Gem cutting screen

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!GemCutRegistry.isEnabled(world)) {
            return ActionResult.PASS; // gem cuts disabled on this server: plain workstation block
        }
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.CONSUME;
    }

    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, playerInventory, player) -> new GemCuttingScreenHandler(syncId, playerInventory, ScreenHandlerContext.create(world, pos)),
                Text.translatable(this.getTranslationKey()));
    }

    // MARK: Facing

    // 1.21.11: `DirectionProperty` is gone, `Properties.HORIZONTAL_FACING` is an `EnumProperty<Direction>`.
    private static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // MARK: Partial transparency

    // 1.21.11: `isTranslucent(state, world, pos)` → `isTransparent(state)`.
    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }

    // The "Workbench for Jeweler Villagers." hint used to live in `Block#appendTooltip`, which no longer
    // exists in 1.21.11 — it moved onto the block's item (see `JewelryBlockItem`).
}
