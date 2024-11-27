package com.lucas.mygameapp.model

class Timeline(val year : Int) {
    var playedGamesCount : Int? = null
    var beatenGamesCount : Int? = null

    init {
        if (playedGamesCount == null)
            playedGamesCount = 0

        if (beatenGamesCount == null)
            beatenGamesCount = 0
    }
}