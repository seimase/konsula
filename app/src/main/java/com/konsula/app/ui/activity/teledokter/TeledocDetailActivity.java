package com.konsula.app.ui.activity.teledokter;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.UpcomingTeledocModel;
import com.konsula.app.ui.adapter.DetailTeledocImageAdapter;
import com.konsula.app.ui.custom.CircleTransform;
import com.konsula.app.ui.fragment.direktori.ImagePreviewFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by Konsula on 19/04/2016.
 */
public class TeledocDetailActivity extends AppCompatActivity {
    private ImageButton backbutton;
    private ImageView imageViewAvatar;
    private TextView textViewName, textViewspesiality, textViewdate, textViewstatus, textViewkeluhan;
    private RecyclerView listimagedetail;
    final public static String AVATAR_IMAGE = "avatarImage";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teledoc_detail);
        backbutton = (ImageButton) findViewById(R.id.teledoc_done_image_button);
        imageViewAvatar = (ImageView) findViewById(R.id.teledoc_avatar_image_view);
        textViewName = (TextView) findViewById(R.id.teledoc_doctor_name_text_view);
        textViewspesiality = (TextView) findViewById(R.id.teledoc_spesialisai_text_view);
        textViewdate = (TextView) findViewById(R.id.teledoc_time_text_view);
        textViewstatus = (TextView) findViewById(R.id.teledoc_status_name_text_view);
        textViewkeluhan = (TextView) findViewById(R.id.teledoc_keluhan_text_view);
        listimagedetail = (RecyclerView) findViewById(R.id.recycler_view_image_teledoc);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setdata();
    }

    private void setdata() {
        UpcomingTeledocModel.Datum data = ((AppController) getApplication()).getSessionManager().getupcomingteledocdata();
        if (data.doctor_photo != null) {
            Picasso.with(this).load(data.doctor_photo.primary.medium_image).transform(new CircleTransform()).into(imageViewAvatar);
        }
        textViewName.setText(data.doctor_name);
        textViewspesiality.setText(data.doctor_specialization);
        textViewdate.setText(((AppController) getApplication()).dateJoin(data.schedule,"en"));
        textViewstatus.setText(data.tele_status);
        textViewstatus.setTextColor(data.tele_status.equals("Confirmed") ? getResources().getColor(R.color.green_xxl) : getResources().getColor(R.color.black));
        textViewkeluhan.setText(data.reason);



        if (data.teledoc_photos != null) {
            listimagedetail.setHasFixedSize(true);
            listimagedetail.setClickable(true);
            listimagedetail.setLayoutManager(new GridLayoutManager(this, 2));
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.space_5);
            listimagedetail.addItemDecoration(itemDecoration);
            listimagedetail.setAdapter(new DetailTeledocImageAdapter(getApplicationContext(),data.teledoc_photos, R.layout.item_image_teledoc, new DetailTeledocImageAdapter.OnImageClicked() {
                @Override
                public void onImageClicked(String imgUrl) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AVATAR_IMAGE, imgUrl);
                    android.app.FragmentManager fm = getFragmentManager();
                    ImagePreviewFragment powerDialog = new ImagePreviewFragment();
                    powerDialog.setArguments(bundle);
                    powerDialog.show(fm, "fragment_power");
                }
            }));

        }
    }



    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}



