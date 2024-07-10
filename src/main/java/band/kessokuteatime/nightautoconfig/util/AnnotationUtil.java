package band.kessokuteatime.nightautoconfig.util;

import band.kessokuteatime.nightautoconfig.serde.annotations.DeserializerProvider;
import band.kessokuteatime.nightautoconfig.serde.annotations.DeserializerProvidersContainer;
import band.kessokuteatime.nightautoconfig.serde.annotations.SerializerProvider;
import band.kessokuteatime.nightautoconfig.serde.annotations.SerializerProvidersContainer;
import com.electronwill.nightconfig.core.serde.ValueDeserializerProvider;
import com.electronwill.nightconfig.core.serde.ValueSerializerProvider;

import java.util.ArrayList;
import java.util.List;

public class AnnotationUtil {
    public static List<? extends ValueSerializerProvider<?, ?>> getSerializerProviders(Class<?> cls) {
        boolean isSingleAnnotated = cls.isAnnotationPresent(SerializerProvider.class);
        boolean isPluralAnnotated = cls.isAnnotationPresent(SerializerProvidersContainer.class);

        ArrayList<SerializerProvider> annotations = new ArrayList<>();

        if (isSingleAnnotated) {
            annotations.add(cls.getAnnotation(SerializerProvider.class));
        } else if (isPluralAnnotated) {
            annotations.addAll(List.of(cls.getAnnotation(SerializerProvidersContainer.class).value()));
        }

        return annotations.stream().map(annotation -> {
            try {
                return annotation.value().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot instantiate serializer provider!", e);
            }
        }).toList();
    }

    public static List<? extends ValueDeserializerProvider<?, ?>> getDeserializerProviders(Class<?> cls) {
        boolean isSingleAnnotated = cls.isAnnotationPresent(DeserializerProvider.class);
        boolean isPluralAnnotated = cls.isAnnotationPresent(DeserializerProvidersContainer.class);

        ArrayList<DeserializerProvider> annotations = new ArrayList<>();

        if (isSingleAnnotated) {
            annotations.add(cls.getAnnotation(DeserializerProvider.class));
        } else if (isPluralAnnotated) {
            annotations.addAll(List.of(cls.getAnnotation(DeserializerProvidersContainer.class).value()));
        }

        return annotations.stream().map(annotation -> {
            try {
                return annotation.value().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Cannot instantiate deserializer provider!", e);
            }
        }).toList();
    }
}
