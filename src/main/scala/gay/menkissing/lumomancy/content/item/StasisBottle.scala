package gay.menkissing.lumomancy.content.item

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.menkissing.lumomancy.content.item.StasisBottle.{StasisBottleContents, getMaxStoredAmount}
import gay.menkissing.lumomancy.registries.LumomancyDataComponents
import gay.menkissing.lumomancy.util.codec.LumoCodecs
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.{ClickAction, Slot}
import net.minecraft.world.item.{Item, ItemStack, TooltipFlag}

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