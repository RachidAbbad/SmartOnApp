package com.abbad.smartonapp.utils;

import android.graphics.Typeface;
import android.view.Menu;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.activities.MainActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TapTargetGuide {
    private BottomNavigationView navView;
    private MainActivity mainActivity;
    public TapTargetGuide(BottomNavigationView menu, MainActivity mainActivity){
        this.navView = menu;
        this.mainActivity = mainActivity;
    }
    public void firstGuide(){
        if(SessionManager.isTheFirstTime()){
            SessionManager.setFirsTimeUse(false);
            Menu menu = navView.getMenu();

            TapTargetView.showFor(mainActivity,                 // `this` is an Activity
                    TapTarget.forView(navView.findViewById(menu.getItem(0).getItemId()), "Section de tableau de bord", "Ici vous trouverez toutes les données sur chaudiere dans votre site.")
                            // All options below are optional
                            .outerCircleColor(R.color.firstColor)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(25)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.white)  // Specify the color of the description text
                            .textColor(R.color.white)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(60));
            TapTargetView.showFor(mainActivity,                 // `this` is an Activity
                    TapTarget.forView(navView.findViewById(menu.getItem(1).getItemId()), "Section des interventions", "Ici vous trouverez toutes vos interventions.")
                            // All options below are optional
                            .outerCircleColor(R.color.secondPointColor)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(25)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.white)  // Specify the color of the description text
                            .textColor(R.color.white)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(60));
            TapTargetView.showFor(mainActivity,                 // `this` is an Activity
                    TapTarget.forView(navView.findViewById(menu.getItem(2).getItemId()), "Section des notification", "Ici vous trouverez toutes vos notifications.")
                            // All options below are optional
                            .outerCircleColor(R.color.thirdPointColor)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(25)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.white)  // Specify the color of the description text
                            .textColor(R.color.white)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(60));
            TapTargetView.showFor(mainActivity,                 // `this` is an Activity
                    TapTarget.forView(navView.findViewById(menu.getItem(3).getItemId()), "Section de profile", "Ici vous trouverez toutes informations à propos de votre profile .")
                            // All options below are optional
                            .outerCircleColor(R.color.secondColor)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(25)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.white)  // Specify the color of the description text
                            .textColor(R.color.white)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(60));


        }
    }
}
