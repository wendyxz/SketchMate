package com.cs346.web.user

import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.PersistenceCreator
import java.io.Serializable
import java.util.UUID


data class User @PersistenceCreator constructor(
    val id: String?,

    var name: String?,

    var password: String?
) : Serializable