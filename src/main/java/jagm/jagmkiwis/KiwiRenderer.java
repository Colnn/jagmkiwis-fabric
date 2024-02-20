package jagm.jagmkiwis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class KiwiRenderer extends MobEntityRenderer<KiwiEntity, KiwiModel<KiwiEntity>> {

	private static final Identifier NORMAL_KIWI = new Identifier(JagmKiwis.MODID, "textures/entity/kiwi.png");
	private static final Identifier LASER_KIWI = new Identifier(JagmKiwis.MODID, "textures/entity/laser_kiwi.png");

	public KiwiRenderer(EntityRendererFactory.Context context) {
		super(context, new KiwiModel<>(context.getPart(KiwiModel.KIWI_LAYER)), 0.5F);
	}

	@Override
	public Identifier getTexture(KiwiEntity kiwi) {
		return kiwi.getVariant() == KiwiEntity.Variant.LASER ? LASER_KIWI : NORMAL_KIWI;
	}

}
