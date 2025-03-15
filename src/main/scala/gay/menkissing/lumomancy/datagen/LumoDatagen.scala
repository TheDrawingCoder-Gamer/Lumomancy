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
    pack.addProvider(LootTableGenerator.apply)
    pack.addProvider(GameplayLootTableGenerator.apply)
    pack.addProvider(EnglishLanguageGenerator.apply)
    pack.addProvider(ItemTagGenerator.apply)
    pack.addProvider(BlockTagGenerator.apply)



  private class LootTableGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends FabricBlockLootTableProvider(output, lookup):
    override def generate(): Unit =
      dropSelf(LumomancyBlocks.stasisCooler)

      // stillwood
      dropSelf(LumomancyBlocks.stillwoodLog)
      dropSelf(LumomancyBlocks.stillwoodWood)
      dropSelf(LumomancyBlocks.strippedStillwoodLog)
      dropSelf(LumomancyBlocks.strippedStillwoodWood)
      dropSelf(LumomancyBlocks.stillwoodPlanks)
      dropSelf(LumomancyBlocks.stillwoodSlab)
      dropSelf(LumomancyBlocks.stillwoodButton)
      dropSelf(LumomancyBlocks.stillwoodPressurePlate)
      dropSelf(LumomancyBlocks.stillwoodFence)
      dropSelf(LumomancyBlocks.stillwoodFenceGate)
      dropSelf(LumomancyBlocks.stillwoodStairs)
      // wall variants of signs drop like their normal variant
      dropOther(LumomancyBlocks.stillwoodSign, LumomancyBlocks.stillwoodSignItem)
      dropOther(LumomancyBlocks.stillwoodHangingSign, LumomancyBlocks.stillwoodHangingSignItem)

      dropSelf(LumomancyBlocks.stillwoodDoor)
      dropSelf(LumomancyBlocks.stillwoodTrapdoor)

      // wieder wood

      dropSelf(LumomancyBlocks.wiederLog)
      dropSelf(LumomancyBlocks.wiederWood)
      dropSelf(LumomancyBlocks.strippedWiederLog)
      dropSelf(LumomancyBlocks.strippedWiederWood)
      dropSelf(LumomancyBlocks.wiederPlanks)
      dropSelf(LumomancyBlocks.wiederSlab)
      dropSelf(LumomancyBlocks.wiederButton)
      dropSelf(LumomancyBlocks.wiederPressurePlate)
      dropSelf(LumomancyBlocks.wiederFence)
      dropSelf(LumomancyBlocks.wiederFenceGate)
      dropSelf(LumomancyBlocks.wiederStairs)
      dropOther(LumomancyBlocks.wiederSign, LumomancyBlocks.wiederSignItem)
      dropOther(LumomancyBlocks.wiederHangingSign, LumomancyBlocks.wiederHangingSignItem)

  class GameplayLootTableGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends SimpleFabricLootTableProvider(output, lookup, LootContextParamSets.BLOCK):
    import net.minecraft.world.level.storage.loot.*
    import entries.LootItem
    override def generate(output: BiConsumer[ResourceKey[LootTable], LootTable.Builder]): Unit =
      output.accept(LumomancyLootTables.stripStillwood,
        LootTable.lootTable()
         .pool(
           LootPool.lootPool()
                   .setRolls(ConstantValue.exactly(1.0f))
                   .`with`(LootItem.lootTableItem(LumomancyItems.stillwoodBark).build())
                   .build()
         ))
      output.accept(LumomancyLootTables.stripWieder,
        LootTable.lootTable()
        .pool(
          LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0f))
                  .`with`(LootItem.lootTableItem(LumomancyItems.wiederBark).build())
                  .build()
        ))


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
        .createHangingSign(LumomancyBlocks.strippedWiederWood, LumomancyBlocks.wiederHangingSign, LumomancyBlocks
          .wiederWallHangingSign)

    override def generateItemModels(itemModelGenerators: ItemModelGenerators): Unit =
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

      // stasis bottle
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(LumomancyItems.stasisBottle, "_base"), TextureMapping.layer0(LumomancyItems.stasisBottle), itemModelGenerators.output)

      // tool container
      itemModelGenerators.generateFlatItem(LumomancyItems.toolContainer, ModelTemplates.FLAT_ITEM)

      itemModelGenerators.generateFlatItem(LumomancyItems.stillwoodBark, ModelTemplates.FLAT_ITEM)
      itemModelGenerators.generateFlatItem(LumomancyItems.wiederBark, ModelTemplates.FLAT_ITEM)

      // stasis tube
      // is there a way to automate the entity part as well?
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(LumomancyItems.stasisTube, "_base"), TextureMapping.layer0(LumomancyItems.stasisTube), itemModelGenerators.output)



    override def generateBlockStateModels(blockModelGenerators: BlockModelGenerators): Unit =
      generateStasisCoolerModels(blockModelGenerators)
      generateStillwood(blockModelGenerators)
      generateWieder(blockModelGenerators)
 



  private class EnglishLanguageGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends FabricLanguageProvider(output, "en_us", lookup):
    override def generateTranslations(provider: HolderLookup.Provider, translationBuilder: FabricLanguageProvider.TranslationBuilder): Unit =
      translationBuilder.add(LumomancyItems.clearQuartz, "Clear Quartz")
      translationBuilder.add(LumomancyItems.bloodTopazShard, "Blood Topaz Shard")
      translationBuilder.add(LumomancyItems.prasioliteShard, "Prasiolite Shard")
      translationBuilder.add(LumomancyItems.adventurineShard, "Adventurine Shard")
      translationBuilder.add(LumomancyItems.toolContainer, "Tool Container")
      translationBuilder.add(LumomancyItems.bottleOfLight, "Empty Bottle of Light")
      translationBuilder.add(LumomancyItems.azureBottleOfLight, "Azure Bottle of Light")
      translationBuilder.add(LumomancyItems.blackBottleOfLight, "Black Bottle of Light")
      translationBuilder.add(LumomancyItems.blueBottleOfLight, "Blue Bottle of Light")
      translationBuilder.add(LumomancyItems.brownBottleOfLight, "Brown Bottle of Light")
      translationBuilder.add(LumomancyItems.cyanBottleOfLight, "Cyan Bottle of Light")
      translationBuilder.add(LumomancyItems.grayBottleOfLight, "Gray Bottle of Light")
      translationBuilder.add(LumomancyItems.greenBottleOfLight, "Green Bottle of Light")
      translationBuilder.add(LumomancyItems.lightGrayBottleOfLight, "Light Gray Bottle of Light")
      translationBuilder.add(LumomancyItems.limeBottleOfLight, "Lime Bottle of Light")
      translationBuilder.add(LumomancyItems.magentaBottleOfLight, "Magenta Bottle of Light")
      translationBuilder.add(LumomancyItems.orangeBottleOfLight, "Orange Bottle of Light")
      translationBuilder.add(LumomancyItems.purpleBottleOfLight, "Purple Bottle of Light")
      translationBuilder.add(LumomancyItems.redBottleOfLight, "Red Bottle of Light")
      translationBuilder.add(LumomancyItems.roseBottleOfLight, "Rose Bottle of Light")
      translationBuilder.add(LumomancyItems.seafoamBottleOfLight, "Seafoam Bottle of Light")
      translationBuilder.add(LumomancyItems.whiteBottleOfLight, "White Bottle of Light")
      translationBuilder.add(LumomancyItems.yellowBottleOfLight, "Yellow Bottle of Light")

      translationBuilder.add(LumomancyItems.stasisTube, "Stasis Tube")
      translationBuilder.add(LumomancyTranslationKeys.keys.stasisTube.tooltip.empty, "Empty")
      translationBuilder.add(LumomancyTranslationKeys.keys.stasisTube.tooltip.count, "%1$d / %2$d (%3$d stacks)")

      translationBuilder.add(LumomancyItems.stasisBottle, "Stasis Bottle")
      translationBuilder.add(LumomancyTranslationKeys.keys.stasisBottle.tooltip.empty, "Empty")
      translationBuilder.add(LumomancyTranslationKeys.keys.stasisBottle.tooltip.usagePickup, "Use to pickup")
      translationBuilder.add(LumomancyTranslationKeys.keys.stasisBottle.tooltip.usagePlace, "Sneak-use to place")
      translationBuilder.add(LumomancyTranslationKeys.keys.stasisBottle.tooltip.countMB, "%1$s mB / %2$s buckets")

      translationBuilder.add(LumomancyItems.lumonLens, "Lumon Lens")

      // blocks
      translationBuilder.add(LumomancyBlocks.stasisCooler, "Stasis Cooler")
      // stillwood
      translationBuilder.add(LumomancyBlocks.stillwoodLog, "Stillwood Log")
      translationBuilder.add(LumomancyBlocks.stillwoodWood, "Stillwood Wood")
      translationBuilder.add(LumomancyBlocks.strippedStillwoodLog, "Stripped Stillwood Log")
      translationBuilder.add(LumomancyBlocks.strippedStillwoodWood, "Stripped Stillwood Wood")

      translationBuilder.add(LumomancyBlocks.stillwoodPlanks, "Stillwood Planks")
      translationBuilder.add(LumomancyBlocks.stillwoodSlab, "Stillwood Slab")
      translationBuilder.add(LumomancyBlocks.stillwoodButton, "Stillwood Button")
      translationBuilder.add(LumomancyBlocks.stillwoodPressurePlate, "Stillwood Pressure Plate")
      translationBuilder.add(LumomancyBlocks.stillwoodFence, "Stillwood Fence")
      translationBuilder.add(LumomancyBlocks.stillwoodFenceGate, "Stillwood Fence Gate")
      translationBuilder.add(LumomancyBlocks.stillwoodStairs, "Stillwood Stairs")
      translationBuilder.add(LumomancyBlocks.stillwoodSignItem, "Stillwood Sign")
      translationBuilder.add(LumomancyBlocks.stillwoodHangingSignItem, "Stillwood Hanging Sign")
      translationBuilder.add(LumomancyBlocks.stillwoodDoor, "Stillwood Door")
      translationBuilder.add(LumomancyBlocks.stillwoodTrapdoor, "Stillwood Trapdoor")

      translationBuilder.add(LumomancyItems.stillwoodBark, "Stillwood Bark")

      // item group
      translationBuilder.add(LumomancyItems.itemGroupKey, "Lumomancy")
      // wieder wood
      translationBuilder.add(LumomancyBlocks.wiederLog, "Wieder Log")
      translationBuilder.add(LumomancyBlocks.wiederWood, "Wieder Wood")
      translationBuilder.add(LumomancyBlocks.strippedWiederLog, "Stripped Wieder Log")
      translationBuilder.add(LumomancyBlocks.strippedWiederWood, "Stripped Wieder Wood")

      translationBuilder.add(LumomancyBlocks.wiederPlanks, "Wieder Planks")
      translationBuilder.add(LumomancyBlocks.wiederSlab, "Wieder Slab")
      translationBuilder.add(LumomancyBlocks.wiederButton, "Wieder Button")
      translationBuilder.add(LumomancyBlocks.wiederPressurePlate, "Wieder Pressure Plate")
      translationBuilder.add(LumomancyBlocks.wiederFence, "Wieder Fence")
      translationBuilder.add(LumomancyBlocks.wiederFenceGate, "Wieder Fence Gate")
      translationBuilder.add(LumomancyBlocks.wiederStairs, "Wieder Stairs")
      translationBuilder.add(LumomancyBlocks.wiederSignItem, "Wieder Sign")
      translationBuilder.add(LumomancyBlocks.wiederHangingSignItem, "Wieder Hanging Sign")
      translationBuilder.add(LumomancyBlocks.wiederDoor, "Wieder Door")
      translationBuilder.add(LumomancyBlocks.wiederTrapdoor, "Wieder Trapdoor")

      translationBuilder.add(LumomancyItems.wiederBark, "Wieder Bark")

      // tags
      translationBuilder.add(LumomancyTags.item.validToolTag, "Tools that go in Tool Containers")
      translationBuilder.add(LumomancyTags.item.stillwoodLogsTag, "Stillwood Logs")
      translationBuilder.add(LumomancyTags.block.stillwoodLogsTag, "Stillwood Logs")
      translationBuilder.add(LumomancyTags.item.wiederLogsTag, "Wieder Logs")
      translationBuilder.add(LumomancyTags.block.wiederLogsTag, "Wieder Logs")

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
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_BUTTONS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodButton)
        .addBlock(LumomancyBlocks.wiederButton)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_PRESSURE_PLATES.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodPressurePlate)
        .addBlock(LumomancyBlocks.wiederPressurePlate)
        .setReplace(false)
      makeBuilder(BlockTags.FENCE_GATES.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodFenceGate)
        .addBlock(LumomancyBlocks.wiederFenceGate)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_DOORS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodDoor)
        .addBlock(LumomancyBlocks.wiederDoor)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_SLABS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodSlab)
        .addBlock(LumomancyBlocks.wiederSlab)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_STAIRS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodStairs)
        .addBlock(LumomancyBlocks.wiederStairs)
        .setReplace(false)
      makeBuilder(BlockTags.WOODEN_TRAPDOORS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodTrapdoor)
        .addBlock(LumomancyBlocks.wiederTrapdoor)
        .setReplace(false)
      makeBuilder(BlockTags.LOGS_THAT_BURN.transmute(registry))
        .addTag(LumomancyTags.block.stillwoodLogsTag.transmute(registry))
        .addTag(LumomancyTags.block.wiederLogsTag.transmute(registry))
        .setReplace(false)
      makeBuilder(BlockTags.PLANKS.transmute(registry))
        .addBlock(LumomancyBlocks.stillwoodPlanks)
        .addBlock(LumomancyBlocks.wiederPlanks)
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
        .setReplace(false)
      getOrCreateTagBuilder(ItemTags.HANGING_SIGNS)
        .add(LumomancyBlocks.stillwoodHangingSignItem)
        .add(LumomancyBlocks.wiederHangingSignItem)
        .setReplace(false)


  private class BlockTagGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends FabricTagProvider[Block](output, BuiltInRegistries
    .BLOCK.key(), lookup):
    override def addTags(provider: HolderLookup.Provider): Unit =
      tagHelper.addWoodSetTags(registryKey, this.getOrCreateTagBuilder)
      getOrCreateTagBuilder(BlockTags.WALL_SIGNS)
        .add(LumomancyBlocks.stillwoodWallSign)
        .add(LumomancyBlocks.wiederWallSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.STANDING_SIGNS)
        .add(LumomancyBlocks.stillwoodSign)
        .add(LumomancyBlocks.wiederSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS)
        .add(LumomancyBlocks.stillwoodHangingSign)
        .add(LumomancyBlocks.wiederHangingSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS)
        .add(LumomancyBlocks.stillwoodWallHangingSign)
        .add(LumomancyBlocks.wiederWallHangingSign)
        .setReplace(false)
      getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
        // all other mineable with axe blocks are covered under other tags
        .add(LumomancyBlocks.stasisCooler)
        .setReplace(false)

