package gay.sylv.weird_wares.impl.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Codecs {
	public static final Codec<Set<BlockPos>> POS_LIST = Codec.list(BlockPos.CODEC).xmap(HashSet::new, ArrayList::new);
	
	private Codecs() {}
}
