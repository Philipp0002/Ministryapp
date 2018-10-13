package tk.phili.dienst.dienst;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Licenses extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        ((TextView)findViewById(R.id.licensing)).setText(getString(R.string.weusethat)+"\n\n" +
                "com.google.firebase:firebase-messaging:11.0.2 \n" +
                "de.hdodenhof:circleimageview:1.3.0 \n"+
                "com.koushikdutta.ion:ion:2.1.8 \n"+
                "me.grantland:autofittextview:0.2.+ \n"+
                "com.github.AndroidDeveloperLB:AutoFitTextView:4 \n"+
                "com.sothree.slidinguppanel:library:3.3.1 \n"+
                "com.prof.rssparser:rssparser:1.3.1 \n"+
                "com.github.ozodrukh:CircularReveal:2.0.1@aar \n"+
                "com.github.tylersuehr7:chips-input-layout:2.2 \n"+
                "com.squareup.picasso:picasso:2.5.2 \n"+
                "com.github.jakob-grabner:Circle-Progress-View:v1.3 \n"+
                "com.mikepenz:materialdrawer:6.0.7@aar");
    }

}
