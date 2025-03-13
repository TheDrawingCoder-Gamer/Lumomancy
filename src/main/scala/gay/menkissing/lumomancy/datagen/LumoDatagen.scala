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
import gay.menkissing.lumomancy.content.block.{LumoBlockFamilies, StasisCooler}
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.{DataGeneratorEntrypoint, FabricDataGenerator, FabricDataOutput}
import net.minecraft.core.Direction
import net.minecraft.data.models.{BlockModelGenerators, ItemModelGenerators}
import net.minecraft.data.models.blockstates.{Condition, MultiPartGenerator, Variant, VariantProperties}
import net.minecraft.data.models.model.{ModelLocationUtils, ModelTemplate, ModelTemplates, TextureMapping, TextureSlot}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.{BlockStateProperties, EnumProperty}

import scala.collection.mutable

object LumoDatagen extends DataGeneratorEntrypoint:
  override def onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator): Unit =
    val pack = fabricDataGenerator.createPack()

    pack.addProvider(ModelGenerator.apply)

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


    def generateStillwood(blockModelGenerators: BlockModelGenerators): Unit =
      val family = LumoBlockFamilies.stillwoodPlanks
      blockModelGenerators.family(family.getBaseBlock).generateFor(family)
      blockModelGenerators.woodProvider(LumomancyBlocks.stillwoodLog).logWithHorizontal(LumomancyBlocks.stillwoodLog).wood(LumomancyBlocks.stillwoodWood)
      blockModelGenerators.woodProvider(LumomancyBlocks.strippedStillwoodLog).logWithHorizontal(LumomancyBlocks.strippedStillwoodLog).wood(LumomancyBlocks.strippedStillwoodWood)
      blockModelGenerators.createHangingSign(LumomancyBlocks.strippedStillwoodLog, LumomancyBlocks.stillwoodHangingSign, LumomancyBlocks.stillwoodWallHangingSign)

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
      itemModelGenerators.generateFlatItem(LumomancyItems.stasisBottle, ModelTemplates.FLAT_ITEM)

      // tool container
      itemModelGenerators.generateFlatItem(LumomancyItems.toolContainer, ModelTemplates.FLAT_ITEM)

      // stasis tube
      // is there a way to automate the entity part as well?
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(LumomancyItems.stasisTube, "_base"), TextureMapping.layer0(LumomancyItems.stasisTube), itemModelGenerators.output)



    override def generateBlockStateModels(blockModelGenerators: BlockModelGenerators): Unit =
      generateStasisCoolerModels(blockModelGenerators)
      generateStillwood(blockModelGenerators)
 


