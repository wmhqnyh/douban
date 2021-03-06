package com.liuhao.douban.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.liuhao.douban.R;
import com.liuhao.douban.util.ImageUtil;
import com.liuhao.douban.util.NetWorkUtil;


/**
 * Created by liuhao on 2015/5/11.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final int NEED_CAPTCHA = 10;
    private static final int NOT_NEED_CAPTCHA = 11;
    private static final int GET_CAPTCHA_ERROR = 12;
    private static final String TAG = "LoginActivity";
    private EditText mEmailEditText;
    private EditText mPwdEditText;
    private LinearLayout mCaptchaLinearLayout;
    private EditText mCaptchaEditTextValue;
    private ImageView mCaptchaImageView;
    private Button mLoginButton;
    private Button mBackButton;
    private ProgressDialog pd;
    private Bitmap bitmap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //handler已经收到消息，代表已经查询到结果，将ProgressDialog取消掉
            pd.dismiss();
            super.handleMessage(msg);
            switch (msg.what) {
                case NEED_CAPTCHA:
                    mCaptchaLinearLayout.setVisibility(View.VISIBLE);
                    mCaptchaImageView.setImageBitmap(bitmap);
                    break;
                case NOT_NEED_CAPTCHA:
                    break;
                case GET_CAPTCHA_ERROR:
                    Toast.makeText(getApplicationContext(), "加载失败，请检查网络设置", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initView();
        setListener();

    }

    /**
     * 1.寻找控件，2.为控件赋值
     */
    private void initView() {
        mEmailEditText = (EditText) this.findViewById(R.id.editText_email);
        mPwdEditText = (EditText) this.findViewById(R.id.editText_password);
        mCaptchaEditTextValue = (EditText) this.findViewById(R.id.editText_captchaValue);
        mCaptchaImageView = (ImageView) this.findViewById(R.id.imageView_captcha);
        mCaptchaLinearLayout = (LinearLayout) this.findViewById(R.id.ll_captcha);
        mLoginButton = (Button) this.findViewById(R.id.btn_login);
        mBackButton = (Button) this.findViewById(R.id.btn_back);
        getCaptcha();


    }

    private void getCaptcha() {
        //查询验证码操作属于联网查询，耗时较长，因此采用一个ProgressDialog进行缓冲
        pd = new ProgressDialog(this);
        pd.setMessage("玩命加载中...");
        pd.setCancelable(false);
        pd.show();

        //判断是否需要验证码，然后设置mCaptchaLinearLayout的可见属性
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    String result = NetWorkUtil.isNeedCaptcha(getApplicationContext());
                    Log.i(TAG, result);
                    msg = new Message();
                    if (result == null) {
                        msg.what = NOT_NEED_CAPTCHA;
                        handler.sendMessage(msg);
                    } else {
                        msg.what = NEED_CAPTCHA;
//                        String imgUrl = getResources().getString(R.string.imgUrl) + result + "&amp;size=s";
//                        bitmap = ImageUtil.getImageByURL(imgUrl);
                        bitmap = ImageUtil.getImageByURL(result);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = GET_CAPTCHA_ERROR;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 3.为控件添加点击事件
     */
    private void setListener() {
        mLoginButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        mCaptchaImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.imageView_captcha:
                getCaptcha();
                break;
        }
    }
}
