package gay.sylv.weird_wares.impl.item;

import gay.sylv.weird_wares.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
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
								.stacksTo(99)
								.component(DataComponents.LORE, new ItemLore(List.of(Component.translatable("lore.weird-wares.item.glitter"))))
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
