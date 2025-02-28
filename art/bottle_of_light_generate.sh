for x in bottle_of_light_palettes/*.ase; do aseprite -b bottle_of_light.aseprite --palette "$x" --save-as "${x%.ase}_bottle_of_light.png"; done
