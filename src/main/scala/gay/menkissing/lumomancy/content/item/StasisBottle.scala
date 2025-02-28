package gay.menkissing.lumomancy.content.item

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.menkissing.lumomancy.content.item.StasisBottle.getMaxStoredAmount
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
    val curStack = StasisBottle.getContainedStack(thisStack)
    if thatStack.isEmpty then
      if !curStack.isEmpty then
        val res = curStack.split(curStack.getMaxStackSize)
        slot.setByPlayer(res)
        return true
    else
      if !thatStack.getItem.canFitInsideContainerItems then
        return false
      if curStack.isEmpty then
        thisStack.set(LumomancyDataComponents.stasisBottleContents, thatStack)
        slot.setByPlayer(ItemStack.EMPTY)
        return true
      else if ItemStack.isSameItemSameComponents(curStack, thatStack) then
        curStack.grow(thatStack.getCount)
        thisStack.set(LumomancyDataComponents.stasisBottleContents, curStack)
        slot.setByPlayer(ItemStack.EMPTY)
        return true
    false
  }

  override def overrideOtherStackedOnMe(thisStack: ItemStack, thatStack: ItemStack, slot: Slot, clickAction: ClickAction, player: Player, slotAccess: SlotAccess): Boolean = {
    val curStack = StasisBottle.getContainedStack(thisStack)
    if clickAction != ClickAction.SECONDARY then
      return false
    if thatStack.isEmpty then
      if !curStack.isEmpty then
        val res = curStack.split(curStack.getMaxStackSize)
        slotAccess.set(res)
        thisStack.set(LumomancyDataComponents.stasisBottleContents, curStack)
        return true
    else
      if !thatStack.getItem.canFitInsideContainerItems then
        return false
      if curStack.isEmpty then
        thisStack.set(LumomancyDataComponents.stasisBottleContents, thatStack)
        slotAccess.set(ItemStack.EMPTY)
        return true
      else if ItemStack.isSameItemSameComponents(curStack, thatStack) then
        curStack.grow(thatStack.getCount)
        thisStack.set(LumomancyDataComponents.stasisBottleContents, curStack)
        slot.setChanged()
        slotAccess.set(ItemStack.EMPTY)
        return true
    false
  }

  override def appendHoverText(stack: ItemStack, ctx: Item.TooltipContext, tooltip: util.List[Component], tooltipFlag: TooltipFlag): Unit = {
    val thatStack = StasisBottle.getContainedStack(stack)
    if thatStack.isEmpty then
      tooltip.add(Component.translatable("item.lumomancy.stasis_bottle.tooltip.empty"))
    else
      val totalStacks = math.floorDiv(thatStack.getCount, thatStack.getMaxStackSize).toString
      tooltip.add(Component.translatable("item.lumomancy.stasis_bottle.tooltip.count", thatStack.getCount, getMaxStoredAmount(stack), totalStacks))
      tooltip.add(thatStack.getHoverName)
  }

object StasisBottle:
  def getContainedStack(stack: ItemStack): ItemStack =
    stack.getOrDefault(LumomancyDataComponents.stasisBottleContents, ItemStack.EMPTY)

  def getStoredAmount(stack: ItemStack): Int =
    getContainedStack(stack).getCount

  // todo: changable power
  def getMaxStoredAmount(stack: ItemStack): Int = 20000


  case class StasisBottleContents(baseStack: ItemStack, count: Long)

  object StasisBottleContents:
    val CODEC: Codec[StasisBottleContents] = RecordCodecBuilder.create { instance =>
      instance.group(
        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("base_stack").forGetter((it: StasisBottleContents) => it.baseStack),
        LumoCodecs.scalaLongCodec.fieldOf("count").forGetter((it: StasisBottleContents) => it.count)
      ).apply(instance, StasisBottleContents.apply)
    }