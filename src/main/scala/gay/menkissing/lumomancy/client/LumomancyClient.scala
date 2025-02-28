package gay.menkissing.lumomancy.client

import gay.menkissing.lumomancy.client.gui.ToolContainerGui
import gay.menkissing.lumomancy.registries.LumomancyScreens
import net.fabricmc.api.{ClientModInitializer, EnvType, Environment}
import net.minecraft.client.gui.screens.MenuScreens

@Environment(EnvType.CLIENT)
object LumomancyClient extends ClientModInitializer:
  override def onInitializeClient(): Unit =
    MenuScreens.register(LumomancyScreens.toolContainer, ToolContainerGui.apply)

  