package dev.woolbackport;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;

public class CushionClient implements ClientModInitializer {
    public static final ModelLayerLocation CUSHION_LAYER =
        new ModelLayerLocation(Identifier.fromNamespaceAndPath(WoolBackport.MOD_ID, "cushion"), "main");

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(CUSHION_LAYER, CushionEntityModel::getTexturedModelData);
        EntityRenderers.register(WoolBackport.CUSHION, CushionEntityRenderer::new);
    }
}
