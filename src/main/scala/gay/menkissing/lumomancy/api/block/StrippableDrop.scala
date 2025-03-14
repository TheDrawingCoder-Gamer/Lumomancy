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

package gay.menkissing.lumomancy.api.block

import gay.menkissing.lumomancy.api.block.StrippableDrop.getStrippedStacks
import net.minecraft.core.BlockPos
import net.minecraft.resources.{ResourceKey, ResourceLocation}
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.{LootParams, LootTable}
import net.minecraft.world.level.storage.loot.parameters.{LootContextParamSet, LootContextParamSets, LootContextParams}
import net.minecraft.world.phys.Vec3

// Reimplementation of spectrums thing
trait StrippableDrop:

  def strippedBlock: Block

  def strippingLootTableKey: ResourceKey[LootTable]

  def checkAndDropStrippedLoot(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, moved: Boolean): Boolean =
    if !moved && newState.is(strippedBlock) then
      val harvestedStacks = getStrippedStacks(state, level.asInstanceOf[ServerLevel], pos, level.getBlockEntity(pos), null, ItemStack.EMPTY, strippingLootTableKey)
      harvestedStacks.forEach { stack =>
        Containers.dropItemStack(level, pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, stack)
      }
      true
    else
      false

object StrippableDrop:

  def getStrippedStacks(state: BlockState, world: ServerLevel, pos: BlockPos, blockEntity: BlockEntity, entity: LivingEntity, stack: ItemStack, lootTableLocation: ResourceKey[LootTable]): java.util.List[ItemStack] =
    val builder = LootParams.Builder(world)
                            .withParameter(LootContextParams.BLOCK_STATE, state)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withParameter(LootContextParams.TOOL, stack)
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)

    val lootTable = world.getServer.reloadableRegistries().getLootTable(lootTableLocation)

    lootTable.getRandomItems(builder.create(LootContextParamSets.BLOCK))



