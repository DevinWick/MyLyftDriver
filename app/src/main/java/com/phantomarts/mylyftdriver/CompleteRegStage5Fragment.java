package com.phantomarts.mylyftdriver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.phantomarts.mylyftdriver.Util.Util;
import com.phantomarts.mylyftdriver.Util.Utility;
import com.phantomarts.mylyftdriver.model.Driver;
import com.shuhart.stepview.StepView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CompleteRegStage5Fragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_IMAGE_GALLERY = 2;
    private String currentPhotoPath;
    private String userChoosenTask;
    private Uri photoURI;

    private EditText etFirstName, etLastName;
    private ImageView imageView;
    private Button photoButton;
    private ProgressDialog progressDialog;
    public StepView stepView;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_compreg_stage5, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        progressDialog=new ProgressDialog(getContext());
        stepView = ((CompleteRegActivity) getActivity()).stepView;
        stepView.go(2, true);


        imageView = inflate.findViewById(R.id.imageView6);
        photoButton = inflate.findViewById(R.id.button8);
        etFirstName = inflate.findViewById(R.id.etfname);
        etLastName = inflate.findViewById(R.id.etlastname);

        imageView.setOnClickListener(this);
        photoButton.setOnClickListener(this);

        return inflate;
    }

    int tempcount = 0;

    @Override
    public void onClick(View v) {
        if (v == imageView) {
            selectImage();
            return;
        } else if (v == photoButton) {
            //validate
            if (photoURI == null) {
                Toast.makeText(getContext(), "Select Profile Image", Toast.LENGTH_SHORT).show();
                return;
            }

            //upload
            uploadData();
            return;
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getContext());
                System.out.println("result:" + result);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        dispatchCameraPictureIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        dispatchGalleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchGalleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(galleryIntent, REQUEST_SELECT_IMAGE_GALLERY);
        }
    }


    private void dispatchCameraPictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(getContext(), "Error in file creation", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.phantomarts.mylyftdriver.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(getContext(), "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap rotatedBitmap = null;
            try {
                System.out.println(data);
                rotatedBitmap = Util.handleSamplingAndRotationBitmap(getContext(), photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageBitmap(rotatedBitmap);
            System.out.println("printing uri");
            System.out.println(rotatedBitmap.getWidth() + " " + rotatedBitmap.getHeight());
            System.out.println("*******");
            System.out.println(photoURI);
        } else if (requestCode == REQUEST_SELECT_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            photoURI=data.getData();
            Bitmap rotatedBitmap;
            try {
                rotatedBitmap = Util.handleSamplingAndRotationBitmap(getContext(), data.getData());
                System.out.println(rotatedBitmap.getWidth() + " " + rotatedBitmap.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error reading file");
            }
            imageView.setImageBitmap(rotatedBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MyLyft_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println("***" + currentPhotoPath);
        return image;
    }

    private void uploadData() {
        String fname = etFirstName.getText().toString();
        String lname = etLastName.getText().toString();
        if (TextUtils.isEmpty(fname)) {
            etFirstName.setError("Enter First Name");
            return;
        } else if (TextUtils.isEmpty(lname)) {
            etLastName.setError("Enter Last Name");
            return;
        }

        progressDialog.setMessage("Saving User Data ...");
        progressDialog.show();

        //uploading data to cloud storage
        StorageReference storageReference=firebaseStorage.getReference("uimages/"+
                firebaseAuth.getCurrentUser().getUid()+"/profilepic.jpg");
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        UploadTask uploadTask = storageReference.putFile(photoURI,metadata);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
                progressDialog.setMessage("upload paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                System.out.println("upload fail");
                progressDialog.dismiss();
                Toast.makeText(getContext(), "upload Failed", Toast.LENGTH_SHORT).show();
                return;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("stage5 upload success");
            }
        });

        //update staus
        Map<String,Object> childUpdates=new HashMap<>();
        childUpdates.put("users/drivers/"+firebaseAuth.getCurrentUser().getUid()+"/firstName",fname);
        childUpdates.put("users/drivers/"+firebaseAuth.getCurrentUser().getUid()+"/lastName",lname);
        childUpdates.put("users/drivers/" + firebaseAuth.getCurrentUser().getUid() + "/regCompleteStaus",Driver.ACCOUNT_COMPLETE_ALL);

        firebaseDatabase.getReference().updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        System.out.println("data upload failed stage 5");
                    }
                });
    }


}
