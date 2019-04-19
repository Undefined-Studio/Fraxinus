package com.udstu.fraxinus.web.pages

import com.udstu.fraxinus.web.components.*
import react.*
import react.dom.*

fun RBuilder.signUpPage() {
    div("columns") {
        div("column is-4 is-offset-one-third") {
            signUpForm()
        }
    }
}
