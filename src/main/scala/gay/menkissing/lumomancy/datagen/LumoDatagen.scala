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

package gay.menkissing.lumomancy.datagen

import gay.menkissing.lumomancy.content.{LumomancyBlocks, LumomancyItems}
import gay.menkissing.lumomancy.content.block.StasisCooler
import gay.menkissing.lumomancy.registries.{LumoBlockFamilies, LumomancyLootTables, LumomancyTags, LumomancyTranslationKeys}
import gay.menkissing.lumomancy.util.registry.InfoCollector
import net.fabricmc.fabric.api.datagen.v1.loot.FabricBlockLootTableGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.{FabricBlockLootTableProvider, FabricLanguageProvider, FabricModelProvider, FabricTagProvider, SimpleFabricLootTableProvider}
import net.fabricmc.fabric.api.datagen.v1.{DataGeneratorEntrypoint, FabricDataGenerator, FabricDataOutput}
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.{Direction, HolderLookup, Registry}
import net.minecraft.data.models.{BlockModelGenerators, ItemModelGenerators}
import net.minecraft.data.models.blockstates.{Condition, MultiPartGenerator, Variant, VariantProperties}
import net.minecraft.data.models.model.{ModelLocationUtils, ModelTemplate, ModelTemplates, TextureMapping, TextureSlot}
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.tags.{BlockTags, ItemTags, TagKey}
import net.minecraft.world.item.{Item, Items}
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.{BlockStateProperties, EnumProperty}
import net.minecraft.world.level.storage.loot.{LootPool, LootTable}
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue

import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import scala.collection.mutable

