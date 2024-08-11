/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.entity;

import gay.sylv.weird_wares.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Entities implements Initializable {
	public static final Entities INSTANCE = new Entities();
	
	private Entities() {}
	
	private static <E extends Entity> EntityType<E> register(String id, EntityType<E> entityType) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, modId(id), entityType);
	}
}
