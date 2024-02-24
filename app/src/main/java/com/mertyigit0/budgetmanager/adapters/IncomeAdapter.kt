package com.mertyigit0.budgetmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.Income

class IncomeAdapter(val incomeList : ArrayList<Income>)
    : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {
    class IncomeViewHolder(var view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):IncomeViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_income,parent,false)
        return IncomeViewHolder(view)

    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {






    }

    fun updateCategoryList(newCategoryList:List<Income>){
       incomeList.clear()
        incomeList.addAll(newCategoryList)
        notifyDataSetChanged()
    }


}