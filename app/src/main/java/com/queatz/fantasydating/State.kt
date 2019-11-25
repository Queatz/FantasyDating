package com.queatz.fantasydating

import com.queatz.fantasydating.features.MyProfileFeature
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class State constructor(private val on: On) : OnLifecycle {

    private val disposables = CompositeDisposable()
    private val changes = PublishSubject.create<StateChange>()

    override fun off() {
        disposables.dispose()
    }

    fun observe(vararg states: Area, callback: State.(StateChange) -> Unit) {
        disposables.add(changes
            .let {
                if (states.isEmpty()) it else it.filter { states.contains(it.area) }
            }
            .startWith(StateChange(Area.Initial, current))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback(this, it)
            })
    }

    fun <T : Any> changed(stateChange: StateChange, value: StateSnapshot.() -> T) = value(current) != value(stateChange.previous)

    var person get() = current.person
        set(value) {
            val previous = current
            current = current.copy(person = value)
            changes.onNext(StateChange(Area.Person, previous))
        }

    var profile get() = current.profile
        set(value) {
            val previous = current
            current = current.copy(profile = value)
            changes.onNext(StateChange(Area.Profile, previous))
        }

    var ui get() = current.ui
        set(value) {
            if (current.ui == value) {
                return
            }

            val previous = current
            current = current.copy(ui = value)
            changes.onNext(StateChange(Area.Ui, previous))
        }

    var current = StateSnapshot(
        person = PersonState(null),
        profile = ProfileState(on<MyProfileFeature>().myProfile),
        ui = UiState(
            showFantasy = false,
            showEditProfile = false,
            showFeed = true,
            showDiscoveryPreferences = false,
            showCompleteYourProfileButton = false
        )
    )

    enum class Area {
        Initial,
        Person,
        Profile,
        Ui
    }
}

data class StateChange constructor(
    val area: State.Area,
    val previous: StateSnapshot
)

data class StateSnapshot constructor(
    val person: PersonState,
    val profile: ProfileState,
    val ui: UiState
)

data class UiState internal constructor(
    val showFantasy: Boolean,
    val showEditProfile: Boolean,
    val showFeed: Boolean,
    val showDiscoveryPreferences: Boolean,
    val showCompleteYourProfileButton: Boolean
)

data class PersonState internal constructor(
    val current: Person?
)

data class ProfileState internal constructor(
    val me: Person
)