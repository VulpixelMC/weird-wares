/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.util;

import gay.sylv.weird_wares.impl.block.BlockHolder;
import gay.sylv.weird_wares.impl.block.entity.type.BlockEntityHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Conversions {
	private Conversions() {}
	
	public static <B extends Block, I extends Item, BE extends BlockEntity> BlockEntityHolder<B, I, BE> convert(BlockHolder<B, I> holder, BlockEntityType<BE> type) {
		return new BlockEntityHolder<>(holder.block(), holder.item(), type);
	}
}
