package com.udstu.fraxinus.helheim.core.base

import com.fasterxml.jackson.annotation.*
import com.udstu.fraxinus.helheim.enum.*

abstract class Texture(@JsonIgnore val type: TextureType, val url: String, val metadata: Map<String, String>?)
