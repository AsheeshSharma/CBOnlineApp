package com.codingblocks.cbonlineapp.admin.doubts

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.safeApiCall
import com.github.jasminb.jsonapi.JSONAPIDocument
import retrofit2.Response

class DoubtRepository {

    suspend fun getLiveDoubts(): ResultWrapper<Response<JSONAPIDocument<List<Doubts>>>> {
        return safeApiCall { Clients.onlineV2JsonApi.getLiveDoubts() }
    }

    suspend fun getMyDoubts(acknowledgedId: String): ResultWrapper<Response<JSONAPIDocument<List<Doubts>>>> {
        return safeApiCall { Clients.onlineV2JsonApi.getMyDoubts(acknowledgedId = acknowledgedId) }
    }

    suspend fun resolveDoubt(doubtId: String, doubt: Doubts): ResultWrapper<Response<List<Doubts>>> {
        return safeApiCall { Clients.onlineV2JsonApi.resolveAdminDoubt(doubtId, doubt) }
    }

    suspend fun acknowledgeDoubt(doubtId: String, doubt: Doubts): ResultWrapper<Response<List<Doubts>>> {
        return safeApiCall { Clients.onlineV2JsonApi.acknowledgeDoubt(doubtId, doubt) }
    }


}
