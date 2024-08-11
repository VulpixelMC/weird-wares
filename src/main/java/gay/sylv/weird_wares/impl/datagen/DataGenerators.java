package gay.sylv.weird_wares.impl.datagen;

import gay.sylv.weird_wares.impl.block.Blocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

@org.jetbrains.annotations.ApiStatus.Internal
public final class DataGenerators implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(BlockLootTableGenerator::new);
		pack.addProvider(RecipeGenerator::new);
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
		public void buildRecipes(RecipeOutput exporter) {
			ShapedRecipeBuilder
					.shaped(RecipeCategory.MISC, Blocks.NETHER_REACTOR.item())
					.define('I', Items.IRON_INGOT)
					.define('D', Items.DIAMOND)
					.pattern("IDI")
					.pattern("IDI")
					.pattern("IDI")
					.unlockedBy("has_diamond", has(Items.DIAMOND))
					.save(exporter);
		}
	}
}
