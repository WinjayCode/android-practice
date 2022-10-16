package com.winjay.practice.ui.drawable

import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.ActivityDrawableBinding

/**
 * Drawable
 *
 * @author Winjay
 * @date 2022-06-25
 */
class DrawableActivity : BaseActivity() {
    private lateinit var binding: ActivityDrawableBinding

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = ActivityDrawableBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 根据等级切换drawable
        binding.levelListDrawable.setImageLevel(1)
        binding.levelListDrawable.background.level = 1

        // startTransition和reverseTransition实现淡入淡出以及它的逆过程
        val transitionDrawable = binding.transitionDrawable.background as TransitionDrawable
        transitionDrawable.startTransition(5000)
//        transitionDrawable.reverseTransition(5000)

        // 缩放level的等级必须为0~10000
        val scaleDrawable = binding.scaleDrawable.background as ScaleDrawable
        scaleDrawable.level = 1

        // 裁剪level范围0~10000，0表示完全裁剪，即不可见，10000表示不裁剪
        val clipDrawable = binding.clipDrawable.drawable as ClipDrawable
        clipDrawable.level = 8000

        // custom drawable
        val customDrawable = CustomDrawable(Color.BLUE)
        binding.customDrawable.background = customDrawable
    }
}