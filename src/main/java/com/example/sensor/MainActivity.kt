package com.example.sensor

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener{

    val smsAndStoragePermissionHandler: WCRequestPermissionHandler by lazy {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            WCRequestPermissionHandler(this@MainActivity,
                permissions = setOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.VIBRATE
                ),
                listener = object : WCRequestPermissionHandler.Listener {
                    override fun onComplete(
                        grantedPermissions: Set<String>,
                        deniedPermissions: Set<String>
                    ) {
                        //Toast.makeText(this@LoginActivity, "complete", Toast.LENGTH_SHORT).show()
                        /* text_granted.text = "Granted: " + grantedPermissions.toString()
                         text_denied.text = "Denied: " + deniedPermissions.toString()*/
                    }

                    override fun onShowPermissionRationale(permissions: Set<String>): Boolean {
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("To able to Send Photo, we need camera and" + " Storage permission")
                            .setPositiveButton("OK") { _, _ ->
                                smsAndStoragePermissionHandler.retryRequestDeniedPermission()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                smsAndStoragePermissionHandler.cancel()
                                dialog.dismiss()
                            }
                            .show()
                        return true // don't want to show any rationale, just return false here
                    }

                    override fun onShowSettingRationale(permissions: Set<String>): Boolean {
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("Go Settings -> Permission. " + "Make the ${permissions.size} permission  on")
                            .setPositiveButton("Settings") { _, _ ->
                                smsAndStoragePermissionHandler.requestPermissionInSetting()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                smsAndStoragePermissionHandler.cancel()
                                dialog.cancel()
                            }
                            .show()
                        return true
                    }
                })
        } else {
            WCRequestPermissionHandler(this@MainActivity,
                permissions = setOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                ),
                listener = object : WCRequestPermissionHandler.Listener {
                    override fun onComplete(
                        grantedPermissions: Set<String>,
                        deniedPermissions: Set<String>
                    ) {
                        //Toast.makeText(this@LoginActivity, "complete", Toast.LENGTH_SHORT).show()
                        /* text_granted.text = "Granted: " + grantedPermissions.toString()
                         text_denied.text = "Denied: " + deniedPermissions.toString()*/
                    }

                    override fun onShowPermissionRationale(permissions: Set<String>): Boolean {
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("To able to Send Photo, we need camera and" + " Storage permission")
                            .setPositiveButton("OK") { _, _ ->
                                smsAndStoragePermissionHandler.retryRequestDeniedPermission()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                smsAndStoragePermissionHandler.cancel()
                                dialog.dismiss()
                            }
                            .show()
                        return true // don't want to show any rationale, just return false here
                    }

                    override fun onShowSettingRationale(permissions: Set<String>): Boolean {
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("Go Settings -> Permission. " + "Make the ${permissions.size} permission  on")
                            .setPositiveButton("Settings") { _, _ ->
                                smsAndStoragePermissionHandler.requestPermissionInSetting()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                smsAndStoragePermissionHandler.cancel()
                                dialog.cancel()
                            }
                            .show()
                        return true
                    }
                })
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleRequestPermission()
        btn_location.setOnClickListener(this)
        btn_flash.setOnClickListener(this)
        btn_gyroscope.setOnClickListener(this)
        btn_motion.setOnClickListener(this)
        btn_vibration.setOnClickListener(this)

    }

    private fun handleRequestPermission() {
        smsAndStoragePermissionHandler.requestPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsAndStoragePermissionHandler.onRequestPermissionsResult(
            requestCode, permissions,
            grantResults
        )
    }

    fun statusCheck() {
        val manager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            Util.getAddress(this, object : Util.GetAddress {
                override fun getAddress(
                    fullAddress: String?,
                    doorNumber: String?,
                    addressLineOne: String?,
                    addressLineTwo: String?,
                    city: String?,
                    district: String?,
                    state: String?,
                    pinCode: String?,
                    countryName: String?
                ) {
                    tv_location.text = """$doorNumber,$addressLineOne,
                        |$addressLineTwo,$city,
                        |$district,$state,
                        |$countryName,$pinCode
                    """.trimMargin()
                }
            })
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("For a better Experience ,turn on the Device Location")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, id ->
                run {
                    dialog.cancel()
                }
            }
        val alert = builder.create()
        alert.show()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_flash->{
                val gyro = getGyroscope()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv_flash.text = """Max Event Count: ${gyro?.fifoMaxEventCount.toString()},
                        |Highest Directional report${gyro?.highestDirectReportRateLevel.toString()},
                        |Maximum Delay:${gyro?.maxDelay.toString()},
                        |Minimum Delay:${gyro?.minDelay.toString()},
                        |Maximum Range:${gyro?.maximumRange.toString()},
                        |Power:${gyro?.power.toString()}
                        |Resolution:${gyro?.resolution.toString()}}""".trimMargin()
                }
            }
            R.id.btn_gyroscope->{
                val gravity = getGravitySensor()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tv_gyroscope.text = """Max Event Count:${gravity?.fifoMaxEventCount.toString()}
                            |Highest Direction report:${gravity?.highestDirectReportRateLevel.toString()},
                            |Maximum Delay:${gravity?.maxDelay.toString()},
                            |Minimum Delay:${gravity?.minDelay.toString()},
                            |Maximum Range:${gravity?.maximumRange.toString()},
                            |Power:${gravity?.power.toString()},
                            |Resolution:${gravity?.resolution.toString()}
                        """.trimMargin()
                    }

                }
            }
            R.id.btn_location->{
                statusCheck()
            }
            R.id.btn_motion->{
                val accelerometer = getAcceleroMeter()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tv_motion.text = """Reservered Event Count: ${accelerometer?.fifoReservedEventCount.toString()}
                            |Resolution: ${accelerometer?.resolution.toString()},
                            |Power:${accelerometer?.power.toString()},
                            |Min Delay:${accelerometer?.minDelay.toString()},
                            |Max Delay:${accelerometer?.maxDelay.toString()},
                            |Maximum Range:${accelerometer?.maximumRange.toString()}
                        """.trimMargin()
                    }
                }
            }
            R.id.btn_vibration->{
                vibrationNotification()
            }
        }
    }

    fun vibrationNotification(){
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tv_vibration.text = "Vibrating, please click the test button again"
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(300)
        }
    }

    fun getGravitySensor():Sensor?{
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    }

    fun getAcceleroMeter():Sensor?{
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun getGyroscope(): Sensor? {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }
}