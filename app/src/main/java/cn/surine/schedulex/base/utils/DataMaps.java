package cn.surine.schedulex.base.utils;

import java.util.ArrayList;
import java.util.List;

import cn.surine.coursetableview.entity.BCourse;
import cn.surine.coursetableview.entity.BTimeTable;
import cn.surine.schedulex.base.Constants;
import cn.surine.schedulex.data.entity.Course;
import cn.surine.schedulex.data.entity.TimeTable;

/**
 * Intro：
 * 数据映射
 *
 * @author sunliwei
 * @date 2020-02-09 16:45
 */
public class DataMaps {

    /**
     * 数据映射
     *
     * @param bCourse BCourse实体
     */
    public static Course dataMappingByBCourse(BCourse bCourse) {
        Course course = new Course();
        course.id = bCourse.getId();
        course.scheduleId = bCourse.getScheduleId();
        course.teachingBuildingName = bCourse.getPosition();
        course.classDay = String.valueOf(bCourse.getDay());
        course.teacherName = bCourse.getTeacher();
        course.coureName = bCourse.getName();
        course.classSessions = String.valueOf(bCourse.getSectionStart());
        course.continuingSession = String.valueOf(bCourse.getSectionContinue());
        course.color = bCourse.getColor();
        course.xf = bCourse.getScore();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Constants.MAX_WEEK; i++) {
            stringBuilder.append(0);
        }

        for (int i = 0; i < bCourse.getWeek().size(); i++) {
            int pos = bCourse.getWeek().get(i);
            stringBuilder.replace(pos - 1, pos, String.valueOf(1));
        }

        course.classWeek = stringBuilder.toString();
        return course;
    }


    /**
     * 数据映射
     *
     * @param course Course实体
     */
    public static BCourse dataMappingByCourse(Course course) {
        BCourse bCourse = new BCourse();
        bCourse.setId(course.id);
        bCourse.setName(course.coureName);
        bCourse.setScheduleId(course.scheduleId);
        bCourse.setPosition(course.teachingBuildingName + course.classroomName);
        bCourse.setDay(Integer.parseInt(course.classDay));
        bCourse.setTeacher(course.teacherName);
        bCourse.setSectionStart(Integer.parseInt(course.classSessions));
        bCourse.setSectionContinue(Integer.parseInt(course.continuingSession));
        bCourse.setColor(course.color);
        bCourse.setScore(course.xf);
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < course.classWeek.length(); i++) {
            if (course.classWeek.charAt(i) == '1') {
                list.add(i + 1);
            }
        }
        bCourse.setWeek(list);
        return bCourse;
    }


    /**
     * 数据映射
     */
    public static BTimeTable dataMappingTimeTableToBTimeTable(TimeTable timeTable) {
        if (timeTable == null) return null;
        BTimeTable bTimeTable = new BTimeTable();
        List<BTimeTable.BTimeInfo> timeList = new ArrayList<>();
        String[] ruleData = timeTable.rule.split(",");
        long startTime = timeTable.startTime;
        for (int i = 0; i < ruleData.length; i += 2) {
            long endTime = startTime + Integer.parseInt(ruleData[i]);
            BTimeTable.BTimeInfo bTimeInfo = new BTimeTable.BTimeInfo();
            bTimeInfo.sessionNo = i / 2 + 1;
            bTimeInfo.startTime = Dates.getTransformTimeNumber(startTime);
            bTimeInfo.endTime = Dates.getTransformTimeNumber(endTime);
            startTime = endTime += Integer.parseInt(ruleData[i + 1]);
            timeList.add(bTimeInfo);
        }
        bTimeTable.timeInfoList = timeList;
        return bTimeTable;
    }
}
