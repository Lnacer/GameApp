package com.lucas.mygameapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class UserGame(@Embedded var game : Game?) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "usergame_id")
    var id: Int? = null
    @ColumnInfo(name = "game_status")
    var status : GameStatus? = null
    @ColumnInfo(name = "user_game_start_play_on")
    var startPlayingOn : Date? = null
    @ColumnInfo(name = "user_game_stop_play_on")
    var stopPlayingOn : Date? = null
    @ColumnInfo(name = "user_game_play_time")
    var playingTime : Int? = null
    @ColumnInfo(name = "user_game_progress")
    var progress : Int? = null
    @ColumnInfo(name = "user_game_platform")
    var platform : String? = null
    @ColumnInfo(name = "user_game_rating")
    var rating : Float? = null
    @ColumnInfo(name = "user_game_created_date")
    var createdDate : Date? = null
    @ColumnInfo(name = "user_game_game_id")
    var gameId: Int? = null

    constructor(parcel: Parcel) : this(parcel.readParcelable(Game::class.java.classLoader, Game::class.java) as Game) {
        id = parcel.readInt()
        val parcelStatus = parcel.readString()
        if (parcelStatus != null) {
            status = GameStatus.byName(parcelStatus)
        }
        startPlayingOn = parcel.readSerializable(Date::class.java.classLoader, Date::class.java)
        stopPlayingOn = parcel.readSerializable(Date::class.java.classLoader, Date::class.java)

        playingTime = getIntNullableParcel(parcel)
        progress = getIntNullableParcel(parcel)
        rating = getFloatNullableParcel(parcel)

        platform = parcel.readString()
        createdDate = parcel.readSerializable(Date::class.java.classLoader, Date::class.java)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(game, flags)
        if (id != null) {
            parcel.writeInt(id!!)
        }
        if (status?.printableName != null) {
            parcel.writeString(status?.printableName)
        }

        parcel.writeSerializable(startPlayingOn)
        parcel.writeSerializable(stopPlayingOn)
        parcel.writeInt(playingTime ?: -1)
        parcel.writeInt(progress ?: -1)
        parcel.writeFloat(rating ?: -1f)
        parcel.writeString(platform)
        parcel.writeSerializable(createdDate)
    }

    companion object CREATOR : Parcelable.Creator<UserGame> {
        override fun createFromParcel(parcel: Parcel): UserGame {
            return UserGame(parcel)
        }

        override fun newArray(size: Int): Array<UserGame?> {
            return arrayOfNulls(size)
        }
    }

    private fun getIntNullableParcel(parcel : Parcel) : Int? {
        var intParcel = parcel.readInt()

        return if (intParcel != -1) intParcel else null
    }

    private fun getFloatNullableParcel(parcel : Parcel) : Float? {
        var floatParcel = parcel.readFloat()

        return if (floatParcel != -1f) floatParcel else null
    }
}
