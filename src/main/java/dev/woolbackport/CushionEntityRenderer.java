package dev.woolbackport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;

/**
 * Draws a flat, 1-block-wide, 4/16-tall box using the real cushion texture, submitted
 * via raw vertices rather than the ModelPart system (since this Minecraft version's
 * rendering pipeline replaced MultiBufferSource/RenderType-based model rendering with
 * a SubmitNodeCollector-based one). UVs are measured directly from the 64x64 texture.
 */
public class CushionEntityRenderer extends EntityRenderer<CushionEntity, CushionEntityRenderState> {

    public CushionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
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

    public Identifier getTextureLocation(CushionEntityRenderState state) {
        return Identifier.fromNamespaceAndPath(WoolBackport.MOD_ID, "textures/entity/cushion/" + state.color + ".png");
    }

    @Override
    public void submit(CushionEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);
        RenderType renderType = RenderType.entityCutoutNoCull(getTextureLocation(state));
        collector.submitCustomGeometry(poseStack, renderType, CushionEntityRenderer::addVertices);
    }

    private static void addVertices(PoseStack.Pose pose, VertexConsumer consumer) {
        float x0 = -0.5f, x1 = 0.5f;
        float z0 = -0.5f, z1 = 0.5f;
        float y0 = 0f, y1 = 0.25f;
        int light = 0xF000F0;
        int overlay = 0;

        // Top
        quad(pose, consumer, light, overlay,
            x0, y1, z0, 0.25f, 0f, x0, y1, z1, 0.25f, 0.25f,
            x1, y1, z1, 0.5f, 0.25f, x1, y1, z0, 0.5f, 0f, 0, 1, 0);
        // Bottom
        quad(pose, consumer, light, overlay,
            x0, y0, z1, 0.5f, 0f, x0, y0, z0, 0.5f, 0.25f,
            x1, y0, z0, 0.75f, 0.25f, x1, y0, z1, 0.75f, 0f, 0, -1, 0);
        // North (-Z)
        quad(pose, consumer, light, overlay,
            x1, y0, z0, 0f, 0.3125f, x1, y1, z0, 0f, 0.25f,
            x0, y1, z0, 0.25f, 0.25f, x0, y0, z0, 0.25f, 0.3125f, 0, 0, -1);
        // South (+Z)
        quad(pose, consumer, light, overlay,
            x0, y0, z1, 0.25f, 0.3125f, x0, y1, z1, 0.25f, 0.25f,
            x1, y1, z1, 0.5f, 0.25f, x1, y0, z1, 0.5f, 0.3125f, 0, 0, 1);
        // West (-X)
        quad(pose, consumer, light, overlay,
            x0, y0, z0, 0.5f, 0.3125f, x0, y1, z0, 0.5f, 0.25f,
            x0, y1, z1, 0.75f, 0.25f, x0, y0, z1, 0.75f, 0.3125f, -1, 0, 0);
        // East (+X)
        quad(pose, consumer, light, overlay,
            x1, y0, z1, 0.75f, 0.3125f, x1, y1, z1, 0.75f, 0.25f,
            x1, y1, z0, 1f, 0.25f, x1, y0, z0, 1f, 0.3125f, 1, 0, 0);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay,
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3,
            float x4, float y4, float z4, float u4, float v4,
            float nx, float ny, float nz) {
        vertex(pose, consumer, light, overlay, x1, y1, z1, u1, v1, nx, ny, nz);
        vertex(pose, consumer, light, overlay, x2, y2, z2, u2, v2, nx, ny, nz);
        vertex(pose, consumer, light, overlay, x3, y3, z3, u3, v3, nx, ny, nz);
        vertex(pose, consumer, light, overlay, x4, y4, z4, u4, v4, nx, ny, nz);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay,
            float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        consumer.addVertex(pose, x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
    }
}
