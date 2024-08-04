package gay.sylv.weird_wares.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import gay.sylv.weird_wares.impl.duck.Accessor_BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BufferBuilder.class)
public abstract class Mixin_BufferBuilder implements Accessor_BufferBuilder {
	private Mixin_BufferBuilder() {}
	
	@Override
	@Accessor("building")
	public abstract boolean weird_wares$isBuilding();
}
