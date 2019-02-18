package com.udstu.fraxinus.asgard.dto

import com.udstu.fraxinus.helheim.enum.*

class SkinModel(url: String, val model: ProfileModelType) : TextureModel(TextureType.SKIN, url, mapOf("model" to model.desc))
