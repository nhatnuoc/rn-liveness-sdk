import React, { useEffect } from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import {
  configure,
  getDeviceId,
  startLiveNess,
  registerFace,
} from 'liveness-rn';
export default function App() {
  useEffect(() => {
    let appId = 'com.qts.test';
    configure(appId, '', '', '');
  }, []);

  const onStartLiveNess = () => {
    startLiveNess((data) => {
      console.log('startLiveNess', data);
    });
  };

  const onGetDeviceID = () => {
    getDeviceId((data) => {
      console.log('onGetDeviceID', data);
    });
  };

  const onRegisterFace = () => {
    registerFace(require('../assets/image.png'), (data) => {
      console.log('startLiveNess', data);
    });
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={onStartLiveNess} style={styles.btn_liveness}>
        <Text>Start LiveNess</Text>
      </TouchableOpacity>
      <TouchableOpacity
        onPress={onGetDeviceID}
        style={styles.btn_register_face}
      >
        <Text>get Device ID</Text>
      </TouchableOpacity>
      <TouchableOpacity
        onPress={onRegisterFace}
        style={styles.btn_register_face}
      >
        <Text>Start Register Face</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  btn_liveness: {
    padding: 10,
    backgroundColor: '#c0c0c0',
    width: '90%',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 12,
  },
  btn_register_face: {
    padding: 10,
    marginTop: 24,
    backgroundColor: '#c0c0c0',
    width: '90%',
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 12,
  },
});
