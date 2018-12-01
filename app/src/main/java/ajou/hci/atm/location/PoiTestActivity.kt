package ajou.hci.atm.location

import ajou.hci.atm.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_poi_test.*

class PoiTestActivity : AppCompatActivity() {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poi_test)

        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var centerLat = "37.282036"
        var centerLon = "127.045201"

        mFusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        Log.i("PoiTestActivity", location.latitude.toString())
                        Log.i("PoiTestActivity", location.longitude.toString())
                        centerLat = location.latitude.toString()
                        centerLon = location.longitude.toString()


                        Log.i("PoiTestActivity", "!!")
                        val call = provideApi.getPoiInfo(centerLat, centerLon)
                        call.enqueue({ response ->
                            response.body()?.let {
                                Log.i("PoiTestActivity", it.toString())

                                val results = it.searchPoiInfo.pois.poi
                                results?.let { Poi ->
                                    textView.text = Poi.toString()
                                }
                            }
                        }, {
                            Log.i("PoiTestActivity", it.message.toString())
                        })
                    }
                }

    }
}