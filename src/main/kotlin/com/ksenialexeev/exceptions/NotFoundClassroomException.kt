package com.ksenialexeev.exceptions

class NotFoundClassroomException  (   val name: String,
val classroom: String
) : Exception(
"$name with id $classroom not found"
)