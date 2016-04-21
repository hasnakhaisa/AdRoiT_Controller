package com.example.prof.AdroitController;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class buttonchooser extends AppCompatActivity {

    //String[] Abutton = {"button_up","button_down","button_right","button_left","button_A","button_B","button_C","button_D","button_E"};
    String[] Abutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttonchooser);
        Abutton = getResources().getStringArray(R.array.ButtonList);
        ArrayAdapter adapter  = new ArrayAdapter<String>(this, R.layout.activity_buttonlist,Abutton);

        ListView listView = (ListView) findViewById(R.id.button_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListClickHandler());

    }

    public class ListClickHandler implements OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            String msg = "Android : ";
            TextView listText = (TextView) view.findViewById(R.id.label);
            String text = listText.getText().toString();
            //Log.d(msg, text);
            Intent intent=new Intent();
            intent.putExtra("MESSAGE",text);
            setResult(2,intent);
            finish();//finishing activity

        }

    }


}
