package com.techbytecare.kk.livewallpaper.Database.DataSource;

import com.techbytecare.kk.livewallpaper.Database.Recents;

import java.util.List;

import io.reactivex.Flowable;

public class RecentsRepository implements IRecentsDataSource  {

    private IRecentsDataSource mLocalDataSource;
    private static RecentsRepository instance;

    public RecentsRepository(IRecentsDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static RecentsRepository getInstance(IRecentsDataSource mLocalDataSource) {
        if (instance == null)   {
            instance = new RecentsRepository(mLocalDataSource);
        }
        return instance;
    }

    @Override
    public Flowable<List<Recents>> getAllRecents() {
        return mLocalDataSource.getAllRecents();
    }

    @Override
    public void insertRecents(Recents... recents) {
        mLocalDataSource.insertRecents(recents);
    }

    @Override
    public void updateRecents(Recents... recents) {
        mLocalDataSource.updateRecents(recents);
    }

    @Override
    public void deleteRecents(Recents... recents) {
        mLocalDataSource.deleteRecents(recents);
    }

    @Override
    public void deleteAllRecents() {
        mLocalDataSource.deleteAllRecents();
    }
}
