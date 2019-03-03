package tech.twentytwobits.aplicacinclima

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import tech.twentytwobits.aplicacinclima.ApiResponse.ApiResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    // Inicializar las variables que manejaran el layout
    var tvCiudad: TextView? = null
    var tvDescripcion: TextView? = null
    var tvGrados: TextView? = null
    var tvEstado: TextView? = null
    var tvMinima: TextView? = null
    var tvMaxima: TextView? = null
    var tvHumedadResultado: TextView? = null
    var tvVientoResultado: TextView? = null
    var tvPresionResultado: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ciudad por defecto
        val city: String = "comayagua"

        // URL de OpenWeatherApi
        val url: String = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=10bc1fcd8615b888c89f52126f7e90c7&lang=es&units=metric"

        // Obtener la fecha y hora desde el dispositivo
        tvDescripcion = findViewById(R.id.tvDescripcion)
        var answer = getDateAndTime()

        // Verificar si existe conexion a Internet
        if (Network.verificarConexion(this)) {
            solicitudHTTPVolley(url)
        } else {
            Toast.makeText(this, "No tienes conexion a Internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun solicitudHTTPVolley(url: String) {
        // Instanciar la cola de peticiones
        val queue = Volley.newRequestQueue(this)

        // Obtener un string de respuesta desde la URL solicitada
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                Log.d("HTTPVolley", response)
                // Toast.makeText(this, "Conexion establecida", Toast.LENGTH_SHORT).show()
                jsonToObject(response);
            },
            Response.ErrorListener {
                Log.d("HTTPVolley", "Error en la URL $url")
                Toast.makeText(this, "¡Ha ocurrido un error en la conexión!", Toast.LENGTH_SHORT).show()
            }
            )

        // Agregar la peticion a la cola de peticiones
        queue.add(stringRequest)
    }

    fun jsonToObject(response: String) {
        // Inicializar un objeto de tipo Gson
        val gson = Gson()
        val apiResponse = gson.fromJson(response, ApiResponse::class.java)

        if (apiResponse.cod == 200) {
            Toast.makeText(this, "¡Datos de la ciudad obtenidos satisfactoriamente!", Toast.LENGTH_SHORT).show()
            // Mapear las variables a las vistas del layout
            tvCiudad = findViewById(R.id.tvCiudad)
            tvGrados = findViewById(R.id.tvGrados)
            tvEstado = findViewById(R.id.tvEstado)
            tvMinima = findViewById(R.id.tvMinima)
            tvMaxima = findViewById(R.id.tvMaxima)
            tvHumedadResultado = findViewById(R.id.tvHumedadResultado)
            tvVientoResultado = findViewById(R.id.tvVientoResultado)
            tvPresionResultado = findViewById(R.id.tvPresionResultado)

            // Asignar los valores de la API obtenidos por Gson
            tvCiudad?.text = "${apiResponse.name}, ${apiResponse.sys.country}"
            tvGrados?.text = "${apiResponse.main.temp.toInt().toString()}${getString(R.string.temperatura)}"
            tvEstado?.text = apiResponse.weather.get(0).description.capitalize()
            tvMinima?.text = "${getString(R.string.minima)} ${apiResponse.main.temp_min.toInt().toString()}"
            tvMaxima?.text = "${getString(R.string.maxima)} ${apiResponse.main.temp_max.toInt().toString()}"
            tvHumedadResultado?.text = "${apiResponse.main.humidity.toInt().toString()}${getString(R.string.porcentaje)}"
            tvVientoResultado?.text = "${apiResponse.wind.speed.toInt().toString()} ${getString(R.string.viento)}"
            tvPresionResultado?.text = "${apiResponse.wind.pressure.toInt().toString()} ${getString(R.string.presion)}"
        } else if (apiResponse.cod == 404) {
            Toast.makeText(this, "Los datos para la ciudad no han podido ser obtenidos", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al momento de obtener los datos solicitados...", Toast.LENGTH_SHORT).show()
        }
    }

    fun getDateAndTime(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val locale = Locale(getString(R.string.localeLanguage), getString(R.string.localeCountry))
            val formatter = DateTimeFormatter.ofPattern("hh:mm a | EEEE, dd LLLL yyyy", locale);
            val answer: String = current.format(formatter)
            Log.d("FECHA", answer)
            return answer
        } else {
          return "nada"
        }
    }
}
