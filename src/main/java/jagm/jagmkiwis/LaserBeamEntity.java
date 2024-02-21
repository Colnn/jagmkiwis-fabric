package jagm.jagmkiwis;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class LaserBeamEntity extends PersistentProjectileEntity {
	public LaserBeamEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected LaserBeamEntity(LivingEntity owner, World world) {
		super(JagmKiwis.LASER_BEAM, owner, world);
	}

	@Override
	public ItemStack asItemStack() {
		return null;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		BlockState blockstate = this.getWorld().getBlockState(blockHitResult.getBlockPos());
		blockstate.onProjectileHit(this.getWorld(), blockstate, blockHitResult, this);
		this.discard();
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity target = entityHitResult.getEntity();
		float f = (float) this.getVelocity().length();
		double baseDamage = 4.0D;
		int i = MathHelper.ceil(MathHelper.clamp((double) f * baseDamage, 0.0D, (double) Integer.MAX_VALUE));
		if (this.isCritical()) {
			long j = (long) this.random.nextInt(i / 2 + 2);
			i = (int) Math.min(j + (long) i, 2147483647L);
		}
		Entity shooter = this.getOwner();
		DamageSource damagesource;
		if (shooter == null) {
			damagesource = this.getDamageSources().arrow(this, this);
		} else {
			damagesource = this.getDamageSources().arrow(this, shooter);
//			if (shooter instanceof LivingEntity) {
//				((LivingEntity) shooter).setLastHurtMob(target);
//			}
		}
		boolean flag = target.getType() == EntityType.ENDERMAN;
		if (target.damage(damagesource, (float) i)) {
			if (flag) {
				return;
			}
			if (target instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity) target;
				World level = this.getWorld();
//				if (!level.isClient && shooter instanceof LivingEntity) {
//					EnchantmentHelper.doPostHurtEffects(livingentity, shooter);
//					EnchantmentHelper.doPostDamageEffects((LivingEntity) shooter, livingentity);
//				}
				this.applyDamageEffects((LivingEntity) shooter, livingentity);
			}
		}
		this.discard();
	}
	
	public void tick() {
		super.tick();
		if(this.getVelocity().length() < 1.0D) {
			this.discard();
		}
	}

}
