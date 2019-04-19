package com.udstu.fraxinus.web

import react.dom.*
import react.router.dom.*
import kotlin.browser.*

fun main() {
    render(document.getElementById("root")) {
        browserRouter {
            app()
        }
    }
}
