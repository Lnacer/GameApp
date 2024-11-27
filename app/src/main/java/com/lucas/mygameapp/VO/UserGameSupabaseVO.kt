package com.lucas.mygameapp.VO

import java.util.Date

class UserGameSupabaseVO (var game_id : Int) {
    var id : Int? = null
    var status : String? = null
    var start_playing : Date? = null
    var stop_playing : Date? = null
    var playing_time : Int? = null
    var progress : Int? = null
    var platform : String? = null
    var rating : Float? = null
    var created_date : Date? = null
    var game : GameSupabaseVO? = null
}