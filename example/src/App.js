import React, { useState, useRef } from 'react';

import {
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  Platform,
  PixelRatio,
  TextInput,
  Alert,
  NativeModules,
} from 'react-native';

import SimpleModal from './SimpleModal';

import {
  LivenessView,
  registerFace
} from 'liveness-rn';
import { launchCamera } from 'react-native-image-picker';

const privateKey = `-----BEGIN PRIVATE KEY-----
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
`

const publicKey = `-----BEGIN CERTIFICATE-----
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
`
const appId = 'com.pvcb'
const baseURL = 'https://ekyc-sandbox.eidas.vn/face-matching'

export default function App() {
  const [status, setStatus] = useState(false);
  const [isFlashCamera, setIsFlashCamera] = useState(false);
  const [layout, setLayout] = useState({ width: 0, height: 0 });
  const ref = useRef(null);

  const [loginError, setLoginError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [text, setText] = useState('');

  const onStartLiveNess = () => {
    setStatus(prev => !prev);
  };

  const handleLayout = e => {
    const { height, width } = e.nativeEvent.layout;
    if (layout.width === width && layout.height === height) {
      return;
    }
    setLayout({ width, height });
  };

  return (
    <View style={styles.container}>
      {status && (
        <View style={[styles.view_camera, { width: isFlashCamera ? '100%' : '100%' }]} onLayout={handleLayout}>
          <LivenessView
            ref={ref}
            // key={isFlashCamera == null ? 'null' : isFlashCamera == false ? 'flash' : 'normal'}
            style={
              Platform.OS === 'ios' ? styles.view_liveness :
                {
                  height: PixelRatio.getPixelSizeForLayoutSize(layout.height),
                  width: PixelRatio.getPixelSizeForLayoutSize(layout.width),
                }
            }
            onSuccess={(event) => {
              let response = event.nativeEvent.response
              Alert.alert("Thông báo", JSON.stringify(response))
              console.log(JSON.stringify(response))
            }}
            idCardRequestId={text}
            debugging={false}
            // useFlash={true}
            appId={appId}
            baseURL={baseURL}
            publicKey={publicKey}
            privateKey={privateKey}
          />
        </View>
      )}
      {!status && <TextInput
        style={styles.input}
        placeholder="ID Card Reader Request ID"
        value={text}
        onChangeText={(newText) => setText(newText)}
      />}
      <View style={{ position: 'absolute', width: '80%', zIndex: 1000, bottom: 20, alignSelf: 'center' }}>
        <TouchableOpacity onPress={onStartLiveNess} style={styles.btn_liveness}>
          <Text>Start Liveness</Text>
        </TouchableOpacity>
      </View>
      {
        !status && <View style={{ position: 'absolute', width: '80%', zIndex: 1000, bottom: 70, alignSelf: 'center' }}>
          <TouchableOpacity onPress={() => {
            NativeModules.CardReaderRn.readCard(appId, 'https://ekyc-sandbox.eidas.vn/ekyc', publicKey, privateKey)
            .then(idCardRequestId => {
              setText(idCardRequestId)
            })
        }} style={styles.btn_liveness}>
          <Text>Scan NFC</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => {
          launchCamera({
            mediaType: 'photo',
            cameraType: 'front'
          })
          .then(response => {
            if (!response.didCancel && !response.error) {
              console.log(response.assets[0])
              return Promise.all([
                registerFace({ image: response.assets[0].uri }),
              ])
            }
          })
          .catch(error => {

          })
        }} style={styles.btn_liveness}>
          <Text>Register face</Text>
        </TouchableOpacity>
      </View>
      }
      <SimpleModal
        isOpen={loginError}
        setIsOpen={setLoginError}
        onConfirm={() => {
          setLoginError(false);
          setStatus(false);
        }}
        errorMessage={errorMessage}
      />
    </View>
  );
}


const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  view_camera: {
    height: '100%',
    // height: Platform.OS == 'android' ? windowWidth * 1.7 : '100%',
    backgroundColor: 'transparent',
  },
  view_liveness: {
    flex: 1,
  },
  btn_liveness: {
    padding: 10,
    backgroundColor: '#c0c0c0',
    width: '90%',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 12,
    marginBottom: 24,
    zIndex: 1000,
  },
  btn_register_face: {
    padding: 10,
    backgroundColor: '#c0c0c0',
    width: '90%',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 12,
    marginBottom: 24,
  },
  input: {
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    paddingHorizontal: 10,
    marginBottom: 20,
    color: 'black',
  },
});
