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

package gay.menkissing.lumomancy.content.item

import gay.menkissing.lumomancy.api.block.StrippableDrop
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.{Block, RotatedPillarBlock}
import net.minecraft.world.level.block.state.{BlockBehaviour, BlockState}
import net.minecraft.world.level.storage.loot.LootTable

class StrippablePillarBlock(val strippedBlock: Block,
                            val strippingLootTableKey: ResourceKey[LootTable],
                            props: BlockBehaviour.Properties) extends RotatedPillarBlock(props), StrippableDrop:
  override def onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean): Unit = {
    checkAndDropStrippedLoot(state, level, pos, newState, movedByPiston)
    super.onRemove(state, level, pos, newState, movedByPiston)
  }