package cn.surine.schedulex.ui.schedule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.surine.schedulex.R;
import cn.surine.schedulex.base.controller.App;
import cn.surine.schedulex.base.controller.BaseAdapter;
import cn.surine.schedulex.base.controller.BaseFragment;
import cn.surine.schedulex.base.utils.Toasts;
import cn.surine.schedulex.data.entity.Course;
import cn.surine.schedulex.ui.view.custom.EmptyView;

/**
 * Intro：
 *
 * @author sunliwei
 * @date 2020-01-26 21:21
 */
public class ScheduleViewpagerAdapter extends RecyclerView.Adapter<ScheduleViewpagerAdapter.ViewPagerViewHolder> {

    public static final String COURSE_ID = "course_id";
    private List<List<Course>> courseList;
    private BaseFragment baseFragment;

    public ScheduleViewpagerAdapter(List<List<Course>> courseList, BaseFragment baseFragment) {
        this.courseList = courseList;
        this.baseFragment = baseFragment;
    }


    public interface OnScrollBindListener{
        void onScroll(int position);
    }


    private OnScrollBindListener onScrollBindListener;

    public void setOnScrollBindListener(OnScrollBindListener onScrollBindListener) {
        this.onScrollBindListener = onScrollBindListener;
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        //课程表
        BaseAdapter<Course> adapter = new BaseAdapter<>(courseList.get(position), R.layout.item_course_view, cn.surine.schedulex.BR.course);
        adapter.setBanRecycle(true);
        holder.recyclerView.setLayoutManager(new GridLayoutManager(baseFragment.activity(), 5));
        holder.recyclerView.setAdapter(adapter);
        holder.recyclerView.scrollTo(0,0);
        //设置滚动监听器
        setScrollerListener(holder.recyclerView);

        adapter.setOnItemClickListener(courseDataPosition -> {
            Course course = courseList.get(position).get(courseDataPosition);
            if(course == null){
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.activity());
            View view = LayoutInflater.from(baseFragment.activity()).inflate(R.layout.view_course_info,null);
            TextView courseName = view.findViewById(R.id.courseName);
            TextView coursePosition = view.findViewById(R.id.coursePosition);
            TextView courseClassDay = view.findViewById(R.id.courseClassDay);
            TextView courseSession = view.findViewById(R.id.courseSession);
            TextView courseTeacher = view.findViewById(R.id.courseTeacher);
            TextView courseScore = view.findViewById(R.id.courseScore);
            ImageView edit = view.findViewById(R.id.courseEdit);
            courseName.setText(course.coureName);
            coursePosition.setText(course.teachingBuildingName + course.classroomName);
            courseClassDay.setText("周"+course.classDay);
            courseSession.setText(course.classSessions + "-" + (Integer.parseInt(course.continuingSession) + Integer.parseInt(course.classSessions) - 1) + "节");
            courseTeacher.setText(course.teacherName == null ? App.context.getResources().getString(R.string.unknown_teacher):course.teacherName);
            courseScore.setText(course.xf+"分");
            edit.setOnClickListener(v -> Toasts.toast("编辑"));
            builder.setView(view);
            builder.show();
        });
    }

    private void setScrollerListener(RecyclerView recyclerView) {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(onScrollBindListener != null){
                    onScrollBindListener.onScroll(dy);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        EmptyView emptyView;

        ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.schedule_view);
        }
    }
}
