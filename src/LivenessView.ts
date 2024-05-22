import { Platform, requireNativeComponent } from 'react-native';

const LivenessView =
  Platform.OS === 'ios'
    ? requireNativeComponent('RCTLivenessView')
    : requireNativeComponent('LivenessViewManager');
export default LivenessView;
