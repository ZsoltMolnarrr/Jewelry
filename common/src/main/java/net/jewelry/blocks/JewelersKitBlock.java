package net.jewelry.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
