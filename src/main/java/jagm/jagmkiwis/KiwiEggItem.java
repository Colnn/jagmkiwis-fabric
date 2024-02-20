package jagm.jagmkiwis;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class KiwiEggItem extends EggItem {

	public KiwiEggItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getStackInHand(hand);
		world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!world.isClient) {
			EggEntity thrownegg = new EggEntity(world, player) {

				@Override
				protected void onCollision(HitResult hitResult) {
					World world = this.getWorld();
					HitResult.Type hitresult$type = hitResult.getType();
					if (hitresult$type == HitResult.Type.ENTITY) {
						this.onEntityHit((EntityHitResult) hitResult);
						world.emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, (BlockState) null));
					} else if (hitresult$type == HitResult.Type.BLOCK) {
						BlockHitResult blockhitresult = (BlockHitResult) hitResult;
						this.onBlockHit(blockhitresult);
						BlockPos blockpos = blockhitresult.getBlockPos();
						world.emitGameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Emitter.of(this, world.getBlockState(blockpos)));
					}
					if (!world.isClient) {
						if (this.random.nextInt(8) == 0) {
							int i = 1;
							if (this.random.nextInt(32) == 0) {
								i = 4;
							}

							for (int j = 0; j < i; ++j) {
								KiwiEntity kiwi = JagmKiwis.KIWI.create(world);
								if (kiwi != null) {
									kiwi.age = -24000;
									kiwi.setPos(this.getX(), this.getY(), this.getZ());
									kiwi.setYaw(this.getYaw());
									world.spawnEntity(kiwi);
								}
							}
						}

						world.sendEntityStatus(this, (byte) 3);
						this.discard();
					}
				}

			};
			thrownegg.setItem(itemstack);
			thrownegg.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 0.25F, 1.0F);
			world.spawnEntity(thrownegg);
		}

		player.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!player.getAbilities().creativeMode) {
			itemstack.decrement(1);
		}

		return TypedActionResult.success(itemstack, world.isClient);
	}

}
