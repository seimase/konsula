package com.konsula.app.ui.fragment.direktori;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsula.app.AppConstant;
import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.CommonModel;
import com.konsula.app.service.model.ViewAccountModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.ui.custom.CircleTransform;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Owner on 12/3/2015.
 */
public class NavMenuFragment extends Fragment implements ProfileViewFragment.OnPhotoChangeListener {
    private static String TAG = NavMenuFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] navTitleMenu = null;
    private FragmentDrawerListener drawerListener;
    private TextView name_username;
    private TextView version;
    private TextView Server;
    private ImageView image_logo;
    private int test;
    private AuthModel.Results userData;


    private ArrayList<CommonModel> menuList = new ArrayList<CommonModel>();

    public NavMenuFragment() {
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set data
        navTitleMenu = getActivity().getResources().getStringArray(R.array.nav_name_menu);
        TypedArray navIcon = getActivity().getResources().obtainTypedArray(R.array.nav_icon_menu);
        TypedArray navActiveIcon = getActivity().getResources().obtainTypedArray(R.array.nav_icon_active_menu);

        menuList.add(createStaticObjectData(navIcon.getDrawable(0), navTitleMenu[0], navActiveIcon.getDrawable(0)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(1), navTitleMenu[1], navActiveIcon.getDrawable(1)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(2), navTitleMenu[2], navActiveIcon.getDrawable(2)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(3), navTitleMenu[3], navActiveIcon.getDrawable(3)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(4), navTitleMenu[4], navActiveIcon.getDrawable(4)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(5), navTitleMenu[5], navActiveIcon.getDrawable(5)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(6), navTitleMenu[6], navActiveIcon.getDrawable(6)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(7), navTitleMenu[7], navActiveIcon.getDrawable(7)));
        menuList.add(createStaticObjectData(navIcon.getDrawable(8), navTitleMenu[8], navActiveIcon.getDrawable(8)));
        navIcon.recycle();

    }

    public void setMenuTitle(int position, String title) {
        menuList.get(position).setProperty("title", title);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_nav_menu, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.nav_menu_recycler_view);
        name_username = (TextView) view.findViewById(R.id.nav_welcome_username);
        image_logo = (ImageView) view.findViewById(R.id.nav_welcome_logo_image_view);
        version = (TextView) view.findViewById(R.id.nav_menu_version);
        Server = (TextView) view.findViewById(R.id.nav_menu_server);
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version.setText("V " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            //Handle exception
        }
        userData = ((AppController) getActivity().getApplication()).getSessionManager().getUserAccount();
        adapter = new NavigationDrawerAdapter(getActivity(), menuList);
        adapter.setCurrentPosition(0);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mDrawerLayout.closeDrawer(containerView);
                test = position;
                if (AppConstant.FROM_BACKMENU) adapter.setCurrentPosition(0);

                AppConstant.FROM_PROFILE = false;
                AppConstant.FROM_MEMBERSHIP = false;
                AppConstant.FROM_BACKMENU = false;
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        name_username.setText(((AppController) getActivity().getApplication()).firstWord(userData.fullname));
        if (userData.photo != null) {
            //((AppController) getActivity().getApplication()).displayImage(getActivity(),userData.photo, image_logo);
            Picasso.with(getActivity())
                    .load(userData.photo)
                    .into(image_logo);
        }
        Server.setText(AppConstant.DOMAIN_URL.equals("https://staging-api.konsula.com") ? "Staging" : "");

        return view;
    }

    public void setImage(final String Image) {
        Picasso picasso = Picasso.with(getActivity());
        picasso.setIndicatorsEnabled(false);
        picasso.load(Image)
                .transform(new CircleTransform())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .fit().centerInside().placeholder(null)
                .into(image_logo, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getActivity())
                                .load(Image)
                                .transform(new CircleTransform())
                                .error(R.drawable.ic_homebanner)
                                .into(image_logo, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });

