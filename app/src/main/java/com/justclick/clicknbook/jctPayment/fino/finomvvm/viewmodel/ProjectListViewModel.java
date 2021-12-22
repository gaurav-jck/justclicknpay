package com.justclick.clicknbook.jctPayment.fino.finomvvm.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.justclick.clicknbook.jctPayment.fino.finomvvm.model.FinoResponseModel;

import java.util.List;

public class ProjectListViewModel extends AndroidViewModel {
//    private final LiveData<List<FinoResponseModel>> projectListObservable;
    private LiveData<List<FinoResponseModel>> projectListObservable;

    public ProjectListViewModel(Application application) {
        super(application);

        // If any transformation is needed, this can be simply done by Transformations class ...
//        projectListObservable = ProjectRepository.getInstance().getProjectList("Google");
        projectListObservable = new MediatorLiveData<>();
    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public LiveData<List<FinoResponseModel>> getProjectListObservable() {
        return projectListObservable;
    }
}