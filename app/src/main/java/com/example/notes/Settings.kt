package com.example.notes

import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator

class Settings : Parcelable {
    var language: String?
    var nightMode: String?

    protected constructor(`in`: Parcel) {
        language = `in`.readString()
        nightMode = `in`.readString()
    }

    constructor(language: String?, nightMode: String?) {
        this.language = language
        this.nightMode = nightMode
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(language)
        parcel.writeString(nightMode)
    }

    companion object {
        @JvmField
        var ENGLISH = "ENGLISH"
        @JvmField
        var RUSSIAN = "RUSSIAN"
        @JvmField
        var NIGHT_MODE_YES = "MODE_NIGHT_YES"
        @JvmField
        var NIGHT_MODE_NO = "MODE_NIGHT_NO"
        @JvmField
        val CREATOR: Creator<Settings?> = object : Creator<Settings?> {
            override fun createFromParcel(`in`: Parcel): Settings? {
                return Settings(`in`)
            }

            override fun newArray(size: Int): Array<Settings?> {
                return arrayOfNulls(size)
            }
        }
    }
}