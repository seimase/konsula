package com.konsula.app.ui.activity.teledokter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.ui.activity.direktori.DoctorProfileActivity;
import com.konsula.app.ui.custom.CustomtextView;
import com.konsula.app.ui.view.CustomAlertDialogBuilder;
import com.vistrav.ask.Ask;

import java.util.HashMap;
import java.util.List;

/**
 * Created by konsula on 3/28/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;
    private HashMap<String, List<String>> _doctorId, _practiceId, _profileLink, _identityUri, _currency, _rate;
    private AlertDialog dialogpopup;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData, HashMap<String, List<String>> doctorId, HashMap<String, List<String>> practiceId, HashMap<String, List<String>> profileLink, HashMap<String, List<String>> identityUri, HashMap<String, List<String>> currency, HashMap<String, List<String>> rate) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._doctorId = doctorId;
        this._practiceId = practiceId;
        this._profileLink = profileLink;
        this._identityUri = identityUri;
        this._currency = currency;
        this._rate = rate;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
        final String doctor_id = _doctorId.get((this._listDataHeader.get(groupPosition))).get(childPosition);
        final String practice_id = _practiceId.get((this._listDataHeader.get(groupPosition))).get(childPosition);
        final String profile_link = _profileLink.get((this._listDataHeader.get(groupPosition))).get(childPosition);
        final String identityUri = _identityUri.get((this._listDataHeader.get(groupPosition))).get(childPosition);
        final String currency = _currency.get((this._listDataHeader.get(groupPosition))).get(childPosition);
        final String rate = _rate.get((this._listDataHeader.get(groupPosition))).get(childPosition);

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.custom_list_item, null);

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        TextView tvRate = (TextView) convertView
                .findViewById(R.id.lblListItemRight);
        TextView tvProg = (TextView) convertView
                .findViewById(R.id.lblListItemRight2);
        txtListChild.setText(childText);
         if (rate.length() == 4) {
            tvRate.setText(rate.substring(0, 1));
            tvProg.setText(rate.substring(1, 4));
        } else if (rate.length() == 5) {
            tvRate.setText(rate.substring(0, 2));
            tvProg.setText(rate.substring(2, 5));
        } else if (rate.length() == 6) {
            tvRate.setText(rate.substring(0, 3));
            tvProg.setText(rate.substring(3, 6));
        } else if (rate.length() == 7) {
            tvRate.setText(rate.substring(0, 4));
            tvProg.setText(rate.substring(4, 7));
        } else if (rate.equals("N/A") || rate.equals("") || rate == null || rate.equals("null") || "0".equals(rate)) {
            tvRate.setText(_context.getResources().getString(R.string.free_con));
            tvProg.setText("");
        }else {
             tvRate.setText(rate);
         }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(_context, AppController.getInstance().getThemeColor(_context));
                ListView list = new ListView(_context);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, R.layout.view_simple_list_item);

                adapter.add(_context.getResources().getString(R.string.dialog_choose));
                adapter.add(_context.getResources().getString(R.string.dialog_see_p));
                adapter.add(_context.getResources().getString(R.string.dialog_decline2));
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("name", childText);
                            intent.putExtra("doctor_id", doctor_id);
                            intent.putExtra("practice_id", practice_id);
                            intent.putExtra("profile_link", profile_link);
                            intent.putExtra("identityUri", identityUri);
                            intent.putExtra("currency", currency);
                            intent.putExtra("rate", rate);
                            ((Activity) _context).setResult(((Activity) _context).RESULT_OK, intent);
                            ((Activity) _context).finish();
                            dialogpopup.dismiss();
                        } else if (position == 1) {
                            Intent i = new Intent(_context, DoctorProfileActivity.class);
                            i.putExtra("from_teledokter", true);
                            i.putExtra("doctorUri", identityUri);
                            _context.startActivity(i);
                            dialogpopup.dismiss();
                        } else if (position == 2) {
                            dialogpopup.dismiss();
                        }
                    }
                });

                builder.setView(list);
                builder.setTitle(R.string.title_action);
                dialogpopup = builder.create();
                dialogpopup.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        Dialog d = ((Dialog) dialog);

                        int dividerId = d.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                        View divider = d.findViewById(dividerId);
                        if (divider != null) {
                            divider.setBackgroundColor(AppController.getInstance().getThemeColor(_context));
                        }
                    }
                });
                dialogpopup.show();

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.custom_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
        lblListHeader.setText(headerTitle);

        if (isExpanded) {
            arrow.setImageDrawable(_context.getResources().getDrawable(R.drawable.arrow_up_grey));
        } else {
            arrow.setImageDrawable(_context.getResources().getDrawable(R.drawable.arrow_down_grey));
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



}
