package jagm.jagmkiwis.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import jagm.jagmkiwis.KiwiEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(CatEntity.class)
public class CatEntityMixin extends MobEntity {

    protected CatEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    protected void addGoals(CallbackInfo ci) {
        this.targetSelector.add(1, new UntamedActiveTargetGoal<>((CatEntity) (Object)this, KiwiEntity.class, false, null));
    }
}
