package com.wanderingcan.fam.sample;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wanderingcan.fam.sample.R;
import com.wanderingcan.floatingactionmenu.FloatingActionButton;
import com.wanderingcan.floatingactionmenu.FloatingActionMenu;


public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = (CoordinatorLayout) findViewById(R.id.root);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fam_fab_2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(root, "Floating Action Button pressed", Snackbar.LENGTH_LONG).show();
            }
        });

        FloatingActionMenu menu = new FloatingActionMenu(this);
        fab = new FloatingActionButton(this);
        menu.addButton(fab);
        fab = new FloatingActionButton(this);
        menu.addButton(fab);
        fab = new FloatingActionButton(this);
        menu.addButton(fab);
        menu.setMenuDirection(FloatingActionMenu.RIGHT);
        root.addView(menu);

    }
}
