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

  def make(rl: ResourceLocation, item: Item): Item =
    items.append(item)
    Registry.register(BuiltInRegistries.ITEM, rl, item)



  val itemGroupKey: ResourceKey[CreativeModeTab] = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath(Lumomancy.MOD_ID, "creative_tab"))
  val itemGroup: CreativeModeTab = FabricItemGroup.builder()
                                                  .icon(() => new ItemStack(lumonLens))
                                                  .title(Component.translatable("itemGroup.lumomancy"))
                                                  .build()

  val clearQuartz: Item = make(Lumomancy.locate("clear_quartz"), new Item(Item.Properties()))

  val bloodTopazShard: Item = make(Lumomancy.locate("blood_topaz_shard"), new Item(Item.Properties()))

  val prasioliteShard: Item = make(Lumomancy.locate("prasiolite_shard"), new Item(Item.Properties()))

  val adventurineShard: Item = make(Lumomancy.locate("adventurine_shard"), new Item(Item.Properties()))
  

  // val lens: Item = make(Lumomancy.locate("lens"), new Item(Item.Properties()))
  
  val toolContainer: Item = make(Lumomancy.locate("tool_container"), new ToolContainer(Item.Properties().stacksTo(1)))

  val bottleOfLight: Item = make(Lumomancy.locate("bottle_of_light"), new Item(Item.Properties()))

  val azureBottleOfLight: Item = make(Lumomancy.locate("azure_bottle_of_light"), new Item(Item.Properties()))
  val blackBottleOfLight: Item = make(Lumomancy.locate("black_bottle_of_light"), new Item(Item.Properties()))
  val blueBottleOfLight: Item = make(Lumomancy.locate("blue_bottle_of_light"), new Item(Item.Properties()))
  val brownBottleOfLight: Item = make(Lumomancy.locate("brown_bottle_of_light"), new Item(Item.Properties()))
  val cyanBottleOfLight: Item = make(Lumomancy.locate("cyan_bottle_of_light"), new Item(Item.Properties()))
  val grayBottleOfLight: Item = make(Lumomancy.locate("gray_bottle_of_light"), new Item(Item.Properties()))
  val greenBottleOfLight: Item = make(Lumomancy.locate("green_bottle_of_light"), new Item(Item.Properties()))
  val lightGrayBottleOfLight: Item = make(Lumomancy.locate("light_gray_bottle_of_light"), new Item(Item.Properties()))
  val limeBottleOfLight: Item = make(Lumomancy.locate("lime_bottle_of_light"), new Item(Item.Properties()))
  val magentaBottleOfLight: Item = make(Lumomancy.locate("magenta_bottle_of_light"), new Item(Item.Properties()))
  val orangeBottleOfLight: Item = make(Lumomancy.locate("orange_bottle_of_light"), new Item(Item.Properties()))
  val purpleBottleOfLight: Item = make(Lumomancy.locate("purple_bottle_of_light"), new Item(Item.Properties()))
  val redBottleOfLight: Item = make(Lumomancy.locate("red_bottle_of_light"), new Item(Item.Properties()))
  val roseBottleOfLight: Item = make(Lumomancy.locate("rose_bottle_of_light"), new Item(Item.Properties()))
  val seafoamBottleOfLight: Item = make(Lumomancy.locate("seafoam_bottle_of_light"), new Item(Item.Properties()))
  val whiteBottleOfLight: Item = make(Lumomancy.locate("white_bottle_of_light"), new Item(Item.Properties()))
  val yellowBottleOfLight: Item = make(Lumomancy.locate("yellow_bottle_of_light"), new Item(Item.Properties()))

  val stasisTube: Item = make(Lumomancy.locate("stasis_tube"), new StasisTube(Item.Properties().stacksTo(1)))
  val stasisBottle: Item = make(Lumomancy.locate("stasis_bottle"), new StasisBottle(Item.Properties().stacksTo(1)))

  val lumonLens: Item = make(Lumomancy.locate("lumon_lens"), new LumonLens(Item.Properties().stacksTo(1)))
  
  val stillwoodBark: Item = make(Lumomancy.locate("stillwood_bark"), new Item(Item.Properties()))
  val wiederBark: Item = make(Lumomancy.locate("wieder_bark"), Item(Item.Properties()))
  val aftusBark: Item = make(Lumomancy.locate("aftus_bark"), Item(Item.Properties()))

  def init(): Unit =
    Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, itemGroupKey, itemGroup)
    StasisBottle.registerCauldronInteractions()
    ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register { group =>
      items.foreach(group.accept)
    }

    FluidStorage.ITEM.registerForItems((stack: ItemStack, context: ContainerItemContext) => {
      StasisBottle.StasisBottleContents.StasisBottleStorage(context)
    }, stasisBottle)
