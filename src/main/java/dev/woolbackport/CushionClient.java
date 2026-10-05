package dev.woolbackport;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class CushionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(WoolBackport.CUSHION, CushionEntityRenderer::new);
    }
}
