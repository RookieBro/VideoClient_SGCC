package com.telecomyt.videoclient.ui;

import android.view.View;
import android.widget.EditText;

import com.telecomyt.videoclient.R;
import com.telecomyt.videolibrary.Config;
import com.telecomyt.videolibrary.VideoClient;
import com.telecomyt.videolibrary.base.BaseActivity;
import com.telecomyt.videolibrary.bean.UserEntityInfo;
import com.telecomyt.videolibrary.callback.VideoLoginCallback;
import com.telecomyt.videolibrary.utils.UIUtils;

/**
 * @author lbx
 */
public class LoginActivity extends BaseActivity {

    public EditText et_ip;
    public EditText et_name;
    public EditText et_password;

    @Override
    public int getLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(View view) {
        et_ip = findView(R.id.et_ip);
        et_name = findView(R.id.et_name);
        et_password = findView(R.id.et_password);
        //110101199910101234  //110101199910101235
        //101234  //101235
        et_name.setText("18200000008"); //110101199910101235
        et_password.setText("0008");
    }

    @Override
    public void initData() {

    }

    /**
     * 登录逻辑
     */
    public void login(View view) {
        login();
    }

    private void login() {
        UIUtils.showProgressDialog(this);
        /**
         * 第二步  登陆并开启video服务
         *
         * @param ac  Activity
         * @param callback  结果回调
         *  方法重载： VideoClient.start(this, et_name.getText().toString().trim(),
         *                  et_password.getText().toString(). trim(), new VideoLoginCallback()
         */

        final String videoUsername = et_name.getText().toString();
        final String videoPassword = et_password.getText().toString();

        //设置推送所需的必要信息
        UserEntityInfo userEntityInfo = new UserEntityInfo("昵称", "用户编码（即身份证号）", "机构编码", "设备imei");
        VideoClient.start(this, videoUsername, videoPassword, "大乒乓", "250", userEntityInfo, new VideoLoginCallback() {
            @Override
            public void loginSuccess() {
                UIUtils.closeProgressDialog();
                //成功
                startActivity(MainActivity.newInstance(LoginActivity.this));
                finish();
            }

            @Override
            public void loginFailed(String err) {
                UIUtils.closeProgressDialog();
                //登录失败
                VideoClient.getInstance().login(Config.getVideoUrl(), videoUsername, videoPassword, this);
            }

            @Override
            public void permissionErr(String err) {
                UIUtils.closeProgressDialog();
                //获取android权限失败
            }

            @Override
            public void netErr(String err) {
                UIUtils.closeProgressDialog();
                //网络错误导致的失败
            }
        });
    }
}
