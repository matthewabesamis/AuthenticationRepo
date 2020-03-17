package com.example.authentication;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class KunektProfile extends Fragment {

    //Variables Used for Uploading Files
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private int imageCounter = 0;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    CircularImageView ProfilePicture;

    Button saveProfile;
    EditText editAbout;
    EditText editJob;
    EditText editCompany;
    EditText editSchool;

    TextView textViewName;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

       textViewName = view.findViewById(R.id.view_name);
        saveProfile = view.findViewById(R.id.button_save_profile);
        editAbout = view.findViewById(R.id.editText_about);
        editJob= view.findViewById(R.id.editText_job);
        editCompany= view.findViewById(R.id.editText_company);
        editSchool = view.findViewById(R.id.editText_school);

        mAuth = FirebaseAuth.getInstance();
        //Initialize widgets
        mButtonChooseImage = view.findViewById(R.id.button_choose_image);
        mButtonUpload = view.findViewById(R.id.button_upload);
        mImageView = view.findViewById(R.id.profileImageView);
        mProgressBar = view.findViewById(R.id.progress_bar);
        ProfilePicture = view.findViewById(R.id.profileImageView);

        //Button Choose File
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //Button Upload File
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadTask != null && mUploadTask.isInProgress())
                {
                    Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        //Reference Folder "uploads"
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileSettings();
            }
        });
        return view;
    }

    /**
     * This method allows the user to select files
     */
    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * This method allows the chosen image to be displayed
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    /**
     *
     * @param uri file extension
     * @return image extension
     */
    private String getFileExtension (Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * This method will upload the file chosen. Further steps needed to complete and create (gallery? or fix user picture)
     */
    private void uploadFile(){
        if (mImageUri != null)
        {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    }, 500);
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_LONG).show();
                    Upload upload = new Upload("Picture " + imageCounter++, downloadUrl.toString());
                    String uploadId =mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int)progress);

                }
            });
        }
        else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileSettings(){
        Intent intent = new Intent(getActivity().getBaseContext(), Kunekt_Main_Home.class);

        String stringAbout = editAbout.getText().toString();
        String stringJob = editJob.getText().toString();
        String stringCompany = editCompany.getText().toString();
        String stringSchool = editSchool.getText().toString();

        intent.putExtra("about", stringAbout);
        intent.putExtra("job", stringJob);
        intent.putExtra("company", stringCompany);
        intent.putExtra("school", stringSchool);

        Kunekt_Main_Home kunekt_main_home = (Kunekt_Main_Home) getActivity();
        startActivity(intent);
        kunekt_main_home.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new KunektSavedProfile()).commit();

    }

    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
    }
}
