package gay.sylv.weird_wares.impl.datagen;

import gay.sylv.weird_wares.impl.block.Blocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

@org.jetbrains.annotations.ApiStatus.Internal
public final class DataGenerators implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(BlockLootTableGenerator::new);
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
			dropSelf(Blocks.GLOWING_OBSIDIAN.block());
			dropSelf(Blocks.NETHER_REACTOR.block());
		}
	}
}
