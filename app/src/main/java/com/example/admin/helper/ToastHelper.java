package com.example.admin.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.R;

public class ToastHelper {
    private Toast toast;
    private final Context context;

    public enum ToastType {
        SUCCESS(R.drawable.ic_checkbox_circle_line),
        ERROR(R.drawable.ic_error),
        WARNING(R.drawable.ic_warning),
        INFO(R.drawable.ic_notification_fill);

        private final int iconResId;

        ToastType(int iconResId) {
            this.iconResId = iconResId;
        }

        public int getIconResId() {
            return iconResId;
        }

    }

    public ToastHelper(Context context) {
        this.context = context;
    }

    public void showToast(String message) {
        showToast(message, ToastType.INFO);
    }

    public void showToast(String message, ToastType type) {
        cancelToast();

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);

        // Set icon based on type
        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(type.getIconResId());

        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    // Cancel the toast
    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
