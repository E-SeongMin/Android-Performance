package com.example.pagesliding

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SlidingPaneLayout
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var isPageOpen : Boolean = false
    lateinit var LeftAnim : Animation
    lateinit var RightAnim : Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left)
        RightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right)

        LeftAnim.setAnimationListener(SlidingPageAnimationListener())
        RightAnim.setAnimationListener(SlidingPageAnimationListener())

        button.setOnClickListener {
            if(isPageOpen) {
                page.startAnimation(RightAnim)
            } else {
                page.visibility = View.VISIBLE
                page.startAnimation(LeftAnim)
            }
        }
    }

    private inner class SlidingPageAnimationListener : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            if(isPageOpen) {
                page.visibility = View.INVISIBLE
                button.text = "OPEN"
                isPageOpen = false
            } else {
                button.text = "CLOSE"
                isPageOpen = true
            }
        }

        override fun onAnimationRepeat(p0: Animation?) {

        }

    }
}