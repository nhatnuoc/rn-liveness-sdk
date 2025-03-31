/**
 * Copyright (c) 2018-2019 SpringCard - www.springcard.com
 * All right reserved
 * This software is covered by the SpringCard SDK License Agreement - see LICENSE.txt
 */

package com.livenessrnexample.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.livenessrnexample.R
import com.springcard.pcscaiot.SCardAiot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DeviceFragment : Fragment() {
    protected val TAG = this::class.java.simpleName
    private lateinit var activityContext: Context
    lateinit var device: Any
    val v_device = SCardAiot()
    lateinit var rapduTextBox: EditText
    lateinit var PassportNbr: EditText
    lateinit var DateOfBirth:EditText
    lateinit var DateOfExpiration:EditText
    lateinit var disconnectCardButton:Button
    lateinit var connectCardButton:Button
    lateinit var transmitDG1:Button
    lateinit var transmitDG2:Button
    lateinit var transmitDG13:Button
    lateinit var transmitSOD:Button
    lateinit var transmitDG14:Button
    lateinit var transmitDG15:Button
    lateinit var textState:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContext = activity as Context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        _binding = FragmentDevice2Binding.inflate(inflater, container, false)
        val view: View = inflater.inflate(R.layout.fragment_device_2, container, false)
        rapduTextBox = view.findViewById<View>(R.id.rapduTextBox) as EditText
        PassportNbr = view.findViewById<View>(R.id.PassportNbr) as EditText
        DateOfBirth = view.findViewById<View>(R.id.DateOfBirth) as EditText
        DateOfExpiration = view.findViewById<View>(R.id.DateOfExpiration) as EditText
        disconnectCardButton = view.findViewById<View>(R.id.disconnectCardButton) as Button
        connectCardButton = view.findViewById<View>(R.id.connectCardButton) as Button
        transmitDG1 = view.findViewById<View>(R.id.transmitDG1) as Button
        transmitDG2 = view.findViewById<View>(R.id.transmitDG2) as Button
        transmitDG13 = view.findViewById<View>(R.id.transmitDG13) as Button
        transmitSOD = view.findViewById<View>(R.id.transmitSOD) as Button
        transmitDG14 = view.findViewById<View>(R.id.transmitDG14) as Button
        transmitDG15 = view.findViewById<View>(R.id.transmitDG15) as Button
        textState = view.findViewById<View>(R.id.textState) as TextView

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectCardButton.isEnabled = true
        disconnectCardButton.isEnabled = false

    }
    var strDG1:String = ""
    var strDG2:String = ""
    var strDG13:String = ""
    var strDG14:String = ""
    var strDG15:String = ""
    var strSOD:String = ""
    override fun onResume() {
        super.onResume()
        try {
            Log.d(TAG, "Vuong onResume 1")
            if (v_device.connectToNewDevice) {
//            rapduTextBox.text.clear()
                v_device.connectToDevice(device,activityContext)
                disconnectCardButton.setOnClickListener {
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    rapduTextBox.text.append("Card Disconnect" + "\n")
                    v_device.DisconnectCard()
                    connectCardButton.isEnabled = true
                    disconnectCardButton.isEnabled = false
                }
                connectCardButton.setOnClickListener {
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    if(v_device.ConnectCard()) {
                        rapduTextBox.text.append("Card Connected" + "\n")
                        connectCardButton.isEnabled = false
                        disconnectCardButton.isEnabled = true
                    }
                    else{
                        rapduTextBox.text.append("Card Connected Error" + "\n")
                    }

                }
                transmitDG1.setOnClickListener {
                    textState.setText("")
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    rapduTextBox.text.append("Running DG1...." + "\n")
                    var passportNumber = PassportNbr.text.toString().trim()
                    var dateOfBirth = DateOfBirth.text.toString().trim()
                    var dateOfExpiration = DateOfExpiration.text.toString().trim()
                    v_device.readData_DG1(
                        passportNumber,
                        dateOfBirth,
                        dateOfExpiration
                    )
                    GlobalScope.launch {
                        println("Bắt đầu công việc")
                        while (!v_device.isSuccess_DG1) {
                            delay(2L)
                        }
                        println("Công việc hoàn thành: " + v_device.isSuccess_DG1)
                        if (v_device.dg1 != null) {
//                        rapduTextBox.text.append(bytesToString(v_device.dg1) + "\n")
                            textState.setText("Đọc DG1 thành công")
                        } else {
                            textState.setText("Lỗi đọc DG1")
                        }
                    }
                }
                transmitDG2.setOnClickListener {
                    textState.setText("")
                    Log.d(TAG, "Read DG2")
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    rapduTextBox.text.append("Running DG2...." + "\n")
                    var passportNumber = PassportNbr.text.toString().trim()
                    var dateOfBirth = DateOfBirth.text.toString().trim()
                    var dateOfExpiration = DateOfExpiration.text.toString().trim()
                    v_device.readData_DG2(
                        passportNumber,
                        dateOfBirth,
                        dateOfExpiration
                    )
                    GlobalScope.launch {
                        println("Bắt đầu công việc")
                        while (!v_device.isSuccess_DG2) {
                            delay(2L)
                        }
                        println("Công việc hoàn thành: " + v_device.isSuccess_DG2)
                        if (v_device.dg2 != null) {
//                        rapduTextBox.text.append(bytesToString(v_device.dg2) + "\n")
                            textState.setText("Đọc DG2 thành công")
                        } else {
                            textState.setText("Lỗi đọc DG2")
                        }
                    }
                }
                transmitDG13.setOnClickListener {
                    textState.setText("")
                    Log.d(TAG, "Read DG13")
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    rapduTextBox.text.append("Running DG13...." + "\n")
                    var passportNumber = PassportNbr.text.toString().trim()
                    var dateOfBirth = DateOfBirth.text.toString().trim()
                    var dateOfExpiration = DateOfExpiration.text.toString().trim()
                    v_device.readData_DG13(
                        passportNumber,
                        dateOfBirth,
                        dateOfExpiration
                    )
                    GlobalScope.launch {
                        println("Bắt đầu công việc")
                        while (!v_device.isSuccess_DG13) {
                            delay(2L)
                        }
                        println("Công việc hoàn thành: " + v_device.isSuccess_DG13)
                        if (v_device.dg13 != null) {
//                        rapduTextBox.text.append(bytesToString(v_device.dg13) + "\n")
                            textState.setText("Đọc DG13 thành công")
                        } else {
                            textState.setText("Lỗi đọc DG13")
                        }
                    }
                }
                transmitSOD.setOnClickListener {
                    textState.setText("")
                    Log.d(TAG, "Read SOD")
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    rapduTextBox.text.append("Running SOD...." + "\n")
                    var passportNumber = PassportNbr.text.toString().trim()
                    var dateOfBirth = DateOfBirth.text.toString().trim()
                    var dateOfExpiration = DateOfExpiration.text.toString().trim()
                    v_device.readData_SOD(
                        passportNumber,
                        dateOfBirth,
                        dateOfExpiration
                    )
                    GlobalScope.launch {
                        println("Bắt đầu công việc")
                        while (!v_device.isSuccess_SOD) {
                            delay(2L)
                        }
                        println("Công việc hoàn thành: " + v_device.isSuccess_SOD)
                        if (v_device.sod != null) {
//                        rapduTextBox.text.append(bytesToString(v_device.sod) + "\n")
                            textState.setText("Đọc SOD thành công")
                        } else {
                            textState.setText("Lỗi đọc SOD")
                        }
                    }
                }
                transmitDG14.setOnClickListener {
                    textState.setText("")
                    Log.d(TAG, "Read DG14")
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    rapduTextBox.text.append("Running DG14...." + "\n")
                    var passportNumber = PassportNbr.text.toString().trim()
                    var dateOfBirth = DateOfBirth.text.toString().trim()
                    var dateOfExpiration = DateOfExpiration.text.toString().trim()
                    v_device.readData_DG14(
                        passportNumber,
                        dateOfBirth,
                        dateOfExpiration
                    )
                    GlobalScope.launch {
                        println("Bắt đầu công việc")
                        while (!v_device.isSuccess_DG14) {
                            delay(2L)
                        }
                        println("Công việc hoàn thành: " + v_device.isSuccess_DG14)
                        if (v_device.dg14 != null) {
//                        rapduTextBox.text.append(bytesToString(v_device.dg14) + "\n")
                            textState.setText("Đọc DG14 thành công")
                        } else {
                            textState.setText("Lỗi đọc DG14")
                        }
                    }
                }
                transmitDG15.setOnClickListener {
                    textState.setText("")
                    Log.d(TAG, "Read DG15")
                    rapduTextBox.text.clear()
                    rapduTextBox.text.append("")
                    rapduTextBox.text.append("Running DG15...." + "\n")
                    var passportNumber = PassportNbr.text.toString().trim()
                    var dateOfBirth = DateOfBirth.text.toString().trim()
                    var dateOfExpiration = DateOfExpiration.text.toString().trim()
                    v_device.readData_DG15(
                        passportNumber,
                        dateOfBirth,
                        dateOfExpiration
                    )
                    GlobalScope.launch {
                        println("Bắt đầu công việc")
                        while (!v_device.isSuccess_DG15) {
                            delay(2L)
                        }
                        println("Công việc hoàn thành: " + v_device.isSuccess_DG15)
                        if (v_device.dg15 != null) {
//                        rapduTextBox.text.append(bytesToString(v_device.dg15) + "\n")
                            textState.setText("Đọc DG15 thành công")
                        } else {
                            textState.setText("Lỗi đọc DG15")
                        }
                    }
                }
                Log.d(TAG, "End onResume connectToNewDevice")
                v_device.connectToNewDevice = false
            }
        }
        catch (e: Exception) {
            println("Exception caught: ${e.message}")
        }

    }

    fun bytesToString(input: ByteArray?): String? {
        if (input == null) {
            return ""
        }
        var output = ""
        for (b in input) {
            output = output + String.format("%02x", b)
        }
        return output
    }

}
