package com.udstu.fraxinus.web.components

import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.*
import react.*
import react.dom.*

class SignUpForm : RComponent<RProps, SignUpForm.State>() {

    interface State : RState {
        var email: String
        var password: String
        var characterName: String
    }

    override fun State.init() {
        email = ""
        password = ""
        characterName = ""
    }

    fun signUp() {

    }

    override fun RBuilder.render() {
        div("card") {
            div("card-content") {
                div("content") {
                    div("columns") {
                        div("column is-half is-offset-one-quarter") {
                            input(classes = "input", type = InputType.text) {
                                attrs {
                                    placeholder = "邮箱"
                                    value = state.email
                                    onChangeFunction = {
                                        val target = it.target as HTMLInputElement
                                        setState {
                                            email = target.value
                                        }
                                    }
                                }
                            }
                        }
                    }

                    div("columns") {
                        div("column is-half is-offset-one-quarter") {
                            input(classes = "input", type = InputType.text) {
                                attrs {
                                    placeholder = "角色名"
                                    value = state.characterName
                                    onChangeFunction = {
                                        val target = it.target as HTMLInputElement
                                        setState {
                                            password = target.value
                                        }
                                    }
                                }
                            }
                        }
                    }

                    div("columns") {
                        div("column is-half is-offset-one-quarter") {
                            input(classes = "input", type = InputType.password) {
                                attrs {
                                    placeholder = "密码"
                                    value = state.password
                                    onChangeFunction = {
                                        val target = it.target as HTMLInputElement
                                        setState {
                                            password = target.value
                                        }
                                    }
                                }
                            }
                        }
                    }

                    div("columns") {
                        div("column is-half is-offset-one-quarter") {
                            button(classes = "button") {
                                +"注册"
                                attrs {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.signUpForm() = child(SignUpForm::class) { }
