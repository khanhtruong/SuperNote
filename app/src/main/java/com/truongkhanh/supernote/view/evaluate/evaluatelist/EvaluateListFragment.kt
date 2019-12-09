package com.truongkhanh.supernote.view.evaluate.evaluatelist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.RxView
import com.truongkhanh.musicapplication.base.BaseFragment
import com.truongkhanh.supernote.R
import com.truongkhanh.supernote.model.Evaluate
import com.truongkhanh.supernote.utils.DisposeBag
import com.truongkhanh.supernote.utils.THROTTLE_TIME
import com.truongkhanh.supernote.utils.disposedBy
import com.truongkhanh.supernote.utils.getEvaluateViewModelFactory
import com.truongkhanh.supernote.view.evaluate.evaluatelist.adapter.EvaluateListAdapter
import kotlinx.android.synthetic.main.fragment_evaluate_list.*
import kotlinx.android.synthetic.main.layout_toolbar_light.*
import org.jetbrains.anko.design.snackbar
import java.util.concurrent.TimeUnit

class EvaluateListFragment : BaseFragment() {

    companion object {
        fun getInstance() =
            EvaluateListFragment()
    }

    interface NavigationListener {
        fun navigateToCreateEvaluate(evaluate: Evaluate)
    }

    private lateinit var evaluateListViewModel: EvaluateListViewModel
    private val bag = DisposeBag(this)
    private lateinit var evaluateListAdapter: EvaluateListAdapter
    private lateinit var navigationListener: NavigationListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_evaluate_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationListener)
            navigationListener = context
    }

    override fun setUpView(view: View, savedInstanceState: Bundle?) {
        initViewInformation()
        initClickListener()
        initEvaluateListRecyclerView()
        bindingViewModel()
    }

    private fun initViewInformation() {
        tvCurrentDate.text = context?.getString(R.string.lbl_evaluate_list)
        btnToday.visibility = View.GONE
    }

    private fun bindingViewModel() {

        val activity = activity ?: return
        evaluateListViewModel = ViewModelProviders
            .of(activity, getEvaluateViewModelFactory(activity))
            .get(EvaluateListViewModel::class.java)

        evaluateListViewModel.evaluateList.observe(this, Observer { evaluateList ->
            evaluateListAdapter.submitList(evaluateList)
        })
        evaluateListViewModel.showMessage.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let{message ->
                view?.snackbar(message)
            }
        })
        evaluateListViewModel.navigateToCreateEvaluate.observe(this, Observer {event ->
            event.getContentIfNotHandled()?.let{evaluate ->
                navigationListener.navigateToCreateEvaluate(evaluate)
            }
        })
    }

    private fun initEvaluateListRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvEvaluateList.layoutManager = layoutManager
        val context = context ?: return
        evaluateListAdapter = EvaluateListAdapter(context) {evaluate ->
            navigationListener.navigateToCreateEvaluate(evaluate)
        }
        rvEvaluateList.adapter = evaluateListAdapter
    }

    private fun initClickListener() {
        RxView.clicks(fbCreateEvaluate)
            .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
            .subscribe {
                evaluateListViewModel.getDayEvaluate()
            }.disposedBy(bag)
        RxView.clicks(btnNavigation)
            .throttleFirst(THROTTLE_TIME, TimeUnit.MILLISECONDS)
            .subscribe {
                activity?.finish()
            }.disposedBy(bag)
    }
}