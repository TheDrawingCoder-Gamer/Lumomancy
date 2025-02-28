package gay.menkissing.lumomancy.registries

import com.mojang.serialization.Codec
import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.screen.ToolContainerMenu
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.{FriendlyByteBuf, RegistryFriendlyByteBuf}
import net.minecraft.network.codec.{StreamCodec, StreamDecoder, StreamEncoder}
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.inventory.MenuType

object LumomancyScreens:
  val toolContainer: ExtendedScreenHandlerType[ToolContainerMenu, Boolean] = new ExtendedScreenHandlerType[ToolContainerMenu, Boolean](ToolContainerMenu.fromNetwork, StreamCodec.of(
    new StreamEncoder[RegistryFriendlyByteBuf, Boolean] {
      override def encode(a: RegistryFriendlyByteBuf, b: Boolean): Unit =
        a.writeBoolean(b)  
    },
    new StreamDecoder[RegistryFriendlyByteBuf, Boolean] {
      override def decode(buf: RegistryFriendlyByteBuf): Boolean = buf.readBoolean()
    }
  ))
  Registry.register(BuiltInRegistries.MENU, Lumomancy.locate("tool_container"), toolContainer)

  def init(): Unit = ()