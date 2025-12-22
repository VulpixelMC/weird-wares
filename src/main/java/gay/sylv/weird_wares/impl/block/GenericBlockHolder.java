package gay.sylv.weird_wares.impl.block;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface GenericBlockHolder {
	Block block();

	Item item();

	Identifier id();
}
