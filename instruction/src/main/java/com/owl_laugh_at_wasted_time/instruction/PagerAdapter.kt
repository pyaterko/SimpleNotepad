package com.example.viewpagerexample

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.owl_laugh_at_wasted_time.instruction.databinding.PageLayoutBinding
import com.owl_laugh_at_wasted_time.instruction.Slide

class DiffCallBack(
    private val oldList: List<Slide>,
    private val newList: List<Slide>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

}

class PagerAdapter() :
    RecyclerView.Adapter<PagerAdapter.PageHolder>() {

    var words: List<Slide> = emptyList()
        set(value) {
            val result = DiffUtil.calculateDiff(DiffCallBack(field, value))
            field = value
            result.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PageLayoutBinding.inflate(inflater, parent, false)
        return PageHolder(parent.context,binding)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        val slide = words[position]
        holder.bind(slide)
    }

    override fun getItemCount(): Int = words.size

    inner class PageHolder(
        val context: Context,
        val view: PageLayoutBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(slide: Slide) {
          view.apply {
              textViewTitle.text=slide.title
              textViewDescriptor.text=slide.description
              card.setBackgroundColor(Color.parseColor(slide.introImage1))
                  Glide.with(imageView)
                      .load(slide.introImageSmall)
                      .transform(
                          com.bumptech.glide.load.resource.bitmap.CenterInside(),
                          RoundedCorners(16)
                      )
                      .transition(DrawableTransitionOptions.withCrossFade())
                      .transform().diskCacheStrategy(DiskCacheStrategy.NONE)
                      .transition(DrawableTransitionOptions.withCrossFade())
                      //   .transition(DrawableTransitionOptions().crossFade(3000))
                      .into(imageView)
          }



            //  view.imageView.setBackgroundColor(Color.GRAY)
        }
    }

}