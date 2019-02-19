package com.udstu.fraxinus.asgard.dto

import com.udstu.fraxinus.helheim.enum.*

class SkinModel(url: String, val model: String) : TextureModel(TextureType.SKIN, url, mapOf("model" to model))
