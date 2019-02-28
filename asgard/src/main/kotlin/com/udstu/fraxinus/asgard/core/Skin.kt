package com.udstu.fraxinus.asgard.core

import com.udstu.fraxinus.asgard.core.base.*
import com.udstu.fraxinus.asgard.dao.entity.SkinEntity
import com.udstu.fraxinus.asgard.enum.*

class Skin(url: String, val model: String) : Texture(TextureType.SKIN, url, mapOf("model" to model))
