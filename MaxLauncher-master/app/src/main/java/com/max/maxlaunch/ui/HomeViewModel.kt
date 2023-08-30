package com.max.maxlaunch.ui

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.BatteryManager
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var dateListner: BroadcastReceiver
    lateinit var mBatInfoReceiver: BroadcastReceiver
    val app: Application = application
    var date: MutableLiveData<String> = MutableLiveData()
    var batteryInfo: MutableLiveData<Int> = MutableLiveData()
    var chargingOrNot: MutableLiveData<Int> = MutableLiveData()

    init {
        setDate()
        initializeBatteryBR()
        initializeDateBR()
    }


    private fun initializeBatteryBR() {
        mBatInfoReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context?, intent: Intent) {
                //cập nhật giá trị của biến batteryInfo với giá trị hiện tại của mức pin trong thiết bị
                batteryInfo.value = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                //cập nhật giá trị của biến chargingOrNot  với trạng thái sạc hoặc không sạc
                chargingOrNot.value = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            }
        }
    }

    private fun initializeDateBR() {
        dateListner = object : BroadcastReceiver() {
            //object : BroadcastReceiver(): Đây là cách để bạn định nghĩa một lớp nội danh (anonymous class) kế thừa từ lớp BroadcastReceiver.
            override fun onReceive(c: Context?, i: Intent?) {
                setDate()//để cập nhật ngày mới.
            }
        }

    }

    fun setDate() {
        val dayFormat = SimpleDateFormat("EE-d-m-yyy", Locale.US) //cho biết định dạng ngày thứ trong tuần, ngày, tháng và năm.
        val calendar: Calendar = Calendar.getInstance()//Tạo một đối tượng Calendar để lấy thời gian hiện tại.
        //ormat được sử dụng để định dạng thời gian từ đối tượng Calendar thành chuỗi theo định dạng đã xác định ở trên.
        date.value = dayFormat.format(calendar.getTime())
    }


}