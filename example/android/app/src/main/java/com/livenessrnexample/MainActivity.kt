package com.livenessrnexample

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.bridge.Promise
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import com.google.android.material.snackbar.Snackbar
import com.liveness.sdk.corev4.LiveNessSDK
import com.liveness.sdk.corev4.model.DataConfig
import com.liveness.sdk.corev4.model.LivenessModel
import com.liveness.sdk.corev4.model.LivenessRequest
import com.liveness.sdk.corev4.model.VerifyLevel
import com.liveness.sdk.corev4.utils.CallbackLivenessListener
import com.livenessrnexample.mlkit.DocType
import com.livenessrnexample.ui.CaptureActivity
import com.livenessrnexample.ui.ChooseScannerDialog
import com.livenessrnexample.ui.DeviceFragment
import com.livenessrnexample.ui.LiveCheckResultActivity
import com.livenessrnexample.ui.ScanMrzResultActivity
import com.livenessrnexample.ui.ScanNfcBottomSheetDialog
import com.livenessrnexample.ui.ScanNfcResultActivity
import com.livenessrnexample.ui.ScanNfcWithDeviceDialog
import com.livenessrnexample.util.AppConst
import com.livenessrnexample.util.DataUtil
import com.livenessrnexample.util.DateTimeUtil
import com.livenessrnexample.util.PermissionUtil
import com.springcard.pcscaiot.SCardAiot
import com.springcard.pcscaiot.SCardReaderListCallback
import io.kycv3.CardReader
import io.kycv3.exception.CardReaderException
import io.kycv3.exception.CardReaderInitException
import io.kycv3.idcard.IdCardInfo
import io.kycv3.model.CardReaderResult
import org.jmrtd.BACKey
import org.jmrtd.BACKeySpec
import org.jmrtd.lds.icao.MRZInfo
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date

class MainActivity : ReactActivity() {
  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  override fun getMainComponentName(): String = "LivenessRnExample"

