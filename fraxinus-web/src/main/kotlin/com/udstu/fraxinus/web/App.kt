package com.udstu.fraxinus.web

import com.udstu.fraxinus.web.pages.signUpPage
import react.*
import react.dom.*
import react.router.dom.*

fun RBuilder.app() {
    div {
        header {
            // Header Component
        }

        switch {
            route("/") {

                div {
                    signUpPage()
                }
            }
        }

        footer {
            // Footer Component
        }
    }
}