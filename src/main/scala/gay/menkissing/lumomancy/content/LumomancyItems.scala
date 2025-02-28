package gay.menkissing.lumomancy.content

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.item.{StasisBottle, ToolContainer}
import net.fabricmc.fabric.api.itemgroup.v1.{FabricItemGroup, ItemGroupEvents}
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.world.item.{CreativeModeTab, Item, ItemStack}

import scala.collection.mutable as mut

object LumomancyItems:
  private val items: mut.ListBuffer[Item] = mut.ListBuffer()

  def make(rl: ResourceLocation, item: Item): Item =
    items.append(item)
    Registry.register(BuiltInRegistries.ITEM, rl, item)



  val itemGroupKey: ResourceKey[CreativeModeTab] = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Lumomancy.MOD_ID, "creative_tab"))
  val itemGroup: CreativeModeTab = FabricItemGroup.builder()
                                                  .icon(() => new ItemStack(bloodTopazShard))
                                                  .title(Component.translatable("itemGroup.lumomancy"))
                                                  .build()

  val clearQuartz: Item = make(Lumomancy.locate("clear_quartz"), new Item(Item.Properties()))

  val bloodTopazShard: Item = make(Lumomancy.locate("blood_topaz_shard"), new Item(Item.Properties()))

  val prasioliteShard: Item = make(Lumomancy.locate("prasiolite_shard"), new Item(Item.Properties()))

  val adventurineShard: Item = make(Lumomancy.locate("adventurine_shard"), new Item(Item.Properties()))
  

  val lens: Item = make(Lumomancy.locate("lens"), new Item(Item.Properties()))
  
  val toolContainer: Item = make(Lumomancy.locate("tool_container"), new ToolContainer(Item.Properties()))

  val bottleOfLight: Item = make(Lumomancy.locate("bottle_of_light"), new Item(Item.Properties()))

  val azureBottleOfLight: Item = make(Lumomancy.locate("azure_bottle_of_light"), new Item(Item.Properties()))
  val blackBottleOfLight: Item = make(Lumomancy.locate("black_bottle_of_light"), new Item(Item.Properties()))
  val blueBottleOfLight: Item = make(Lumomancy.locate("blue_bottle_of_light"), new Item(Item.Properties()))
  val cyanBottleOfLight: Item = make(Lumomancy.locate("cyan_bottle_of_light"), new Item(Item.Properties()))
  val greenBottleOfLight: Item = make(Lumomancy.locate("green_bottle_of_light"), new Item(Item.Properties()))
  val limeBottleOfLight: Item = make(Lumomancy.locate("lime_bottle_of_light"), new Item(Item.Properties()))
  val magentaBottleOfLight: Item = make(Lumomancy.locate("magenta_bottle_of_light"), new Item(Item.Properties()))
  val orangeBottleOfLight: Item = make(Lumomancy.locate("orange_bottle_of_light"), new Item(Item.Properties()))
  val purpleBottleOfLight: Item = make(Lumomancy.locate("purple_bottle_of_light"), new Item(Item.Properties()))
  val redBottleOfLight: Item = make(Lumomancy.locate("red_bottle_of_light"), new Item(Item.Properties()))
  val roseBottleOfLight: Item = make(Lumomancy.locate("rose_bottle_of_light"), new Item(Item.Properties()))
  val seafoamBottleOfLight: Item = make(Lumomancy.locate("seafoam_bottle_of_light"), new Item(Item.Properties()))
  val whiteBottleOfLight: Item = make(Lumomancy.locate("white_bottle_of_light"), new Item(Item.Properties()))
  val yellowBottleOfLight: Item = make(Lumomancy.locate("yellow_bottle_of_light"), new Item(Item.Properties()))

  val stasisBottle: Item = make(Lumomancy.locate("stasis_bottle"), new StasisBottle(Item.Properties().stacksTo(1)))

  def init(): Unit =
    Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, itemGroupKey, itemGroup)
    ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register { group =>
      items.foreach(group.accept)
    }