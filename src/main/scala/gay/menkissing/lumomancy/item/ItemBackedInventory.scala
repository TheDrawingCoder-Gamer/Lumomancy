package gay.menkissing.lumomancy.item

import gay.menkissing.lumomancy.registries.LumomancyDataComponents
import net.minecraft.core.component.DataComponents
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents

import scala.jdk.CollectionConverters.*
import scala.jdk.StreamConverters.*

class ItemBackedInventory(val stack: ItemStack, expectedSize: Int) extends SimpleContainer(expectedSize):
  locally:
    val contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
    var i = 0
    contents.stream().forEachOrdered { item =>
      setItem(i, item)
      i = i + 1
    }

  override def stillValid(player: Player): Boolean =
    !stack.isEmpty

  override def setChanged(): Unit =
    super.setChanged()
    stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(getItems))
      
  