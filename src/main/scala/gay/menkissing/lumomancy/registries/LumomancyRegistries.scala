package gay.menkissing.lumomancy.registries

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.api.energy.LumonColor
import net.fabricmc.fabric.api.event.registry.{FabricRegistryBuilder, RegistryAttribute}
import net.minecraft.core.Registry
import net.minecraft.resources.{ResourceKey, ResourceLocation}

object LumomancyRegistries:
  val lumonColorsID: ResourceLocation = Lumomancy.locate("lumon_color")
  val lumonColorsKey: ResourceKey[Registry[LumonColor]] = ResourceKey.createRegistryKey(lumonColorsID)
  val lumonColors: Registry[LumonColor] = FabricRegistryBuilder.createSimple(lumonColorsKey)
                                                               .attribute(RegistryAttribute.SYNCED).buildAndRegister()
  
  
  def init(): Unit = ()
