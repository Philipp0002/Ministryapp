package tk.phili.dienst.dienst.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.uiwrapper.FragmentCommunicationPass;
import tk.phili.dienst.dienst.uiwrapper.WrapperActivity;

public class NotesFragment extends Fragment {

    public SharedPreferences spp;
    public SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private EditText notesEditText;

    Toolbar toolbar;
    FragmentCommunicationPass fragmentCommunicationPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCommunicationPass = (FragmentCommunicationPass) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_notes, null);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        notesEditText = view.findViewById(R.id.notes);
        toolbar = view.findViewById(R.id.toolbar);

        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);

        toolbar.setTitle(R.string.title_section3);

        sp = getContext().getSharedPreferences("MainActivity3",  Context.MODE_PRIVATE );
        editor = sp.edit();
        spp = getContext().getSharedPreferences("Notizen", Context.MODE_PRIVATE); //LEGACY NOTES SUPPORT
        if(spp.contains("NOTES")){
            editor.putString("NOTES", sp.getString("NOTES","")+"\n"+spp.getString("NOTES", ""));
            editor.commit();
            SharedPreferences.Editor edit2 = spp.edit();
            edit2.remove("NOTES");
            edit2.apply();
        }

        if(sp.contains("NOTES")){
            notesEditText.setText(sp.getString("NOTES", "0"));
        }


        Linkify.addLinks(notesEditText, Linkify.WEB_URLS);
        CharSequence text = TextUtils.concat(notesEditText.getText(), "\u200B");
        notesEditText.setText(text);

        notesEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                editor.putString("NOTES", notesEditText.getText().toString().replace("\u200B", "") + "");
                editor.commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Linkify.addLinks(notesEditText, Linkify.WEB_URLS);
            }
        });
        notesEditText.requestFocus();
    }

    @Override
    public void onResume() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        fragmentCommunicationPass.onDataPass(this, WrapperActivity.FRAGMENTPASS_TOOLBAR, toolbar);
        super.onResume();
    }


}
