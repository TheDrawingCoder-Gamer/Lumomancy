/*
 * Copyright (c) BulbyVR/TheDrawingCoder-Gamer 2025.
 *
 * This file is part of Lumomancy.
 *
 * Lumomancy is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the license, or (at your option) any later version.
 *
 * Lumomancy is distributed in the hopes it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 *  PARTICULAR PURPOSE. See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with Lumomancy. If not,
 *  see <https://www.gnu.org/licenses/>
 */

package gay.menkissing.lumomancy.content

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.item.{LumonLens, StasisBottle, StasisTube, ToolContainer}
import gay.menkissing.lumomancy.util.LumoEnchantmentHelper
import gay.menkissing.lumomancy.util.registry.InfoCollector
import gay.menkissing.lumomancy.util.registry.builder.ItemBuilder
import net.fabricmc.fabric.api.itemgroup.v1.{FabricItemGroup, ItemGroupEvents}
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.{CreativeModeTab, Item, ItemStack}

import scala.collection.mutable as mut

object LumomancyItems:
  private val items: mut.ListBuffer[Item] = mut.ListBuffer()

  extension (builder: ItemBuilder[?])
    def make(): Item =
      val i = builder.register()
      items.append(i)
      i



  val itemGroupKey: ResourceKey[CreativeModeTab] = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Lumomancy.MOD_ID, "creative_tab"))
  val itemGroup: CreativeModeTab = FabricItemGroup.builder()
                                                  .icon(() => new ItemStack(lumonLens))
                                                  .title(Component.translatable("itemGroup.lumomancy"))
                                                  .build()

  val clearQuartz: Item =
    InfoCollector.instance.item("clear_quartz", Item(Item.Properties()))
                 .lang("Clear Quartz")
                 .defaultModel()
                 .make()
  

  val bloodTopazShard: Item =
    InfoCollector.instance.item("blood_topaz_shard", Item(Item.Properties()))
                 .lang("Blood Topaz Shard")
                 .defaultModel()
                 .make()

  val prasioliteShard: Item = 
    InfoCollector.instance.item(Lumomancy.locate("prasiolite_shard"), new Item(Item.Properties()))
                 .lang("Prasiolite Shard")
                 .defaultModel()
                 .make()

  val adventurineShard: Item = 
    InfoCollector.instance.item(Lumomancy.locate("adventurine_shard"), new Item(Item.Properties()))
                 .lang("Adventurine Shard")
                 .defaultModel()
                 .make()
  

  // val lens: Item = make(Lumomancy.locate("lens"), new Item(Item.Properties()))
  
  val toolContainer: Item = 
    InfoCollector.instance.item(Lumomancy.locate("tool_container"), new ToolContainer(Item.Properties().stacksTo(1)))
                 .lang("Tool Container")
                 .defaultModel()
                 .make()

  val bottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("bottle_of_light"), new Item(Item.Properties()))
                 .lang("Bottle of Light")
                 .defaultModel()
                 .make()

  val azureBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("azure_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Azure Bottle of Light")
                 .defaultModel()
                 .make()
  val blackBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("black_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Black Bottle of Light")
                 .defaultModel()
                 .make()
  val blueBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("blue_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Blue Bottle of Light")
                 .defaultModel()
                 .make()
  val brownBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("brown_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Brown Bottle of Light")
                 .defaultModel()
                 .make()
  val cyanBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("cyan_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Cyan Bottle of Light")
                 .defaultModel()
                 .make()
  val grayBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("gray_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Gray Bottle of Light")
                 .defaultModel()
                 .make()
  val greenBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("green_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Green Bottle of Light")
                 .defaultModel()
                 .make()
  val lightGrayBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("light_gray_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Light Gray Bottle of Light")
                 .defaultModel()
                 .make()
  val limeBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("lime_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Lime Bottle of Light")
                 .defaultModel()
                 .make()
  val magentaBottleOfLight: Item =
    InfoCollector.instance.item(Lumomancy.locate("magenta_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Magenta Bottle of Light")
                 .defaultModel()
                 .make()
  val orangeBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("orange_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Orange Bottle of Light")
                 .defaultModel()
                 .make()
  val purpleBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("purple_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Purple Bottle of Light")
                 .defaultModel()
                 .make()
  val redBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("red_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Red Bottle of Light")
                 .defaultModel()
                 .make()
  val roseBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("rose_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Rose Bottle of Light")
                 .defaultModel()
                 .make()
  val seafoamBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("seafoam_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Seafoam Bottle of Light")
                 .defaultModel()
                 .make()
  val whiteBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("white_bottle_of_light"), new Item(Item.Properties()))
                 .lang("White Bottle of Light")
                 .defaultModel()
                 .make()
  val yellowBottleOfLight: Item = 
    InfoCollector.instance.item(Lumomancy.locate("yellow_bottle_of_light"), new Item(Item.Properties()))
                 .lang("Yellow Bottle of Light")
                 .defaultModel()
                 .make()

  val stasisTube: Item = 
    InfoCollector.instance.item(Lumomancy.locate("stasis_tube"), new StasisTube(Item.Properties().stacksTo(1)))
                 .lang("Stasis Tube")
                 .tooltip("empty", "Empty")
                 .tooltip("count", "%1$d / %2$d (%3$d stacks)")
                 .make()
  val stasisBottle: Item = 
    InfoCollector.instance.item(Lumomancy.locate("stasis_bottle"), new StasisBottle(Item.Properties().stacksTo(1)))
                 .lang("Stasis Bottle")
                 .tooltip("empty", "Empty")
                 .tooltip("usage_pickup", "Use to pickup")
                 .tooltip("usage_place", "Sneak-use to place")
                 .tooltip("count_mb", "%1$s mB / %2$s buckets")
                 .make()

  val lumonLens: Item = 
    InfoCollector.instance.item(Lumomancy.locate("lumon_lens"), new LumonLens(Item.Properties().stacksTo(1)))
                 .lang("Lumon Lens")
                 .defaultModel()
                 .make()
  
  val stillwoodBark: Item = 
    InfoCollector.instance.item(Lumomancy.locate("stillwood_bark"), new Item(Item.Properties()))
                 .lang("Stillwood Bark")
                 .defaultModel()
                 .make()
  val wiederBark: Item = 
    InfoCollector.instance.item(Lumomancy.locate("wieder_bark"), Item(Item.Properties()))
                 .lang("Wieder Bark")
                 .defaultModel()
                 .make()
  val aftusBark: Item = 
    InfoCollector.instance.item(Lumomancy.locate("aftus_bark"), Item(Item.Properties()))
                 .lang("Aftus Bark")
                 .defaultModel()
                 .make()

  def init(): Unit =
    Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, itemGroupKey, itemGroup)
    StasisBottle.registerCauldronInteractions()
    ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register { group =>
      items.foreach(group.accept)
    }

    FluidStorage.ITEM.registerForItems((stack: ItemStack, context: ContainerItemContext) => {
      StasisBottle.StasisBottleContents.StasisBottleStorage(context)
    }, stasisBottle)
