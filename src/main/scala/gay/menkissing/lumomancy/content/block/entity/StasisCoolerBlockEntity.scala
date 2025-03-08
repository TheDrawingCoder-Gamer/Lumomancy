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

package gay.menkissing.lumomancy.content.block.entity

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.content.{LumomancyBlocks, LumomancyItems}
import gay.menkissing.lumomancy.content.block.StasisCooler
import gay.menkissing.lumomancy.content.item.{StasisBottle, StasisTube}
import net.fabricmc.fabric.api.transfer.v1.fluid.{FluidStorage, FluidVariant}
import net.fabricmc.fabric.api.transfer.v1.item.{ItemStorage, ItemVariant}
import net.fabricmc.fabric.api.transfer.v1.storage.{Storage, StoragePreconditions}
import net.fabricmc.fabric.api.transfer.v1.storage.base.{CombinedStorage, SingleSlotStorage, SingleVariantStorage}
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.core.{BlockPos, HolderLookup, NonNullList}
import net.minecraft.nbt.{CompoundTag, NbtOps, ListTag}
import net.minecraft.world.entity.player.Player
import net.minecraft.world.{Container, ContainerHelper}
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent

import scala.jdk.CollectionConverters.*
import java.util.Objects

class StasisCoolerBlockEntity(pos: BlockPos, state: BlockState) extends BlockEntity(LumomancyBlocks.stasisCoolerBlockEntity, pos, state):
  private val items = NonNullList.withSize(6, ItemStack.EMPTY)
  private var lastInteractedSlot: Int = -1

  override def loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider): Unit = {
    super.loadAdditional(tag, registries)
    ContainerHelper.loadAllItems(tag, items, registries)
    this.loadItemFilters(tag)
    this.loadFluidFilters(tag)
    this.lastInteractedSlot = tag.getInt("last_interacted_slot")
  }

  override def saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider): Unit = {
    super.saveAdditional(tag, registries)
    ContainerHelper.saveAllItems(tag, items, registries)
    this.saveItemFilters(tag)
    this.saveFluidFilters(tag)
    tag.putInt("last_interacted_slot", lastInteractedSlot)
  }

  def clearContent(): Unit =
    items.clear()

  def isEmpty: Boolean =
    items.stream().allMatch(_.isEmpty)

  private def updateState(slot: Int): Unit =
    if slot >= 0 && slot < 6 then
      this.lastInteractedSlot = slot
      var blockState = this.getBlockState

      StasisCooler.COOLER_SLOT_OCCUPIED_PROPS.zipWithIndex.foreach { (prop, i) =>
        val kind =
          val item = items.get(i)
          if item.isEmpty then
            StasisCooler.CoolerSlotOccupiedBy.Empty
          else if item.is(LumomancyItems.stasisBottle) then
            StasisCooler.CoolerSlotOccupiedBy.Bottle
          else
            StasisCooler.CoolerSlotOccupiedBy.Tube
        blockState = blockState.setValue(prop, kind)
      }
      Objects.requireNonNull(this.level).setBlock(this.worldPosition, blockState, 3)
      this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.worldPosition, GameEvent.Context.of(blockState))
    else
      Lumomancy.LOGGER.error("Expected slot to be 0-5 got {}", slot)


  def loadItemFilters(tag: CompoundTag): Unit =
    val listTag = tag.getList(StasisCoolerBlockEntity.tagItemFilters, 10)
    for i <- 0 until listTag.size() do
      val compound = listTag.getCompound(i)
      val j = compound.getByte("Slot").toInt
      if j >= 0 && j < 6 then
        val variant = ItemVariant.CODEC.parse(NbtOps.INSTANCE, compound).result().orElse(ItemVariant.blank())
        itemStorage.parts.get(j).filter = variant

  def loadFluidFilters(tag: CompoundTag): Unit =
    val listTag = tag.getList(StasisCoolerBlockEntity.tagFluidFilters, 10)
    for i <- 0 until listTag.size() do
      val compound = listTag.getCompound(i)
      val j = compound.getByte("Slot").toInt
      if j >= 0 && j < 6 then
        val variant = FluidVariant.CODEC.parse(NbtOps.INSTANCE, compound).result().orElse(FluidVariant.blank())
        fluidStorage.parts.get(j).filter = variant

  def saveItemFilters(tag: CompoundTag): Unit =
    val listTag = ListTag()
    for i <- 0 until 6 do
      val filter = itemStorage.parts.get(i).filter
      if !filter.isBlank then
        val compound = CompoundTag()
        compound.putByte("Slot", i.toByte)
        listTag.add(ItemVariant.CODEC.encode(filter, NbtOps.INSTANCE, compound).getOrThrow())
    tag.put(StasisCoolerBlockEntity.tagItemFilters, listTag)

  def saveFluidFilters(tag: CompoundTag): Unit =
    val listTag = ListTag()
    for i <- 0 until 6 do
      val filter = fluidStorage.parts.get(i).filter
      if !filter.isBlank then
        val compound = CompoundTag()
        compound.putByte("Slot", i.toByte)
        listTag.add(FluidVariant.CODEC.encode(filter, NbtOps.INSTANCE, compound).getOrThrow())
    tag.put(StasisCoolerBlockEntity.tagFluidFilters, listTag)


  // Implemented SOME fields from Container, but didn't implement container
  // so i can have a separate fabric storage transfer api impl
  def getContainerSize(): Int = 6

  def getItem(slot: Int): ItemStack = this.items.get(slot)

  def removeItem(slot: Int, amount: Int): ItemStack =
    val stack = Objects.requireNonNullElse(this.items.get(slot), ItemStack.EMPTY)
    this.items.set(slot, ItemStack.EMPTY)
    resetSlot(slot)
    if !stack.isEmpty then
      this.updateState(slot)

    stack

  def removeItemNoUpdate(slot: Int): ItemStack = this.removeItem(slot, 1)

  def setItem(slot: Int, stack: ItemStack, ignoreReset: Boolean = false): Unit =
    if stack.is(LumomancyItems.stasisTube) || stack.is(LumomancyItems.stasisBottle) then
      this.items.set(slot, stack)
      if !ignoreReset then
        resetSlot(slot)
      this.updateState(slot)
    else if stack.isEmpty then
      this.removeItem(slot, 1)

  def stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)

  def getMaxStackSize(stack: ItemStack): Int = 1

  def resetSlot(slot: Int): Unit =
    if slot >= 0 && slot < 6 then
      fluidStorage.parts.get(slot).resetVariant()
      itemStorage.parts.get(slot).resetVariant()



  val fluidStorage: CombinedStorage[FluidVariant, BottleFluidStorageWrapper] =
    new CombinedStorage((0 until 6).map(BottleFluidStorageWrapper.apply).toList.asJava)

  val itemStorage: CombinedStorage[ItemVariant, TubeItemStorageWrapper] =
    CombinedStorage((0 until 6).map(TubeItemStorageWrapper.apply).toList.asJava)

  class TubeItemStorageWrapper(val slot: Int) extends SingleSlotStorage[ItemVariant]:
    var filter: ItemVariant = ItemVariant.blank()



    def resetVariant(): Unit = filter = ItemVariant.blank()

    def validVariant(storedVariant: ItemVariant, resource: ItemVariant): Boolean =
      if filter.isBlank && !storedVariant.isBlank then
        filter = storedVariant

      if !storedVariant.isBlank then
        assert(storedVariant == filter)

      filter.isBlank || filter == resource

    def tube: Option[ItemStack] =
      val thingie = StasisCoolerBlockEntity.this.items.get(slot)
      Option.when(thingie.is(LumomancyItems.stasisTube))(thingie)

    override def insert(resource: ItemVariant, maxAmount: Long, transactionContext: TransactionContext): Long =
      StoragePreconditions.notBlankNotNegative(resource, maxAmount)

      this.tube match
        case None => 0L
        case Some(tube) =>
          val builder = StasisTube.StasisTubeContents.Builder.expensively(tube)

          if validVariant(builder.template, resource) then
            val inserted = builder.insertVariant(resource, maxAmount)
            tube.applyComponents(builder.asPatch)
            StasisCoolerBlockEntity.this.setItem(slot, tube, true)

            inserted
          else
            0L

    override def extract(resource: ItemVariant, maxAmount: Long, transactionContext: TransactionContext): Long =
      StoragePreconditions.notBlankNotNegative(resource, maxAmount)

      this.tube match
        case None => 0L
        case Some(tube) =>
          val builder = StasisTube.StasisTubeContents.Builder.expensively(tube)

          if validVariant(builder.template, resource) then
            val extracted = builder.removeVariant(resource, maxAmount)
            tube.applyComponents(builder.asPatch)
            StasisCoolerBlockEntity.this.setItem(slot, tube, true)

            extracted
          else
            0L

    override def isResourceBlank: Boolean =
      // forall: default true
      this.tube.forall { stack =>
        StasisTube.getContents(stack).isEmpty
      }

    override def getResource: ItemVariant =
      this.tube match
        case None => ItemVariant.blank()
        case Some(tube) => StasisTube.getContents(tube).variant

    override def getAmount: Long =
      this.tube match
        case None => 0L
        case Some(tube) => StasisTube.getContents(tube).count

    override def getCapacity: Long =
      this.tube match
        case None => 0L
        case Some(tube) => StasisTube.maxAmountExpensive(tube)


  class BottleFluidStorageWrapper(val slot: Int) extends SingleSlotStorage[FluidVariant]:
    var filter: FluidVariant = FluidVariant.blank()


    def resetVariant(): Unit = filter = FluidVariant.blank()

    def bottle: Option[ItemStack] =
      val thingie = StasisCoolerBlockEntity.this.items.get(slot)
      Option.when(thingie.is(LumomancyItems.stasisBottle))(thingie)

    def validVariant(storedVariant: FluidVariant, resource: FluidVariant): Boolean =
      if filter.isBlank && !storedVariant.isBlank then
        filter = storedVariant

      if !storedVariant.isBlank then
        assert(storedVariant == filter)

      filter.isBlank || filter == resource

    override def insert(resource: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long = {
      StoragePreconditions.notBlankNotNegative(resource, maxAmount)

      this.bottle match
        case None => 0L
        case Some(bottle) =>
          val builder = StasisBottle.StasisBottleContents.Builder.expensively(bottle)

          if validVariant(builder.template, resource) then
            val inserted = builder.insert(resource, maxAmount)
            bottle.applyComponents(builder.asPatch)
            StasisCoolerBlockEntity.this.setItem(slot, bottle, true)

            inserted
          else
            0L
    }

    override def extract(resource: FluidVariant, maxAmount: Long, transaction: TransactionContext): Long = {
      StoragePreconditions.notBlankNotNegative(resource, maxAmount)

      this.bottle match
        case None => 0L
        case Some(bottle) =>
          val builder = StasisBottle.StasisBottleContents.Builder.expensively(bottle)

          if validVariant(builder.template, resource) then
            val extracted = builder.extract(resource, maxAmount)
            bottle.applyComponents(builder.asPatch)
            StasisCoolerBlockEntity.this.setItem(slot, bottle, true)

            extracted
          else
            0L
    }

    override def getResource: FluidVariant =
      this.bottle match
        case None => FluidVariant.blank()
        case Some(b) => StasisBottle.getContents(b).variant

    override def isResourceBlank: Boolean = getResource.isBlank

    override def getAmount: Long =
      this.bottle match
        case None => 0L
        case Some(bottle) =>
          StasisBottle.getContents(bottle).amount

    override def getCapacity: Long =
      this.bottle match
        case None => 0L
        case Some(bottle) =>
          StasisBottle.getMaxStackExpensive(bottle)

object StasisCoolerBlockEntity:
  val tagFluidFilters = "fluid_filters"
  val tagItemFilters = "item_filters"

  def registerStorages(): Unit =
    FluidStorage.SIDED.registerForBlockEntity[StasisCoolerBlockEntity]((cooler, dir) => {
      cooler.fluidStorage
    }, LumomancyBlocks.stasisCoolerBlockEntity)
    ItemStorage.SIDED.registerForBlockEntity[StasisCoolerBlockEntity]((cooler, dir) => {
      cooler.itemStorage
    }, LumomancyBlocks.stasisCoolerBlockEntity)