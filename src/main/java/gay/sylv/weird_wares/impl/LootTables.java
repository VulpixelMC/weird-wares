/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl;

import gay.sylv.weird_wares.impl.util.Initializable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
public final class LootTables implements Initializable {
	public static final LootTables INSTANCE = new LootTables();
	
	public static ResourceKey<LootTable> NETHER_REACTION_BYPRODUCT;
	
	private LootTables() {}
	
	@Override
	public void initialize() {
		NETHER_REACTION_BYPRODUCT = create("nether_reaction_byproduct");
	}
	
	private static ResourceKey<LootTable> create(String id) {
		return ResourceKey.create(Registries.LOOT_TABLE, modId(id));
	}
}
