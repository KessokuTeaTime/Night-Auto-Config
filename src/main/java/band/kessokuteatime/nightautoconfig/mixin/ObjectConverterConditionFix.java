package band.kessokuteatime.nightautoconfig.mixin;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ObjectConverter.class)
public class ObjectConverterConditionFix {
    @Unique
    private @Nullable Object object = null;

    @ModifyVariable(
            method = "convertToConfig",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/reflect/Field;getModifiers()I",
                    shift = At.Shift.BEFORE
            ),
            index = 1,
            argsOnly = true,
            remap = false
    )
    private Object captureAndReplaceObject(Object value) {
        object = value;
        return null;
    }

    @ModifyVariable(
            method = "convertToConfig",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/reflect/Field;get(Ljava/lang/Object;)Ljava/lang/Object;",
                    shift = At.Shift.BEFORE
            ),
            index = 1,
            argsOnly = true,
            remap = false
    )
    private Object restoreObject(Object value) {
        return this.object;
    }
}
