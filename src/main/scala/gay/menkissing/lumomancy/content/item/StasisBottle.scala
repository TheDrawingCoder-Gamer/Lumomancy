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

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.menkissing.lumomancy.content.LumomancyItems
import gay.menkissing.lumomancy.registries.LumomancyDataComponents
import gay.menkissing.lumomancy.util.LumoEnchantmentHelper
import net.fabricmc.fabric.api.item.v1.EnchantingContext
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.{FluidConstants, FluidVariant}
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.{ByteBufCodecs, StreamCodec}
import net.minecraft.world.item.enchantment.{Enchantment, Enchantments}
import net.minecraft.world.item.{Item, ItemStack}
import net.minecraft.world.level.Level

import gay.menkissing.lumomancy.util.codec.LumoCodecs.LongExtensions.*

// A container item 4 fluids : 3
class StasisBottle(props: Item.Properties) extends Item(props):
  override def isEnchantable(stack: ItemStack): Boolean = true

  override def getEnchantmentValue: Int = 5

  override def canBeEnchantedWith(stack: ItemStack, enchantment: Holder[Enchantment], context: EnchantingContext): Boolean = {
    super
      .canBeEnchantedWith(stack, enchantment, context) || enchantment.is(Enchantments.POWER)
  }


object StasisBottle:
  val baseMax: Long = FluidConstants.BUCKET * 128

  def maxAllowed(level: Int): Long =
    baseMax * math.pow(2, math.min(level, 5)).toLong

  def getMaxStackExpensive(stack: ItemStack): Long =
    maxAllowed(LumoEnchantmentHelper.getLevelExpensive(Enchantments.POWER, stack))

  case class StasisBottleContents(variant: FluidVariant, amount: Long):
    def isEmpty: Boolean = variant.isBlank || amount == 0

  object StasisBottleContents:
    val EMPTY: StasisBottleContents =
      StasisBottleContents(FluidVariant.blank(), 0)

    val CODEC: Codec[StasisBottleContents] = RecordCodecBuilder.create[StasisBottleContents] { instance =>
      instance.group(
        FluidVariant.CODEC.fieldOf("variant").forGetter((i: StasisBottleContents) => i.variant),
        Codec.LONG.asScala.fieldOf("amount").forGetter((i: StasisBottleContents) => i.amount)
      ).apply(instance, StasisBottleContents.apply)
    }

    val STREAM_CODEC: StreamCodec[RegistryFriendlyByteBuf, StasisBottleContents] = StreamCodec.composite(
      FluidVariant.PACKET_CODEC, _.variant,
      ByteBufCodecs.VAR_LONG, _.amount,
      StasisBottleContents.apply
    )

    /*
    class StasisBottleStorage(val context: ContainerItemContext) extends SingleSlotStorage[FluidVariant]:
      override def getCapacity: Long =
        if !context.getItemVariant.isOf(LumomancyItems.stasisBottle) then
          0
        else
          getMaxStackExpensive(context.getItemVariant.toStack)

      override def extract(resource: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long =
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        
        if !context.getItemVariant.isOf(LumomancyItems.stasisBottle) then
          return 0
        
        val builder = Builder.expensively(context.getItemVariant.toStack)
        
        if !builder.isEmpty && resource == builder.template then
          */





    class Builder(var template: FluidVariant, var amount: Long, val max: Long):
      def isEmpty: Boolean =
        template.isBlank || amount == 0
      
      def copied: Builder =
        Builder(template, amount, max)

      def build: StasisBottleContents =
        StasisBottleContents(template, amount)
        
      def getMaxAllowed(variant: FluidVariant, amount: Long): Long =
        if variant.isBlank || amount <= 0 then
          0
        else
          this.max - this.amount


    object Builder:
      def ofWorld(level: Level, stack: ItemStack): StasisBottleContents.Builder =
        val prev = stack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY)
        val max = StasisBottle.maxAllowed(LumoEnchantmentHelper.getLevel(level.registryAccess(), Enchantments.POWER, stack))
        StasisBottleContents.Builder(prev.variant, prev.amount, max)

      def expensively(stack: ItemStack): StasisBottleContents.Builder =
        val prev = stack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY)
        val max = StasisBottle.getMaxStackExpensive(stack)
        StasisBottleContents.Builder(prev.variant, prev.amount, max)
