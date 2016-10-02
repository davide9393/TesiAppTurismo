package it.uniba.di.ivu.progettotesi.appturismo;

import java.io.File;

import android.os.Environment;

public final class AlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        // TODO Auto-generated method stub
        return new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName
        );
    }
}
