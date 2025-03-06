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

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.client.render.LumoRenderHelper
import gay.menkissing.lumomancy.content.LumomancyItems
import gay.menkissing.lumomancy.mixin.{LumoBucketItemAccessor, RenderSystemAccessor}
import gay.menkissing.lumomancy.registries.LumomancyDataComponents
import gay.menkissing.lumomancy.util.{LumoEnchantmentHelper, LumoNumberFormatting}
import net.fabricmc.fabric.api.item.v1.EnchantingContext
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.{FluidConstants, FluidVariant, FluidVariantAttributes}
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.core.{BlockPos, Holder}
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.{ByteBufCodecs, StreamCodec}
import net.minecraft.world.item.enchantment.{Enchantment, Enchantments}
import net.minecraft.world.item.{BucketItem, Item, ItemDisplayContext, ItemStack, TooltipFlag}
import net.minecraft.world.level.{ClipContext, Level}
import net.minecraft.world.level.material.{FlowingFluid, Fluid, Fluids}
import gay.menkissing.lumomancy.util.codec.LumoCodecs.LongExtensions.*
import net.fabricmc.api.{EnvType, Environment}
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.mixin.transfer.BucketItemAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{MultiBufferSource, RenderType}
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.particles.{ParticleOptions, ParticleTypes}
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.{SoundEvents, SoundSource}
import net.minecraft.tags.FluidTags
import net.minecraft.world.{InteractionHand, InteractionResultHolder}
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.{BucketPickup, LiquidBlockContainer}
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.{BlockHitResult, HitResult}
import org.jetbrains.annotations.Nullable

import java.util

