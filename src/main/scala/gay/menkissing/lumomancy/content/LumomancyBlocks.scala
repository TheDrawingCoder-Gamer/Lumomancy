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

import com.google.common.collect.ImmutableMap
import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.block.{StasisCooler, StrippablePillarBlock}
import gay.menkissing.lumomancy.content.block.entity.StasisCoolerBlockEntity
import gay.menkissing.lumomancy.mixin.AxeItemAccessor
import gay.menkissing.lumomancy.registries.{LumoBlockFamilies, LumoWorldFeatures, LumomancyLootTables, LumomancyTags}
import gay.menkissing.lumomancy.util.resources.{*, given}
import net.fabricmc.api.{EnvType, Environment}
import net.fabricmc.fabric.api.`object`.builder.v1.block.`type`.{BlockSetTypeBuilder, WoodTypeBuilder}
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.{Direction, Registry}
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.world.item.{BlockItem, HangingSignItem, Item, SignItem}
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.{Block, Blocks, CeilingHangingSignBlock, DoorBlock, FenceBlock, FenceGateBlock, LeavesBlock, PressurePlateBlock, RotatedPillarBlock, SaplingBlock, SignBlock, SlabBlock, SoundType, StairBlock, StandingSignBlock, TrapDoorBlock, WallHangingSignBlock, WallSignBlock}
import net.minecraft.world.level.block.state.{BlockBehaviour, BlockState}
import net.minecraft.world.level.block.state.properties.{BlockSetType, NoteBlockInstrument, WoodType}
import net.minecraft.world.level.material.{MapColor, PushReaction}
import BlockBehaviour.Properties as BlockProps
import gay.menkissing.lumomancy.util.registry.InfoCollector
import gay.menkissing.lumomancy.util.registry.builder.{BlockBuilder, ItemBuilder}
import net.minecraft.core.Direction.Axis
import net.minecraft.tags.{BlockTags, ItemTags, TagKey}
import net.minecraft.world.level.block.grower.TreeGrower
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.storage.loot.LootTable

import scala.collection.mutable
import java.util.Optional

