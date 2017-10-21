package com.example.liujingjing.mobilesafe.MyApplication.activity;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liujingjing.mobilesafe.R;
import com.example.liujingjing.mobilesafe.db.dao.BlackNumberDao;
import com.example.liujingjing.mobilesafe.db.domain.BlackNum;

import java.util.List;

import static com.example.liujingjing.mobilesafe.R.id.iv_delete;

public class BlackPhoneActivity extends AppCompatActivity {
    //获取添加黑名单号码的按钮
    private Button bt_add_blackNum;
    //获取列表展示的listView
    private ListView lv_blackNum_item;
    //弹出的添加拦截号码的对话框
    private AlertDialog dialog;
    //获取要拦截的手机号
    private EditText et_refused_phone;
    //获取确认和取消按钮
    private Button bt_ok;
    private Button bt_not_ok;
    //获取拦截方式
    private RadioGroup rg_refused_way;
    //操作数据库的工具对象
    private BlackNumberDao mDao;
    //存储黑名单列表的list
    private List<BlackNum> mBlackNumList;
    //默认选择拦截短信
    private int mode=1;
    //给listView设值的数据适配器
    private MyAdapter mAdapter=new MyAdapter();
    private boolean mIsLoad=false;
    private int mCount;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //拿到黑名单列表，告知listView可以设置适配器
            lv_blackNum_item.setAdapter(mAdapter);
        }
    };

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackNumList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        //展示listView要掉用很多遍getView，数据很多的时候，反复调用此方法会很占用资源，导致应用停止运行，因此要做优化
        //1.创建ViewHolder对象，如果第一次调用此方法，findViewById找到的控件存储到ViewHolder中，再把ViewHolder
        //赋值给系统创建好复用规则的convertView,2,第二次进入该方法时，直接把设置好的ViewHolder上的控件拿来用
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            //判断是第几次进来
            if (convertView==null){
                //先将布局文件--view
                convertView=View.inflate(getApplicationContext(),R.layout.listview_black_phone_items,null);
                holder=new ViewHolder();
                //拿到布局文件中的所有控件
                holder.tv_black_num= (TextView)convertView.findViewById(R.id.tv_black_num);
                holder.tv_black_mode= (TextView) convertView.findViewById(R.id.tv_black_mode);
                holder.iv_delete= (ImageView) convertView.findViewById(iv_delete);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }



            //删除
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除数据库中的数据
                    mDao.deleteBlackNum(mBlackNumList.get(position).getPhone());
                    //删除集合中的数据
                    mBlackNumList.remove(mBlackNumList.get(position));
                    //告知数据适配器刷新页面数据
                    mAdapter.notifyDataSetChanged();
                }
            });

            //给控件赋值
            holder.tv_black_num.setText(mBlackNumList.get(position).getPhone());
            int mode=Integer.parseInt(mBlackNumList.get(position).getMode());
            //根据集合中存储的数字显示相应的文本到页面上
            switch (mode){
                case 1:
                    holder.tv_black_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_black_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_black_mode.setText("拦截所有");
                    break;
            }

            return convertView;
        }
    }

    static class ViewHolder{
        TextView tv_black_num;
        TextView tv_black_mode;
        ImageView iv_delete;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_phone);
        initUI();
        initData();
    }

    //拿到显示在listView中的所有黑名单号码
    private void initData() {
        //要调用查询方法，先创建dao对象
        mDao=BlackNumberDao.getInstance(getApplicationContext());
        //查询为耗时操作，丢给子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBlackNumList=mDao.queryDataByPager(0);
                //拿到黑名单集合之后，告知主线程
                handler.sendEmptyMessage(0);
                mCount=mDao.getCount();
            }
        }).start();
    }

    private void initUI() {
        bt_add_blackNum= (Button) findViewById(R.id.bt_add_blackNum);
        lv_blackNum_item= (ListView) findViewById(R.id.lv_blackNum_item);

        //添加数据很多，需要用到数据库
        bt_add_blackNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框
                showDialog();
            }
        });

        //监听listView的滚动状态
        lv_blackNum_item.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mBlackNumList != null) {
                    //如果状态为空闲，并且最后一个条目可见时，加载下一页的数据
                    //为了防止在加载的过程中又满足条件，重复加载，定义全局标志位
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                            lv_blackNum_item.getLastVisiblePosition() >= mBlackNumList.size() - 1
                            && !mIsLoad) {
                        //如果数据库中的数据大于集合中的数据，加载更多
                        if (mCount>mBlackNumList.size()){
                            //查询为耗时操作，丢给子线程
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //要调用查询方法，先创建dao对象
                                    mDao=BlackNumberDao.getInstance(getApplicationContext());
                                    List<BlackNum> moreData=mDao.queryDataByPager(mBlackNumList.size());
                                    mBlackNumList.addAll(moreData);
                                    //拿到黑名单集合之后，告知主线程
                                    handler.sendEmptyMessage(0);
                                }
                            }).start();
                        }


                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }


        });
    }


    public void showDialog(){
        //创建对话框的格式
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        dialog=builder.create();
        //把布局文件设置给对话框
        View view=View.inflate(this,R.layout.dialog_refuse_black_num_way,null);
        //因为版本兼容问题，低版本四周会有间距，这里将上下左右间距都设为0
        dialog.setView(view,0,0,0,0);

        //拿到该对话框中的所有控件
        et_refused_phone= (EditText) view.findViewById(R.id.et_refused_phone);
        rg_refused_way= (RadioGroup) view.findViewById(R.id.rg_refused_way);
        bt_ok= (Button)view. findViewById(R.id.bt_ok);
        bt_not_ok= (Button) view.findViewById(R.id.bt_not_ok);


        //监听单选框的值改变
        rg_refused_way.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rbt_msg:
                        mode=1;
                        break;
                    case R.id.rbt_phone:
                        mode=2;
                        break;
                    case R.id.rbt_all:
                        mode=3;
                        break;
                }
            }
        });

        //确定
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击确定，把添加的数据插入到数据库中
                String phone = et_refused_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    BlackNum blackNum = new BlackNum();
                    blackNum.setPhone(phone);
                    blackNum.setMode(mode + "");
                    mDao.addBlackNum(phone, mode + "");
                    //把加入到数据库里的那条数据也加入到显示在页面上的集合里，保持同步，并插入到集合顶部
                    mBlackNumList.add(0, blackNum);
                    //通知数据适配器，集合更新了
                    if (mAdapter!=null)
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(),"请输入拦截号码",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //给控件写监听事件
        bt_not_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
