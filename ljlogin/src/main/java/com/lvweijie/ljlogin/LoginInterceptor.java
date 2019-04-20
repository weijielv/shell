package com.lvweijie.ljlogin;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by weijie lv on 2019/3/19.in j1
 */
@Interceptor(priority = 8, name = "测试用拦截器")
public class LoginInterceptor implements IInterceptor {
    /**
     * The operation of this interceptor.
     *
     * @param postcard meta
     * @param callback cb
     */
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {

        //todo

        int extra = postcard.getExtra();//need login
        if (extra== 0) {
            String value1 = postcard.getExtras().getString("key2");
            String path = postcard.getPath();
            if (!value1.equals("logined")) {
                // go to login;
                ARouter.getInstance().build("/login/loginActivity")
                        .withBundle("originbunder",postcard.getExtras())
                        .withString("originpath",path)
                        .navigation();
                // 觉得有问题，中断路由流程
                callback.onInterrupt(new RuntimeException("还没有登陆哦。"));
                return;
            }
        }
        // 以上两种至少需要调用其中一种，否则不会继续路由
        callback.onContinue(postcard);  // 处理完成，交还控制权
    }

    /**
     * Do your init work in this method, it well be call when processor has been load.
     *
     * @param context ctx
     */
    @Override
    public void init(Context context) {

    }
}
