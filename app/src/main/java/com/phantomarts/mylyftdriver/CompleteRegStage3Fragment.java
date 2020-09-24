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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class CompleteRegStage3Fragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_SELECT_IMAGE_GALLERY_NIC1 = 1;
    private static final int REQUEST_SELECT_IMAGE_GALLERY_NIC2 = 2;
    private static final int REQUEST_IMAGE_CAPTURE_NIC1 = 3;
    private static final int REQUEST_IMAGE_CAPTURE_NIC2 = 4;

    private Uri photoURINIC1;
    private Uri photoURINIC2;
    private String currentPhotoPath;

    private ImageView nic1;
    private ImageView nic2;
    private Button btn;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_compreg_stage3, container, false);

        //update stepview
        StepView stepView = ((CompleteRegActivity) getActivity()).stepView;
        stepView.setVisibility(View.VISIBLE);
        stepView.go(0, true);

        nic1 = inflate.findViewById(R.id.imageView4);
        nic2 = inflate.findViewById(R.id.imageView5);
        btn = inflate.findViewById(R.id.button6);
        progressDialog=new ProgressDialog(getContext());
        nic1.setOnClickListener(this);
        nic2.setOnClickListener(this);
        btn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        return inflate;
    }


    @Override
    public void onClick(View v) {

        if (v == nic1) {
            showImageChooseDialog(nic1);
            return;
        } else if (v == nic2) {
            showImageChooseDialog(nic2);
            return;
        } else if (v == btn) {
            if(photoURINIC1==null){
                Toast.makeText(getContext(), "Add NIC1 Image", Toast.LENGTH_SHORT).show();
                return;
            }else if (photoURINIC2==null){
                Toast.makeText(getContext(), "Add NIC2 Image", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadPhotos();
            return;
        }
    }

    private void uploadPhotos() {
        progressDialog.setMessage("Uploading Images....");
        progressDialog.show();

        //upload to cloud storage
        StorageReference storageReferenceNIC1=firebaseStorage.getReference("uimages/"+
                firebaseAuth.getCurrentUser().getUid()+"/nic1.jpg");
        StorageReference storageReferenceNIC2=firebaseStorage.getReference("uimages/"+
                firebaseAuth.getCurrentUser().getUid()+"/nic2.jpg");

        System.out.println("nic1uri :"+photoURINIC1);
        System.out.println("nic2uri :"+photoURINIC2);

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        UploadTask uploadTask1 = storageReferenceNIC1.putFile(photoURINIC1,metadata);
        uploadTask1.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                System.out.println("nic1 upload success");
            }
        });

        UploadTask uploadTask2 = storageReferenceNIC2.putFile(photoURINIC2,metadata);
        uploadTask1.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
                progressDialog.setMessage("upload pausednic2");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                System.out.println("upload fail");
                progressDialog.dismiss();
                Toast.makeText(getContext(), "upload Failed nic2", Toast.LENGTH_SHORT).show();
                return;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("nic2 upload success");

                //update firebase db
                firebaseDatabase.getReference("/users/drivers/"+firebaseAuth.getCurrentUser().getUid()+"/regCompleteStaus")
                        .setValue(Driver.ACCOUNT_COMPLETE_STAGE4);

                //change Fragment
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.parentLayoutCompReg, new CompleteRegStage4Fragment(),"compreg_stage4")
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .addToBackStack(null)
                        .commit();

                progressDialog.dismiss();
            }
        });




    }

    private void showImageChooseDialog(final View v) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle((v == nic1) ? "Add Front Side NIC" : "Add Back Side NIC");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getContext());
                System.out.println("result:" + result);
                if (items[item].equals("Take Photo")) {
                    if (result)
                        dispatchCameraPictureIntent(v);
                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        dispatchGalleryIntent(v);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void dispatchCameraPictureIntent(View v) {
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

                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.phantomarts.mylyftdriver.fileprovider",
                        photoFile);
                if(v==nic1){
                    photoURINIC1=photoURI;
                    System.out.println("settings photouri nic1");
                }else{
                    photoURINIC2=photoURI;
                    System.out.println("settings photouri nic2");
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, (v==nic1)?REQUEST_IMAGE_CAPTURE_NIC1:REQUEST_IMAGE_CAPTURE_NIC2);
            }
        }else{
            Toast.makeText(getContext(), "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchGalleryIntent(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(galleryIntent, (v == nic1) ? REQUEST_SELECT_IMAGE_GALLERY_NIC1 : REQUEST_SELECT_IMAGE_GALLERY_NIC2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE_NIC1:
                    processImageCaptureResult(nic1,data);
                    break;
                case REQUEST_IMAGE_CAPTURE_NIC2:
                    processImageCaptureResult(nic2,data);
                    break;
                case REQUEST_SELECT_IMAGE_GALLERY_NIC1:
                    processImageGalleryResult(nic1,data);
                    break;
                case REQUEST_SELECT_IMAGE_GALLERY_NIC2:
                    processImageGalleryResult(nic2,data);
                    break;
                default:
            }
        }
    }

    private void processImageGalleryResult(ImageView imageView,Intent data) {
        Bitmap rotatedBitmap;
        try {
            if(imageView==nic1){
                photoURINIC1=data.getData();
            }else if(imageView==nic2){
                photoURINIC2=data.getData();
            }
            rotatedBitmap= Util.handleSamplingAndRotationBitmap(getContext(),data.getData());
            System.out.println(rotatedBitmap.getWidth()+" "+rotatedBitmap.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error reading file", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("Error reading file");
        }
        imageView.setImageBitmap(rotatedBitmap);
    }

    private void processImageCaptureResult(ImageView imageView, Intent data) {
        Bitmap rotatedBitmap;
        try {
            rotatedBitmap= Util.handleSamplingAndRotationBitmap(getContext(),(imageView==nic1)?photoURINIC1:photoURINIC2);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error reading file", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("Error reading file");

        }
        imageView.setImageBitmap(rotatedBitmap);
    }



     private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MyLyft_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   //prefix
                ".jpg",          //suffix
                storageDir       //directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        System.out.println("***"+currentPhotoPath);
        return image;
    }


}
