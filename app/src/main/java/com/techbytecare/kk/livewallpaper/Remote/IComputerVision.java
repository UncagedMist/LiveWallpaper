package com.techbytecare.kk.livewallpaper.Remote;

import com.techbytecare.kk.livewallpaper.Model.AnalyseModel.ComputerVision;
import com.techbytecare.kk.livewallpaper.Model.AnalyseModel.URLUpload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface IComputerVision {
    @Headers({
            "Content-Type:application/json",
            "Ocp-Apim-Subscription-Key:6dffa5b5ce8b472687fd7147943eb205"
    })
    @POST
    Call<ComputerVision> analyseImage(@Url String apiEndPoint, @Body URLUpload url);
}
