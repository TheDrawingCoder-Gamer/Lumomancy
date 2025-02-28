package gay.menkissing.lumomancy.registries

import gay.menkissing.lumomancy.Lumomancy
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object LumomancyTags:
  val validToolTag: TagKey[Item] = TagKey.create(BuiltInRegistries.ITEM.key(), Lumomancy.locate("valid_tools"))

  def init(): Unit = ()