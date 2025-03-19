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




    override def generateItemModels(itemModelGenerators: ItemModelGenerators): Unit =

      // stasis bottle
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(LumomancyItems.stasisBottle, "_base"), TextureMapping.layer0(LumomancyItems.stasisBottle), itemModelGenerators.output)

      // stasis tube
      // is there a way to automate the entity part as well?
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(LumomancyItems.stasisTube, "_base"), TextureMapping.layer0(LumomancyItems.stasisTube), itemModelGenerators.output)



    override def generateBlockStateModels(blockModelGenerators: BlockModelGenerators): Unit =
      generateStasisCoolerModels(blockModelGenerators)
 

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
      makeBuilder(BlockTags.LOGS_THAT_BURN.transmute(registry))
        .addOptionalTag(LumomancyTags.block.stillwoodLogsTag.transmute(registry))
        .addOptionalTag(LumomancyTags.block.wiederLogsTag.transmute(registry))
        .addOptionalTag(LumomancyTags.block.aftusLogsTag.transmute(registry))
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

  private class BlockTagGenerator(output: FabricDataOutput, lookup: CompletableFuture[HolderLookup.Provider]) extends FabricTagProvider[Block](output, BuiltInRegistries
    .BLOCK.key(), lookup):
    override def addTags(provider: HolderLookup.Provider): Unit =
      tagHelper.addWoodSetTags(registryKey, this.getOrCreateTagBuilder)

