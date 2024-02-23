package com.example.attendease;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ViewMsgDialog extends DialogFragment {
    interface AddMsgDialogListener {

        void deleteMsg(Msg message,int position);
        void addMsg(Msg message);

    }


    private AddMsgDialogListener listener;

    private Msg message;
    private int position;
    public ViewMsgDialog() {
        this.message = null;
    }
    public ViewMsgDialog(Msg message, int position) {
        this.message = message;
        this.position=position;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddMsgDialogListener) {
            listener = (AddMsgDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view =
                LayoutInflater.from(getContext()).inflate(R.layout.viewmsg, null);
        EditText viewTitle=view.findViewById(R.id.Title);
        EditText viewMessage=view.findViewById(R.id.textview_third);
        String NegativeButton;
        String Details;
        if(message!=null){
            viewTitle.setText(message.getTitle());
            viewMessage.setText(message.getMessage());
            viewTitle.setFocusable(false);
            viewMessage.setFocusable(false);
            NegativeButton="Delete";
            Details="Details";
        }
        else{
            NegativeButton="Add";
            Details="Add";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle(Details)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(NegativeButton, (dialog, which) -> {
                    String Titlename = viewTitle.getText().toString();
                    String messagename = viewMessage.getText().toString();
                    if(message!=null){
                        listener.deleteMsg(message,position);
                    }
                    else{
                        listener.addMsg(new Msg(Titlename,messagename));
                    }

                })
                .create();

    }
}
