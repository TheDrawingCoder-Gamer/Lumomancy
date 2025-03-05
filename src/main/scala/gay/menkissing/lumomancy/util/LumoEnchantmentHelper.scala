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

package gay.menkissing.lumomancy.util

import net.minecraft.core.registries.Registries
import net.minecraft.core.{HolderLookup, RegistryAccess}
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.{Enchantment, EnchantmentHelper}

object LumoEnchantmentHelper:
  def getLevel(lookup: HolderLookup.Provider, key: ResourceKey[Enchantment], stack: ItemStack): Int =
    lookup.lookup(Registries.ENCHANTMENT)
          .flatMap(_.get(key))
          .map(entry => EnchantmentHelper.getItemEnchantmentLevel(entry, stack))
          .orElse(0)


