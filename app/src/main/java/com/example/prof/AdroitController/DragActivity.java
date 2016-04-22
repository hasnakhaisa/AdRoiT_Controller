package com.example.prof.AdroitController;

/*
 * Copyright (C) 2013 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (Note to other developers: The above note says you are free to do what you want with this code.
 *  Any problems are yours to fix. Wglxy.com is simply helping you get started. )
 */

import android.app.Activity;
//import android.content.ClipData;
//import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

/**
 * This activity presents a screen with a grid on which images can be added and moved around.
 * It also defines areas on the screen where the dragged views can be dropped. Visual feedback is
 * provided to the user as the objects are dragged over the views where something can be dropped.
 *
 * <p> 
 * This example starts dragging when a DragSource view is touched. 
 * If you want to start with a long press (long click), set the variable mLongClickStartsDrag to true.
 * 
 */

public class DragActivity extends Activity 
    implements View.OnLongClickListener, View.OnClickListener,
               DragDropPresenter,
               View.OnTouchListener
{


    /**
     */
    // Constants

    private static final int HIDE_TRASHCAN_MENU_ID = Menu.FIRST;
    private static final int SHOW_TRASHCAN_MENU_ID = Menu.FIRST + 1;
    private static final int ADD_OBJECT_MENU_ID = Menu.FIRST + 2;
    private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST + 3;

    public static final String LOG_NAME = "DragActivity";

    /**
     */
    // Variables

    private DragController mDragController;   // Object that handles a drag-drop sequence.
                                              // It interacts with DragSource and DropTarget objects.
    //private GridView mGridView;               // the GridView
    private DeleteZone mDeleteZone;           // A drop target that is used to remove objects from the screen.
    private int mImageCount = 0;              // The number of images that have been added to screen.
    private ImageCell mLastNewCell = null;    // The last ImageCell added to the screen when Add Image is clicked.
    private boolean mLongClickStartsDrag = true;   // If true, it takes a long click to start the drag operation.
                                                    // Otherwise, any touch event starts a drag.

    private Vibrator mVibrator;
    private static final int VIBRATE_DURATION = 35;

    public static final boolean Debugging = false;   // Use this to see extra toast messages while debugging.

    // New variable
    private boolean mPlayMode = false; // If true, we can't add new button.
    private String[] ButtonListName;

    /**
     */
    // Methods

    /**
     * Add a new image so the user can move it around. It shows up in the image_source_frame
     * part of the screen.
     *
     * @param resourceId int - the resource id of the image to be added
     */

    public void addNewImageToScreen (int resourceId)
    {
        if (mLastNewCell != null) mLastNewCell.setVisibility (View.GONE);

        FrameLayout imageHolder = (FrameLayout) findViewById (R.id.image_source_frame);
        if (imageHolder != null) {
           FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams (LayoutParams.MATCH_PARENT,
                                                                       LayoutParams.MATCH_PARENT,
                                                                       Gravity.CENTER);
           ImageCell newView = new ImageCell (this);
           newView.setImageResource (resourceId);
           imageHolder.addView (newView, lp);
           newView.mEmpty = false;
           newView.mCellNumber = -1;
           mLastNewCell = newView;
           mImageCount++;

           // Have this activity listen to touch and click events for the view.
           newView.setOnClickListener(this);
           newView.setOnLongClickListener(this);
           newView.setOnTouchListener(this);
           newView.ResourceId = resourceId;
           trace("Resource ID : " + String.valueOf(resourceId));
        }
    }

    /**
     * Add one of the images to the screen so the user has a new image to move around.
     * See addImageToScreen.
     *
     */

    public void addNewImageToScreen ()
    {
        Intent intent = new Intent(this, buttonchooser.class);
        startActivityForResult(intent,2);
    }

    // Call Back method  to get the Message form other Activity

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            //int resourceId = R.drawable.button;
            String message=data.getStringExtra("MESSAGE");

            for (int i=0; i<ButtonListName.length-1;i++) {
                trace("Compare " + message + " and " + ButtonListName[i]);
                if (message.equals(ButtonListName[i])) {
                    trace("Success");
                    toast(ButtonListName[i]);
                    addNewImageToScreen(getResources().getIdentifier(ButtonListName[i], "drawable", getPackageName()));
                    break;
                }
            }
        }
    }


    /**
     * Handle a click on a view.
     *
     */

    public void onClick(View v)
    {
        if ((mLongClickStartsDrag) && (mPlayMode)) {
           // Tell the user that it takes a long click to start dragging.

           ImageCell i = (ImageCell) v;
            for (int j = 0; j < ButtonListName.length - 1; j++) {
                if (i.ResourceId == getResources().getIdentifier(ButtonListName[j] , "drawable", getPackageName())) {
                    switching(ButtonListName[j]);
                    break;
                    }

            }
            trace("Resource Id on Click: " + i.ResourceId);
        }
    }


    public  void switching(String clicked) {
        switch (clicked) {
            case "button_up": trace("Maju"); break;
            case "button_left": trace("Kiri"); break;
            case "button_right": trace("Kanan"); break;
            case "button_down": trace("Mundur"); break;
        }
    }

    /**
     * Handle a click of the Add Image button
     *
     */

    public void onClickAddImage (View v)
    {
        if (!mPlayMode) {
            addNewImageToScreen();
        }
    }

    /**
     * Handle a click of the Play button
      */

    public void SwitchMode (View v) {
        mPlayMode = !mPlayMode;
        GridView gridViewUtama = (GridView) findViewById(R.id.image_grid_view);
        if (mPlayMode) {
            toast("Play Mode, Disable editting");
            gridViewUtama.setBackgroundColor(0xFFFFFFFF);
            //gridViewUtama.setOnTouchListener();
        }
        else {
            toast("Edit Mode, Enable editting");
            gridViewUtama.setBackgroundColor(0x00FFFFFF);
        }
    }

    /**
     * onCreate - called when the activity is first created.
     *
     * Creates a drag controller and sets up three views so click and long click on the views are sent to this activity.
     * The onLongClick method starts a drag sequence.
     *
     */

     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When a drag starts, we vibrate so the user gets some feedback.
        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        // This activity will listen for drag-drop events.
        // The listener used is a DragController. Set it up.
        mDragController = new DragController (this);

        // Set up the grid view with an ImageCellAdapter and have it use the DragController.
        GridView gridView = (GridView) findViewById(R.id.image_grid_view);
        if (gridView == null) toast ("Unable to find GridView");
        else {
             gridView.setAdapter (new ImageCellAdapter (this, mDragController));
            // gridView.setOnItemClickListener (this);
        }
        //mGridView = gridView;


        // Always add the delete_zone so there is a place to get rid of views.
        // Find the delete_zone and add it as a drop target.
        // That gives the user a place to drag views to get them off the screen.
        mDeleteZone = (DeleteZone) findViewById (R.id.delete_zone_view);
        if (mDeleteZone != null) mDeleteZone.setOnDragListener (mDragController);

        // Give the user a little guidance.
        Toast.makeText (getApplicationContext(),
                        getResources ().getString (R.string.Welcomemsg),
                        Toast.LENGTH_LONG).show ();
        ButtonListName  = getResources().getStringArray(R.array.ButtonList);
    }

    /**
     * Build a menu for the activity.
     *
     */

    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, HIDE_TRASHCAN_MENU_ID, 0, "Hide Trashcan").setShortcut('1', 'c');
        menu.add(0, SHOW_TRASHCAN_MENU_ID, 0, "Show Trashcan").setShortcut('2', 'c');
        menu.add(0, ADD_OBJECT_MENU_ID, 0, "Add View").setShortcut('9', 'z');
        menu.add(0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");


        return true;
    }

    /**
     * Handle a click of an item in the grid of cells.
     *
     */

    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    {
        ImageCell i = (ImageCell) v;
        trace ("onItemClick in view: " + i.mCellNumber);
    }

    /**
     * Handle a long click.
     * If mLongClickStartsDrag  only is true, this will be the only way to start a drag operation.
     *
     * @param v View
     * @return boolean - true indicates that the event was handled
     */

    public boolean onLongClick(View v)
    {
        if ((mLongClickStartsDrag) && (!mPlayMode)) {

            //trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());

            // Make sure the drag was started by a long press as opposed to a long click.
            // (Note: I got this from the Workspace object in the Android Launcher code.
            //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
            if (!v.isInTouchMode()) {
               toast ("isInTouchMode returned false. Try touching the view again.");
               return false;
            }
            return startDrag (v);
        }

        // If we get here, return false to indicate that we have not taken care of the event.
        return false;
    }

    /**
     * Handle a long click.
     * If mLongClick only is true, this will be the only way to start a drag operation.
     *
     * @param v View
     * @return boolean - true indicates that the event was handled
     */

    public boolean onLongClickOLD (View v)
    {
        if (mLongClickStartsDrag) {

            //trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());

            // Make sure the drag was started by a long press as opposed to a long click.
            // (Note: I got this from the Workspace object in the Android Launcher code.
            //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
            if (!v.isInTouchMode()) {
               toast ("isInTouchMode returned false. Try touching the view again.");
               return false;
            }
            return startDrag (v);
        }

        // If we get here, return false to indicate that we have not taken care of the event.
        return false;
    }

    /**
     * Perform an action in response to a menu item being clicked.
     *
     */

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case HIDE_TRASHCAN_MENU_ID:
                if (mDeleteZone != null) mDeleteZone.setVisibility (View.INVISIBLE);
                return true;
            case SHOW_TRASHCAN_MENU_ID:
                if (mDeleteZone != null) mDeleteZone.setVisibility (View.VISIBLE);
                return true;
            case ADD_OBJECT_MENU_ID:
                // Add a new object to the screen;
                addNewImageToScreen ();
                return true;
            case CHANGE_TOUCH_MODE_MENU_ID:
                mLongClickStartsDrag = !mLongClickStartsDrag;
                String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)."
                                                      : "Changed touch mode. Drag now starts on touch (click).";
                Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is the starting point for a drag operation if mLongClickStartsDrag is false.
     * It looks for the down event that gets generated when a user touches the screen.
     * Only that initiates the drag-drop sequence.
     *
     */

    public boolean onTouch (View v, MotionEvent ev) {
        // If we are configured to start only on a long click, we are not going to handle any events here.
        //if (mLongClickStartsDrag) return false;
        if (!mPlayMode) return false;

        boolean handledHere = false;

        final int action = ev.getAction();

        // In the situation where a long click is not needed to initiate a drag, simply start on the down event.
        if (action == MotionEvent.ACTION_DOWN) {
            ImageCell i = (ImageCell) v;
            if (i.ResourceId != 0) {
                for (int j = 0; j < ButtonListName.length - 1; j++) {
                    if (i.ResourceId == getResources().getIdentifier(ButtonListName[j] , "drawable", getPackageName())) {
                        switching(ButtonListName[j]);
                        break;
                    }

                }
                trace("Resource Id on touch: " + i.ResourceId);
            }
            else mVibrator.vibrate(100);

           //handledHere = startDrag (v);
        }

        return handledHere;
    }

    /**
     * Start dragging a view.
     *
     */

    public boolean startDrag (View v) {
        // We are starting a drag-drop operation.
        // Set up the view and let our controller handle it.
        v.setOnDragListener(mDragController);
        mDragController.startDrag(v, ((ImageCell) v).ResourceId);
        return true;
    }

    /**
     * Show a string on the screen via Toast.
     *
     * @param msg String
     * @return void
     */

    public void toast (String msg)
    {
        Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
    } // end toast

    /**
     * Send a message to the debug log. Also display it using Toast if Debugging is true.
     */

    public void trace (String msg)
    {
        Log.d (LOG_NAME, msg);
        if (Debugging) toast (msg);
    }


    /**
     * DragDropPresenter methods
     *
     */

    /**
     * This method is called to determine if drag-drop is enabled.
     *
     * @return boolean
     */

    public boolean isDragDropEnabled () {
        return true;
    }

    /**
     * React to the start of a drag-drop operation.
     * In this activity, we vibrate to give the user some feedback.
     *
     * @param source DragSource
     * @return void
     */

    public void onDragStarted (DragSource source) {
        mVibrator.vibrate(VIBRATE_DURATION);
    }

    /**
     * This method is called on the completion of a drag operation.
     * If the drop was not successful, the target is null.
     *
     * @param target DropTarget
     * @param success boolean - true means that the object was dropped successfully
     */

    public void onDropCompleted (DropTarget target, boolean success) {
    }

} // end class

//up 2130837588
//down 2130837585
//left 2130837586
//right 2130837587
//
//a 2130837581
//b 2130837582
//c 2130837583
//d 2130837584