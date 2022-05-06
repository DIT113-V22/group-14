package platis.solutions.smartcarmqttcontroller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.nio.file.Path;
import java.nio.file.Paths;

public class QrCodeTest {

    //String path = ("/Users/drago/Desktop/Car Club/group-14/android/SmartcarMqttController/app/src/main/java/platis/solutions/smartcarmqttcontroller");
   // Path path1 = Paths.get(path);

    public String decodeQRImage() {


        Bitmap bMap = BitmapFactory.decodeFile ("/Users/drago/Desktop/Car Club/group-14/android/SmartcarMqttController/app/src/main/java/platis/solutions/smartcarmqttcontroller/qrCode.png");
        String decoded = null;
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            decoded = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return decoded;
    }
    /*
    File sd = Environment.getExternalStorageDirectory();
    File image = new File(sd+filePath, imageName);
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
    bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
    imageView.setImageBitmap(bitmap); */
}