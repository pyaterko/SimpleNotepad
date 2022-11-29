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


        val words = arrayListOf(

            Slide(
                getString(R.string.app_name),
                true,
                "#FFFFFFFF",
                R.drawable.stock_photo,
                getString(R.string.tour_listactivity_final_detail)
            ),
            Slide(
                getString(R.string.app_name),
                false,
                "#71F44336",
                R.drawable.slide2,
                getString(R.string.tour_listactivity_home_detail)
            ),
            Slide(
                getString(R.string.app_name),
                false,
                "#8bc34a",
                R.drawable.slide3,
                getString(R.string.tour_listactivity_tag_detail)
            ),
            Slide(
                getString(R.string.tour_detailactivity_attachment_title),
                false,
                "#60FDD835",
                R.drawable.slide4,
                getString(R.string.tour_detailactivity_attachment_detail)
            ),
            Slide(
                getString(R.string.shopping_list_lable),
                false,
                "#41BB86FC",
                R.drawable.slide5,
                getString(R.string.tour_detailactivity_links_detail)
            ),
            Slide(
                getString(R.string.tour_listactivity_final_title),
                false,
                "#FFFFFFFF",
                R.drawable.slide6,
                getString(R.string.tour_community)
            )


        )
        setViewPager(words)
        initActivAndHomeButton(words)

        binding.bottomButton.setPositiveButtonText("ДАЛЕЕ")
        binding.bottomButton.hideNegativeButton()

        binding.bottomButton.setListener { action ->
            if (action == BottomButtonAction.POSITIVE &&
                binding.bottomButton.getPositiveButtonText() == "ДАЛЕЕ"
            ) {

                binding.pager.setCurrentItem(getItem(1), true)
//                binding.bottomButton.isProgressMode = true
//                lifecycleScope.launch {
//                    delay(1000)
//                    binding.bottomButton.isProgressMode = false
//                    binding.pager.setCurrentItem(getItem(1), true)
//                }
            } else if (action == BottomButtonAction.POSITIVE &&
                binding.bottomButton.getPositiveButtonText() == "ГОТОВО"
            ) {
                finish()
            } else if (action == BottomButtonAction.NEGATIVE &&
                binding.bottomButton.getNegativeButtonText() == "НАЗАД"
            ) {
                binding.pager.setCurrentItem(getItem(-1), true)
            }
        }
    }

    private fun initActivAndHomeButton(words: ArrayList<Slide>) {
        with(binding) {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.position == words.size - 1) {
                        binding.bottomButton.setPositiveButtonText("ГОТОВО")
                        binding.bottomButton.hideNegativeButton()
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