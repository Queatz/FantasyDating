package com.queatz.fantasydating

import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*


@BaseEntity
open class BaseModel {
    @Id var objectBoxId: Long = 0
    var id: String? = null
}

@Entity
data class Token constructor(var token: String = "") : BaseModel()

@Entity
data class Phone constructor(var token: String = "", var synced: Boolean = false) : BaseModel()

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
data class Event constructor(
    var person: String = "",
    var created: Date? = null,
    var name: String = "",
    var data: String = ""
) : BaseModel()

@Entity
data class Person constructor(
    var sex: String = "",
    var name: String = "",
    var age: Int = 0,
    var approved: Boolean = false,
    var active: Boolean = false,
    var fantasy: String = "",
    var youLove: Boolean = false,
    var lovesYou: Boolean = false,
    @Convert(converter = PersonStoryListJsonConverter::class, dbType = String::class)
    var stories: List<PersonStory> = listOf(),
    var boss: Boolean = false
) : BaseModel()

@Entity
class Message constructor(
    var created: Date? = null,
    var from: String = "",
    var to: String = "",
    var message: String? = null,
    var attachment: String? = null
) : BaseModel()

class PersonStory constructor(
    var story: String = "",
    var photo: String = "",
    var x: Float = .5f,
    var y: Float = .5f
)

