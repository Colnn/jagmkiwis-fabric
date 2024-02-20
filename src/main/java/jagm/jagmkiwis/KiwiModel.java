package jagm.jagmkiwis;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class KiwiModel<T extends Entity> extends AnimalModel<T> {

	public static final EntityModelLayer KIWI_LAYER = new EntityModelLayer(new Identifier(JagmKiwis.MODID, "kiwi"), "main");
	private final ModelPart head;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart body;
	private float headXRot;

	public KiwiModel(ModelPart root) {
		super(false, 2.0F, 0.5F);
		this.head = root.getChild("head");
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");
		this.body = root.getChild("body");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 10).cuboid(-2.0F, -1.5F, -3.0F, 3.0F, 3.0F, 3.0F).uv(12, 10)
				.cuboid(-1.0F, -0.5F, -7.0F, 1.0F, 1.0F, 4.0F), ModelTransform.pivot(0.5F, 17.5F, -1.0F));
		modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(15, 0).cuboid(-1.5F, -1.0F, -1.0F, 2.0F, 3.0F, 2.0F).uv(23, 0)
				.cuboid(-1.0F, 1.5F, -0.5F, 1.0F, 2.0F, 1.0F).uv(21, 3).cuboid(-2.0F, 3.5F, -2.5F, 3.0F, 0.0F, 2.0F),
				ModelTransform.pivot(3.0F, 20.5F, 2.0F));
		modelPartData.addChild("right_leg",
				ModelPartBuilder.create().uv(15, 0).cuboid(-1.5F, -1.0F, -1.0F, 2.0F, 3.0F, 2.0F).uv(23, 0)
						.cuboid(-1.0F, 1.5F, -0.5F, 1.0F, 2.0F, 1.0F).uv(21, 3)
						.cuboid(-2.0F, 3.5F, -2.5F, 3.0F, 0.0F, 2.0F),
				ModelTransform.pivot(-2.0F, 20.5F, 2.0F));
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F),
				ModelTransform.pivot(0.5F, 19.5F, 1.5F));

		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.of(this.body, this.leftLeg, this.rightLeg);
	}

	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.pitch = this.headXRot;
		this.head.yaw = netHeadYaw * ((float) Math.PI / 180F);
		this.rightLeg.pitch = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftLeg.pitch = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
	}
}
