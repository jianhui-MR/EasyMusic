package com.bobby.musiczone;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bobby.musiczone.Activity.BaseActivity;
import com.bobby.musiczone.Activity.SearchMusicActivity;
import com.bobby.musiczone.fragment.DiscoveryFragment;
import com.bobby.musiczone.fragment.RecommendMvFragment;
import com.bobby.musiczone.service.PlayerService;
import com.bobby.musiczone.util.ScanMusicUtil;
import com.bobby.musiczone.fragment.RankMusicFragment;
import com.bobby.musiczone.fragment.LocalMusicFragment;
import com.bobby.musiczone.util.TimerTask.TimerTakUtil;
import com.flyco.tablayout.SlidingTabLayout;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends BaseActivity {
    public static final String QUERYSONG_ACTION="musiczone.querySong";
    public static final String SearchMusicActivity_ACTION="musiczone.searchMusic";
    public static final int SetTimerTask=0x001;
    @BindView(R.id.viewPager)
    public ViewPager viewPager;
    @BindView(R.id.drawerLayout)
    public DrawerLayout drawerLayout;
    @BindView(R.id.nav_View)
    public NavigationView navigationView;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.slidingTabLayout)
    public SlidingTabLayout slidingTabLayout;

    private Unbinder unbinder;
    private ArrayList<Fragment> fragmentList;
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        unbinder=ButterKnife.bind(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void initView()
    {
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.actionbar_menu);

        titles= new String[]{this.getString(R.string.myMusic),
                this.getString(R.string.discovery),this.getString(R.string.rank),this.getString(R.string.recommendMv)};
        fragmentList = new ArrayList<>();
        fragmentList.add(new LocalMusicFragment());
        fragmentList.add(new DiscoveryFragment());
        fragmentList.add(new RankMusicFragment());
        fragmentList.add(new RecommendMvFragment());
        slidingTabLayout.setViewPager(viewPager,titles,this,fragmentList);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_search_localMusic:
                        ScanMusicUtil scanMusicUtil =new ScanMusicUtil();
                        scanMusicUtil.query(MainActivity.this);
                        Intent intent=new Intent(QUERYSONG_ACTION);
                        sendBroadcast(intent);
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"扫描本地歌曲完成",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_search_setTimerTask:
                        TimerTakUtil util=new TimerTakUtil(PlayerService.getService());
                        util.setTimerTask();
                }
                return false;
            }
        });
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        super.onServiceConnected(name, binder);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.main_search:
                Intent intent=new Intent(MainActivity.this,SearchMusicActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
