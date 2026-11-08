package @ANDROID_PACKAGE@;

import android.Manifest;
import android.app.NativeActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class Activity extends NativeActivity {
  static {
    System.loadLibrary("openxr_loader");
    System.loadLibrary("lovr");
  }

  private static final String PERMISSION_USE_SCENE = "com.oculus.permission.USE_SCENE";

  protected native void lovrPermissionEvent(int permission, boolean granted);

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

    int permission = switch (permissions[0]) {
      case Manifest.permission.RECORD_AUDIO -> 0;
      case PERMISSION_USE_SCENE -> 1;
      default -> throw new UnknownError("Invalid permission:" + permissions[0]);
    };

    lovrPermissionEvent(permission, granted);
  }

  private void requestUseScenePermission() {
    if (checkSelfPermission(PERMISSION_USE_SCENE) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { PERMISSION_USE_SCENE }, 2);
    } else {
      lovrPermissionEvent(1, true);
    }
  }

  private void requestAudioCapturePermission() {
    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { Manifest.permission.RECORD_AUDIO }, 1);
    } else {
      lovrPermissionEvent(0, true);
    }
  }
}
