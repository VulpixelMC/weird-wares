/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.block.entity.type;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Holds a {@link Block} and a {@link BlockItem} and a {@link BlockEntityType}.
 */
@org.jetbrains.annotations.ApiStatus.Internal
public record BlockEntityHolder<B extends Block, I extends Item, BE extends BlockEntity>(B block, I item, BlockEntityType<BE> type) {
}
