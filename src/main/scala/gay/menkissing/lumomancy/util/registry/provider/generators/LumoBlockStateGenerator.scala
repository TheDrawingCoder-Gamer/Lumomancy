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

package gay.menkissing.lumomancy.util.registry.provider.generators

import com.google.common.base.Supplier
import com.google.gson.{JsonElement, JsonObject}
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.Direction.Axis
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.{CachedOutput, DataProvider, PackOutput}
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.blockstates.{BlockStateGenerator, Condition, MultiPartGenerator, MultiVariantGenerator, PropertyDispatch, Variant, VariantProperties}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.{Block, FenceGateBlock, RotatedPillarBlock, TrapDoorBlock}
import gay.menkissing.lumomancy.util.resources.{*, given}
import net.minecraft.world.level.block.state.properties.{BlockStateProperties, Half}

import java.util.concurrent.CompletableFuture
import scala.collection.mutable

abstract class LumoBlockStateGenerator(val output: FabricDataOutput) extends DataProvider:
  val models = new LumoModelProvider(output)
  val itemModels = new LumoItemModelProvider(output)

  val blockStates: mutable.Map[Block, BlockStateGenerator] = mutable.LinkedHashMap()


  private def extend(rl: ResourceLocation, suffix: String): ResourceLocation =
    ResourceLocation.fromNamespaceAndPath(rl.getNamespace, rl.getPath + suffix)

  private def blockTexture(block: Block): ResourceLocation =
    val key = BuiltInRegistries.BLOCK.getKey(block)
    ResourceLocation.fromNamespaceAndPath(key.getNamespace, "block/" + key.getPath)

  def simpleBlock(block: Block, model: LumoModelFile): Unit =
    blockStates(block) = MultiVariantGenerator.multiVariant(block, Variant.variant().`with`(VariantProperties.MODEL, model.location))


  def simpleBlock(block: Block): Unit =
    simpleBlock(block, models.cubeAll(block.modelLoc, blockTexture(block)))

  def axisBlock(block: Block, vertical: LumoModelFile, horizontal: LumoModelFile): Unit =
    blockStates(block) = MultiVariantGenerator.multiVariant(block)
                                              .`with`(PropertyDispatch.property(RotatedPillarBlock.AXIS)
                                                                      .select(Axis.Y, Variant.variant().`with`(VariantProperties.MODEL, vertical.location))
                                              .select(Axis.Z, Variant.variant().`with`(VariantProperties.MODEL, horizontal.location)
                                              .`with`(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                                              .select(Axis.X, Variant.variant().`with`(VariantProperties.MODEL, horizontal.location)
                                              .`with`(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                              .`with`(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))

  def axisBlock(block: Block, side: ResourceLocation, ends: ResourceLocation): Unit =
    axisBlock(block,
      models.cubeColumn(block.modelLoc, side, ends),
      models.cubeColumnHorizontal(extend(block.modelLoc,  "_horizontal"), side, ends)
    )

  def logBlock(block: Block): Unit =
    axisBlock(block,
      blockTexture(block),
      extend(blockTexture(block), "_top"))

  def fenceGateBlock(block: Block, texture: ResourceLocation): Unit =
    val gate = models.fenceGate(block, texture)
    val gateOpen = models.fenceGateOpen(block, texture)
    val gateWall = models.fenceGateWall(block, texture)
    val gateWallOpen = models.fenceGateWallOpen(block, texture)
    fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen)

  def fenceGateBlock(block: Block, gate: LumoModelFile, gateOpen: LumoModelFile, gateWall: LumoModelFile, gateWallOpen: LumoModelFile): Unit =
    MultiVariantGenerator.multiVariant(block).`with` {
      PropertyDispatch.properties(FenceGateBlock.IN_WALL, FenceGateBlock.OPEN, BlockStateProperties.HORIZONTAL_FACING).generate { (inWall, isOpen, facing) =>
        val model =
          if inWall && isOpen then
            gateWallOpen
          else if inWall then
            gateWall
          else if isOpen then
            gateOpen
          else
            gate

        Variant.variant().`with`(VariantProperties.MODEL, model.location)
               .`with`(VariantProperties.UV_LOCK, true)
               .`with`(VariantProperties.Y_ROT, VariantProperties.Rotation.values()(facing.toYRot.toInt / 90))



      }
    }

  def trapdoorBlock(block: Block, orientable: Boolean = true): Unit =
    val tex = blockTexture(block)
    val top = if orientable then models.trapdoorOrientableTop(block.modelLoc.withSuffix("_top"), tex) else models.trapdoorTop(block.modelLoc.withSuffix("_top"), tex)
    val bottom = if orientable then models.trapdoorOrientableBottom(block.modelLoc.withSuffix("_bottom"), tex) else models.trapdoorTop(block.modelLoc.withSuffix("_bottom"), tex)
    val open = if orientable then models.trapdoorOrientableOpen(block.modelLoc.withSuffix("_open"), tex) else models.trapdoorOpen(block.modelLoc.withSuffix("_open"), tex)
    trapdoorBlock(block, bottom, top, open, orientable)


  def trapdoorBlock(block: Block, bottom: LumoModelFile, top: LumoModelFile, open: LumoModelFile, orientable: Boolean): Unit =
    MultiVariantGenerator.multiVariant(block).`with` {
      PropertyDispatch.properties(TrapDoorBlock.HALF, TrapDoorBlock.OPEN, BlockStateProperties.HORIZONTAL_FACING)
                      .generate { (half, isOpen, facing) =>
                        var xRot = 0
                        var yRot = facing.toYRot.toInt + 180
                        if orientable && isOpen && half == Half.TOP then
                          xRot += 180
                          yRot += 180

                        if !orientable && !isOpen then
                          yRot = 0

                        yRot %= 360

                        Variant.variant()
                               .`with`(VariantProperties.MODEL, if isOpen then open.location else if half == Half.TOP then top.location else bottom.location)
                               .`with`(VariantProperties.X_ROT, VariantProperties.Rotation.values()(xRot / 90))
                               .`with`(VariantProperties.Y_ROT, VariantProperties.Rotation.values()(yRot / 90))
                      }
    }


  private def saveBlockState(cache: CachedOutput, stateJson: JsonElement, owner: Block): CompletableFuture[?] =
    val blockName = owner.location
    val outputPath = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                         .resolve(blockName.getNamespace).resolve("blockstates").resolve(blockName.getPath + ".json")
    DataProvider.saveStable(cache, stateJson, outputPath)

  override def run(output: CachedOutput): CompletableFuture[?] =
    registerStates()
    val freakyOne = Seq(models.generateAll(output), itemModels.generateAll(output))
    val freakies = blockStates.map { (k, v) =>
      saveBlockState(output, v.get(), k)
    }.toSeq
    CompletableFuture.allOf((freakyOne ++ freakies)*)

  override def getName: String =
    "Lumomancy block state generator"

  def registerStates(): Unit