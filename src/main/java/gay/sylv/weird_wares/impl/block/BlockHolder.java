/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Holds a {@link Block} and a {@link BlockItem}.
 */
@org.jetbrains.annotations.ApiStatus.Internal
public record BlockHolder<B extends Block, I extends ItemLike>(@NotNull B block, @NotNull I item) {
}
