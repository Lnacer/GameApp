package com.lucas.mygameapp.VO

class GameVO {
    var id : Int? = null
    var name : String? = null
    var first_release_date : Int? = null
    var genres : List<GenreVO> = emptyList()
    var platforms : List<PlatformVO> = emptyList()
    var summary : String? = null
    var involved_companies : List<CompanyVO> = emptyList()
    var cover : SampleCoverVO? = SampleCoverVO()
}