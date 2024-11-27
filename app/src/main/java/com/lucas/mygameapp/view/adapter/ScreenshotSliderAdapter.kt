package com.lucas.mygameapp.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lucas.mygameapp.R

class ScreenshotSliderAdapter(private val context: Context, private var imageList: List<Bitmap>, private val onClickFunction : () -> Unit) : PagerAdapter() {

    override fun getCount() = imageList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =  (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.screenshot_slider_item, null)
        val ivImages = view.findViewById<ImageView>(R.id.imageView)
        val root = view.findViewById<RelativeLayout>(R.id.rlRoot)

        root.setOnClickListener {
            onClickFunction()
        }

        ivImages.setImageBitmap(imageList[position])

        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

    fun setPosition(index: Int) {

    }
}
