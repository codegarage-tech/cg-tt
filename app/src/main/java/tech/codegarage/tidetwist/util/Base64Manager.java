package tech.codegarage.tidetwist.util;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class Base64Manager {

    private static String TAG = Base64Manager.class.getSimpleName();

    public static void encrypt(String filePath) {
        try {
            byte[] bytes = readBytesFromFile(filePath);
            if (bytes == null || bytes.length == 0) {
                Log.d(TAG, "encryption source is empty/null");
                throw new Exception("encryption Source file is empty/null");
            } else {
                Log.d(TAG, "encryption readBytesFromFile is successful");
            }

            String str = new String(bytes, "UTF-8");
            byte[] encryptedBase64 = Base64.encode(str.getBytes("UTF-8"), Base64.NO_WRAP);

            if (encryptedBase64 == null || encryptedBase64.length == 0) {
                Log.d(TAG, "Encrypted file is empty/null");
                throw new Exception("Encrypted file is empty/null");
            } else {
                Log.d(TAG, "Encrypted byte array is created");
            }
            delete(filePath);
            File destinationFile = new File(filePath);
            writeBytesToFile(destinationFile, encryptedBase64);
            Log.d(TAG, "encryption writeBytesToFile is successful");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public static void decrypt(String filePath) {
        try {
            byte[] bytes = readBytesFromFile(filePath);
            byte[] decryptedBase64 = null;
            if (bytes == null || bytes.length == 0) {
                Log.d(TAG, "decryption source is empty/null");
                throw new Exception("encryption Source file is empty/null");
            } else {
                Log.d(TAG, "decryption readBytesFromFile is successful");
            }

            String str = new String(bytes, "UTF-8");
            decryptedBase64 = Base64.decode(str.getBytes("UTF-8"), Base64.DEFAULT);
            Log.d(TAG, "Finished processing byte array");

            if (decryptedBase64 == null || decryptedBase64.length == 0) {
                Log.d(TAG, "Decrypted file is empty/null");
                throw new Exception("Decrypted file is empty/null");
            } else {
                Log.d(TAG, "Decrypted byte array is created");
            }
            delete(filePath);
            File destinationFile = new File(filePath);
            writeBytesToFile(destinationFile, decryptedBase64);
            Log.d(TAG, "decryption writeBytesToFile is successful");
        } catch (Exception e) {
            delete(filePath);
            Log.d(TAG, e.getMessage());
        }
    }

    private static boolean delete(String fullPath) {
        File file = new File(fullPath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    private static byte[] readBytesFromFile(String filePath) {
        InputStream is = null;
        byte[] bytes = null;

        try {
            File file = new File(filePath);
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                Log.d(TAG, "Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported " + Integer.MAX_VALUE + ")");
                throw new IOException("Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported " + Integer.MAX_VALUE + ")");
            }

            is = new FileInputStream(file);
            bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                Log.d(TAG, "Could not completely read file " + file.getName());
                throw new IOException("Could not completely read file " + file.getName());
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
        return bytes;
    }

    private static void writeBytesToFile(File theFile, byte[] bytes) {
        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = new FileOutputStream(theFile);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }
}