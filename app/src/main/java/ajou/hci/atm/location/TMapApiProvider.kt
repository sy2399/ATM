@file:JvmName("LocationUtils")

package ajou.hci.atm.location

import ajou.hci.atm.data.LOCATIONDBHelper
import ajou.hci.atm.models.Location
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val httpClient: OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
}.build()

val provideApi: TMapApi = Retrofit.Builder().apply {
    baseUrl("https://api2.sktelecom.com/")
    client(httpClient)
    addConverterFactory(GsonConverterFactory.create())
}.build().create(TMapApi::class.java)


fun getPoiResult(context: Context, uid: String, time: String, centerLat: String, centerLon: String) {
    val call = provideApi.getPoiInfo(centerLat, centerLon)
    call.enqueue({ response ->
        response.body()?.let {
            Log.i("0zoo PoiTestActivity", it.toString())

            val results = it.searchPoiInfo.pois.poi
            results?.let { pois ->
                val poiName = pois[0].name
                val addrName = ("${pois[0].upperAddrName} ${pois[0].middleAddrName} " +
                        "${pois[0].lowerAddrName} ${pois[0].detailAddrName}").trim()
                val radius = pois[0].radius!!
                val location = Location(time = time, latitude = centerLat, longitude = centerLon,
                        poiName = poiName, addrName = addrName, radius = radius)
                val dbHelper = LOCATIONDBHelper(context, "LOCATION.db", null, 1)
                val date = time.split(" ")[0]
                dbHelper.insert(uid, location)
            }
        }
    }, {
        Log.i("0zoo PoiTestActivity", it.message.toString())
    })
}


@SuppressLint("MissingPermission")
fun insertDB(context: Context, uid: String, time: String) {

    val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    mFusedLocationClient.lastLocation
            .addOnSuccessListener(Activity()) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    Log.i("0zoo PoiTestActivity", location.latitude.toString())
                    Log.i("0zoo PoiTestActivity", location.longitude.toString())
                    val centerLat = location.latitude.toString()
                    val centerLon = location.longitude.toString()
                    getPoiResult(context, uid, time, centerLat, centerLon)
                }
            }
}