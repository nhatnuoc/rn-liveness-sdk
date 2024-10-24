import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'liveness-rn' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Liveness3DRn = NativeModules.Liveness3DRn
  ? NativeModules.Liveness3DRn
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

type ActionCallback = (status: any) => void;

export { default as Liveness3DView } from './Liveness3DView';

export function configure(
  appId: String,
  publicKey: String,
  privateKey: String,
  secret: String = '',
  baseURL: String = '',
  clientTransactionId: String = ''
) {
  Liveness3DRn.configure(
    appId,
    publicKey,
    privateKey,
    secret,
    baseURL,
    clientTransactionId
  );
}

export function getDeviceId(
  callback: ActionCallback | undefined | null = null
) {
  Liveness3DRn.getDeviceId(callback);
}

export function registerFace(
  image: String = '',
  callback: ActionCallback | undefined | null = null
) {
  Liveness3DRn.registerFace(image, callback);
}

export function startLiveNess(
  callback: ActionCallback | undefined | null = null
) {
  Liveness3DRn.startLiveNess(callback);
}

const Liveness3DSdk = {
  configure,
  getDeviceId,
  registerFace,
  startLiveNess,
};

export default Liveness3DSdk;
