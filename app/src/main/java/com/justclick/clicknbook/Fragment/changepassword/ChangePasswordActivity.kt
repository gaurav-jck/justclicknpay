package com.justclick.clicknbook.Fragment.changepassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.justclick.clicknbook.R
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener

class ChangePasswordActivity : AppCompatActivity() , ToolBarHideFromFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        replaceFragment(ChangePasswordFragment.newInstance())
    }

    fun replaceFragmentWithBackStack(fragment: Fragment) {
        val fm = supportFragmentManager
        if (!fragment.isVisible) {
            fm.beginTransaction()
                .setCustomAnimations(
                    R.anim.fragment_enter_from_right,
                    R.anim.fragment_exit_to_left,
                    R.anim.fragment_enter_from_left,
                    R.anim.fragment_exit_to_right
                )
                .replace(R.id.container, fragment, fragment.tag).addToBackStack(null).commit()
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        if (!fragment.isVisible) {
            fm.beginTransaction()
                .setCustomAnimations(
                    R.anim.fragment_enter_from_right,
                    R.anim.fragment_exit_to_left,
                    R.anim.fragment_enter_from_left,
                    R.anim.fragment_exit_to_right
                )
                .replace(R.id.container, fragment, fragment.tag).commit()
        }
    }

    override fun onToolBarHideFromFragment(isHide: Boolean?) {

    }
}