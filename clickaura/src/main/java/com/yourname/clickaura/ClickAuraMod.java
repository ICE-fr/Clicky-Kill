package com.yourname.clickaura;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
public class ClickAuraMod implements ClientModInitializer {
    public static ClickAuraFeature clickAura;
    @Override
    public void onInitializeClient() {
        clickAura = new ClickAuraFeature();
        var toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.clickaura.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.clickaura"));
        var priorityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.clickaura.priority", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.clickaura"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            while (toggleKey.wasPressed()) {
                clickAura.enabled = !clickAura.enabled;
                client.player.sendMessage(Text.literal("[ClickAura] " +
                    (clickAura.enabled ? "§aEnabled" : "§cDisabled")), true);
            }
            while (priorityKey.wasPressed()) {
                clickAura.cyclePriority();
                client.player.sendMessage(Text.literal("[ClickAura] Priority: §e" +
                    clickAura.priority.name()), true);
            }
        });
    }
}
