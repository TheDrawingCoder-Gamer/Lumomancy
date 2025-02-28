package gay.menkissing.lumomancy.registries

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.api.energy.LumonColor
import gay.menkissing.lumomancy.content.item.StasisBottle.StasisBottleContents
import gay.menkissing.lumomancy.util.codec.LumoCodecs
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack

object LumomancyDataComponents:
  val lumonColorComponent: DataComponentType[LumonColor] = Registry.register(
    BuiltInRegistries.DATA_COMPONENT_TYPE,
    Lumomancy.locate("lumon_color"),
    DataComponentType.builder[LumonColor]().persistent(LumonColor.CODEC).build()
  )
  
  val itemBackedInventoryComponent: DataComponentType[List[ItemStack]] = Registry.register(
    BuiltInRegistries.DATA_COMPONENT_TYPE,
    Lumomancy.locate("item_backed_inventory"),
    DataComponentType.builder[List[ItemStack]]().persistent(LumoCodecs.scalaListCodec[ItemStack](ItemStack.CODEC)).build()
  )



  val stasisBottleContents: DataComponentType[StasisBottleContents] = Registry.register(
    BuiltInRegistries.DATA_COMPONENT_TYPE,
    Lumomancy.locate("stasis_bottle_contents"),
    DataComponentType.builder[StasisBottleContents]().persistent(StasisBottleContents.CODEC).build()
  )
  
  def init(): Unit = ()