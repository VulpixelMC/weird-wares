/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.item.group;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

@org.jetbrains.annotations.ApiStatus.Internal
public record CreativeModeTabHolder(CreativeModeTab creativeModeTab, ResourceKey<CreativeModeTab> resourceKey) {
}
