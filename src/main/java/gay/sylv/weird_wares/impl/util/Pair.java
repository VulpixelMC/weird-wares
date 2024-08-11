/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.util;

@org.jetbrains.annotations.ApiStatus.Internal
public class Pair<T, U> extends ImmutablePair<T, U> {
	public Pair(T first, U second) {
		super(first, second);
	}
	
	public static <T, U> Pair<T, U> of(T first, U second) {
		return new Pair<>(first, second);
	}
	
	public void setFirst(T first) {
		this.first = first;
	}
	
	public void setSecond(U second) {
		this.second = second;
	}
}
