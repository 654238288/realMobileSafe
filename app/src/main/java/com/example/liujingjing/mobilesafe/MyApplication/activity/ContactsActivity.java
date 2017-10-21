package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liujingjing.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactsActivity extends BaseActivity {


    private ListView lv_contacts;
    //存储联系人对象的List,list中存储的是名字和电话号所属的对象，应该存储一个HashMap
    private List<HashMap<String,String>> contacts=new ArrayList<HashMap<String,String>>();
    private MyAdapter adapter;

    //把存好数据的数据适配器给联系人列表
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter=new MyAdapter();
            lv_contacts.setAdapter(adapter);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //获取控件
        initUI();
        //给listView设置数据
        initData();
    }


    private void initData() {
       /* //查询手机联系人列表属于耗时操作，放入子线程中
        if(getApplicationContext()
                .checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ContactsActivity.this,new String[]{Manifest
                    .permission.READ_CONTACTS},1);
        }else{*/
            readContact();
        //}
            }





            //读取联系人
    public  void readContact(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                contacts.clear();//清空数据
                Cursor cursor=null;//用于读取数据的游标
                try {
                    //使用Android预定义的Uri对象查询数据
                    cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,null,null,null);
                    HashMap<String,String> contact=null;
                    if(cursor.moveToFirst()){
                        do{
                            String name=cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//姓名
                            String phoneNumber=cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));//电话号码
                            contact=new HashMap<String, String>();
                            contact.put("name",name);
                            contact.put("phone",phoneNumber);
                            contacts.add(contact);
                        }while(cursor.moveToNext());
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } finally {
                    if(cursor!=null){//关闭资源
                        cursor.close();
                    }
                }
                /*adapter.notifyDataSetChanged();//刷新列表数据*/

                //给listView设置适配器，属于UI操作，要返回主线程，使用消息机制
                handler.sendEmptyMessage(0);
            }
        }).start();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //如果有这个权限，再读取数据
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                  readContact();
                }
                break;
            default:
        }
    }



    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public HashMap<String, String> getItem(int i) {
            return contacts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            //把联系人的每个条目控件绑定在View对象上，返回给数据适配器
            View view=View.inflate(getApplicationContext(),R.layout.activity_contacts_item,null);

            TextView tv_contact_name= (TextView) view.findViewById(R.id.tv_contact_name);
            TextView tv_contact_num= (TextView) view.findViewById(R.id.tv_contact_num);

            //给控件设值
            tv_contact_name.setText(getItem(i).get("name"));
            tv_contact_num.setText(getItem(i).get("phone"));
            return view;
        }
    }

    private void initUI() {
        //获取到联系人列表控件
        lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        //设置点击事件
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                //如果适配器拿到了数据
                if(adapter!=null){
                    //把存好姓名和电话号的集合拿出来，把电话号作为结果返回到设置界面3
                    HashMap<String,String> hashmap=adapter.getItem(i);
                    String phone=hashmap.get("phone");
                    Intent intent=new Intent(ContactsActivity.this,Setup3Activity.class);
                    intent.putExtra("phone",phone);
                    setResult(0,intent);
                    finish();
                }
            }
        });
    }
}