object LumoDatagen extends DataGeneratorEntrypoint:
  override def onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator): Unit =
    val pack = fabricDataGenerator.createPack()

    pack.addProvider(ModelGenerator.apply)
    // pack.addProvider(LootTableGenerator.apply)
    pack.addProvider(GameplayLootTableGenerator.apply)
    // pack.addProvider(EnglishLanguageGenerator.apply)
    InfoCollector.instance.registerDataGenerators(pack)
    pack.addProvider(ItemTagGenerator.apply)
    pack.addProvider(BlockTagGenerator.apply)



  class GameplayLootTableGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends SimpleFabricLootTableProvider(output, lookup, LootContextParamSets.BLOCK):
    import net.minecraft.world.level.storage.loot.*
    import entries.LootItem
    
    def stripTable(bark: Item): LootTable.Builder =
      LootTable.lootTable()
      .pool(
          LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0f))
                  .`with`(LootItem.lootTableItem(bark).build())
                  .build()
        )
    
    override def generate(output: BiConsumer[ResourceKey[LootTable], LootTable.Builder]): Unit =
      output.accept(LumomancyLootTables.stripStillwood, stripTable(LumomancyItems.stillwoodBark))
      output.accept(LumomancyLootTables.stripWieder, stripTable(LumomancyItems.wiederBark))
      output.accept(LumomancyLootTables.stripAftus, stripTable(LumomancyItems.aftusBark))
      


  private case class CoolerModelSlotKey(template: ModelTemplate, str: String)

  private class ModelGenerator(output: FabricDataOutput) extends FabricModelProvider(output):

    private val coolerCache = mutable.HashMap[CoolerModelSlotKey, ResourceLocation]()

    def addCoolerSlotModel(modelGens: BlockModelGenerators, generator: MultiPartGenerator, condition: Condition.TerminalCondition, rot: VariantProperties.Rotation, slotProp: EnumProperty[StasisCooler.CoolerSlotOccupiedBy],
                           template: ModelTemplate)(propValue: StasisCooler.CoolerSlotOccupiedBy): Unit =
      val str = "_" + propValue.getSerializedName
      val mapping = new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(LumomancyBlocks.stasisCooler, str))
      val key = CoolerModelSlotKey(template, str)
      val model = coolerCache.getOrElseUpdate(key, template.createWithSuffix(LumomancyBlocks.stasisCooler, str, mapping, modelGens.modelOutput))
      generator.`with`(Condition.and(condition, Condition.condition().term(slotProp, propValue)), Variant.variant().`with`(VariantProperties.MODEL, model).`with`(VariantProperties.Y_ROT, rot))


    def addSlotAndRotationVariants(modelGens: BlockModelGenerators, multiPartGenerator: MultiPartGenerator, condition: Condition.TerminalCondition, rotation: VariantProperties.Rotation): Unit =
      List(
        (StasisCooler.COOLER_SLOT_0_OCCUPIED_BY, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_LEFT),
        (StasisCooler.COOLER_SLOT_1_OCCUPIED_BY, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_MID),
        (StasisCooler.COOLER_SLOT_2_OCCUPIED_BY, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_RIGHT),
        (StasisCooler.COOLER_SLOT_3_OCCUPIED_BY, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT),
        (StasisCooler.COOLER_SLOT_4_OCCUPIED_BY, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_MID),
        (StasisCooler.COOLER_SLOT_5_OCCUPIED_BY, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT)
      ).foreach { (prop, template) =>
        val freakyFunc = addCoolerSlotModel(modelGens, multiPartGenerator, condition, rotation, prop, template)
        freakyFunc(StasisCooler.CoolerSlotOccupiedBy.Empty)
        freakyFunc(StasisCooler.CoolerSlotOccupiedBy.Bottle)
        freakyFunc(StasisCooler.CoolerSlotOccupiedBy.Tube)
      }

    def generateStasisCoolerModels(modelGenerators: BlockModelGenerators): Unit =
      import VariantProperties.Rotation

      val block = LumomancyBlocks.stasisCooler
      val rl = ModelLocationUtils.getModelLocation(block)
      val multiPartGenerator = MultiPartGenerator.multiPart(block)
      List(
        (Direction.NORTH, Rotation.R0),
        (Direction.EAST, Rotation.R90),
        (Direction.SOUTH, Rotation.R180),
        (Direction.WEST, Rotation.R270)
      ).foreach { (dir, rot) =>
        val cond = Condition.condition().term(BlockStateProperties.HORIZONTAL_FACING, dir)
        multiPartGenerator
          .`with`(cond, Variant.variant().`with`(VariantProperties.MODEL, rl).`with`(VariantProperties.Y_ROT, rot)
                               .`with`(VariantProperties.UV_LOCK, true))
        this.addSlotAndRotationVariants(modelGenerators, multiPartGenerator, cond, rot)
      }
      modelGenerators.delegateItemModel(block, ModelLocationUtils.getModelLocation(block, "_inventory"))
      modelGenerators.blockStateOutput.accept(multiPartGenerator)
      
      coolerCache.clear()


    def generateLog(bmg: BlockModelGenerators, log: Block, wood: Block): Unit =
      bmg.woodProvider(log).logWithHorizontal(log).wood(wood)

    def generateStillwood(blockModelGenerators: BlockModelGenerators): Unit =
      val family = LumoBlockFamilies.stillwoodPlanks
      blockModelGenerators.family(family.getBaseBlock).generateFor(family)
      generateLog(blockModelGenerators, LumomancyBlocks.stillwoodLog, LumomancyBlocks.stillwoodWood)
      generateLog(blockModelGenerators, LumomancyBlocks.strippedStillwoodLog, LumomancyBlocks.strippedStillwoodWood)
      blockModelGenerators.createHangingSign(LumomancyBlocks.strippedStillwoodLog, LumomancyBlocks.stillwoodHangingSign, LumomancyBlocks.stillwoodWallHangingSign)

    def generateWieder(blockModelGenerators: BlockModelGenerators): Unit =
      val family = LumoBlockFamilies.wiederPlanks

      blockModelGenerators.family(family.getBaseBlock).generateFor(family)
      generateLog(blockModelGenerators, LumomancyBlocks.wiederLog, LumomancyBlocks.wiederWood)
      generateLog(blockModelGenerators, LumomancyBlocks.strippedWiederLog, LumomancyBlocks.strippedWiederWood)
      blockModelGenerators
        .createHangingSign(LumomancyBlocks.strippedWiederLog, LumomancyBlocks.wiederHangingSign, LumomancyBlocks
          .wiederWallHangingSign)

    def generateAftus(blockModelGenerators: BlockModelGenerators): Unit =
      val family = LumoBlockFamilies.aftusPlanks

      blockModelGenerators.family(family.getBaseBlock).generateFor(family)
      generateLog(blockModelGenerators, LumomancyBlocks.aftusLog, LumomancyBlocks.aftusWood)
      generateLog(blockModelGenerators, LumomancyBlocks.strippedAftusLog, LumomancyBlocks.strippedAftusWood)
      blockModelGenerators
        .createHangingSign(LumomancyBlocks.strippedAftusLog, LumomancyBlocks.aftusHangingSign, LumomancyBlocks
          .aftusWallHangingSign)

    override def generateItemModels(itemModelGenerators: ItemModelGenerators): Unit =
      /*
      // shards
      itemModelGenerators.generateFlatItem(LumomancyItems.adventurineShard, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.clearQuartz, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.bloodTopazShard, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.prasioliteShard, ModelTemplates.FLAT_ITEM)

      // bottles of light
      itemModelGenerators.generateFlatItem(LumomancyItems.azureBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.blackBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.blueBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.bottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.brownBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.cyanBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.grayBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.greenBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.lightGrayBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.limeBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.magentaBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.orangeBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.purpleBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.redBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.roseBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.seafoamBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.whiteBottleOfLight, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.yellowBottleOfLight, ModelTemplates.FLAT_ITEM)

      // lumon lens
      itemModelGenerators.generateFlatItem(LumomancyItems.lumonLens, ModelTemplates.FLAT_ITEM)
      */
      // stasis bottle
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(LumomancyItems.stasisBottle, "_base"), TextureMapping.layer0(LumomancyItems.stasisBottle), itemModelGenerators.output)

      // tool container
      // itemModelGenerators.generateFlatItem(LumomancyItems.toolContainer, ModelTemplates.FLAT_ITEM)

      /*
      itemModelGenerators.generateFlatItem(LumomancyItems.stillwoodBark, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.wiederBark, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.aftusBark, ModelTemplates.FLAT_ITEM)
      */
      // stasis tube
      // is there a way to automate the entity part as well?
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(LumomancyItems.stasisTube, "_base"), TextureMapping.layer0(LumomancyItems.stasisTube), itemModelGenerators.output)



    override def generateBlockStateModels(blockModelGenerators: BlockModelGenerators): Unit =
      generateStasisCoolerModels(blockModelGenerators)
      //generateStillwood(blockModelGenerators)
      //generateWieder(blockModelGenerators)
      //generateAftus(blockModelGenerators)
 

  private object tagHelper:
    trait AcceptsBlockItems[T]:
      def addBlock(self: T, block: Block): T

    extension[T](self: T)(using abi: AcceptsBlockItems[T])
      def addBlock(block: Block): T =
        abi.addBlock(self, block)

    extension[T](self: TagKey[T])
      def transmute[G](target: ResourceKey[? <: Registry[G]]) : TagKey[G] =
        TagKey.create(target, self.location())

    given acceptsBlockItemsItem: AcceptsBlockItems[FabricTagProvider[Item]#FabricTagBuilder]:
      override def addBlock(self: FabricTagProvider[Item]#FabricTagBuilder, block: Block): FabricTagProvider[Item]#FabricTagBuilder =
        self.add(block.asItem())

    given acceptsBlockItemsBlock: AcceptsBlockItems[FabricTagProvider[Block]#FabricTagBuilder]:
      override def addBlock(self: FabricTagProvider[Block]#FabricTagBuilder, block: Block): FabricTagProvider[Block]#FabricTagBuilder =
        self.add(block)


    def addWoodSetTags[T](registry: ResourceKey[? <: Registry[T]], makeBuilder: TagKey[T] => FabricTagProvider[T]#FabricTagBuilder)(using AcceptsBlockItems[FabricTagProvider[T]#FabricTagBuilder]): Unit =
      makeBuilder(BlockTags.WOODEN_FENCES.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodFence)
        .addBlock(LumomancyBlocks.wiederFence)
        .addBlock(LumomancyBlocks.aftusFence)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_BUTTONS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodButton)
        .addBlock(LumomancyBlocks.wiederButton)
        .addBlock(LumomancyBlocks.aftusButton)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_PRESSURE_PLATES.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodPressurePlate)
        .addBlock(LumomancyBlocks.wiederPressurePlate)
        .addBlock(LumomancyBlocks.aftusPressurePlate)
        .setReplace(false)
      makeBuilder(BlockTags.FENCE_GATES.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodFenceGate)
        .addBlock(LumomancyBlocks.wiederFenceGate)
        .addBlock(LumomancyBlocks.aftusFenceGate)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_DOORS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodDoor)
        .addBlock(LumomancyBlocks.wiederDoor)
        .addBlock(LumomancyBlocks.aftusDoor)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_SLABS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodSlab)
        .addBlock(LumomancyBlocks.wiederSlab)
        .addBlock(LumomancyBlocks.aftusSlab)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_STAIRS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodStairs)
        .addBlock(LumomancyBlocks.wiederStairs)
        .addBlock(LumomancyBlocks.aftusStairs)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_TRAPDOORS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodTrapdoor)
        .addBlock(LumomancyBlocks.wiederTrapdoor)
        .addBlock(LumomancyBlocks.aftusTrapdoor)
        .setReplace(false)
      makeBuilder(BlockTags.LOGS_THAT_BURN.transmute(registry))
        .addTag(LumomancyTags.block.stillwoodLogsTag.transmute(registry))
        .addTag(LumomancyTags.block.wiederLogsTag.transmute(registry))
        .addTag(LumomancyTags.block.aftusLogsTag.transmute(registry))
        .setReplace(false)
      makeBuilder(BlockTags.PLANKS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodPlanks)
        .addBlock(LumomancyBlocks.wiederPlanks)
        .addBlock(LumomancyBlocks.aftusPlanks)
        .setReplace(false)
      makeBuilder(LumomancyTags.block.stillwoodLogsTag.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodLog)
        .addBlock(LumomancyBlocks.stillwoodWood)
        .addBlock(LumomancyBlocks.strippedStillwoodLog)
        .addBlock(LumomancyBlocks.strippedStillwoodWood)
        .setReplace(false)
      makeBuilder(LumomancyTags.block.wiederLogsTag.transmute(registry))
        .addBlock(LumomancyBlocks.wiederLog)
        .addBlock(LumomancyBlocks.wiederWood)
        .addBlock(LumomancyBlocks.strippedWiederLog)
        .addBlock(LumomancyBlocks.strippedWiederWood)
        .setReplace(false)
      makeBuilder(LumomancyTags.block.aftusLogsTag.transmute(registry))
        .addBlock(LumomancyBlocks.aftusLog)
        .addBlock(LumomancyBlocks.aftusWood)
        .addBlock(LumomancyBlocks.strippedAftusLog)
        .addBlock(LumomancyBlocks.strippedAftusWood)
        .setReplace(false)



  import tagHelper.given

  private class ItemTagGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends FabricTagProvider[Item](output, BuiltInRegistries.ITEM.key(), lookup):
    override def addTags(provider: HolderLookup.Provider): Unit =
      getOrCreateTagBuilder(LumomancyTags.item.validToolTag)
        .addOptionalTag(ConventionalItemTags.TOOLS)
        .addOptionalTag(ItemTags.HEAD_ARMOR)
        .addOptionalTag(ItemTags.CHEST_ARMOR)
        .addOptionalTag(ItemTags.LEG_ARMOR)
        .addOptionalTag(ItemTags.FOOT_ARMOR)
        .add(Items.SPYGLASS)
        .addOptionalTag(ItemTags.COMPASSES)
        .addOptionalTag(TagKey.create(registryKey, ResourceLocation.fromNamespaceAndPath("c", "wrenches")))
        .setReplace(false)
      tagHelper.addWoodSetTags(registryKey, this.getOrCreateTagBuilder)
      getOrCreateTagBuilder(ItemTags.SIGNS)
        .add(LumomancyBlocks.stillwoodSignItem)
        .add(LumomancyBlocks.wiederSignItem)
        .add(LumomancyBlocks.aftusSignItem)
        .setReplace(false)
      getOrCreateTagBuilder(ItemTags.HANGING_SIGNS)
        .add(LumomancyBlocks.stillwoodHangingSignItem)
        .add(LumomancyBlocks.wiederHangingSignItem)
        .add(LumomancyBlocks.aftusHangingSignItem)
        .setReplace(false)


  private class BlockTagGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends FabricTagProvider[Block](output, BuiltInRegistries
    .BLOCK.key(), lookup):
    override def addTags(provider: HolderLookup.Provider): Unit =
      tagHelper.addWoodSetTags(registryKey, this.getOrCreateTagBuilder)
      getOrCreateTagBuilder(BlockTags.WALL_SIGNS)
        .add(LumomancyBlocks.stillwoodWallSign)
        .add(LumomancyBlocks.wiederWallSign)
        .add(LumomancyBlocks.aftusWallSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.STANDING_SIGNS)
        .add(LumomancyBlocks.stillwoodSign)
        .add(LumomancyBlocks.wiederSign)
        .add(LumomancyBlocks.aftusSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS)
        .add(LumomancyBlocks.stillwoodHangingSign)
        .add(LumomancyBlocks.wiederHangingSign)
        .add(LumomancyBlocks.aftusHangingSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS)
        .add(LumomancyBlocks.stillwoodWallHangingSign)
        .add(LumomancyBlocks.wiederWallHangingSign)
        .add(LumomancyBlocks.aftusWallHangingSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
        // all other mineable with axe blocks are covered under other tags
        .add(LumomancyBlocks.stasisCooler)
        .setReplace(false)

