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


