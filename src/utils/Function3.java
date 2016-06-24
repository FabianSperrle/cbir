package utils;

/**
 * Functional Interface for a transformation function.
 * To be applied to every pixel with the RGB values as input.
 * Returns a new greyscale RBG value.
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public interface Function3<A, B, C, D> {
        public D apply(A a, B b, C c);
}
