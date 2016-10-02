package it.uniba.di.ivu.progettotesi.appturismo;

import java.io.File;

abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}

