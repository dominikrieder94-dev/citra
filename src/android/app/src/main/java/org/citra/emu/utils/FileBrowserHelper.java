package org.citra.emu.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;

import androidx.annotation.Nullable;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import java.io.File;
import java.util.List;

import org.citra.emu.R;
import org.citra.emu.ui.GameFilePickerActivity;

public final class FileBrowserHelper {
    private static final String EXTERNAL_STORAGE_PROVIDER = "com.android.externalstorage.documents";
    public static final int REQUEST_OPEN_DIRECTORY = 1;
    public static final int REQUEST_OPEN_FILE = 2;
    public static final int REQUEST_OPEN_DOCUMENT_TREE = 3;
    public static final int REQUEST_OPEN_DOCUMENT_GAMES = 4;
    public static final int REQUEST_OPEN_DOCUMENT_CIA = 5;
    public static final int REQUEST_OPEN_DOCUMENT_TREE_ES_DE = 6;
    public static final int REQUEST_OPEN_DIRECTORY_ES_DE = 7;

    public static void openDirectoryPicker(Activity activity) {
        openDirectoryPicker(activity, REQUEST_OPEN_DIRECTORY,
                Environment.getExternalStorageDirectory().getPath());
    }

    public static void openDirectoryPicker(Activity activity, int requestCode,
                                           @Nullable String startPath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Environment.isExternalStorageLegacy() && !PermissionsHandler.hasWriteAccess(activity)) {
                openGameDocuments(activity, REQUEST_OPEN_DOCUMENT_GAMES);
                return;
            }
        }

        if (!PermissionsHandler.checkWritePermission(activity)) {
            return;
        }

        Intent i = new Intent(activity, GameFilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH,
                   startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath());
        activity.startActivityForResult(i, requestCode);
    }

    public static void openFilePicker(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Environment.isExternalStorageLegacy() && !PermissionsHandler.hasWriteAccess(activity)) {
                openCiaDocuments(activity, REQUEST_OPEN_DOCUMENT_CIA);
                return;
            }
        }

        if (!PermissionsHandler.checkWritePermission(activity)) {
            return;
        }

        Intent i = new Intent(activity, GameFilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH,
                   Environment.getExternalStorageDirectory().getPath());
        activity.startActivityForResult(i, REQUEST_OPEN_FILE);
    }

    @Nullable
    public static String getSelectedDirectory(Intent result) {
        // Use the provided utility method to parse the result
        List<Uri> files = Utils.getSelectedFilesFromResult(result);
        if (!files.isEmpty()) {
            File file = Utils.getFileForUri(files.get(0));
            return file.getAbsolutePath();
        }

        return null;
    }

    @Nullable
    public static String[] getSelectedFiles(Intent result) {
        // Use the provided utility method to parse the result
        List<Uri> files = Utils.getSelectedFilesFromResult(result);
        if (!files.isEmpty()) {
            String[] paths = new String[files.size()];
            for (int i = 0; i < files.size(); i++)
                paths[i] = Utils.getFileForUri(files.get(i)).getAbsolutePath();
            return paths;
        }

        return null;
    }

    public static void openDocumentTree(Activity activity, int requestCode) {
        openDocumentTree(activity, requestCode, "ROMs/n3ds");
    }

    public static void openEsDeCustomSystemsTree(Activity activity, int requestCode) {
        openDocumentTree(activity, requestCode, "ES-DE");
    }

    public static void openDocumentTree(Activity activity, int requestCode,
                                        @Nullable String initialRelativePath) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            StorageManager storageManager = activity.getSystemService(StorageManager.class);
            StorageVolume primaryVolume =
                    storageManager != null ? storageManager.getPrimaryStorageVolume() : null;
            intent = primaryVolume != null ? primaryVolume.createOpenDocumentTreeIntent()
                                           : new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        }
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        Uri initialUri = buildPrimaryDocumentUri(initialRelativePath);
        if (initialUri != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
        }
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
        activity.startActivityForResult(intent, requestCode, bundle);
    }

    public static void openGameDocuments(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        Uri initialUri = buildPrimaryDocumentUri("ROMs/n3ds");
        if (initialUri != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
        }
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
        activity.startActivityForResult(intent, requestCode, bundle);
    }

    public static void openCiaDocuments(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
        activity.startActivityForResult(intent, requestCode, bundle);
    }

    @Nullable
    private static Uri buildPrimaryDocumentUri(String relativePath) {
        try {
            return DocumentsContract.buildDocumentUri(EXTERNAL_STORAGE_PROVIDER,
                    "primary:" + relativePath);
        } catch (Exception e) {
            return null;
        }
    }
}
