import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'liveness-rn' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const LivenessRn = NativeModules.LivenessRn
  ? NativeModules.LivenessRn
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export { default as LivenessView } from './LivenessView';

interface LivenessConfig {
  appId: String;
  baseURL: String;
  publicKey: String;
  privateKey: String;
}

export function setupLiveness({
  appId,
  baseURL,
  publicKey,
  privateKey,
}: LivenessConfig) {
  LivenessRn.setupLiveness(appId, baseURL, publicKey, privateKey);
}

export async function registerFace({ image }: { image: String }) {
  return LivenessRn.registerFace(image);
}