  /**
   * Returns the instance of the [ReactActivityDelegate]. We use [DefaultReactActivityDelegate]
   * which allows you to enable New Architecture with a single boolean flags [fabricEnabled]
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

  companion object {
    val TAG: String = MainActivity::class.java.simpleName
    const val DOC_TYPE: String = "DOC_TYPE"
    const val DO_NEXT_STEP: String = "DO_NEXT_STEP"
    const val TRANSACTION_ID_READ_CARD: String = "TRANSACTION_ID_READ_CARD"
    const val APP_CAMERA_ACTIVITY_REQUEST_CODE: Int = 150
    const val APP_SETTINGS_ACTIVITY_REQUEST_CODE: Int = 550
    const val APP_RESULT_MRZ_ACTIVITY_REQUEST_CODE: Int = 650
    const val APP_RESULT_NFC_ACTIVITY_REQUEST_CODE: Int = 750
    const val APP_RESULT_LIVE_ACTIVITY_REQUEST_CODE: Int = 850
    //    private static final String APP_ID = "vn.pvcombank.dev";
    const val APP_ID: String = "com.qts.test"
  }

  private var mReadingNFC = false
  private var mMainLayout: View? = null
  private var mLoadingLayout: LinearLayout? = null
  private var imgStep1: ImageView? = null
  private var imgStep2: ImageView? = null
  private var imgStep3: ImageView? = null
  private var btnNextStep: Button? = null
  private var swMode: Switch? = null
  private var etColor: EditText? = null
  private var etFrame: EditText? = null
  private var cbYellow: CheckBox? = null
  private var cbPurple: CheckBox? = null
  private var cbOrange: CheckBox? = null
  private var rbLow: RadioButton? = null
  private var rbMedium: RadioButton? = null
  private var rbHigh: RadioButton? = null
  private val frameContainerMain: FrameLayout? = null
  private var mMRZInfo: MRZInfo? = null
  private var mBackCardImage: String? = null // base64data
  private var requestId: String? = null
  private var cameraType: CameraType? = null

  private var passedStep = 0

  private var scanNfcBottomSheetDialog: ScanNfcBottomSheetDialog? = null
  private var scanNfcWithDeviceDialog: ScanNfcWithDeviceDialog? = null

  private var scanNfcCount = 1
  var readCardPromise: Promise? = null

  private fun readFile(context: Context, fileName: String): String {
    try {
      val assetManager = context.assets
      val inputStream = assetManager.open(fileName)
      val reader = BufferedReader(InputStreamReader(inputStream))
      val stringBuilder = StringBuilder()
      var line: String?
      while ((reader.readLine().also { line = it }) != null) {
        stringBuilder.append(line)
        stringBuilder.append("\n")
      }
      val contents = stringBuilder.toString()
      return contents
    } catch (e: java.lang.Exception) {
      return ""
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_main)
//    // some device still show the action bar, e.g. Fold 2,
//    // so we need to manual hide the action bar
//    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//    if (supportActionBar != null) {
//      supportActionBar!!.hide()
//    }
//    scanNfcCount = 0
////    findViewById<View>(R.id.tv_reset).setOnClickListener(this)
//
//    //        findViewById(R.id.btn_verify_card).setOnClickListener(this);
////        findViewById(R.id.btn_verify_face).setOnClickListener(this);
//    mMainLayout = findViewById<View>(R.id.main_layout)
//    mLoadingLayout = findViewById<LinearLayout>(R.id.loading_layout)
//    imgStep1 = findViewById<ImageView>(R.id.img_step1)
//    imgStep2 = findViewById<ImageView>(R.id.img_step2)
//    imgStep3 = findViewById<ImageView>(R.id.img_step3)
//    btnNextStep = findViewById<Button>(R.id.btn_next_step)
//    btRegisterFace = findViewById<Button>(R.id.btn_register_face)
//    btLiveness = findViewById<Button>(R.id.btn_liveness)
//    swMode = findViewById<Switch>(R.id.swMode)
//    etColor = findViewById<EditText>(R.id.etColor)
//    etFrame = findViewById<EditText>(R.id.etFrame)
//    cbYellow = findViewById<CheckBox>(R.id.cbYellow)
//    cbPurple = findViewById<CheckBox>(R.id.cbPurple)
//    cbOrange = findViewById<CheckBox>(R.id.cbOrange)
//    rbLow = findViewById<RadioButton>(R.id.rbLow)
//    rbMedium = findViewById<RadioButton>(R.id.rbMedium)
//    rbHigh = findViewById<RadioButton>(R.id.rbHigh)

//    btnNextStep.setOnClickListener(this)
//    btRegisterFace.setOnClickListener(this)
//    btLiveness.setOnClickListener(this)

//    swMode.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
//      if (isChecked) {
//        etColor.setVisibility(View.VISIBLE)
//        etFrame.setVisibility(View.VISIBLE)
//      } else {
//        etColor.setVisibility(View.GONE)
//        etFrame.setVisibility(View.GONE)
//      }
//    })
    CardReader.DEBUG = true
//    InitCardReaderSdk().execute()
  }

  private fun openCameraActivity() {
    if (cameraType == CameraType.OCR) {
      val intent = Intent(this, CaptureActivity::class.java)
      intent.putExtra(DOC_TYPE, DocType.ID_CARD)
      startActivityForResult(intent, APP_CAMERA_ACTIVITY_REQUEST_CODE)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == RESULT_OK && data != null) {
      when (requestCode) {
        APP_CAMERA_ACTIVITY_REQUEST_CODE -> if (cameraType == CameraType.OCR) {
          val mrzInfo = data.getSerializableExtra(CaptureActivity.MRZ_RESULT) as MRZInfo?
          mBackCardImage = DataUtil.backCardImage
          if (mrzInfo != null) {
            mMRZInfo = mrzInfo
            val doe: Date = DateTimeUtil.fromString(mrzInfo.dateOfExpiry, "yyMMdd")
            if (doe.year != 1999 && doe.before(Date())) {
              updatePassedStep(0)
              this.mMainLayout?.let {
                Snackbar.make(it, "Thẻ đã hết hạn sử dụng", Snackbar.LENGTH_SHORT)
                  .show()
              }
            } else {
              updatePassedStep(1)
              val intent = Intent(this, ScanMrzResultActivity::class.java)
              intent.putExtra(CaptureActivity.MRZ_RESULT, mrzInfo)
              startActivityForResult(intent, APP_RESULT_MRZ_ACTIVITY_REQUEST_CODE)
            }
          } else {
            updatePassedStep(0)
            this.mMainLayout?.let {
              Snackbar.make(
                it, R.string.error_input, Snackbar.LENGTH_SHORT).show()
            }
          }
        }

        APP_RESULT_MRZ_ACTIVITY_REQUEST_CODE -> if (data.hasExtra(DO_NEXT_STEP)) {
          val shouldDoNextStep = data.getSerializableExtra(DO_NEXT_STEP) as Boolean?
          if (shouldDoNextStep!!) {
            doNextStep()
          }
        }

        APP_RESULT_NFC_ACTIVITY_REQUEST_CODE -> if (data.hasExtra(DO_NEXT_STEP)) {
          val shouldDoNextStep = data.getSerializableExtra(DO_NEXT_STEP) as Boolean?
          if (shouldDoNextStep!!) {
            doNextStep()
          }
        }

        APP_RESULT_LIVE_ACTIVITY_REQUEST_CODE -> if (data.hasExtra(DO_NEXT_STEP)) {
          updatePassedStep(3)
        }

        else -> {}
      }
    }
  }

  private fun reset() {
    passedStep = 0
    mMRZInfo = null
    updatePassedStep(0)
  }

  private fun updatePassedStep(newPassedStep: Int) {
    passedStep = newPassedStep
//    imgStep1.setVisibility(if (passedStep > 0) View.VISIBLE else View.GONE)
//    imgStep2.setVisibility(if (passedStep > 1) View.VISIBLE else View.GONE)
//    imgStep3.setVisibility(if (passedStep > 2) View.VISIBLE else View.GONE)
//    btnNextStep.setText(if (passedStep == 0) R.string.main_text_start else R.string.main_text_continue)
//    btnNextStep.setVisibility(if (passedStep < 3) View.VISIBLE else View.GONE)
  }

  fun doNextStep() {
    Log.d("Thuytv", "-----doNextStep:$passedStep")
    when (passedStep) {
      0 -> {
        cameraType = CameraType.OCR
        requestPermissionForCamera()
      }

      1 -> {
        mReadingNFC = false
        if (!checkNfc()) {
          ChooseScannerDialog.newInstance(this@MainActivity
          ) { usbDevice -> showPluginScanNfc(usbDevice) }.show(fragmentManager, "")
        } else {
          AlertDialog.Builder(this).setTitle("Chọn máy quét")
            .setMessage("Chọn máy quét để thực hiện đọc dữ liệu").setPositiveButton(
              "NFC điện thoại"
            ) { dialog: DialogInterface?, which: Int ->
              showEmbeddedScanNfc()
            }.setNegativeButton(
              "Cổng USB cắm thêm"
            ) { dialog: DialogInterface?, which: Int ->
              ChooseScannerDialog.newInstance(
                this@MainActivity
              ) { usbDevice -> showPluginScanNfc(usbDevice) }.show(fragmentManager, "")
            }.show()
        }
      }

      2 -> startVerifyFace()

      3 -> {}
      else -> {}
    }
  }

  private fun startVerifyFace() {
    requestId?.let {
      readCardPromise?.resolve(it)
//      LiveNessSDK.Companion.startLiveNess(
//        this, getLivenessRequest(it),
//        object : CallbackLivenessListener {
//          override fun onCallbackLiveness(livenessModel: LivenessModel?) {
//            if (livenessModel != null) {
//              DataUtil.IMAGE_RESULT = livenessModel.imageResult
//              if (livenessModel.status != null && livenessModel.status == 200) {
//                DataUtil.IMG_TRANS = livenessModel.livenessImage
//                DataUtil.result = livenessModel
//              } else {
//                DataUtil.IMG_TRANS = null
//                DataUtil.result = null
//              }
//              val intent = Intent(this@MainActivity, LiveCheckResultActivity::class.java)
//              startActivityForResult(intent, APP_RESULT_LIVE_ACTIVITY_REQUEST_CODE)
//            }
//          }
//        })
    }
  }

  private fun getLivenessRequest(transactionId: String): LivenessRequest {
    val public_key = """
        -----BEGIN CERTIFICATE-----
        MIIE8jCCA9qgAwIBAgIQVAESDxKv/JtHV15tvtt1UjANBgkqhkiG9w0BAQsFADAr
        MQ0wCwYDVQQDDARJLUNBMQ0wCwYDVQQKDARJLUNBMQswCQYDVQQGEwJWTjAeFw0y
        MzA2MDcwNjU1MDNaFw0yNjA2MDkwNjU1MDNaMIHlMQswCQYDVQQGEwJWTjESMBAG
        A1UECAwJSMOgIE7hu5lpMRowGAYDVQQHDBFRdeG6rW4gSG/DoG5nIE1haTFCMEAG
        A1UECgw5Q8OUTkcgVFkgQ1AgROG7ikNIIFbhu6QgVsOAIEPDlE5HIE5HSOG7hiBT
        4buQIFFVQU5HIFRSVU5HMUIwQAYDVQQDDDlDw5RORyBUWSBDUCBE4buKQ0ggVuG7
        pCBWw4AgQ8OUTkcgTkdI4buGIFPhu5AgUVVBTkcgVFJVTkcxHjAcBgoJkiaJk/Is
        ZAEBDA5NU1Q6MDExMDE4ODA2NTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC
        ggEBAJO6JDU+kNEUIiO6m75LOfgHkwGExYFv0tILHInS9CkK2k0FjmvU8VYJ0cQA
        sGGabpHIwfh07llLfK3TUZlhnlFZYRrYvuexlLWQydjHYPqT+1c3iYaiXXcOqEjm
        OupCj71m93ThFrYzzI2Zx07jccRptAAZrWMjI+30vJN7SDxhYsD1uQxYhUkx7psq
        MqD4/nOyaWzZHLU94kTAw5lhAlVOMu3/6pXhIltX/097Wji1eyYqHFu8w7q3B5yW
        gJYugEZfplaeLLtcTxok4VbQCb3cXTOSFiQYJ3nShlBd89AHxaVE+eqJaMuGj9z9
        rdIoGr9LHU/P6KF+/SLwxpsYgnkCAwEAAaOCAVUwggFRMAwGA1UdEwEB/wQCMAAw
        HwYDVR0jBBgwFoAUyCcJbMLE30fqGfJ3KXtnXEOxKSswgZUGCCsGAQUFBwEBBIGI
        MIGFMDIGCCsGAQUFBzAChiZodHRwczovL3Jvb3RjYS5nb3Yudm4vY3J0L3ZucmNh
        MjU2LnA3YjAuBggrBgEFBQcwAoYiaHR0cHM6Ly9yb290Y2EuZ292LnZuL2NydC9J
        LUNBLnA3YjAfBggrBgEFBQcwAYYTaHR0cDovL29jc3AuaS1jYS52bjA0BgNVHSUE
        LTArBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcKAwwGCSqGSIb3LwEBBTAj
        BgNVHR8EHDAaMBigFqAUhhJodHRwOi8vY3JsLmktY2Eudm4wHQYDVR0OBBYEFE6G
        FFM4HXne9mnFBZInWzSBkYNLMA4GA1UdDwEB/wQEAwIE8DANBgkqhkiG9w0BAQsF
        AAOCAQEAH5ifoJzc8eZegzMPlXswoECq6PF3kLp70E7SlxaO6RJSP5Y324ftXnSW
        0RlfeSr/A20Y79WDbA7Y3AslehM4kbMr77wd3zIij5VQ1sdCbOvcZXyeO0TJsqmQ
        b46tVnayvpJYW1wbui6smCrTlNZu+c1lLQnVsSrAER76krZXaOZhiHD45csmN4dk
        Y0T848QTx6QN0rubEW36Mk6/npaGU6qw6yF7UMvQO7mPeqdufVX9duUJav+WBJ/I
        Y/EdqKp20cAT9vgNap7Bfgv5XN9PrE+Yt0C1BkxXnfJHA7L9hcoYrknsae/Fa2IP
        99RyIXaHLJyzSTKLRUhEVqrycM0UXg==
        -----END CERTIFICATE-----
        """.trimIndent()
    val privateKey = """
        -----BEGIN PRIVATE KEY-----
        MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCiOMdedNfAhAdI
        M1YmUd2hheu2vDMmFHjCfWHon8wv0doubYPY6/uhMcUERpPiFddWqe+Dfr/XwCsa
        EaPOa27ghyUQ8HjdzAxcZ1yTWrgWttGruHlrHoXDPaov3QqvJTUrBclsH8p3ufPp
        gmBC0HrFD0Pl4+vEpki4VvCDJFEGuBaSAqFe7JqUuaOVRG9mBBZWslkNi8XNkAQT
        /Es+zReMf4EXIO2+wMo3aPIhe+sSZ3e3VqFL/10EJqNhurOT5ijUwReMlNb9wcxu
        drfSKjLOgW1n+ZLjo16GdS2ye68B7ZaA0J3DPuDdRXJ5YuoW4UQd8o6CyezIHWpP
        vH1tWFABAgMBAAECggEAB485yy8Kts/wPu8Vfqel+lbxSwyuHYIqtnV9UIfRzhCr
        aCp2UG9+xF47Xh2j2o9F/6XfoXMQoY808vwLdB0Rh6kEkyuBlmRh1xSB/ePmXDic
        wLHSBqnfdd+zxJM6YjsLpTuZzU4V80pZEXKf5b0tW22Arn/Whs1w6hYzEwloNTXf
        4K974i+st1E5/0JjufTBTOTlBtwbphwN9ia/Xs2EY3D6kuJhYZ5lCWDocD21xYWd
        NPM2CWqVXjJYEaqDTIWGwNGb2hkwNG5t/9MnN2On6BR7kgOWU4XxXHoLD3XoErwB
        M3J8QAXGZwb+wRtkzRCVgojA6AQXfu9/QyPjyHW4oQKBgQDYMEC+LuNtjrNju8yF
        LHMFbYbSfBQITE+kJn7iemezkwJw25NuKWl0pcxPe+NtpaHNFDmHnTVrlICTh90c
        qrtge1vsqtgEoaZfdYqkUVvl1jJWBJ+VqQNO2Nxos/6fM0ARDC/9YXHoDWKC4WeS
        PvYJ55MkMHseddpKIUGrZ1xO5QKBgQDAGGFxC9xWhG/CEm/JAFul+uyp9ncG6ro/
        47Tw75M5+2K9wsP2R2c0uoXZtQHFvvi9CADaQkSYrzY3wCqgjDhsR+3psN1R+Pkw
        bgMf3Rt6bMrYemPaGOe9qZ+Dpw/2GnLZfmCcJfKoRfY73YsxlL4/0Zf1va/qZnbp
        pGh4IlvO7QKBgD87teQq0Mi9wYi9aG/XdXkz9Qhh1HYs4+qOe/SAew6SRFeAUhoZ
        sMe2qxDgmr/6f139uWoKOJLT59u/FJSK962bx2JtAiwwn/ox5jBzv551TVnNlmPv
        AJGyap2RcDtegTG7T9ocA3YtXBAOH/4tvkddXbNrHsflDsk5+vxIij5lAoGAFli/
        vS7sCwSNG76ZUoDAKKbwMTWC00MrN5N90SmNrwkXi4vE0DmuP+wS9iigdCirNxJf
        RwS+hiSb4hBw5Qxq4+3aN31jwc18761cn7BRKgTN9DEIvK55Bw9chyxAJxkck0Co
        bIHdoMXCx2QWdUYge7weOXA/rr0MyFFf9dnJZGECgYEAuhJrRoxLdyouTd6X9+R1
        8FWY0XGfsBp+PkN/nnPuK6IJR/IeI+cdiorfm45l4ByF0XEBCDz2xXQ6MVBNz3zF
        MjEQ61dTFRfiTW2ZDqhMTtZH4R4T5NLWf+3ItjkAkOdStszplhHy0bUQIYgptYXd
        5Sw/UvMv83CmlztVC5tGG9o=
        -----END PRIVATE KEY-----
        """.trimIndent()
    //        val appId = "com.qts.test"
//        if (deviceId.isNullOrEmpty()) {
//            deviceId = UUID.randomUUID().toString()
//        }
//        deviceId = "f8552f6d-35da-45f0-9761-f38fe1ea33d1"
//        val transactionId="TEST"
//        val transactionId = "51219b1d-fc4e-4005-a988-4183e76fcd97"
    val request: LivenessRequest = LivenessRequest()
    request.duration = 600
    request.privateKey = privateKey
    request.publicKey = public_key
    request.baseURL = "https://ekyc-sandbox.eidas.vn/face-matching"
    request.appId = AppConst.APP_ID
    request.ownerId = "123"
    //        request.setOptionHeader((HashMap<String, String>) optionHeader);
//        request.setOptionRequest((HashMap<String, String>) optionRequest);
    request.isDebug = true
    //    request.setColorConfig(List.of("FFFFF00", "FF800080", "FFFFA500"));
    request.clientTransactionId = transactionId
    if (swMode?.isChecked == true) {
      request.offlineMode = true
      val sColor: String = etColor?.text.toString()
      val sFrame: String = etFrame?.text.toString()
      var color = 0
      var frame = 60
      if (sColor.isNotEmpty()) {
        try {
          color = sColor.toInt()
        } catch (e: NumberFormatException) {
          e.printStackTrace()
        }
      }
      if (sFrame.isNotEmpty()) {
        try {
          frame = sFrame.toInt()
        } catch (e: NumberFormatException) {
          e.printStackTrace()
        }
      }

      val config: DataConfig = DataConfig(color, frame)
      request.dataConfig = config
    }
    val colorConfig: MutableList<Long> = ArrayList()
    if (cbYellow?.isChecked == true) {
      colorConfig.add(0xFFFFFF00L)
    }
    if (cbPurple?.isChecked == true) {
      colorConfig.add(0xFF800080L)
    }
    if (cbOrange?.isChecked == true) {
      colorConfig.add(0xFFFFA500L)
    }
    //        if (!colorConfig.isEmpty()) {
//            request.setColorConfig(colorConfig);
//        }
    request.verifyLevel = getLevel()
    return request
  }

  private fun getLevel(): VerifyLevel {
//    return if (rbLow.isChecked()) {
//      VerifyLevel.LOW
//    } else if (rbMedium.isChecked()) {
//      VerifyLevel.MEDIUM
//    } else {
//      VerifyLevel.HIGH
//    }
    return VerifyLevel.LOW
  }

  private fun showEmbeddedScanNfc() {
    scanNfcBottomSheetDialog = ScanNfcBottomSheetDialog.newInstance()
    scanNfcBottomSheetDialog?.show(supportFragmentManager, scanNfcBottomSheetDialog!!.tag)
  }

  private fun showPluginScanNfc(usbDevice: UsbDevice) {
//        scanNfcWithDeviceDialog = new ScanNfcWithDeviceDialog(usbDevice, mMRZInfo, new ScanNfcWithDeviceDialog.OnScanCardListener() {
//            @Override
//            public void onSuccess(RawScanData data) {
//                Log.d("Thuytv", "-----Data Scan:");
//                Log.d("Thuytv", "-----DG1: " + data.getDg1());
//                Log.d("Thuytv", "-----DG2: " + data.getDg2());
//                Log.d("Thuytv", "-----DG13: " + data.getDg13());
//                Log.d("Thuytv", "-----SOD: " + data.getSod());
//                Log.d("Thuytv", "-----DG14: " + data.getDg14());
//                Log.d("Thuytv", "-----DG15: " + data.getDg15());
//                String mes= "DG1: "+ data.getDg1()+"\n"+"DG2: "+ data.getDg2()+"\n"+"DG13: "+ data.getDg13()+"\n"+"SOD: "+ data.getDg13()+"\n"+"DG14: "+ data.getDg14()+"\n"+"DG15: "+ data.getDg15();
//                Toast.makeText(MainActivity.this, mes, Toast.LENGTH_SHORT).show();
//                //todo call api readCard
//            }
//        }, MainActivity.this);
//        scanNfcWithDeviceDialog.show(getSupportFragmentManager(), scanNfcWithDeviceDialog.getTag());


    if (card.init_Device(usbDevice).toString().trim { it <= ' ' } != "") {
//            deviceFragment.device= usbDevice;
//            FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
//            transaction.add(R.id.bottom_container, deviceFragment);
//            transaction.addToBackStack(null);
//            transaction.commitAllowingStateLoss();


      scanNfcWithDeviceDialog =
        ScanNfcWithDeviceDialog(usbDevice, mMRZInfo
        ) { data ->
          Log.d("Thuytv", "-----Data Scan:")
          Log.d("Thuytv", "-----DG1: " + data.dg1)
          Log.d("Thuytv", "-----DG2: " + data.dg2)
          Log.d("Thuytv", "-----DG13: " + data.dg13)
          Log.d("Thuytv", "-----SOD: " + data.sod)
          Log.d("Thuytv", "-----DG14: " + data.dg14)
          Log.d("Thuytv", "-----DG15: " + data.dg15)
          val cardReaderResult = CardReaderResult()
          cardReaderResult.sodData = data.sod.replace("\n", "")
          cardReaderResult.dg1Data = data.dg1.replace("\n", "")
          cardReaderResult.dg2Data = data.dg2.replace("\n", "")
          cardReaderResult.dg13Data = data.dg13.replace("\n", "")
          cardReaderResult.dg14Data = data.dg14.replace("\n", "")
          mLoadingLayout?.visibility = View.VISIBLE
          ReadCardDataTark(cardReaderResult, mBackCardImage!!).execute()
          //todo call api readCard
        }
      scanNfcWithDeviceDialog!!.show(supportFragmentManager, scanNfcWithDeviceDialog!!.tag)
    } else {
      Toast.makeText(this, "can't init", Toast.LENGTH_SHORT).show()
    }
  }

  private val deviceFragment: DeviceFragment = DeviceFragment()
  private val callback: SCardReaderListCallback? = null
  private val card = SCardAiot()

  private fun checkNfc(): Boolean {
    val manager = getSystemService(NFC_SERVICE) as NfcManager
    val adapter = manager.defaultAdapter
    return adapter != null
  }

  private fun requestPermissionForCamera() {
    val permissions = arrayOf(
      permission.CAMERA,
      permission.MANAGE_EXTERNAL_STORAGE,
      permission.WRITE_EXTERNAL_STORAGE,
      permission.READ_EXTERNAL_STORAGE
    )
    val isPermissionGranted: Boolean = PermissionUtil.hasPermissions(this, *permissions)

    if (!isPermissionGranted) {
//            AppUtil.showAlertDialog(this, getString(R.string.permission_title), getString(R.string.permission_description), getString(R.string.button_ok), false, (dialogInterface, i) -> ActivityCompat.requestPermissions(this, permissions, PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS));
      ActivityCompat.requestPermissions(
        this,
        permissions,
        PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS
      )
    } else {
      openCameraActivity()
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String?>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS) {
      val result = grantResults[0]
      if (result == PackageManager.PERMISSION_DENIED) {
        if (!PermissionUtil.showRationale(this, permissions[0])) {
          val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
          val uri = Uri.fromParts("package", packageName, null)
          intent.setData(uri)
          startActivityForResult(intent, APP_SETTINGS_ACTIVITY_REQUEST_CODE)
        } else {
          requestPermissionForCamera()
        }
      } else if (result == PackageManager.PERMISSION_GRANTED) {
        openCameraActivity()
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    if (!mReadingNFC && NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
      mReadingNFC = true
      val mTag = intent.extras!!.getParcelable<Tag>(NfcAdapter.EXTRA_TAG)
      //            if (mTag.getTechList().)
      if (mMRZInfo != null) {
        val bacKey: BACKeySpec = BACKey(
          mMRZInfo!!.documentNumber,
          mMRZInfo!!.dateOfBirth,
          mMRZInfo!!.dateOfExpiry
        )
        scanNfcBottomSheetDialog?.startReadingNfc()
        ReadTask(
          IsoDep.get(mTag),
          mMRZInfo!!.documentNumber,
          mMRZInfo!!.dateOfBirth,
          mMRZInfo!!.dateOfExpiry,
          mBackCardImage!!
        ).execute()
      } else {
        mReadingNFC = false
      }
    }
  }

  enum class CameraType {
    OCR, FACE
  }

  @SuppressLint("StaticFieldLeak")
  private inner class ReadCardDataTark(var carData: CardReaderResult, private val backCardImage: String) :
    AsyncTask<Void?, Void?, java.lang.Exception?>() {
    private var cardInfo: IdCardInfo? = null
    private var errorCode: String? = null

    init {
      scanNfcCount++
    }

    override fun doInBackground(vararg p0: Void?): java.lang.Exception? {
      try {
        val duration = 30
        cardInfo = CardReader.getInstance().readCard(
          carData,
          this.backCardImage, duration, null, null
        )
        errorCode = ""
      } catch (e: CardReaderException) {
        Log.e(TAG, "Read card exception")
        e.printStackTrace()
        errorCode = e.errorCode.name
      }
      return null
    }

    override fun onPostExecute(e: java.lang.Exception?) {
      var isEmptyInfo = false
      if (e == null && errorCode!!.isEmpty() && cardInfo != null) {
        val intent = if (getCallingActivity() != null) Intent() else Intent(
          this@MainActivity,
          ScanNfcResultActivity::class.java
        )
        requestId = cardInfo!!.requestId
        if (cardInfo!!.citizenIdentifyCard != null && cardInfo!!.citizenIdentifyCard.length > 0) {
          isEmptyInfo = false
          intent.putExtra(ScanNfcResultActivity.KEY_CARD_ID, cardInfo!!.citizenIdentifyCard)
          intent.putExtra(ScanNfcResultActivity.KEY_CARD_OLD, cardInfo!!.oldIdentifyCard)
          intent.putExtra(ScanNfcResultActivity.KEY_FULLNAME, cardInfo!!.fullName)
          intent.putExtra(ScanNfcResultActivity.KEY_BIRTH, cardInfo!!.birthday)
          intent.putExtra(ScanNfcResultActivity.KEY_EXPIRE, cardInfo!!.dateOfExpiry)
          intent.putExtra(ScanNfcResultActivity.KEY_ETHNIC, cardInfo!!.ethnic)
          intent.putExtra(ScanNfcResultActivity.KEY_RELIGION, cardInfo!!.religion)
          intent.putExtra(ScanNfcResultActivity.KEY_GENDER, cardInfo!!.gender)
          intent.putExtra(ScanNfcResultActivity.KEY_STATE, cardInfo!!.nationality)
          intent.putExtra(ScanNfcResultActivity.KEY_NATIONALITY, cardInfo!!.nationality)
          intent.putExtra(ScanNfcResultActivity.KEY_ORIGIN, cardInfo!!.placeOfOrigin)
          intent.putExtra(
            ScanNfcResultActivity.KEY_REGISTER_PLACE_ADDRESS,
            cardInfo!!.placeOfResidence
          )
          intent.putExtra(
            ScanNfcResultActivity.KEY_IDENTIFY_CHARACTER,
            cardInfo!!.personalIdentification
          )
          intent.putExtra(
            ScanNfcResultActivity.KEY_PASSIVE_AUTH,
            if (cardInfo!!.passiveAuthSuccess) getString(R.string.pass) else getString(R.string.failed)
          )
          intent.putExtra(
            ScanNfcResultActivity.KEY_CHIP_AUTH,
            if (cardInfo!!.chipAuthSucceeded) getString(R.string.pass) else getString(R.string.failed)
          )
          intent.putExtra(ScanNfcResultActivity.KEY_FATHER, cardInfo!!.fatherName)
          intent.putExtra(ScanNfcResultActivity.KEY_MOTHER, cardInfo!!.motherName)
          intent.putExtra(ScanNfcResultActivity.KEY_TRANSACTION_ID, cardInfo!!.transactionId)
          if (cardInfo!!.partnerName != null && cardInfo!!.partnerName.length > 0) {
            intent.putExtra(ScanNfcResultActivity.KEY_PARTNER, cardInfo!!.partnerName)
          }

          if (cardInfo!!.faceBitmap != null) {
            DataUtil.CARD_IMAGE = cardInfo!!.faceBitmap
            val bitmap = cardInfo!!.faceBitmap
            val ratio = 320.0 / bitmap.height
            val height = (bitmap.height * ratio).toInt()
            val width = (bitmap.width * ratio).toInt()
            val scaledBitMap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            // save card id photo for later use at liveness check result
//                        DataUtil.SaveCardIdPhoto(scaledBitMap);
            intent.putExtra(ScanNfcResultActivity.KEY_PHOTO, scaledBitMap)
          }
          updatePassedStep(2)
          mReadingNFC = false
          mLoadingLayout?.setVisibility(View.GONE)
          scanNfcWithDeviceDialog?.completeReadingNfc()
          if (getCallingActivity() != null) {
            setResult(RESULT_OK, intent)
            finish()
          } else {
            startActivityForResult(
              intent,
              APP_RESULT_NFC_ACTIVITY_REQUEST_CODE
            )
          }
        } else {
          isEmptyInfo = true
        }
      }
      if (!errorCode!!.isEmpty()) {
        scanNfcWithDeviceDialog?.readNfcError(errorCode, scanNfcCount)
      }
      scanNfcWithDeviceDialog?.endScanNfc()
      mReadingNFC = false
    }
  }

  @SuppressLint("StaticFieldLeak")
  inner class InitCardReaderSdk(private val appId: String, private val baseURL: String, private val publicKey: String, private val privateKey: String) : AsyncTask<Void?, Void?, Exception?>() {
    private var initSuccess = false

    override fun doInBackground(vararg p0: Void?): Exception? {
      val context: Context = applicationContext
      try {
        val optionalHeader: MutableMap<String, String> = HashMap()
        optionalHeader["header1"] = "test1"
        optionalHeader["header2"] = "test2"
        val optionalBody = JSONObject()
        optionalBody.put("request1", "test1")
        optionalBody.put("request2", "test2")
        CardReader.getInstance().init(
          context,
          baseURL,
          appId,
          privateKey,
          publicKey,
          optionalHeader,
          optionalBody
        )

        //                CardReader.getInstance().init(
//                        context,
//                        "https://ekyc-sandbox.eidas.vn/ekyc",
//                        "com.mobile.vtc.smartAgent",
//                        "-----BEGIN PRIVATE KEY-----\n" +
//                                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3PyggRyu4LViC\n" +
//                                "XXRpuAnvhSmwuU03CRtN6Pn7UwdrgqRGqGpzh5OCWk3lvyCVz8avDf0RRSUXsRuq\n" +
//                                "9OSkLI8P9QSvyiF7ajbEPVkMUS46Y5c0ClTJWDearm6Ck5OB6/NoIdm8V8NQsxsl\n" +
//                                "C0BnOPrNs8u7cf4FcpVnT88syAJdnyqsBx35zfHXrNdXm0fZefFc0k3TqXtOM/iW\n" +
//                                "zAWZTjhQCVg7Mo9+H91MKBdpqFyAbZ8hmfUp2mIUjlhwJ7zbR0g3HRtQNIP8yd1y\n" +
//                                "soolgT17pSkVMEFjCSn7BpVm92Hh+i6tL1WRUOybTymV0DmZSgqDo7c2Yfn2X4AK\n" +
//                                "plgHX5ODAgMBAAECggEAN4WSUd1emMpcg/tu901E7WbKj/pFNAS1WjDZ9VVd7SxD\n" +
//                                "PAlxX5nXjupvqcn0RJAU4ht57w6lQ31tJapX0IqTukEB74VuCafP+jugTIQYXqao\n" +
//                                "PKNYtzzcdCif4T53tijYsld3UoLA8iWiduHm3J++Fi+A7kWGsv3Z2NisrCPZ26WH\n" +
//                                "+usSnGDm8P1Os2Gm0lXuloyoQuzuCFSJ3w2YmYrzqouPFvW4s9O9aJj1bZWbd50E\n" +
//                                "Fy+HBWaB27Kl7CXTQ5L8z+3Q/k+zlf4OASQMa4CtCfoYNYc+kIgAnGtbCHXv5O9J\n" +
//                                "GrBD+c3TBS9gnhoz0FIsYkzl9SiM81+JwjX+QGYcaQKBgQDhqLw8SUqtXKIntMzB\n" +
//                                "g/nh82Md5ko3AO23loEAYFWxwx/c9T3FshDAwuJIZC+O8VjCNOaPjOR8Ba+GP8eR\n" +
//                                "DZ9nEZN9WjDzgdPJAGxJqkMs+X3i0p5Pp2b6OEEU2HOZAwEqyxX4hETqYJIWTPJd\n" +
//                                "mpsuTRktJaRFTy6cfa8GZCF/7wKBgQDP4pJMJbV9xl4W7p8AZbV5NjXllVrRzZU6\n" +
//                                "kr3/UO4lClzo8Lp7NlLUv4tFewkDo4Wy+9dTI7qDknRPjh4yTwnXmDfhseH3+5C8\n" +
//                                "B4Aunl9VDDbLUziPxKaYBv9RWRBvCjlBoxcZSe0lqDNw43jTDcerJva6Jwm3cFQV\n" +
//                                "M6Bhh1bRrQKBgQCKYUwVeCh4SU8TJFdP8FvDSNqmUjt+oW4lYAD5txm16d9XQ3i/\n" +
//                                "DCQj4R6cn/teH/h7F6+vpgnTWDU8EU6TUBRJQ0j73NwZ56p1H3JIdkW9hb3B48C7\n" +
//                                "Pf/zP54BZ37z/RixIlr1zD7qr4/GZydVaWLyMMtYmTW0XwtJYfV76Nl2NwKBgGId\n" +
//                                "KjUcOwtQm0mxd4PFfR/nMQrZMdMBd2P8rAWbWbpLqMwcCbdrTS6x9HZnejqoa8qs\n" +
//                                "x9SqcTX14kVF6wSNfzSs1/sCbIcQyR+lXp8iChZmvFm8Pl7ETmhELS5knbf6mEld\n" +
//                                "QuKhnHaAaz7lbToXL9yceCglkpVsQ5L6GcDOEnDFAoGBALMFN6lELpx51/G0Czt7\n" +
//                                "g8TKEAarBCQDJj/ylnAmruc3z4Utenp59FqC1/YZByEYn6aPA6r0DLJv+G/bIbUk\n" +
//                                "Ot0P8ommJBaPktxniBnXTuMAMDynn9LoF9LAT8KLp02caDsEXoZEp4m+nBfgiYYA\n" +
//                                "Q4UTTnXfXIVe8QvuKV2CH0xS\n" +
//                                "-----END PRIVATE KEY-----",
//                        "-----BEGIN CERTIFICATE-----\n" +
//                                "MIIE8jCCA9qgAwIBAgIQVAESDxKv/JtHV15tvtt1UjANBgkqhkiG9w0BAQsFADAr\n" +
//                                "MQ0wCwYDVQQDDARJLUNBMQ0wCwYDVQQKDARJLUNBMQswCQYDVQQGEwJWTjAeFw0y\n" +
//                                "MzA2MDcwNjU1MDNaFw0yNjA2MDkwNjU1MDNaMIHlMQswCQYDVQQGEwJWTjESMBAG\n" +
//                                "A1UECAwJSMOgIE7hu5lpMRowGAYDVQQHDBFRdeG6rW4gSG/DoG5nIE1haTFCMEAG\n" +
//                                "A1UECgw5Q8OUTkcgVFkgQ1AgROG7ikNIIFbhu6QgVsOAIEPDlE5HIE5HSOG7hiBT\n" +
//                                "4buQIFFVQU5HIFRSVU5HMUIwQAYDVQQDDDlDw5RORyBUWSBDUCBE4buKQ0ggVuG7\n" +
//                                "pCBWw4AgQ8OUTkcgTkdI4buGIFPhu5AgUVVBTkcgVFJVTkcxHjAcBgoJkiaJk/Is\n" +
//                                "ZAEBDA5NU1Q6MDExMDE4ODA2NTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC\n" +
//                                "ggEBAJO6JDU+kNEUIiO6m75LOfgHkwGExYFv0tILHInS9CkK2k0FjmvU8VYJ0cQA\n" +
//                                "sGGabpHIwfh07llLfK3TUZlhnlFZYRrYvuexlLWQydjHYPqT+1c3iYaiXXcOqEjm\n" +
//                                "OupCj71m93ThFrYzzI2Zx07jccRptAAZrWMjI+30vJN7SDxhYsD1uQxYhUkx7psq\n" +
//                                "MqD4/nOyaWzZHLU94kTAw5lhAlVOMu3/6pXhIltX/097Wji1eyYqHFu8w7q3B5yW\n" +
//                                "gJYugEZfplaeLLtcTxok4VbQCb3cXTOSFiQYJ3nShlBd89AHxaVE+eqJaMuGj9z9\n" +
//                                "rdIoGr9LHU/P6KF+/SLwxpsYgnkCAwEAAaOCAVUwggFRMAwGA1UdEwEB/wQCMAAw\n" +
//                                "HwYDVR0jBBgwFoAUyCcJbMLE30fqGfJ3KXtnXEOxKSswgZUGCCsGAQUFBwEBBIGI\n" +
//                                "MIGFMDIGCCsGAQUFBzAChiZodHRwczovL3Jvb3RjYS5nb3Yudm4vY3J0L3ZucmNh\n" +
//                                "MjU2LnA3YjAuBggrBgEFBQcwAoYiaHR0cHM6Ly9yb290Y2EuZ292LnZuL2NydC9J\n" +
//                                "LUNBLnA3YjAfBggrBgEFBQcwAYYTaHR0cDovL29jc3AuaS1jYS52bjA0BgNVHSUE\n" +
//                                "LTArBggrBgEFBQcDAgYIKwYBBQUHAwQGCisGAQQBgjcKAwwGCSqGSIb3LwEBBTAj\n" +
//                                "BgNVHR8EHDAaMBigFqAUhhJodHRwOi8vY3JsLmktY2Eudm4wHQYDVR0OBBYEFE6G\n" +
//                                "FFM4HXne9mnFBZInWzSBkYNLMA4GA1UdDwEB/wQEAwIE8DANBgkqhkiG9w0BAQsF\n" +
//                                "AAOCAQEAH5ifoJzc8eZegzMPlXswoECq6PF3kLp70E7SlxaO6RJSP5Y324ftXnSW\n" +
//                                "0RlfeSr/A20Y79WDbA7Y3AslehM4kbMr77wd3zIij5VQ1sdCbOvcZXyeO0TJsqmQ\n" +
//                                "b46tVnayvpJYW1wbui6smCrTlNZu+c1lLQnVsSrAER76krZXaOZhiHD45csmN4dk\n" +
//                                "Y0T848QTx6QN0rubEW36Mk6/npaGU6qw6yF7UMvQO7mPeqdufVX9duUJav+WBJ/I\n" +
//                                "Y/EdqKp20cAT9vgNap7Bfgv5XN9PrE+Yt0C1BkxXnfJHA7L9hcoYrknsae/Fa2IP\n" +
//                                "99RyIXaHLJyzSTKLRUhEVqrycM0UXg==\n" +
//                                "-----END CERTIFICATE-----",
//                        optionalHeader,
//                        optionalBody
//                );
        initSuccess = true
      } catch (e: CardReaderInitException) {
        initSuccess = false
        e.printStackTrace()
      } catch (e: JSONException) {
        throw RuntimeException(e)
      }
      return null
    }

    override fun onPostExecute(e: Exception?) {
      if (!initSuccess) {
        // TODO: do something
      }
    }
  }


  @SuppressLint("StaticFieldLeak")
  private inner class ReadTask(
    private val mIsoDep: IsoDep,
    private val documentNumber: String,
    private val dateOfBirth: String,
    private val expiryDate: String,
    private val backCardImage: String
  ) :
    AsyncTask<Void?, Void?, Exception?>() {
    private var cardInfo: IdCardInfo? = null
    private var errorCode: String? = null

    init {
      scanNfcCount++
    }

    override fun onPostExecute(e: Exception?) {
      var isEmptyInfo = false
      if (e == null && errorCode!!.isEmpty() && cardInfo != null) {
        val intent = if (getCallingActivity() != null) Intent() else Intent(
          this@MainActivity,
          ScanNfcResultActivity::class.java
        )
        requestId = cardInfo!!.requestId
        if (cardInfo!!.citizenIdentifyCard != null && cardInfo!!.citizenIdentifyCard.length > 0) {
          isEmptyInfo = false
          intent.putExtra(ScanNfcResultActivity.KEY_CARD_ID, cardInfo!!.citizenIdentifyCard)
          intent.putExtra(ScanNfcResultActivity.KEY_CARD_OLD, cardInfo!!.oldIdentifyCard)
          intent.putExtra(ScanNfcResultActivity.KEY_FULLNAME, cardInfo!!.fullName)
          intent.putExtra(ScanNfcResultActivity.KEY_BIRTH, cardInfo!!.birthday)
          intent.putExtra(ScanNfcResultActivity.KEY_EXPIRE, cardInfo!!.dateOfExpiry)
          intent.putExtra(ScanNfcResultActivity.KEY_ETHNIC, cardInfo!!.ethnic)
          intent.putExtra(ScanNfcResultActivity.KEY_RELIGION, cardInfo!!.religion)
          intent.putExtra(ScanNfcResultActivity.KEY_GENDER, cardInfo!!.gender)
          intent.putExtra(ScanNfcResultActivity.KEY_STATE, cardInfo!!.nationality)
          intent.putExtra(ScanNfcResultActivity.KEY_NATIONALITY, cardInfo!!.nationality)
          intent.putExtra(ScanNfcResultActivity.KEY_ORIGIN, cardInfo!!.placeOfOrigin)
          intent.putExtra(
            ScanNfcResultActivity.KEY_REGISTER_PLACE_ADDRESS,
            cardInfo!!.placeOfResidence
          )
          intent.putExtra(
            ScanNfcResultActivity.KEY_IDENTIFY_CHARACTER,
            cardInfo!!.personalIdentification
          )
          intent.putExtra(
            ScanNfcResultActivity.KEY_PASSIVE_AUTH,
            if (cardInfo!!.passiveAuthSuccess) getString(R.string.pass) else getString(R.string.failed)
          )
          intent.putExtra(
            ScanNfcResultActivity.KEY_CHIP_AUTH,
            if (cardInfo!!.chipAuthSucceeded) getString(R.string.pass) else getString(R.string.failed)
          )
          intent.putExtra(ScanNfcResultActivity.KEY_FATHER, cardInfo!!.fatherName)
          intent.putExtra(ScanNfcResultActivity.KEY_MOTHER, cardInfo!!.motherName)
          intent.putExtra(ScanNfcResultActivity.KEY_TRANSACTION_ID, cardInfo!!.transactionId)
          if (cardInfo!!.partnerName != null && cardInfo!!.partnerName.length > 0) {
            intent.putExtra(ScanNfcResultActivity.KEY_PARTNER, cardInfo!!.partnerName)
          }

          if (cardInfo!!.faceBitmap != null) {
            DataUtil.CARD_IMAGE = cardInfo!!.faceBitmap
            val bitmap = cardInfo!!.faceBitmap
            val ratio = 320.0 / bitmap.height
            val height = (bitmap.height * ratio).toInt()
            val width = (bitmap.width * ratio).toInt()
            val scaledBitMap = Bitmap.createScaledBitmap(bitmap, width, height, false)
            // save card id photo for later use at liveness check result
//                        DataUtil.SaveCardIdPhoto(scaledBitMap);
            intent.putExtra(ScanNfcResultActivity.KEY_PHOTO, scaledBitMap)
          }
          updatePassedStep(2)
          mReadingNFC = false
          scanNfcBottomSheetDialog?.completeReadingNfc()
          if (getCallingActivity() != null) {
            setResult(RESULT_OK, intent)
            finish()
          } else {
            startActivityForResult(intent, APP_RESULT_NFC_ACTIVITY_REQUEST_CODE)
          }
        } else {
          isEmptyInfo = true
        }
      }
      if (errorCode!!.isNotEmpty()) {
        scanNfcBottomSheetDialog?.readNfcError(errorCode, scanNfcCount)
      }
      scanNfcBottomSheetDialog?.endScanNfc()
      mReadingNFC = false
    }

    override fun doInBackground(vararg p0: Void?): Exception? {
      try {
        val duration = 30

        cardInfo = CardReader.getInstance().readCard(
          this.mIsoDep,
          this.documentNumber,
          this.dateOfBirth,
          this.expiryDate,
          this.backCardImage, duration

        ) { integer -> scanNfcBottomSheetDialog?.updateProgress(integer) }
        errorCode = ""
      } catch (e: CardReaderException) {
        Log.e(TAG, "Read card exception")
        e.printStackTrace()
        errorCode = e.errorCode.name
      }
      return null
    }
  }

}
