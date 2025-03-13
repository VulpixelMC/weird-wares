/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

@org.jetbrains.annotations.ApiStatus.Internal
public record RemoteTarget(ResourceKey<Level> dimension, BlockPos pos) {
	public static final Codec<RemoteTarget> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					ResourceKey.codec(Registries.DIMENSION)
							.fieldOf("dimension")
							.forGetter(RemoteTarget::dimension),
					BlockPos.CODEC
							.fieldOf("pos")
							.forGetter(RemoteTarget::pos)
			).apply(instance, RemoteTarget::new)
	);
	
	public static final StreamCodec<ByteBuf, RemoteTarget> STREAM_CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION), RemoteTarget::dimension,
			BlockPos.STREAM_CODEC, RemoteTarget::pos,
			RemoteTarget::new
	);
}
