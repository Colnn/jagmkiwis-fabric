package jagm.jagmkiwis;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KiwiEntity extends AnimalEntity implements VariantHolder<KiwiEntity.Variant>, RangedAttackMob {

	private static final Ingredient FOOD_ITEMS = Ingredient.ofItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS,
			Items.PITCHER_POD);
	public static final Identifier KIWI_LOOT_TABLE = new Identifier(JagmKiwis.MODID, "entities/kiwi");
	private static final TrackedData<Integer> DATA_TYPE_ID = DataTracker.registerData(KiwiEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final int CHANCE_OF_LASERS = 5;

	private KiwiDigGoal digGoal;
	private int digAnimationTick;
	public int eggTime = this.random.nextInt(12000) + 12000;

	protected KiwiEntity(EntityType<? extends AnimalEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.digGoal = new KiwiDigGoal(this);
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
		this.goalSelector.add(3, new TemptGoal(this, 1.0D, FOOD_ITEMS, false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1D));
		this.goalSelector.add(5, new FleeEntityGoal<>(this, CatEntity.class, 8.0F, 1.0D, 1.0D));
		this.goalSelector.add(6, this.digGoal);
		this.goalSelector.add(7, new WanderAroundGoal(this, 1.0D));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions size) {
		return this.isBaby() ? size.height : size.height * 0.8125F;
	}

//	@Override
//	protected void customServerAiStep() {
//		this.digAnimationTick = this.digGoal.getDigAnimationTick();
//		super.customServerAiStep();
//	}

//	@Override
//	public void aiStep() {
//		Level level = this.level();
//		if (level.isClientSide) {
//			this.digAnimationTick = Math.max(0, this.digAnimationTick - 1);
//		}
//		if (!level.isClientSide && this.isAlive() && !this.isBaby() && --this.eggTime <= 0) {
//			this.playSound(JagmKiwis.KIWI_LAY_EGG.get(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
//			this.spawnAtLocation(JagmKiwis.KIWI_EGG.get());
//			this.gameEvent(GameEvent.ENTITY_PLACE);
//			this.eggTime = this.random.nextInt(12000) + 12000;
//		}
//
//		super.aiStep();
//	}

	public static Ingredient getFoodItems() {
		return FOOD_ITEMS;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 10) {
			this.digAnimationTick = 40;
		} else {
			super.handleStatus(status);
		}
	}

	public float getHeadEatPositionScale(float p_29881_) {
		if (this.digAnimationTick <= 0) {
			return 0.0F;
		} else if (this.digAnimationTick >= 4 && this.digAnimationTick <= 36) {
			return 1.0F;
		} else {
			return this.digAnimationTick < 4 ? ((float) this.digAnimationTick - p_29881_) / 4.0F : -((float) (this.digAnimationTick - 40) - p_29881_) / 4.0F;
		}
	}

	public float getHeadEatAngleScale(float p_29883_) {
		if (this.digAnimationTick > 4 && this.digAnimationTick <= 36) {
			float f = ((float) (this.digAnimationTick - 4) - p_29883_) / 32.0F;
			return ((float) Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
		} else {
			return this.digAnimationTick > 0 ? ((float) Math.PI / 5F) : this.getRoll() * ((float) Math.PI / 180F);
		}
	}

	public static DefaultAttributeContainer.Builder prepareAttributes() {
		return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 5.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D);
	}

	@Override
	protected Identifier getLootTableId() {
		return KIWI_LOOT_TABLE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return JagmKiwis.KIWI_AMBIENT_SOUND;
	}

	@Override
	public void playAmbientSound() {
		if (this.getWorld().getDimension().hasFixedTime() || this.getWorld().isNight()) {
			super.playAmbientSound();
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return JagmKiwis.KIWI_HURT_SOUND;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return JagmKiwis.KIWI_DEATH_SOUND;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockState) {
		this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.3F;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 160;
	}

	@Override
	public void setVariant(Variant variant) {
		if (variant == Variant.LASER) {
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(8.0D);
			this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.0D, 20, 40, 20.0F));
			this.targetSelector.add(1, new RevengeGoal(this));
			this.targetSelector.add(2, new ActiveTargetGoal<>(this, HostileEntity.class, true));
		}
		else {
			this.goalSelector.add(1, new EscapeDangerGoal(this, 1.0D));
		}
		this.dataTracker.set(DATA_TYPE_ID, variant.id);
	}

	@Override
	public Variant getVariant() {
		return Variant.byId(this.dataTracker.get(DATA_TYPE_ID));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(DATA_TYPE_ID, Variant.NORMAL.id);
	}

	@Override
	public boolean saveNbt(NbtCompound nbt) {
		nbt.putInt("KiwiType", this.getVariant().id);
		return super.saveNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.setVariant(Variant.byId(nbt.getInt("KiwiType")));
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
		boolean isLaserVariant = world.getRandom().nextInt(100) < CHANCE_OF_LASERS;
		this.setVariant(isLaserVariant ? Variant.LASER : Variant.NORMAL);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		KiwiEntity babyKiwi = JagmKiwis.KIWI.create(world);
		boolean isLaserVariant = this.getRandom().nextInt(100) < CHANCE_OF_LASERS;
		babyKiwi.setVariant(isLaserVariant ? Variant.LASER : Variant.NORMAL);
		return babyKiwi;
	}


	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		LaserBeamEntity laser = new LaserBeamEntity(this, this.getWorld());
		double d0 = target.getX() - this.getX();
		double d1 = target.getEyeY() - this.getEyeY();
		double d2 = target.getZ() - this.getZ();
		Vec3d vec3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ()).add((new Vec3d(d0, d1, d2)).normalize());
		laser.setPos(vec3d.x, vec3d.y, vec3d.z);
		laser.setVelocity(d0, d1, d2, 1.5F, 0.0F);
		this.getWorld().spawnEntity(laser);
		if(!this.isSilent()) {
			this.playSound(JagmKiwis.LASER_SHOOT_SOUND, 0.3F, 1.0F);
		}
	}

	public static enum Variant implements StringIdentifiable {
		NORMAL(0, "normal"),
		LASER(99, "laser");
		final int id;
		private final String name;

		private Variant(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public int id() {
			return this.id;
		}

		public static Variant byId(int p_262665_) {
			for (Variant value : values()) {
				if(value.id == p_262665_) {
					return value;
				}
			}
			return null;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

}
