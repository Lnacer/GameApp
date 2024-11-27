package com.lucas.mygameapp.VO

import java.util.Date
import kotlin.properties.Delegates

class UserVO {
    var id : Int? = null
    lateinit var name : String
    lateinit var email : String
    var access_token : String? = null
    var picture : String? = null
    var picture_path : String? = null
    var created_date : Date? = null
}