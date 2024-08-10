package gay.sylv.weird_wares.impl.item.group;

import gay.sylv.weird_wares.impl.block.Blocks;
import gay.sylv.weird_wares.impl.item.Items;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

public final class CreativeModeTabs implements Initializable {
	public static final CreativeModeTabs INSTANCE = new CreativeModeTabs();
	
	public static CreativeModeTabHolder WEIRD_WARES;
	
	private CreativeModeTabs() {}
	
	@Override
	public void initialize() {
		WEIRD_WARES = register("weird_wares", new ItemStack(Blocks.INFO_UPDATE.item()));
		ItemGroupEvents.modifyEntriesEvent(WEIRD_WARES.resourceKey()).register(creativeModeTab -> {
			creativeModeTab.accept(Blocks.UNKNOWN.item());
			creativeModeTab.accept(Blocks.INFO_UPDATE.item());
			creativeModeTab.accept(Blocks.INFO_UPDATE2.item());
			creativeModeTab.accept(Blocks.GLOWING_OBSIDIAN.item());
			creativeModeTab.accept(Blocks.NETHER_REACTOR.item());
			creativeModeTab.accept(Items.GLITTER);
		});
	}
	
	public static CreativeModeTabHolder register(String id, ItemStack icon) {
		CreativeModeTab creativeModeTab = FabricItemGroup.builder()
				.icon(() -> icon)
				.title(Component.translatable("creative_mode_tab.weird-wares." + id))
				.build();
		ResourceKey<CreativeModeTab> resourceKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, modId(id));
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, resourceKey, creativeModeTab);
		return new CreativeModeTabHolder(creativeModeTab, resourceKey);
	}
}
