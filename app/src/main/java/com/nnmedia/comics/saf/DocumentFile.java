package com.nnmedia.comics.saf;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

 
public abstract class DocumentFile {

    private final  com.nnmedia.comics.saf.DocumentFile mParent;

    DocumentFile( com.nnmedia.comics.saf.DocumentFile parent) {
        mParent = parent;
    }

    public static  com.nnmedia.comics.saf.DocumentFile fromFile(File file) {
        return new RawDocumentFile(null, file);
    }

    public static  com.nnmedia.comics.saf.DocumentFile fromTreeUri(Context context, Uri treeUri) {
        if (Build.VERSION.SDK_INT >= 21) {
            Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri,
                    DocumentsContract.getTreeDocumentId(treeUri));
            return new TreeDocumentFile(null, context, documentUri);
        }
        return null;
    }

    public static  com.nnmedia.comics.saf.DocumentFile fromSubTreeUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= 21) {
            /*
             * https://stackoverflow.com/questions/27759915/bug-when-listing-files-with-android-storage-access-framework-on-lollipop
             * 如果使用 buildDocumentUriUsingTree 会获取到授权的那个 DocumentFile
             */
            return new TreeDocumentFile(null, context, uri);
        }
        return null;
    }

    public abstract  com.nnmedia.comics.saf.DocumentFile createFile(String displayName);

    public abstract  com.nnmedia.comics.saf.DocumentFile createDirectory(String displayName);

    public abstract Uri getUri();

    public abstract String getName();

    public abstract String getType();

    public  com.nnmedia.comics.saf.DocumentFile getParentFile() {
        return mParent;
    }

    public abstract boolean isDirectory();

    public abstract boolean isFile();

    public abstract long length();

    public abstract boolean canRead();

    public abstract boolean canWrite();

    public abstract boolean delete();

    public abstract boolean exists();

    public abstract InputStream openInputStream() throws FileNotFoundException;

    public List<DocumentFile> listFiles(DocumentFileFilter filter) {
        return listFiles(filter, null);
    }

    public  com.nnmedia.comics.saf.DocumentFile[] listFiles(Comparator<? super DocumentFile> comp) {
         com.nnmedia.comics.saf.DocumentFile[] files = listFiles();
        Arrays.sort(files, comp);
        return files;
    }

    public abstract List<DocumentFile> listFiles(DocumentFileFilter filter, Comparator<? super DocumentFile> comp);

    public abstract  com.nnmedia.comics.saf.DocumentFile[] listFiles();

    public abstract void refresh();

    public abstract  com.nnmedia.comics.saf.DocumentFile findFile(String displayName);

    public abstract boolean renameTo(String displayName);

    public interface DocumentFileFilter {
        boolean call( com.nnmedia.comics.saf.DocumentFile file);
    }

}
