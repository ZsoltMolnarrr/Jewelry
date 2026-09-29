package net.jewelry.mixin;

import net.jewelry.gems.GemComponents;
import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataComponentTypes.class)
public class DataComponentTypesMixin {
    /// Registers Jewelry's component types at the tail of vanilla's own component bootstrap — before any
    /// mod registers an item, on both loaders and regardless of mod order. That lets other mods resolve
    /// `jewelry:sockets` by id while building their items (see README, "Giving your items sockets") without
    /// depending on Jewelry. Same trick SpellEngine uses for its component types.
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit_TAIL_Jewelry(CallbackInfo ci) {
        GemComponents.register();
    }
}
