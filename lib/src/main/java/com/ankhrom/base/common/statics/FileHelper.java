package com.ankhrom.base.common.statics;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import com.ankhrom.base.Base;

public final class FileHelper {

    /**
     * save private file to application internal storage
     *
     * @param file  name
     * @param bytes byte array data to write
     */
    public static void saveInternal(Context context, String file, byte[] bytes) {

        try {
            BufferedOutputStream bos = new BufferedOutputStream(context.openFileOutput(file, Context.MODE_PRIVATE));

            bos.write(bytes);

            bos.flush();
            bos.close();
        } catch (IOException e) {
            Base.logE("File: " + file + " cannot be write: " + e.getMessage());
        }
    }

    /**
     * load private file from application internal storage
     *
     * @param file name
     * @return file content in byte array
     */
    public static byte[] loadInternal(Context context, String file) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (existInternal(context, file, false)) {
            try {
                FileInputStream is = context.openFileInput(file);
                BufferedInputStream br = new BufferedInputStream(is);

                int b;
                byte[] bar = new byte[4096];
                while ((b = br.read(bar)) != -1) {
                    bos.write(bar, 0, b);
                }

                br.close();
                is.close();
            } catch (IOException e) {
                Base.logE("File: " + file + " cannot be read: " + e.getMessage());
            }
        } else {
            return null;
        }

