package com.konsula.app.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konsula.app.AppController;
import com.konsula.app.R;
import com.konsula.app.service.model.AuthModel;
import com.konsula.app.service.model.ResendVerificationModel;
import com.konsula.app.service.model.VerificationUserModel;
import com.konsula.app.service.net.NetworkManager;
import com.konsula.app.service.task.CancelableCallback;
import com.konsula.app.ui.activity.direktori.EditProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Konsula on 21/03/2016.
 */
public class VerificationFragment extends Fragment {
    private EditText editText1, editText2, editText3, editText4;
    private TextView textView;
    private LinearLayout btnKirimulang, btnGantinomor;
    ProgressDialog dlg;
    OnVerificationListener listener;
    AuthModel.Results userData;
    CancelableCallback cancelableCallback;

    public interface OnVerificationListener {
        public void onSuccessVerification();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnVerificationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement" + OnVerificationListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_verification_code, container, false);
        initid(view);
        final StringBuilder sb = new StringBuilder();
        editText1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editText1.length() == 1) {
                    sb.append(s);
                    editText1.clearFocus();
                    editText2.requestFocus();
                    editText2.setCursorVisible(true);
                }
                if (editText1.length() == 1 && editText2.length() == 1 && editText3.length() == 1 && editText4.length() == 1) {
                    doVerification();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 0) {
                    editText1.requestFocus();
                }

            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editText2.length() == 1) {
                    sb.append(s);
                    editText2.clearFocus();
                    editText3.requestFocus();
                    editText3.setCursorVisible(true);
                } else if (editText2.length() == 0) {
                    editText1.requestFocus();
                    editText1.setCursorVisible(true);
                }
                if (editText1.length() == 1 && editText2.length() == 1 && editText3.length() == 1 && editText4.length() == 1) {
                    doVerification();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 2) {
                    editText3.requestFocus();
                }

            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editText3.length() == 1) {
                    sb.append(s);
                    editText3.clearFocus();
                    editText4.requestFocus();
                    editText4.setCursorVisible(true);
                } else if (editText3.length() == 0) {
                    editText2.requestFocus();
                    editText2.setCursorVisible(true);
                }
                if (editText1.length() == 1 && editText2.length() == 1 && editText3.length() == 1 && editText4.length() == 1) {
                    doVerification();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 3) {
                    editText4.requestFocus();
                }

            }
        });
        editText4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (sb.length() == 0 & editText4.length() == 1) {
                    sb.append(s);
                    editText3.clearFocus();
                    editText4.requestFocus();
                    editText4.setCursorVisible(true);
                } else if (editText4.length() == 0) {
                    editText3.requestFocus();
                    editText3.setCursorVisible(true);
                }
                if (editText1.length() == 1 && editText2.length() == 1 && editText3.length() == 1 && editText4.length() == 1) {
                    doVerification();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sb.length() == 1) {
                    sb.deleteCharAt(0);
                }
            }

            public void afterTextChanged(Editable s) {
                if (sb.length() == 4) {
                    Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnKirimulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject props = new JSONObject();
                try {
                    props.put("SOURCE", "resend code");
                    ((AppController) getActivity().getApplication()).setMixpanel("While Phone Verification", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                doResend();
            }
        });
        btnGantinomor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject props = new JSONObject();
                try {
                    props.put("SOURCE", "change number");
                    ((AppController) getActivity().getApplication()).setMixpanel("While Phone Verification", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, 10002);
            }
        });

        return view;
    }

    private void initid(View view) {
        textView = (TextView) view.findViewById(R.id.textverfication);
        editText1 = (EditText) view.findViewById(R.id.edit_text_verifivation_1);
        editText2 = (EditText) view.findViewById(R.id.edit_text_verifivation_2);
        editText3 = (EditText) view.findViewById(R.id.edit_text_verifivation_3);
        editText4 = (EditText) view.findViewById(R.id.edit_text_verifivation_4);
        btnKirimulang = (LinearLayout) view.findViewById(R.id.btn_kirim_ulang);
        btnGantinomor = (LinearLayout) view.findViewById(R.id.btn_ganti_nomer);
        editText1.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.showSoftInput(editText1, 0);
        userData = ((AppController) getActivity().getApplication()).getSessionManager().getUserAccount();
        textView.setText(getString(R.string.text_verifikasi).replace("{phone_number}", String.valueOf(userData.phone_number) + ""));
        Log.d("phone", userData.phone_number);
    }

    private void doVerification() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        dlg = ProgressDialog.show(getActivity(), "Please wait", "Verification Code", true);
        dlg.show();
        cancelableCallback = new CancelableCallback<VerificationUserModel>() {
            @Override
            protected void onSuccess(VerificationUserModel verificationUserModel, Response response) {
                dlg.dismiss();
                if (verificationUserModel.results.success) {
                    JSONObject props = new JSONObject();
                    try {
                        props.put("SOURCE", "input verification");
                        ((AppController) getActivity().getApplication()).setMixpanel("While Phone Verification", props);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ((AppController) getActivity().getApplication()).getSessionManager().updateStatusUser(verificationUserModel.results.data.authentication);
                    final AuthModel.Results userData = ((AppController) getActivity().getApplication()).getSessionManager().getUserAccount();
                    userData.authentication = verificationUserModel.results.data.authentication;
                    listener.onSuccessVerification();
                } else {
                    Toast.makeText(getActivity(), verificationUserModel.messages.response_text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFailure(RetrofitError error) {
                dlg.dismiss();
            }
        };
        String code = editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString();
        NetworkManager.getNetworkService(getActivity()).doVerificationUser(((AppController) getActivity().getApplication())
                .getSessionManager().getToken(), code, cancelableCallback);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void doResend() {
        dlg = ProgressDialog.show(getActivity(), "Please wait", "Resend Code", true);
        dlg.show();
        cancelableCallback = new CancelableCallback<ResendVerificationModel>() {
            @Override
            protected void onSuccess(ResendVerificationModel resendVerificationModel, Response response) {
                dlg.dismiss();
                ((AppController) ((Activity) getContext()).getApplication()).doDialog(getActivity(), resendVerificationModel.messages.response_text);

            }

            @Override
            protected void onFailure(RetrofitError error) {
                dlg.dismiss();
            }
        };
        NetworkManager.getNetworkService(getActivity()).doResedVerificationUser(((AppController) getActivity().getApplication())
                .getSessionManager().getToken(), cancelableCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10002) && (resultCode == Activity.RESULT_OK)) {
            userData = ((AppController) getActivity().getApplication()).getSessionManager().getUserAccount();
            textView.setText(getString(R.string.text_verifikasi).replace("{phone_number}", userData.phone_number + ""));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            cancelableCallback.cancel();
        } catch (Exception e) {

        }
    }


}
