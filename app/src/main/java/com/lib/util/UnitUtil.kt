package com.lib.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.RectF
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Patterns
import android.util.Size
import com.google.android.gms.maps.model.LatLng
import com.ironraft.pupping.bero.store.SystemEnvironment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import kotlin.math.floor
import kotlin.math.round

fun Date.toLocalDateTime(): LocalDateTime? {
    return this.toInstant()
        .atOffset(ZoneOffset.UTC)
        .toLocalDateTime()
}
fun Date.toLocalDate(): LocalDate? {
    return this.toInstant()
        .atOffset(SystemEnvironment.zoneOffset)
        //.atZone(ZoneId.systemDefault())
        .toLocalDate()
}
fun String.isEmailType():Boolean {
    val pattern: Pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(this).matches()
}

fun String.toDate(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.toDateFormatter(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss", timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}
fun Date.isEqualDay(date:Date): Boolean{
    return this.toDateFormatter("yyyyMMdd") == date.toDateFormatter("yyyyMMdd")
}
fun LocalDate.toDateFormatter(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss"): String? {
    //val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    //return formatter.format(this)
    val date = LocalDateTime.of(this, LocalTime.MIN)
    return date.toFormatString(dateFormat.replace("Z", ""))
}

fun String.onlyNumric() : String {
    return this.filter { it.isDigit() }.reduce { acc, c -> acc.plus(c.digitToInt()) }.toString()
}



@SuppressLint("SimpleDateFormat")
fun String.toDateUtc(
    dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss"
): Date? {
    val simpleDateFormat = SimpleDateFormat(dateFormat)
    return simpleDateFormat.parse(this)
}

fun Date.sinceNow():String {
    val diff = -(this.time/1000.0)
    return diff.since()
}
@SuppressLint("SimpleDateFormat")
fun Date.sinceNowDate(dateFormat:String = "yyyy-MM-dd'T'HH:mm:ssZ",
                      returnFormat:String = "MMMM d, yyyy",
                      isLocale:Boolean = true):String {
    val nowDate = SimpleDateFormat("yyyyMMdd").format(Date()) ?: return ""
    val nowDay = nowDate.toDateUtc("yyyyMMdd") ?: return ""
    val now = nowDay.time
    val me = this.time
    val diff = now - me
    if (diff < 0) {
        return sinceNow()
    } else if (diff < 60 * 60 * 24.0) {
        return "Yesterday"
    }
    if (!isLocale) return SimpleDateFormat(dateFormat).format(this)
    return this.toDateFormatter(returnFormat) //toFormatString(dateFormat)?.toDate(dateFormat)?.toFormatString(returnFormat) ?: ""
}


fun LocalDateTime.toFormatString(
    dateFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ"
): String? {
    return try {
        val pattern = DateTimeFormatter.ofPattern(dateFormat)
        this.format(pattern)
    } catch (e: Exception) {
        null
    }
}



fun Date.toAge(trailing:String = "Y", subTrailing:String = "M", isKr:Boolean = false) : String {
    val now = Date()
    val yy = now.toDateFormatter("yyyy") ?: "0"
    val birthYY = toDateFormatter("yyyy") ?: "0"
    val mm = now.toDateFormatter("MM") ?: "0"
    val birthMM = toDateFormatter("MM") ?: "0"

    val yearDiff = yy.toInt() - birthYY.toInt()
    val monthDiff = mm.toInt() - birthMM.toInt()
    val diff = (yearDiff * 12) + monthDiff

    val age = floor(diff/12.0).toInt()
    return if (isKr) {
        (age + 1).toString() + trailing
    } else {
        if (age > 0) {
            val unit = if(age != 1) trailing else trailing.replace("s", "")
            age.toString() + unit
        } else {
            val unit = if(diff != 1 ) subTrailing else subTrailing.replace("s", "")
            diff.toString() + unit
        }
    }
}

fun String.toDecimalFormat(): String {
    val decimal = round(this.toDouble()).toInt()
    if (decimal > 999) {
        val df = DecimalFormat("#,###")
        return df.format(decimal)
    }
    return decimal.toString()
}

fun String.toFixLength(l:Int, prefix:String = "000000"): String {
    if (length >= l) { return this }
    val fix:String = prefix + this
    return fix.takeLast(l)
}
fun String.toTruncateDecimal (n:Int = 0) : String {
    return String.format("%." + n.toString() + "f",this.toFloatOrNull() ?: 0f)
}

fun String.replace(newString:String): String {
    return replace("%s", newString)
}

fun Double.secToMinString(div:String = ":", fix:Int=2) : String {
    val sec = this.toInt() % 60
    val min = floor( this / 60.0 ).toInt()
    return min.toString().toFixLength(2) + div + sec.toString().toFixLength(fix)
}

fun Double.toDecimal(divid:Double = 1.0 ,f:Int = 0) : String {
    val decimal = if( (this % divid) == 0.0 )  "%.0f" else "%.${f.toString()}f"
    val n = this / divid
    return String.format(decimal, n)
}

fun Double.toThousandUnit(f:Int = 0) : String {
    when {
        this < 1000 -> { return round(this).toInt().toString() }
        this < 100000 -> return "${this.toDecimal(1000.0, f)}K"
        this < 100000000 -> return "${this.toDecimal(100000.0, f)}M"
        else -> return "${this.toDecimal(100000000.0, f)}B"
    }
}

fun Double.millisecToSec() : Double {
    return this/1000.0
}
fun Double.since() : String {
    var value:Double = 0.0
    var unit:String = ""
    if (this < 60.0) return "just now"
    else if (this < 60.0 * 60.0) {
        value = this / 60.0
        unit = "min before"
    }
    else if (this < 60.0 * 60.0 * 24.0) {
        value = this / ( 60.0 * 60.0)
        unit = "hour before"
    }
    else if (this < 60.0 * 60.0 * 24.0 * 30.0){
        value = this / ( 60.0 * 60.0 * 24.0)
        unit = "days before"
    }
    else if (this < 60.0 * 60.0 * 24.0 * 365.0) {
        value = this / ( 60.0 * 60.0 * 24.0 * 30.0)
        unit = "month ago"
    }
    else {
        value = this / ( 60.0 * 60.0 * 24.0 * 365.0)
        unit = "year ago"
    }
    return String.format ("%.0f",  value ) + unit
}
fun Boolean.toggle() : Boolean {
    val prev = this
    return !prev
}

fun Size.getCropRatioSize(crop: Size):RectF{
    val cropRatio = crop.width.toFloat()/crop.height.toFloat()
    val originWidth = width.toFloat()
    val originHeight = height.toFloat()

    var ratioWidth = originWidth
    var ratioHeight = originWidth / cropRatio
    if( ratioHeight > originHeight ){
        ratioHeight = originHeight
        ratioWidth = originHeight * cropRatio
    }
    val marginX = (originWidth - ratioWidth)/2
    val marginY = (originHeight - ratioHeight)/2
    return RectF(marginX, marginY, marginX + ratioWidth, marginY + ratioHeight)
}



val Int.toPx: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()


@SuppressLint("Range")
fun Uri.getAbsuratePathFromUri(context: Context): String {
    var path = ""
    val array = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(this, array, null, null, null)
    cursor?.let {
        it.moveToFirst()
        path = it.getString(it.getColumnIndex(array[0]))
        cursor.close()
    }
    return path
}
fun Uri.getBitmap(context: Context): Bitmap? {
    try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        }
        return bitmap

    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

}
fun LatLng.distance(from:LatLng): Double {
    val results = FloatArray(1)
    try {
        Location.distanceBetween(latitude, longitude, from.latitude, from.longitude, results)
    } catch (e:IllegalArgumentException){
        return 0.0
    }
    return results[0].toDouble()
}
