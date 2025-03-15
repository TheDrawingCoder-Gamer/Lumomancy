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

package gay.menkissing.lumomancy.registries

import gay.menkissing.lumomancy.content.LumomancyBlocks
import net.minecraft.data.{BlockFamilies, BlockFamily}

object LumoBlockFamilies:
  val stillwoodPlanks: BlockFamily = BlockFamilies.familyBuilder(LumomancyBlocks.stillwoodPlanks)
                                                  .button(LumomancyBlocks.stillwoodButton)
                                                  .pressurePlate(LumomancyBlocks.stillwoodPressurePlate)
                                                  .fence(LumomancyBlocks.stillwoodFence)
                                                  .fenceGate(LumomancyBlocks.stillwoodFenceGate)
                                                  .slab(LumomancyBlocks.stillwoodSlab)
                                                  .stairs(LumomancyBlocks.stillwoodStairs)
                                                  .sign(LumomancyBlocks.stillwoodSign, LumomancyBlocks.stillwoodWallSign)
                                                  .door(LumomancyBlocks.stillwoodDoor)
                                                  .trapdoor(LumomancyBlocks.stillwoodTrapdoor)
                                                  .recipeGroupPrefix("wooden")
                                                  .recipeUnlockedBy("has_planks")
                                                  .getFamily

  val wiederPlanks: BlockFamily = BlockFamilies.familyBuilder(LumomancyBlocks.wiederPlanks)
                                               .button(LumomancyBlocks.wiederButton)
                                               .pressurePlate(LumomancyBlocks.wiederPressurePlate)
                                               .fence(LumomancyBlocks.wiederFence)
                                               .fenceGate(LumomancyBlocks.wiederFenceGate)
                                               .slab(LumomancyBlocks.wiederSlab)
                                               .stairs(LumomancyBlocks.wiederStairs)
                                               .sign(LumomancyBlocks.wiederSign, LumomancyBlocks.wiederWallSign)
                                               .door(LumomancyBlocks.wiederDoor)
                                               .trapdoor(LumomancyBlocks.wiederTrapdoor)
                                               .recipeGroupPrefix("wooden")
                                               .recipeUnlockedBy("has_planks")
                                               .getFamily

  val aftusPlanks: BlockFamily = BlockFamilies.familyBuilder(LumomancyBlocks.aftusPlanks)
                                               .button(LumomancyBlocks.aftusButton)
                                               .pressurePlate(LumomancyBlocks.aftusPressurePlate)
                                               .fence(LumomancyBlocks.aftusFence)
                                               .fenceGate(LumomancyBlocks.aftusFenceGate)
                                               .slab(LumomancyBlocks.aftusSlab)
                                               .stairs(LumomancyBlocks.aftusStairs)
                                               .sign(LumomancyBlocks.aftusSign, LumomancyBlocks.aftusWallSign)
                                               .door(LumomancyBlocks.aftusDoor)
                                               .trapdoor(LumomancyBlocks.aftusTrapdoor)
                                               .recipeGroupPrefix("wooden")
                                               .recipeUnlockedBy("has_planks")
                                               .getFamily
  
  // is this needed?
  def register(): Unit = ()
