package com.techbytecare.kk.livewallpaper.Common;

import com.techbytecare.kk.livewallpaper.Model.WallpaperItem;
import com.techbytecare.kk.livewallpaper.Remote.IComputerVision;
import com.techbytecare.kk.livewallpaper.Remote.RetrofitClient;

public class Common {

    public static final String STR_CATEGORY_BACKGROUND = "CategoryBackground";

    public static String CATEGORY_SELECTED;

    public static String STR_WALLPAPER = "Wallpapers";
    public static String CATEGORY_ID_SELECTED;

    public static final int PERMISSION_REQUEST_CODE = 1000;

    public static WallpaperItem select_background = new WallpaperItem();

    public static String select_background_key;
    public static int SIGN_IN_REQUEST_CODE = 1001;
    public static int PICK_IMAGE_REQUEST = 1002;

    String url = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze?visualFeatures=Adult&language=en";

    public static String BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/";

    public static IComputerVision getComputerVisionAPI()    {
        return RetrofitClient.getClient(BASE_URL).create(IComputerVision.class);
    }

    public static String getAPIAdultEndPoint()  {

        return new StringBuilder(BASE_URL).append("analyze?visualFeatures=Adult&language=en").toString();
    }
}
