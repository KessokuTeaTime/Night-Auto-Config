package band.kessokuteatime.nightautoconfig.util;

import java.util.IdentityHashMap;

public class TypeUtil {
    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || WRAPPER_TO_PRIMITIVE.get(type) != null;
    }

    /** Checks that {@code type} is a primitive or a wrapper type that is a {@code Number}
     * (all primitive types except boolean and char). */
    public static boolean isPrimitiveOrWrapperNumber(Class<?> type) {
        return isPrimitiveOrWrapper(type) && type != Boolean.class && type != boolean.class && type != Character.class
                && type != char.class;
    }

    private static final IdentityHashMap<Class<?>, TypeAndOrder> PRIMITIVE_TO_WRAPPER = new IdentityHashMap<>();
    private static final IdentityHashMap<Class<?>, TypeAndOrder> WRAPPER_TO_PRIMITIVE = new IdentityHashMap<>();

    static void addPrimitiveAndWrapper(Class<?> primitiveType, Class<?> wrapperType) {
        PRIMITIVE_TO_WRAPPER.put(primitiveType,
                new TypeAndOrder(PRIMITIVE_TO_WRAPPER.size(), wrapperType));
        WRAPPER_TO_PRIMITIVE.put(wrapperType,
                new TypeAndOrder(WRAPPER_TO_PRIMITIVE.size(), primitiveType));
    }

    static {
        addPrimitiveAndWrapper(Boolean.TYPE, Boolean.class);
        addPrimitiveAndWrapper(Byte.TYPE, Byte.class);
        addPrimitiveAndWrapper(Short.TYPE, Short.class);
        addPrimitiveAndWrapper(Character.TYPE, Character.class);
        addPrimitiveAndWrapper(Integer.TYPE, Integer.class);
        addPrimitiveAndWrapper(Long.TYPE, Long.class);
        addPrimitiveAndWrapper(Float.TYPE, Float.class);
        addPrimitiveAndWrapper(Double.TYPE, Double.class);
    }

    private static final class TypeAndOrder {
        final int order;
        final Class<?> type;

        TypeAndOrder(int order, Class<?> type) {
            this.order = order;
            this.type = type;
        }

        boolean canAssignValue(TypeAndOrder valueType) {
            // no widening conversion for boolean
            if (this.order == 0) {
                return valueType.order == 0;
            } else if (valueType.order == 0) {
                return false;
            }
            // widening conversions for numbers: int <- short, float <- int, ...
            return this.order >= valueType.order;
        }

        @Override
        public String toString() {
            return "TypeAndOrder [order=" + order + ", type=" + type + "]";
        }
    }
}
