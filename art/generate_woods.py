#!/usr/bin/python

import subprocess
import shutil
import os

wood_types = {
        "secondary": [
            "aftus",
            "wieder",
            "stillwood"
        ]
}

def aseprite_save(source, dest, palette):
    subprocess.run(["aseprite", "-b", source, "--palette", "wood_palettes/" + palette + ".ase", "--save-as", dest])

def rebase(src_root, dst_root, file):
    rel_path = os.path.relpath(file, start=src_root)
    return os.path.join(dst_root, rel_path)


root_dir = "wood_images"

if os.path.exists(root_dir):
    shutil.rmtree(root_dir)

for root, dirs, files in os.walk("wood/common"):
    for filename in files:
        source = os.path.join(root, filename)
        for wood_type_group_name, wood_type_group in wood_types.items():
            for woodtype in wood_type_group:
                new_file = filename.replace("lumo", woodtype).replace("aseprite", "png")
                dest = rebase("wood/common", root_dir, os.path.join(root, new_file))
                print(source)
                print(dest)
                os.makedirs(os.path.dirname(dest), exist_ok=True)
                aseprite_save(source, dest, woodtype)


for wood_type_group_name, wood_type_group in wood_types.items():
    if os.path.exists("wood/" + wood_type_group_name):
        for root, dirs, files in os.walk("wood/" + wood_type_group_name):
            for filename in files:
                source = os.path.join(root, filename)
                for wood_type in wood_type_group:
                    new_file = filename.replace("lumo", wood_type).replace("aseprite", "png")
                    dest = rebase("wood/" + wood_type_group_name, root_dir, os.path.join(root, new_file))
                    os.makedirs(os.path.dirname(dest), exist_ok=True)
                    aseprite_save(source, dest, wood_type)




