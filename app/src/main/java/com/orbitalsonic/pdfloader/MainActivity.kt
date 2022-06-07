package com.orbitalsonic.pdfloader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.orbitalsonic.pdfloader.adapter.AdapterFilesLoader
import com.orbitalsonic.pdfloader.databinding.ActivityMainBinding
import com.orbitalsonic.pdfloader.interfaces.OnDialogPermissionClickListener
import com.orbitalsonic.pdfloader.interfaces.OnItemClickListener
import com.orbitalsonic.pdfloader.utils.Constants.STORAGE_PERMISSION
import com.orbitalsonic.pdfloader.utils.DialogUtils
import com.orbitalsonic.pdfloader.viewmodel.LoaderViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var loaderViewModel: LoaderViewModel
    private lateinit var mAdapter: AdapterFilesLoader
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        initViewModel()
        createLoaderRecyclerView()

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){
                getFiles()
            }else{
                showPermissionDialog()
            }
        } else {
            if (checkReadWritePermission()) {
                getFiles()
            }else{
                requestStoragePermission()
            }
        }

    }

    private fun getFiles(){
        loaderViewModel.fetchPdfFiles(0){
            submitList()
        }
    }

    private fun submitList(){
        if (!loaderViewModel.isFilesListEmpty()){
            binding.loadingProgressBar.visibility = View.GONE
            binding.noFilesLayout.visibility = View.GONE
            mAdapter.submitList(loaderViewModel.fileList)

        }else{
            binding.loadingProgressBar.visibility = View.GONE
            binding.noFilesLayout.visibility = View.VISIBLE
            mAdapter.submitList(loaderViewModel.fileList)
        }
    }

    private fun initViewModel(){
        loaderViewModel = ViewModelProvider(this).get(LoaderViewModel::class.java)

    }

    private fun createLoaderRecyclerView() {
        mAdapter = AdapterFilesLoader()
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = GridLayoutManager(this, 3)

        mAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                showMessage(mAdapter.currentList[position].pdfFilePath.absolutePath)
            }

        })


    }

    private fun checkReadWritePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestExternalStorageManager(){
        try {
            val mIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            mIntent.addCategory("android.intent.category.DEFAULT")
            mIntent.data = Uri.parse(String.format("package:%s", packageName))
            openActivityForResult(mIntent)
        } catch (e: Exception) {
            val mIntent = Intent()
            mIntent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            openActivityForResult(mIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (STORAGE_PERMISSION==requestCode){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getFiles()
            } else {
                showMessage("Permission Denied!")
            }
        }

    }


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()){
               getFiles()
            }else{
                showPermissionDialog()
            }
        }
    }

    private fun openActivityForResult(mIntent:Intent) {
        resultLauncher.launch(mIntent)
    }

    private fun showMessage(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionDialog() {

        DialogUtils.permissionDialog(
            this,
            object : OnDialogPermissionClickListener {
                override fun onDiscardClick() {
                }

                @RequiresApi(Build.VERSION_CODES.R)
                override fun onProceedClick() {
                    requestExternalStorageManager()
                }

            })

    }

}