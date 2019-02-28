package com.udstu.fraxinus.asgard.core.base

import com.fasterxml.jackson.annotation.*
import com.udstu.fraxinus.asgard.enum.*

abstract class Texture(@JsonIgnore val type: TextureType, val url: String, val metadata: Map<String, String>?)
