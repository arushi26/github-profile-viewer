package com.example.githubprofileviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.githubprofileviewer.data.ApiRequestInterface;
import com.example.githubprofileviewer.data.model.Profile;
import com.example.githubprofileviewer.utils.MiscellaneousUtils;
import com.example.githubprofileviewer.utils.NetworkUtils;

import java.io.File;
import java.io.FileOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements SearchDialogFragment.SearchDialogListener,
                   View.OnClickListener{
    private String TAG = MainActivity.class.getName();
    ScrollView mScrollView;
    TextView mName, mTvUsername, mLocation, mFollowerCount, mFollowingCount, mRepoCount;
    TextView mBio, mHireable, mTvError, mMore, mCompany, mEmail, mBlog;
    ImageView mAvatar, mShare, mShareBg, mLocationImg, mHireableImg, mPersonImg;
    ImageView mCompanyImg, mEmailImg, mBlogImg;
    ProgressBar mProgressBar;
    TextView mLblBio, mLblFollowers, mLblFollowing, mLblRepo;
    View mSepOne, mSepTwo, mSepThree;
    Button mFollow;
    private String mUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        loadData();
    }

    private void initializeUI(){
        // Fields to be hidden
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mLblBio = findViewById(R.id.lbl_bio);
        mLblFollowers = findViewById(R.id.lbl_followers);
        mLblFollowing = findViewById(R.id.lbl_following);
        mLblRepo = findViewById(R.id.lbl_repos);
        mShare = findViewById(R.id.img_share);
        mShareBg = findViewById(R.id.bg_share);
        mLocationImg = findViewById(R.id.img_location);
        mSepOne = findViewById(R.id.sep_one);
        mSepTwo = findViewById(R.id.sep_two);
        mSepThree = findViewById(R.id.sep_three);
        mPersonImg = findViewById(R.id.img_person);
        mMore = findViewById(R.id.lbl_more);
        mCompanyImg = findViewById(R.id.img_company);
        mEmailImg = findViewById(R.id.img_email);
        mBlogImg = findViewById(R.id.img_blog);
        mTvError = findViewById(R.id.tv_message);
        mHireable = findViewById(R.id.tv_hire);
        mHireableImg = findViewById(R.id.img_hire);
        mBio = findViewById(R.id.tv_bio);
        mCompany = findViewById(R.id.tv_company);
        mEmail = findViewById(R.id.tv_email);
        mBlog = findViewById(R.id.tv_blog);
        mFollow = findViewById(R.id.btn_follow);
        hideData();

        // Initialize rest
        mScrollView = findViewById(R.id.scroll_view);
        mName = findViewById(R.id.tv_name);
        mTvUsername = findViewById(R.id.tv_username);
        mLocation = findViewById(R.id.tv_location);
        mFollowerCount = findViewById(R.id.tv_followers);
        mFollowingCount = findViewById(R.id.tv_following);
        mRepoCount = findViewById(R.id.tv_repos);
        mAvatar = findViewById(R.id.img_avatar);

        mShareBg.setOnClickListener(this);
        mFollow.setOnClickListener(this);
    }

    private void loadData(){
        if (TextUtils.isEmpty(mUsername)) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mTvError.setVisibility(View.VISIBLE);
            mTvError.setText(getResources().getString(R.string.error_no_user));
            return;
        }

        final ApiRequestInterface request = NetworkUtils.getRetrofitInstance().create(ApiRequestInterface.class);
        Call<Profile> mCall = request.getProfile(mUsername);
        Log.d(TAG,"URL called - " + mCall.request().url());

        mCall.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                int responseCode = response.code();

                if(responseCode==200){
                    Profile profile = response.body();
                    showData(profile);
                }else{
                    mProgressBar.setVisibility(View.INVISIBLE);
                    String error = getResources().getString(R.string.error_no_such_user);
                    if(TextUtils.isEmpty(mName.getText())){
                        mTvError.setVisibility(View.VISIBLE);
                        mTvError.setText(error);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mTvError.setVisibility(View.VISIBLE);
                mTvError.setText(getResources().getString(R.string.error_internet_connection));
            }

        });
    }

    private void showData(Profile profile){
        // hide progress bar
        mProgressBar.setVisibility(View.INVISIBLE);
        // hide no user msg
        mTvError.setVisibility(View.INVISIBLE);

        // show data ---
        // Always available
        Glide.with(getApplicationContext())
                .load(profile.getAvatarUrl())
                .into(mAvatar);
        mName.setText(profile.getName());
        mTvUsername.setText(profile.getLogin());
        mPersonImg.setVisibility(View.VISIBLE);
        mLocation.setText(profile.getLocation());
        mLocationImg.setVisibility(View.VISIBLE);
        mFollowerCount.setText(String.valueOf(profile.getFollowers()));
        mFollowingCount.setText(String.valueOf(profile.getFollowing()));
        mRepoCount.setText(String.valueOf(profile.getPublicRepos()));

        mLblFollowers.setVisibility(View.VISIBLE);
        mLblFollowing.setVisibility(View.VISIBLE);
        mLblRepo.setVisibility(View.VISIBLE);
        mShare.setVisibility(View.VISIBLE);
        mShareBg.setVisibility(View.VISIBLE);
        mFollow.setVisibility(View.VISIBLE);
        mSepOne.setVisibility(View.VISIBLE);
        mSepTwo.setVisibility(View.VISIBLE);
        mSepThree.setVisibility(View.VISIBLE);

        // Check availability
        String bio = profile.getBio();
        if (!TextUtils.isEmpty(bio)){
            mBio.setText(bio);
            mBio.setVisibility(View.VISIBLE);
            mLblBio.setVisibility(View.VISIBLE);
        }

        Boolean hireable = profile.isHireable();
        if (hireable){
            mHireable.setVisibility(View.VISIBLE);
            mHireableImg.setVisibility(View.VISIBLE);
        }

        Boolean showMore = false;
        String company = profile.getCompany() == null?"":String.valueOf(profile.getCompany());
        String email = profile.getEmail() == null?"":String.valueOf(profile.getEmail());
        String blog = String.valueOf(profile.getBlog());

        if (!TextUtils.isEmpty(company)){
            mCompany.setText(company);
            mCompany.setVisibility(View.VISIBLE);
            mCompanyImg.setVisibility(View.VISIBLE);
            showMore = true;
        }

        if (!TextUtils.isEmpty(email)){
            mEmail.setText(email);
            mEmail.setVisibility(View.VISIBLE);
            mEmailImg.setVisibility(View.VISIBLE);
            // Set TextView text underline
            mEmail.setPaintFlags(mEmail.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            mEmail.setOnClickListener(this);
            showMore = true;
        }

        if (!TextUtils.isEmpty(blog)){
            mBlog.setText(blog);
            mBlog.setVisibility(View.VISIBLE);
            mBlogImg.setVisibility(View.VISIBLE);
            // Set TextView text underline
            mBlog.setPaintFlags(mBlog.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            mBlog.setOnClickListener(this);
            showMore = true;
        }

        if(showMore){
         mMore.setVisibility(View.VISIBLE);
        }

    }

    private void hideData(){
        // show progress bar
        mProgressBar.setVisibility(View.VISIBLE);
        // hide error message
        mTvError.setVisibility(View.INVISIBLE);
        // hide icons & labels
        mShare.setVisibility(View.INVISIBLE);
        mShareBg.setVisibility(View.INVISIBLE);
        mFollow.setVisibility(View.INVISIBLE);
        mLblFollowers.setVisibility(View.INVISIBLE);
        mLblFollowing.setVisibility(View.INVISIBLE);
        mLblRepo.setVisibility(View.INVISIBLE);
        mLblBio.setVisibility(View.GONE);
        mLocationImg.setVisibility(View.INVISIBLE);
        mSepOne.setVisibility(View.INVISIBLE);
        mSepTwo.setVisibility(View.INVISIBLE);
        mSepThree.setVisibility(View.INVISIBLE);
        mPersonImg.setVisibility(View.INVISIBLE);
        mHireable.setVisibility(View.INVISIBLE);
        mHireableImg.setVisibility(View.INVISIBLE);
        mBio.setVisibility(View.GONE);
        mMore.setVisibility(View.INVISIBLE);
        mCompanyImg.setVisibility(View.GONE);
        mCompany.setVisibility(View.GONE);
        mEmailImg.setVisibility(View.GONE);
        mEmail.setVisibility(View.GONE);
        mBlogImg.setVisibility(View.GONE);
        mBlog.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            showSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSearchDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SearchDialogFragment();
        dialog.show(getSupportFragmentManager(), "SearchDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String username) {
        mTvError.setVisibility(View.INVISIBLE);
        hideData();
        mUsername = username;
        loadData();
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.bg_share:
                Bitmap bitmap = MiscellaneousUtils.getBitmapFromView(mScrollView,
                            mScrollView.getChildAt(0).getHeight(),
                            mScrollView.getChildAt(0).getWidth());

                try {
                    File file = new File(this.getExternalCacheDir(),"profile.png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    intent.setType("image/png");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        //startActivity(intent);
                        startActivity(Intent.createChooser(intent, "Share image via"));
                    }
                    else{
                        Toast.makeText(this, "No app installed to share profile image", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Trying to share profile", e);
                }

                break;
            case R.id.tv_email:
                intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("application/octet-stream");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.tv_blog:
                Uri webpage = Uri.parse(mBlog.getText().toString());
                intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.btn_follow:
                Toast.makeText(this,
                        getResources().getString(R.string.msg_on_follow).concat(" "+mUsername),
                        Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }
}
