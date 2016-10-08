package com.konsula.app.ui.fragment.direktori;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.konsula.app.AppController;
import com.konsula.app.R;

import javax.annotation.Nullable;

/**
 * Created by Konsula on 24/02/2016.
 */
public class ImagePreviewFragment extends DialogFragment {
    private ImageView avatarImageView;
    final public static String AVATAR_IMAGE = "avatarImage";
    String image;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_show_picture, container);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            image = bundle.getString(AVATAR_IMAGE);
//        }
//        setStyle(DialogFragment.STYLE_NO_FRAME,0);
//        View view = inflater.inflate(R.layout.activity_show_picture, container, false);
//        avatarImageView = (ImageView) view.findViewById(R.id.show_picture_avatar_image_view);
//        ((AppController) getActivity().getApplication()).displayImage(image, avatarImageView);
//
//
//        return view;
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            image = bundle.getString(AVATAR_IMAGE);
        }
        avatarImageView = (ImageView) view.findViewById(R.id.show_picture_avatar_image_view);
        ((AppController) getActivity().getApplication()).displayImage(getActivity(),image, avatarImageView);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        // This MUST be called first! Otherwise the view tweaking will not be present in the displayed Dialog (most likely overriden)
        super.onStart();

        forceWrapContent(avatarImageView);
    }

    protected void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                current.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }
}
