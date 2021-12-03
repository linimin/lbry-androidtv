package app.newproj.lbrytv.hiltmodule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.MainScope
import javax.inject.Qualifier

@Module
@InstallIn(FragmentComponent::class)
object MainCoroutineScopeModule {
    @Provides
    @MainCoroutineScope
    @FragmentScoped
    fun mainCoroutineScope() = MainScope()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainCoroutineScope
