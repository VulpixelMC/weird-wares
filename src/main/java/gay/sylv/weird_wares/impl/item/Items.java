/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.item;

import gay.sylv.weird_wares.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Items implements Initializable {
	public static final Items INSTANCE = new Items();
	
	public static Item GLITTER;
	public static Item SCULK_REMOTE;
	
	private Items() {}
	
	@Override
	public void initialize() {
		GLITTER = register(
				"glitter",
				GlitterItem::new,
				new Item.Properties()
						.stacksTo(99)
		);
		
		SCULK_REMOTE = register(
				"sculk_remote",
				SculkRemoteItem::new,
				new Item.Properties()
						.stacksTo(1)
		);
	}
	
	public static <I extends Item> I register(String id, Factory<I> itemFactory, Item.Properties properties) {
		properties.setId(ResourceKey.create(Registries.ITEM, modId(id)));
		return Registry.register(
				BuiltInRegistries.ITEM,
				modId(id),
				itemFactory.create(properties)
		);
	}

	@FunctionalInterface
	public interface Factory<I extends Item> {
		I create(Item.Properties properties);
	}
}
