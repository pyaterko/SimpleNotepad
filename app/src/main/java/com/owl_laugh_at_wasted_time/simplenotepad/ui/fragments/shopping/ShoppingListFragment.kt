package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.shopping

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.elveum.elementadapter.SimpleBindingAdapter
import com.google.android.material.tabs.TabLayout
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.preferences
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.showActionAlertDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentShoppingListBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.activity.MainNoteBookActivity
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.callback.TouchHelperCallback
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.decorator.ItemDecoration
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.OnShoppingListener
import com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.adapters.createSoppingAdapter
import com.owl_laugh_at_wasted_time.viewmodel.shopping.ShoppingListViewModel

class ShoppingListFragment : BaseFragment(R.layout.fragment_shopping_list), OnShoppingListener {

    private val binding by viewBinding(FragmentShoppingListBinding::bind)
    private val viewModel by viewModels<ShoppingListViewModel> { viewModelFactory }
    private lateinit var adapter: SimpleBindingAdapter<ShoppingListItem>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val tab: TabLayout.Tab? =
                        (activity as MainNoteBookActivity).binding.selectTabs.getTabAt(0)
                    tab?.select()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = createSoppingAdapter(this)
        binding.recyclerViewShoppingItem.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewShoppingItem.adapter = adapter
        binding.recyclerViewShoppingItem.isNestedScrollingEnabled = false
        val dividerItemDecoration = ItemDecoration(16)
        binding.recyclerViewShoppingItem.addItemDecoration(dividerItemDecoration)
        if (preferences(requireContext()).getBoolean(
                getString(R.string.settings_swipe_to_trash_key),
                true
            )
        ) {
            val touchCallback = TouchHelperCallback(adapter) { item ->
                showDeleteAlertDialog(item)
            }
            val touchHelper = ItemTouchHelper(touchCallback)
            touchHelper.attachToRecyclerView(binding.recyclerViewShoppingItem)
        }

        fabActionOnScroll(
            binding.recyclerViewShoppingItem,
            binding.buttonFabShoppingList,
            null,
            null
        )
        viewModel.shoppingList.collectWhileStarted { list ->
            if (list.isEmpty()) {
                binding.noDataImageView.visibility = View.VISIBLE
            } else {
                binding.noDataImageView.visibility = View.GONE
            }
            adapter.submitList(list.sortedBy { it.done })
        }

        binding.buttonFabShoppingList.setOnClickListener {
            showActionAlertDialog(
                requireContext(),
                layoutInflater,
                getString(R.string.volume_setup),
                getString(R.string.empty_value),
                R.string.action_confirm,
                R.string.name_of_product,
                actionPB1 = {
                    if (it.isNotBlank()) {
                        viewModel.addShoppingListItem(ShoppingListItem(text = it))
                    }
                }
            )
        }

    }

    private fun showDeleteAlertDialog(item: ShoppingListItem) {
        displayAConfirmationDialog(
            requireContext(),
            getString(R.string.default_alert_shop),
            actionPB1 = {
                launchScope {
                    viewModel.deleteShoppingListItem(item)
                }
            },
            actionNB1 = {
                adapter.notifyDataSetChanged()
            }
        )
    }

    override fun markAsPurchased(item: ShoppingListItem) {
        viewModel.addShoppingListItem(item.copy(done = !item.done))
    }
}