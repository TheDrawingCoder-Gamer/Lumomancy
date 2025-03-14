if [ -d "wood_images" ]; then
  rm -r wood_images
fi

  mkdir -p "wood_images/"
  mkdir "wood_images/block"
  mkdir "wood_images/item"
  mkdir -p "wood_images/entity/signs/hanging"
  mkdir -p "wood_images/gui/hanging_signs"

for x in wood_palettes/*.ase; do
  NAME1=$( basename $x )
  NAME=${NAME1%.ase}


  aseprite -b lumo_bark.aseprite --palette "$x" --save-as "wood_images/item/${NAME}_bark.png"
  aseprite -b lumo_hanging_sign.aseprite --palette "$x" --save-as "wood_images/entity/signs/hanging/${NAME}.png"
  aseprite -b lumo_hanging_sign_gui.aseprite --palette "$x" --save-as "wood_images/gui/hanging_signs/${NAME}.png"
  aseprite -b lumo_hanging_sign_icon.aseprite --palette "$x" --save-as "wood_images/item/${NAME}_hanging_sign.png"
  aseprite -b lumo_log.aseprite --palette "$x" --save-as "wood_images/block/${NAME}_log.png"
  aseprite -b lumo_log_top.aseprite --palette "$x" --save-as "wood_images/block/${NAME}_log_top.png"
  aseprite -b lumo_sign.aseprite --palette "$x" --save-as "wood_images/entity/signs/${NAME}.png"
  aseprite -b lumo_sign_icon.aseprite --palette "$x" --save-as "wood_images/item/${NAME}_sign.png"
  aseprite -b lumo_wooden_planks.aseprite --palette "$x" --save-as "wood_images/block/${NAME}_planks.png"
  aseprite -b stripped_lumo_log.aseprite --palette "$x" --save-as "wood_images/block/stripped_${NAME}_log.png"
  aseprite -b stripped_lumo_log_top.aseprite --palette "$x" --save-as "wood_images/block/stripped_${NAME}_log_top.png"
done