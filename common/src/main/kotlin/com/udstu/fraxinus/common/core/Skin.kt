package com.udstu.fraxinus.helheim.core

import com.udstu.fraxinus.helheim.core.base.*
import com.udstu.fraxinus.helheim.enum.*

class Skin(url: String, model: String) : Texture(TextureType.SKIN, url, mapOf("model" to model))
