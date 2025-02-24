package com.abencrauz.yates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity: Fragment() {
    val themes = mutableListOf<String>("Light", "Dark")
    val currencies = mutableListOf<String>("IDR","USD")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val currTheme = AppCompatDelegate.getDefaultNightMode()
        var v = inflater.inflate(R.layout.activity_settings, container, false)
        val adapter = ArrayAdapter(this.context!!, R.layout.dropdown_item, themes)
        val them = v.findViewById<AutoCompleteTextView>(R.id.actv_theme)

        if(currTheme == AppCompatDelegate.MODE_NIGHT_YES){
            them.setText("Dark")
        }else{
            them.setText("Light")
        }

        them.setAdapter(adapter)
        val adapter2 = ArrayAdapter(this.context!!, R.layout.dropdown_item, currencies)
        val curr = v.findViewById<AutoCompleteTextView>(R.id.actv_currency)

        curr.setText(PreferenceHelper.currencyString)
        curr.setAdapter(adapter2)
        val btn = v.findViewById<MaterialButton>(R.id.btn_save)
        btn.setOnClickListener(View.OnClickListener {
            if(curr.text.toString() == "IDR"){
                PreferenceHelper.currencyString = "IDR"
                PreferenceHelper.currencyMultiplier = 1.0
            }else if(curr.text.toString() == "USD"){
                PreferenceHelper.currencyString = "USD"
                PreferenceHelper.currencyMultiplier = 0.000064
            }

            if(them.text.toString() == "Light"){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }else if(them.text.toString() == "Dark"){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            Toast.makeText(this.context, R.string.changes_saved, Toast.LENGTH_LONG).show()
        })
        return v
    }
}