//        Picasso.with(getActivity()).load(Image).transform(new CircleTransform()).into(image_logo);
        //((AppController) getActivity().getApplication()).displayImage(getActivity(),Image, image_logo);
    }


    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //  getActivity().invalidateOptionsMenu();

                if (AppConstant.FROM_BACKMENU) {
                    AppConstant.FROM_INDEX = 0;
                    adapter.setCurrentPosition(0);
                    drawerListener.onDrawerItemSelected(drawerView, 0);
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (AppConstant.FROM_PROFILE) {
                    test = 1;
                    AppConstant.FROM_PROFILE = false;
                } else if (AppConstant.FROM_MEMBERSHIP) {
                    test = 2;
                    AppConstant.FROM_MEMBERSHIP = false;
                } else if (AppConstant.FROM_BACKMENU) {
                    test = 0;
                }
                adapter.setCurrentPosition(test);
                drawerListener.onDrawerItemSelected(drawerView, test);
                adapter.notifyDataSetChanged();
                //  getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    @Override
    public void onSuccessOnPhotoChangeListener(String photo) {
        if (photo != null) {
            ((AppController) getActivity().getApplication()).displayImage(getActivity(), photo, image_logo);
        }
    }


    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    // create object data
    private CommonModel createStaticObjectData(Drawable iconId, String title, Drawable activeIcon) {
        CommonModel menuModel = new CommonModel();
        menuModel.setProperty("icon", iconId);
        menuModel.setProperty("activeIcon", activeIcon);
        menuModel.setProperty("title", title);
        return menuModel;
    }

    // adapter
    public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
        private List<CommonModel> listMenu = Collections.emptyList();
        private LayoutInflater inflater;
        private Context context;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();
        private int pos = -1;

        public NavigationDrawerAdapter(Context context, List<CommonModel> listMenu) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.listMenu = listMenu;
        }

        public void delete(int position) {
            listMenu.remove(position);
            notifyItemRemoved(position);
        }

        private void setCurrentPosition(int position) {
         /*   if (AppConstant.FROM_PROFILE) {
                position = 1;
            }else if (AppConstant.FROM_MEMBERSHIP) {
                position = 2;
            }else if (AppConstant.FROM_BACKMENU) {
                position = 0;
            }*/
            selectedItems.put(position, true);
            pos = position;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_nav_menu, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CommonModel current = listMenu.get(position);
            holder.title.setText(current.getProperty("title"));

            Drawable icon = null;

    /*        if (AppConstant.FROM_BACKMENU){
                if (position == 0){
                    icon = (Drawable) current.getObject("activeIcon");
                    holder.container.setSelected(true);
                    holder.title.setSelected(true);
                    holder.icon.setImageDrawable(icon);
                }else{
                    icon = (Drawable) current.getObject("icon");
                    holder.container.setSelected(false);
                    holder.title.setSelected(false);
                    holder.icon.setImageDrawable(icon);
                }
            }else{
                boolean isSelected = selectedItems.get(position, false);
                if (isSelected) {
                    icon = (Drawable) current.getObject("activeIcon");
                } else {
                    icon = (Drawable) current.getObject("icon");
                }
                holder.container.setSelected(isSelected);
                holder.title.setSelected(isSelected);
                holder.icon.setImageDrawable(icon);
            }*/

            if (AppConstant.FROM_INDEX == position) {
                icon = (Drawable) current.getObject("activeIcon");
                holder.container.setSelected(true);
                holder.title.setSelected(true);
                holder.icon.setImageDrawable(icon);
            } else {
                icon = (Drawable) current.getObject("icon");
                holder.container.setSelected(false);
                holder.title.setSelected(false);
                holder.icon.setImageDrawable(icon);
            }
        }

        @Override
        public int getItemCount() {
            return listMenu.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            ImageView icon;
            RelativeLayout container;

            public ViewHolder(View itemView) {
                super(itemView);
                container = (RelativeLayout) itemView.findViewById(R.id.item_nav_menu_container);
                title = (TextView) itemView.findViewById(R.id.item_nav_menu_name_text_view);
                icon = (ImageView) itemView.findViewById(R.id.item_nav_menu_icon_image_view);

                container.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                //        Save the selected positions to the SparseBooleanArray

                if (pos > -1) {
                    selectedItems.delete(pos);
                }
                if (!selectedItems.get(getAdapterPosition(), false)) {
                    setCurrentPosition(getAdapterPosition());
                }

//                RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
//                if (animator instanceof SimpleItemAnimator) {
//                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//                        notifyDataSetChanged();
//                    }
//                });
            }
        }
    }

 /*   @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    adapter.setCurrentPosition(0);
                }
                return false;
            }
        });
    }
*/
}
