package tk.phili.dienst.dienst;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Licenses extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        ((TextView)findViewById(R.id.licensing)).setText(getString(R.string.weusethat)+"\n\n" +
                "> com.github.sundeepk:compact-calendar-view:2.0.2.3\n\n"+
                "> androidx.appcompat:appcompat:1.0.0\n\n"+
                "> com.google.android.material:material:1.1.0-alpha01\n\n"+
                "> de.hdodenhof:circleimageview:1.3.0\n\n"+
                "> com.koushikdutta.ion:ion:2.+\n\n"+
                "> me.grantland:autofittextview:0.2.+\n\n"+
                "> com.github.AndroidDeveloperLB:AutoFitTextView:4\n\n"+
                "> com.sothree.slidinguppanel:library:3.3.1\n\n"+
                "> com.squareup.picasso:picasso:2.5.2\n\n"+
                "> com.github.jakob-grabner:Circle-Progress-View:v1.3\n\n"+
                "> com.mikepenz:materialdrawer:6.1.1\n\n"+
                "> com.github.ozodrukh:CircularReveal:2.0.1\n\n"+
                "> com.github.tylersuehr7:chips-input-layout:2.3\n\n"+
                "> com.prof.rssparser:rssparser:1.3.1\n\n"+
                "> com.fasterxml.jackson.dataformat:jackson-dataformat-smile:2.9.6\n\n"+
                "> com.fasterxml.jackson.core:jackson-databind:2.8.5\n\n"+
                "> com.fasterxml.jackson.core:jackson-core:2.8.5\n\n"+
                "> com.fasterxml.jackson.core:jackson-annotations:2.8.5\n\n"+
                "> com.yarolegovich:mp:1.0.9\n\n");
    }

}
