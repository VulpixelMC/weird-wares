package gay.sylv.weird_wares.impl.entity;

import gay.sylv.weird_wares.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

public final class Entities implements Initializable {
	public static final Entities INSTANCE = new Entities();
	
	private Entities() {}
	
	private static <E extends Entity> EntityType<E> register(String id, EntityType<E> entityType) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, modId(id), entityType);
	}
}
