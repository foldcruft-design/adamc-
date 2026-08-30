package com.example.model

enum class Priority(val titleRu: String, val level: Int) {
    LOW("Низкий", 1),
    MEDIUM("Средний", 2),
    HIGH("Высокий", 3),
    URGENT("Срочный", 4)
}
