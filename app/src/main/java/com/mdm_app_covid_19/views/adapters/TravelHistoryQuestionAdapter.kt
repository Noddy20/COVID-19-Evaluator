package com.mdm_app_covid_19.views.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mdm_app_covid_19.R
import com.mdm_app_covid_19.data.models.QuestionModel
import com.mdm_app_covid_19.extFunctions.hide
import com.mdm_app_covid_19.extFunctions.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_travel_history_question.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

class TravelHistoryQuestionAdapter(private val mActivity: Activity) : RecyclerView.Adapter<TravelHistoryQuestionAdapter.ViewHolder>() {

    private val colorWhite = ContextCompat.getColor(mActivity, R.color.colorWhite)
    private val colorDimTxt = ContextCompat.getColor(mActivity, R.color.colorTextDim)
    private val colorRed = ContextCompat.getColor(mActivity, R.color.colorRedLight)
    private val colorBgGrey = ContextCompat.getColor(mActivity, R.color.colorBgGrey)

    private val typeFace = ResourcesCompat.getFont(mActivity, R.font.font_open_sans_regular)

    private var arrayModel: List<QuestionModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.row_travel_history_question, parent, false))
    }

    override fun getItemCount(): Int {
        return arrayModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrayModel[position])
    }

    fun setItemList(list: List<QuestionModel>){
        arrayModel = list
        notifyDataSetChanged()
    }

    fun getItemList(): List<QuestionModel>{
        return arrayModel
    }

    fun updateUnAnswered(position: Int){
        kotlin.runCatching {
            arrayModel[position].answer = "3"
            notifyItemChanged(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer, View.OnClickListener {

        override val containerView: View?
            get() = itemView


        init {
            btnYes.setOnClickListener(this)
            btnNo.setOnClickListener(this)
            btnYes.typeface = typeFace
            btnNo.typeface = typeFace
        }

        override fun onClick(v: View?) {
            kotlin.runCatching {
                when (v?.id) {
                    R.id.btnYes -> {
                        arrayModel[adapterPosition].apply {
                            if (answer != "1") answer = "1"
                        }
                        notifyItemChanged(adapterPosition)
                    }
                    R.id.btnNo -> {
                        arrayModel[adapterPosition].apply {
                            if (answer != "2") answer = "2"
                        }
                        notifyItemChanged(adapterPosition)
                    }
                }
            }
        }

        fun bind(model: QuestionModel){
            txtQuestion.text=model.question

            if (model.questionHindi.isNullOrEmpty()){
                txtQuestionHindi.hide()
                questionDivider.hide()
            }else{
                txtQuestionHindi.text = model.questionHindi
                questionDivider.show()
                txtQuestionHindi.show()
            }

            when(model.answer){
                "0" -> {
                    cardView.setCardBackgroundColor(colorBgGrey)
                    btnYes.backgroundResource = R.drawable.bg_grey_rounded
                    btnNo.backgroundResource = R.drawable.bg_grey_rounded

                    btnYes.textColor = colorDimTxt
                    btnNo.textColor = colorDimTxt
                }
                "1" -> {
                    cardView.setCardBackgroundColor(colorBgGrey)
                    btnYes.backgroundResource = R.drawable.bg_accent_rounded
                    btnNo.backgroundResource = R.drawable.bg_grey_rounded

                    btnYes.textColor = colorWhite
                    btnNo.textColor = colorDimTxt
                }
                "2" -> {
                    cardView.setCardBackgroundColor(colorBgGrey)
                    btnNo.backgroundResource = R.drawable.bg_accent_rounded
                    btnYes.backgroundResource = R.drawable.bg_grey_rounded

                    btnNo.textColor = colorWhite
                    btnYes.textColor = colorDimTxt
                }
                "3" -> {
                    cardView.setCardBackgroundColor(colorRed)
                    btnYes.backgroundResource = R.drawable.bg_grey_rounded
                    btnNo.backgroundResource = R.drawable.bg_grey_rounded

                    btnYes.textColor = colorDimTxt
                    btnNo.textColor = colorDimTxt
                }
            }

        }

    }
}