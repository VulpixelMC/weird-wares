package gay.sylv.weird_wares.impl.item;

import gay.sylv.weird_wares.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

public final class Items implements Initializable {
	public static final Items INSTANCE = new Items();
	
	public static Item GLITTER;
	
	private Items() {}
	
	@Override
	public void initialize() {
		GLITTER = register(
				"glitter",
				new GlitterItem(
						new Item.Properties()
								.stacksTo(256)
				)
		);
	}
	
	private static <I extends Item> I register(String id, I item) {
		return Registry.register(
				BuiltInRegistries.ITEM,
				modId(id),
				item
		);
	}
}
