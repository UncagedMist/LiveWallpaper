package com.techbytecare.kk.livewallpaper.Database.LocalDatabase;

import com.techbytecare.kk.livewallpaper.Database.DataSource.IRecentsDataSource;
import com.techbytecare.kk.livewallpaper.Database.Recents;

import java.util.List;

import io.reactivex.Flowable;

public class RecentsDataSource implements IRecentsDataSource {

    private RecentDAO recentDAO;
    private static RecentsDataSource instance;

    public RecentsDataSource(RecentDAO recentDAO) {
        this.recentDAO = recentDAO;
    }

    public static RecentsDataSource getInstance(RecentDAO recentDAO)   {
        if (instance == null)   {
            instance = new RecentsDataSource(recentDAO);
        }
        return instance;
    }

    @Override
    public Flowable<List<Recents>> getAllRecents() {
        return recentDAO.getAllRecents();
    }

    @Override
    public void insertRecents(Recents... recents) {
        recentDAO.insertRecents(recents);
    }

    @Override
    public void updateRecents(Recents... recents) {
        recentDAO.updateRecents(recents);
    }

    @Override
    public void deleteRecents(Recents... recents) {
        recentDAO.deleteRecents(recents);
    }

    @Override
    public void deleteAllRecents() {
        recentDAO.deleteAllRecents();
    }
}
