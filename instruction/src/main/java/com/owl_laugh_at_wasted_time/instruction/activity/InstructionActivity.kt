package com.owl_laugh_at_wasted_time.instruction.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.viewpagerexample.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.owl_laugh_at_wasted_time.instruction.R
import com.owl_laugh_at_wasted_time.instruction.databinding.ActivitySlideBinding
import com.owl_laugh_at_wasted_time.instruction.intro.BottomButtonAction
import com.owl_laugh_at_wasted_time.instruction.Slide

class InstructionActivity : AppCompatActivity() {

    val binding by lazy { ActivitySlideBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
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
        val words = arrayListOf(
            Slide(
                getString(R.string.app_name),
                "#FFFFFFFF",
                R.drawable.stock_photo,
                getString(R.string.tour_listactivity_final_detail)
            ),
            Slide(
                getString(R.string.app_name),
                "#71F44336",
                R.drawable.slide2,
                getString(R.string.tour_listactivity_home_detail)
            ),
            Slide(
                getString(R.string.app_name),
                "#8bc34a",
                R.drawable.slide3,
                getString(R.string.tour_listactivity_tag_detail)
            ),
            Slide(
                getString(R.string.tour_detailactivity_attachment_title),
                "#60FDD835",
                R.drawable.slide4,
                getString(R.string.tour_detailactivity_attachment_detail)
            ),
            Slide(
                getString(R.string.shopping_list_lable),
                "#41BB86FC",
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
        return words
    }

    private fun setButton(words: ArrayList<Slide>) {
        with(binding) {
          bottomButton.setPositiveButtonText("ДАЛЕЕ")
          bottomButton.hideNegativeButton()
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position == words.size - 1) {
                        binding.bottomButton.setPositiveButtonText("ГОТОВО")
                        binding.bottomButton.hideNegativeButton()
                        binding.bottomButton.replaceListener { action ->
                            if (action == BottomButtonAction.POSITIVE) { finish() }
                        }
                    } else if (tab?.position == 0) {
                        binding.bottomButton.hideNegativeButton()
                    } else {
                        binding.bottomButton.showNegativeButton()
                        binding.bottomButton.setPositiveButtonText("ДАЛЕЕ")
                        binding.bottomButton.setNegativeButtonText("НАЗАД")
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
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.view.isClickable = false
        }.attach()
    }


    private fun getItem(i: Int): Int {
        return binding.pager.getCurrentItem() + i
    }
}