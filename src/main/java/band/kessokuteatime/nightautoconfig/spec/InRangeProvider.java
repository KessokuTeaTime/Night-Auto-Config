package band.kessokuteatime.nightautoconfig.spec;

import org.jetbrains.annotations.NotNull;

public interface InRangeProvider<T extends Comparable<T>> {
    @NotNull T min();
    @NotNull T max();

    default boolean inRangeInclusive(T value) {
        return value.compareTo(min()) >= 0 && value.compareTo(max()) <= 0;
    }

    default boolean inRangeExclusive(T value) {
        return value.compareTo(min()) > 0 && value.compareTo(max()) < 0;
    }
}
