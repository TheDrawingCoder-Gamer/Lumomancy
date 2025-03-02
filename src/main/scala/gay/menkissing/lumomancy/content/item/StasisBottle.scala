package gay.menkissing.lumomancy.content.item

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.item.StasisBottle.{StasisBottleContents, getMaxStoredAmount}
import gay.menkissing.lumomancy.mixin.RenderSystemAccessor
import gay.menkissing.lumomancy.registries.LumomancyDataComponents
import gay.menkissing.lumomancy.util.codec.LumoCodecs
import net.fabricmc.api.{EnvType, Environment}
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{MultiBufferSource, RenderType}
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.{ClickAction, Slot}
import net.minecraft.world.item.{Item, ItemDisplayContext, ItemStack, TooltipFlag}

import java.util

class StasisBottle(props: Item.Properties) extends Item(props):
  override def overrideStackedOnOther(thisStack: ItemStack, slot: Slot, clickAction: ClickAction, player: Player): Boolean = {
    if clickAction != ClickAction.SECONDARY then
      return false
    val thatStack = slot.getItem
    val contents = StasisBottle.getContents(thisStack)
    if thatStack.isEmpty then
      if !contents.isEmpty then
        val (newContents, res) = contents.splitStack()
        newContents.patched(thisStack)
        slot.setByPlayer(res)
        return true
    else
      if !thatStack.getItem.canFitInsideContainerItems then
        return false
      if contents.isEmpty then
        StasisBottleContents.fromStack(thatStack).patched(thisStack)
        slot.setByPlayer(ItemStack.EMPTY)
        return true
      else if ItemStack.isSameItemSameComponents(contents.baseStack, thatStack) then
        val newContents = contents.grown(thatStack.getCount)
        newContents.patched(thisStack)
        slot.setByPlayer(ItemStack.EMPTY)
        return true
    false
  }

  override def overrideOtherStackedOnMe(thisStack: ItemStack, thatStack: ItemStack, slot: Slot, clickAction: ClickAction, player: Player, slotAccess: SlotAccess): Boolean = {
    val contents = StasisBottle.getContents(thisStack)
    if clickAction != ClickAction.SECONDARY then
      return false
    if thatStack.isEmpty then
      if !contents.isEmpty then
        val (newContents, res) = contents.splitStack()
        slotAccess.set(res)
        thisStack.set(LumomancyDataComponents.stasisBottleContents, newContents)
        return true
    else
      if !thatStack.getItem.canFitInsideContainerItems then
        return false
      if contents.isEmpty then
        thisStack.set(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.fromStack(thatStack))
        slotAccess.set(ItemStack.EMPTY)
        return true
      else if ItemStack.isSameItemSameComponents(contents.baseStack, thatStack) then
        val newContents = contents.grown(thatStack.getCount)
        thisStack.set(LumomancyDataComponents.stasisBottleContents, newContents)
        slot.setChanged()
        slotAccess.set(ItemStack.EMPTY)
        return true
    false
  }

  override def appendHoverText(stack: ItemStack, ctx: Item.TooltipContext, tooltip: util.List[Component], tooltipFlag: TooltipFlag): Unit = {
    val contents = StasisBottle.getContents(stack)
    if contents.isEmpty then
      tooltip.add(Component.translatable("item.lumomancy.stasis_bottle.tooltip.empty"))
    else
      val totalStacks = math.floorDiv(contents.count, contents.baseStack.getMaxStackSize).toString
      tooltip.add(Component.translatable("item.lumomancy.stasis_bottle.tooltip.count", contents.count, getMaxStoredAmount(stack), totalStacks))
      tooltip.add(contents.baseStack.getHoverName)
  }

object StasisBottle:
  def getContents(stack: ItemStack): StasisBottleContents =
    stack.getOrDefault(LumomancyDataComponents.stasisBottleContents, StasisBottleContents.EMPTY)

  def getStoredAmount(stack: ItemStack): Long =
    getContents(stack).count

  // todo: changable power
  def getMaxStoredAmount(stack: ItemStack): Long = 20000


  case class StasisBottleContents(baseStack: ItemStack, count: Long):
    def isEmpty: Boolean = baseStack.isEmpty || count == 0

    def splitStack(): (StasisBottleContents, ItemStack) =
      split(baseStack.getMaxStackSize)

    def split(amount: Int): (StasisBottleContents, ItemStack) =
      if this.isEmpty then
        (this, ItemStack.EMPTY)
      else
        (this.copy(count = math.max(0L, count - amount)), baseStack.copyWithCount(math.min(count, amount.toLong).toInt))

    def grown(by: Int): StasisBottleContents = this.copy(count = count + by)

    def patched(stack: ItemStack): Unit =
      stack.set(LumomancyDataComponents.stasisBottleContents, this)

  object StasisBottleContents:
    val CODEC: Codec[StasisBottleContents] = RecordCodecBuilder.create { instance =>
      instance.group(
        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("base_stack").forGetter((it: StasisBottleContents) => it.baseStack),
        LumoCodecs.scalaLongCodec.fieldOf("count").forGetter((it: StasisBottleContents) => it.count)
      ).apply(instance, StasisBottleContents.apply)
    }

    val EMPTY: StasisBottleContents = StasisBottleContents(ItemStack.EMPTY, 0)

    def fromStack(stack: ItemStack): StasisBottleContents =
      new StasisBottleContents(stack.copyWithCount(1), stack.getCount.toLong)

  // FIXME: Weird rendering order can cause 3d items to render as 2d items
  object Renderer:
    val stasisBottleID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Lumomancy.MOD_ID, "item/stasis_bottle_base")

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
      val bottleModel = modelManager.getModel(Renderer.stasisBottleID)


      drawBundle(itemRenderer, stack, itemDisplayContext, poseStack, multiBufferSource, light, overlay, bottleModel)
      if itemDisplayContext != ItemDisplayContext.GUI || !stack.has(LumomancyDataComponents.stasisBottleContents) then
        return

      val contents = stack.get(LumomancyDataComponents.stasisBottleContents)
      if !contents.isEmpty then
        drawContents(itemRenderer, contents.baseStack, poseStack, multiBufferSource, light)


    override def onInitializeModelLoader(context: ModelLoadingPlugin.Context): Unit =
      context.addModels(Renderer.stasisBottleID)
