package com.lucas.mygameapp.VO

import java.util.Date

class GameSupabaseVO {
    var id : Int? = null
    var name : String? = null
    var first_release_date : Date? = null
    var genres : List<String> = emptyList()
    var platforms : List<String> = emptyList()
    var summary : String? = null
    var publishers : List<String> = emptyList()
    var developers : List<String> = emptyList()
    var cover_url : String? = null
    var cover_blob : String? = null
}