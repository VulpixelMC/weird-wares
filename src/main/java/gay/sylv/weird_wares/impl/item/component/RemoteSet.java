package gay.sylv.weird_wares.impl.item.component;

import com.mojang.serialization.Codec;

@org.jetbrains.annotations.ApiStatus.Internal
public record RemoteSet(boolean isSet) {
	public static final RemoteSet TRUE = new RemoteSet(true);
	public static final RemoteSet FALSE = new RemoteSet(false);
	
	public static final Codec<RemoteSet> CODEC = Codec.unit(new RemoteSet(false));
}
