package gay.menkissing.lumomancy.registries

import gay.menkissing.lumomancy.Lumomancy
import gay.menkissing.lumomancy.api.energy.LumonColor
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

object LumonColors:
  def make(id: ResourceLocation, color: LumonColor): LumonColor =
    Registry.register(LumomancyRegistries.lumonColors, id, color)

  // primary colors
  val red: LumonColor = make(Lumomancy.locate("red"), LumonColor.fromInts(0xffff0000, 0xffff0000))
  val green: LumonColor = make(Lumomancy.locate("green"), LumonColor.fromInts(0xff00ff00, 0xff00ff00))
  val blue: LumonColor = make(Lumomancy.locate("blue"), LumonColor.fromInts(0xff0000ff, 0xff0000ff))
  val white: LumonColor = make(Lumomancy.locate("white"), LumonColor.fromInts(0xffffffff, 0xffffffff))
  val black: LumonColor = make(Lumomancy.locate("black"), LumonColor.fromInts(0xff000000, 0xff808080))

  // blue-red mixes
  val purple: LumonColor = make(Lumomancy.locate("purple"), LumonColor.fromInts(0xff8000ff, 0xff8000ff))
  val magenta: LumonColor = make(Lumomancy.locate("magenta"), LumonColor.fromInts(0xffff00ff, 0xffff00ff))
  val rose: LumonColor = make(Lumomancy.locate("rose"), LumonColor.fromInts(0xffff007f, 0xffff007f))

  // red-green mixes
  val orange: LumonColor = make(Lumomancy.locate("orange"), LumonColor.fromInts(0xffff8000, 0xffff8000))
  val yellow: LumonColor = make(Lumomancy.locate("yellow"), LumonColor.fromInts(0xffffff00, 0xffffff00))
  val lime: LumonColor = make(Lumomancy.locate("lime"), LumonColor.fromInts(0xff7fff00, 0xff7fff00))

  // blue-green mixes
  val azure: LumonColor = make(Lumomancy.locate("azure"), LumonColor.fromInts(0xff0080ff, 0xff0080ff))
  val cyan: LumonColor = make(Lumomancy.locate("cyan"), LumonColor.fromInts(0xff00ffff, 0xff00ffff))
  val seafoam: LumonColor = make(Lumomancy.locate("seafoam"), LumonColor.fromInts(0xff00ff7f, 0xff00ff7f))
  
  def init(): Unit = ()

