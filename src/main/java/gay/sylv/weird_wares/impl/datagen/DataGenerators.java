/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.datagen;

import gay.sylv.weird_wares.impl.block.Blocks;
import gay.sylv.weird_wares.impl.block.GenericBlockHolder;
import gay.sylv.weird_wares.impl.block.NetherReactorBlock;
import gay.sylv.weird_wares.impl.util.Constants;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

@org.jetbrains.annotations.ApiStatus.Internal
public final class DataGenerators implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(BlockLootTableGenerator::new);
		pack.addProvider(RecipeGenerator::new);
		pack.addProvider(ModelGenerator::new);
	}
	
	private static final class ModelGenerator extends FabricModelProvider {
		public ModelGenerator(FabricDataOutput output) {
			super(output);
		}
		
		@Override
		public void generateBlockStateModels(BlockModelGenerators generators) {
			cubeItem(generators, Blocks.GLOWING_OBSIDIAN);
			cubeItem(generators, Blocks.INFO_UPDATE);
			cubeItem(generators, Blocks.INFO_UPDATE2);
			cubeItem(generators, Blocks.UNKNOWN);
			
			createNetherReactor(generators);
		}
		
		private void createNetherReactor(BlockModelGenerators generators) {
			generators.blockStateOutput.accept(
					MultiVariantGenerator.dispatch(Blocks.NETHER_REACTOR.block())
							.with(
									PropertyDispatch.initial(NetherReactorBlock.STATE)
											.select(
													NetherReactorBlock.State.INACTIVE,
													plainVariant(cubeVariant(generators, Blocks.NETHER_REACTOR))
											)
											.select(
													NetherReactorBlock.State.ACTIVE,
													plainVariant(cubeVariant(generators, Blocks.NETHER_REACTOR, "_active"))
											)
											.select(
													NetherReactorBlock.State.USED,
													plainVariant(cubeVariant(generators, Blocks.NETHER_REACTOR, "_used"))
											)
							)
			);
			blockItemModel(generators, Blocks.NETHER_REACTOR);
		}
		
		private @NonNull Identifier cubeVariant(BlockModelGenerators generators, GenericBlockHolder block) {
			return TexturedModel.CUBE.create(block.block(), generators.modelOutput);
		}
		
		private @NonNull Identifier cubeVariant(BlockModelGenerators generators, GenericBlockHolder block, String suffix) {
			return TexturedModel.createAllSame(block.id().withPrefix("block/").withSuffix(suffix)).createWithSuffix(block.block(), suffix, generators.modelOutput);
		}
		
		@Override
		public void generateItemModels(ItemModelGenerators generators) {
			generators.generateFlatItem(
					gay.sylv.weird_wares.impl.item.Items.GLITTER,
					ModelTemplates.FLAT_HANDHELD_ITEM
			);
			generators.generateFlatItem(
					gay.sylv.weird_wares.impl.item.Items.SCULK_REMOTE,
					ModelTemplates.FLAT_HANDHELD_ITEM
			);
		}

		private void cubeItem(BlockModelGenerators generators, GenericBlockHolder block) {
			generators.createTrivialCube(block.block());
			blockItemModel(generators, block);
		}

		private void blockItemModel(BlockModelGenerators generators, GenericBlockHolder block) {
			generators.itemModelOutput.accept(block.item().asItem(), ItemModelUtils.plainModel(block.id().withPrefix("block/")));
		}
	}
	
	private static final class BlockLootTableGenerator extends FabricBlockLootTableProvider {
		public BlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(dataOutput, registryLookup);
		}
		
		@Override
		public void generate() {
			dropSelf(Blocks.UNKNOWN.block());
			dropSelf(Blocks.INFO_UPDATE.block());
			dropSelf(Blocks.INFO_UPDATE2.block());
			dropOther(Blocks.GLOWING_OBSIDIAN.block(), net.minecraft.world.level.block.Blocks.OBSIDIAN);
			dropSelf(Blocks.NETHER_REACTOR.block());
		}
	}
	
	private static final class RecipeGenerator extends FabricRecipeProvider {
		public RecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}
		
		@Override
		protected RecipeProvider createRecipeProvider(
				HolderLookup.Provider provider,
				RecipeOutput recipeOutput
		) {
			return new RecipeProvider(provider, recipeOutput) {
				@Override
				public void buildRecipes() {
					this.shaped(RecipeCategory.MISC, Blocks.NETHER_REACTOR.item())
							.define('I', Items.IRON_INGOT)
							.define('D', Items.DIAMOND)
							.pattern("IDI")
							.pattern("IDI")
							.pattern("IDI")
							.unlockedBy("has_diamond", has(Items.DIAMOND))
							.save(recipeOutput);
					this.shaped(RecipeCategory.REDSTONE, gay.sylv.weird_wares.impl.item.Items.SCULK_REMOTE)
							.define('Y', Items.SCULK_SENSOR)
							.define('O', Items.GLASS)
							.define('S', Items.SCULK_SHRIEKER)
							.define('#', Items.STONE)
							.pattern(" S ")
							.pattern("OYO")
							.pattern("###")
							.unlockedBy("has_sculk_shrieker", has(Items.SCULK_SHRIEKER))
							.unlockedBy("has_sculk_sensor", has(Items.SCULK_SENSOR))
							.save(recipeOutput);
				}
			};
		}
		
		@Override
		public String getName() {
			return Constants.MOD_NAME + " Recipe Generator";
		}
	}
}
