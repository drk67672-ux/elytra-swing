package com.elytraswing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElytraSwingClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("elytraswing");

    private static KeyBinding launchKey;
    private long tick = 0;
    private long lastUseTick = Long.MIN_VALUE / 2;
    private int pendingDelay = -1;

    @Override
    public void onInitializeClient() {
        ElytraSwingConfig.load();

        launchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elytraswing.launch",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.elytraswing"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        tick++;
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) {
            pendingDelay = -1;
            return;
        }

        boolean pressed = false;
        while (launchKey.wasPressed()) pressed = true;

        ElytraSwingConfig cfg = ElytraSwingConfig.INSTANCE;

        if (pressed && pendingDelay < 0 && tick - lastUseTick >= cfg.cooldownTicks) {
            pendingDelay = cfg.delayTicks;
        }

        if (pendingDelay >= 0) {
            if (pendingDelay == 0) {
                pendingDelay = -1;
                if (!cfg.onlyWhileGliding || player.isFallFlying()) {
                    if (useRocket(client, player)) lastUseTick = tick;
                }
            } else {
                pendingDelay--;
            }
        }
    }

    private boolean useRocket(MinecraftClient client, ClientPlayerEntity player) {
        // Offhand first: no slot switching needed.
        if (player.getOffHandStack().isOf(Items.FIREWORK_ROCKET)) {
            client.interactionManager.interactItem(player, Hand.OFF_HAND);
            player.swingHand(Hand.OFF_HAND);
            return true;
        }

        // Already holding one in the main hand.
        if (player.getMainHandStack().isOf(Items.FIREWORK_ROCKET)) {
            client.interactionManager.interactItem(player, Hand.MAIN_HAND);
            player.swingHand(Hand.MAIN_HAND);
            return true;
        }

        // Search the hotbar, switch, use, switch back.
        for (int slot = 0; slot < 9; slot++) {
            if (player.getInventory().getStack(slot).isOf(Items.FIREWORK_ROCKET)) {
                int previous = player.getInventory().selectedSlot;
                player.getInventory().selectedSlot = slot;
                client.interactionManager.interactItem(player, Hand.MAIN_HAND);
                player.swingHand(Hand.MAIN_HAND);
                player.getInventory().selectedSlot = previous;
                return true;
            }
        }
        return false;
    }
}
