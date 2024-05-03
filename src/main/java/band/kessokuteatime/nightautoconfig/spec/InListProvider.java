package band.kessokuteatime.nightautoconfig.spec;

import java.util.Collection;

public interface InListProvider<T> {
    Collection<T> acceptableValues();
}
