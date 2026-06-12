package com.example.apptravelfood.data.supabase

import com.example.apptravelfood.core.constant.AppConstant
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    val client = createSupabaseClient(
        supabaseUrl = AppConstant.SUPABASE_URL,
        supabaseKey = AppConstant.SUPABASE_ANON_KEY
    ) {
        install(Storage)
    }
}