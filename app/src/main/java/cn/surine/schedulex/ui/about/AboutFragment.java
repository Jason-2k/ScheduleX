package cn.surine.schedulex.ui.about;



import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.tencent.bugly.beta.Beta;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.surine.schedulex.R;
import cn.surine.schedulex.base.controller.BaseBindingFragment;
import cn.surine.schedulex.base.utils.Navigations;
import cn.surine.schedulex.base.utils.Others;
import cn.surine.schedulex.base.utils.Toasts;
import cn.surine.schedulex.databinding.FragmentAboutBinding;


public class AboutFragment extends BaseBindingFragment<FragmentAboutBinding> {

    int rotate;

    @Override
    public int layoutId() {
        return R.layout.fragment_about;
    }


    @Override
    protected void onInit(FragmentAboutBinding t) {
        t.aboutItemQQ.setOnClickListener(v -> {
            Others.startQQ(activity(), "686976115");
            Toasts.toast(getString(R.string.qq_copy));
        });
        t.github.setOnClickListener(v -> {
            Toasts.toast("求个star，感谢啦！");
            Others.openUrl("https://github.com/surine/ScheduleX");
        });
        t.alipay.setOnClickListener(v -> {
            Toasts.toast("开心到飞起~~~");
            Others.donateAlipay(activity(), "fkx00798tue4qrncwkknh09");
        });
        t.aboutItemCoolApk.setOnClickListener(v -> Others.startCoolApk("667393"));
        t.versionSlogan.setText(getString(R.string.version_slogan, Others.getAppVersion()));
        loadAnimation(t);
        t.jetpack.setOnClickListener(v -> {
            Beta.checkUpgrade(true, false);
            loadAnimation(t);
        });
    }

    private void loadAnimation(FragmentAboutBinding t) {
        SpringForce springForce = new SpringForce(rotate = (rotate == 0 ? 360 : 0));
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        springForce.setStiffness(SpringForce.STIFFNESS_VERY_LOW);
        SpringAnimation animation = new SpringAnimation(t.jetpack, SpringAnimation.ROTATION_Y);
        animation.setSpring(springForce);
        animation.start();
    }

}
