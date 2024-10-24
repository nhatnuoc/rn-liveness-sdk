import { Platform, requireNativeComponent } from 'react-native';

const Liveness3DView =
  Platform.OS === 'ios'
    ? requireNativeComponent('RCTLiveness3DView')
    : requireNativeComponent('LivenessViewManager');
export default Liveness3DView;
