package com.queatz.fantasydating.models

import com.queatz.fantasydating.StringListJsonConverter
import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


@BaseEntity
open class BaseModel {
    @Id var objectBoxId: Long = 0
    var id: String? = null
}

@Entity
data class Token constructor(var token: String = "") : BaseModel()

@Entity
data class DiscoveryPreferences constructor(
    var who: String = "",
    var where: String = "",
    var ageMin: Int = 0,
    var ageMax: Int = 0
) : BaseModel()

@Entity
data class WalkthroughStep constructor(
    var step: String = "",
    var shown: Boolean = false
) : BaseModel()

@Entity
data class Person constructor(
    var sex: String = "",
    var name: String = "",
    var age: Int = 0,
    var approved: Boolean = false,
    var active: Boolean = false,
    var fantasy: String = "",
    @Convert(converter = StringListJsonConverter::class, dbType = String::class)
    var stories: List<String> = listOf()
) : BaseModel()
