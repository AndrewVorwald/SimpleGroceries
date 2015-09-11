package com.example.andrew.simplegroceries;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ListActivity {
    FoodData testData;
    FoodAdapter stuffIHaveAdapter;
    FoodAdapter stuffINeedAdapter;
    TextView title;
    Context parent;
    ListView listView;
    FoodAdapter currentAdapter;
    TextView textDelete;
    TextView textMoveTo;




    private void setTitle() {
        if (testData.currentList == FoodData.Status.HAVE) {
            title.setText("Stuff I have");
            setListAdapter(stuffIHaveAdapter);

            currentAdapter = stuffIHaveAdapter;
            textMoveTo.setText("Move to Stuff I Need");

        } else {
            title.setText("Stuff I need");
            setListAdapter(stuffINeedAdapter);
            currentAdapter = stuffINeedAdapter;
            textMoveTo.setText("Move to Stuff I Have");

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* restores foodData, either from disk or bundle
        *  or, if neither exist, we create a new one
        * */
        File directory = this.getFilesDir();
        File diskData = new File(directory, "diskData");
        parent = this;


        /* first check for a bundle.  If there is one, we fetch testData from the bundle */
        if (savedInstanceState != null) {
            /* deserialize bytes from savedInstanceState and set testData equal to it */
            try {
                testData = FoodData.deserialize(savedInstanceState.getByteArray("Data"));
            } catch (IOException e) {
                e.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        /* then, if there isn't a bundle, we'll check the local disk*/
        else if (diskData.isFile()) {

            try {
                FileInputStream fis = this.openFileInput("diskData");
                ObjectInputStream is = new ObjectInputStream(fis);
                testData = (FoodData) is.readObject();
                is.close();
                fis.close();
            } catch (IOException | ClassNotFoundException e) {
                testData = new FoodData();
                Toast tempToast = Toast.makeText(this, "old data could not be opened", Toast.LENGTH_LONG);
                tempToast.show();
            }
        }

        /* if there's no bundle and there's no foodData on disk, then we'll make a new one */
        else {
            testData = new FoodData();
        }

//        /* create some test data to play with */
//        testData.addHave("chicken");
//        testData.addHave("plates");
//        testData.addNeed("a million bucks");

        /* set up adapter for data set */
        stuffIHaveAdapter = new FoodAdapter(this, android.R.layout.simple_list_item_1, testData.stuffIHave);
        stuffINeedAdapter = new FoodAdapter(this, android.R.layout.simple_list_item_1, testData.stuffINeed);


        /* set the title text */
        title = (TextView) this.findViewById(R.id.title);


        /* set up pointer to listView to hook up the swipe to dismiss method */
        listView = getListView();
        listView = (ListView) findViewById(android.R.id.list);

        /* Initialize textViews to pass into the listener*/
        textDelete = (TextView) findViewById(R.id.textDelete);
        textDelete.setAlpha(0);

        textMoveTo = (TextView) findViewById(R.id.textMoveTo);
        textMoveTo.setAlpha(0);

        setTitle();


        /* Create the listener for the listview */
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                testData, listView, textDelete, textMoveTo,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions, boolean
                            swipedLeft) {
                        ArrayList<FoodData.Food> otherList;
                        if (testData.currentList == FoodData.Status.HAVE) {
                            otherList = testData.stuffINeed;
                        } else
                            otherList = testData.stuffIHave;
                        for (int position : reverseSortedPositions) {
                            if (swipedLeft) {
                                otherList.add((FoodData.Food) currentAdapter.getItem(position));
                            }
                            currentAdapter.remove(position);

                        }
                        currentAdapter.notifyDataSetChanged();
                    }
                }
        );

        listView.setOnTouchListener(touchListener);


    }

    @Override
    public void onBackPressed(){
        //pop off the undoStack.  If there's no action there, we'll
        //go ahead and assume the user wants to exit
        if(!testData.undoStack.undo())
            super.onBackPressed();

    }

    @Override
    public void onPause() {
        try {
            FileOutputStream fos = this.openFileOutput("diskData", this.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(testData);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        /* add the serialized bytes array to the saved instance */
        try {
            byte[] serialized = testData.convertToBytes();
            savedInstanceState.putByteArray("FoodData", serialized);
        } catch (IOException e) {
            Toast.makeText(this, "there was an IO exception.  restart", Toast.LENGTH_LONG);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    public void swapLists(View view) {
        if (testData.currentList == FoodData.Status.HAVE) {
            setListAdapter(stuffINeedAdapter);
            testData.currentList = FoodData.Status.NEED;
            setTitle();
        } else {
            setListAdapter(stuffIHaveAdapter);
            testData.currentList = FoodData.Status.HAVE;
            setTitle();
        }
    }

    // Needs to be organized into a separate file
    public void openAddFoodDialgoue(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_food);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        TextView titleView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_edit_reminder);

        Button commitButton = (Button) dialog.findViewById(R.id.custom_button_commit);
        Button cancelButon = (Button) dialog.findViewById(R.id.custom_button_cancel);
        LinearLayout rootLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);

        commitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String foodName = editCustom.getText().toString();
                ArrayList<String> inputs = parser(foodName);

                /* check that string isn't blank or just a bunch of spaces */
                foodName = foodName.trim();
                if (foodName.length() > 0) {
                    if (testData.currentList == FoodData.Status.HAVE) {
                        for (String x : inputs) {
                            testData.addHave(x);
                            stuffIHaveAdapter.notifyDataSetChanged();
                        }
                    } else {
                        for (String x : inputs) {
                            testData.addNeed(x);
                            stuffINeedAdapter.notifyDataSetChanged();
                        }
                    }

                    dialog.dismiss();
                }
            }
        });

        cancelButon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        editCustom.requestFocus();

    }

    /* parses the string so a person can add multiple food items at once */
    ArrayList<String> parser(String input) {
        ArrayList<String> parsed = new ArrayList<String>();
        int start = 0, finish = 0;
        String temp;
        finish = input.indexOf(",", start);
        while (finish != -1) {

            temp = input.substring(start, finish);
            temp = temp.trim();
            parsed.add(temp);
            start = finish + 1;
            finish = input.indexOf(",", start);

        }

        temp = input.substring(start);
        temp.trim();
        parsed.add(temp);
        return parsed;
    }
}
