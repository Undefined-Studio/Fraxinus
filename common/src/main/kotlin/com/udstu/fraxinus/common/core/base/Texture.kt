package com.udstu.fraxinus.common.core.base

import com.fasterxml.jackson.annotation.*
import com.udstu.fraxinus.common.enum.*

abstract class Texture(@JsonIgnore val type: TextureType, val url: String, val metadata: Map<String, String>?)
