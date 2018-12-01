package ajou.hci.atm.location

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TMapApi {
    @GET("tmap/pois/search/around?version=1&appKey=ddc0e991-80ed-4a7c-9cd0-4e14bd6eb2c4")
    fun getPoiInfo(
            @Query("centerLat") centerLat: String,
            @Query("centerLon") centerLon: String): Call<Response>
}