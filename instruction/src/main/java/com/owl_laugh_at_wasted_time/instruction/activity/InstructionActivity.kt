package com.owl_laugh_at_wasted_time.instruction.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.owl_laugh_at_wasted_time.instruction.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.databinding.ActivitySlideBinding
import com.owl_laugh_at_wasted_time.instruction.BottomButtonAction
import com.owl_laugh_at_wasted_time.instruction.Slide

class InstructionActivity : AppCompatActivity() {

    val binding by lazy { ActivitySlideBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MySimpleNotepad)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val slides = getSlides()
        setViewPager(slides)
        setButton(slides)
        binding.bottomButton.setListener { action ->
            if (action == BottomButtonAction.POSITIVE) {
                binding.pager.setCurrentItem(getItem(1), true)
            }  else if (action == BottomButtonAction.NEGATIVE) {
                binding.pager.setCurrentItem(getItem(-1), true)
            }
        }
    }

    private fun getSlides(): ArrayList<Slide> {
        return arrayListOf(
            Slide(
                getString(R.string.app_name),
                "#FFFFFFFF",
                R.drawable.stock_photo,
                getString(R.string.tour_listactivity_final_detail)
            ),
            Slide(
                getString(R.string.app_name),
                "#EF9CF2",
                R.drawable.slide2,
                getString(R.string.tour_listactivity_home_detail)
            ),
            Slide(
                getString(R.string.app_name),
                "#FF47EB7C",
                R.drawable.slide3,
                getString(R.string.tour_listactivity_tag_detail)
            ),
            Slide(
                getString(R.string.tour_detailactivity_attachment_title),
                "#FDD835",
                R.drawable.slide4,
                getString(R.string.tour_detailactivity_attachment_detail)
            ),
            Slide(
                getString(R.string.shopping_list_lable),
                "#EA9666",
                R.drawable.slide5,
                getString(R.string.tour_detailactivity_links_detail)
            ),
            Slide(
                getString(R.string.tour_listactivity_final_title),
                "#FFFFFFFF",
                R.drawable.slide6,
                getString(R.string.tour_community)
            )
        )
    }

    private fun setButton(words: ArrayList<Slide>) {
        with(binding) {
          bottomButton.setPositiveButtonText("ДАЛЕЕ")
          bottomButton.hideNegativeButton()
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        words.size - 1 -> {
                            binding.bottomButton.setPositiveButtonText("ГОТОВО")
                            binding.bottomButton.hideNegativeButton()
                            binding.bottomButton.setListener { action ->
                                if (action == BottomButtonAction.POSITIVE) { finish() }
                            }
                        }
                        0 -> {
                            binding.bottomButton.hideNegativeButton()
                        }
                        else -> {
                            binding.bottomButton.showNegativeButton()
                            binding.bottomButton.setPositiveButtonText("ДАЛЕЕ")
                            binding.bottomButton.setNegativeButtonText("НАЗАД")
                        }
                    }
                }
            })
        }

    }

    private fun setViewPager(words: ArrayList<Slide>) {
        val pagerAdapter = PagerAdapter()
        pagerAdapter.words = words
        binding.pager.adapter = pagerAdapter
        binding.pager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, _ ->
            tab.view.isClickable = false
        }.attach()
    }


    private fun getItem(i: Int): Int {
        return binding.pager.currentItem + i
    }
}