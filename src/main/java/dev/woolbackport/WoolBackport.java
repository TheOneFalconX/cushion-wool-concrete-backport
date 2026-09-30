package dev.woolbackport;

import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class WoolBackport implements ModInitializer {
    public static final String MOD_ID = "woolbackport";


    private static final String[] COLORS = {
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
        "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    @Override
    public void onInitialize() {
        for (String color : COLORS) {
            registerPair(color + "_wool", true);
            registerPair(color + "_concrete", false);
        }
    }


    private static void registerPair(String baseName, boolean flammable) {
        Block source = BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(baseName));

        Block stairs = register(baseName + "_stairs", source, flammable,
            props -> new StairBlock(source.defaultBlockState(), props));
        Block slab = register(baseName + "_slab", source, flammable, SlabBlock::new);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
            .register(output -> output.insertAfter(source, stairs, slab));
    }

    private static Block register(String name, Block copyFrom, boolean flammable,
            Function<BlockBehaviour.Properties, Block> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        Block block = factory.apply(BlockBehaviour.Properties.ofFullCopy(copyFrom).setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
            new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));

        if (flammable) {      
            FlammableBlockRegistry.getDefaultInstance().add(block, 30, 60);
        }
        return block;
    }
}
