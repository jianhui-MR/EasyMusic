package com.rex.easymusic.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.rex.easymusic.Activity.Login.LoginActivity;
import com.rex.easymusic.Application.MusicApplication;
import com.rex.easymusic.R;
import com.rex.easymusic.fragment.DiscoveryFragment;
import com.rex.easymusic.fragment.MusicFragment;
import com.rex.easymusic.util.HttpUtil;
import com.rex.easymusic.util.ScanMusicUtil;
import com.rex.easymusic.fragment.RankMusicFragment;
import com.rex.easymusic.util.StringAndBitmapUtil;
import com.rex.easymusic.util.TimeUtil;
import com.rex.easymusic.util.TimerTask.TimerTakUtil;
import com.rex.easymusic.util.ToastUtils;
import com.rex.easymusic.util.ipAddressUtil;
import com.rex.easymusic.videomodule.fragment.RecommendMvFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

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

    public static final String QUERYSONG_ACTION="musiczone.querySong";
    public static final String SearchMusicActivity_ACTION="musiczone.searchMusic";
    public static final int SetTimerTask=0x001;
    public final String getHeadSculptureUrl= ipAddressUtil.serviceIp+"/User/getUserHeadSculpture?account=";

    private Unbinder unbinder;
    private ArrayList<Fragment> fragmentList;
    private String[] titles;
    private String TAG="MainActivity";
    private Intent intent;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: "+"进入MainActivity" );
        setContentView(R.layout.activity_main);
        context=this;
        unbinder=ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initNavigationView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * 初始化视图（4个Fragment）
     */
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
        fragmentList.add(new MusicFragment());
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
                        TimerTakUtil util=new TimerTakUtil(((MusicApplication)getApplication()).getPlayerService());
                        util.setTimerTask();
                        break;
                    case R.id.exchange_user:
                        intent=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    /**
     *初始化NavigationView
     */
    private void initNavigationView(){
        View headView=navigationView.getHeaderView(0);
        TextView userName=headView.findViewById(R.id.userName);
        ImageView headPhoto=headView.findViewById(R.id.head_photo);
        userName.setText(LoginActivity.userName);
        headPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(context,UserInfoActivity.class);
                startActivity(intent);
            }
        });
        Glide.with(this)
                .load(getHeadSculptureUrl+LoginActivity.userAccount)
                .dontAnimate()
                .error(R.mipmap.cat)
                .placeholder(R.mipmap.cat)
                .signature(new StringSignature(TimeUtil.getTime()))
                .into(headPhoto);
    }


    /**
     * 重写菜单栏
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单栏点击事件处理
     * @param item
     * @return
     */
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
    public void onBackPressed() {
        if(GSYVideoManager.isFullState(this)){
            GSYVideoManager.backFromWindowFull(this);
            return;
        }
        moveTaskToBack(false);
//        super.onBackPressed();
    }
}
