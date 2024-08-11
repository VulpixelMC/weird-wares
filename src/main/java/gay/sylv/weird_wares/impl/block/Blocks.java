/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.block;

import gay.sylv.weird_wares.impl.block.NetherReactorBlock.NetherReactorBlockEntity;
import gay.sylv.weird_wares.impl.block.entity.type.BlockEntityHolder;
import gay.sylv.weird_wares.impl.util.Constants;
import gay.sylv.weird_wares.impl.util.Conversions;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Blocks implements Initializable {
	public static final Blocks INSTANCE = new Blocks();
	
	public static BlockHolder<Block, BlockItem> UNKNOWN;
	public static BlockHolder<Block, BlockItem> INFO_UPDATE;
	public static BlockHolder<Block, BlockItem> INFO_UPDATE2;
	public static BlockHolder<Block, BlockItem> GLOWING_OBSIDIAN;
	
	public static BlockEntityHolder<NetherReactorBlock, BlockItem, NetherReactorBlockEntity> NETHER_REACTOR;
	
	private Blocks() {}
	
	@Override
	public void initialize() {
		UNKNOWN = register(
				"unknown",
				new Block(
						BlockBehaviour.Properties.of()
								.isValidSpawn((blockState, blockGetter, blockPos, entityType) -> true)
								.instabreak()
								.mapColor(MapColor.DIRT)
								.friction(0.98f)
				)
		);
		INFO_UPDATE = register(
				"info_update",
				new Block(
						BlockBehaviour.Properties.of()
								.sound(SoundType.GRAVEL)
								.isValidSpawn((blockState, blockGetter, blockPos, entityType) -> true)
								.strength(0.0f, Constants.INFINITE_BLAST_RESISTANCE)
								.mapColor(MapColor.COLOR_LIGHT_GREEN)
				)
		);
		INFO_UPDATE2 = register(
				"info_update2",
				new Block(
						BlockBehaviour.Properties.ofFullCopy(INFO_UPDATE.block())
				)
		);
		GLOWING_OBSIDIAN = register(
				"glowing_obsidian",
				new Block(
						BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OBSIDIAN)
								.emissiveRendering((blockState, blockGetter, blockPos) -> true)
								.lightLevel(blockState -> 12)
				)
		);
		
		NETHER_REACTOR = registerBlockEntityItem(
				"nether_reactor",
				new NetherReactorBlock(
						BlockBehaviour.Properties.of()
								.strength(3.0f, 6.0f)
								.requiresCorrectToolForDrops()
								.sound(SoundTypes.NETHER_REACTOR)
				),
				NetherReactorBlockEntity::new
		);
		
		if (Constants.isClient()) {
			BlockRendering.INSTANCE.initialize();
		}
	}
	
	private static <B extends Block> B registerBlock(@NotNull String id, B block) {
		return Registry.register(BuiltInRegistries.BLOCK, modId(id), block);
	}
	
	private static <B extends Block> BlockHolder<B, BlockItem> register(@NotNull String id, B block) {
		return register(id, block, new BlockItem(block, new Item.Properties()));
	}
	
	private static <B extends Block, I extends Item> BlockHolder<B, I> register(@NotNull String id, B block, I item) {
		block = registerBlock(id, block);
		item = Registry.register(BuiltInRegistries.ITEM, modId(id), item);
		return new BlockHolder<>(block, item);
	}
	
	private static <B extends Block, I extends Item, BE extends BlockEntity> BlockEntityHolder<B, I, BE> registerBlockEntityItem(@NotNull String id, B block, BlockEntityType.BlockEntitySupplier<BE> supplier, I item) {
		BlockHolder<B, I> holder = register(id, block, item);
		BlockEntityType<BE> type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId(id), BlockEntityType.Builder.of(supplier, block).build());
		return Conversions.convert(holder, type);
	}
	
	private static <B extends Block, BE extends BlockEntity> BlockEntityHolder<B, BlockItem, BE> registerBlockEntityItem(@NotNull String id, B block, BlockEntityType.BlockEntitySupplier<BE> supplier) {
		return registerBlockEntityItem(id, block, supplier, new BlockItem(block, new Item.Properties()));
	}
	
	@org.jetbrains.annotations.ApiStatus.Internal
	public static final class BlockRendering implements Initializable {
		public static final BlockRendering INSTANCE = new BlockRendering();
		
		private BlockRendering() {}
		
		private static <B extends Block, I extends Item, BE extends BlockEntity> void register(@NotNull BlockEntityHolder<B, I, BE> holder, BlockEntityRendererProvider<BE> rendererProvider) {
			BlockEntityRenderers.register(holder.type(), rendererProvider);
		}
		
		private static void addRenderType(Block block, RenderType renderType) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
		}
	}
	
	private static class SoundTypes {
		public static final net.minecraft.world.level.block.SoundType NETHER_REACTOR = new net.minecraft.world.level.block.SoundType(
				1.0F, 1.2F, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL
		);
	}
}
