package com.codingblocks.cbonlineapp.admin.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.ErrorStatus
import kotlinx.android.synthetic.main.admin_overview_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminOverviewFragment : Fragment() {

    private val viewModel by viewModel<AdminOverviewViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.admin_overview_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchDoubtStats()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.doubtStats.observer(viewLifecycleOwner) {
            doubtResolvedTv.text = it.totalResolvedDoubts.toString()
            userRatingTv.text = it.avgRating.toString()
            cbRatingTv.text = it.cbRating.toString()
            responseTv.text = it.avgFirstResponse.toString()
            badReviewTv.text = it.totalBadReviews.toString()
            resolutionTv.text = it.avgResolution.toString()

        }

        viewModel.errorLiveData.observer(viewLifecycleOwner)
        {
            when (it) {
                ErrorStatus.EMPTY_RESPONSE -> {

                }
                ErrorStatus.NO_CONNECTION -> {

                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(requireContext(), UNAUTHORIZED) {
                        requireActivity().finish()
                    }
                }
                ErrorStatus.TIMEOUT -> {

                }
            }
        }
    }


}
