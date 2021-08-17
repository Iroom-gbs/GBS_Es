package com.dayo.executer.ui

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.preference.*
import com.dayo.executer.service.FCMService
import com.dayo.executer.R
import com.dayo.executer.data.DataManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<ListPreference>("class")?.let {
            it.setEntries(R.array.class_list)
            it.setEntryValues(R.array.class_list)
            it.value = DataManager.classInfo

            it.setOnPreferenceChangeListener { _, newValue ->
                if(DataManager.alwaysReceiveTimeTableData)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.ALWAYS_RECEIVE_TIMETABLE_DATA)
                if(DataManager.receiveSwdTimeTableData)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.SWD_RECEIVE_TIMETABLE_DATA)
                DataManager.classInfo = newValue.toString()
                FCMService.reinitToken()
                if(DataManager.alwaysReceiveTimeTableData)
                    FirebaseMessaging.getInstance().subscribeToTopic(FCMService.ALWAYS_RECEIVE_TIMETABLE_DATA)
                if(DataManager.receiveSwdTimeTableData)
                    FirebaseMessaging.getInstance().subscribeToTopic(FCMService.SWD_RECEIVE_TIMETABLE_DATA)
                DataManager.saveSettings()
                DataManager.loadSettings()
                true
            }
        }

        findPreference<EditTextPreference>("ablrID")?.let {
            it.text = DataManager.ablrID
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.ablrID = newValue.toString()
                true
            }
        }

        findPreference<EditTextPreference>("ablrPW")?.let {
            it.text = DataManager.ablrPW
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.ablrPW = newValue.toString()
                true
            }
        }

        findPreference<EditTextPreference>("asckName")?.let {
            it.text = DataManager.asckName
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.asckName = newValue.toString()
                true
            }
        }

        findPreference<EditTextPreference>("asckBirth")?.let {
            it.text = DataManager.asckBirth
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.asckBirth = newValue.toString()
                true
            }
            it.setOnBindEditTextListener { it2 ->
                it2.inputType = InputType.TYPE_CLASS_NUMBER
                val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = InputFilter.LengthFilter(6)
                it2.filters = fArray
            }
        }

        findPreference<EditTextPreference>("asckPW")?.let {
            it.text = DataManager.asckPW
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.asckPW = newValue.toString()
                true
            }
            it.setOnBindEditTextListener { it2 ->
                it2.inputType = InputType.TYPE_CLASS_NUMBER
                val fArray = arrayOfNulls<InputFilter>(1)
                fArray[0] = InputFilter.LengthFilter(4)
                it2.filters = fArray
            }
        }

        findPreference<SwitchPreference>("asckAlert")?.let {
            it.isChecked = DataManager.alwaysReceiveAsckAlert
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.alwaysReceiveAsckAlert = newValue as Boolean
                if(newValue)
                    FirebaseMessaging.getInstance().subscribeToTopic(FCMService.ASCK_ALERT)
                else FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.ASCK_ALERT)
                true
            }
        }

        findPreference<SwitchPreference>("asckAdv")?.let {
            it.isChecked = DataManager.asckUseAdvOpt
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.asckUseAdvOpt = newValue as Boolean
                true
            }
        }

        findPreference<Preference>("reloadData")?.let {
            it.setOnPreferenceClickListener {
                DataManager.loadSettings()
                true
            }
        }

        findPreference<SwitchPreference>("timetable_swd")?.let {
            it.isChecked = DataManager.receiveSwdTimeTableData
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.receiveSwdTimeTableData = newValue as Boolean
                if(newValue) FirebaseMessaging.getInstance().subscribeToTopic(FCMService.SWD_RECEIVE_TIMETABLE_DATA)
                else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.SWD_RECEIVE_TIMETABLE_DATA)
                    if(DataManager.alwaysReceiveTimeTableData){
                        findPreference<SwitchPreference>("timetable_always")?.isChecked = false
                        DataManager.alwaysReceiveTimeTableData = false
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.ALWAYS_RECEIVE_TIMETABLE_DATA)
                    }
                }
                DataManager.saveSettings()
                //DataManager.loadSettings()
                true
            }
        }

        findPreference<SwitchPreference>("timetable_always")?.let {
            it.isChecked = DataManager.alwaysReceiveTimeTableData
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.alwaysReceiveTimeTableData = newValue as Boolean
                Log.d("asdf", "asdf")
                if(newValue) FirebaseMessaging.getInstance().subscribeToTopic(FCMService.ALWAYS_RECEIVE_TIMETABLE_DATA)
                else FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.ALWAYS_RECEIVE_TIMETABLE_DATA)
                true
            }
        }

        findPreference<SwitchPreference>("debugFCM")?.let {
            it.isChecked = DataManager.receiveDebugFCMData
            it.setOnPreferenceChangeListener { _, newValue ->
                DataManager.receiveDebugFCMData = newValue as Boolean
                if(newValue) FirebaseMessaging.getInstance().subscribeToTopic(FCMService.DEBUG)
                else FirebaseMessaging.getInstance().unsubscribeFromTopic(FCMService.DEBUG)
                true
            }
        }
    }

    override fun onStop() {
        super.onStop()
        DataManager.saveSettings()
    }
}