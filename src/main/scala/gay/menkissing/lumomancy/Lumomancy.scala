package gay.menkissing.lumomancy

import gay.menkissing.lumomancy.content.LumomancyItems
import gay.menkissing.lumomancy.registries.{LumomancyDataComponents, LumomancyRegistries, LumomancyScreens, LumomancyTags, LumonColors}
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation

object Lumomancy extends ModInitializer:
  val MOD_ID: String = "lumomancy"

  def locate(id: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, id)
  
  override def onInitialize(): Unit =
    LumomancyItems.init()
    LumomancyRegistries.init()
    LumomancyDataComponents.init()
    LumomancyTags.init()
    LumomancyScreens.init()
    LumonColors.init()