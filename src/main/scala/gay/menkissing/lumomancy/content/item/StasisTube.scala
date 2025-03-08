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

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.item.StasisTube.StasisTubeContents
import gay.menkissing.lumomancy.mixin.RenderSystemAccessor
import gay.menkissing.lumomancy.registries.{LumomancyDataComponents, LumomancyTranslationKeys}
import gay.menkissing.lumomancy.util.LumoEnchantmentHelper
import gay.menkissing.lumomancy.util.codec.LumoCodecs
import net.fabricmc.api.{EnvType, Environment}
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.item.v1.EnchantingContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{MultiBufferSource, RenderType}
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.{Holder, HolderLookup}
import net.minecraft.core.component.{DataComponentPatch, DataComponents}
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.{ByteBufCodecs, StreamCodec}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.{ClickAction, Slot}
import net.minecraft.world.item.enchantment.{Enchantment, Enchantments, ItemEnchantments}
import net.minecraft.world.item.{Item, ItemDisplayContext, ItemStack, Items, TooltipFlag}
import net.minecraft.world.level.Level

import java.util
import scala.util.Using

class StasisTube(props: Item.Properties) extends Item(props):
  override def overrideStackedOnOther(thisStack: ItemStack, slot: Slot, clickAction: ClickAction, player: Player): Boolean = {
    if clickAction == ClickAction.SECONDARY then
      val thatStack = slot.getItem
      val builder = StasisTube.StasisTubeContents.Builder.ofWorld(player.level(), thisStack)
      if thatStack.isEmpty then
        val stack = builder.removeStack()
        if !stack.isEmpty then
          val remainder = slot.safeInsert(stack)
          builder.insertStack(remainder)

      else
        // not needed?
        if thatStack.getItem.canFitInsideContainerItems then
          builder.addFromSlot(slot, player)

      thisStack.set(LumomancyDataComponents.stasisTubeContents, builder.build)
      true
    else
      false
  }

  override def overrideOtherStackedOnMe(thisStack: ItemStack, thatStack: ItemStack, slot: Slot, clickAction: ClickAction, player: Player, slotAccess: SlotAccess): Boolean = {

    if clickAction == ClickAction.SECONDARY && slot.allowModification(player) then
      val builder = StasisTubeContents.Builder.ofWorld(player.level(), thisStack)
      if thatStack.isEmpty then
        if !builder.isEmpty then
          val removed = builder.removeStack()
          if !removed.isEmpty then
            slotAccess.set(removed)
      else
        builder.insertStack(thatStack)

      thisStack.set(LumomancyDataComponents.stasisTubeContents, builder.build)
      true
    else
      false
  }

  override def appendHoverText(stack: ItemStack, ctx: Item.TooltipContext, tooltip: util.List[Component], tooltipFlag: TooltipFlag): Unit = {
    val contents = StasisTube.getContents(stack)
    if contents.isEmpty then
      tooltip.add(LumomancyTranslationKeys.stasisTube.tooltip.empty)
    else
      val containedStack = contents.variant.toStack
      val totalStacks = math.floorDiv(contents.count, containedStack.getMaxStackSize).toString
      val maxCount = StasisTube.maxAmountWithLookup(ctx.registries(), stack)
      tooltip.add(LumomancyTranslationKeys.stasisTube.tooltip.count(contents.count, maxCount, totalStacks))
      tooltip.add(containedStack.getHoverName)
  }

  override def isEnchantable(stack: ItemStack): Boolean = stack.getCount == 1

  override def getEnchantmentValue: Int = 5

  override def canBeEnchantedWith(stack: ItemStack, enchantment: Holder[Enchantment], context: EnchantingContext): Boolean = {
    super
      .canBeEnchantedWith(stack, enchantment, context) || enchantment.is(Enchantments.POWER)
  }

