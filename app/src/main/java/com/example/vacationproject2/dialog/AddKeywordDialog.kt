package com.example.vacationproject2.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.vacationproject2.databinding.DialogAddKeywordBinding
import com.example.vacationproject2.main.CreateKeywordAdapter

class AddKeywordDialog(val adapterCreate: CreateKeywordAdapter): DialogFragment(){
    private lateinit var binding: DialogAddKeywordBinding
    var keyword: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogAddKeywordBinding.inflate(inflater,container,false)
        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.positiveButton.setOnClickListener {
            if (binding.keywordEdit.text.toString().isEmpty()){
                Toast.makeText(context,"키워드를 입력해주세요",Toast.LENGTH_SHORT).show()
            }else{
                adapterCreate.apply {
                    data.add(binding.keywordEdit.text.toString())
                }
                adapterCreate.notifyDataSetChanged()

                dialog?.dismiss()
            }
        }
        binding.nagativeButton.setOnClickListener {
            dialog?.dismiss()
        }
        return view
    }
}