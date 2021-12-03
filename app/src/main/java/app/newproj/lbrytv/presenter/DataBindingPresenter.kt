package app.newproj.lbrytv.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.leanback.widget.Presenter

open class DataBindingPresenter(
        @LayoutRes private val layoutId: Int,
        @IdRes private val variableId: Int
) : Presenter() {

    private inner class ViewHolder(val binding: ViewDataBinding) : Presenter.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        return LayoutInflater.from(parent.context).run {
            ViewHolder(DataBindingUtil.inflate(this, layoutId, parent, false))
        }
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        if (item == null) return
        (viewHolder as ViewHolder).binding.setVariable(variableId, item)
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder?) = Unit
}
