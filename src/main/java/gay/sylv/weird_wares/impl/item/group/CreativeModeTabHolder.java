package gay.sylv.weird_wares.impl.item.group;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

@org.jetbrains.annotations.ApiStatus.Internal
public record CreativeModeTabHolder(CreativeModeTab creativeModeTab, ResourceKey<CreativeModeTab> resourceKey) {
}