// A container item 4 fluids : 3
class StasisBottle(props: Item.Properties) extends Item(props):
  override def isEnchantable(stack: ItemStack): Boolean = true

  override def getEnchantmentValue: Int = 5

  override def canBeEnchantedWith(stack: ItemStack, enchantment: Holder[Enchantment], context: EnchantingContext): Boolean = {
    super
      .canBeEnchantedWith(stack, enchantment, context) || enchantment.is(Enchantments.POWER)
  }

  override def appendHoverText(stack: ItemStack, context: Item.TooltipContext, tooltipComponents: util.List[Component], tooltipFlag: TooltipFlag): Unit = {

    val contents = StasisBottle.getContents(stack)
    if contents.isEmpty then
      tooltipComponents.add(Component.translatable("item.lumomancy.stasis_bottle.tooltip.empty"))
    else
      val containedFluid = contents.variant
      val max = StasisBottle.maxAllowed(LumoEnchantmentHelper.getLevel(context.registries(), Enchantments.POWER, stack))
      tooltipComponents.add(Component.translatable("item.lumomancy.stasis_bottle.tooltip.count_mb", LumoNumberFormatting.formatMB(contents.amount),LumoNumberFormatting.formatMB(max)))
      tooltipComponents.add(FluidVariantAttributes.getName(containedFluid))


  }

  def playEmptyingSound(player: Player, level: Level, pos: BlockPos, variant: FluidVariant): Unit =
    val event = FluidVariantAttributes.getEmptySound(variant)
    level.playSound(player, pos, event, SoundSource.BLOCKS, 1f, 1f)

  // adapted from tech reborn
  def placeFluid(@Nullable player: Player, level: Level, pos: BlockPos, hitResult: BlockHitResult, thisStack: ItemStack): Boolean =
    val contents = StasisBottle.getContents(thisStack)
    if contents.isEmpty || contents.amount < FluidConstants.BUCKET then
      return false

    val blockState = level.getBlockState(pos)
    val canPlace = blockState.canBeReplaced(contents.variant.getFluid)

    if
      !blockState.isAir && !canPlace && (
        blockState.getBlock match
          case liquidBlock: LiquidBlockContainer => !liquidBlock.canPlaceLiquid(player, level, pos, blockState, contents.variant.getFluid)
          case _ => true
      )
    then
      hitResult != null && this.placeFluid(player, level, hitResult.getBlockPos.relative(hitResult.getDirection), null, thisStack)
    else
      if level.dimensionType().ultraWarm() && contents.variant.getFluid.is(FluidTags.WATER) then
        val i = pos.getX
        val j = pos.getY
        val k = pos.getZ
        level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f)

        (0 until 8).foreach { l =>
          level.addParticle(ParticleTypes.LARGE_SMOKE, i.toDouble + math.random(), j.toDouble + math.random(), k.toDouble + math.random, 0d, 0d, 0d)
        }
      else if blockState.getBlock.isInstanceOf[LiquidBlockContainer] && contents.variant.getFluid == Fluids.WATER then
        if blockState.getBlock.asInstanceOf[LiquidBlockContainer].placeLiquid(level, pos, blockState, Fluids.WATER.getSource(false)) then
          this.playEmptyingSound(player, level, pos, contents.variant)
      else
        if !level.isClientSide && canPlace && !blockState.liquid() then
          level.removeBlock(pos, true)

        this.playEmptyingSound(player, level, pos, contents.variant)
        level.setBlock(pos, contents.variant.getFluid.defaultFluidState().createLegacyBlock(), 11)

      true


  override def use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder[ItemStack] = {
    val stack = player.getItemInHand(usedHand)
    val contents = StasisBottle.getContents(stack)
    val blockHitResult = Item.getPlayerPOVHitResult(level, player, if player.isShiftKeyDown then ClipContext.Fluid.NONE else ClipContext.Fluid.SOURCE_ONLY)
    if blockHitResult.getType == HitResult.Type.MISS then
      return InteractionResultHolder.pass(stack)
    else if blockHitResult.getType != HitResult.Type.BLOCK then
      return InteractionResultHolder.pass(stack)
    else
      val hitPos = blockHitResult.getBlockPos
      val direction = blockHitResult.getDirection
      val placePos = hitPos.relative(direction)
      if !level.mayInteract(player, hitPos) || !player.mayUseItemAt(placePos, direction, stack) then
        return InteractionResultHolder.fail(stack)
      else
        val hitState = level.getBlockState(hitPos)
        val builder = StasisBottle.StasisBottleContents.Builder.ofWorld(level, stack)
        if player.isShiftKeyDown then
          // placing
          if builder.extract(builder.template, FluidConstants.BUCKET) != FluidConstants.BUCKET then
            return InteractionResultHolder.fail(stack)


          val targetPos = if hitState.getBlock.isInstanceOf[LiquidBlockContainer] then hitPos else placePos
          if this.placeFluid(player, level, targetPos, blockHitResult, stack) then
            if player.getAbilities.instabuild then
              return InteractionResultHolder.success(stack)

            val newStack = stack.copy()
            newStack.applyComponents(builder.asPatch)
            return InteractionResultHolder.success(newStack)
        else
          // pickup
          if builder.max - builder.amount >= FluidConstants.BUCKET then

            val fluid = level.getFluidState(hitPos)

            if fluid != null && (builder.isEmpty || builder.template.getFluid == fluid.getType) then
              hitState.getBlock match
                case bucketPickup: BucketPickup =>
                  if !bucketPickup.pickupBlock(player, level, hitPos, hitState).isEmpty then
                    // play sound...
                    Lumomancy.LOGGER.info("inserted " + builder.insert(if builder.isEmpty then FluidVariant.of(fluid.getType) else builder.template, FluidConstants.BUCKET))
                    Lumomancy.LOGGER.info("maximum " + builder.max)
                    val newStack = stack.copy()
                    newStack.applyComponents(builder.asPatch)
                    return InteractionResultHolder.success(newStack)




    InteractionResultHolder.fail(stack)
  }


