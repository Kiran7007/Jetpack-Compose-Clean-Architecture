package com.example.usermanagement.data.mapper

import com.example.usermanagement.data.local.entity.UserEntity
import com.example.usermanagement.data.remote.dto.CreateUserDto
import com.example.usermanagement.data.remote.dto.UpdateUserDto
import com.example.usermanagement.data.remote.dto.UserDto
import com.example.usermanagement.domain.model.CreateUserInput
import com.example.usermanagement.domain.model.UpdateUserInput
import com.example.usermanagement.domain.model.User

/**
 * Mapper functions to convert between different representations of User:
 * - UserDto (from API) ↔ User (domain model)
 * - UserEntity (from DB) ↔ User (domain model)
 * - CreateUserInput ↔ CreateUserDto
 * - UpdateUserInput ↔ UpdateUserDto
 */

// ========== DTO → Domain ==========

fun UserDto.toDomainModel(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        avatarUrl = avatarUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<UserDto>.toDomainModels(): List<User> {
    return map { it.toDomainModel() }
}

// ========== Domain → DTO ==========

fun CreateUserInput.toDto(): CreateUserDto {
    return CreateUserDto(
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        avatarUrl = avatarUrl
    )
}

fun UpdateUserInput.toDto(): UpdateUserDto {
    return UpdateUserDto(
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        avatarUrl = avatarUrl
    )
}

// ========== Entity → Domain ==========

fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        avatarUrl = avatarUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<UserEntity>.toDomainModels(): List<User> {
    return map { it.toDomainModel() }
}

// ========== Domain → Entity ==========

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        avatarUrl = avatarUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        cachedAt = System.currentTimeMillis()
    )
}

// ========== DTO → Entity (for caching) ==========

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        avatarUrl = avatarUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        cachedAt = System.currentTimeMillis()
    )
}

fun List<UserDto>.toEntities(): List<UserEntity> {
    return map { it.toEntity() }
}
