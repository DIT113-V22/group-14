package plantholder.application;

// reference for QrCodeProcessing from
// https://stackoverflow.com/questions/36210537/find-qr-code-in-image-and-decode-it-using-zxing
// by user3646022

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


public class QrCodeProcessing {

    public String decodeQRImage(String imagesPath) {
        Bitmap bMap = BitmapFactory.decodeFile(imagesPath);
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
        if(decoded==null){
            decoded="Unreadable QR code";
        }
        return decoded;
    }
}