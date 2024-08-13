/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.util;

/**
 * A utility class for asserting conditions at runtime.
 * <p>
 * Do not use complex checks in production!
 */
@org.jetbrains.annotations.ApiStatus.Internal
public final class Assertions {
	private Assertions() {}
	
	public static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
