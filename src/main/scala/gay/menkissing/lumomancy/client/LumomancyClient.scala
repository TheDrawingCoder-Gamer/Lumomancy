package gay.menkissing.lumomancy.client

import gay.menkissing.lumomancy.client.gui.ToolContainerGui
import gay.menkissing.lumomancy.content.LumomancyItems
import gay.menkissing.lumomancy.content.item.StasisBottle
import gay.menkissing.lumomancy.registries.LumomancyScreens
import net.fabricmc.api.{ClientModInitializer, EnvType, Environment}
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.gui.screens.MenuScreens

@Environment(EnvType.CLIENT)
object LumomancyClient extends ClientModInitializer:
  override def onInitializeClient(): Unit =
    BuiltinItemRendererRegistry.INSTANCE.register(LumomancyItems.stasisBottle, new StasisBottle.Renderer())
    ModelLoadingPlugin.register(new StasisBottle.Renderer())
    MenuScreens.register(LumomancyScreens.toolContainer, ToolContainerGui.apply)

  