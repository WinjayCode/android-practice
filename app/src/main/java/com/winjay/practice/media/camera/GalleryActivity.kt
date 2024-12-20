package com.winjay.practice.media.camera

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.ActivityGalleryBinding
import com.winjay.practice.utils.LogUtil
import java.io.FileNotFoundException


/**
 * open system gallery
 */
class GalleryActivity : BaseActivity() {
    private lateinit var binding: ActivityGalleryBinding

    val OPEN_GALLERY_REQUEST_CODE: Int = 1

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.openGallery.setOnClickListener {
            pickMediaFromGallery()
        }
    }

    private fun pickMediaFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/* video/*");
        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                data.data?.let {
                    // 检查MIME类型
                    val mimeType = contentResolver.getType(it)
                    LogUtil.d("dwj", "mimeType=${mimeType}")
                    if (mimeType != null) {
                        if (mimeType.startsWith("image")) {
                            // 用户选择了图片
                            LogUtil.d("dwj", "Image selected")
                            // 处理图片
                            val inputStream = contentResolver.openInputStream(it)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.galleryIv.setImageBitmap(bitmap)
                        } else if (mimeType.startsWith("video")) {
                            // 用户选择了视频
                            LogUtil.d("dwj", "Video selected")
                            // 处理视频
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace();
            }
        }
    }
}