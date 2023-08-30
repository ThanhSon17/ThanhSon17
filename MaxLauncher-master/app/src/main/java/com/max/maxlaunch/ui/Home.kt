package com.max.maxlaunch.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.max.maxlaunch.R
import com.max.maxlaunch.databinding.ActivityHomeBinding
import com.max.maxlaunch.ui.adapter.PackageAdapter


class Home : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //kiểm tra xem ứng dụng có quyền truy cập danh bạ (READ_CONTACTS) hay không.
        // Nếu quyền chưa được cấp (PERMISSION_GRANTED),
        // thì ứng dụng sẽ yêu cầu cấp quyền từ người dùng thông qua phương thức requestPermissions.
        //requestPermissions là một mảng chứa danh sách quyền cần được yêu cầu,
        // trong trường hợp này chỉ có một quyền là Manifest.permission.READ_CONTACTS.
        // Đối số thứ hai là một mã requestCode, ở đây là 999,
        // mà bạn có thể sử dụng sau này để xác định xem yêu cầu quyền nào đã được xử lý.
        if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
            requestPermissions(
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    999)
        //gắn kết giao diện người dùng của hoạt động với giao diện
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Dòng đầu tiên tạo một đối tượng ViewModelFactory
        // và chuyển đối tượng Application vào để có thể sử dụng trong quá trình tạo ViewModel.
        val viewModelProviderFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(HomeViewModel::class.java)

        //sử dụng ViewModelProvider để tạo một thể hiện của HomeViewModel.
        // Điều này giúp bạn quản lý trạng thái và dữ liệu của giao diện người dùng một cách hiệu quả.
       viewModel.date.observe(this, androidx.lifecycle.Observer {
           binding.date.text = it
       })

        //để theo dõi thay đổi trong dữ liệu liên quan đến pin
        //observe để theo dõi sự thay đổi trong biến chargingOrNot trong HomeViewModel
        //Khi giá trị của chargingOrNot thay đổi, mã bên trong khối Observer sẽ được thực thi.
        viewModel.chargingOrNot.observe(this, Observer {
            when(it)
            {
                //BatteryManager.BATTERY_STATUS_CHARGING,
                // hình ảnh của batteryindicator sẽ được cập nhật để hiển thị biểu tượng sạc.
                BatteryManager.BATTERY_STATUS_CHARGING->
                    binding.batteryindicator.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.noun_battery_charge, theme))
            }

        })
        //sử dụng phương thức observe để theo dõi sự thay đổi
        // trong biến batteryInfo trong HomeViewModel
        viewModel.batteryInfo.observe(this, androidx.lifecycle.Observer { level ->
            when (level) {
                in 1..15 -> {
                    binding.battery.text = String.format("%s%%", level)
                    binding.battery.setTextColor(Color.RED)
                    binding.batteryindicator.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.noun_battery_1015, theme))
                }
                in 15..40 -> {
                    binding.battery.text = String.format("%s%%", level)
                    binding.batteryindicator.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.noun_battery_2040, theme))

                }
                in 40..60 -> {
                    binding.battery.text = String.format("%s%%", level)
                    binding.batteryindicator.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.noun_battery_4060, theme))

                }
                in 60..80 -> {
                    binding.battery.text = String.format("%s%%", level)
                    binding.batteryindicator.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.noun_battery_6080, theme))

                }
                in 80..100 -> {
                    binding.battery.text = String.format("%s%%", level)
                    binding.batteryindicator.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.noun_battery_full, theme))

                }
            }
        })

        //Dòng này đặt thuộc tính "hasFixedSize" của RecyclerView được tham chiếu qua biến appDrawer trong tệp layout
        // giúp cải thiện hiệu suất bằng cách xác định rằng kích thước của các mục trong RecyclerView không thay đổi.
        binding.appDrawer.setHasFixedSize(true)
        //đặt LayoutManager của RecyclerView để sắp xếp các mục trong lưới (grid).
        binding.appDrawer.layoutManager = GridLayoutManager(this,4)
        //: Dòng này gắn một Adapter vào RecyclerView.
        // PackageAdapter có thể là một lớp tự định nghĩa dùng để
        // cung cấp dữ liệu cho các mục trong RecyclerView
       binding.appDrawer.adapter = PackageAdapter(this)


        }

    override fun onPause() {
        super.onPause()
        //để hủy đăng ký các BroadcastReceiver đã được đăng ký trước đó trong onResume()
        //Điều này đảm bảo rằng không có thể nghe sự kiện từ các BroadcastReceiver trong khi hoạt động tạm dừng.
        this.unregisterReceiver(viewModel.dateListner)
        this.unregisterReceiver(viewModel.mBatInfoReceiver)

    }

    override fun onResume() {
        super.onResume()
        //, bạn đăng ký lại các BroadcastReceiver để bắt đầu nghe sự kiện từ các nguồn tương ứng,
        // chẳng hạn như sự thay đổi trong trạng thái sạc
        // pin (ACTION_BATTERY_CHANGED) và thời gian (ACTION_TIME_CHANGED).
        this.registerReceiver(viewModel.mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        this.registerReceiver(viewModel.dateListner, IntentFilter(Intent.ACTION_TIME_CHANGED))

    }
}




