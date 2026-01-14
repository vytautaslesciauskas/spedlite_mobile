package lt.agmis.spedlite.settings

import android.content.Context

enum class AppTheme {
    Light,
    Dark,
    Auto
}

class SpedliteSettings(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("spedlite", Context.MODE_PRIVATE)

    fun getAppTheme(): AppTheme {
        val themeInt = sharedPreferences.getInt("appTheme", 0)
        if (themeInt == 1) {
            return AppTheme.Light
        }

        if (themeInt == 2) {
            return AppTheme.Dark
        }
        return AppTheme.Auto
    }

    fun setAppTheme(appTheme: AppTheme) {
        sharedPreferences.edit().putInt(
            "appTheme", when (appTheme) {
                AppTheme.Light -> 1
                AppTheme.Dark -> 2
                AppTheme.Auto -> 0
            }
        ).apply()
    }

    fun setToken(token: String?) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

}