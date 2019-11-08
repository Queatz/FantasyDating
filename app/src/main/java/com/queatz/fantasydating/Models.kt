package com.queatz.fantasydating

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
    @Convert(converter = PersonStoryListJsonConverter::class, dbType = String::class)
    var stories: List<PersonStory> = listOf()
) : BaseModel()

@Entity
class Message constructor(
    var from: String = "",
    var to: String = "",
    var message: String? = null,
    var attachment: String? = null
) : BaseModel()

class PersonStory constructor(
    val story: String = "",
    val photo: String = ""
)