object LumomancyBlocks:
  private val blockItems: mutable.ListBuffer[Item] = mutable.ListBuffer()

  extension (builder: BlockBuilder[?])
    def registerItem(): Block =
      val i = builder.register()
      blockItems.append(i.asItem())
      i

  extension (builder: ItemBuilder[?])
    def registerItem(): Item =
      val i = builder.register()
      blockItems.append(i.asItem())
      i

  def makeItem(rl: ResourceLocation, item: Item): Item =
    blockItems.append(item)
    Registry.register(BuiltInRegistries.ITEM, rl, item)
    
  def makeEntity[T <: BlockEntity](name: String, factory: BlockEntityType.BlockEntitySupplier[T], blocks: Block*): BlockEntityType[T] =
    val id = Lumomancy.locate(name)
    Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.of[T](factory, blocks*).build())

  // strength copied from chiseled bookshelf
  val stasisCooler: Block =
    InfoCollector.instance.block(Lumomancy.locate("stasis_cooler"), StasisCooler(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.5f)))
                 .lang("Stasis Cooler")
                 .item()
                 .model(gen => item => gen.withExistingParent(item, stasisCooler.modelLoc.withSuffix("_inventory"))).build()
                 .tag(BlockTags.MINEABLE_WITH_AXE)
                 .dropSelf()
                 .registerItem()
  
  val stasisCoolerBlockEntity: BlockEntityType[StasisCoolerBlockEntity] =
    makeEntity("stasis_cooler",StasisCoolerBlockEntity.apply, stasisCooler)




  trait WoodSet(id: String, name: String):

    val blockSet: BlockSetType = BlockSetTypeBuilder.copyOf(BlockSetType.OAK).register(Lumomancy.locate(id))
    val woodSet: WoodType = WoodTypeBuilder.copyOf(WoodType.OAK).register(Lumomancy.locate(id), blockSet)

    val barkColor: MapColor
    val plankColor: MapColor

    def logItemTag: TagKey[Item]
    def logBlockTag: TagKey[Block]

    val strippedLog: Block =
      InfoCollector.instance.block("stripped_" + id + "_log", Blocks.log(plankColor, plankColor))
                   .lang(s"Stripped $name Log")
                   .blockItem().tag(logItemTag).build()
                   .blockstate(_.logBlock)
                   .tag(logBlockTag)
                   .dropSelf()
                   .registerItem()


    val strippedWood: Block =
      InfoCollector.instance.block(s"stripped_${id}_wood", RotatedPillarBlock(BlockProps.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).mapColor(plankColor)))
                   .lang(s"Stripped $name Wood")
                   .blockItem().tag(logItemTag).build()
                   .blockstate(gen => block => gen.woodBlock(block, strippedLog.modelLoc))
                   .tag(logBlockTag)
                   .dropSelf()
                   .registerItem()

    def stripDrop: ResourceKey[LootTable]

    val log: Block =
      InfoCollector.instance.block(s"${id}_log", StrippablePillarBlock(strippedLog, stripDrop, BlockProps.ofFullCopy(Blocks.OAK_LOG).mapColor((state: BlockState) => if state.getValue(RotatedPillarBlock.AXIS) == Axis
        .Y then plankColor else barkColor)))
                   .lang(s"$name Log")
                   .blockItem().tag(logItemTag).build()
                   .blockstate(_.logBlock)
                   .tag(logBlockTag)
                   .dropSelf()
                   .registerItem()

    val wood: Block =
      InfoCollector.instance.block(s"${id}_wood", StrippablePillarBlock(strippedWood, stripDrop, BlockProps.ofFullCopy(Blocks.OAK_WOOD).mapColor(barkColor)))
                  .lang(s"$name Wood")
                   .blockItem().tag(logItemTag).build()
                   .blockstate(gen => block => gen.woodBlock(block, log.modelLoc))
                   .tag(logBlockTag)
                   .dropSelf()
                   .registerItem()

    val planks: Block =
      InfoCollector.instance.block(s"${id}_planks", Block(BlockProps.ofFullCopy(Blocks.OAK_PLANKS).mapColor(plankColor)))
                   .lang(s"$name Planks")
                   .blockItem().tag(ItemTags.PLANKS).build()
                   .tag(BlockTags.PLANKS)
                   .blockstate(_.simpleBlock)
                   .dropSelf()
                   .registerItem()

    val slab: Block =
      InfoCollector.instance.block(s"${id}_slab", SlabBlock(BlockProps.ofFullCopy(Blocks.OAK_SLAB).mapColor(planks.defaultMapColor())))
                   .lang(s"$name Slab")
                   .blockItem().tag(ItemTags.WOODEN_SLABS).build()
                   .tag(BlockTags.WOODEN_SLABS)
                   .lootTable(_.createSlabItemTable(slab))
                   .blockstate(gen => block => gen.slabBlock(block, planks.modelLoc, planks.modelLoc))
                   .registerItem()

    val button: Block =
      InfoCollector.instance.block(s"${id}_button", Blocks.woodenButton(blockSet))
                   .lang(s"$name Button")
                   .item()
                   .model(gen => item =>
                     gen.buttonInventory(button.modelLoc.withSuffix("_inventory"), planks.modelLoc)
                     gen.withExistingParent(item, button.modelLoc.withSuffix("_inventory"))
                   )
                   .tag(ItemTags.WOODEN_BUTTONS)
                   .build()
                   .tag(BlockTags.WOODEN_BUTTONS)
                   .blockstate(gen => block => gen.buttonBlock(block, planks.modelLoc))
                   .dropSelf()
                   .registerItem()

    val pressurePlate: Block =
      InfoCollector.instance.block(s"${id}_pressure_plate", PressurePlateBlock(blockSet, BlockProps.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(planks.defaultMapColor())))
                   .lang(s"$name Pressure Plate")
                   .blockItem().tag(ItemTags.WOODEN_PRESSURE_PLATES).build()
                   .tag(BlockTags.WOODEN_PRESSURE_PLATES)
                   .blockstate(gen => block => gen.pressurePlateBlock(block, planks.modelLoc))
                   .dropSelf()
                   .registerItem()

    val fence: Block =
      InfoCollector.instance.block(s"${id}_fence",
                     FenceBlock(BlockProps.ofFullCopy(Blocks.OAK_FENCE).mapColor(plankColor)))
                   .lang(s"$name Fence")
                   .item().model(gen => item =>
                     gen.fenceInventory(fence.modelLoc.withSuffix("_inventory"), planks.modelLoc)
                     gen.withExistingParent(item, fence.modelLoc.withSuffix("_inventory"))
                   )
                   .tag(ItemTags.WOODEN_FENCES)
                   .build()
                   .tag(BlockTags.WOODEN_FENCES)
                   .blockstate(gen => block => gen.fenceBlock(block, planks.modelLoc))
                   .dropSelf()
                   .registerItem()

    val fenceGate: Block =
      InfoCollector.instance
                   .block(s"${id}_fence_gate",
                     FenceGateBlock(woodSet, BlockProps.ofFullCopy(Blocks.OAK_FENCE_GATE).mapColor(plankColor)))
                   .lang(s"$name Fence Gate")
                   .blockItem().tag(ItemTags.FENCE_GATES).build()
                   .tag(BlockTags.FENCE_GATES)
                   .blockstate(gen => block => gen.fenceGateBlock(block, planks.modelLoc))
                   .dropSelf()
                   .registerItem()

    val stairs: Block =
      InfoCollector.instance.block(s"${id}_stairs", StairBlock(planks.defaultBlockState(), BlockProps.ofFullCopy(planks)))
                   .lang(s"$name Stairs")
                   .blockItem().tag(ItemTags.WOODEN_STAIRS).build()
                   .tag(BlockTags.WOODEN_STAIRS)
                   .blockstate(gen => block => gen.stairsBlock(block, planks.modelLoc))
                   .dropSelf()
                   .registerItem()

    //noinspection ForwardReference
    val sign: Block =
      InfoCollector.instance
                   .block(s"${id}_sign", StandingSignBlock(woodSet, BlockProps.ofFullCopy(Blocks.OAK_SIGN).mapColor(plankColor)))
                   .lang(s"$name Sign")
                   .tag(BlockTags.STANDING_SIGNS)
                   // hopefully the forward reference is tolerable here
                   .lootTable(_.createSingleItemTable(this.signItem))
                   .register()

    val wallSign: Block =
      InfoCollector.instance
                   .block(s"${id}_wall_sign", WallSignBlock(woodSet, BlockBehaviour
                     .Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).mapColor(plankColor)
                     .dropsLike(sign)))
                   .blockstate(gen => block => gen.signBlock(sign, block, planks.modelLoc))
                   .tag(BlockTags.WALL_SIGNS)
                   .register()

    val signItem: Item =
      InfoCollector.instance.item(s"${id}_sign", SignItem(Item.Properties().stacksTo(16), sign, wallSign))
                   .lang(s"$name Sign")
                   .tag(ItemTags.SIGNS)
                   .defaultModel()
                   .registerItem()

    //noinspection ForwardReference
    val hangingSign: Block =
      InfoCollector.instance.block(s"${id}_hanging_sign",
                     CeilingHangingSignBlock(woodSet, BlockProps.ofFullCopy(Blocks.OAK_HANGING_SIGN).mapColor(plankColor)))
                   .lang(s"$name Hanging Sign")
                   .tag(BlockTags.CEILING_HANGING_SIGNS)
                   .lootTable(_.createSingleItemTable(this.hangingSignItem))
                   .register()
    val wallHangingSign: Block =
      InfoCollector.instance.block(s"${id}_wall_hanging_sign",
                     WallHangingSignBlock(woodSet, BlockProps.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).mapColor(plankColor).dropsLike(hangingSign)))
                   .tag(BlockTags.WALL_HANGING_SIGNS)
                   .blockstate(gen => block => gen
                     .signBlock(hangingSign, block, strippedLog.modelLoc))
                   .register()

    val hangingSignItem: Item =
      InfoCollector.instance.item(s"${id}_hanging_sign", HangingSignItem(hangingSign, wallHangingSign, Item.Properties().stacksTo(16)))
                   .lang(s"$name Hanging Sign")
                   .tag(ItemTags.HANGING_SIGNS)
                   .defaultModel()
                   .registerItem()

    val door: Block =
      InfoCollector.instance.block(s"${id}_door",
                     DoorBlock(blockSet, BlockProps.ofFullCopy(Blocks.OAK_DOOR).mapColor(planks.defaultMapColor())))
                   .lang(s"$name Door")
                   .item().defaultModel()
                   .tag(ItemTags.WOODEN_DOORS)
                   .build()
                   .tag(BlockTags.WOODEN_DOORS)
                   .lootTable(_.createDoorTable(door))
                   .blockstate(_.doorBlock)
                   .registerItem()

    val trapdoor: Block =
      InfoCollector.instance.block(s"${id}_trapdoor",
                     TrapDoorBlock(blockSet, BlockProps.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(planks.defaultMapColor())))
                   .lang(s"$name Trapdoor")
                   .item()
                   .tag(ItemTags.WOODEN_TRAPDOORS)
                   .model(gen => item => gen.withExistingParent(item, trapdoor.modelLoc.extend("_bottom")))
                   .build()
                   .blockstate(gen => block => gen.trapdoorBlock(block))
                   .tag(BlockTags.WOODEN_TRAPDOORS)
                   .dropSelf()
                   .registerItem()


    def treeFeature: ResourceKey[ConfiguredFeature[?, ?]]

    val treeGrower = TreeGrower("LUMO_" + id.toUpperCase, Optional.empty(), Optional.of(LumoWorldFeatures.stillwoodTree), Optional
      .empty())

    val sapling: Block =
      InfoCollector.instance
                   .block(s"${id}_sapling", SaplingBlock(treeGrower, BlockProps.ofFullCopy(Blocks.OAK_SAPLING)))
                   .item()
                   .model(gen => item => gen.flatItem(item, sapling.modelLoc))
                   .tag(LumomancyTags.item.coloredSaplingsTag)
                   .build()
                   .blockstate(gen => block => gen.crossBlock(block, sapling.modelLoc))
                   .lang(s"$name Sapling")
                   .tag(LumomancyTags.block.coloredSaplingsTag)
                   .dropSelf()
                   .registerItem()

    val leaves: Block =
      InfoCollector.instance.block(s"${id}_leaves", LeavesBlock(BlockProps.ofFullCopy(Blocks.OAK_LEAVES)))
                   .blockItem()
                   .tag(LumomancyTags.item.coloredLeavesTag)
                   .build()
                   .lang(s"$name Leaves")
                   .lootTable(loot => loot
                     .createLeavesDrops(leaves, sapling, 0.02f, 0.022222223f, 0.025f, 0.033333335f, 0.1f))
                   .blockstate(_.simpleBlock)
                   .tag(LumomancyTags.block.coloredLeavesTag)
                   .registerItem()


    def appendStrippables(map: java.util.HashMap[Block, Block]): Unit =
      map.put(log, strippedLog)
      map.put(wood, strippedWood)

    def init(): Unit =
      BlockEntityType.SIGN.addSupportedBlock(sign)
      BlockEntityType.SIGN.addSupportedBlock(wallSign)
      BlockEntityType.HANGING_SIGN.addSupportedBlock(hangingSign)
      BlockEntityType.HANGING_SIGN.addSupportedBlock(wallHangingSign)

    @Environment(EnvType.CLIENT)
    def initClient(): Unit =
      BlockRenderLayerMap.INSTANCE.putBlock(door, RenderType.cutout())
      BlockRenderLayerMap.INSTANCE.putBlock(trapdoor, RenderType.cutout())
      BlockRenderLayerMap.INSTANCE.putBlock(sapling, RenderType.cutout())
      BlockRenderLayerMap.INSTANCE.putBlock(leaves, RenderType.cutout())

  // STILL WOOD SET

  object stillwood extends WoodSet("stillwood", "Stillwood"):
    override val barkColor: MapColor = MapColor.TERRACOTTA_CYAN
    override val plankColor: MapColor = MapColor.COLOR_CYAN

    override def logItemTag: TagKey[Item] = LumomancyTags.item.stillwoodLogsTag

    override def logBlockTag: TagKey[Block] = LumomancyTags.block.stillwoodLogsTag

    override def stripDrop: ResourceKey[LootTable] = LumomancyLootTables.stripStillwood

    override def treeFeature: ResourceKey[ConfiguredFeature[?, ?]] = LumoWorldFeatures.stillwoodTree



  object wieder extends WoodSet("wieder", "Wieder"):
    override val barkColor: MapColor = MapColor.TERRACOTTA_MAGENTA
    override val plankColor: MapColor = MapColor.COLOR_MAGENTA

    override def logItemTag: TagKey[Item] = LumomancyTags.item.wiederLogsTag
    override def logBlockTag: TagKey[Block] = LumomancyTags.block.wiederLogsTag

    override def stripDrop: ResourceKey[LootTable] = LumomancyLootTables.stripWieder

    override def treeFeature: ResourceKey[ConfiguredFeature[?, ?]] = LumoWorldFeatures.wiederTree

  object aftus extends WoodSet("aftus", "Aftus"):
    override val barkColor: MapColor = MapColor.TERRACOTTA_YELLOW
    override val plankColor: MapColor = MapColor.COLOR_YELLOW

    override def logItemTag: TagKey[Item] = LumomancyTags.item.aftusLogsTag

    override def logBlockTag: TagKey[Block] = LumomancyTags.block.aftusLogsTag

    override def stripDrop: ResourceKey[LootTable] = LumomancyLootTables.stripAftus

    override def treeFeature: ResourceKey[ConfiguredFeature[?, ?]] = LumoWorldFeatures.aftusTree

  @Environment(EnvType.CLIENT)
  def registerClient(): Unit =
    stillwood.initClient()
    wieder.initClient()
    aftus.initClient()


  def init(): Unit =
    StasisCoolerBlockEntity.registerStorages()
    stillwood.init()
    wieder.init()
    aftus.init()
    ItemGroupEvents.modifyEntriesEvent(LumomancyItems.itemGroupKey).register { group =>
      blockItems.foreach(group.accept)
    }
    val updatedMap = java.util.HashMap(AxeItemAccessor.getStrippables)

    stillwood.appendStrippables(updatedMap)
    wieder.appendStrippables(updatedMap)
    aftus.appendStrippables(updatedMap)

    AxeItemAccessor.setStrippables(ImmutableMap.copyOf(updatedMap))

    
