package jagm.jagmkiwis;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.RabbitEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(CatEntity.class)
public class CatEntityMixin {
    @Shadow
    GoalSelector targetSelector;

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void addGoals(CallbackInfo ci) {
        this.targetSelector.add(1, new UntamedActiveTargetGoal<>(cat, RabbitEntity.class, false, null));

    }
}
