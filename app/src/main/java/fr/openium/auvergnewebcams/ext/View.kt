package fr.openium.auvergnewebcams.ext

import android.view.View
import androidx.core.view.ViewPropertyAnimatorCompat
import fr.openium.kotlintools.ext.animateCompat
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.hide
import fr.openium.kotlintools.ext.show

fun View.goneWithAnimationCompat(): ViewPropertyAnimatorCompat? {
    return animateCompat().setDuration(200).alpha(0f).withEndAction {
        gone()
    }
}

fun View.hideWithAnimationCompat(): ViewPropertyAnimatorCompat? {
    return animateCompat().setDuration(200).alpha(0f).withEndAction {
        hide()
    }
}

fun View.showWithAnimationCompat(): ViewPropertyAnimatorCompat? {
    return animateCompat().setDuration(200).alpha(1f).withStartAction {
        show()
    }
}