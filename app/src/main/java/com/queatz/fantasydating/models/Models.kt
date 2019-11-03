package com.queatz.fantasydating.models

import com.queatz.fantasydating.StringListJsonConverter
import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id


@BaseEntity
open class BaseModel {
    @Id var objectBoxId: Long = 0
}

@Entity
class DiscoveryPreferences constructor(
    var who: String,
    var where: String,
    var ageMin: Int,
    var ageMax: Int
) : BaseModel()

@Entity
class WalkthroughStep constructor(
    var step: String,
    var shown: Boolean
) : BaseModel()

@Entity
class MyPreferences constructor(
    var sex: String,
    var name: String,
    var age: Int,
    var fantasy: String,
    @Convert(converter = StringListJsonConverter::class, dbType = String::class)
    var stories: List<String>
) : BaseModel()
