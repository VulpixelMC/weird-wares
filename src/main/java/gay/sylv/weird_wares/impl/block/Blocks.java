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
import gay.sylv.weird_wares.impl.item.Items;
import gay.sylv.weird_wares.impl.util.Constants;
import gay.sylv.weird_wares.impl.util.Conversions;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
				Block::new,
				BlockBehaviour.Properties.of()
						.isValidSpawn((blockState, blockGetter, blockPos, entityType) -> true)
						.instabreak()
						.mapColor(MapColor.DIRT)
		);
		INFO_UPDATE = register(
				"info_update",
				Block::new,
				BlockBehaviour.Properties.of()
						.sound(SoundType.GRAVEL)
						.isValidSpawn((blockState, blockGetter, blockPos, entityType) -> true)
						.strength(0.0f, Constants.INFINITE_BLAST_RESISTANCE)
						.mapColor(MapColor.COLOR_LIGHT_GREEN)
		);
		INFO_UPDATE2 = register(
				"info_update2",
				Block::new,
				BlockBehaviour.Properties.ofFullCopy(INFO_UPDATE.block())
		);
		GLOWING_OBSIDIAN = register(
				"glowing_obsidian",
				Block::new,
				BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OBSIDIAN)
						.emissiveRendering((blockState, blockGetter, blockPos) -> true)
						.lightLevel(blockState -> 12)
		);
		
		NETHER_REACTOR = registerBlockEntityItem(
				"nether_reactor",
				NetherReactorBlock::new,
				BlockBehaviour.Properties.of()
						.strength(3.0f, 6.0f)
						.requiresCorrectToolForDrops()
						.sound(SoundTypes.NETHER_REACTOR),
				NetherReactorBlockEntity::new
		);
		
		if (Constants.isClient()) {
			BlockRendering.INSTANCE.initialize();
		}
	}
	
	private static <B extends Block> B registerBlock(
			@NotNull String id,
			Factory<B> blockFactory,
			BlockBehaviour.Properties properties
	) {
		properties.setId(ResourceKey.create(Registries.BLOCK, modId(id)));
		return Registry.register(
				BuiltInRegistries.BLOCK,
				modId(id),
				blockFactory.create(properties)
		);
	}
	
	private static <B extends Block> BlockHolder<B, BlockItem> register(@NotNull String id, Factory<B> blockFactory, BlockBehaviour.Properties blockProperties) {
		B block = registerBlock(id, blockFactory, blockProperties);
		Item.Properties itemProperties = new Item.Properties();
		itemProperties.useBlockDescriptionPrefix();
		BlockItem item = Items.register(id, properties -> new BlockItem(block, properties), itemProperties);
		return new BlockHolder<>(block, item, modId(id));
	}
	
	private static <B extends Block, I extends Item> BlockHolder<B, I> register(@NotNull String id, Factory<B> blockFactory, BlockBehaviour.Properties blockProperties, Items.Factory<I> itemFactory, Item.Properties itemProperties) {
		B block = registerBlock(id, blockFactory, blockProperties);
		itemProperties.useBlockDescriptionPrefix();
		I item = Items.register(id, itemFactory, itemProperties);
		return new BlockHolder<>(block, item, modId(id));
	}
	
	private static <B extends Block, I extends Item, BE extends BlockEntity> BlockEntityHolder<B, I, BE> registerBlockEntityItem(@NotNull String id, Factory<B> blockFactory, BlockBehaviour.Properties blockProperties, Items.Factory<I> itemFactory, Item.Properties itemProperties, FabricBlockEntityTypeBuilder.Factory<BE> supplier) {
		BlockHolder<B, I> holder = register(id, blockFactory, blockProperties, itemFactory, itemProperties);
		BlockEntityType<BE> type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId(id), FabricBlockEntityTypeBuilder.create(supplier, holder.block()).build());
		return Conversions.convert(holder, type, modId(id));
	}
	
	private static <B extends Block, BE extends BlockEntity> BlockEntityHolder<B, BlockItem, BE> registerBlockEntityItem(@NotNull String id, Factory<B> blockFactory, BlockBehaviour.Properties blockProperties, FabricBlockEntityTypeBuilder.Factory<BE> supplier) {
		BlockHolder<B, BlockItem> holder = register(id, blockFactory, blockProperties);
		BlockEntityType<BE> type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId(id), FabricBlockEntityTypeBuilder.create(supplier, holder.block()).build());
		return Conversions.convert(holder, type, modId(id));
	}
	
	@org.jetbrains.annotations.ApiStatus.Internal
	public static final class BlockRendering implements Initializable {
		public static final BlockRendering INSTANCE = new BlockRendering();
		
		private BlockRendering() {}
		
		private static <B extends Block, I extends Item, BE extends BlockEntity, BERS extends BlockEntityRenderState> void register(@NotNull BlockEntityHolder<B, I, BE> holder, BlockEntityRendererProvider<BE, BERS> rendererProvider) {
			BlockEntityRenderers.register(holder.type(), rendererProvider);
		}
		
		private static void addRenderType(Block block, ChunkSectionLayer layer) {
			BlockRenderLayerMap.putBlock(block, layer);
		}
	}

	@FunctionalInterface
	public interface Factory<B extends Block> {
		B create(BlockBehaviour.Properties properties);
	}
	
	private static class SoundTypes {
		public static final net.minecraft.world.level.block.SoundType NETHER_REACTOR = new net.minecraft.world.level.block.SoundType(
				1.0F, 1.2F, SoundEvents.STONE_BREAK, SoundEvents.STONE_STEP, SoundEvents.STONE_PLACE, SoundEvents.STONE_HIT, SoundEvents.STONE_FALL
		);
	}
}
