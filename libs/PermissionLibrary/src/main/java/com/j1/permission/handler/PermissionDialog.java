package com.j1.permission.handler;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.j1.permission.R;


/**
 * Created by wenjing.liu on 19/7/2 in J1.
 * <p>
 * 权限提示框
 *
 * @author wenjing.liu
 */
public class PermissionDialog extends Dialog {

    protected PermissionDialog(Context context) {
        this(context, 0);
    }

    protected PermissionDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static class Builder {
        private Context context;
        private String title;
        private String message;

        private int positiveMsg;
        private int negativeMsg;

        private TextView tvTitle;
        private TextView tvMessage;
        private Button btnPositive;
        private Button btnNegative;
        private DialogInterface.OnClickListener positiveListener;
        private DialogInterface.OnClickListener negativeListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveButton(@StringRes int msg, DialogInterface.OnClickListener positiveListener) {
            this.positiveMsg = msg;
            this.positiveListener = positiveListener;
            return this;
        }

        public Builder setNegativeButton(@StringRes int msg, DialogInterface.OnClickListener negativeListener) {
            this.negativeMsg = msg;
            this.negativeListener = negativeListener;
            return this;
        }

        public PermissionDialog create() {
            PermissionDialog dialog = new PermissionDialog(context);
            Window window = dialog.getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setContentView(R.layout.view_permission_dialog);
            tvTitle = window.findViewById(R.id.tv_title);
            tvMessage = window.findViewById(R.id.tv_message);
            btnPositive = window.findViewById(R.id.btn_positive);
            btnNegative = window.findViewById(R.id.btn_negative);
            setTopLayout();
            setButtonListener(dialog);
            return dialog;
        }

        private void setTopLayout() {
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
            tvTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
            tvMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
            if (!TextUtils.isEmpty(message)) {
                tvMessage.setText(message);
            }
        }

        private void setButtonListener(final PermissionDialog dialog) {
            btnNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeListener == null) {
                        dialog.dismiss();
                        return;
                    }
                    negativeListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                }
            });
            btnPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveListener == null) {
                        return;
                    }
                    positiveListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
            });
            if (positiveMsg != 0) {
                btnPositive.setText(positiveMsg);
            }
            if (negativeMsg != 0) {
                btnNegative.setText(negativeMsg);
            }
        }

        public void show() {
            PermissionDialog dialog = create();
            dialog.show();
        }
    }
}
