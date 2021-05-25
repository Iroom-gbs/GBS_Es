package com.dayo.executer.ui

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.preference.*
import com.dayo.executer.FCMService
import com.dayo.executer.R
import com.dayo.executer.data.DataManager
import com.google.firebase.messaging.FirebaseMessaging

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.parseColor("#303030"))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val classPreferences = findPreference<ListPreference>("class")!!

        val ablrIDPreference = findPreference<EditTextPreference>("ablrID")!!
        val ablrPWPreference = findPreference<EditTextPreference>("ablrPW")!!
        val saveHomeEditedData = findPreference<SwitchPreference>("saveHomeFragmentEditedData")!!

        val asckPWPreference = findPreference<EditTextPreference>("asckPW")!!
        val asckUseAdvOptPreference = findPreference<SwitchPreference>("asckAdv")!!
        val asckDtPreference = findPreference<EditTextPreference>("dt")!!
        val asckDselPreference = findPreference<EditTextPreference>("dsel")!!
        val asckDsPreference = findPreference<EditTextPreference>("ds")!!

        val reloadDataPreference = findPreference<Preference>("reloadData")!!

        val alwaysReceveTimeTableAlert = findPreference<SwitchPreference>("timetable_always")!!

        val receiveDebugFCMPreference = findPreference<SwitchPreference>("debugFCM")!!

        classPreferences.setEntries(R.array.class_list)
        classPreferences.setEntryValues(R.array.class_list)
        classPreferences.value = DataManager.classInfo

        ablrIDPreference.text = DataManager.ablrID
        ablrPWPreference.text = DataManager.ablrPW
        saveHomeEditedData.isChecked = DataManager.noTempDataInHomeFragment

        asckPWPreference.text = DataManager.asckPW
        asckUseAdvOptPreference.isChecked = DataManager.asckUseAdvOpt
        asckDtPreference.text = DataManager.asckDt.toString()
        asckDselPreference.text = DataManager.asckDsel.toString()
        asckDsPreference.text = DataManager.asckDs.toString()

        alwaysReceveTimeTableAlert.isChecked = DataManager.alwaysReceiveTimeTableData

        receiveDebugFCMPreference.isChecked = DataManager.receiveDebugFCMData


        classPreferences.setOnPreferenceChangeListener { _, newValue ->
            if(DataManager.alwaysReceiveTimeTableData)
                FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.ALWAYS_RECEVE_TIMETABLE_DATA)
            DataManager.classInfo = newValue.toString()
            FCMService.reinitToken()
            if(DataManager.alwaysReceiveTimeTableData)
                FirebaseMessaging.getInstance().subscribeToTopic(FCMService.ALWAYS_RECEVE_TIMETABLE_DATA)
            DataManager.saveSettings()
            DataManager.loadSettings()
            true
        }

        ablrIDPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.ablrID = newValue.toString()
            true
        }

        ablrPWPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.ablrPW = newValue.toString()
            true
        }

        saveHomeEditedData.setOnPreferenceChangeListener { _, newValue ->
            DataManager.tmpAblrData = mutableListOf()
            DataManager.tmpAblrData.addAll(DataManager.todayAblrTableData)
            DataManager.noTempDataInHomeFragment = newValue as Boolean
            true
        }

        asckPWPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.asckPW = newValue.toString()
            true
        }

        asckPWPreference.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(4)
            it.filters = fArray
        }

        asckUseAdvOptPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.asckUseAdvOpt = newValue as Boolean
            true
        }

        asckDtPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.asckDt = newValue.toString().toLong()
            true
        }

        asckDtPreference.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(4)
            it.filters = fArray
        }

        asckDselPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.asckDsel = newValue.toString().toLong()
            true
        }

        asckDselPreference.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(4)
            it.filters = fArray
        }

        asckDsPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.asckDs = newValue.toString().toLong()
            true
        }

        asckDsPreference.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = InputFilter.LengthFilter(4)
            it.filters = fArray
        }

        reloadDataPreference.setOnPreferenceClickListener {
            DataManager.loadSettings()
            true
        }

        alwaysReceveTimeTableAlert.setOnPreferenceChangeListener { _, newValue ->
            DataManager.alwaysReceiveTimeTableData = newValue as Boolean
            if(newValue) FirebaseMessaging.getInstance().subscribeToTopic(FCMService.ALWAYS_RECEVE_TIMETABLE_DATA)
            else FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.ALWAYS_RECEVE_TIMETABLE_DATA)
            true
        }

        receiveDebugFCMPreference.setOnPreferenceChangeListener { _, newValue ->
            DataManager.receiveDebugFCMData = newValue as Boolean
            if(newValue) FirebaseMessaging.getInstance().subscribeToTopic(FCMService.DEBUG)
            else FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.DEBUG)
            true
        }
    }

    override fun onStop() {
        super.onStop()
        DataManager.saveSettings()
    }
}