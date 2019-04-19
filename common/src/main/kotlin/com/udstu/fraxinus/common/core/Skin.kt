package com.udstu.fraxinus.common.core

import com.udstu.fraxinus.common.core.base.*
import com.udstu.fraxinus.common.enum.*

class Skin(url: String, model: String) : Texture(TextureType.SKIN, url, mapOf("model" to model))