object StasisBottle:
  val baseMax: Long = FluidConstants.BUCKET * 128

  def maxAllowed(level: Int): Long =
    baseMax * math.pow(2, math.min(level, 5)).toLong

  def getMaxStackExpensive(stack: ItemStack): Long =
    maxAllowed(LumoEnchantmentHelper.getLevelExpensive(Enchantments.POWER, stack))

  def getContents(stack: ItemStack): StasisBottleContents =
    stack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY)

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
          val extracted = builder.extract(resource, maxAmount)
          val newStack = context.getItemVariant.toStack
          newStack.set(LumomancyDataComponents.stasisBottleContents, builder.build)
          val newVariant = ItemVariant.of(newStack)

          if context.exchange(newVariant, 1, transaction) == 1 then
            return extracted

        0

      override def insert(resource: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long =
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)

        if !context.getItemVariant.isOf(LumomancyItems.stasisBottle) then
          return 0

        val builder = Builder.expensively(context.getItemVariant.toStack)

        if builder.isEmpty || resource == builder.template then
          val inserted = builder.insert(resource, maxAmount)
          val newStack = context.getItemVariant.toStack
          newStack.applyComponents(builder.asPatch)
          val newVariant = ItemVariant.of(newStack)

          if context.exchange(newVariant, 1, transaction) == 1 then
            return inserted

        0

      override def isResourceBlank: Boolean =
        !context.getItemVariant.isOf(LumomancyItems.stasisBottle)
        || context.getItemVariant.toStack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY).variant.isBlank

      override def getResource: FluidVariant =
        if !context.getItemVariant.isOf(LumomancyItems.stasisBottle) then
          FluidVariant.blank()
        else
          context.getItemVariant.toStack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY).variant

      override def getAmount: Long =
        if !context.getItemVariant.isOf(LumomancyItems.stasisBottle) then
          0
        else
          context.getItemVariant.toStack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY).amount


    class Builder(var template: FluidVariant, var amount: Long, val max: Long):
      def isEmpty: Boolean =
        template.isBlank || amount == 0

      def asPatch: DataComponentPatch =
        DataComponentPatch.builder()
                          .set(LumomancyDataComponents.stasisBottleContents, this.build)
                          .build()

      def copied: Builder =
        Builder(template, amount, max)

      def build: StasisBottleContents =
        StasisBottleContents(template, amount)

      def getMaxAllowed(variant: FluidVariant, amount: Long): Long =
        if variant.isBlank || amount <= 0 || (!this.isEmpty && template != variant) then
          0
        else
          this.max - this.amount

      def insert(variant: FluidVariant, amount: Long): Long =
        val added = math.min(amount, getMaxAllowed(variant, amount))
        if added == 0 then
          return 0

        if this.isEmpty then
          this.template = variant

        this.amount += math.min(this.max - this.amount, added)
        added

      def extract(variant: FluidVariant, amount: Long): Long =
        if variant != template then
          0
        else
          val toRemove = math.min(this.amount, amount)
          this.amount -= toRemove
          if this.amount == 0 then
            this.template = FluidVariant.blank()

          toRemove




    object Builder:
      def ofWorld(level: Level, stack: ItemStack): StasisBottleContents.Builder =
        val prev = stack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY)
        val max = StasisBottle.maxAllowed(LumoEnchantmentHelper.getLevel(level.registryAccess(), Enchantments.POWER, stack))
        StasisBottleContents.Builder(prev.variant, prev.amount, max)

      def expensively(stack: ItemStack): StasisBottleContents.Builder =
        val prev = stack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY)
        val max = StasisBottle.getMaxStackExpensive(stack)
        StasisBottleContents.Builder(prev.variant, prev.amount, max)

  /*
  object Renderer:
    val stasisBottleID: ResourceLocation = Lumomancy.locate("item/stasis_bottle_base")
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

    def drawContents(variant: FluidVariant, poseStack: PoseStack, multiBufferSource: MultiBufferSource, light: Int): Unit =
      val sprites = FluidVariantRendering.getSprites(variant)
      if sprites == null || sprites.length < 1 || sprites(0) == null then
        return
      val sprite = sprites(0)
      val color = FluidVariantRendering.getColor(variant)
      poseStack.pushPose()

      poseStack.translate(0.5f, 0.5f, 1f)
      poseStack.scale(0.5f, 0.5f, 0.5f)
      poseStack.translate(0.5f, 0.5f, 0.5f)

      val source = multiBufferSource.asInstanceOf[MultiBufferSource.BufferSource]

      val buf = source.getBuffer(RenderType.gui())
      buf

      LumoRenderHelper.drawTintedSprite(poseStack, sprite, source, color, 0, 0, 0, 0, 16, 16)
      source.endBatch()

      poseStack.popPose()


    override def render(stack: ItemStack, itemDisplayContext: ItemDisplayContext, poseStack: PoseStack, multiBufferSource: MultiBufferSource, light: Int, overlay: Int): Unit =
      val client = Minecraft.getInstance()
      val itemRenderer = client.getItemRenderer

      val modelManager = client.getModelManager
      val bottleModel = modelManager.getModel(Renderer.stasisBottleID)


      drawBundle(itemRenderer, stack, itemDisplayContext, poseStack, multiBufferSource, light, overlay, bottleModel)
      if itemDisplayContext != ItemDisplayContext.GUI || !stack.has(LumomancyDataComponents.stasisBottleContents) then
        return

      val contents = stack.get(LumomancyDataComponents.stasisBottleContents)
      if !contents.isEmpty then
        drawContents(contents.variant, poseStack, multiBufferSource, light)


    override def onInitializeModelLoader(context: ModelLoadingPlugin.Context): Unit =
      context.addModels(Renderer.stasisBottleID)
        */