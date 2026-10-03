package dev.woolbackport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class CushionEntityRenderer extends EntityRenderer<CushionEntity, CushionEntityRenderState> {
    private final CushionEntityModel model;

    public CushionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CushionEntityModel(context.bakeLayer(CushionClient.CUSHION_LAYER));
    }

    @Override
    public CushionEntityRenderState createRenderState() {
        return new CushionEntityRenderState();
    }

    @Override
    public void extractRenderState(CushionEntity entity, CushionEntityRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.color = entity.getColor();
    }

    @Override
    public Identifier getTextureLocation(CushionEntityRenderState state) {
        return Identifier.fromNamespaceAndPath(WoolBackport.MOD_ID, "textures/entity/cushion/" + state.color + ".png");
    }

    @Override
    public void render(CushionEntityRenderState state, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        this.model.setupAnim(state);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(state)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(state, poseStack, buffer, packedLight);
    }
}
