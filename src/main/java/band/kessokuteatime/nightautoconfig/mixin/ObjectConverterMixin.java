package band.kessokuteatime.nightautoconfig.mixin;

import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ObjectConverter.class)
public class ObjectConverterMixin {
    @Redirect(
            method = "convertToObject",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/electronwill/nightconfig/core/conversion/Converter;convertToField(Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            remap = false
    )
    private <F, C> F preventConvertingMaps(Converter<F, C> instance, C c) {
        if (c instanceof java.util.Map) {
            return (F) c;
        }
        return instance.convertToField(c);
    }
}
