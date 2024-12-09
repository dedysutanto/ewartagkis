package org.gkiserpong.ewartagkis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_jadwal_ibadah1.*
import org.gkiserpong.ewartagkis.adapter.JadwalIbadahAdapter
import org.gkiserpong.ewartagkis.adapter.JsonListAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class JadwalPendeta1Activity : AppCompatActivity() {

    private var arrayList_details = ArrayList<HashMap<String, String>>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal_pendeta1)


        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.title_activity_jadwal_pendeta)
        actionBar.setDisplayHomeAsUpEnabled(true)

        loader.visibility = View.VISIBLE

        val URIAddress = "https://ewarta.gkiserpong.org/wp-content/json/jadwalpendeta.json"

        try {
            FileLoader.deleteWith(this).fromDirectory("json", FileLoader.DIR_INTERNAL).deleteFiles(URIAddress);
        } catch (e: Exception) {
            e.printStackTrace();
        }

        getJSON(URIAddress)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getJSON(URIAddress: String) {

        FileLoader.with(this)
            .load(URIAddress)
            .fromDirectory("json", FileLoader.DIR_INTERNAL)
            .asFile(object: FileRequestListener<File> {
                @Override
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    loader.visibility = View.GONE
                    val jsonFile =  p1!!.body
                    parseJSON(jsonFile.readText())

                }

                override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                    //Toast.makeText(this@JadwalPendeta1Activity, "Sorry Jadwal Pendeta: " + p1!!.message, Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@JadwalPendeta1Activity, "Use local json", Toast.LENGTH_SHORT).show()
                    loader.visibility = View.GONE
                    val jsonStream = applicationContext.assets.open("jadwalpendeta.json")
                    parseJSON(jsonStream.bufferedReader().readText())
                }
            })

    }

    fun parseJSON(result: String?) {
        val jsonObj = JSONObject(result!!)

        var json_array: JSONArray = jsonObj.getJSONArray("info")
        var size:Int = json_array.length()
        arrayList_details = ArrayList()

        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE

        for(i in 0..size-1) {
            var jsonObjectdetail = json_array.getJSONObject(i)
            var map = HashMap<String, String>()
            map["id"] = jsonObjectdetail.getString("id")
            map["title"] = jsonObjectdetail.getString("title")
            map["time"] = jsonObjectdetail.getString("time")
            arrayList_details.add(map)
        }

        findViewById<ListView>(R.id.listView).adapter =
            JsonListAdapter(
                this@JadwalPendeta1Activity,
                arrayList_details
            )


    }
}
