package gay.sylv.weird_wares.impl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A grab-gab of functional utilities.
 */
@org.jetbrains.annotations.ApiStatus.Internal
public final class Functional {
	private Functional() {}
	
	public static boolean isStaticAccessible(Field field, Class<?> type) {
		final int modifiers = field.getModifiers();
		final boolean modifiersOk = Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
		final boolean typeOk = field.getType().isAssignableFrom(type);
		return modifiersOk && typeOk;
	}
}
