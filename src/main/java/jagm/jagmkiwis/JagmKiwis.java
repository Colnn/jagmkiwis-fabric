package jagm.jagmkiwis;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.impl.itemgroup.ItemGroupEventsImpl;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JagmKiwis implements ModInitializer, ClientModInitializer {

	public static final String MODID = "jagmkiwis";

	public static final EntityType<KiwiEntity> KIWI = Registry.register(Registries.ENTITY_TYPE, createIdentifier("kiwi"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, KiwiEntity::new).forceTrackedVelocityUpdates(true).trackRangeChunks(8)
					.dimensions(EntityDimensions.fixed(0.5F, 0.5F)).build());
	public static final EntityType<LaserBeamEntity> LASER_BEAM = Registry.register(Registries.ENTITY_TYPE, createIdentifier("laser_beam"), FabricEntityTypeBuilder.<LaserBeamEntity>create(SpawnGroup.MISC, LaserBeamEntity::new).forceTrackedVelocityUpdates(true).dimensions(EntityDimensions.fixed(0.5F, 0.5F)).build());

	public static final Item KIWI_SPAWN_EGG = Registry.register(Registries.ITEM, createIdentifier("kiwi_spawn_egg"), new SpawnEggItem(KIWI, 0x97784A, 0xBEE000, new Item.Settings()));
	public static final Item KIWI_FRUIT = Registry.register(Registries.ITEM, createIdentifier("kiwi_fruit"), new Item((new Item.Settings()).food(FoodComponents.APPLE)));
	public static final Item KIWI_EGG = Registry.register(Registries.ITEM, createIdentifier("kiwi_egg"), new KiwiEggItem((new Item.Settings()).maxCount(16)));
	public static final Item PAVLOVA = Registry.register(Registries.ITEM, createIdentifier("pavlova"),
			new Item((new Item.Settings()).food((new FoodComponent.Builder()).hunger(10).saturationModifier(0.6F).build())));

	public static final SoundEvent KIWI_AMBIENT_SOUND = Registry.register(Registries.SOUND_EVENT, createIdentifier("kiwi_ambient"),
			SoundEvent.of(new Identifier(MODID, "kiwi_ambient")));
	public static final SoundEvent KIWI_HURT_SOUND = Registry.register(Registries.SOUND_EVENT, createIdentifier("kiwi_hurt"),
			SoundEvent.of(new Identifier(MODID, "kiwi_hurt")));
	public static final SoundEvent KIWI_DEATH_SOUND = Registry.register(Registries.SOUND_EVENT, createIdentifier("kiwi_death"),
			SoundEvent.of(new Identifier(MODID, "kiwi_death")));
	public static final SoundEvent KIWI_DIG = Registry.register(Registries.SOUND_EVENT, createIdentifier("kiwi_dig"), SoundEvent.of(new Identifier(MODID, "kiwi_dig")));
	public static final SoundEvent KIWI_LAY_EGG = Registry.register(Registries.SOUND_EVENT, createIdentifier("kiwi_lay_egg"),
			SoundEvent.of(new Identifier(MODID, "kiwi_lay_egg")));
	public static final SoundEvent LASER_SHOOT_SOUND = Registry.register(Registries.SOUND_EVENT, createIdentifier("laser_shoot"),
			SoundEvent.of(new Identifier(MODID, "laser_shoot")));

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		LOGGER.info("JagmKiwis loaded.");

		FabricDefaultAttributeRegistry.register(KIWI, KiwiEntity.prepareAttributes());

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
			entries.add(KIWI_FRUIT);
			entries.add(PAVLOVA);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
			entries.add(KIWI_SPAWN_EGG);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
			entries.add(KIWI_EGG);
		});
	}

	public static Collection<RegistryKey<Biome>> getValidSpawnBiomes() {
		List<RegistryKey<Biome>> collection = new ArrayList<>();
		collection.add(BiomeKeys.FOREST);
		collection.add(BiomeKeys.FLOWER_FOREST);
		collection.add(BiomeKeys.BIRCH_FOREST);
		collection.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
		collection.add(BiomeKeys.CHERRY_GROVE);
		return collection;
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(KIWI, KiwiRenderer::new);
		EntityRendererRegistry.register(LASER_BEAM, LaserBeamRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(KiwiModel.KIWI_LAYER, KiwiModel::getTexturedModelData);
		KiwiEntitySpawn.addEntitySpawn();
	}

	public static Identifier createIdentifier(String name) {
		return new Identifier(MODID, name);
	}

//	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//	public class ModSetup {
//
//		@SubscribeEvent
//		public static void onAttributeCreate(EntityAttributeCreationEvent event) {
//			event.put(KIWI.get(), KiwiEntity.prepareAttributes().build());
//		}
//
//		@SubscribeEvent
//		public static void onRegisterSpawnPlacements(SpawnPlacementRegisterEvent event) {
//			event.register(KIWI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules,
//					SpawnPlacementRegisterEvent.Operation.REPLACE);
//		}
//
//		@SubscribeEvent
//		public static void onFillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
//
//			if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
//				event.accept(KIWI_FRUIT);
//				event.accept(PAVLOVA);
//			}
//
//			if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
//				event.accept(KIWI_SPAWN_EGG);
//			}
//
//			if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
//				event.accept(KIWI_EGG);
//			}
//
//			if (event.getTabKey() == CreativeModeTabs.COMBAT) {
//				event.accept(KIWI_EGG);
//			}
//
//		}
//
//	}
//
//	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//	public class ClientModEvents {
//
//		@SubscribeEvent
//		public static void onClientSetup(FMLClientSetupEvent event) {
//			EntityRenderers.register(KIWI.get(), KiwiRenderer::new);
//			EntityRenderers.register(LASER_BEAM.get(), LaserBeamRenderer::new);
//		}
//
//		@SubscribeEvent
//		public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
//			event.registerLayerDefinition(KiwiModel.KIWI_LAYER, KiwiModel::createBodyLayer);
//		}
//
//	}
//
//	@SubscribeEvent
//	public static void onJoinLevel(EntityJoinLevelEvent event) {
//		if (event.getEntity() instanceof Cat) {
//			Cat cat = (Cat) event.getEntity();
//			if (cat.level() != null && !cat.level().isClientSide) {
//				cat.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(cat, KiwiEntity.class, false, (Predicate<LivingEntity>) null));
//			}
//		}
//	}

}
