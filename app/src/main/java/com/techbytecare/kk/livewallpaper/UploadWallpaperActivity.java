package com.techbytecare.kk.livewallpaper;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.techbytecare.kk.livewallpaper.Common.Common;
import com.techbytecare.kk.livewallpaper.Model.AnalyseModel.ComputerVision;
import com.techbytecare.kk.livewallpaper.Model.AnalyseModel.URLUpload;
import com.techbytecare.kk.livewallpaper.Model.CategoryItem;
import com.techbytecare.kk.livewallpaper.Model.WallpaperItem;
import com.techbytecare.kk.livewallpaper.Remote.IComputerVision;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadWallpaperActivity extends AppCompatActivity {

    Button btn_browse,btn_upload,btn_submit;
    ImageView image_preview;
    MaterialSpinner spinner;

    String categoryIdSelected = "",directUrl = "",nameOfFile = "";
    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    Map<String,String> spinnerData = new HashMap<>();

    IComputerVision mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_wallpaper);

        mService = Common.getComputerVisionAPI();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        image_preview = findViewById(R.id.image_preview);

        btn_browse = findViewById(R.id.btn_browse);
        btn_upload = findViewById(R.id.btn_upload);
        btn_submit = findViewById(R.id.btn_submit);

        spinner = findViewById(R.id.spinner);

        loadCategorySpinner();

        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedIndex() == 0)    {
                    Toast.makeText(UploadWallpaperActivity.this, "Please choose category first..", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadPicture();
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectAdultContent(directUrl);
            }
        });
    }

    private void detectAdultContent(final String directUrl) {
        if (directUrl.isEmpty())    {
            Toast.makeText(this, "Picture Not Uploaded!!!", Toast.LENGTH_SHORT).show();
        }
        else    {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Analyzing Image...");
            progressDialog.show();

            mService.analyseImage(Common.getAPIAdultEndPoint(),new URLUpload(directUrl))
                    .enqueue(new Callback<ComputerVision>() {
                        @Override
                        public void onResponse(Call<ComputerVision> call, Response<ComputerVision> response) {

                            if (response.isSuccessful())    {

                                if (!response.body().getAdult().isAdultContent())   {
                                    progressDialog.dismiss();
                                    saveUriToCategory(categoryIdSelected,directUrl);
                                    Toast.makeText(UploadWallpaperActivity.this, "Uploaded..", Toast.LENGTH_SHORT).show();
                                }
                                else    {
                                    progressDialog.dismiss();
                                    detectFileFromStorage(nameOfFile);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ComputerVision> call, Throwable t) {
                            Toast.makeText(UploadWallpaperActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void detectFileFromStorage(String nameOfFile) {
        storageReference.child("images/"+nameOfFile)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadWallpaperActivity.this, "Your image contains adult content and will be deleted..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPicture() {
        if (filePath != null)   {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            nameOfFile = UUID.randomUUID().toString();
            final StorageReference ref = storageReference.child(new StringBuilder("images/").append(nameOfFile).toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            directUrl = taskSnapshot.getDownloadUrl().toString();
                            btn_submit.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadWallpaperActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded.."+(int)progress+"%");
                        }
                    });

        }
    }

    private void saveUriToCategory(String categoryIdSelected, String imageLink) {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .push()
                .setValue(new WallpaperItem(imageLink,categoryIdSelected))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadWallpaperActivity.this, "Successfully Uploaded...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)    {

            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                image_preview.setImageBitmap(bitmap);
                btn_upload.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCategorySpinner() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_CATEGORY_BACKGROUND)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())    {
                            CategoryItem item = postSnapshot.getValue(CategoryItem.class);
                            String key = postSnapshot.getKey();

                            spinnerData.put(key,item.getName());
                        }

                        Object[] valueArray = spinnerData.values().toArray();
                        List<Object> valueList = new ArrayList<>();
                        valueList.add(0,"Category");
                        valueList.addAll(Arrays.asList(valueArray));
                        spinner.setItems(valueList);

                        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                Object[] keyArray = spinnerData.keySet().toArray();
                                List<Object> keyList = new ArrayList<>();
                                keyList.add(0,"Category_Key");
                                keyList.addAll(Arrays.asList(keyArray));
                                categoryIdSelected = keyList.get(position).toString();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        detectFileFromStorage(nameOfFile);
        super.onBackPressed();
    }
}
