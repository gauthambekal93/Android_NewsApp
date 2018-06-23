package com.example.gauth.android_newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
//THE LISTVIEWONLINE NEEDS TO GET UPDATED ONLINETITLE!!
public class MainActivity extends AppCompatActivity implements DownloadTask.IData,DownloadJson.IData2,HtmlContent.IData3 {
ContentValues contentValues=new ContentValues();
    //ContentValues contentValues2=new ContentValues();
     ArrayAdapter arrayAdapter;
    ArrayAdapter arrayAdapterOnline;
     ListView listView;
    ListView listViewOnline;
  SQLiteDatabase myDatabase;
    String secondaryURL;
    String  listName;
    int offset;
    boolean doNotSkip=true;
   static   ArrayList<String> title=new ArrayList<String>();
    static   ArrayList<String> url=new ArrayList<String>();
    static   ArrayList<String> titleOnline=new ArrayList<String>();
    static   ArrayList<String> urlOnline=new ArrayList<String>();

    AlertDialog.Builder builder;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.download:
                Log.i("Download button","Clicked!!");
                listView.setVisibility(View.INVISIBLE);
                listViewOnline.setVisibility(View.VISIBLE);

                try {
            new DownloadTask(MainActivity.this).execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
                    titleOnline.clear();
                    arrayAdapterOnline.notifyDataSetChanged();
                    urlOnline.clear();

        } catch (InterruptedException e) {

            e.printStackTrace(); //prints all information of the error
        } catch (ExecutionException e) {

            e.printStackTrace();
        }
        catch (Exception e){e.printStackTrace();}
                return true;

            case R.id.saved:
                Log.i("Save button","Clicked!!");
                listViewOnline.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                showDb();
                return true;

            default:
                return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        listViewOnline = (ListView) findViewById(R.id.listViewOnline);
        listViewOnline.setVisibility(View.INVISIBLE);
        arrayAdapterOnline = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titleOnline);
        builder = new AlertDialog.Builder(this);
        try {
            myDatabase=this.openOrCreateDatabase("News",MODE_PRIVATE,null);
        //   myDatabase.execSQL("DROP TABLE IF EXISTS news");
            myDatabase.execSQL("DROP TABLE IF EXISTS html");
            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS news (id INTEGER PRIMARY KEY,TITLE VARCHAR,SECONDARY_URL VARCHAR)");
          //  myDatabase.execSQL("CREATE TABLE IF NOT EXISTS html (id INTEGER PRIMARY KEY,HTML VARCHAR)");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        showDb();
    }

    @Override
    public void handleData(String data) {
        Log.i("Result is from MAIN",data);
        data=data.replace("[","");
        data=data.replace("]","");
        List<String> myList = new ArrayList<String>(Arrays.asList(data.split(",")));

for(int i=1;i<=10;i++) {
    Log.i("List is",myList.get(i).toString().trim());
    String primaryURL = "https://hacker-news.firebaseio.com/v0/item/" + myList.get(i).toString().trim() + ".json?print=pretty";
    new DownloadJson(MainActivity.this).execute(primaryURL);

}
    }
public  void handleData2(String data)
{
    try {
        doNotSkip=true;
        JSONObject jsonObject = new JSONObject(data);//CONVERTS STRING TO JSON DATA
        secondaryURL = jsonObject.getString("url");
        listName = jsonObject.getString("title");
        if (listName.isEmpty()) {
            listName = jsonObject.getString("id");
        }
    }
    catch (JSONException e) {
        Log.i("There was an","ISSUE!");
        doNotSkip=false;
        e.printStackTrace();
    }
    Log.i("DATA IS !!!",data);
if(doNotSkip){
    titleOnline.add(listName);
    arrayAdapterOnline.notifyDataSetChanged();
    urlOnline.add(secondaryURL);}

        /*
        contentValues.put("TITLE",listName);
        contentValues.put("SECONDARY_URL",secondaryURL);
        myDatabase.insert("news",null,contentValues);
*/
        offset=offset+1;

        if(offset==10)
        {
showOnline();
        }
}
    //THIS METHOD IS NOT BEING USED AS OF NOW!!!
    @Override
    public void handleData3(String data) {
//Log.i("WEB DATA",data);
        //contentValues2.put("HTML", data);
        //myDatabase.insert("html",null,contentValues2);
       // myDatabase.execSQL("Insert into html (HTML) values("+data+");");
        //webView.loadData(data,"text/html","UTF-8");
    }


                        //THE BELOW CODE IS FOR DISPLAYING ONLINE DATA
    public void showOnline()
    {

        listViewOnline.setAdapter(arrayAdapterOnline);
        listViewOnline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                arrayAdapterOnline.notifyDataSetChanged();
                Log.i("Online Menu",Integer.toString(i));

                Intent intent=new Intent(getApplicationContext(),WebDisplay.class);
                intent.putExtra("commonTag",-2);
                intent.putExtra("onlineNumber",i);
                startActivity(intent);
            }
        });

        listViewOnline.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Log.i("Online Menu","LONG CLICKED");

                builder.setTitle("Save the item!!")
                        .setMessage("Do you definately want to save this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("Yes", "Clicked");
                                contentValues.put("TITLE",titleOnline.get(i));
                                contentValues.put("SECONDARY_URL",urlOnline.get(i));
                                myDatabase.insert("news",null,contentValues);
                            Log.i("Saved title is are",titleOnline.get(i).toString());
                                Log.i("Saved url is are",urlOnline.get(i).toString());
                                //SAVING WORKS PROPERLY
                            }
                        }).setCancelable(true)
                        .setNegativeButton("No",new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,int which)
                            {
                                Log.i("No","Clicked");
                            }
                        }).setCancelable(true).
                        show();

