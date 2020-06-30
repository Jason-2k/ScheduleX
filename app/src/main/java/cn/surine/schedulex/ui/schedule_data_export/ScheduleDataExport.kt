package cn.surine.schedulex.ui.schedule_data_export

import android.Manifest
import android.app.ProgressDialog
import android.util.Log
import android.view.View
import cn.surine.schedulex.R
import cn.surine.schedulex.app_base.VmManager
import cn.surine.schedulex.base.controller.BaseFragment
import cn.surine.schedulex.base.utils.*
import cn.surine.schedulex.data.entity.Course
import cn.surine.schedulex.data.entity.Schedule
import cn.surine.schedulex.ui.course.CourseViewModel
import cn.surine.schedulex.ui.schedule.ScheduleViewModel
import cn.surine.schedulex.ui.schedule_list.ScheduleListFragment.Companion.SCHEDULE_ID
import cn.surine.schedulex.ui.timetable_list.TimeTableViewModel
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_date_export.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Intro：
 *
 * @author sunliwei
 * @date 2020/6/26 10:47
 */
class ScheduleDataExport : BaseFragment() {

    lateinit var scheduleViewModel: ScheduleViewModel
    lateinit var courseViewModel: CourseViewModel
    private lateinit var timeTableViewModel: TimeTableViewModel
    lateinit var schedule: Schedule
    val calendarIds = ArrayList<Long>()
    override fun layoutId(): Int = R.layout.fragment_date_export

    override fun onInit(parent: View?) {
        arguments ?: return
        VmManager(this).apply {
            scheduleViewModel = vmSchedule
            courseViewModel = vmCourse
            timeTableViewModel = vmTimetable
        }
        val scheduleId = requireArguments().getInt(SCHEDULE_ID)
        schedule = scheduleViewModel.getScheduleById(scheduleId.toLong())
        scheduleTitle.text = schedule.name

        exportJson.setOnClickListener {
            RxPermissions(activity()).apply {
                request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe {
                    if (it) {
                        saveToJson(scheduleId);
                    } else {
                        Toasts.toast(getString(R.string.permission_is_denied))
                    }
                }
            }
        }

        other.setOnLongClickListener {
            RxPermissions(activity()).apply {
                request(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR).subscribe {
                    if (it) {
                       CoroutineScope(Dispatchers.IO).launch {
                           deleteCourseTask()
                       }
                    } else {
                        Toasts.toast(getString(R.string.permission_is_denied))
                    }
                }
            }
            true
        }
        other.setOnClickListener {
            RxPermissions(activity()).apply {
                request(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR).subscribe {
                    if (it) {
                        val dialog = ProgressDialog(activity()).apply { setMessage("正在导出至日历，请勿退出~")}
                        dialog.show()
                        CoroutineScope(Dispatchers.IO).launch {
                            addCourseTask(scheduleId,dialog)
                        }
                    } else {
                        Toasts.toast(getString(R.string.permission_is_denied))
                    }
                }
            }
        }

        exportExcel.setOnClickListener { Toasts.toast("暂不支持！") }
    }

    /**
     * 删除操作
     * */
    private suspend fun deleteCourseTask() {
        withContext(Dispatchers.Default) {
            val list = Prefs.getString("calendar_ids")
            list?.split(" ")?.forEach { v ->
                Log.d("slw", "$v: ");
                try {
//                    Calendars.removeAllEvent(activity(), v.toLong())
                    Calendars.removeAllEvent(activity(), activity().packageName)
//                    Calendars.removeAllEvent(activity(), "这是")
//                    Calendars.removeAllEvent(activity(), "再来一个")
                } catch (e: Exception) {

                }
            }
            Prefs.save("calendar_ids", "")
        }
        withContext(Dispatchers.Main){
            Toasts.toast("删除成功！")
        }
    }

    /**
     * 添加日程
     * */
    private suspend fun addCourseTask(scheduleId: Int, dialog: ProgressDialog) {
        val block = GlobalScope.async {
            val list: List<Course> = courseViewModel.getCourseByScheduleId(scheduleId)
            val c: Calendar = Calendar.getInstance()
            c.timeInMillis = System.currentTimeMillis()
            val weekday = listOf(-1, 7, 1, 2, 3, 4, 5, 6)[c.get(Calendar.DAY_OF_WEEK)]
            for (course in list) {
                //事件名称和提示信息
                val eventTitle = course.coureName
                val eventMsg = course.teachingBuildingName + course.classroomName + "/" + course.teacherName
                //当前周
                val curSchedule = scheduleViewModel.curSchedule
                val currentWeek = curSchedule.curWeek()
                val termStartTime = Dates.getDate(curSchedule.termStartDate, Dates.yyyyMMdd).time
                for ((index, week) in course.classWeek.withIndex()) {
                    //历史的周不会再添加
                    if (index + 1 < currentWeek || week == '0')
                        continue
                    //周和日的偏差
                    val startWeekOffsetTime = (index + 1 - currentWeek) * Dates.ONE_WEEK
                    val startDayOffsetTime = (course.classDay.toInt() - weekday) * Dates.ONE_DAY
                    val eventStartTime = termStartTime + startWeekOffsetTime + startDayOffsetTime + getCourseTime(course.classSessions.toInt())
                    val eventEndTime = termStartTime + startWeekOffsetTime + startDayOffsetTime + getCourseTime(course.classSessions.toInt() + course.continuingSession.toInt())
                    //insert into calendar
                    val result = Calendars.addCalendarEvent(activity(), eventTitle, eventMsg, eventStartTime, eventEndTime)
                    if (result != -1L) {
                        calendarIds.add(result)
                    }
                }
            }
        }
        block.await()
//        Prefs.save("calendar_ids", StringBuilder().apply {
//            calendarIds.forEach { v ->
//                append(v).append(" ")
//            }
//            trim()
//        })
        withContext(Dispatchers.Main){
            dialog.dismiss()
            Toasts.toast("添加成功！")
        }
    }


    /**
     * 获取上课时间
     * */
    private fun getCourseTime(classSessions: Int): Long {
        val timeTable = timeTableViewModel.getTimTableById(scheduleViewModel.curSchedule.timeTableId)
        timeTable ?: return 0
        val bTimeTable = DataMaps.dataMappingTimeTableToBTimeTable(timeTable)
        if (classSessions >= bTimeTable.timeInfoList.size) return 0
        return Dates.getTransformTimeString(bTimeTable.timeInfoList[classSessions - 1].startTime) * 60 * 1000
    }


    private fun saveToJson(scheduleId: Int) {
        val fileName = "${schedule.name}_$scheduleId"
        if (Files.saveAsJson(fileName, Jsons.entityToJson(courseViewModel.getCourseByScheduleId(scheduleId)))) {
            Snackbar.make(exportJson, "保存成功,路径 /Download/$fileName.json", Snackbar.LENGTH_SHORT).show();
        } else {
            Toasts.toast("保存失败！请稍后再试！");
        }
    }


}
