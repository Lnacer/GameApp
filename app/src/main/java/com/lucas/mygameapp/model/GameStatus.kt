package com.lucas.mygameapp.model

import com.lucas.mygameapp.R

enum class GameStatus(val printableName: String, val icon : Int) {
    WANT("Want", R.drawable.bookmark_24px),
    PLAYING("Playing", R.drawable.stadia_controller_24px),
    FINISHED("Beaten", R.drawable.military_tech_24px),
    PAUSED("Paused", R.drawable.pause_circle_24px),
    STOPPED("Stopped", R.drawable.stop_circle_24px);

    companion object {
        private val map = values().associateBy { it.printableName }
        fun byName(statusName: String): GameStatus? = map[statusName]
    }

}