//Delete ends here
                return true;

            }
        });
    }



                        //THE BELOW CODE IS FOR DISPLAYING SAVED DATA
public void showDb()
{
    Cursor c=myDatabase.rawQuery("SELECT * FROM news",null);
   Log.i("No of rows", myDatabase.rawQuery("Select count(*) from news", null).toString());
    int idIndex=c.getColumnIndex("id");
    int titleIndex=c.getColumnIndex("TITLE");
    int urlIndex=c.getColumnIndex("SECONDARY_URL");
Log.i("Display Function ","called");
    title.clear();
    url.clear();
    while (c.moveToNext())
    {   Log.i("DB id is",Integer.toString(c.getInt(idIndex)));
        Log.i("DB Title is",c.getString(titleIndex));
        Log.i("DB URL is",c.getString(urlIndex));
        title.add(c.getString(titleIndex));
        url.add(c.getString(urlIndex));
     //   new HtmlContent(MainActivity.this).execute(c.getString(urlIndex));
    }
    arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, title);
    listView.setAdapter(arrayAdapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i("Menu",Integer.toString(i));
            Intent intent=new Intent(getApplicationContext(),WebDisplay.class);
            intent.putExtra("commonTag",-3);
            intent.putExtra("savedNumber",i);
            startActivity(intent);
        }
    });

    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
            Log.i("Menu","LONG CLICKED");

//Delete starts here
            builder.setTitle("Delete the item!!")
                    .setMessage("Do you definately want to delete this?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("Delete", "Clicked");
                            Log.i("Deleted menu is",Integer.toString(i));

                            update(i);
                        }
                    }).setCancelable(true)
                    .setNegativeButton("No",new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog,int which)
                        {
                            Log.i("No","Clicked");
                        }
                    }).setCancelable(true).
                    show();

//Delete ends here
            return true;

        }
    });
//showHtml();
}

    public void update(int i)
    {
        String urlDelete=url.get(i);
     //   myDatabase.execSQL("delete from news where id="+(i+1)+";");
       // myDatabase.delete("news", "SECONDARY_URL" + " = " + urlDelete , null);
       urlDelete= new StringBuilder().append('\'').append(urlDelete).append('\'').toString();
      Log.i("URL DELTE",urlDelete);
       //The below hard coded line works
        // myDatabase.execSQL("delete from news where SECONDARY_URL='https://www.apple.com/support/keyboard-service-program-for-macbook-and-macbook-pro/'");
      myDatabase.execSQL("delete from news where SECONDARY_URL="+(urlDelete));
        showDb();

       // arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, title);
        //listView.setAdapter(arrayAdapter);
    }
//THIS METHOD IS NOT BEING USED AS OF NOW!!!
    public void showHtml()
{
    Cursor c=myDatabase.rawQuery("SELECT * FROM html",null);
    int idIndex=c.getColumnIndex("id");
    int htmlIndex=c.getColumnIndex("HTML");
    while (c.moveToNext())
    {
        Log.i("DB HTML ID IS",Integer.toString(c.getInt(idIndex)));
        Log.i("html data is",c.getString(htmlIndex));
        Log.i("End of one file","-----------------------");
    }
}
}


