package jagm.jagmkiwis;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.EnumSet;

public class KiwiDigGoal extends Goal {

	private final MobEntity mob;
	public static final Identifier DIGGING_LOOT = new Identifier(JagmKiwis.MODID, "entities/kiwi_diggables");
	private int digAnimationTick;

	public KiwiDigGoal(MobEntity mob) {
		this.mob = mob;
		this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
	}

	@Override
	public boolean canStart() {
		if (this.mob.isBaby() || this.mob.getRandom().nextInt(3000) != 0) {
			return false;
		} else {
			return this.mob.getWorld().getBlockState(this.mob.getBlockPos().down()).equals(BlockTags.DIRT);
		}
	}

	@Override
	public void start() {
		this.digAnimationTick = this.getTickCount(40);
		this.mob.getWorld().sendEntityStatus(this.mob, (byte) 10);
		this.mob.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.digAnimationTick = 0;
	}

	@Override
	public boolean shouldContinue() {
		return this.digAnimationTick > 0;
	}

	@Override
	public void tick() {
		this.digAnimationTick = Math.max(this.digAnimationTick - 1, 0);
		if (this.digAnimationTick == this.getTickCount(4)) {
			World world = this.mob.getWorld();
			if (!world.isClient && this.mob.isAlive()) {
				this.mob.playSound(JagmKiwis.KIWI_DIG, 1.0F, (this.mob.getRandom().nextFloat() - this.mob.getRandom().nextFloat()) * 0.2F + 1.0F);
				LootTable diggingLoot = world.getServer().getLootManager().getLootTable(DIGGING_LOOT);
				LootContextParameterSet.Builder lootParams$builder = new LootContextParameterSet.Builder((ServerWorld) world);
				LootContextParameterSet lootParams = lootParams$builder.build(LootContextTypes.EMPTY);
				diggingLoot.generateLoot(lootParams, this.mob.getLootTableSeed(), this.mob::dropStack);
				this.mob.emitGameEvent(GameEvent.ENTITY_PLACE);
			}

		}
	}

	public int getDigAnimationTick() {
		return this.digAnimationTick;
	}

}