        return bos.toByteArray();
    }


    /**
     * delete private file from application internal storage
     *
     * @param file name
     */
    public static void deleteInternal(Context context, String file) {

        context.deleteFile(file);
    }

    /**
     * check if file exist in application internal storage and creates this file if requested
     *
     * @param file             name
     * @param createIfNotExist true - to create file if not exist
     */
    public static boolean existInternal(Context context, String file, boolean createIfNotExist) {

        File outFile = context.getFileStreamPath(file);
        if (outFile.exists()) {
            return true;
        } else {
            if (createIfNotExist) {
                saveInternal(context, file, new byte[]{});
            }
            return false;
        }
    }

    /**
     * loads private files
     *
     * @return list of files
     */
    public static String[] internalFiles(Context context) {

        return context.getFilesDir().list(null);
    }

    /**
     * loads private file names and filter result
     *
     * @param filter filter by file names
     * @return list of files
     */
    public static String[] internalFiles(Context context, FilenameFilter filter) {

        return context.getFilesDir().list(filter);
    }

    /**
     * check if SD Card is available
     */
    public static boolean sdIsMounted() {

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * check if SD Card is available and we can write data in
     */
    public static boolean sdIsWriteable() {

        return sdIsMounted() && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    /**
     * check if SD Card is mounted and file exist
     *
     * @param path file path on sd card
     * @return true if exist
     */
    public static boolean sdFileExist(String path) {

        if (sdIsMounted()) {
            return new File(Environment.getExternalStorageDirectory(), path).exists();
        } else {
            Base.logE("Monkey's can't find SD Card...");
            return false;
        }
    }

    /**
     * read text file(if exist) from SD Card
     *
     * @param path file path on sd card
     * @return file content
     */
    public static String sdReadTextFile(String path) {

        return new String(sdReadFile(path));
    }

    /**
     * read text file(if exist) from SD Card
     *
     * @param path file path on sd card
     * @return file content in byte array
     */
    public static byte[] sdReadFile(String path) {

        byte[] bytes = null;

        if (sdFileExist(path)) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
                FileInputStream is = new FileInputStream(file);
                BufferedInputStream br = new BufferedInputStream(is);

                bytes = new byte[(int) file.length()];
                br.read(bytes, 0, bytes.length);

                br.close();
                is.close();
            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be read: " + e.getMessage());
            }
        }

        return bytes;
    }

    /**
     * creates directory hierarchy
     *
     * @return true if hierarchy succesfully created
     */
    public static boolean sdMakeDirs(String path) {

        File file = new File(Environment.getExternalStorageDirectory() + "/" + path);

        if (file.exists()) {
            return true;
        }

        if (!file.mkdirs()) {
            Base.logE("Monkey's can't create folder: " + file.getAbsolutePath());
        } else {
            return true;
        }

        return false;
    }

    /**
     * write text file into SD Card(if is writeable)
     *
     * @param path    file path on sd card
     * @param content data to write
     */
    public static void sdWriteFile(String path, String content) {

        sdWriteFile(path, content.getBytes());
    }

    /**
     * write text file into SD Card(if is writeable)
     *
     * @param path  file path on sd card
     * @param bytes data to write
     */
    public static void sdWriteFile(String path, byte[] bytes) {

        if (sdIsWriteable()) {
            try {
                int slash = path.lastIndexOf("/") + 1;

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path.substring(0, slash));

                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Base.logE("Monkey's can't create folder: " + file.getAbsolutePath());
                    }
                }

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(file, path.substring(slash, path.length()))));
                bos.write(bytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be write: " + e.getMessage());
            }
        } else {
            Base.logE("Monkey's can't write to SD Card...");
        }
    }

    /**
     * write text file into SD Card(if is writeable)
     *
     * @param path  file path on sd card
     * @param bytes data to write
     */
    public static void sdWritePrivateFile(Context context, String dir, String path, byte[] bytes) {

        if (sdIsWriteable()) {
            try {
                int slash = path.lastIndexOf("/") + 1;

                File file = new File(context.getExternalFilesDir(dir), path);

                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Base.logE("Monkey's can't create folder: " + file.getAbsolutePath());
                    }
                }

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(file, path.substring(slash, path.length()))));
                bos.write(bytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be write: " + e.getMessage());
            }
        } else {
            Base.logE("Monkey's can't write to SD Card...");
        }
    }

    /**
     * read text file(if exist) from SD Card
     *
     * @param path file path on sd card
     * @return file content in byte array
     */
    public static byte[] sdReadPrivateFile(Context context, String dir, String path) {

        byte[] bytes = null;

        try {
            File file = new File(context.getExternalFilesDir(dir), path);

            if (sdFileExist(file.getPath())) {

                FileInputStream is = new FileInputStream(file);
                BufferedInputStream br = new BufferedInputStream(is);

                bytes = new byte[(int) file.length()];
                br.read(bytes, 0, bytes.length);

                br.close();
                is.close();
            }
        } catch (IOException e) {
            Base.logE("File: " + path + " cannot be read: " + e.getMessage());
        }

        return bytes;
    }

    /**
     * delete file on SD Card
     *
     * @param path file path on sd card
     * @return true if deleted properly
     */
    public static boolean sdDeleteFile(String path) {

        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path).delete();
    }

    public static File[] sdDirectoryFiles(String dir) {

        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dir).listFiles();
    }

    public static File[] sdDirectoryFiles(String dir, FilenameFilter filter) {

        if (filter != null) {
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dir).listFiles(filter);
        } else {
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dir).listFiles();
        }
    }

    public static File sdFile(String path) {

        int slash = path.lastIndexOf("/") + 1;

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path.substring(0, slash));

        return new File(file, path.substring(slash, path.length()));
    }

    /**
     * reads data from file
     *
     * @param file File
     * @return file content in byte array
     */
    public static byte[] readFile(File file) {

        byte[] bytes = null;
        try {
            FileInputStream is = new FileInputStream(file);
            BufferedInputStream br = new BufferedInputStream(is);

            bytes = new byte[(int) file.length()];
            br.read(bytes, 0, bytes.length);

            br.close();
            is.close();
        } catch (IOException e) {
            Base.logE("File: " + file.getName() + " cannot be read: " + e.getMessage());
        }

        return bytes;
    }

    public static byte[] downloadFile(String fileUrl) {

        byte[] result = null;

        try {
            URL url = new URL(fileUrl);

            BufferedInputStream is = new BufferedInputStream(url.openConnection().getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int b;
            byte[] bar = new byte[4096];
            while ((b = is.read(bar)) != -1) {
                bos.write(bar, 0, b);
            }

            result = bos.toByteArray();

            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean downloadFile(String fileUrl, String sdFile) {

        boolean result = false;

        if (sdIsWriteable()) {
            try {
                URL url = new URL(fileUrl);

                int slash = sdFile.lastIndexOf("/") + 1;

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + sdFile.substring(0, slash));

                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Base.logE("Monkey's can't create folder: " + file.getAbsolutePath());
                    }
                }

                BufferedInputStream is = new BufferedInputStream(url.openConnection().getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(file, sdFile.substring(slash, sdFile.length()))));

                int b;
                byte[] bar = new byte[4096];
                while ((b = is.read(bar)) != -1) {
                    bos.write(bar, 0, b);
                }

                bos.flush();
                bos.close();
                is.close();

                result = true;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!result && sdFileExist(sdFile)) {
                    sdDeleteFile(sdFile);
                }
            }
        } else {
            Base.logE("Monkey's can't write to SD Card...");
        }

        return result;
    }

    public static boolean downloadFile(String fileUrl, Uri uri) {

        boolean result = false;

        if (sdIsWriteable()) {
            try {
                URL url = new URL(fileUrl);

                File file = new File(uri.getPath());

                if (!file.exists()) {
                    File dir = new File(uri.getPath().substring(0, uri.getPath().lastIndexOf("/") + 1));
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            Base.logE("Monkey's can't create folder: " + dir.getAbsolutePath());
                        }
                    }
                }

                BufferedInputStream is = new BufferedInputStream(url.openConnection().getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                int b;
                byte[] bar = new byte[4096];
                while ((b = is.read(bar)) != -1) {
                    bos.write(bar, 0, b);
                }

                bos.flush();
                bos.close();
                is.close();

                result = true;

            } catch (IOException e) {
                Base.logE("File downloading fails | " + fileUrl +" | " + uri);
                e.printStackTrace();
            } finally {
                if (!result && fileExist(uri)) {
                    fileDelete(uri);
                }
            }
        } else {
            Base.logE("Monkey's can't write to SD Card...");
        }

        return result;
    }

    public static boolean fileExist(Uri uri) {

        return new File(uri.getPath()).exists();
    }

    public static boolean fileDelete(Uri uri) {

        return new File(uri.getPath()).delete();
    }

    public static Uri sdFileUri(String path) {

        return Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + path));
    }

    public static Uri getResourceUri(Context context, String folder, String file) {

        if (StringHelper.isEmpty(file)) {
            return null;
        }

        return Uri.parse(String.format(Locale.US, "android.resource://%s/%s/%s", context.getPackageName(), folder, file));
    }

    public static int getResourceId(Context context, String folder, String file) {

        if (StringHelper.isEmpty(file)) {
            return 0;
        }

        return context.getResources().getIdentifier(String.format(Locale.US, "%s.png", file), folder, context.getPackageName());
    }

    public static String lowerExtension(String name) {

        int start = name.lastIndexOf(".") + 1;
        int end = name.length();

        if (start >= end) {
            return name;
        }

        String fileName = name.substring(0, start);
        String extension = name.substring(start, end).toLowerCase();

        return fileName + extension;
    }
}
