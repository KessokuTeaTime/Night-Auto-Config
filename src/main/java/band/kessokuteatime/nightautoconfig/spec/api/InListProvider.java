package band.kessokuteatime.nightautoconfig.spec.api;

import java.util.Collection;

public interface InListProvider<T> {
    Collection<T> acceptableValues();
}
