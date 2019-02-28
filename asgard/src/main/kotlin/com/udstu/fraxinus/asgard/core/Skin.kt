package com.udstu.fraxinus.asgard.core

import com.udstu.fraxinus.asgard.core.base.*
import com.udstu.fraxinus.asgard.enum.*

class Skin(url: String, model: String) : Texture(TextureType.SKIN, url, mapOf("model" to model))