object StasisTube:
  def getContents(stack: ItemStack): StasisTubeContents =
    stack.getOrDefault(LumomancyDataComponents.stasisTubeContents, StasisTubeContents.EMPTY)

  def getStoredAmount(stack: ItemStack): Long =
    getContents(stack).count

  def getMaxAmount(level: Int): Long =
    20000L * math.pow(10, math.min(5, level)).toInt

  def maxAmountExpensive(stack: ItemStack): Long =
    getMaxAmount(LumoEnchantmentHelper.getLevelExpensive(Enchantments.POWER, stack))

  def maxAmountWithLookup(lookup: HolderLookup.Provider, stack: ItemStack): Long =
    getMaxAmount(LumoEnchantmentHelper.getLevel(lookup, Enchantments.POWER, stack))

  case class StasisTubeContents(variant: ItemVariant, count: Long):
    def isEmpty: Boolean = variant.isBlank || count == 0

    def patched(stack: ItemStack): Unit =
      stack.set(LumomancyDataComponents.stasisTubeContents, this)

  object StasisTubeContents:
    val CODEC: Codec[StasisTubeContents] = RecordCodecBuilder.create { instance =>
      instance.group(
        ItemVariant.CODEC.fieldOf("variant").forGetter((it: StasisTubeContents) => it.variant),
        LumoCodecs.scalaLongCodec.fieldOf("count").forGetter((it: StasisTubeContents) => it.count)
      ).apply(instance, StasisTubeContents.apply)
    }

    val STREAM_CODEC: StreamCodec[RegistryFriendlyByteBuf, StasisTubeContents] = StreamCodec.composite(
      ItemVariant.PACKET_CODEC, (it: StasisTubeContents) => it.variant,
      ByteBufCodecs.VAR_LONG, (it: StasisTubeContents) => it.count,
      StasisTubeContents.apply
    )

    val EMPTY: StasisTubeContents = StasisTubeContents(ItemVariant.blank(), 0)

    def fromStack(stack: ItemStack): StasisTubeContents =
      new StasisTubeContents(ItemVariant.of(stack), stack.getCount.toLong)

    // idea stolen from spectrum
    // this is _required_ because you NEED to grab the world (or at least the max allowed)
    class Builder(var template: ItemVariant, var count: Long, val max: Long):
      def isEmpty: Boolean = count == 0 || template.isBlank

      def asPatch: DataComponentPatch =
        DataComponentPatch.builder()
                          .set(LumomancyDataComponents.stasisTubeContents, this.build)
                          .build()

      def getMaxAllowed(variant: ItemVariant, amount: Long): Long =
        if variant.isBlank
          || amount <= 0
          || !variant.getItem.canFitInsideContainerItems
          || (!this.isEmpty && variant != template) then
          0
        else
          this.max - this.count
      def getMaxAllowed(stack: ItemStack): Int =
        math.min(getMaxAllowed(ItemVariant.of(stack), stack.getCount), Int.MaxValue).toInt
      def insertStack(stack: ItemStack): Int =
        val added = math.min(stack.getCount, this.getMaxAllowed(stack))
        if added == 0 then
          return 0

        if this.count == 0 then
          this.template = ItemVariant.of(stack)

        this.count += math.min(this.max - this.count, added)
        stack.shrink(added)
        added
      def extractFromStorage(storage: SingleVariantStorage[ItemVariant]): Long =
        Using(Transaction.openOuter()) { transaction =>
          val max = getMaxAllowed(storage.variant, storage.amount)
          val added = storage.extract(storage.variant, max, transaction)
          // Written like this to avoid Non Local Returns
          if added == 0 then
            0
          else
            if this.count == 0 then
              this.template = storage.variant

            this.count += math.min(this.max - this.count, added)
            transaction.commit()
            added
        }.get

      def insertVariant(variant: ItemVariant, amount: Long): Long =
        val added = math.min(amount, getMaxAllowed(variant, amount))

        if added == 0 then
          return 0

        if this.isEmpty then
          this.template = variant

        this.count += math.min(this.max - this.count, added)
        added

      def removeVariant(variant: ItemVariant, amount: Long): Long =
        if this.isEmpty || variant != template then
          0
        else
          val toRemove = math.min(this.count, amount)
          this.count -= toRemove
          if this.count == 0 then
            this.template = ItemVariant.blank()

          toRemove

      def addFromSlot(slot: Slot, player: Player): Long =
        val i = this.getMaxAllowed(slot.getItem)
        this.insertStack(slot.safeTake(slot.getItem.getCount, i, player))

      def remove(amount: Int): ItemStack =
        if this.isEmpty then
          ItemStack.EMPTY
        else
          val toRemove = math.min(this.count, amount).toInt
          val removed = this.template.toStack(toRemove)
          this.count -= toRemove
          if this.count == 0 then
            this.template = ItemVariant.blank()

          removed

      def removeStack(): ItemStack =
        remove(template.toStack.getMaxStackSize)

      def build: StasisTubeContents = StasisTubeContents(template, count)

    object Builder:
      def ofLookup(lookup: HolderLookup.Provider, stack: ItemStack): Builder =
        val prev = stack.getOrDefault(LumomancyDataComponents.stasisTubeContents, StasisTubeContents.EMPTY)
        val max = StasisTube.getMaxAmount(LumoEnchantmentHelper.getLevel(lookup, Enchantments.POWER, stack))
        Builder(prev.variant, prev.count, max)
      def ofWorld(world: Level, stack: ItemStack): Builder = ofLookup(world.registryAccess(), stack)

      def expensively(stack: ItemStack): Builder =
        val prev = stack.getOrDefault(LumomancyDataComponents.stasisTubeContents, StasisTubeContents.EMPTY)
        val max = maxAmountExpensive(stack)
        Builder(prev.variant, prev.count, max)

  
  object Renderer:
    val stasisTubeID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Lumomancy.MOD_ID, "item/stasis_tube_base")

  @Environment(EnvType.CLIENT)
  class Renderer extends BuiltinItemRendererRegistry.DynamicItemRenderer, ModelLoadingPlugin:
    def drawBundle(itemRenderer: ItemRenderer, stack: ItemStack, mode: ItemDisplayContext, poseStack: PoseStack, multiBufferSource: MultiBufferSource, light: Int, overlay: Int, model: BakedModel): Unit =
      poseStack.pushPose()

      poseStack.translate(0.5f, 0.5f, 0.5f)
      // hack
      //val lights = Array.copyOf(RenderSystemAccessor.getShaderLightDirections, 2)
      if mode == ItemDisplayContext.GUI then
        Lighting.setupForFlatItems()

      // HACK: coerce to BufferSource so I can end the batch and render as a flat item
      // correctly
      val source = multiBufferSource.asInstanceOf[MultiBufferSource.BufferSource]

      itemRenderer.render(stack, mode, false, poseStack, source, light, overlay, model)
      model.getTransforms.getTransform(mode).apply(false, poseStack)
      source.endBatch()



      poseStack.popPose()
      if mode == ItemDisplayContext.GUI then
        Lighting.setupFor3DItems()
      //Array.copy(lights, 0, RenderSystemAccessor.getShaderLightDirections, 0, 2)

    def drawContents(itemRenderer: ItemRenderer, stack: ItemStack, poseStack: PoseStack, multiBufferSource: MultiBufferSource, light: Int): Unit =
      val bundledModel = itemRenderer.getModel(stack, null, null, 0)
      poseStack.pushPose()
      val lights = Array.copyOf(RenderSystemAccessor.getShaderLightDirections, 2)

      if bundledModel.isGui3d then
        Lighting.setupFor3DItems()
      else
        Lighting.setupForFlatItems()

      poseStack.translate(0.5f, 0.5f, 1f)
      poseStack.scale(0.5f, 0.5f, 0.5f)
      poseStack.translate(0.5f, 0.5f, 0.5f)

      val source = multiBufferSource.asInstanceOf[MultiBufferSource.BufferSource]

      itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, source, light, OverlayTexture
          .NO_OVERLAY, bundledModel)
      source.endBatch()

      Array.copy(lights, 0, RenderSystemAccessor.getShaderLightDirections, 0, 2)
      poseStack.popPose()


    override def render(stack: ItemStack, itemDisplayContext: ItemDisplayContext, poseStack: PoseStack, multiBufferSource: MultiBufferSource, light: Int, overlay: Int): Unit =
      val client = Minecraft.getInstance()
      val itemRenderer = client.getItemRenderer

      val modelManager = client.getModelManager
      val bottleModel = modelManager.getModel(Renderer.stasisTubeID)


      drawBundle(itemRenderer, stack, itemDisplayContext, poseStack, multiBufferSource, light, overlay, bottleModel)
      if itemDisplayContext != ItemDisplayContext.GUI || !stack.has(LumomancyDataComponents.stasisTubeContents) then
        return

      val contents = stack.get(LumomancyDataComponents.stasisTubeContents)
      if !contents.isEmpty then
        drawContents(itemRenderer, contents.variant.toStack, poseStack, multiBufferSource, light)


    override def onInitializeModelLoader(context: ModelLoadingPlugin.Context): Unit =
      context.addModels(Renderer.stasisTubeID)
