package jagm.jagmkiwis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LaserBeamRenderer extends ProjectileEntityRenderer<LaserBeamEntity> {
	
	private static final Identifier TEXTURE = new Identifier(JagmKiwis.MODID, "textures/entity/laser_beam.png");

	public LaserBeamRenderer(Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(LaserBeamEntity entity) {
		return TEXTURE;
	}
}
