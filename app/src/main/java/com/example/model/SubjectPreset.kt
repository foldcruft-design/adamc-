package com.example.model

import org.json.JSONArray
import org.json.JSONObject

data class SubjectPreset(
    val name: String,
    val colorHex: String,
    val iconName: String = "school",
    val isCustom: Boolean = false
) {
    companion object {
        val DEFAULT_SUBJECTS = listOf(
            SubjectPreset("Алгебра", "#6366F1", "calculate"),
            SubjectPreset("Геометрия", "#3B82F6", "shapes"),
            SubjectPreset("Физика", "#06B6D4", "bolt"),
            SubjectPreset("Химия", "#10B981", "science"),
            SubjectPreset("Русский язык", "#EC4899", "spellcheck"),
            SubjectPreset("Литература", "#F43F5E", "menu_book"),
            SubjectPreset("История", "#F59E0B", "history_edu"),
            SubjectPreset("Обществознание", "#8B5CF6", "groups"),
            SubjectPreset("Информатика", "#14B8A6", "code"),
            SubjectPreset("Английский язык", "#38BDF8", "translate"),
            SubjectPreset("Биология", "#22C55E", "eco"),
            SubjectPreset("География", "#EAB308", "public"),
            SubjectPreset("Другое", "#A855F7", "school")
        )

        fun parseCustomSubjects(json: String): List<SubjectPreset> {
            if (json.isBlank()) return emptyList()
            return try {
                val array = JSONArray(json)
                val list = mutableListOf<SubjectPreset>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        SubjectPreset(
                            name = obj.getString("name"),
                            colorHex = obj.getString("colorHex"),
                            iconName = obj.optString("iconName", "school"),
                            isCustom = true
                        )
                    )
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun customSubjectsToJson(list: List<SubjectPreset>): String {
            val array = JSONArray()
            list.filter { it.isCustom }.forEach { sub ->
                val obj = JSONObject()
                obj.put("name", sub.name)
                obj.put("colorHex", sub.colorHex)
                obj.put("iconName", sub.iconName)
                array.put(obj)
            }
            return array.toString()
        }
    }
}
