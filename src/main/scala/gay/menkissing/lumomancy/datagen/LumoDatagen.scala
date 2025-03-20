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

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.{LumomancyBlocks, LumomancyItems}
import gay.menkissing.lumomancy.content.block.StasisCooler
import gay.menkissing.lumomancy.registries.{LumoBlockFamilies, LumomancyLootTables, LumomancyTags, LumomancyTranslationKeys}
import gay.menkissing.lumomancy.util.registry.InfoCollector
import net.fabricmc.fabric.api.datagen.v1.loot.FabricBlockLootTableGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.{FabricBlockLootTableProvider, FabricLanguageProvider, FabricModelProvider, FabricTagProvider, SimpleFabricLootTableProvider}
import net.fabricmc.fabric.api.datagen.v1.{DataGeneratorEntrypoint, FabricDataGenerator, FabricDataOutput}
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.registries.{BuiltInRegistries, Registries}
import net.minecraft.core.{Direction, HolderLookup, Registry}
import net.minecraft.data.PackOutput.Target
import net.minecraft.data.{CachedOutput, DataProvider}
import net.minecraft.data.models.{BlockModelGenerators, ItemModelGenerators}
import net.minecraft.data.models.blockstates.{Condition, MultiPartGenerator, Variant, VariantProperties}
import net.minecraft.data.models.model.{ModelLocationUtils, ModelTemplate, ModelTemplates, TextureMapping, TextureSlot}
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.tags.{BlockTags, ItemTags, TagKey}
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.item.{Item, Items}
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.{BlockStateProperties, EnumProperty}
import net.minecraft.world.level.levelgen.feature.configurations.{FeatureConfiguration, TreeConfiguration}
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.trunkplacers.{StraightTrunkPlacer, TrunkPlacer}
import net.minecraft.world.level.levelgen.feature.{ConfiguredFeature, TreeFeature}
import net.minecraft.world.level.storage.loot.{LootPool, LootTable}
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.levelgen.feature.Feature

import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer
import scala.collection.mutable
import cats.syntax.arrow.*

object LumoDatagen extends DataGeneratorEntrypoint:
  override def onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator): Unit =
    val pack = fabricDataGenerator.createPack()

    pack.addProvider(ModelGenerator.apply)
    // pack.addProvider(LootTableGenerator.apply)
    pack.addProvider(GameplayLootTableGenerator.apply)
    // pack.addProvider(EnglishLanguageGenerator.apply)
    InfoCollector.instance.registerDataGenerators(pack)
    //pack.addProvider(ItemTagGenerator.apply)
    //pack.addProvider(BlockTagGenerator.apply)
    pack.addProvider(FeatureGenerator.apply)



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
 
  private class FeatureGenerator(val output: FabricDataOutput, val lookup: CompletableFuture[HolderLookup.Provider]) extends DataProvider:
    override def run(cache: CachedOutput): CompletableFuture[?] =
      features.clear()
      lookup.thenCompose { lookup =>
        registerFeatures(lookup)
        CompletableFuture.allOf(
          features.map { (key, value) =>
            saveFeature(cache, lookup, key.location(), value)
          }.toSeq*
        )
      }

    override def getName: String = "Lumo Feature Generator"

    val features: mutable.HashMap[ResourceKey[ConfiguredFeature[?, ?]], ConfiguredFeature[?, ?]] = mutable.HashMap()

    def saveFeature(cache: CachedOutput, lookup: HolderLookup.Provider, loc: ResourceLocation, configuredFeature: ConfiguredFeature[?, ?]): CompletableFuture[?] =
      val outputPath = output.getOutputFolder(Target.DATA_PACK).resolve(loc.getNamespace).resolve("worldgen/configured_feature").resolve(loc.getPath + ".json")
      DataProvider.saveStable(cache, lookup, ConfiguredFeature.DIRECT_CODEC, configuredFeature, outputPath)


    def key(loc: ResourceLocation): ResourceKey[ConfiguredFeature[?, ?]] =
      ResourceKey.create(Registries.CONFIGURED_FEATURE, loc)

    def straightTree(log: Block, leaves: Block, baseHeight: Int, heightRandA: Int, heightRandB: Int, radius: Int): TreeConfiguration.TreeConfigurationBuilder =
      new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(log),
        StraightTrunkPlacer(baseHeight, heightRandA, heightRandB),
        BlockStateProvider.simple(leaves),
        BlobFoliagePlacer(ConstantInt.of(radius), ConstantInt.of(0), 3),
        TwoLayersFeatureSize(1, 0, 1))


    def feature[FC <: FeatureConfiguration, F <: Feature[FC]](loc: ResourceLocation, feature: F, config: FC): Unit =
      features(key(loc)) = ConfiguredFeature[FC, F](feature, config)

    def registerFeatures(lookup: HolderLookup.Provider): Unit =
     feature(Lumomancy.locate("aftus_tree"), Feature.TREE,
       straightTree(LumomancyBlocks.aftusLog, LumomancyBlocks.aftusLeaves, 4, 2, 0, 2).ignoreVines().build())

     feature(Lumomancy.locate("stillwood_tree"), Feature.TREE,
       straightTree(LumomancyBlocks.stillwoodLog, LumomancyBlocks.stillwoodLeaves, 4, 2, 0, 2).ignoreVines().build())

     feature(Lumomancy.locate("wieder_tree"), Feature.TREE,
       straightTree(LumomancyBlocks.wiederLog, LumomancyBlocks.wiederLeaves, 4, 2, 0, 2).ignoreVines().build())
