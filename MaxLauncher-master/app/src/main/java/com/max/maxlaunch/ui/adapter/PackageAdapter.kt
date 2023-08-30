package com.max.maxlaunch.ui.adapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.max.maxlaunch.databinding.AppItemLayoutBinding
import com.max.maxlaunch.ui.AppInfo
import com.max.maxlaunch.ui.HomeViewModel
import java.lang.Exception


class PackageAdapter(context: Context) : RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {
      var packageList: ArrayList<AppInfo> = ArrayList()

    val mctx = context
   init {
       loadApps()
    }
    private fun loadApps() {
        packageList.clear() // Xóa danh sách ứng dụng trước khi tải mới
        val pm: PackageManager = mctx.packageManager // Lấy PackageManager từ Context

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER) // Tạo một Intent để lấy danh sách các ứng dụng chính

        val allApps = pm.queryIntentActivities(i, 0) // Truy vấn danh sách các ứng dụng
        for (ri in allApps) {
            // Duyệt qua từng ResolveInfo (ứng dụng)
            val app =AppInfo(
                ri.loadLabel(pm), // Tên ứng dụng
                ri.activityInfo.packageName, // Tên gói (package) của ứng dụng
                ri.activityInfo.loadIcon(pm)) // Biểu tượng của ứng dụng
            packageList.add(app) // Thêm thông tin ứng dụng vào danh sách
        }
        packageList.sortBy {
            it.label.toString() // Sắp xếp danh sách theo tên ứng dụng
        }
    }
    inner class PackageViewHolder(val binding: AppItemLayoutBinding) : RecyclerView.ViewHolder(
            binding.root//được sử dụng để hiển thị và quản lý các mục trong danh sách khi được hiển thị trên màn hình.
                        // Lớp này giúp tối ưu hóa hiệu suất của ứng dụng và cải thiện quản lý dữ liệu hiển thị.
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        //tạo một ViewHolder mới dựa trên layout mà đã thiết kế cho mỗi mục trong danh sách
        val binding = AppItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), //LayoutInflater được sử dụng để chuyển đổi layout XML thành các đối tượng View, lấy context từ viewgroup cha
                parent,
                false
        )
        return PackageViewHolder(binding) // Trả về một PackageViewHolder mới với layout đã inflate
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        // Gán thông tin từ danh sách ứng dụng vào các thành phần giao diện của ViewHolder
        holder.binding.appname.text = packageList[position].label
        holder.binding.icon.setImageDrawable(packageList[position].icon)
        // khi click
        holder.binding.root.setOnClickListener {
            // tạo một Intent để khởi chạy ứng dụng được chọn
            val launchIntent =
                mctx.packageManager.getLaunchIntentForPackage(packageList.get(position).packageName.toString()) //Phương thức này trả về một Intent để khởi chạy ứng dụng
            try {
               mctx.startActivity(launchIntent) // chạy Intent
            } catch (e:Exception)
            {
               Toast.makeText(mctx,"App Not Installed or Uninstalled Recently",Toast.LENGTH_LONG).show()
               loadApps() // Tải lại danh sách ứng dụng
               notifyDataSetChanged() // Cập nhật giao diện
            }

        }
        //khi giữ vào app
        holder.binding.root.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_DELETE)//thông báo cho hệ thống muốn gỡ ứng dụng
            intent.data = Uri.parse("package:"+packageList.get(position).packageName)//đặt dữ liệu (data) cho Intent với thông tin về gói (package) của ứng dụng muốn gỡ cài đặt
            mctx.startActivity(intent)//yêu cầu hệ thống mở ứng dụng cài đặt và xóa
            return@setOnLongClickListener true //đã được xử lý và không cần chuyển tiếp sự kiện này lên các tầng xử lý sự kiện khác
        }

    }

    override fun getItemCount(): Int {
        return packageList.size // Trả về số lượng mục trong danh sách
    }
}