package com.example.usermanagement.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.usermanagement.core.Constants
import com.example.usermanagement.core.retryIO
import com.example.usermanagement.data.local.database.UserDatabase
import com.example.usermanagement.data.local.entity.UserEntity
import com.example.usermanagement.data.local.entity.UserRemoteKeys
import com.example.usermanagement.data.mapper.toEntities
import com.example.usermanagement.data.remote.api.UserApiService
import com.example.usermanagement.data.remote.error.safeApiCall
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator for Paging 3.
 * 
 * Responsibilities:
 * - Loads data from network when local cache is exhausted
 * - Stores data in local database
 * - Manages pagination keys
 * - Handles refresh logic
 * - Provides seamless offline experience
 * 
 * This is the heart of cache-first pagination strategy.
 */
@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val apiService: UserApiService,
    private val database: UserDatabase
) : RemoteMediator<Int, UserEntity>() {

    private val userDao = database.userDao()
    private val remoteKeysDao = database.userRemoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        // Refresh if cache is older than 1 hour
        val cacheTimeout = System.currentTimeMillis() - (Constants.CACHE_EXPIRY_HOURS * 60 * 60 * 1000)
        val oldestUser = database.withTransaction {
            userDao.getStaleUsers(cacheTimeout).firstOrNull()
        }

        return if (oldestUser != null) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        return try {
            // Determine page to load
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: Constants.INITIAL_PAGE_KEY
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            // Fetch data from network with retry logic
            val response = retryIO(maxRetries = Constants.MAX_RETRY_ATTEMPTS) {
                safeApiCall {
                    apiService.getUsers(
                        page = page,
                        pageSize = state.config.pageSize
                    )
                }
            }

            val users = response.data
            val endOfPaginationReached = !response.hasNext

            // Update database in a transaction
            database.withTransaction {
                // Clear database on refresh
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys()
                    userDao.clearAll()
                }

                // Calculate pagination keys
                val prevKey = if (page == Constants.INITIAL_PAGE_KEY) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                // Create remote keys for each user
                val keys = users.map { userDto ->
                    UserRemoteKeys(
                        userId = userDto.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                // Insert data
                remoteKeysDao.insertAll(keys)
                userDao.insertUsers(users.toEntities())
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    /**
     * Get remote key for the first item in the list
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, UserEntity>): UserRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { user ->
            remoteKeysDao.getRemoteKeys(user.id)
        }
    }

    /**
     * Get remote key for the last item in the list
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, UserEntity>): UserRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { user ->
            remoteKeysDao.getRemoteKeys(user.id)
        }
    }

    /**
     * Get remote key closest to current position (for refresh)
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, UserEntity>): UserRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { userId ->
                remoteKeysDao.getRemoteKeys(userId)
            }
        }
    }
}
