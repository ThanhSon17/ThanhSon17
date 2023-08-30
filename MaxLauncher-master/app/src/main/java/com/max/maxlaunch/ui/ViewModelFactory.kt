package com.max.maxlaunch.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelFactory(
        val app: Application//khai báo của lớp
) : ViewModelProvider.Factory { //cho phép bạn truyền Application vào lớp ViewModelFactory

    override fun <T : ViewModel?> create(modelClass: Class<T>): T { //yêu cầu một thể hiện của ViewModel từ ViewModelProvider
        return HomeViewModel(app) as T // tạo ra các đối tượng ViewModel bằng cách sử dụng constructor của ViewModel và cung cấp các tham số cần thiết
    }
}
//ViewModelFactory là một lớp được tạo ra để cung cấp các thể hiện của lớp ViewModel trong ứng dụng
//một cách tùy chỉnh để tạo các đối tượng ViewModel và cung cấp các tham số cần thiết cho việc khởi tạo chúng.