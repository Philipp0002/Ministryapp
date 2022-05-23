package tk.phili.dienst.dienst.uiwrapper;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

import tk.phili.dienst.dienst.R;
import tk.phili.dienst.dienst.drawer.Drawer;
import tk.phili.dienst.dienst.report.ReportFragment;
import tk.phili.dienst.dienst.utils.AdaptiveUtils;

public class WrapperActivity extends AppCompatActivity implements FragmentCommunicationPass {

    public static final String FRAGMENTPASS_TOOLBAR = "TOOLBAR";

    //ADAPTIVE
    private DrawerLayout drawerLayout;
    private NavigationView modalNavDrawer;
    private NavigationRailView navRail;
    private NavigationView navDrawer;
    private Configuration configuration;
    private FragmentContainerView fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wrapper);

        fragmentContainerView = findViewById(R.id.fragment_container_view);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, ReportFragment.class, null)
                    .commit();
        }else{
            Drawer.initialized = false;
        }
    }

    @Override
    public void onDataPass(Fragment fragment, String tag, Object object) {
        if(tag == FRAGMENTPASS_TOOLBAR){
            Toolbar toolbar = (Toolbar) object;
            //setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.bringToFront();

            drawerLayout = findViewById(R.id.drawer_layout);
            modalNavDrawer = findViewById(R.id.modal_nav_drawer);
            navRail = findViewById(R.id.nav_rail);
            navDrawer = findViewById(R.id.nav_drawer);
            configuration = getResources().getConfiguration();

            int screenWidth = configuration.screenWidthDp;
            AdaptiveUtils.updateNavigationViewLayout(
                    screenWidth, drawerLayout, modalNavDrawer, navRail, navDrawer,
                    toolbar, this);


            Drawer.manageDrawers(this, fragment, drawerLayout, modalNavDrawer, navRail, navDrawer);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Drawer.initialized = false;
    }
}