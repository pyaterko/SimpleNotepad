package com.owl_laugh_at_wasted_time.simplenotepad.ui.fragments.shopping

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.displayAConfirmationDialog
import com.owl_laugh_at_wasted_time.notesprojectandroiddevelopercourse.domain.showActionAlertDialog
import com.owl_laugh_at_wasted_time.simplenotepad.R
import com.owl_laugh_at_wasted_time.simplenotepad.databinding.FragmentShoppingListBinding
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.BaseFragment
import com.owl_laugh_at_wasted_time.simplenotepad.ui.base.viewBinding
import com.owl_laugh_at_wasted_time.viewmodel.shopping.ShoppingListViewModel

class ShoppingListFragment : BaseFragment(R.layout.fragment_shopping_list) {

    private val binding by viewBinding(FragmentShoppingListBinding::bind)
    private val viewModel by viewModels<ShoppingListViewModel> { viewModelFactory }
    private lateinit var adapter: ShoppingListRVAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ShoppingListRVAdapter()
        binding.recyclerViewShoppingItem.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewShoppingItem.adapter = adapter

        viewModel.shoppingList.collectWhileStarted {
            if (it.isEmpty()) {
                binding.noDataImageView.visibility = View.VISIBLE
            } else {
                binding.noDataImageView.visibility = View.GONE
            }
            adapter.shoppingList = it.sortedBy { it.done }
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
                        launchScope {
                            viewModel.addShoppingListItem(ShoppingListItem(text = it))
                        }
                    }
                }

            )
        }
        adapter.onCheckBoxClickListener = {
            val item = it.tag as ShoppingListItem
            launchScope {
                viewModel.addShoppingListItem(item.copy(done = !item.done))
            }
        }
        adapter.onDeleteButtonClickListener = {
            val item = it.tag as ShoppingListItem
            showDeleteAlertDialog(item)
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

            }
        )
    }

    companion object {

        fun newInstance(): ShoppingListFragment {
            return ShoppingListFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }

}