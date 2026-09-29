package net.jewelry.blocks;

import net.jewelry.gems.GemCutRegistry;
import net.jewelry.gems.GemCuttingScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class JewelersKitBlock extends Block {
    public JewelersKitBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    // MARK: Gem cutting screen

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!GemCutRegistry.isEnabled(world)) {
            return InteractionResult.PASS; // gem cuts disabled on this server: plain workstation block
        }
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(state.getMenuProvider(world, pos));
        return InteractionResult.CONSUME;
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider(
                (syncId, playerInventory, player) -> new GemCuttingScreenHandler(syncId, playerInventory, ContainerLevelAccess.create(world, pos)),
                Component.translatable(this.getDescriptionId()));
    }

    // MARK: Facing

    // 1.21.11: `DirectionProperty` is gone, `Properties.HORIZONTAL_FACING` is an `EnumProperty<Direction>`.
    private static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // MARK: Partial transparency

    // 1.21.11: `isTranslucent(state, world, pos)` → `isTransparent(state)`.
    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    // The "Workbench for Jeweler Villagers." hint used to live in `Block#appendTooltip`, which no longer
    // exists in 1.21.11 — it moved onto the block's item (see `JewelryBlockItem`).
}
