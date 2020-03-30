package com.mdm_app_covid_19.views.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.SelectionListDialogModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_selection_list_dialog.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

class SelectionListDialogAdapter(var act: Activity, var isMultiSel: Boolean,
                                 var onClickItem: (model:SelectionListDialogModel) -> Unit):
    RecyclerView.Adapter<SelectionListDialogAdapter.MyViewHolder>() {

    private var selModels = ArrayList<SelectionListDialogModel>()
    private var arrayModel: List<SelectionListDialogModel> = ArrayList()

    private val textColor = ContextCompat.getColor(act, R.color.colorText)
    private val hintolor = ContextCompat.getColor(act, R.color.colorTextDim)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(act).inflate(R.layout.row_selection_list_dialog,parent,false))
    }

    override fun getItemCount(): Int {
        return arrayModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(arrayModel[position])
    }

    fun setDataList(arrayModel: List<SelectionListDialogModel>, sortById: Boolean = true){
        this.arrayModel = if (sortById) arrayModel.sortedBy { it.id } else arrayModel
        notifyDataSetChanged()
    }

    fun setSelectedList(arraySelModel: ArrayList<SelectionListDialogModel>){
        selModels = arraySelModel
        notifyDataSetChanged()
    }

    fun getSelectedModels(): ArrayList<SelectionListDialogModel>{
        return selModels
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, LayoutContainer {

        override val containerView: View? get() = itemView

        init {
            linItem.setOnClickListener(this)
            chkBox.isVisible = isMultiSel
        }

        override fun onClick(p0: View?) {
            try {
                val model = arrayModel[adapterPosition]
                if (isMultiSel){
                    if (selModels.contains(model))
                        selModels.remove(model)
                    else
                        selModels.add(model)

                    notifyItemChanged(adapterPosition)
                }else{
                    onClickItem(model)
                }
            }catch (e: Exception){
                Log.v("SelectionListAdapter", "Exc $e")
            }
        }

        fun bind(model: SelectionListDialogModel){
            chkBox.isChecked = selModels.contains(model)
            //chkBox.backgroundResource = if(chkBox.isChecked) R.drawable.blue_check_box_checked else R.drawable.blue_check_box_unchecked
            txtName.text = model.str
            txtName.textColor = if (model.id == -1) hintolor else textColor
        }

    }
}