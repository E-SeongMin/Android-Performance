package com.example.usingcamera

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingPermission() // 권한체크 시작

        btnPicture.setOnClickListener {
            startCapture()
        }

    }

    // 권한설정을 하는 함수
    // TedPermission 이용
    fun settingPermission(){
        var permis = object  : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity, "권한 허가", Toast.LENGTH_SHORT)
                        .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한 거부", Toast.LENGTH_SHORT)
                        .show()
                ActivityCompat.finishAffinity(this@MainActivity) // 권한 거부시 앱 종료
            }
        }

        TedPermission.with(this)
                .setPermissionListener(permis)
                .setRationaleMessage("카메라 사진 권한 필요")
                .setDeniedMessage("카메라 권한 요청 거부")
                .setPermissions(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA)
                .check()
    }

    // Intent를 이용하여 카메라를 호출하는 함수
    // 사진 촬영 버튼을 누를때 실행
    fun startCapture(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImageFile()
                }catch(ex: IOException){
                    null
                }
                photoFile?.also{
                    val photoURI : Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.usingcamera",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    // 사진 찍은 이후 이미지를 파일로 저장해주는 함수
    @Throws(IOException::class)
    private fun createImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
        ).apply{
            currentPhotoPath = absolutePath
        }
    }

    // 이미지뷰에 찍은 사진을 표시
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val file = File(currentPhotoPath)
            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media
                        .getBitmap(contentResolver, Uri.fromFile(file))
                imgPicture.setImageBitmap(bitmap)
            }
            else{
                val decode = ImageDecoder.createSource(this.contentResolver,
                        Uri.fromFile(file))
                val bitmap = ImageDecoder.decodeBitmap(decode)
                imgPicture.setImageBitmap(bitmap)
            }
        }
    }
}