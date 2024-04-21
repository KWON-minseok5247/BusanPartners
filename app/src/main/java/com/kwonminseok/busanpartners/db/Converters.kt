
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kwonminseok.busanpartners.data.AuthenticationInformation
import java.time.LocalDate
import java.util.*
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromChipGroup(chipGroup: List<String>?): String? {
        return gson.toJson(chipGroup)
    }

    @TypeConverter
    fun toChipGroup(chipGroupString: String?): List<String>? {
        if (chipGroupString == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(chipGroupString, type)
    }

    @TypeConverter
    fun fromAuthenticationInfo(authInfo: AuthenticationInformation?): String? {
        return gson.toJson(authInfo)
    }

    @TypeConverter
    fun toAuthenticationInfo(authInfoString: String?): AuthenticationInformation? {
        if (authInfoString == null) return null
        return gson.fromJson(authInfoString, AuthenticationInformation::class.java)
    }
}


//class Converters {
//    @TypeConverter
//    fun toDate(value: Long?): Date? {
//        return if (value == null) null else Date(value)
//    }
//
//    @TypeConverter
//    fun toLong(value: Date?): Long? {
//        return value?.time
//    }
//
//    @TypeConverter
//    fun toLocalDate(value: String?): LocalDate? {
//        return if (value == null) null else LocalDate.parse(value)
//    }
//
//    @TypeConverter
//    fun toString(value: LocalDate?): String? {
//        return value?.toString()
//    }
//}