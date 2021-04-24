package com.cg.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.net.ConnectivityManager
import android.net.Network
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

val PREF_NAME = "locations"
@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), TextWatcher, LocationListener, AdapterView.OnItemClickListener {

    var addressList = mutableListOf<String>()
    var list = mutableListOf<Address>()
    lateinit var adapter : ArrayAdapter<String>
    lateinit var lManager : LocationManager
    var currentLoc : Location? = null
    var currentLat = ""
    var currentLong = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!isOnline()){
            Toast.makeText(this,"This app works only with internet connection. Enable internet and restart app",Toast.LENGTH_LONG).show()
            finish()
        }

        adapter = AdapterNoFilter<String>(this,android.R.layout.simple_list_item_1,addressList)
        actv.threshold = 3
        actv.setAdapter(adapter)
        actv.addTextChangedListener(this@MainActivity)
        actv.onItemClickListener = this

    }

    private fun isOnline() : Boolean{
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo!=null && netInfo.isConnectedOrConnecting

    }


    private fun getInfo(context: Context, location: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val locationName = if(location.length == 0) "a" else location

            val geocoder = Geocoder(context, Locale.getDefault())
            list = geocoder.getFromLocationName(locationName,3)
            CoroutineScope(Dispatchers.Main).launch{
                    for(address in list) {
                    val city = address.locality
                    val country = address.countryName
                    val state = address.adminArea
                    addressList.add("$city, $state, $country")
                        Log.d("Mainactivity","Location: $location, List: $addressList")
                        adapter.notifyDataSetChanged()
                    }
            }
        }


    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            getInfo(this@MainActivity,s.toString())
    }

    override fun afterTextChanged(s: Editable?) {
        addressList.clear()

    }


    fun buttonClick(view: View) {
        checkPermissions()
        actv.setText("")
        lManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val providerList = lManager.getProviders(true)
        var providerName = ""
        getLocation(providerList, providerName)

    }

    private fun getLocation(
            providerList: List<String>,
            providerName: String
    ) {
        var providerName1 = providerName
        if (providerList.isNotEmpty()) {
            if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                providerName1 = LocationManager.GPS_PROVIDER
            } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                providerName1 = LocationManager.NETWORK_PROVIDER
            } else {
                providerName1 = providerList[0]
            }
            Log.d("MainActivityL", "Provider: $providerName1")
            val loc = lManager.getLastKnownLocation(providerName1)

            if (loc != null) {
                updateLocation(loc)
            } else {
                Toast.makeText(this, "No location found, Please enable location", Toast.LENGTH_LONG).show()
            }
            lManager.requestLocationUpdates(providerName1, 1000L, 1000.0f, this)
        } else {
            Toast.makeText(this, "Pls enable location", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateLocation(loc: Location) {
        val latt = loc.latitude
        val longi = loc.longitude

        var latString = latt.toString()
        latString = latString.substring(0,latString.indexOf(".")+4)
        var longString = longi.toString()
        longString = longString.substring(0,longString.indexOf(".")+4)

        currentLoc=loc
        currentLat = latString
        currentLong = longString
        val pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("Latitude",currentLat)
        editor.putString("longitude",currentLong)
        editor.apply()
        val frag = CityWeather()

        actv.setText(getAddress())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.layoutL, frag)
            .commit()

    }
    private fun getAddress(): String {
        val gCoder = Geocoder(this)
        val addressList = gCoder.getFromLocation(currentLoc?.latitude!!,currentLoc?.longitude!!,10)
        if(addressList.isNotEmpty()){
            val addr = addressList[0]
            var strAddress = "${addr.locality}, ${addr.adminArea}, ${addr.countryName}"
            return strAddress
        }
        return ""
    }
    private fun checkPermissions(){
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            //Toast.makeText(this,"Location permission granted",Toast.LENGTH_LONG).show()
        }
    }

    override fun onLocationChanged(location: Location) {
        updateLocation(location)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = list.get(position)
        var latString = item.latitude.toString()
        latString = latString.substring(0,latString.indexOf(".")+4)
        var longString = item.longitude.toString()
        longString = longString.substring(0,longString.indexOf(".")+4)
        currentLat = latString
        currentLong = longString
        val pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("Latitude",currentLat)
        editor.putString("longitude",currentLong)
        editor.apply()
        val frag = CityWeather()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.layoutL, frag)
                .addToBackStack(null)
                .commit()

        Log.d("Mainactivity","Location is: ${item.locality}")
    }


}

class AdapterNoFilter<String>(context:Context, resource:Int, list:MutableList<String>) : ArrayAdapter<String>(context,resource, list){
    val NO_FILTER = NoFilter()
    override fun getFilter(): Filter {
        return NO_FILTER
    }
    inner class NoFilter : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }

    }
}