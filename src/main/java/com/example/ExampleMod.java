package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Регистрируем событие, которое срабатывает каждый игровой такт (тик) на сервере
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                
                // 1. Проверяем, что игрок присел (зажал Shift)
                if (player.isSneaking()) {
                    World world = player.getWorld();
                    BlockPos blockUnderPlayer = player.getBlockPos().down();

                    // 2. Проверяем, что под ногами воздух
                    if (world.getBlockState(blockUnderPlayer).isOf(Blocks.AIR)) {
                        ItemStack itemInHand = player.getStackInHand(Hand.MAIN_HAND);

                        // 3. Проверяем, что в руке именно БЛОК, а не меч или яблоко
                        if (!itemInHand.isEmpty() && itemInHand.getItem() instanceof BlockItem) {
                            BlockItem blockItem = (BlockItem) itemInHand.getItem();
                            Block blockToPlace = blockItem.getBlock();

                            // 4. Ставим этот блок в мир под игрока
                            world.setBlockState(blockUnderPlayer, blockToPlace.getDefaultState());

                            // 5. Если игрок не в креативе, забираем 1 блок из руки
                            if (!player.isCreative()) {
                                itemInHand.decrement(1);
                            }

                            // 6. Воспроизводим звук установки блока
                            world.playSound(
                                null, 
                                blockUnderPlayer, 
                                SoundEvents.BLOCK_STONE_PLACE, 
                                SoundCategory.BLOCKS, 
                                1.0F, 1.0F
                            );
                        }
                    }
                }
            }
        });
    }
}
