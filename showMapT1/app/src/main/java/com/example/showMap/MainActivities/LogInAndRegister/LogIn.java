package com.example.showMap.MainActivities.LogInAndRegister;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.showMap.ActicityTools.MyActivityTools;
import com.example.showMap.ActivityManager.AppManager;
import com.example.showMap.ClientHttpService.LoginPostService;
import com.example.showMap.MainActivities.FirstActivityShowMap;
import com.example.showMap.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by asus on 2019/3/1.
 */

public class LogIn extends AppCompatActivity {

    private static float mWidth, mHeight;
    private static Button mBtnLogin;
    private static View progress;
    private static View mInputLayout;
    private Button  register_button;
    private static LinearLayout mName, mPsw;
    //获取编辑框实例
    private static EditText login_username;
    private static EditText login_userpsd;

    private void initView() {
        register_button = (Button) findViewById(R.id.register_Button_on_login);
        mBtnLogin = (Button) findViewById(R.id.Login_Button_on_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);
        login_username = (EditText) findViewById(R.id.login_username_edit);
        login_userpsd = (EditText) findViewById(R.id.login_password_edit);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.login);
        //如果是从HasLogIn跳转过来的,将HasLogIn从Activity堆栈中移除
        AppManager.getInstance().killActivity(HasLogIn.class);
        //隐藏标题栏
        this.getSupportActionBar().hide();

        //控件实例化函数
        initView();

        //检查用户是否登录
        if(MyActivityTools.ishas_login(LogIn.this)){
            Toast.makeText(LogIn.this,"您已登录~",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LogIn.this,FirstActivityShowMap.class));
        }
        else{
            Toast.makeText(LogIn.this,"请登录~",Toast.LENGTH_SHORT).show();
        }

        //设置登录按钮监听事件
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login_username.getText().toString();
                String userpsd = login_userpsd.getText().toString();
                //检查网络信息
                if(MyActivityTools.isConnectIntent(LogIn.this)){
                    if(username.equals(""))
                        Toast.makeText(LogIn.this,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                    else if (userpsd.equals(""))
                        Toast.makeText(LogIn.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
                    else {
                        //启动登录Thread
                        new MyActivityTools.LogInPostThread(username,userpsd).start();
                        Handler loginhandler = MyActivityTools.myhandler;
                    }
                }else{
                    Toast.makeText(LogIn.this,"未连接互联网，请检查网络状态~",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //设置注册按钮监听事件
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动注册按钮界面
                startActivity(new Intent(LogIn.this,Register.class));
            }
        });
    }

    /**
     * 处理LoginPostThread线程返回的信息
     * @param msg
     */
    public static void resolve_Login_Handler(Message msg) {
        //处理返回线程返回的信息
        final Context logincontext = AppManager.getInstance().getActivitycontext(LogIn.class);
        if (msg.obj.toString().equals("SUCCEEDED")) {
            //跳转
            SharedPreferences sharedPreferences = logincontext.getSharedPreferences("userdata_sp", MODE_PRIVATE);
            SharedPreferences.Editor userdata_sp_editor = sharedPreferences.edit();
            Toast.makeText(logincontext, "登录成功，模拟跳转中", Toast.LENGTH_SHORT).show();
            userdata_sp_editor.putString("username", login_username.getText().toString());
            userdata_sp_editor.putString("password", login_userpsd.getText().toString());
            if (userdata_sp_editor.commit()) {
                //设置当前用户账号和密码全局变量
                MyActivityTools.currentusername = login_username.getText().toString();
                MyActivityTools.currentpassword = login_userpsd.getText().toString();
                Log.i("tag", "插入sp文件成功");
            } else {
                Log.e("错误：", "userdata_sp_editor提交失败");
            }
            /**
             * 跳转
             */
            // 计算出控件的高与宽
            mWidth = mBtnLogin.getMeasuredWidth();
            mHeight = mBtnLogin.getMeasuredHeight();
            // 隐藏输入框
            mName.setVisibility(View.INVISIBLE);
            mPsw.setVisibility(View.INVISIBLE);
            inputAnimator(mInputLayout, mWidth, mHeight);
            //延迟跳转
            Handler mhandler = new Handler();
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    intent = new Intent(logincontext, FirstActivityShowMap.class);
                    logincontext.startActivity(intent);
                }
            }, 2000);//2秒后执行Runnable中的run方法

        } else {
            Toast.makeText(logincontext, "登录失败，账号与密码不匹配", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 输入框的动画效果
     *
     * @param view
     *            控件
     * @param w
     *            宽
     * @param h
     *            高
     */
    private static void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }

    /**
     * 出现进度动画
     *
     * @param view
     */
    private static void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(100);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().killActivity(this);
    }
}
