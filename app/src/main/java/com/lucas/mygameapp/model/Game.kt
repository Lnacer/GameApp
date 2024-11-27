package com.lucas.mygameapp.model
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lucas.mygameapp.database.DateConverter
import java.io.ByteArrayOutputStream
import java.sql.Blob
import java.util.Date


@Entity
data class Game(
    @ColumnInfo(name = "name") val name : String,
    @PrimaryKey @ColumnInfo(name = "game_id") val id: Int)
    : Parcelable {

    @ColumnInfo(name = "cover_image")
    private var _coverBlob : ByteArray? = null

    @ColumnInfo(name = "game_summary")
    var summary : String? = null
    @ColumnInfo(name = "game_release_date")
    var releaseDate : Date? = null
    @ColumnInfo(name = "game_publishers")
    var publishers : List<String> = emptyList()
    @ColumnInfo(name = "game_developers")
    var developers : List<String> = emptyList()
    @ColumnInfo(name = "game_genres")
    var genres : List<String> = emptyList()
    @ColumnInfo(name = "game_platforms")
    var platforms : List<String> = emptyList()
    @Ignore
    var coverUrl : String? = null
    @Ignore
    var coverBitmap : Bitmap? = null

    var coverBlob : ByteArray?
        get() = this._coverBlob
        set(value) {
            if (value == null) {
                coverBitmap = null
            }
            else if (_coverBlob == null || !_coverBlob.contentEquals(value)) {
                coverBitmap = BitmapFactory.decodeByteArray(value, 0, value.size)
            }

            _coverBlob = value
        }

    constructor(parcel: Parcel) : this(parcel.readString()!!, parcel.readInt()) {
        coverUrl = parcel.readString()

        _coverBlob = parcel.readBlob()
        if (_coverBlob != null) {
            coverBitmap = BitmapFactory.decodeByteArray(_coverBlob, 0, _coverBlob?.size!!)
        }

        summary = parcel.readString()
        releaseDate = parcel.readSerializable(Date::class.java.classLoader, Date::class.java)

        val mutablePublishers = mutableListOf<String>()
        parcel.readList(mutablePublishers, List::class.java.classLoader, String::class.java)
        publishers = mutablePublishers

        val mutableDevelopers = mutableListOf<String>()
        parcel.readList(mutableDevelopers, List::class.java.classLoader, String::class.java)
        developers =  mutableDevelopers

        val mutablePlatforms = mutableListOf<String>()
        parcel.readList(mutablePlatforms, List::class.java.classLoader, String::class.java)
        platforms = mutablePlatforms

        val mutableGenres = mutableListOf<String>()
        parcel.readList(mutableGenres, List::class.java.classLoader, String::class.java)
        genres = mutableGenres
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeString(name)
        out.writeInt(id)

        out.writeString(coverUrl)
        if (_coverBlob != null) {
            out.writeBlob(_coverBlob)
        }
        else if (coverBitmap != null) {
            val bos = ByteArrayOutputStream()
            coverBitmap?.compress(Bitmap.CompressFormat.PNG, 100, bos)
            out.writeBlob(bos.toByteArray())
        }
        out.writeString(summary)
        out.writeSerializable(releaseDate)
        out.writeList(publishers)
        out.writeList(developers)
        out.writeList(platforms)
        out.writeList(genres)
    }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }
}