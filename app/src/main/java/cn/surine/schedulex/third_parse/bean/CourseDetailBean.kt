package cn.surine.schedulex.third_parse.bean

data class CourseDetailBean(
    var id: Int,
    var day: Int,
    var room: String?,
    var teacher: String?,
    var startNode: Int,
    var step: Int,
    var startWeek: Int,
    var endWeek: Int,
    var type: Int,
    var tableId: Int
)